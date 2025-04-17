import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function PATCH(request) {
  try {
    const body = await request.json();
    const { employeeId, latitude, longitude } = body;
    
    if (!employeeId || latitude === undefined || longitude === undefined) {
      return new Response(
        JSON.stringify({ error: 'Missing required fields: employeeId, latitude, longitude' }),
        { status: 400 }
      );
    }
    
    // Optional: Ensure that the employee exists.
    const employee = await prisma.employee.findUnique({
      where: { employeeId }
    });
    if (!employee) {
      return new Response(JSON.stringify({ error: 'Employee not found' }), { status: 404 });
    }
    
    // Insert a new record into EmployeeLocation.
    const locationRecord = await prisma.employeeLocation.create({
      data: {
        employeeId,
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude)
      }
    });
    
    return new Response(
      JSON.stringify({
        message: 'Location updated successfully',
        location: locationRecord
      }),
      { status: 200 }
    );
  } catch (error) {
    console.error("Error updating location:", error);
    return new Response(
      JSON.stringify({ error: error.message }),
      { status: 500 }
    );
  }
}