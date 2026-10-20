import React, { useState } from 'react';
import { Bell, Flame, CheckCircle, X } from 'lucide-react';

export default function Header({ user }) {
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState(user.notifications || []);
  const unreadCount = notifications.filter(n => n.unread).length;

  const markAllRead = () => {
    setNotifications(notifications.map(n => ({ ...n, unread: false })));
  };

  return (
    <header className="header-root">
      <div className="header-content">
        <div className="user-greeting-wrap">
          <div className="avatar-wrapper">
            <img 
              src={user.avatar} 
              alt={user.name} 
              className="user-avatar"
            />
            <span className="online-indicator"></span>
          </div>
          <div>
            <h1 className="greeting-text">Hi, {user.name} 👋</h1>
            <p className="greeting-sub">Ready to conquer your next quest?</p>
          </div>
        </div>

        <div className="header-actions">
          {/* Streak Badge */}
          <div className="streak-badge" title="Daily coding streak">
            <Flame className="streak-flame-icon" size={18} />
            <span className="streak-text">{user.streak}-day streak</span>
          </div>

          {/* Notification Bell */}
          <div className="notification-bell-container">
            <button 
              className={`bell-button ${showNotifications ? 'active' : ''}`}
              onClick={() => setShowNotifications(!showNotifications)}
              aria-label="View notifications"
            >
              <Bell size={20} className="bell-icon" />
              {unreadCount > 0 && (
                <span className="unread-dot">{unreadCount}</span>
              )}
            </button>

            {/* Notification Dropdown Modal */}
            {showNotifications && (
              <div className="notifications-dropdown">
                <div className="notifications-header">
                  <div>
                    <h3 className="notifications-title">Notifications</h3>
                    <span className="unread-label">{unreadCount} new alerts</span>
                  </div>
                  <div className="notifications-actions">
                    {unreadCount > 0 && (
                      <button className="mark-read-btn" onClick={markAllRead}>
                        Mark read
                      </button>
                    )}
                    <button 
                      className="close-dropdown-btn" 
                      onClick={() => setShowNotifications(false)}
                    >
                      <X size={16} />
                    </button>
                  </div>
                </div>

                <div className="notifications-list">
                  {notifications.map((item) => (
                    <div 
                      key={item.id} 
                      className={`notification-item ${item.unread ? 'unread' : ''}`}
                    >
                      <div className="notification-bullet"></div>
                      <div className="notification-info">
                        <div className="notification-item-title">{item.title}</div>
                        <div className="notification-item-desc">{item.desc}</div>
                        <div className="notification-item-time">{item.time}</div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        </div>
      </div>

      <style>{`
        .header-root {
          position: sticky;
          top: 0;
          z-index: 40;
          background: rgba(9, 13, 22, 0.85);
          backdrop-filter: blur(16px);
          -webkit-backdrop-filter: blur(16px);
          border-bottom: 1px solid rgba(255, 255, 255, 0.05);
          padding: 18px 20px 14px 20px;
        }

        .header-content {
          display: flex;
          align-items: center;
          justify-content: space-between;
          gap: 12px;
        }

        .user-greeting-wrap {
          display: flex;
          align-items: center;
          gap: 12px;
        }

        .avatar-wrapper {
          position: relative;
        }

        .user-avatar {
          width: 44px;
          height: 44px;
          border-radius: var(--radius-full);
          object-fit: cover;
          border: 2px solid var(--border-highlight);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
        }

        .online-indicator {
          position: absolute;
          bottom: 1px;
          right: 1px;
          width: 10px;
          height: 10px;
          background: var(--emerald);
          border: 2px solid var(--bg-dark);
          border-radius: 50%;
        }

        .greeting-text {
          font-size: 1.15rem;
          font-weight: 700;
          letter-spacing: -0.02em;
          color: var(--text-primary);
          line-height: 1.25;
        }

        .greeting-sub {
          font-size: 0.78rem;
          color: var(--text-secondary);
        }

        .header-actions {
          display: flex;
          align-items: center;
          gap: 10px;
        }

        .streak-badge {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          background: rgba(245, 158, 11, 0.12);
          border: 1px solid rgba(245, 158, 11, 0.35);
          color: #FBBF24;
          padding: 6px 12px;
          border-radius: var(--radius-full);
          font-weight: 700;
          font-size: 0.82rem;
          box-shadow: 0 2px 10px rgba(245, 158, 11, 0.15);
          transition: var(--transition);
        }

        .streak-badge:hover {
          background: rgba(245, 158, 11, 0.2);
          transform: translateY(-1px);
        }

        .streak-flame-icon {
          color: #F59E0B;
          filter: drop-shadow(0 0 6px rgba(245, 158, 11, 0.6));
          animation: flamePulse 2s infinite ease-in-out;
        }

        @keyframes flamePulse {
          0%, 100% { transform: scale(1); }
          50% { transform: scale(1.15); }
        }

        .streak-text {
          white-space: nowrap;
        }

        .notification-bell-container {
          position: relative;
        }

        .bell-button {
          width: 40px;
          height: 40px;
          border-radius: var(--radius-md);
          background: var(--bg-card);
          border: 1px solid var(--border-color);
          color: var(--text-secondary);
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          position: relative;
          transition: var(--transition);
        }

        .bell-button:hover,
        .bell-button.active {
          color: var(--text-primary);
          border-color: var(--border-highlight);
          background: var(--bg-card-subtle);
        }

        .unread-dot {
          position: absolute;
          top: -3px;
          right: -3px;
          min-width: 18px;
          height: 18px;
          padding: 0 4px;
          background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
          color: #FFFFFF;
          border: 2px solid var(--bg-dark);
          border-radius: var(--radius-full);
          font-size: 0.68rem;
          font-weight: 800;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .notifications-dropdown {
          position: absolute;
          right: 0;
          top: calc(100% + 10px);
          width: 320px;
          background: #111726;
          border: 1px solid var(--border-highlight);
          border-radius: var(--radius-lg);
          box-shadow: 0 16px 40px rgba(0, 0, 0, 0.7);
          overflow: hidden;
          animation: dropIn 0.2s cubic-bezier(0.16, 1, 0.3, 1);
          z-index: 50;
        }

        @keyframes dropIn {
          from { opacity: 0; transform: translateY(-8px) scale(0.96); }
          to { opacity: 1; transform: translateY(0) scale(1); }
        }

        .notifications-header {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 12px 16px;
          border-bottom: 1px solid var(--border-color);
          background: rgba(0, 0, 0, 0.2);
        }

        .notifications-title {
          font-size: 0.95rem;
          font-weight: 700;
          color: var(--text-primary);
        }

        .unread-label {
          font-size: 0.75rem;
          color: var(--text-muted);
        }

        .notifications-actions {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .mark-read-btn {
          background: transparent;
          border: none;
          color: var(--primary);
          font-size: 0.78rem;
          font-weight: 600;
          cursor: pointer;
        }

        .close-dropdown-btn {
          background: transparent;
          border: none;
          color: var(--text-muted);
          cursor: pointer;
          display: flex;
          padding: 2px;
        }

        .close-dropdown-btn:hover {
          color: var(--text-primary);
        }

        .notifications-list {
          max-height: 280px;
          overflow-y: auto;
        }

        .notification-item {
          display: flex;
          gap: 12px;
          padding: 12px 16px;
          border-bottom: 1px solid rgba(255, 255, 255, 0.03);
          transition: background 0.15s;
        }

        .notification-item:hover {
          background: rgba(255, 255, 255, 0.03);
        }

        .notification-item.unread {
          background: rgba(99, 102, 241, 0.06);
        }

        .notification-bullet {
          width: 8px;
          height: 8px;
          border-radius: 50%;
          background: var(--primary);
          margin-top: 6px;
          flex-shrink: 0;
        }

        .notification-item:not(.unread) .notification-bullet {
          background: var(--text-muted);
          opacity: 0.3;
        }

        .notification-item-title {
          font-size: 0.85rem;
          font-weight: 600;
          color: var(--text-primary);
        }

        .notification-item-desc {
          font-size: 0.78rem;
          color: var(--text-secondary);
          margin-top: 2px;
        }

        .notification-item-time {
          font-size: 0.7rem;
          color: var(--text-muted);
          margin-top: 4px;
        }
      `}</style>
    </header>
  );
}
