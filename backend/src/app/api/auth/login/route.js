import { PrismaClient } from '@prisma/client';
import bcrypt from 'bcrypt';
import jwt from 'jsonwebtoken';
import { createTokens } from '@/lib/token';

const prisma = new PrismaClient();
const SECRET = process.env.JWT_SECRET || 'crewride-secret';

export async function POST(request) {
  const host = request.headers.get("host");
    
  const forwardedProto = request.headers.get("x-forwarded-proto");
  const protocol = forwardedProto ? forwardedProto.split(',')[0].trim() : "http";
  
  const url = `${protocol}://${host}`;
  
  const { email, password } = await request.json();

  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) {
    return new Response(JSON.stringify({ error: 'User not found' }), { status: 404 });
  }
  
  console.log("User found:", user);
  
  const valid = await bcrypt.compare(password, user.password);
  if (!valid) {
    return new Response(JSON.stringify({ error: 'Invalid password' }), { status: 401 });
  }

  if (!user) {
    return new Response(JSON.stringify({ error: 'User not found' }), { status: 404 });
  }

  const employee = await prisma.employee.findUnique({ where: { employeeId: user.employeeId } });
  if (!employee) {
    return new Response(JSON.stringify({ error: 'Employee not found' }), { status: 404 });
  }
  
  // Construct a profile object with selected fields.
  const profile = {
    userId: user.userId,
    employeeId: employee.employeeId,
    email: user.email,
    phoneNumber: user.phoneNumber,
    name: employee.name,
    department: employee.department,
    jobRole: employee.jobRole,
    headshotUrl: `${url}/api/headshot/${employee.employeeId}`
  };
  
  //const { accessToken, refreshToken } = createTokens({
  //  userId: user.id,
  //  email: user.email
  //});

  return new Response(JSON.stringify(profile), { status: 200 });
}