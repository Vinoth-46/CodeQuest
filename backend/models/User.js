const mongoose = require('mongoose');

const UserSchema = new mongoose.Schema({
  googleId: {
    type: String,
    sparse: true,
    unique: true,
    index: true
  },
  email: {
    type: String,
    lowercase: true,
    trim: true,
    sparse: true,
    index: true
  },
  displayName: {
    type: String,
    required: true,
    trim: true,
    default: 'CodeQuest Challenger'
  },
  avatarUrl: {
    type: String,
    default: ''
  },
  xp: {
    type: Number,
    default: 0,
    index: -1 // Fast descending sort for leaderboard queries
  },
  streakDays: {
    type: Number,
    default: 0
  },
  selectedLanguage: {
    type: String,
    default: 'Python'
  },
  solvedCount: {
    type: Number,
    default: 0
  },
  level: {
    type: Number,
    default: 1
  },
  lastActive: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

// Helper to compute level from XP (e.g., Level = floor(sqrt(XP / 50)) + 1)
UserSchema.methods.calculateLevel = function() {
  if (this.xp <= 0) return 1;
  return Math.max(1, Math.floor(Math.sqrt(this.xp / 40)) + 1);
};

UserSchema.pre('save', function(next) {
  this.level = this.calculateLevel();
  this.lastActive = new Date();
  next();
});

module.exports = mongoose.model('User', UserSchema);
