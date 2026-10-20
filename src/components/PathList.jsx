import React, { useState } from 'react';
import { 
  Globe, 
  FileCode2, 
  Network, 
  BrainCircuit, 
  Lock, 
  Unlock, 
  CheckCircle2, 
  ChevronRight, 
  Layers 
} from 'lucide-react';

export default function PathList({ paths, onSelectMilestone }) {
  const [activePathId, setActivePathId] = useState(paths[0]?.id || "web-dev");

  const activePath = paths.find(p => p.id === activePathId) || paths[0];

  // Helper to map string icon names
  const renderPathIcon = (iconName, size = 18) => {
    switch (iconName) {
      case "Globe": return <Globe size={size} />;
      case "FileCode2": return <FileCode2 size={size} />;
      case "Network": return <Network size={size} />;
      case "BrainCircuit": return <BrainCircuit size={size} />;
      default: return <Layers size={size} />;
    }
  };

  const getTierColor = (tier) => {
    switch (tier.toLowerCase()) {
      case 'beginner': return '#10B981';
      case 'intermediate': return '#3B82F6';
      case 'advanced': return '#8B5CF6';
      case 'expert': return '#EC4899';
      default: return '#6366F1';
    }
  };

  return (
    <div className="pathlist-section">
      <div className="section-header-wrap">
        <h2 className="section-title">
          <Layers size={18} className="path-icon-accent" />
          Learning Paths Roadmap
        </h2>
        <span className="roadmap-sub-badge">4 Paths Available</span>
      </div>

      {/* Path Selector Tabs */}
      <div className="path-tabs-row hide-scrollbar">
        {paths.map((p) => {
          const isActive = p.id === activePathId;
          return (
            <button
              key={p.id}
              className={`path-tab-pill ${isActive ? 'active' : ''}`}
              onClick={() => setActivePathId(p.id)}
              style={{
                borderColor: isActive ? p.accentColor : 'transparent'
              }}
            >
              <span className="path-tab-icon" style={{ color: isActive ? '#FFF' : p.accentColor }}>
                {renderPathIcon(p.icon, 16)}
              </span>
              <span className="path-tab-name">{p.name}</span>
            </button>
          );
        })}
      </div>

      {/* Active Path Description */}
      <div className="active-path-desc">
        {activePath.description}
      </div>

      {/* Horizontal Roadmap: Beginner -> Intermediate -> Advanced -> Expert */}
      <div className="roadmap-scroll-wrap hide-scrollbar">
        <div className="roadmap-track">
          {activePath.roadmap.map((milestone, index) => {
            const isCompleted = milestone.status === 'completed';
            const isUnlocked = milestone.status === 'unlocked';
            const isLocked = milestone.status === 'locked';
            const tierColor = getTierColor(milestone.tier);

            return (
              <React.Fragment key={milestone.id}>
                <div 
                  className={`roadmap-node-card ${milestone.status}`}
                  onClick={() => !isLocked && onSelectMilestone && onSelectMilestone(milestone, activePath)}
                >
                  <div className="node-top-bar">
                    <span 
                      className="node-tier-tag"
                      style={{ 
                        color: tierColor, 
                        background: `${tierColor}20`,
                        borderColor: `${tierColor}40`
                      }}
                    >
                      {milestone.tier}
                    </span>
                    <span className="node-status-icon">
                      {isCompleted && <CheckCircle2 size={16} className="status-icon-completed" />}
                      {isUnlocked && <Unlock size={16} className="status-icon-unlocked" />}
                      {isLocked && <Lock size={16} className="status-icon-locked" />}
                    </span>
                  </div>

                  <h4 className="node-title">{milestone.title}</h4>
                  <p className="node-desc">{milestone.description}</p>

                  <div className="node-footer">
                    <span className="node-lessons-count">{milestone.lessonsCount} lessons</span>
                    <span className={`node-action-label ${milestone.status}`}>
                      {isCompleted ? "Completed" : isUnlocked ? "In Progress" : "Locked"}
                    </span>
                  </div>
                </div>

                {/* Arrow connector between stages */}
                {index < activePath.roadmap.length - 1 && (
                  <div className="roadmap-connector">
                    <div className={`connector-line ${isCompleted ? 'active' : ''}`} />
                    <ChevronRight size={16} className={`connector-arrow ${isCompleted ? 'active' : ''}`} />
                  </div>
                )}
              </React.Fragment>
            );
          })}
        </div>
      </div>

      <style>{`
        .pathlist-section {
          padding: 16px 20px;
        }

        .section-header-wrap {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-bottom: 12px;
        }

        .path-icon-accent {
          color: #6366F1;
        }

        .roadmap-sub-badge {
          font-size: 0.75rem;
          color: var(--text-muted);
          background: rgba(255, 255, 255, 0.05);
          padding: 3px 8px;
          border-radius: var(--radius-full);
        }

        .path-tabs-row {
          display: flex;
          gap: 8px;
          overflow-x: auto;
          padding-bottom: 4px;
          margin-bottom: 8px;
        }

        .path-tab-pill {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 8px 14px;
          background: var(--bg-card);
          border: 1px solid var(--border-color);
          border-radius: var(--radius-md);
          color: var(--text-secondary);
          font-family: var(--font-sans);
          font-size: 0.82rem;
          font-weight: 600;
          cursor: pointer;
          white-space: nowrap;
          transition: var(--transition);
        }

        .path-tab-pill:hover {
          background: var(--bg-card-subtle);
          color: var(--text-primary);
        }

        .path-tab-pill.active {
          background: #1E293B;
          color: var(--text-primary);
          box-shadow: 0 4px 14px rgba(0, 0, 0, 0.4);
        }

        .active-path-desc {
          font-size: 0.8rem;
          color: var(--text-secondary);
          margin-bottom: 14px;
          padding: 0 2px;
        }

        /* Roadmap Horizontal Scroll */
        .roadmap-scroll-wrap {
          overflow-x: auto;
          padding: 4px 0 14px 0;
        }

        .roadmap-track {
          display: flex;
          align-items: center;
          min-width: max-content;
          gap: 6px;
        }

        .roadmap-node-card {
          width: 220px;
          background: var(--bg-card);
          border: 1px solid var(--border-color);
          border-radius: var(--radius-md);
          padding: 14px;
          display: flex;
          flex-direction: column;
          gap: 8px;
          cursor: pointer;
          transition: var(--transition);
          position: relative;
        }

        .roadmap-node-card:hover:not(.locked) {
          border-color: var(--border-highlight);
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
        }

        .roadmap-node-card.completed {
          background: linear-gradient(145deg, #101B22 0%, #0D161C 100%);
          border-color: rgba(16, 185, 129, 0.3);
        }

        .roadmap-node-card.unlocked {
          background: linear-gradient(145deg, #151D30 0%, #0F1626 100%);
          border-color: rgba(99, 102, 241, 0.4);
          box-shadow: 0 0 15px rgba(99, 102, 241, 0.15);
        }

        .roadmap-node-card.locked {
          opacity: 0.55;
          cursor: not-allowed;
          background: rgba(15, 20, 30, 0.6);
        }

        .node-top-bar {
          display: flex;
          align-items: center;
          justify-content: space-between;
        }

        .node-tier-tag {
          font-size: 0.68rem;
          font-weight: 800;
          text-transform: uppercase;
          letter-spacing: 0.05em;
          padding: 2px 7px;
          border-radius: var(--radius-sm);
          border: 1px solid;
        }

        .status-icon-completed {
          color: #10B981;
        }

        .status-icon-unlocked {
          color: #6366F1;
        }

        .status-icon-locked {
          color: var(--text-muted);
        }

        .node-title {
          font-size: 0.92rem;
          font-weight: 700;
          color: var(--text-primary);
          line-height: 1.3;
        }

        .node-desc {
          font-size: 0.75rem;
          color: var(--text-secondary);
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          min-height: 32px;
        }

        .node-footer {
          display: flex;
          align-items: center;
          justify-content: space-between;
          margin-top: 4px;
          padding-top: 8px;
          border-top: 1px solid rgba(255, 255, 255, 0.05);
        }

        .node-lessons-count {
          font-size: 0.72rem;
          color: var(--text-muted);
        }

        .node-action-label {
          font-size: 0.72rem;
          font-weight: 700;
        }

        .node-action-label.completed {
          color: #34D399;
        }

        .node-action-label.unlocked {
          color: #818CF8;
        }

        .node-action-label.locked {
          color: var(--text-muted);
        }

        /* Connector */
        .roadmap-connector {
          display: flex;
          align-items: center;
          gap: 2px;
          padding: 0 4px;
        }

        .connector-line {
          width: 16px;
          height: 2px;
          background: rgba(255, 255, 255, 0.1);
        }

        .connector-line.active {
          background: #10B981;
        }

        .connector-arrow {
          color: rgba(255, 255, 255, 0.2);
        }

        .connector-arrow.active {
          color: #10B981;
        }
      `}</style>
    </div>
  );
}
