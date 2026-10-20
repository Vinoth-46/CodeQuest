import React from 'react';
import { Play, Sparkles, BookOpen, Clock, ArrowRight, RotateCcw } from 'lucide-react';

export default function ContinueCard({ continueData, onToggleState, onResume, onStart }) {
  const { hasStarted, course, starterCard } = continueData;

  return (
    <div className="continue-section">
      <div className="continue-header-row">
        <span className="continue-label">
          {hasStarted ? "Pick Up Where You Left Off" : "Begin Your Quest"}
        </span>
        {/* State Toggle for demo/testing convenience */}
        <button 
          className="toggle-state-btn"
          onClick={onToggleState}
          title="Toggle between in-progress and fresh 'Start here' state"
        >
          <RotateCcw size={12} />
          {hasStarted ? "Simulate New User" : "Simulate Active Learner"}
        </button>
      </div>

      {hasStarted && course ? (
        <div className="cq-card continue-card glow-primary">
          <div className="continue-card-badge-row">
            <span className="badge badge-indigo">
              <BookOpen size={12} />
              {course.path} Track
            </span>
            <span className="eta-badge">
              <Clock size={12} />
              ~{course.estimatedMinutesLeft} mins left
            </span>
          </div>

          <h2 className="continue-course-title">{course.title}</h2>
          <p className="continue-lesson-title">{course.currentLesson}</p>

          {/* Progress bar */}
          <div className="progress-bar-container">
            <div className="progress-meta">
              <span className="progress-label">Course Progress</span>
              <span className="progress-percentage">{course.progress}%</span>
            </div>
            <div className="progress-track">
              <div 
                className="progress-fill"
                style={{ width: `${course.progress}%` }}
              ></div>
            </div>
            <div className="lessons-tally">
              {course.completedLessons} of {course.totalLessons} lessons completed
            </div>
          </div>

          {/* Big Resume Button */}
          <button 
            className="cq-button cq-button-primary big-resume-button"
            onClick={onResume}
          >
            <Play size={18} fill="currentColor" />
            <span>Resume Lesson</span>
          </button>
        </div>
      ) : (
        <div className="cq-card start-here-card">
          <div className="start-icon-wrap">
            <Sparkles size={26} className="sparkle-anim" />
          </div>
          <div className="start-card-content">
            <span className="badge badge-emerald">Ready to Begin</span>
            <h2 className="start-title">{starterCard.title}</h2>
            <p className="start-desc">{starterCard.subtitle}</p>
            <p className="start-recommendation">{starterCard.recommendation}</p>

            <button 
              className="cq-button cq-button-primary start-button"
              onClick={onStart}
            >
              <span>{starterCard.actionText}</span>
              <ArrowRight size={18} />
            </button>
          </div>
        </div>
      )}

      <style>{`
        .continue-section {
          padding: 6px 20px 10px 20px;
        }

        .continue-header-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 10px;
        }

        .continue-label {
          font-size: 0.8rem;
          font-weight: 700;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          color: var(--text-secondary);
        }

        .toggle-state-btn {
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid var(--border-color);
          color: var(--text-muted);
          font-size: 0.72rem;
          padding: 4px 8px;
          border-radius: var(--radius-sm);
          cursor: pointer;
          display: inline-flex;
          align-items: center;
          gap: 5px;
          transition: var(--transition);
        }

        .toggle-state-btn:hover {
          color: var(--text-primary);
          background: rgba(255, 255, 255, 0.1);
        }

        .continue-card {
          padding: 20px;
          background: linear-gradient(145deg, #121828 0%, #0D121F 100%);
          border: 1px solid rgba(99, 102, 241, 0.25);
          position: relative;
        }

        .continue-card::after {
          content: "";
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: linear-gradient(90deg, #6366F1, #8B5CF6, #EC4899);
        }

        .continue-card-badge-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .eta-badge {
          display: inline-flex;
          align-items: center;
          gap: 5px;
          font-size: 0.75rem;
          color: var(--text-muted);
        }

        .continue-course-title {
          font-size: 1.15rem;
          font-weight: 700;
          color: var(--text-primary);
          margin-bottom: 4px;
          line-height: 1.3;
        }

        .continue-lesson-title {
          font-size: 0.88rem;
          color: #A5B4FC;
          margin-bottom: 16px;
          display: flex;
          align-items: center;
          gap: 6px;
        }

        .progress-bar-container {
          background: rgba(0, 0, 0, 0.25);
          border: 1px solid rgba(255, 255, 255, 0.05);
          border-radius: var(--radius-md);
          padding: 12px 14px;
          margin-bottom: 18px;
        }

        .progress-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;
        }

        .progress-label {
          font-size: 0.78rem;
          font-weight: 600;
          color: var(--text-secondary);
        }

        .progress-percentage {
          font-size: 0.85rem;
          font-weight: 700;
          color: #818CF8;
          font-family: var(--font-mono);
        }

        .progress-track {
          width: 100%;
          height: 8px;
          background: rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          overflow: hidden;
        }

        .progress-fill {
          height: 100%;
          background: linear-gradient(90deg, #6366F1 0%, #8B5CF6 100%);
          border-radius: var(--radius-full);
          transition: width 0.6s cubic-bezier(0.16, 1, 0.3, 1);
          box-shadow: 0 0 10px rgba(99, 102, 241, 0.6);
        }

        .lessons-tally {
          margin-top: 8px;
          font-size: 0.72rem;
          color: var(--text-muted);
          text-align: right;
        }

        .big-resume-button {
          width: 100%;
          padding: 14px;
          font-size: 1rem;
          letter-spacing: -0.01em;
          border-radius: var(--radius-md);
        }

        /* Start Here Card */
        .start-here-card {
          padding: 24px 20px;
          background: linear-gradient(145deg, #0E1B1D 0%, #0A1317 100%);
          border: 1px solid rgba(16, 185, 129, 0.3);
          display: flex;
          gap: 16px;
          align-items: flex-start;
        }

        .start-icon-wrap {
          width: 48px;
          height: 48px;
          border-radius: var(--radius-md);
          background: rgba(16, 185, 129, 0.15);
          border: 1px solid rgba(16, 185, 129, 0.3);
          color: #34D399;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
        }

        .sparkle-anim {
          animation: spinPulse 4s infinite linear;
        }

        @keyframes spinPulse {
          0% { transform: rotate(0deg) scale(1); }
          50% { transform: rotate(180deg) scale(1.1); }
          100% { transform: rotate(360deg) scale(1); }
        }

        .start-card-content {
          flex: 1;
        }

        .start-title {
          font-size: 1.15rem;
          font-weight: 700;
          color: var(--text-primary);
          margin-top: 8px;
          margin-bottom: 4px;
        }

        .start-desc {
          font-size: 0.85rem;
          color: var(--text-secondary);
          margin-bottom: 6px;
        }

        .start-recommendation {
          font-size: 0.78rem;
          color: var(--text-muted);
          margin-bottom: 16px;
          line-height: 1.4;
        }

        .start-button {
          width: 100%;
          background: linear-gradient(135deg, #10B981 0%, #059669 100%);
          box-shadow: 0 4px 18px rgba(16, 185, 129, 0.4);
        }

        .start-button:hover {
          box-shadow: 0 6px 24px rgba(16, 185, 129, 0.55);
        }
      `}</style>
    </div>
  );
}
