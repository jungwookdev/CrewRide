import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function GET(request) {
  const host = request.headers.get("host");
  const forwardedProto = request.headers.get("x-forwarded-proto");
  const protocol = forwardedProto ? forwardedProto.split(',')[0].trim() : "http";
  const url = `${protocol}://${host}`;

  try {
    console.error('Call History');

    // For PoC, employeeId is supplied as a query parameter.
    const { searchParams } = new URL(request.url);
    const employeeId = searchParams.get('employeeId');
    if (!employeeId) {
      return new Response(JSON.stringify({ error: 'Missing employeeId query parameter' }), { status: 400 });
    }
    const driverRides = await prisma.offeredRide.findMany({
      where: { driverId: employeeId },
      include: { driver: true }
    });
    const participantRides = await prisma.rideParticipant.findMany({
      where: { employeeId },
      include: {
        ride: {
          include: { driver: true }
        },
        employee: true
      }
    });
    return new Response(JSON.stringify({
      driverRides: driverRides.map(ride => ({
        ...ride,
        driverName: ride.driver?.name || null,
        driverHeadshotUrl: ride.driver ? `${url}/api/headshot/${ride.driver.employeeId}` : null
      })),
      participantRides: participantRides.map(rp => ({
        ...rp.ride,
        driverName: rp.ride?.driver?.name || null,
        driverHeadshotUrl: rp.ride?.driver ? `${url}/api/headshot/${rp.ride.driver.employeeId}` : null
      }))
    }), { status: 200 });
  } catch (error) {
    console.error('Error retrieving ride history:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}