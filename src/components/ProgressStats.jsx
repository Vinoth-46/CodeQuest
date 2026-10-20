import React from 'react';
import { 
  Award, 
  Flame, 
  Zap, 
  CheckCircle2, 
  Trophy, 
  TrendingUp, 
  BookOpen, 
  Target 
} from 'lucide-react';

export default function ProgressStats({ stats, badges }) {
  const xpPercentage = Math.min(
    100,
    Math.round((stats.xp / stats.nextLevelXp) * 100)
  );

  const renderBadgeIcon = (iconName) => {
    switch (iconName) {
      case "Flame": return <Flame size={18} />;
      case "CheckCircle2": return <CheckCircle2 size={18} />;
      case "Zap": return <Zap size={18} />;
      case "Trophy": return <Trophy size={18} />;
      default: return <Award size={18} />;
    }
  };

  const getTierBadgeStyle = (tier, unlocked) => {
    if (!unlocked) return { border: '1px dashed #334155', color: '#64748B', bg: 'rgba(255, 255, 255, 0.02)' };
    switch (tier.toLowerCase()) {
      case 'gold':
        return { border: '1px solid rgba(245, 158, 11, 0.4)', color: '#FBBF24', bg: 'rgba(245, 158, 11, 0.12)' };
      case 'silver':
        return { border: '1px solid rgba(148, 163, 184, 0.4)', color: '#CBD5E1', bg: 'rgba(148, 163, 184, 0.12)' };
      case 'bronze':
        return { border: '1px solid rgba(217, 119, 6, 0.4)', color: '#F59E0B', bg: 'rgba(217, 119, 6, 0.12)' };
      case 'diamond':
        return { border: '1px solid rgba(6, 182, 212, 0.4)', color: '#22D3EE', bg: 'rgba(6, 182, 212, 0.12)' };
      default:
        return { border: '1px solid rgba(99, 102, 241, 0.4)', color: '#818CF8', bg: 'rgba(99, 102, 241, 0.12)' };
    }
  };

  return (
    <div className="progress-stats-section">
      <div className="section-header-wrap">
        <h2 className="section-title">
          <TrendingUp size={18} className="stats-icon-accent" />
          Progress Snapshot
        </h2>
        <span className="global-rank-pill">{stats.globalRank} in League</span>
      </div>

      <div className="cq-card stats-overview-card">
        {/* Level and Title Header */}
        <div className="stats-level-row">
          <div>
            <span className="level-badge">Level {stats.level}</span>
            <h3 className="rank-title">{stats.rankTitle}</h3>
          </div>
          <div className="xp-metric-box">
            <span className="xp-number">{stats.xp.toLocaleString()}</span>
            <span className="xp-label">/ {stats.nextLevelXp.toLocaleString()} XP</span>
          </div>
        </div>

        {/* Level Progress Bar */}
        <div className="xp-bar-wrap">
          <div className="xp-track">
            <div className="xp-fill" style={{ width: `${xpPercentage}%` }}></div>
          </div>
          <div className="xp-meta-text">
            <span>{stats.nextLevelXp - stats.xp} XP to Level {stats.level + 1}</span>
            <span>{xpPercentage}%</span>
          </div>
        </div>

        {/* 2-Column Grid: Lessons & Accuracy */}
        <div className="stats-metrics-grid">
          <div className="stat-metric-cell">
            <div className="metric-icon-wrap metric-icon-lessons">
              <BookOpen size={16} />
            </div>
            <div>
              <div className="metric-value">{stats.lessonsCompleted}</div>
              <div className="metric-name">Lessons Completed</div>
            </div>
          </div>

          <div className="stat-metric-cell">
            <div className="metric-icon-wrap metric-icon-acc">
              <Target size={16} />
            </div>
            <div>
              <div className="metric-value">{stats.accuracy}</div>
              <div className="metric-name">Quiz Accuracy</div>
            </div>
          </div>
        </div>

        {/* Badges Earned Subsection */}
        <div className="badges-subsection">
          <div className="badges-header">
            <span className="badges-title">Earned Badges</span>
            <span className="badges-count">
              {badges.filter(b => b.unlocked).length} / {badges.length}
            </span>
          </div>

          <div className="badges-grid">
            {badges.map((badge) => {
              const style = getTierBadgeStyle(badge.tier, badge.unlocked);
              return (
                <div 
                  key={badge.id}
                  className={`badge-item-card ${badge.unlocked ? 'unlocked' : 'locked'}`}
                  style={{
                    background: style.bg,
                    borderColor: style.border.split(' ')[2]
                  }}
                  title={`${badge.name} (${badge.tier}): ${badge.desc}`}
                >
                  <div className="badge-icon-box" style={{ color: style.color }}>
                    {renderBadgeIcon(badge.icon)}
                  </div>
                  <span className="badge-name">{badge.name}</span>
                  <span className="badge-tier-tag" style={{ color: style.color }}>
                    {badge.unlocked ? badge.tier : "Locked"}
                  </span>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      <style>{`
        .progress-stats-section {
          padding: 16px 20px 24px 20px;
        }

        .section-header-wrap {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .stats-icon-accent {
          color: #10B981;
        }

        .global-rank-pill {
          font-size: 0.75rem;
          font-weight: 700;
          color: #34D399;
          background: rgba(16, 185, 129, 0.12);
          border: 1px solid rgba(16, 185, 129, 0.3);
          padding: 3px 10px;
          border-radius: var(--radius-full);
          font-family: var(--font-mono);
        }

        .stats-overview-card {
          padding: 20px;
          background: linear-gradient(145deg, #121828 0%, #0D1321 100%);
          border: 1px solid var(--border-color);
        }

        .stats-level-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .level-badge {
          display: inline-block;
          font-size: 0.7rem;
          font-weight: 800;
          text-transform: uppercase;
          color: #818CF8;
          background: rgba(99, 102, 241, 0.15);
          padding: 2px 8px;
          border-radius: var(--radius-sm);
          margin-bottom: 4px;
        }

        .rank-title {
          font-size: 1.15rem;
          font-weight: 700;
          color: var(--text-primary);
        }

        .xp-metric-box {
          text-align: right;
        }

        .xp-number {
          font-size: 1.15rem;
          font-weight: 800;
          color: #F8FAFC;
          font-family: var(--font-mono);
        }

        .xp-label {
          font-size: 0.78rem;
          color: var(--text-muted);
          font-family: var(--font-mono);
          margin-left: 4px;
        }

        .xp-bar-wrap {
          margin-bottom: 18px;
        }

        .xp-track {
          width: 100%;
          height: 8px;
          background: rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          overflow: hidden;
        }

        .xp-fill {
          height: 100%;
          background: linear-gradient(90deg, #10B981 0%, #3B82F6 50%, #6366F1 100%);
          border-radius: var(--radius-full);
          transition: width 0.6s ease;
        }

        .xp-meta-text {
          display: flex;
          justify-content: space-between;
          font-size: 0.72rem;
          color: var(--text-muted);
          margin-top: 6px;
          font-family: var(--font-mono);
        }

        .stats-metrics-grid {
          display: grid;
          grid-template-columns: 1fr 1fr;
          gap: 12px;
          margin-bottom: 20px;
        }

        .stat-metric-cell {
          display: flex;
          align-items: center;
          gap: 12px;
          background: rgba(0, 0, 0, 0.25);
          border: 1px solid rgba(255, 255, 255, 0.04);
          border-radius: var(--radius-md);
          padding: 12px 14px;
        }

        .metric-icon-wrap {
          width: 36px;
          height: 36px;
          border-radius: var(--radius-sm);
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .metric-icon-lessons {
          background: rgba(99, 102, 241, 0.15);
          color: #818CF8;
        }

        .metric-icon-acc {
          background: rgba(16, 185, 129, 0.15);
          color: #34D399;
        }

        .metric-value {
          font-size: 1.05rem;
          font-weight: 800;
          color: var(--text-primary);
          line-height: 1.2;
          font-family: var(--font-mono);
        }

        .metric-name {
          font-size: 0.72rem;
          color: var(--text-muted);
        }

        /* Badges */
        .badges-subsection {
          border-top: 1px solid rgba(255, 255, 255, 0.06);
          padding-top: 14px;
        }

        .badges-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
        }

        .badges-title {
          font-size: 0.82rem;
          font-weight: 700;
          text-transform: uppercase;
          letter-spacing: 0.04em;
          color: var(--text-secondary);
        }

        .badges-count {
          font-size: 0.75rem;
          color: var(--text-muted);
          font-family: var(--font-mono);
        }

        .badges-grid {
          display: grid;
          grid-template-columns: repeat(4, 1fr);
          gap: 8px;
        }

        .badge-item-card {
          border: 1px solid;
          border-radius: var(--radius-md);
          padding: 10px 6px;
          display: flex;
          flex-direction: column;
          align-items: center;
          text-align: center;
          gap: 4px;
          transition: var(--transition);
        }

        .badge-item-card:hover {
          transform: translateY(-2px);
        }

        .badge-item-card.locked {
          opacity: 0.45;
          filter: grayscale(1);
        }

        .badge-icon-box {
          margin-bottom: 2px;
        }

        .badge-name {
          font-size: 0.7rem;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1.2;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .badge-tier-tag {
          font-size: 0.62rem;
          font-weight: 700;
          text-transform: uppercase;
        }
      `}</style>
    </div>
  );
}
