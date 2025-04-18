import sqlite3
import random
import math
import string
import pandas as pd
import requests
import polyline
import json


# Some typical Bay Area cities near Los Altos
BAY_AREA_LOCATIONS = [
    ("Los Altos", 37.3688, -122.0970),
    ("Mountain View", 37.3861, -122.0839),
    ("Palo Alto", 37.4419, -122.1430),
    ("Sunnyvale", 37.3688, -122.0363),
    ("Santa Clara", 37.3541, -121.9552),
    ("Menlo Park", 37.452959, -122.181725),
    ("Redwood City", 37.4852, -122.2364),
    ("San Jose", 37.3382, -121.8863),
    ("Fremont", 37.5485, -121.9886),
    ("San Francisco", 37.7749, -122.4194),
    ("San Mateo", 37.5630, -122.3255),
    ("Milpitas", 37.4323, -121.8996),
    ("Cupertino", 37.3229978, -122.0321823),
    ("Campbell", 37.2872, -121.94996),
    ("Burlingame", 37.5779, -122.3481),
]

DEPARTMENTS = [
    "AI Research", "Engineering", "Product", "UX", 
    "Mobility Ops", "HR", "Finance", "Marketing", "Legal"
]

DEPT_TO_ROLE = {
    "AI Research": ["Research Scientist", "Data Scientist"],
    "Engineering": ["Software Engineer", "DevOps Engineer", "Embedded Engineer"],
    "Product": ["Product Manager", "Project Manager"],
    "UX": ["UI/UX Designer"],
    "Mobility Ops": ["Operations Manager"],
    "HR": ["HR Specialist"],
    "Finance": ["Accountant"],
    "Marketing": ["Marketing Manager"],
    "Legal": ["Legal Counsel"]
}

SENIORITY_LEVELS = ["Junior", "Mid", "Senior", "Lead", "Director"]

WORK_SCHEDULE_TYPES = ["Fixed", "Flexible", "Shift-based"]
WEEKDAYS = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"]

CARPOOL_ROLES = ["Rider", "Driver", "Both"]
FREQUENCY_PREFS = ["Daily", "Weekly", "Ad-hoc"]
COMM_STYLES = ["Chatty", "Quiet", "Music", "No work-talk"]
GENDER_PREFS = ["Any", "Female only", "Male only"]
MUSIC_GENRES = ["Pop", "Rock", "Jazz", "Classical", "Hip Hop", "EDM"]
TALK_PREFERENCES = ["Deep convos", "Light banter", "Podcasts", "Music", "No small-talk"]
FUEL_TYPES = ["Petrol", "Diesel", "EV", "Hybrid"]

LANGUAGE_POOL = [
    "English", "Spanish", "Chinese", "Hindi", "French", "German", 
    "Korean", "Japanese", "Russian", "Arabic"
]

NOTIFICATION_TYPES = ["App", "Email", "SMS"]

PARKING_OPTIONS = ["Street Parking", "Private Garage", "Shared Parking Lot", "None"]

COMFORT_LEVELS = ["Small", "Medium", "Spacious"]

HQ_LAT, HQ_LON = 37.380644, -122.113323


CAR_MODELS = {
    "Toyota Prius": "Hybrid",
    "Honda Civic": "Petrol",
    "Tesla Model 3": "EV",
    "Ford Focus": "Petrol",
    "Chevy Bolt": "EV",
    "Hyundai Sonata": "Diesel",
    "Subaru Forester": "Petrol",
    "Nissan Leaf": "EV"
}

