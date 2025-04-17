import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

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
    const employeeId = searchParams.get('employeeId');
    console.debug("EmployeeID:" + employeeId);
    
    if (!employeeId) {
      return new Response(JSON.stringify({ error: 'Missing employeeId parameter' }), { status: 400 });
    }

    const rides = await prisma.offeredRide.findMany({
      where: {
        completed: false,
        OR: [
          { driverId: employeeId },
          {
            rideParticipants: {
              some: {
                employeeId: employeeId
              }
            }
          }
        ]
      },
      include: {
        driver: true,
        rideParticipants: { include: { employee: true } }
      }
    });

    if (rides.length === 0) {
      return new Response(JSON.stringify({ error: 'No current ride found for employee' }), { status: 404 });
    }

    const ride = rides[0];

    const latestLocation = await prisma.employeeLocation.findFirst({
      where: { employeeId: ride.driverId },
      orderBy: { updatedAt: 'desc' }
    });

    const currentDriverLocation = latestLocation 
      ? { latitude: latestLocation.latitude, longitude: latestLocation.longitude }
      : "Location not available";

    const estimatedTimeOfArrival = "20"; // Minutes

    const status = {
      rideId: ride.rideId,
      driver: {
        name: ride.driver.name,
        contact: ride.driver.emergencyContact,
        headshotUrl: `${url}/api/headshot/${ride.driver.employeeId}`
      },
      fromAddress: ride.fromAddress,
      toAddress: ride.toAddress,
      currentDriverLocation,
      estimatedTimeOfArrival,
      drivingPath: ride.drivingPath,
      conversationTopics: ride.driver.talkPreferences || "Default conversation topics",
      participants: ride.rideParticipants.map(rp => ({
        employeeId: rp.employeeId,
        name: rp.employee.name,
        headshotUrl: `${url}/api/headshot/${rp.employee.employeeId}`,
        pickUpLocation: rp.pickUpLocation,
        meetingTime: rp.meetingTime
      }))
    };

    return new Response(JSON.stringify(status), { status: 200 });
  } catch (error) {
    console.error('Error getting current ride by employee:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}