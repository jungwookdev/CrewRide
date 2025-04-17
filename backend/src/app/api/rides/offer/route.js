import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

// Define Company HQ coordinates and threshold distance.
const companyHQLat = 37.380644;
const companyHQLon = -122.113323;
const hqThresholdKm = 0.1 * 1.60934; // 0.1 miles in km (approx. 0.16 km).

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

async function getHumanReadableAddress(lat, lon) {
  try {
    // Build the reverse geocoding URL for Nominatim.
    const url = `https://nominatim.openstreetmap.org/reverse?lat=${encodeURIComponent(lat)}&lon=${encodeURIComponent(lon)}&format=json&addressdetails=1`;

    const response = await fetch(url, {
      headers: {
        'User-Agent': 'YourAppName/1.0 (contact@example.com)' // Replace with your app details.
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch address from Nominatim');
    }

    const data = await response.json();

    if (data.address) {
      const addr = data.address;
      // Build the custom address with the desired components.
      const components = [];
      if (addr.house_number) components.push(addr.house_number);
      if (addr.road) components.push(addr.road);
      if (addr.neighbourhood) components.push(addr.neighbourhood);
      if (addr.city) {
        components.push(addr.city);
      } else if (addr.town) {
        components.push(addr.town);
      } else if (addr.village) {
        components.push(addr.village);
      }
      if (addr.county) components.push(addr.county);

      return components.join(', ');
    }

    return `Address for (${lat.toFixed(5)}, ${lon.toFixed(5)})`;
  } catch (error) {
    console.error("Error in reverse geocoding:", error);
    return `Address for (${lat.toFixed(5)}, ${lon.toFixed(5)})`;
  }
}

export async function POST(request) {
  try {
    // Parse request body and get required fields, including employeeId for the driver.
    const body = await request.json();
    
    const { 
      employeeId, 
      toLatitude, 
      toLongitude, 
      fromLatitude, 
      fromLongitude, 
      departureTime, 
      bufferTime, 
      offeredSeats
    } = body;

    if (
      !employeeId ||
      toLatitude == null ||
      toLongitude == null ||
      fromLatitude == null ||
      fromLongitude == null ||
      !departureTime ||
      bufferTime == null ||
      offeredSeats == null
    ) {
      return new Response(JSON.stringify({ error: 'Missing required fields' }), { status: 400 });
    }

    
    let toAddress; 
    // If start is within HQ range, mark it; otherwise, convert to an address.
    //const toDistanceToHQ = haversineDistance(toLatitude, toLongitude, companyHQLat, companyHQLon);
    //if (toDistanceToHQ <= hqThresholdKm) {
    //  toAddress = "Company HQ";
    //} else {
      toAddress = await getHumanReadableAddress(toLatitude, toLongitude);
    //}

    let fromAddress; 
    // Similarly, for destination address.
    //const fromDistanceToHQ = haversineDistance(fromLatitude, fromLongitude, companyHQLat, companyHQLon);
    //if (fromDistanceToHQ <= hqThresholdKm) {
    //  fromAddress = "Company HQ";
    //} else {
      fromAddress = await getHumanReadableAddress(fromLatitude, fromLongitude);
    //}
      
    // Use OpenRouteService to fetch driving path
    const ORS_API_KEY = process.env.ORS_API_KEY;
    const startCoords = `${fromLongitude},${fromLatitude}`;
    const endCoords = `${toLongitude},${toLatitude}`;
    const orsUrl = `https://api.openrouteservice.org/v2/directions/driving-car?api_key=${ORS_API_KEY}&start=${startCoords}&end=${endCoords}`;

    const orsResponse = await fetch(orsUrl, {
      method: "GET",
      "Accept": "application/json"
    });

    if (!orsResponse.ok) {
      throw new Error("Failed to fetch route from OpenRouteService");
    }

    const orsData = await orsResponse.json();
    console.debug("orsData" + orsData)
    
    if (!orsData.features || !orsData.features[0]) {
      throw new Error("OpenRouteService response does not contain valid route data.");
    }
    const drivingPath = orsData.features[0].geometry.coordinates.map(([lon, lat]) => [lat, lon]);

    // Generate a rideId using crypto.randomUUID if available; otherwise, use a timestamp-based string.
    const rideId = (typeof crypto.randomUUID === 'function') ? crypto.randomUUID() : String(Date.now());

    // Create the new ride in the database.
    const newRide = await prisma.offeredRide.create({
      data: {
        rideId,
        driverId: employeeId,
        toLatitude,
        toLongitude,
        toAddress,
        fromLatitude,
        fromLongitude,
        fromAddress,
        departureTime: new Date(departureTime),
        bufferTime,
        offeredSeats,
        completed: false,
        drivingPath: JSON.stringify(drivingPath),
        // Optionally store vehicleInfo if your schema supports it.
      },
    });

    return new Response(
      JSON.stringify({ 
        message: 'Ride offered successfully', 
        rideId: newRide.rideId, 
        drivingPath 
      }),
      { status: 201 }
    );
  } catch (error) {
    console.error('Error offering ride:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}