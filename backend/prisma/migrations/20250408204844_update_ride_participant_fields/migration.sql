/*
  Warnings:

  - You are about to drop the column `pickUpLocation` on the `RideParticipant` table. All the data in the column will be lost.
  - Added the required column `pickupAddress` to the `RideParticipant` table without a default value. This is not possible if the table is not empty.
  - Added the required column `pickupLatitude` to the `RideParticipant` table without a default value. This is not possible if the table is not empty.
  - Added the required column `pickupLongitude` to the `RideParticipant` table without a default value. This is not possible if the table is not empty.

*/
-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_RideParticipant" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "rideId" TEXT NOT NULL,
    "employeeId" TEXT NOT NULL,
    "pickupLatitude" REAL NOT NULL,
    "pickupLongitude" REAL NOT NULL,
    "pickupAddress" TEXT NOT NULL,
    "meetingTime" DATETIME NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "RideParticipant_rideId_fkey" FOREIGN KEY ("rideId") REFERENCES "OfferedRide" ("ride_id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RideParticipant_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_RideParticipant" ("createdAt", "employeeId", "id", "meetingTime", "rideId", "status") SELECT "createdAt", "employeeId", "id", "meetingTime", "rideId", "status" FROM "RideParticipant";
DROP TABLE "RideParticipant";
ALTER TABLE "new_RideParticipant" RENAME TO "RideParticipant";
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