def random_name():
    # Very simple random name generator
    first_names = [
        "James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda", 
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", 
        "Jessica", "Thomas", "Sarah", "Charles", "Karen", "Christopher", "Nancy", 
        "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Margaret", "Mark", "Sandra",
        "Paul", "Ashley", "Steven", "Donna", "Andrew", "Emily", "Kenneth", "Carol", 
        "Joshua", "Michelle", "George", "Dorothy", "Kevin", "Angela", "Brian", "Melissa",
        "Edward", "Deborah", "Ronald", "Stephanie"
    ]
    last_names = [
        "Smith", "Johnson", "Brown", "Williams", "Jones", "Miller", "Davis", "Garcia", 
        "Rodriguez", "Wilson", "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez", 
        "Moore", "Martin", "Jackson", "Thompson", "White", "Lopez", "Lee", "Gonzalez", 
        "Harris", "Clark", "Lewis", "Robinson", "Walker", "Perez", "Hall", "Young", 
        "Allen", "Sanchez", "Wright", "King", "Scott", "Green", "Baker", "Adams", "Nelson",
        "Hill", "Ramirez", "Campbell", "Mitchell", "Roberts", "Carter", "Phillips", 
        "Evans", "Turner", "Torres"
    ]
    first = random.choice(first_names)
    last = random.choice(last_names)
    return f"{first} {last}"

def random_phone_number():
    area_code = random.randint(200, 999)
    first3 = random.randint(200, 999)
    last4 = random.randint(0, 9999)
    return f"({area_code}) {first3}-{last4:04d}"

def random_languages():
    langs = ["English"]
    if random.random() < 0.6:
        langs.append(random.choice(LANGUAGE_POOL[1:]))
    if random.random() < 0.3:
        langs.append(random.choice(LANGUAGE_POOL[1:]))
    return ", ".join(set(langs))

def random_genre():
    genre = []
    if random.random() < 0.6:
        genre.append(random.choice(MUSIC_GENRES[1:]))
    if random.random() < 0.3:
        genre.append(random.choice(MUSIC_GENRES[1:]))
    return ", ".join(set(genre))

def random_lat_lng_around_los_altos():
    city, city_lat, city_lng = random.choice(BAY_AREA_LOCATIONS)
    lat = city_lat + random.uniform(-0.05, 0.05)
    lng = city_lng + random.uniform(-0.05, 0.05)
    return city, lat, lng

def random_subset_of_days():
    return ",".join(random.sample(WEEKDAYS, k=random.randint(2, 5)))

def random_time(start_h=7, end_h=10):
    return f"{random.randint(start_h, end_h):02d}:{random.randint(0, 59):02d}"

def random_time_evening(start_h=16, end_h=20):
    return f"{random.randint(start_h, end_h):02d}:{random.randint(0, 59):02d}"

def random_allergies():
    samples = ["Strong scents", "Pet hair", "Loud music", "Food smells"]
    return ", ".join(random.sample(samples, k=random.randint(0, 2)))

def random_frequent_co_riders(self_id, total=1000):
    size = random.randint(0, 4)
    riders = set()
    for _ in range(size):
        candidate = random.randint(1, total)
        while candidate == self_id or candidate in riders:
            candidate = random.randint(1, total)
        riders.add(candidate)
    return ",".join([f"EMP{str(c).zfill(3)}" for c in riders])

def random_block_list(self_id, total=1000):
    size = random.randint(0, 3)
    blocked = set()
    for _ in range(size):
        candidate = random.randint(1, total)
        while candidate == self_id or candidate in blocked:
            candidate = random.randint(1, total)
        blocked.add(candidate)
    return ",".join([f"EMP{str(c).zfill(3)}" for c in blocked])

def random_preferred_by_list(self_id, total=1000):
    size = random.randint(0, 5)
    pby = set()
    for _ in range(size):
        candidate = random.randint(1, total)
        while candidate == self_id or candidate in pby:
            candidate = random.randint(1, total)
        pby.add(candidate)
    return ",".join([f"EMP{str(c).zfill(3)}" for c in pby])

