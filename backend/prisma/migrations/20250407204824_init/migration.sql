/*
  Warnings:

  - The primary key for the `Employee` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `createdAt` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `email` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `employeeId` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `id` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `password` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `phone` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `updatedAt` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `vehicleCapacity` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `vehicleLicensePlate` on the `Employee` table. All the data in the column will be lost.
  - You are about to drop the column `vehicleModel` on the `Employee` table. All the data in the column will be lost.
  - The primary key for the `OfferedRide` table will be changed. If it partially fails, the table could be left without primary key constraint.
  - You are about to drop the column `availableSeats` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `bufferTime` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `createdAt` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `currentLocation` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `departureTime` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `destinationPoint` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `driverId` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `drivingPath` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `id` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `startPoint` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `status` on the `OfferedRide` table. All the data in the column will be lost.
  - You are about to drop the column `updatedAt` on the `OfferedRide` table. All the data in the column will be lost.
  - Added the required column `employee_id` to the `Employee` table without a default value. This is not possible if the table is not empty.
  - Added the required column `booked_users` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `buffer_time` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `completed` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `departure_time` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `destination_latitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `destination_longitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `driver_id` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `offered_seats` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `ride_id` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `start_latitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.
  - Added the required column `start_longitude` to the `OfferedRide` table without a default value. This is not possible if the table is not empty.

*/
-- RedefineTables
PRAGMA defer_foreign_keys=ON;
PRAGMA foreign_keys=OFF;
CREATE TABLE "new_Employee" (
    "employee_id" TEXT NOT NULL PRIMARY KEY,
    "name" TEXT,
    "department" TEXT,
    "job_role" TEXT,
    "work_schedule_type" TEXT,
    "remote_days" TEXT,
    "languages_spoken" TEXT,
    "emergency_contact" TEXT,
    "seniority_level" TEXT,
    "home_latitude" REAL,
    "home_longitude" REAL,
    "home_area_name" TEXT,
    "parking_availability_near_home" TEXT,
    "work_hours_start" TEXT,
    "work_hours_end" TEXT,
    "days_working_in_office" TEXT,
    "meeting_heavy_days" TEXT,
    "carpool_role" TEXT,
    "driving_availability_days" TEXT,
    "preferred_pickup_time_to_goto_work" TEXT,
    "preferred_dropoff_time_to_goto_work" TEXT,
    "preferred_pickup_time_to_leave_from_work" TEXT,
    "preferred_dropoff_time_to_leave_from_work" TEXT,
    "ride_frequency_preference" TEXT,
    "preferred_communication" TEXT,
    "music_preference" TEXT,
    "co_rider_gender_preference" TEXT,
    "talk_preferences" TEXT,
    "conversation_topics" TEXT,
    "allergies_pet_peeves" TEXT,
    "car_model" TEXT,
    "car_capacity" INTEGER,
    "comfort_level" TEXT,
    "fuel_type" TEXT,
    "carpool_insurance_coverage" TEXT,
    "average_rating" REAL,
    "punctuality_score" REAL,
    "preferred_by_list" TEXT,
    "notification_preferences" TEXT,
    "auto_match_enabled" BOOLEAN,
    "preferred_carpool_group_size" INTEGER,
    "frequent_co_riders" TEXT,
    "do_not_match_list" TEXT,
    "cluster" INTEGER
);
INSERT INTO "new_Employee" ("name") SELECT "name" FROM "Employee";
DROP TABLE "Employee";
ALTER TABLE "new_Employee" RENAME TO "Employee";
CREATE TABLE "new_Message" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "senderId" TEXT NOT NULL,
    "receiverId" TEXT NOT NULL,
    "content" TEXT NOT NULL,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Message_senderId_fkey" FOREIGN KEY ("senderId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "Message_receiverId_fkey" FOREIGN KEY ("receiverId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_Message" ("content", "createdAt", "id", "receiverId", "senderId") SELECT "content", "createdAt", "id", "receiverId", "senderId" FROM "Message";
DROP TABLE "Message";
ALTER TABLE "new_Message" RENAME TO "Message";
CREATE TABLE "new_Notification" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "employeeId" TEXT NOT NULL,
    "message" TEXT NOT NULL,
    "read" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "Notification_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_Notification" ("createdAt", "employeeId", "id", "message", "read") SELECT "createdAt", "employeeId", "id", "message", "read" FROM "Notification";
DROP TABLE "Notification";
ALTER TABLE "new_Notification" RENAME TO "Notification";
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
    "booked_users" TEXT NOT NULL,
    "completed" BOOLEAN NOT NULL,
    "driving_path" TEXT,
    CONSTRAINT "OfferedRide_driver_id_fkey" FOREIGN KEY ("driver_id") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
DROP TABLE "OfferedRide";
ALTER TABLE "new_OfferedRide" RENAME TO "OfferedRide";
CREATE TABLE "new_RideFeedback" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "rideId" TEXT NOT NULL,
    "employeeId" TEXT NOT NULL,
    "satisfactionScore" INTEGER,
    "punctualityScore" INTEGER,
    "suggestionText" TEXT,
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "RideFeedback_rideId_fkey" FOREIGN KEY ("rideId") REFERENCES "OfferedRide" ("ride_id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RideFeedback_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_RideFeedback" ("createdAt", "employeeId", "id", "punctualityScore", "rideId", "satisfactionScore", "suggestionText") SELECT "createdAt", "employeeId", "id", "punctualityScore", "rideId", "satisfactionScore", "suggestionText" FROM "RideFeedback";
DROP TABLE "RideFeedback";
ALTER TABLE "new_RideFeedback" RENAME TO "RideFeedback";
CREATE TABLE "new_RideParticipant" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
    "rideId" TEXT NOT NULL,
    "employeeId" TEXT NOT NULL,
    "pickUpLocation" TEXT NOT NULL,
    "meetingTime" DATETIME NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "createdAt" DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT "RideParticipant_rideId_fkey" FOREIGN KEY ("rideId") REFERENCES "OfferedRide" ("ride_id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "RideParticipant_employeeId_fkey" FOREIGN KEY ("employeeId") REFERENCES "Employee" ("employee_id") ON DELETE RESTRICT ON UPDATE CASCADE
);
INSERT INTO "new_RideParticipant" ("createdAt", "employeeId", "id", "meetingTime", "pickUpLocation", "rideId", "status") SELECT "createdAt", "employeeId", "id", "meetingTime", "pickUpLocation", "rideId", "status" FROM "RideParticipant";
DROP TABLE "RideParticipant";
ALTER TABLE "new_RideParticipant" RENAME TO "RideParticipant";
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
