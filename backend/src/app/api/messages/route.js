import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

/**
 * POST /api/rides/messages
 * Send a message for a given ride.
 * Expected JSON body:
 * {
 *   "rideId": "RIDE_ID",
 *   "senderId": "SENDER_EMPLOYEE_ID",
 *   "receiverId": "RECEIVER_EMPLOYEE_ID",
 *   "content": "Your message content here"
 * }
 */
export async function POST(request) {
  try {
    const body = await request.json();
    const { rideId, senderId, receiverId, content } = body;

    if (!rideId || !senderId || !receiverId || !content) {
      return new Response(
        JSON.stringify({ error: 'Missing required fields: rideId, senderId, receiverId, content' }),
        { status: 400 }
      );
    }

    // Optionally, you could verify the existence of the ride, sender, and receiver.
    const ride = await prisma.offeredRide.findUnique({ where: { rideId } });
    if (!ride) {
      return new Response(JSON.stringify({ error: 'Ride not found' }), { status: 404 });
    }

    const sender = await prisma.employee.findUnique({ where: { employeeId: senderId } });
    if (!sender) {
      return new Response(JSON.stringify({ error: 'Sender not found' }), { status: 404 });
    }

    const receiver = await prisma.employee.findUnique({ where: { employeeId: receiverId } });
    if (!receiver) {
      return new Response(JSON.stringify({ error: 'Receiver not found' }), { status: 404 });
    }

    // Create the new message record.
    const message = await prisma.message.create({
      data: {
        rideId,
        senderId,
        receiverId,
        content,
      },
    });

    return new Response(
      JSON.stringify({ message: 'Message sent successfully', data: message }),
      { status: 201 }
    );
  } catch (error) {
    console.error('Error sending message:', error);
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500 }
    );
  }
}

/**
 * GET /api/rides/messages?rideId=RIDE_ID&after=TIMESTAMP
 * Retrieve messages for a given ride.
 * Query parameters:
 *   - rideId (required): The ride to which the dialog is anchored.
 *   - after (optional): An ISO timestamp string; only messages created after this timestamp will be returned.
 */
export async function GET(request) {
  try {
    const { searchParams } = new URL(request.url);
    const rideId = searchParams.get('rideId');
    if (!rideId) {
      return new Response(
        JSON.stringify({ error: 'Missing rideId query parameter' }),
        { status: 400 }
      );
    }
    const after = searchParams.get('after'); // optional timestamp

    // Construct the where clause.
    const whereClause = { rideId };
    if (after) {
      const afterDate = new Date(after);
      whereClause.createdAt = { gt: afterDate };
    }

    // Retrieve messages for the specified ride.
    const messages = await prisma.message.findMany({
      where: whereClause,
      orderBy: { createdAt: 'asc' },
      include: {
        sender: { select: { employeeId: true, name: true } },
        receiver: { select: { employeeId: true, name: true } },
      }
    });

    return new Response(JSON.stringify({ messages }), { status: 200 });
  } catch (error) {
    console.error('Error retrieving messages:', error);
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500 }
    );
  }
}