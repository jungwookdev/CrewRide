import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function DELETE(request) {
  try {
    const body = await request.json();
    const { rideId, employeeId, reason } = body;

    if (!rideId || !employeeId) {
      return new Response(JSON.stringify({ error: 'Missing required fields: rideId and employeeId' }), { status: 400 });
    }

    // Verify that the ride participant record exists.
    const participant = await prisma.rideParticipant.findFirst({
      where: { rideId, employeeId }
    });

    if (!participant) {
      return new Response(JSON.stringify({ error: 'Ride participant record not found' }), { status: 404 });
    }

    // Log the cancellation event by creating a record in RideCancellation.
    await prisma.rideCancellation.create({
      data: {
        rideId,
        employeeId,
        reason: reason || null, // optionally include a cancellation reason
        cancelledAt: new Date()
      }
    });

    // Remove the participation record.
    await prisma.rideParticipant.delete({
      where: { id: participant.id }
    });

    return new Response(JSON.stringify({ message: 'Ride cancellation successful' }), { status: 200 });
  } catch (error) {
    console.error("Error cancelling ride:", error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}