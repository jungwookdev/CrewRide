import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function POST(request) {
  try {
    const body = await request.json();
    const {
      rideId,
      employeeId,
      satisfactionScore,
      punctualityScore,
      suggestionText
    } = body;

    // Validate required fields.
    if (!rideId || !employeeId) {
      return new Response(JSON.stringify({ error: 'Missing required fields: rideId and employeeId' }), { status: 400 });
    }

    // Optionally, validate that the ride and employee exist.
    const ride = await prisma.offeredRide.findUnique({ where: { rideId } });
    if (!ride) {
      return new Response(JSON.stringify({ error: 'Ride not found' }), { status: 404 });
    }
    const employee = await prisma.employee.findUnique({ where: { employeeId } });
    if (!employee) {
      return new Response(JSON.stringify({ error: 'Employee not found' }), { status: 404 });
    }

    // Create the feedback record.
    const feedback = await prisma.rideFeedback.create({
      data: {
        rideId,
        employeeId,
        satisfactionScore: satisfactionScore !== undefined ? satisfactionScore : null,
        punctualityScore: punctualityScore !== undefined ? punctualityScore : null,
        suggestionText: suggestionText || null
        // createdAt is automatically set via @default(now())
      }
    });

    return new Response(JSON.stringify({ message: 'Feedback submitted successfully', feedback }), { status: 201 });
  } catch (error) {
    console.error("Error submitting feedback:", error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}