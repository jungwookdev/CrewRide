import jwt from 'jsonwebtoken';

const SECRET = process.env.JWT_SECRET || 'crewride-secret';

export function verifyAuth(req) {
  const authHeader = req.headers.get('authorization');
  if (!authHeader) throw new Error('No token provided');

  const token = authHeader.split(' ')[1];
  if (!token) throw new Error('Invalid token format');

  try {
    const decoded = jwt.verify(token, SECRET);
    return decoded;
  } catch (err) {
    throw new Error('Invalid token');
  }
}