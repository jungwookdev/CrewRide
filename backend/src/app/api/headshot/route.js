import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function POST(request) {
  try {
    // Expect JSON payload with employeeId and headshot (as base64 string)
    const body = await request.json();
    const { employeeId, headshot } = body;

    if (!employeeId || !headshot) {
      return new Response(JSON.stringify({ error: 'Missing employeeId or headshot data' }), { status: 400 });
    }

    // Optional: Verify the employee exists.
    const employee = await prisma.employee.findUnique({
      where: { employeeId }
    });
    if (!employee) {
      return new Response(JSON.stringify({ error: 'Employee not found' }), { status: 404 });
    }
    
    // Convert the base64 string into a Buffer (raw image data).
    // Strip out any data URL prefix if present.
    const base64Data = headshot.split(',').pop();
    const imageBuffer = Buffer.from(base64Data, 'base64');

    // Create a new EmployeeHeadshot record in the database.
    const newHeadshot = await prisma.employeeHeadshot.create({
      data: {
        employeeId,
        headshot: imageBuffer,
      }
    });

    return new Response(JSON.stringify({ message: 'Headshot saved successfully', headshotId: newHeadshot.id }), { status: 201 });
  } catch (error) {
    console.error("Error saving headshot:", error);
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}


export async function GET(request) {
    try {
      const { searchParams } = new URL(request.url);
      const employeeId = searchParams.get('employeeId');
      if (!employeeId) {
        return new Response(JSON.stringify({ error: 'Missing employeeId parameter' }), { status: 400 });
      }
      // Fetch the headshot record from the database.
      const headshotRecord = await prisma.employeeHeadshot.findFirst({
        where: { employeeId },
      });
      if (!headshotRecord) {
        return new Response(JSON.stringify({ error: 'Headshot not found' }), { status: 404 });
      }
      // Return the raw image data with appropriate header.
      return new Response(headshotRecord.headshot, {
        headers: { "Content-Type": "image/png" }
      });
    } catch (error) {
      return new Response(JSON.stringify({ error: error.message }), { status: 500 });
    }
  }