# --- Helper Function to Generate a Random Time within a Range ---
def random_time_range(start_str, end_str):
    """
    Given start and end times in 'HH:MM' format, return a random time (as a string in HH:MM)
    between them.
    """
    start_parts = list(map(int, start_str.split(":")))
    end_parts = list(map(int, end_str.split(":")))
    start_min = start_parts[0] * 60 + start_parts[1]
    end_min = end_parts[0] * 60 + end_parts[1]
    random_min = random.randint(start_min, end_min)
    hour = random_min // 60
    minute = random_min % 60
    return f"{hour:02d}:{minute:02d}"


def create_table_employees():
    db_name = "crewride.db"
    conn = sqlite3.connect(db_name)
    cur = conn.cursor()

    cur.execute("DROP TABLE IF EXISTS employees")

    cur.execute("""
    CREATE TABLE employees (
        employee_id TEXT PRIMARY KEY,
        name TEXT,
        department TEXT,
        job_role TEXT,
        work_schedule_type TEXT,
        remote_days TEXT,
        languages_spoken TEXT,
        emergency_contact TEXT,
        seniority_level TEXT,
        home_latitude REAL,
        home_longitude REAL,
        home_area_name TEXT,
        parking_availability_near_home TEXT,
        work_hours_start TEXT,
        work_hours_end TEXT,
        days_working_in_office TEXT,
        meeting_heavy_days TEXT,
        carpool_role TEXT,
        driving_availability_days TEXT,
        preferred_pickup_time_to_goto_work TEXT,
        preferred_dropoff_time_to_goto_work TEXT,
        preferred_pickup_time_to_leave_from_work TEXT,
        preferred_dropoff_time_to_leave_from_work TEXT,
        ride_frequency_preference TEXT,
        preferred_communication TEXT,
        music_preference TEXT,
        co_rider_gender_preference TEXT,
        talk_preferences TEXT,
        conversation_topics TEXT,
        allergies_pet_peeves TEXT,
        car_model TEXT,
        car_capacity INTEGER,
        comfort_level TEXT,
        fuel_type TEXT,
        carpool_insurance_coverage TEXT,
        average_rating REAL,
        punctuality_score REAL,
        preferred_by_list TEXT,
        notification_preferences TEXT,
        auto_match_enabled INTEGER,
        preferred_carpool_group_size INTEGER,
        frequent_co_riders TEXT,
        do_not_match_list TEXT
    )
    """)

    num_employees = 1000

    for i in range(1, num_employees + 1):
        emp_id = f"EMP{str(i).zfill(3)}"
        full_name = random_name()
        dept = random.choice(DEPARTMENTS)
        role = random.choice(DEPT_TO_ROLE[dept])
        schedule_type = random.choice(WORK_SCHEDULE_TYPES)
        remote_days_str = ",".join(random.sample(WEEKDAYS, k=random.randint(0,2)))
        langs = random_languages()
        emer_contact = random_phone_number()
        seniority = random.choice(SENIORITY_LEVELS)
        city, lat, lon = random_lat_lng_around_los_altos()
        parking = random.choice(PARKING_OPTIONS)
        wh_start = random_time(7, 10)
        wh_end = random_time_evening(16, 20)
        office_days = random_subset_of_days()
        meeting_days = random.choice(WEEKDAYS) if random.random() < 0.5 else ""
        c_role = random.choice(CARPOOL_ROLES)
        if c_role in ["Driver", "Both"]:
            office_day_list = office_days.split(",")
            subset_size = random.randint(1, len(office_day_list))
            drive_days = ",".join(random.sample(office_day_list, k=subset_size))
        else:
            drive_days = ""
        pickup_go = random_time(7, 9)
        dropoff_go = random_time_evening(17, 19)
        pickup_return = random_time_evening(17, 19)
        dropoff_return = random_time_evening(17, 19)
        frequency = random.choice(FREQUENCY_PREFS)
        comm_style = ", ".join(random.sample(COMM_STYLES, k=random.randint(1,2)))
        music_pref = random_genre()
        gender_pref = random.choice(GENDER_PREFS)
        talk_prefs = ", ".join(random.sample(TALK_PREFERENCES, k=random.randint(1,2)))
        possible_topics = ["Tech", "Sports", "Books", "Movies", "Food", "Travel"]
        conv_topics = ", ".join([t for t in possible_topics if random.random() < 0.3])
        allergies = random_allergies()
        if c_role in ["Driver", "Both"]:
            chosen_car = random.choice(list(CAR_MODELS.keys()))
            capacity = random.randint(2, 5)
            comfort = random.choice(COMFORT_LEVELS)
            fuel = CAR_MODELS[chosen_car]
            insurance = "Full Coverage" if random.random() < 0.4 else ""
        else:
            chosen_car = ""
            capacity = 0
            comfort = ""
            fuel = ""
            insurance = ""
        avg_rating = round(random.uniform(3.0, 5.0), 2)
        punctuality = round(random.uniform(3.0, 5.0), 2)
        preferred_by = random_preferred_by_list(i, num_employees)
        notif_types = ",".join(random.sample(NOTIFICATION_TYPES, k=random.randint(1, 3)))
        auto_match = 1
        grp_size = random.randint(2, 4)
        freq_riders = random_frequent_co_riders(i, num_employees)
        block_list = random_block_list(i, num_employees)

        cur.execute("""
            INSERT INTO employees VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """, (
            emp_id,
            full_name,
            dept,
            role,
            schedule_type,
            remote_days_str,
            langs,
            emer_contact,
            seniority,
            lat,
            lon,
            city,
            parking,
            wh_start,
            wh_end,
            office_days,
            meeting_days,
            c_role,
            drive_days,
            pickup_go,
            dropoff_go,
            pickup_return,
            dropoff_return,
            frequency,
            comm_style,
            music_pref,
            gender_pref,
            talk_prefs,
            conv_topics,
            allergies,
            chosen_car,
            capacity,
            comfort,
            fuel,
            insurance,
            avg_rating,
            punctuality,
            preferred_by,
            notif_types,
            auto_match,
            grp_size,
            freq_riders,
            block_list
        ))

    conn.commit()
    conn.close()
    print(f"[INFO] Successfully generated {num_employees} employees in '{db_name}'.")

