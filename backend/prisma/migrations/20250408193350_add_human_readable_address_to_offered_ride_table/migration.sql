/*
  Warnings:

  - You are about to drop the column `destination_latitude` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `destination_longitude` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `start_latitude` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `start_longitude` on the `OfferedRide` table. All the data in the column will be lost.
  - Added the required column `from_address` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `from_latitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `from_longitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `to_address` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `to_latitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `to_longitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.

*/
-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_OfferedRide" (
    "ride_id" TEXT NOT NULL PRIMARY KEY,
    "driver_id" TEXT NOT NULL,
    "from_latitude" REAL NOT NULL,
    "from_longitude" REAL NOT NULL,
    "from_address" TEXT NOT NULL,
    "to_latitude" REAL NOT NULL,
    "to_longitude" REAL NOT NULL,
    "to_address" TEXT NOT NULL,
    "departure_time" DATETIME NOT NULL,
    "buffer_time" INTEGER NOT NULL,
    "offered_seats" INTEGER NOT NULL,
    "completed" BOOLEAN NOT NULL,
    "driving_path" TEXT,
    CONSTRAINT "OfferedRide_driver_id_fkey" FOREIGN KEY ("driver_id") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_OfferedRide" ("buffer_time", "completed", "departure_time", "driver_id", "driving_path", "offered_seats", "ride_id") SELECT "buffer_time", "completed", "departure_time", "driver_id", "driving_path", "offered_seats", "ride_id" FROM "OfferedRide";
DROP TABLE "OfferedRide";
ALTER TABLE "new_OfferedRide" RENAME TO "OfferedRide";
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
