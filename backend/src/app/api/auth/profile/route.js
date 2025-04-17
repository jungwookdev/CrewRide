import { PrismaClient } from '@prisma/client';
import jwt from 'jsonwebtoken';

const prisma = new PrismaClient();
const SECRET = process.env.JWT_SECRET || 'crewride-secret';

export async function GET(request) {
  try {
    const { searchParams } = new URL(request.url);
    const userId = searchParams.get('userId');

    // Alternatively, you might want to compare the token's userId with a query parameter or ensure the token is valid for profile access.
    // For this example we assume the token already gives us the correct userId.
    if (!userId) {
      return new Response(JSON.stringify({ error: 'Token missing user information' }), { status: 401 });
    }
    // Find the user by userId from the token and include the associated employee details.
    const user = await prisma.user.findUnique({
      where: { userId: userId },
      include: { employee: true }
    });

    if (!user) {
      return new Response(JSON.stringify({ error: 'User not found' }), { status: 404 });
    }

    // Construct a profile object with selected fields.
    const profile = {
      userId: user.userId,
      email: user.email,
      phoneNumber: user.phoneNumber,
      name: user.employee?.name,
      department: user.employee?.department,
      jobRole: user.employee?.jobRole,
    };

    return new Response(JSON.stringify(profile), { status: 200 });
  } catch (error) {
    console.error('Error retrieving profile:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}