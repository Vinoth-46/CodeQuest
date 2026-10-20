import React from 'react';
import { Clock, Star, Play, Sparkles, Database, FileCode, Bot } from 'lucide-react';

export default function CourseCard({ course, onSelectCourse }) {
  const renderCourseIcon = (iconName) => {
    switch (iconName) {
      case "Sparkles": return <Sparkles size={18} />;
      case "Database": return <Database size={18} />;
      case "FileCode": return <FileCode size={18} />;
      case "Bot": return <Bot size={18} />;
      default: return <FileCode size={18} />;
    }
  };

  return (
    <div 
      className="course-card-root cq-card"
      onClick={() => onSelectCourse && onSelectCourse(course)}
    >
      <div className="card-top-row">
        <span 
          className="course-level-tag"
          style={{ 
            color: course.levelColor || '#3B82F6',
            background: `${course.levelColor || '#3B82F6'}18`,
            borderColor: `${course.levelColor || '#3B82F6'}35`
          }}
        >
          {course.level}
        </span>
        <div className="course-icon-badge" style={{ color: course.levelColor || '#3B82F6' }}>
          {renderCourseIcon(course.icon)}
        </div>
      </div>

      <div className="course-body">
        <span className="course-category-tag">{course.tag}</span>
        <h3 className="course-title">{course.title}</h3>

        <div className="course-stats-row">
          <span className="course-duration">
            <Clock size={13} />
            {course.duration}
          </span>
          <span className="course-rating">
            <Star size={13} fill="#F59E0B" color="#F59E0B" />
            {course.rating}
          </span>
          <span className="course-students">
            {course.enrolledCount} learners
          </span>
        </div>

        {/* Progress row */}
        <div className="course-progress-block">
          <div className="progress-label-wrap">
            <span className="prog-text">
              {course.progress > 0 ? `${course.progress}% completed` : 'Not started'}
            </span>
          </div>
          <div className="progress-track-mini">
            <div 
              className="progress-fill-mini"
              style={{ 
                width: `${course.progress}%`,
                background: course.levelColor || '#6366F1'
              }}
            />
          </div>
        </div>
      </div>

      <div className="card-hover-action">
        <span>{course.progress > 0 ? "Continue" : "Start Course"}</span>
        <Play size={12} fill="currentColor" />
      </div>

      <style>{`
        .course-card-root {
          min-width: 240px;
          max-width: 260px;
          padding: 16px;
          background: linear-gradient(145deg, #121828 0%, #0E131F 100%);
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          cursor: pointer;
          flex-shrink: 0;
          transition: var(--transition);
        }

        .course-card-root:hover {
          transform: translateY(-4px);
          border-color: var(--border-highlight);
          box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.4);
        }

        .card-top-row {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .course-level-tag {
          font-size: 0.68rem;
          font-weight: 800;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          padding: 3px 8px;
          border-radius: var(--radius-sm);
          border: 1px solid;
        }

        .course-icon-badge {
          width: 32px;
          height: 32px;
          border-radius: var(--radius-sm);
          background: rgba(255, 255, 255, 0.04);
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .course-category-tag {
          font-size: 0.72rem;
          font-weight: 600;
          color: var(--text-muted);
          text-transform: uppercase;
          letter-spacing: 0.04em;
          display: block;
          margin-bottom: 4px;
        }

        .course-title {
          font-size: 0.95rem;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1.35;
          margin-bottom: 10px;
          min-height: 40px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .course-stats-row {
          display: flex;
          align-items: center;
          gap: 10px;
          font-size: 0.75rem;
          color: var(--text-secondary);
          margin-bottom: 14px;
        }

        .course-duration,
        .course-rating {
          display: inline-flex;
          align-items: center;
          gap: 4px;
        }

        .course-students {
          color: var(--text-muted);
        }

        .course-progress-block {
          margin-bottom: 10px;
        }

        .progress-label-wrap {
          display: flex;
          justify-content: space-between;
          font-size: 0.72rem;
          color: var(--text-muted);
          margin-bottom: 6px;
        }

        .progress-track-mini {
          width: 100%;
          height: 5px;
          background: rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          overflow: hidden;
        }

        .progress-fill-mini {
          height: 100%;
          border-radius: var(--radius-full);
          transition: width 0.4s ease;
        }

        .card-hover-action {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 10px;
          border-top: 1px solid rgba(255, 255, 255, 0.05);
          font-size: 0.78rem;
          font-weight: 700;
          color: var(--primary);
        }
      `}</style>
    </div>
  );
}
