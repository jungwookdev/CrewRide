/*
  Warnings:

  - You are about to alter the column `auto_match_enabled` on the `Employee` table. The data in that column could be lost. The data in that column will be cast from `Boolean` to `Int`.

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
    "auto_match_enabled" INTEGER,
    "preferred_carpool_group_size" INTEGER,
    "frequent_co_riders" TEXT,
    "do_not_match_list" TEXT,
    "cluster" INTEGER
);
INSERT INTO "new_Employee" ("allergies_pet_peeves", "auto_match_enabled", "average_rating", "car_capacity", "car_model", "carpool_insurance_coverage", "carpool_role", "cluster", "co_rider_gender_preference", "comfort_level", "conversation_topics", "days_working_in_office", "department", "do_not_match_list", "driving_availability_days", "emergency_contact", "employee_id", "frequent_co_riders", "fuel_type", "home_area_name", "home_latitude", "home_longitude", "job_role", "languages_spoken", "meeting_heavy_days", "music_preference", "name", "notification_preferences", "parking_availability_near_home", "preferred_by_list", "preferred_carpool_group_size", "preferred_communication", "preferred_dropoff_time_to_goto_work", "preferred_dropoff_time_to_leave_from_work", "preferred_pickup_time_to_goto_work", "preferred_pickup_time_to_leave_from_work", "punctuality_score", "remote_days", "ride_frequency_preference", "seniority_level", "talk_preferences", "work_hours_end", "work_hours_start", "work_schedule_type") SELECT "allergies_pet_peeves", "auto_match_enabled", "average_rating", "car_capacity", "car_model", "carpool_insurance_coverage", "carpool_role", "cluster", "co_rider_gender_preference", "comfort_level", "conversation_topics", "days_working_in_office", "department", "do_not_match_list", "driving_availability_days", "emergency_contact", "employee_id", "frequent_co_riders", "fuel_type", "home_area_name", "home_latitude", "home_longitude", "job_role", "languages_spoken", "meeting_heavy_days", "music_preference", "name", "notification_preferences", "parking_availability_near_home", "preferred_by_list", "preferred_carpool_group_size", "preferred_communication", "preferred_dropoff_time_to_goto_work", "preferred_dropoff_time_to_leave_from_work", "preferred_pickup_time_to_goto_work", "preferred_pickup_time_to_leave_from_work", "punctuality_score", "remote_days", "ride_frequency_preference", "seniority_level", "talk_preferences", "work_hours_end", "work_hours_start", "work_schedule_type" FROM "Employee";
DROP TABLE "Employee";
ALTER TABLE "new_Employee" RENAME TO "Employee";
PRAGMA foreign_keys=ON;
PRAGMA defer_foreign_keys=OFF;
