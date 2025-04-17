import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';

const prisma = new PrismaClient();

export async function POST(request) {
  try {
    // Parse JSON body
    const body = await request.json();
    // Remap snake_case keys to camelCase variables
    const {
      user_id: userId,
      employee_id: employeeId,
      password,
      email,
      phone_number: phoneNumber,
      created_at: createdAt,
    } = body;

    // Basic validation (you may want to expand this)
    if (!userId || !employeeId || !password || !email) {
      return new Response(JSON.stringify({ error: 'Missing required fields' }), { status: 400 });
    }
    
    // Check if a user already exists using email as unique identifier
    const existingUser = await prisma.user.findUnique({ where: { email } });
    if (existingUser) {
      return new Response(JSON.stringify({ error: 'User already exists' }), { status: 409 });
    }

    // Hash the password using bcrypt
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create the new user record in the database
    const newUser = await prisma.user.create({
      data: {
        userId,
        employeeId,
        password: hashedPassword,
        email,
        phoneNumber,
        // Convert createdAt to a Date object if necessary
        createdAt: createdAt ? new Date(createdAt) : new Date(),
      },
    });

    // Return a success message along with created user info (omit password)
    return new Response(
      JSON.stringify({
        message: 'Signup successful',
        user: {
          userId: newUser.userId,
          employeeId: newUser.employeeId,
          email: newUser.email,
          phoneNumber: newUser.phoneNumber,
          createdAt: newUser.createdAt,
        },
      }),
      { status: 201 }
    );
  } catch (error) {
    console.error('Error during signup:', error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}