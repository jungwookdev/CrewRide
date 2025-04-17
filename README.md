# 🚘 CrewRide: Smarter Carpooling with AI

CrewRide is an AI-enhanced corporate carpooling platform that matches employees for shared rides based on behavioral compatibility, commuting patterns, and social preferences. The system integrates a backend built with Next.js, a mobile Android app, and machine learning clustering techniques to create more enjoyable and socially attuned carpooling experiences.

---

## 🌟 Features

- **Socially-Aware Ride Matching**: Matches co-riders based on personality traits, commuting schedules, and preferences.
- **LLM-Powered Ride Preparation**: Offers personalized small-talk suggestions using a fine-tuned large language model.
- **Real-Time Ride Management**: Users can offer, find, and manage carpools via the mobile app.
- **Dynamic ETA Mapping**: See live routes and driver/rider info on embedded Google Maps.
- **Behavior-Based Clustering**: Uses Deep Embedded Clustering (DEC) for grouping compatible riders.

---

## 🗂️ Repository Structure

```
CrewRide/
├── backend/                # Next.js backend API
│   ├── prisma/             # Prisma ORM schema and migrations
│   ├── public/             # Static assets (e.g., images, icons)
│   ├── src/                # API route handlers, logic, utils
│   ├── .env                # Environment variables
│   ├── next.config.ts      # Next.js config
│   ├── tsconfig.json       # TypeScript config
│   └── package.json        # Backend dependencies and scripts

├── clustering/             # ML-based clustering pipeline
│   └── crewride_dec_pipeline.py  # DEC-based clustering logic

├── database/
│   ├── crewride.db         # SQLite database used for clustering
│   └── crewride_db_gen.py  # Script to generate the DB

├── mobileapp/
│   └── CrewRide/           # Android app source (Java/Kotlin)
│       ├── app/            # Main Android application code
│       ├── gradle/         # Gradle wrapper config
│       ├── build.gradle.kts        # Kotlin DSL build script
│       └── secrets.properties      # API keys or app secrets

├── README.md               # This file
├── .gitignore              # Git ignore rules
└── .gitattributes          # Git text encoding rules
```

---

## ⚙️ How It Works

### 🔁 Ride Matching Flow
1. **User Location & Destination Input**
2. **Backend Search API Filters:**
   - Available seats
   - Cluster membership
   - Do-not-match list
   - Departure time window
   - Proximity (max distance & bearing checks)
3. **Candidate Prioritization:**
   - Preferred-by list
   - Average rating
   - Punctuality score
4. **Sorted Results Returned to Client**

### 🤖 Clustering Logic (DEC)
- Applies Deep Embedded Clustering on behavioral and commute preference data from employees
- Outputs cluster ID per employee used in ride matching

### 🧠 LLM Ride Prep
- Large Language Model provides context-aware conversation starters (e.g., weather, light banter, shared interests)

---

## 📱 Mobile App (Android)

The Android app (under `mobileapp/CrewRide/`) features:
- Tabbed UI for upcoming/completed rides
- Ride offering dialog with social preference options
- Map-based view with ETA and participant list
- Direct call/chat/prep buttons for ride coordination

---

## 🚀 Getting Started

### Prerequisites
- Node.js v18+
- Android Studio
- Python 3.9+
- SQLite3

**Note:** `node_modules/` directory in the backend setup has been excluded from this repository view due to its large file size.

### Backend Setup
```bash
cd backend
npm install
npx prisma generate
npx prisma migrate dev
npm run dev
```

### Clustering Pipeline
```bash
cd clustering
python crewride_dec_pipeline.py
```

### Mobile App
Open `mobileapp/CrewRide` in Android Studio and run the app on an emulator or physical device.

---

## 🧪 Future Work

- In-the-wild user evaluation and clustering parameter refinement
- Fine-tuning LLMs for carpool etiquette and real-time adaptation
- Designing equitable ride contribution frameworks
- Deriving vehicle design insights from shared mobility data

---

## 📜 License

GPL-3.0 license. See `LICENSE` for details.

---
