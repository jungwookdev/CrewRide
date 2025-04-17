import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

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
    const body = await request.json();
    const { rideId, employeeId, pickupLatitude, pickupLongitude, meetingTime } = body;

    if (
      !rideId ||
      !employeeId ||
      pickupLatitude === undefined ||
      pickupLongitude === undefined ||
      !meetingTime
    ) {
      return new Response(
        JSON.stringify({ error: 'Missing required fields' }),
        { status: 400 }
      );
    }

    // Verify that the ride exists.
    const ride = await prisma.offeredRide.findUnique({
      where: { rideId },
    });
    if (!ride) {
      return new Response(
        JSON.stringify({ error: 'Ride not found' }),
        { status: 404 }
      );
    }

    // Verify that the employee exists.
    const employee = await prisma.employee.findUnique({
      where: { employeeId },
    });
    if (!employee) {
      return new Response(
        JSON.stringify({ error: 'Employee not found' }),
        { status: 404 }
      );
    }

    const pickupAddress = await getHumanReadableAddress(pickupLatitude,pickupLongitude);
    console.debug(pickupAddress)
    
    // Create a new RideParticipant record.
    await prisma.rideParticipant.create({
      data: {
        rideId,
        employeeId,
        pickupLatitude,
        pickupLongitude,
        pickupAddress: pickupAddress,
        meetingTime: new Date(meetingTime),
        status: "CONFIRMED"
      },
    });

    return new Response(
      JSON.stringify({ message: 'Joined ride successfully' }),
      { status: 201 }
    );
  } catch (error) {
    console.error("Error joining ride:", error);
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500 }
    );
  }
}