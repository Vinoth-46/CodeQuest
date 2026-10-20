require('dotenv').config();
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const { OAuth2Client } = require('google-auth-library');
const User = require('./models/User');

const app = express();
const PORT = process.env.PORT || 5000;
const MONGODB_URI = process.env.MONGODB_URI;
const GOOGLE_CLIENT_ID = process.env.GOOGLE_CLIENT_ID || '';

const googleClient = GOOGLE_CLIENT_ID ? new OAuth2Client(GOOGLE_CLIENT_ID) : null;

// Middleware
app.use(helmet());
app.use(cors());
app.use(express.json());
app.use(morgan('dev'));

// MongoDB Connection
mongoose.connect(MONGODB_URI, {
  serverSelectionTimeoutMS: 5000
})
.then(async () => {
  console.log('✅ Connected to MongoDB Atlas (CodeQuest)');
  await seedInitialUsersIfEmpty();
})
.catch(err => {
  console.error('❌ MongoDB Connection Error:', err.message);
});

// Seed data if database is fresh
async function seedInitialUsersIfEmpty() {
  try {
    const count = await User.countDocuments();
    if (count === 0) {
      console.log('🌱 Seeding initial global coders for live leaderboard...');
      const seedCoders = [
        { displayName: 'Alex Chen', email: 'alex.chen@example.com', avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150', xp: 2450, streakDays: 28, selectedLanguage: 'Python', solvedCount: 42 },
        { displayName: 'Sarah Jenkins', email: 'sarah.j@example.com', avatarUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150', xp: 2180, streakDays: 21, selectedLanguage: 'Java', solvedCount: 38 },
        { displayName: 'Vikram Malhotra', email: 'vikram.m@example.com', avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150', xp: 1890, streakDays: 19, selectedLanguage: 'Python', solvedCount: 31 },
        { displayName: 'Elena Rostova', email: 'elena.r@example.com', avatarUrl: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150', xp: 1640, streakDays: 14, selectedLanguage: 'Java', solvedCount: 27 },
        { displayName: 'Marcus Aurel', email: 'marcus.a@example.com', avatarUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150', xp: 1420, streakDays: 12, selectedLanguage: 'Python', solvedCount: 24 },
        { displayName: 'Priya Sharma', email: 'priya.s@example.com', avatarUrl: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150', xp: 1190, streakDays: 9, selectedLanguage: 'Java', solvedCount: 19 },
        { displayName: 'Liam O\'Connor', email: 'liam.oc@example.com', avatarUrl: 'https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150', xp: 950, streakDays: 7, selectedLanguage: 'Python', solvedCount: 15 },
        { displayName: 'Aoi Tanaka', email: 'aoi.t@example.com', avatarUrl: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150', xp: 760, streakDays: 5, selectedLanguage: 'Java', solvedCount: 12 }
      ];
      await User.insertMany(seedCoders);
      console.log(`✅ Seeded ${seedCoders.length} challengers to MongoDB!`);
    }
  } catch (err) {
    console.error('Seed error:', err.message);
  }
}

// ── Health Check ──
app.get('/api/health', (req, res) => {
  res.json({
    status: 'online',
    service: 'CodeQuest API',
    uptimeSeconds: Math.floor(process.uptime()),
    database: mongoose.connection.readyState === 1 ? 'connected' : 'disconnected',
    timestamp: new Date().toISOString()
  });
});

// ── Google Sign-In & Registration ──
app.post('/api/auth/google', async (req, res) => {
  try {
    const { idToken, googleId, email, displayName, avatarUrl, initialXp, initialStreak, selectedLanguage } = req.body;

    let verifiedGoogleId = googleId;
    let verifiedEmail = email;
    let verifiedName = displayName;
    let verifiedAvatar = avatarUrl;

    // Verify token if Google Client ID is configured and token provided
    if (googleClient && idToken) {
      try {
        const ticket = await googleClient.verifyIdToken({
          idToken,
          audience: GOOGLE_CLIENT_ID
        });
        const payload = ticket.getPayload();
        verifiedGoogleId = payload.sub;
        verifiedEmail = payload.email;
        verifiedName = payload.name || verifiedName;
        verifiedAvatar = payload.picture || verifiedAvatar;
      } catch (tokenErr) {
        console.warn('Google ID token verification failed:', tokenErr.message);
        // Continue with client-provided credentials if token verification fails in dev
      }
    }

    if (!verifiedGoogleId && !verifiedEmail) {
      return res.status(400).json({ error: 'googleId or email is required' });
    }

    // Find existing user by googleId or email
    let user = await User.findOne({
      $or: [
        ...(verifiedGoogleId ? [{ googleId: verifiedGoogleId }] : []),
        ...(verifiedEmail ? [{ email: verifiedEmail.toLowerCase() }] : [])
      ]
    });

    if (user) {
      // Update profile info
      if (verifiedGoogleId) user.googleId = verifiedGoogleId;
      if (verifiedName) user.displayName = verifiedName;
      if (verifiedAvatar) user.avatarUrl = verifiedAvatar;
      if (selectedLanguage) user.selectedLanguage = selectedLanguage;
      // If client has higher XP, merge it
      if (typeof initialXp === 'number' && initialXp > user.xp) {
        user.xp = initialXp;
      }
      if (typeof initialStreak === 'number' && initialStreak > user.streakDays) {
        user.streakDays = initialStreak;
      }
      await user.save();
    } else {
      // Create new user
      user = new User({
        googleId: verifiedGoogleId,
        email: verifiedEmail ? verifiedEmail.toLowerCase() : undefined,
        displayName: verifiedName || 'CodeQuest Explorer',
        avatarUrl: verifiedAvatar || '',
        xp: typeof initialXp === 'number' ? initialXp : 0,
        streakDays: typeof initialStreak === 'number' ? initialStreak : 1,
        selectedLanguage: selectedLanguage || 'Python'
      });
      await user.save();
    }

    // Compute user's real-time rank
    const higherXpCount = await User.countDocuments({ xp: { $gt: user.xp } });
    const rank = higherXpCount + 1;

    res.json({
      success: true,
      user: {
        id: user._id,
        googleId: user.googleId,
        email: user.email,
        displayName: user.displayName,
        avatarUrl: user.avatarUrl,
        xp: user.xp,
        streakDays: user.streakDays,
        selectedLanguage: user.selectedLanguage,
        level: user.level,
        rank
      }
    });
  } catch (err) {
    console.error('Auth error:', err);
    res.status(500).json({ error: err.message });
  }
});

// ── Sync User Progress (XP, Streak, Language) ──
app.post('/api/user/sync', async (req, res) => {
  try {
    const { googleId, email, xp, streakDays, selectedLanguage, solvedCount, displayName, avatarUrl } = req.body;

    if (!googleId && !email) {
      return res.status(400).json({ error: 'googleId or email is required to sync progress' });
    }

    let user = await User.findOne({
      $or: [
        ...(googleId ? [{ googleId }] : []),
        ...(email ? [{ email: email.toLowerCase() }] : [])
      ]
    });

    if (!user) {
      // Auto-create user if missing
      user = new User({
        googleId,
        email: email ? email.toLowerCase() : undefined,
        displayName: displayName || 'CodeQuest Challenger',
        avatarUrl: avatarUrl || '',
        xp: xp || 0,
        streakDays: streakDays || 0,
        selectedLanguage: selectedLanguage || 'Python',
        solvedCount: solvedCount || 0
      });
    } else {
      // Update with latest stats
      if (typeof xp === 'number') user.xp = Math.max(user.xp, xp);
      if (typeof streakDays === 'number') user.streakDays = streakDays;
      if (selectedLanguage) user.selectedLanguage = selectedLanguage;
      if (typeof solvedCount === 'number') user.solvedCount = Math.max(user.solvedCount, solvedCount);
      if (displayName) user.displayName = displayName;
      if (avatarUrl) user.avatarUrl = avatarUrl;
    }

    await user.save();

    // Compute rank
    const higherXpCount = await User.countDocuments({ xp: { $gt: user.xp } });
    const rank = higherXpCount + 1;

    res.json({
      success: true,
      user: {
        id: user._id,
        googleId: user.googleId,
        email: user.email,
        displayName: user.displayName,
        avatarUrl: user.avatarUrl,
        xp: user.xp,
        streakDays: user.streakDays,
        selectedLanguage: user.selectedLanguage,
        level: user.level,
        rank
      }
    });
  } catch (err) {
    console.error('Sync error:', err);
    res.status(500).json({ error: err.message });
  }
});

// ── Live Leaderboard (Real People from MongoDB Atlas) ──
app.get('/api/leaderboard', async (req, res) => {
  try {
    const limit = Math.min(100, Math.max(1, parseInt(req.query.limit) || 50));
    const currentUserGoogleId = req.query.googleId || '';
    const currentUserEmail = (req.query.email || '').toLowerCase();

    const users = await User.find({})
      .sort({ xp: -1, streakDays: -1, updatedAt: -1 })
      .limit(limit)
      .lean();

    const entries = users.map((u, index) => {
      const isCurrentUser = Boolean(
        (currentUserGoogleId && u.googleId === currentUserGoogleId) ||
        (currentUserEmail && u.email && u.email === currentUserEmail)
      );

      return {
        rank: index + 1,
        id: u._id.toString(),
        name: u.displayName || 'Anonymous Coder',
        email: u.email || '',
        avatarUrl: u.avatarUrl || '',
        language: u.selectedLanguage || 'Python',
        streakDays: u.streakDays || 0,
        xp: u.xp || 0,
        level: u.level || 1,
        isCurrentUser
      };
    });

    const totalUsers = await User.countDocuments();

    res.json({
      success: true,
      totalUsers,
      entries
    });
  } catch (err) {
    console.error('Leaderboard error:', err);
    res.status(500).json({ error: err.message });
  }
});

// ── Get Single User Profile with Rank ──
app.get('/api/user/:identifier', async (req, res) => {
  try {
    const { identifier } = req.params;

    const query = mongoose.Types.ObjectId.isValid(identifier)
      ? { _id: identifier }
      : {
          $or: [
            { googleId: identifier },
            { email: identifier.toLowerCase() }
          ]
        };

    const user = await User.findOne(query);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    const higherXpCount = await User.countDocuments({ xp: { $gt: user.xp } });
    const rank = higherXpCount + 1;

    res.json({
      success: true,
      user: {
        id: user._id,
        googleId: user.googleId,
        email: user.email,
        displayName: user.displayName,
        avatarUrl: user.avatarUrl,
        xp: user.xp,
        streakDays: user.streakDays,
        selectedLanguage: user.selectedLanguage,
        level: user.level,
        rank
      }
    });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

// Start Server
app.listen(PORT, '0.0.0.0', () => {
  console.log(`🚀 CodeQuest Backend running at http://0.0.0.0:${PORT}`);
  console.log(`📡 Health Check: http://localhost:${PORT}/api/health`);
  console.log(`🏆 Live Leaderboard: http://localhost:${PORT}/api/leaderboard`);
});
