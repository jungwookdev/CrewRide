import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function GET(request) {
  try {
    const { searchParams } = new URL(request.url);
    const rideId = searchParams.get('rideId');
    if (!rideId) {
      return new Response(JSON.stringify({ error: 'Missing rideId parameter' }), { status: 400 });
    }
    const ride = await prisma.offeredRide.findUnique({
      where: { rideId },
      include: {
        driver: true,
        rideParticipants: { include: { employee: true } }
      }
    });

    // Query the latest location for the ride's driver from EmployeeLocation.
    const latestLocation = await prisma.employeeLocation.findFirst({
      where: { employeeId: ride.driverId },
      orderBy: { updatedAt: 'desc' }
    });

    // Use the returned location or provide a fallback.
    const currentDriverLocation = latestLocation 
      ? { latitude: latestLocation.latitude, longitude: latestLocation.longitude }
      : "Location not available";


    if (!ride) return new Response(JSON.stringify({ error: 'Ride not found' }), { status: 404 });
    const estimatedTimeOfArrival = "15 mins"; // TODO: Update this placehoder
    const status = {
      rideId: ride.rideId,
      driver: {
        name: ride.driver.name,
        contact: ride.driver.emergencyContact
      },
      currentDriverLocation,
      estimatedTimeOfArrival,
      conversationTopics: ride.driver.talkPreferences || "Default conversation topics",
      participants: ride.rideParticipants.map(rp => ({
        employeeId: rp.employeeId,
        name: rp.employee.name,
        pickUpLocation: rp.pickUpLocation,
        meetingTime: rp.meetingTime
      }))
    };
    return new Response(JSON.stringify(status), { status: 200 });
  } catch (error) {
    console.error('Error getting current ride status:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}

export async function GET_BY_EMPLOYEE(request) {
  try {
    const { searchParams } = new URL(request.url);
    const employeeId = searchParams.get('employeeId');
    if (!employeeId) {
      return new Response(JSON.stringify({ error: 'Missing employeeId parameter' }), { status: 400 });
    }

    const rides = await prisma.offeredRide.findMany({
      where: {
        completed: false,
        rideParticipants: {
          some: {
            employeeId: employeeId
          }
        }
      },
      include: {
        driver: true,
        rideParticipants: { include: { employee: true } }
      }
    });

    if (rides.length === 0) {
      return new Response(JSON.stringify({ error: 'No current ride found for employee' }), { status: 404 });
    }

    const ride = rides[0]; // Assume the first one is the current

    const latestLocation = await prisma.employeeLocation.findFirst({
      where: { employeeId: ride.driverId },
      orderBy: { updatedAt: 'desc' }
    });

    const currentDriverLocation = latestLocation 
      ? { latitude: latestLocation.latitude, longitude: latestLocation.longitude }
      : "Location not available";

    const estimatedTimeOfArrival = "15 mins"; // Placeholder

    const status = {
      rideId: ride.rideId,
      driver: {
        name: ride.driver.name,
        contact: ride.driver.emergencyContact
      },
      currentDriverLocation,
      estimatedTimeOfArrival,
      conversationTopics: ride.driver.talkPreferences || "Default conversation topics",
      participants: ride.rideParticipants.map(rp => ({
        employeeId: rp.employeeId,
        name: rp.employee.name,
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