-- CreateTable
CREATE TABLE "RideCancellation" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "rideId" TEXT NOT NULL,
    "employeeId" TEXT NOT NULL,
    "cancelledAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "reason" TEXT,
    CONSTRAINT "RideCancellation_rideId_fkey" FOREIGN KEY ("rideId") REFERENCES "OfferedRide" ("ride_id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RideCancellation_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