# Replace with your actual ORS API key.
ORS_API_KEY = "ORS_API_KEY" #TODO: Add the ORS API Key here

def get_ors_route(start_lat, start_lon, end_lat, end_lon, api_key):
    """
    Retrieve a driving route from OpenRouteService between two points.
    Returns a list of [lat, lon] coordinates if successful; otherwise, None.
    """
    url = "https://api.openrouteservice.org/v2/directions/driving-car"
    headers = {
        "Authorization": api_key,
        "Content-Type": "application/json"
    }
    body = {
        "coordinates": [[start_lon, start_lat], [end_lon, end_lat]],
        "format": "json"
    }
    try:
        response = requests.post(url, json=body, headers=headers)
        response.raise_for_status()
        data = response.json()
        # Use the "routes" key from ORS response.
        if "routes" not in data or len(data["routes"]) == 0:
            print("ORS API response:", data)
            raise ValueError("No routes in ORS API response.")
        # The geometry is an encoded polyline string.
        encoded_geometry = data["routes"][0]["geometry"]
        # Decode the polyline string.
        route_coords = polyline.decode(encoded_geometry)
        # polyline.decode returns a list of (lat, lon) pairs.
        return route_coords
    except Exception as e:
        print(f"Error fetching ORS route for ({start_lat}, {start_lon}) -> ({end_lat}, {end_lon}): {e}")
        return None

