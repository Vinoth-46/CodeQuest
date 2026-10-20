# 🏆 CodeQuest Live Leaderboard & Authentication API

Production-ready backend connecting the CodeQuest Android app to **MongoDB Atlas** for live leaderboards and Google Sign-In.

---

## ⚡ Features
- **MongoDB Atlas Integration**: Stores players, XP, streaks, levels, and challenge stats in real time.
- **Google Sign-In**: Verifies Google ID tokens and supports seamless player profile registration.
- **Live Leaderboard**: Real-time sorted rankings (`/api/leaderboard`) with user rank calculation.
- **Progress Sync**: Updates XP and streaks automatically whenever users complete coding quests.

---

## 🚀 1-Click Free Deployment

### Deploy to Render (Recommended)
1. Fork or push this repository to GitHub.
2. Go to [Render.com](https://render.com) and create a new **Web Service**.
3. Select your repository, set the **Root Directory** to `backend`.
4. Add the Environment Variable:
   - `MONGODB_URI`: `mongodb+srv://<username>:<password>@cluster0.byg4ifj.mongodb.net/codequest?retryWrites=true&w=majority` (use your MongoDB Atlas credentials)
   - `GOOGLE_CLIENT_ID`: *(Optional - your Google Cloud Web Client ID)*
5. Click **Deploy Web Service**! You'll receive a free public HTTPS URL:
   `https://codequest-api.onrender.com`

---

## 🛠️ Local Development
```bash
cd backend
npm install
npm start
```
Server runs at `http://localhost:5000`.
- Health Check: `http://localhost:5000/api/health`
- Live Leaderboard: `http://localhost:5000/api/leaderboard`
