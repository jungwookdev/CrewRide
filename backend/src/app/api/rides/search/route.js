import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

// Haversine formula: calculate distance (in kilometers) between two geographic points.
function haversineDistance(lat1, lon1, lat2, lon2) {
  const toRad = (x) => (x * Math.PI) / 180;
  const R = 6371; // Earth's radius in km.
  const dLat = toRad(lat2 - lat1);
  const dLon = toRad(lon2 - lon1);
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos(toRad(lat1)) *
      Math.cos(toRad(lat2)) *
      Math.sin(dLon / 2) ** 2;
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

// Compute initial bearing (forward azimuth) between two points in degrees.
function getBearing(lat1, lon1, lat2, lon2) {
  const toRad = (deg) => deg * Math.PI / 180;
  const toDeg = (rad) => rad * 180 / Math.PI;
  const dLon = toRad(lon2 - lon1);
  const y = Math.sin(dLon) * Math.cos(toRad(lat2));
  const x = Math.cos(toRad(lat1)) * Math.sin(toRad(lat2)) -
            Math.sin(toRad(lat1)) * Math.cos(toRad(lat2)) * Math.cos(dLon);
  let brng = Math.atan2(y, x);
  brng = toDeg(brng);
  return (brng + 360) % 360;
}

export async function GET(request) {
  try {
    // Get the host from the headers
    const host = request.headers.get("host");

    // Check for the "x-forwarded-proto" header which is commonly set by proxies.
    // If it's not available, default to "http".
    const forwardedProto = request.headers.get("x-forwarded-proto");
    const protocol = forwardedProto ? forwardedProto.split(',')[0].trim() : "http";
    
    const url = `${protocol}://${host}`;
    
    const { searchParams } = new URL(request.url);
    
    // Extract query parameters.
    const employeeId = searchParams.get('employeeId');
    const currentLatitude = parseFloat(searchParams.get('currentLatitude'));
    const currentLongitude = parseFloat(searchParams.get('currentLongitude'));
    const requestedDestLatitude = parseFloat(searchParams.get('requestedDestLatitude'));
    const requestedDestLongitude = parseFloat(searchParams.get('requestedDestLongitude'));
    const requestedDatetime = searchParams.get('datetime'); // optional ISO date string

    if (
      !employeeId ||
      isNaN(currentLatitude) ||
      isNaN(currentLongitude) ||
      isNaN(requestedDestLatitude) ||
      isNaN(requestedDestLongitude)
    ) {
      return new Response(
        JSON.stringify({
          error:
            'Missing or invalid query parameters. Required: employeeId, currentLatitude, currentLongitude, requestedDestLatitude, requestedDestLongitude'
        }),
        { status: 400 }
      );
    }

    // Retrieve the employee's record.
    const employee = await prisma.employee.findUnique({
      where: { employeeId }
    });
    if (!employee) {
      return new Response(JSON.stringify({ error: 'Employee not found' }), { status: 404 });
    }
    const userCluster = employee.cluster;
    let doNotMatch = [];
    if (employee.doNotMatchList && employee.doNotMatchList.trim() !== "") {
      doNotMatch = employee.doNotMatchList.split(",").map(x => x.trim());
    }

    // Get candidate rides that are not completed.
    const rides = await prisma.offeredRide.findMany({
      where: { completed: false },
      include: { driver: true, rideParticipants: true }
    });

    let candidates = [];

    // Default max distance (if driver's preference not set) in miles.
    const DEFAULT_MAX_DISTANCE_MILES = 1.5;

    for (const ride of rides) {
      console.debug("\n\n-----------------------\nRideID: " + ride.rideId)


      // Check available seats using rideParticipants relation.
      const bookedUsersCount = ride.rideParticipants.length;
      if (bookedUsersCount >= ride.offeredSeats) continue;

      console.debug("1. Passed\t[Available Seats]\tCondition")

      const driver = ride.driver;
      if (!driver) continue;

      // Filter: driver's cluster must match the employee's.
      if (driver.cluster !== userCluster) continue;

      console.debug("2. Passed\t[Cluster]\tCondition")

      // Filter: skip if driver's ID is in the employee's do-not-match list.
      if (doNotMatch.includes(ride.driverId)) continue;

      console.debug("3. Passed\t[DoNotMatch]\tCondition")

      // Priority: set to 1 if driver's preferred_by_list includes the employee.
      let priority = 0;
      if (driver.preferredByList && driver.preferredByList.trim() !== "") {
        const preferredList = driver.preferredByList.split(",").map(x => x.trim());
        if (preferredList.includes(employeeId)) {
          priority = 1;
        }
      }

      // Verify departure time if datetime is provided.
      if (requestedDatetime) {
        const requestedDate = new Date(requestedDatetime);
        const lowerBound = new Date(requestedDate.getTime() - 60 * 60 * 1000); // requestedDatetime - 1 hour
        const upperBound = new Date(requestedDate.getTime() + 60 * 60 * 1000); // requestedDatetime + 1 hour
        const departureDate = new Date(ride.departureTime);
        
        // Continue to next ride if departure time is not within the ±1 hour window.
        if (departureDate < lowerBound || departureDate > upperBound) continue;
      }

      console.debug("4. Passed\t[DateTime]\tCondition")

      // Retrieve the driver's maximum distance preference in miles.
      const driverMaxDistanceMiles = driver.maxDistanceMiles || DEFAULT_MAX_DISTANCE_MILES;
      const maxDistanceKm = driverMaxDistanceMiles * 1.60934;

      // Check current location validity:
      // At least one point in the ride's drivingPath must be within maxDistanceKm of the current location.
      let validCurrent = false;
      if (ride.drivingPath && ride.drivingPath.trim() !== "") {
        try {
          const path = JSON.parse(ride.drivingPath);  // Expect an array of [lat, lon] pairs.
          for (const point of path) {
            const [latPt, lonPt] = point;
            const distance = haversineDistance(currentLatitude, currentLongitude, latPt, lonPt);
            if (distance <= maxDistanceKm) {
              validCurrent = true;
              break;
            }
          }
        } catch (err) {
          console.error(`Error parsing drivingPath for ride ${ride.rideId}: ${err.message}`);
        }
      }
      if (!validCurrent) continue;

      console.debug("5. Passed\t[MaxDistanceKm]\tCondition")

      // Verify destination validity.
      // Condition 1: Check if the ride's explicit destination is within the allowed range of the requested destination.
      let validDestination = false;
      if (ride.toLatitude != null && ride.toLongitude != null) {
        if (haversineDistance(ride.toLatitude, ride.toLongitude, requestedDestLatitude, requestedDestLongitude) <= maxDistanceKm) {
          validDestination = true;
        }
      }

      console.debug("6. Passed\t[Destination Boundary]\tCondition")

      // Condition 2: Or check if any point in the drivingPath is close to the requested destination.
      if (!validDestination && ride.drivingPath && ride.drivingPath.trim() !== "") {
        try {
          const path = JSON.parse(ride.drivingPath);
          for (const point of path) {
            const [latPt, lonPt] = point;
            if (haversineDistance(latPt, lonPt, requestedDestLatitude, requestedDestLongitude) <= maxDistanceKm) {
              validDestination = true;
              break;
            }
          }
        } catch (err) {
          console.error(`Error parsing drivingPath (destination check) for ride ${ride.rideId}: ${err.message}`);
        }
      }

      console.debug("7. Passed\t[Driving Path Boundary]\tCondition")

      // Condition 3: Bearing check (if we have multiple points in drivingPath).
      if (validDestination && ride.drivingPath && ride.drivingPath.trim() !== "") {
        try {
          const path = JSON.parse(ride.drivingPath);
          if (path.length >= 2) {
            // Calculate ride direction from the last two points in the drivingPath.
            const [p1Lat, p1Lon] = path[0];
            const [p2Lat, p2Lon] = path[path.length - 1];
            const rideBearing = getBearing(p1Lat, p1Lon, p2Lat, p2Lon);
            const requestedBearing = getBearing(currentLatitude, currentLongitude, requestedDestLatitude, requestedDestLongitude);
            const bearingDifference = Math.abs(rideBearing - requestedBearing);
            const normalizedDiff = bearingDifference > 180 ? 360 - bearingDifference : bearingDifference;
            // Accept only if within 20 degrees difference.

            if (normalizedDiff > 20) {
              validDestination = false;
              console.debug("8. Failed\t[Driving Bearing]\tCondition Diff:" + normalizedDiff)
            }else{
              console.debug("8. Passed\t[Driving Bearing]\tCondition")
            }
          }
        } catch (err) {
          console.error(`Error computing bearing for ride ${ride.rideId}: ${err.message}`);
        }
      }
      if (!validDestination) continue;



      // Build candidate object.
      const candidate = {
        ride_id: ride.rideId,
        driver_id: ride.driverId,
        driver_name: ride.driver.name, 
        driver_headshotUrl: `${url}/api/headshot/${ride.driver.employeeId}`,
        departure_time: ride.departureTime,
        driving_path: ride.drivingPath,
        available_seats: ride.offeredSeats - bookedUsersCount,
        priority: priority,
        average_rating: driver.averageRating || 0,
        punctuality_score: driver.punctualityScore || 0,
        from_latitude: ride.fromLatitude,
        from_longitude: ride.fromLongitude,
        from_address: ride.fromAddress,
        to_latitude: ride.toLatitude,
        to_longitude: ride.toLongitude,
        to_address: ride.toAddress
      };

      candidates.push(candidate);
    }

    // Sort candidates: descending by priority, then average rating, then punctuality score.
    candidates.sort((a, b) => {
      if (a.priority !== b.priority) return b.priority - a.priority;
      if (a.average_rating !== b.average_rating) return b.average_rating - a.average_rating;
      return b.punctuality_score - a.punctuality_score;
    });

    return new Response(JSON.stringify({ rides: candidates }), { status: 200 });
  } catch (error) {
    console.error('Error in search endpoint:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}