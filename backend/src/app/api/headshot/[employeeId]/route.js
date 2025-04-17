import { PrismaClient } from '@prisma/client';
const prisma = new PrismaClient();

export async function GET(req, { params }) {
  const { employeeId } = await params;
  //console.debug(`Requested employeeId: ${employeeId}`);

  const headshots = await prisma.employeeHeadshot.findMany({
    where: { employeeId },
  });

  if (headshots.length === 0) {
    console.debug(`Headshot not found for employeeId: ${employeeId}`);
    return new Response("Headshot not found", { status: 404 });
  }
  
  if (headshots.length > 1) {
    console.warn(`Multiple headshots found for employeeId: ${employeeId}. Returning the first instance.`);
  }
  
  const headshot = headshots[0];

  //const headshot = await prisma.employeeHeadshot.findUnique({
  //  where: { employeeId },
  //});

  if (!headshot || !headshot.headshot) {
    // Debug: headshot not found
    console.debug(`Headshot not found for employeeId: ${employeeId}`);
    return new Response("Headshot not found", { status: 404 });
  }
  
  // Debug: log the size of the headshot in bytes
  const buffer = headshot.headshot;
  console.debug(`Found headshot for employeeId: ${employeeId}. Image size: ${buffer.byteLength} bytes`);

  // Convert the Node.js Buffer to an ArrayBuffer
  const arrayBuffer = buffer.buffer.slice(
    buffer.byteOffset,
    buffer.byteOffset + buffer.byteLength
  );

  // Debug: log that the image is being returned
  console.debug(`Returning headshot image with Content-Length: ${buffer.byteLength}`);

  return new Response(arrayBuffer, {
    headers: {
      "Content-Type": "image/png",
      "Content-Length": buffer.byteLength.toString(),
    },
  });
}