def create_commute_rides_from_employees_by_area():
    conn = sqlite3.connect("crewride.db")
    cur = conn.cursor()
    
    # Drop the offered_ride table if it exists
    cur.execute("DROP TABLE IF EXISTS offered_ride")
    
    # Create the offered_ride table with an extra 'completed' and 'driving_path' column.
    cur.execute("""
        CREATE TABLE offered_ride (
            ride_id TEXT PRIMARY KEY,
            driver_id TEXT,
            start_latitude REAL,
            start_longitude REAL,
            destination_latitude REAL,
            destination_longitude REAL,
            departure_time TEXT,
            buffer_time INTEGER,
            offered_seats INTEGER,
            booked_users TEXT,
            completed INTEGER,
            driving_path TEXT
        )
    """)
    
    # Query employees with carpool_role as 'Driver' or 'Both'
    employees_df = pd.read_sql_query("""
        SELECT employee_id, home_latitude, home_longitude, home_area_name
        FROM employees
        WHERE carpool_role IN ('Driver', 'Both')
    """, conn)
    
    rides = []
    ride_num = 1
    sample_date = "2023-08-01"  # Sample date for all rides
    
    # Group employees by home_area_name and select up to 10 per group.
    grouped = employees_df.groupby("home_area_name")
    for home_area, group in grouped:
        # Randomly select up to 10 employees in this home area.
        sampled = group.sample(n=min(1, len(group)), random_state=42)
        
        # For this home area, randomly choose a route type: "morning" or "evening".
        route_type = random.choice(["morning", "evening"])
        # Use one sample employee to get the driving route.
        sample_row = sampled.iloc[0]
        emp_id_sample = sample_row['employee_id']
        home_lat_sample = sample_row['home_latitude']
        home_lon_sample = sample_row['home_longitude']
        
        if route_type == "morning":
            # Morning route: from employee's home to HQ.
            ors_route = get_ors_route(home_lat_sample, home_lon_sample, HQ_LAT, HQ_LON, ORS_API_KEY)
        else:
            # Evening route: from HQ to employee's home.
            ors_route = get_ors_route(HQ_LAT, HQ_LON, home_lat_sample, home_lon_sample, ORS_API_KEY)
        
        # Convert the route (if found) to a JSON string.
        driving_path = json.dumps(ors_route) if ors_route is not None else ""
        
        # For each sampled employee in this home area, generate two rides with the same driving_path.
        for idx, row in sampled.iterrows():
            emp_id = row['employee_id']
            home_lat = row['home_latitude']
            home_lon = row['home_longitude']
            
            # Skip if home location info is missing.
            if pd.isnull(home_lat) or pd.isnull(home_lon):
                continue
            
            # Morning ride: from employee's home to HQ.
            departure_morning = f"{sample_date} {random_time_range('07:30', '08:30')}"
            ride_id_morning = f"RIDE{ride_num:04d}"
            ride_num += 1
            rides.append((
                ride_id_morning,
                emp_id,
                home_lat, home_lon,   # Start: employee's home
                HQ_LAT, HQ_LON,       # Destination: HQ
                departure_morning,
                10,                   # Buffer time in minutes
                random.randint(1, 4), # Offered seats
                "",                   # Booked users (empty)
                0,                    # Completed flag
                driving_path          # Driving path from ORS (same for all in this group)
            ))
            
            # Evening ride: from HQ to employee's home.
            departure_evening = f"{sample_date} {random_time_range('16:00', '17:00')}"
            ride_id_evening = f"RIDE{ride_num:04d}"
            ride_num += 1
            rides.append((
                ride_id_evening,
                emp_id,
                HQ_LAT, HQ_LON,       # Start: HQ
                home_lat, home_lon,   # Destination: employee's home
                departure_evening,
                10,                   # Buffer time in minutes
                random.randint(1, 4), # Offered seats
                "",                   # Booked users (empty)
                0,                    # Completed flag
                driving_path          # Driving path from ORS (same for all in this group)
            ))
    
    cur.executemany("INSERT INTO offered_ride VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", rides)
    conn.commit()
    conn.close()
    print(f"[INFO] {len(rides)} rides inserted into offered_ride table in crewride.db.")

if __name__ == "__main__":
    #create_table_employees()
    create_commute_rides_from_employees_by_area()