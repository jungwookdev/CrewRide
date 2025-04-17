import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function PATCH(request) {
  try {
    const body = await request.json();
    const { rideId } = body;
    if (!rideId) {
      return new Response(JSON.stringify({ error: 'Missing rideId' }), { status: 400 });
    }
    const ride = await prisma.offeredRide.findUnique({ where: { rideId } });
    if (!ride) return new Response(JSON.stringify({ error: 'Ride not found' }), { status: 404 });
    // PoC: Skip driver verification.
    await prisma.offeredRide.update({
      where: { rideId },
      data: { completed: true },
    });

    return new Response(JSON.stringify({ message: 'Ride marked as completed' }), { status: 200 });
  } catch (error) {
    console.error('Error completing ride:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}