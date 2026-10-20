import React from 'react';
import { Home, BookOpen, Code2, Trophy, User } from 'lucide-react';

export default function BottomNav({ tabs, activeTab, onTabChange }) {
  const renderTabIcon = (iconName, isActive) => {
    const props = {
      size: 20,
      className: `nav-tab-icon ${isActive ? 'active' : ''}`
    };

    switch (iconName) {
      case "Home": return <Home {...props} />;
      case "BookOpen": return <BookOpen {...props} />;
      case "Code2": return <Code2 {...props} />;
      case "Trophy": return <Trophy {...props} />;
      case "User": return <User {...props} />;
      default: return <Home {...props} />;
    }
  };

  return (
    <nav className="bottom-nav-root" aria-label="Main Navigation">
      <div className="bottom-nav-bar">
        {tabs.map((tab) => {
          const isActive = tab.id === activeTab;
          return (
            <button
              key={tab.id}
              className={`nav-tab-btn ${isActive ? 'active' : ''}`}
              onClick={() => onTabChange && onTabChange(tab.id)}
              aria-selected={isActive}
            >
              <div className="nav-icon-container">
                {renderTabIcon(tab.icon, isActive)}
                {isActive && <span className="active-glow-pill"></span>}
              </div>
              <span className="nav-tab-label">{tab.label}</span>
            </button>
          );
        })}
      </div>

      <style>{`
        .bottom-nav-root {
          position: fixed;
          bottom: 0;
          left: 0;
          right: 0;
          z-index: 50;
          display: flex;
          justify-content: center;
          padding: 12px 16px;
          pointer-events: none;
        }

        .bottom-nav-bar {
          pointer-events: auto;
          width: 100%;
          max-width: 480px;
          background: rgba(13, 18, 30, 0.88);
          backdrop-filter: blur(20px);
          -webkit-backdrop-filter: blur(20px);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          padding: 6px 10px;
          display: flex;
          align-items: center;
          justify-content: space-around;
          box-shadow: 0 16px 40px rgba(0, 0, 0, 0.6);
        }

        @media (min-width: 1024px) {
          .bottom-nav-bar {
            max-width: 580px;
          }
        }

        .nav-tab-btn {
          background: transparent;
          border: none;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 3px;
          padding: 6px 12px;
          cursor: pointer;
          border-radius: var(--radius-md);
          color: var(--text-muted);
          transition: var(--transition);
          position: relative;
        }

        .nav-tab-btn:hover {
          color: var(--text-primary);
        }

        .nav-tab-btn.active {
          color: #818CF8;
        }

        .nav-icon-container {
          position: relative;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 28px;
          height: 24px;
        }

        .nav-tab-icon {
          transition: transform 0.2s cubic-bezier(0.16, 1, 0.3, 1), color 0.2s;
        }

        .nav-tab-btn.active .nav-tab-icon {
          transform: translateY(-2px) scale(1.1);
          color: #818CF8;
          filter: drop-shadow(0 0 8px rgba(99, 102, 241, 0.6));
        }

        .active-glow-pill {
          position: absolute;
          bottom: -4px;
          width: 12px;
          height: 3px;
          background: #6366F1;
          border-radius: var(--radius-full);
          box-shadow: 0 0 8px #6366F1;
        }

        .nav-tab-label {
          font-size: 0.68rem;
          font-weight: 600;
          letter-spacing: 0.02em;
          transition: color 0.2s;
        }

        .nav-tab-btn.active .nav-tab-label {
          color: var(--text-primary);
          font-weight: 700;
        }
      `}</style>
    </nav>
  );
}
