import React, { useState, useMemo } from 'react';
import Header from './components/Header';
import SearchBar from './components/SearchBar';
import ContinueCard from './components/ContinueCard';
import DailyChallenge from './components/DailyChallenge';
import PathList from './components/PathList';
import CourseCard from './components/CourseCard';
import ProgressStats from './components/ProgressStats';
import BottomNav from './components/BottomNav';

import {
  userData,
  continueLearningData,
  dailyChallengeData,
  learningPathsData,
  recommendedCourses,
  navigationTabs
} from './data';

import { 
  Sparkles, 
  ChevronRight, 
  CheckCircle, 
  Trophy, 
  Code2, 
  BookOpen, 
  User, 
  Award,
  Zap,
  Terminal,
  Play
} from 'lucide-react';

export default function App() {
  const [user, setUser] = useState(userData);
  const [continueState, setContinueState] = useState(continueLearningData);
  const [activeTab, setActiveTab] = useState('home');
  const [searchQuery, setSearchQuery] = useState('');
  const [modalInfo, setModalInfo] = useState(null);

  // Toggle between active learner and new user state
  const handleToggleContinueState = () => {
    setContinueState(prev => ({
      ...prev,
      hasStarted: !prev.hasStarted
    }));
  };

  // Filter recommended courses based on search query
  const filteredCourses = useMemo(() => {
    if (!searchQuery.trim()) return recommendedCourses;
    const q = searchQuery.toLowerCase();
    return recommendedCourses.filter(course => 
      course.title.toLowerCase().includes(q) ||
      course.tag.toLowerCase().includes(q) ||
      course.level.toLowerCase().includes(q)
    );
  }, [searchQuery]);

  // Handlers for interactive actions
  const handleResume = () => {
    setModalInfo({
      title: "Resuming Lesson",
      subtitle: continueState.course.currentLesson,
      details: "Loading interactive IDE sandbox and test cases for Asyncio Event Loops...",
      badge: "In Progress",
      type: "resume"
    });
  };

  const handleStartHere = () => {
    setModalInfo({
      title: "Welcome to CodeQuest!",
      subtitle: "Choose Your Starter Path",
      details: "Recommended: Python Syntax Fundamentals or Modern Web Dev. Zero setup required.",
      badge: "Getting Started",
      type: "start"
    });
  };

  const handleStartChallenge = () => {
    setModalInfo({
      title: "Daily Quest Arena",
      subtitle: dailyChallengeData.title,
      details: "Run against 4 automated unit test cases. Complete in < 5 mins for bonus gems!",
      badge: `${dailyChallengeData.difficulty} • +${dailyChallengeData.xpReward} XP`,
      type: "challenge"
    });
  };

  const handleSelectMilestone = (milestone, path) => {
    setModalInfo({
      title: `${path.name} — ${milestone.tier}`,
      subtitle: milestone.title,
      details: `${milestone.description} (${milestone.lessonsCount} interactive lessons)`,
      badge: milestone.status.toUpperCase(),
      type: "milestone"
    });
  };

  const handleSelectCourse = (course) => {
    setModalInfo({
      title: course.title,
      subtitle: `${course.level} • ${course.duration} • ${course.rating} ⭐`,
      details: `Curriculum: ${course.tag}. Join ${course.enrolledCount} active coders.`,
      badge: course.progress > 0 ? `${course.progress}% Completed` : "New Course",
      type: "course"
    });
  };

  return (
    <div className="app-container">
      {/* 1. Header Component */}
      <Header user={user} />

      {/* Main Tab Routing */}
      {activeTab === 'home' && (
        <main className="home-main-content">
          {/* 2. SearchBar Component */}
          <SearchBar 
            value={searchQuery}
            onChange={setSearchQuery}
            onSearchSubmit={(val) => setSearchQuery(val)}
          />

          {/* 3. Continue Learning Card */}
          <ContinueCard 
            continueData={continueState}
            onToggleState={handleToggleContinueState}
            onResume={handleResume}
            onStart={handleStartHere}
          />

          {/* 4. Daily Challenge Card */}
          <DailyChallenge 
            challenge={dailyChallengeData}
            onStartChallenge={handleStartChallenge}
          />

          {/* 5. Learning Paths Roadmap */}
          <PathList 
            paths={learningPathsData}
            onSelectMilestone={handleSelectMilestone}
          />

          {/* 6. Recommended for you (Horizontal Scroll) */}
          <section className="recommended-section">
            <div className="section-header">
              <h2 className="section-title">
                <Sparkles size={18} style={{ color: '#818CF8' }} />
                Recommended for You
              </h2>
              <button 
                className="section-action" 
                onClick={() => setActiveTab('learn')}
              >
                <span>View All</span>
                <ChevronRight size={14} />
              </button>
            </div>

            <div className="recommended-scroll-track hide-scrollbar">
              {filteredCourses.length > 0 ? (
                filteredCourses.map((course) => (
                  <CourseCard 
                    key={course.id}
                    course={course}
                    onSelectCourse={handleSelectCourse}
                  />
                ))
              ) : (
                <div className="no-search-results">
                  <p>No courses matching "{searchQuery}".</p>
                  <button 
                    className="cq-button cq-button-secondary"
                    onClick={() => setSearchQuery('')}
                    style={{ marginTop: '8px' }}
                  >
                    Clear Filter
                  </button>
                </div>
              )}
            </div>
          </section>

          {/* 7. Progress Snapshot */}
          <ProgressStats 
            stats={user.stats}
            badges={user.badges}
          />
        </main>
      )}

      {/* Learn Tab Fallback View */}
      {activeTab === 'learn' && (
        <div className="alt-tab-view">
          <div className="alt-tab-header">
            <BookOpen size={32} className="alt-tab-icon" />
            <h2>Full Curriculum & Catalog</h2>
            <p>Explore all 4 specialized learning tracks from Beginner to Expert.</p>
          </div>
          <PathList 
            paths={learningPathsData}
            onSelectMilestone={handleSelectMilestone}
          />
          <section className="all-courses-grid">
            <h3 className="section-title" style={{ padding: '0 20px', marginBottom: '12px' }}>
              All Available Modules
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', padding: '0 20px' }}>
              {recommendedCourses.map(course => (
                <div key={course.id} style={{ width: '100%' }}>
                  <CourseCard course={course} onSelectCourse={handleSelectCourse} />
                </div>
              ))}
            </div>
          </section>
        </div>
      )}

      {/* Practice Tab View */}
      {activeTab === 'practice' && (
        <div className="alt-tab-view">
          <div className="alt-tab-header">
            <Code2 size={32} className="alt-tab-icon" />
            <h2>Interactive Coding Arena</h2>
            <p>Solve algorithm challenges, fix missing code, and predict terminal outputs.</p>
          </div>
          <DailyChallenge 
            challenge={dailyChallengeData}
            onStartChallenge={handleStartChallenge}
          />
        </div>
      )}

      {/* Leaderboard Tab View */}
      {activeTab === 'leaderboard' && (
        <div className="alt-tab-view">
          <div className="alt-tab-header">
            <Trophy size={32} className="alt-tab-icon" style={{ color: '#F59E0B' }} />
            <h2>Diamond League</h2>
            <p>Top 10 coders advance to the Master Division this week.</p>
          </div>
          <div className="league-podium-card cq-card" style={{ margin: '0 20px', padding: '20px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-around', alignItems: 'flex-end', height: '140px', borderBottom: '1px solid rgba(255,255,255,0.08)', paddingBottom: '12px' }}>
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '1.2rem' }}>🥈</span>
                <div style={{ fontWeight: 700, fontSize: '0.85rem' }}>Sarah_K</div>
                <div style={{ fontSize: '0.72rem', color: '#94A3B8' }}>3,420 XP</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '1.8rem' }}>👑</span>
                <div style={{ fontWeight: 800, fontSize: '0.95rem', color: '#FBBF24' }}>DevNinja</div>
                <div style={{ fontSize: '0.75rem', color: '#F59E0B' }}>4,150 XP</div>
              </div>
              <div style={{ textAlign: 'center' }}>
                <span style={{ fontSize: '1.2rem' }}>🥉</span>
                <div style={{ fontWeight: 700, fontSize: '0.85rem' }}>Arun_V</div>
                <div style={{ fontSize: '0.72rem', color: '#94A3B8' }}>3,180 XP</div>
              </div>
            </div>
            <div style={{ marginTop: '16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: 'rgba(99,102,241,0.1)', padding: '12px', borderRadius: '10px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <span style={{ fontWeight: 800, color: '#818CF8' }}>#6</span>
                <span style={{ fontWeight: 700 }}>{user.name} (You)</span>
              </div>
              <span style={{ fontWeight: 800, color: '#F8FAFC' }}>{user.stats.xp} XP</span>
            </div>
          </div>
        </div>
      )}

      {/* Profile Tab View */}
      {activeTab === 'profile' && (
        <div className="alt-tab-view">
          <div className="alt-tab-header">
            <User size={32} className="alt-tab-icon" />
            <h2>Learner Profile</h2>
            <p>Member since October 2024 • Diamond Tier Scholar</p>
          </div>
          <ProgressStats stats={user.stats} badges={user.badges} />
        </div>
      )}

      {/* 8. Bottom Navigation Component */}
      <BottomNav 
        tabs={navigationTabs}
        activeTab={activeTab}
        onTabChange={(tabId) => {
          setActiveTab(tabId);
          window.scrollTo({ top: 0, behavior: 'smooth' });
        }}
      />

      {/* Interactive Action Modal */}
      {modalInfo && (
        <div className="modal-backdrop" onClick={() => setModalInfo(null)}>
          <div className="modal-card" onClick={e => e.stopPropagation()}>
            <div className="modal-badge">{modalInfo.badge}</div>
            <h3 className="modal-title">{modalInfo.title}</h3>
            <h4 className="modal-sub">{modalInfo.subtitle}</h4>
            <p className="modal-desc">{modalInfo.details}</p>
            <div className="modal-actions">
              <button 
                className="cq-button cq-button-primary"
                onClick={() => {
                  alert(`Launching: ${modalInfo.title}`);
                  setModalInfo(null);
                }}
              >
                <Play size={16} fill="currentColor" />
                <span>Launch Workspace</span>
              </button>
              <button 
                className="cq-button cq-button-secondary"
                onClick={() => setModalInfo(null)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      <style>{`
        .home-main-content {
          display: flex;
          flex-direction: column;
        }

        .recommended-section {
          margin-top: 4px;
        }

        .recommended-scroll-track {
          display: flex;
          gap: 14px;
          overflow-x: auto;
          padding: 4px 20px 14px 20px;
        }

        .no-search-results {
          padding: 24px;
          text-align: center;
          width: 100%;
          color: var(--text-secondary);
        }

        /* Alt Tab Views */
        .alt-tab-view {
          padding: 20px 0;
          animation: fadeIn 0.25s ease-out;
        }

        @keyframes fadeIn {
          from { opacity: 0; transform: translateY(6px); }
          to { opacity: 1; transform: translateY(0); }
        }

        .alt-tab-header {
          padding: 10px 20px 20px 20px;
          text-align: center;
        }

        .alt-tab-icon {
          color: var(--primary);
          margin-bottom: 8px;
        }

        .alt-tab-header h2 {
          font-size: 1.3rem;
          font-weight: 800;
          color: var(--text-primary);
        }

        .alt-tab-header p {
          font-size: 0.85rem;
          color: var(--text-secondary);
          margin-top: 4px;
        }

        /* Modal Backdrop */
        .modal-backdrop {
          position: fixed;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.75);
          backdrop-filter: blur(6px);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 100;
          padding: 20px;
        }

        .modal-card {
          background: #121828;
          border: 1px solid var(--border-highlight);
          border-radius: var(--radius-lg);
          padding: 24px;
          max-width: 440px;
          width: 100%;
          box-shadow: 0 20px 50px rgba(0, 0, 0, 0.7);
          animation: popModal 0.2s cubic-bezier(0.16, 1, 0.3, 1);
        }

        @keyframes popModal {
          from { transform: scale(0.95); opacity: 0; }
          to { transform: scale(1); opacity: 1; }
        }

        .modal-badge {
          display: inline-block;
          font-size: 0.72rem;
          font-weight: 800;
          color: #818CF8;
          background: rgba(99, 102, 241, 0.15);
          padding: 3px 10px;
          border-radius: var(--radius-full);
          margin-bottom: 10px;
        }

        .modal-title {
          font-size: 1.25rem;
          font-weight: 800;
          color: var(--text-primary);
          margin-bottom: 4px;
        }

        .modal-sub {
          font-size: 0.92rem;
          font-weight: 600;
          color: #38BDF8;
          margin-bottom: 12px;
        }

        .modal-desc {
          font-size: 0.85rem;
          color: var(--text-secondary);
          line-height: 1.5;
          margin-bottom: 20px;
        }

        .modal-actions {
          display: flex;
          gap: 10px;
        }

        .modal-actions button {
          flex: 1;
        }
      `}</style>
    </div>
  );
}
