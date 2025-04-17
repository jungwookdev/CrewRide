import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function PATCH(request) {
  try {
    const body = await request.json();
    const { rideId, departureTime, bufferTime, offeredSeats, drivingPath } = body;
    if (!rideId) {
      return new Response(JSON.stringify({ error: 'Missing rideId' }), { status: 400 });
    }

    // PoC: Skip driver verification. (Ensure this is acceptable for your proof-of-concept.)
    const updateData = {};
    if (departureTime) updateData.departureTime = new Date(departureTime);
    if (bufferTime != null) updateData.bufferTime = bufferTime;
    if (offeredSeats != null) updateData.offeredSeats = offeredSeats;
    if (drivingPath) updateData.drivingPath = drivingPath;

    await prisma.offeredRide.update({
      where: { rideId },
      data: updateData,
    });

    return new Response(JSON.stringify({ message: 'Ride modified successfully' }), { status: 200 });
  } catch (error) {
    console.error('Error modifying ride:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}