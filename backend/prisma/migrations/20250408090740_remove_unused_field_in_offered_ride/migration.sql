/*
  Warnings:

  - You are about to drop the column `booked_users` on the `OfferedRide` table. All the data in the column will be lost.

*/
-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_OfferedRide" (
    "ride_id" TEXT NOT NULL PRIMARY KEY,
    "driver_id" TEXT NOT NULL,
    "start_latitude" REAL NOT NULL,
    "start_longitude" REAL NOT NULL,
    "destination_latitude" REAL NOT NULL,
    "destination_longitude" REAL NOT NULL,
    "departure_time" DATETIME NOT NULL,
    "buffer_time" INTEGER NOT NULL,
    "offered_seats" INTEGER NOT NULL,
    "completed" BOOLEAN NOT NULL,
    "driving_path" TEXT,
    CONSTRAINT "OfferedRide_driver_id_fkey" FOREIGN KEY ("driver_id") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_OfferedRide" ("buffer_time", "completed", "departure_time", "destination_latitude", "destination_longitude", "driver_id", "driving_path", "offered_seats", "ride_id", "start_latitude", "start_longitude") SELECT "buffer_time", "completed", "departure_time", "destination_latitude", "destination_longitude", "driver_id", "driving_path", "offered_seats", "ride_id", "start_latitude", "start_longitude" FROM "OfferedRide";
DROP TABLE "OfferedRide";
ALTER TABLE "new_OfferedRide" RENAME TO "OfferedRide";
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
