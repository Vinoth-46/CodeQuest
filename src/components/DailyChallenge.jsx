import React from 'react';
import { Zap, Play, CheckCircle2, Award, Clock } from 'lucide-react';

export default function DailyChallenge({ challenge, onStartChallenge }) {
  // SVG Progress Ring calculations
  const radius = 32;
  const strokeWidth = 6;
  const normalizedRadius = radius - strokeWidth / 2;
  const circumference = normalizedRadius * 2 * Math.PI;
  const strokeDashoffset = circumference - (challenge.progressPercentage / 100) * circumference;

  return (
    <div className="daily-challenge-section">
      <div className="section-header-wrap">
        <h2 className="section-title">
          <Zap size={18} className="zap-accent-icon" />
          Daily Coding Quest
        </h2>
        <span className="reset-timer">
          <Clock size={13} />
          {challenge.timeLeftHours}h left today
        </span>
      </div>

      <div className="cq-card daily-card">
        <div className="daily-card-top">
          <div className="daily-meta-left">
            <div className="daily-tags-wrap">
              <span className="badge badge-amber">{challenge.category}</span>
              <span className="badge badge-indigo">{challenge.difficulty}</span>
            </div>
            <h3 className="daily-task-title">{challenge.title}</h3>
            <p className="daily-task-sub">{challenge.subtitle}</p>
          </div>

          {/* Circular Progress Ring */}
          <div className="progress-ring-container" title={`${challenge.progressPercentage}% completed`}>
            <svg height={radius * 2} width={radius * 2} className="progress-ring-svg">
              <circle
                stroke="rgba(255, 255, 255, 0.08)"
                fill="transparent"
                strokeWidth={strokeWidth}
                r={normalizedRadius}
                cx={radius}
                cy={radius}
              />
              <circle
                stroke="url(#ringGradient)"
                fill="transparent"
                strokeWidth={strokeWidth}
                strokeDasharray={`${circumference} ${circumference}`}
                style={{ strokeDashoffset }}
                strokeLinecap="round"
                r={normalizedRadius}
                cx={radius}
                cy={radius}
                className="progress-ring-circle"
              />
              <defs>
                <linearGradient id="ringGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#F59E0B" />
                  <stop offset="100%" stopColor="#EC4899" />
                </linearGradient>
              </defs>
            </svg>
            <div className="progress-ring-text">
              <span className="ring-percent">{challenge.progressPercentage}%</span>
            </div>
          </div>
        </div>

        {/* Step info & Rewards bar */}
        <div className="daily-mid-row">
          <div className="daily-step-text">
            <CheckCircle2 size={14} className="step-check" />
            <span>{challenge.currentStepText}</span>
          </div>
          <div className="daily-rewards">
            <span className="reward-item xp-reward">+{challenge.xpReward} XP</span>
            <span className="reward-item gem-reward">💎 +{challenge.gemReward}</span>
          </div>
        </div>

        {/* Start / Continue Button */}
        <button 
          className="cq-button cq-button-primary daily-start-btn"
          onClick={onStartChallenge}
        >
          <Play size={16} fill="currentColor" />
          <span>Start Challenge</span>
        </button>
      </div>

      <style>{`
        .daily-challenge-section {
          padding: 10px 20px;
        }

        .section-header-wrap {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .zap-accent-icon {
          color: #F59E0B;
        }

        .reset-timer {
          display: inline-flex;
          align-items: center;
          gap: 5px;
          font-size: 0.75rem;
          color: var(--text-muted);
          font-family: var(--font-mono);
        }

        .daily-card {
          padding: 18px 20px;
          background: linear-gradient(135deg, #131A29 0%, #0F1420 100%);
          border: 1px solid rgba(245, 158, 11, 0.2);
        }

        .daily-card-top {
          display: flex;
          align-items: flex-start;
          justify-content: space-between;
          gap: 16px;
        }

        .daily-meta-left {
          flex: 1;
        }

        .daily-tags-wrap {
          display: flex;
          gap: 6px;
          margin-bottom: 8px;
        }

        .daily-task-title {
          font-size: 1.05rem;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1.3;
          margin-bottom: 4px;
        }

        .daily-task-sub {
          font-size: 0.8rem;
          color: var(--text-secondary);
          line-height: 1.4;
        }

        /* Progress ring */
        .progress-ring-container {
          position: relative;
          width: 64px;
          height: 64px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .progress-ring-svg {
          transform: rotate(-90deg);
        }

        .progress-ring-circle {
          transition: stroke-dashoffset 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }

        .progress-ring-text {
          position: absolute;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .ring-percent {
          font-size: 0.75rem;
          font-weight: 800;
          color: #FBBF24;
          font-family: var(--font-mono);
        }

        /* Mid row */
        .daily-mid-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding: 10px 12px;
          background: rgba(0, 0, 0, 0.25);
          border-radius: var(--radius-sm);
          margin-top: 14px;
          margin-bottom: 14px;
        }

        .daily-step-text {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 0.78rem;
          color: #34D399;
          font-weight: 600;
        }

        .step-check {
          color: #10B981;
        }

        .daily-rewards {
          display: flex;
          align-items: center;
          gap: 8px;
        }

        .reward-item {
          font-size: 0.75rem;
          font-weight: 700;
          padding: 2px 8px;
          border-radius: var(--radius-full);
          font-family: var(--font-mono);
        }

        .xp-reward {
          background: rgba(99, 102, 241, 0.15);
          color: #818CF8;
          border: 1px solid rgba(99, 102, 241, 0.3);
        }

        .gem-reward {
          background: rgba(236, 72, 153, 0.15);
          color: #F472B6;
          border: 1px solid rgba(236, 72, 153, 0.3);
        }

        .daily-start-btn {
          width: 100%;
          background: linear-gradient(135deg, #F59E0B 0%, #D97706 100%);
          box-shadow: 0 4px 18px rgba(245, 158, 11, 0.35);
          color: #000;
          font-weight: 700;
        }

        .daily-start-btn:hover {
          box-shadow: 0 6px 24px rgba(245, 158, 11, 0.5);
          transform: translateY(-2px);
        }
      `}</style>
    </div>
  );
}
