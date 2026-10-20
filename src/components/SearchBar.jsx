import React, { useState } from 'react';
import { Search, X, Sparkles } from 'lucide-react';

export default function SearchBar({ value, onChange, onSearchSubmit }) {
  const [isFocused, setIsFocused] = useState(false);
  const quickFilters = ["Python", "Async", "DSA", "Web Dev", "Algorithms", "React"];

  const handleClear = () => {
    if (onChange) onChange('');
  };

  const handleSelectQuickFilter = (filter) => {
    if (onChange) onChange(filter);
    if (onSearchSubmit) onSearchSubmit(filter);
  };

  return (
    <div className="searchbar-section">
      <div className={`searchbar-wrapper ${isFocused ? 'focused' : ''}`}>
        <Search className="search-icon" size={18} />
        <input 
          type="text"
          className="search-input"
          placeholder="Search courses, topics, lessons..."
          value={value}
          onChange={(e) => onChange && onChange(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onBlur={() => setTimeout(() => setIsFocused(false), 200)}
        />
        {value && (
          <button 
            type="button" 
            className="clear-search-btn"
            onClick={handleClear}
            aria-label="Clear search input"
          >
            <X size={16} />
          </button>
        )}
      </div>

      {/* Quick Filter Chips */}
      <div className="quick-tags-row hide-scrollbar">
        <span className="quick-label">
          <Sparkles size={12} />
          Popular:
        </span>
        {quickFilters.map((tag) => (
          <button 
            key={tag}
            type="button"
            className={`quick-chip ${value.toLowerCase() === tag.toLowerCase() ? 'active' : ''}`}
            onClick={() => handleSelectQuickFilter(tag)}
          >
            {tag}
          </button>
        ))}
      </div>

      <style>{`
        .searchbar-section {
          padding: 16px 20px 8px 20px;
        }

        .searchbar-wrapper {
          display: flex;
          align-items: center;
          gap: 12px;
          background: var(--bg-card);
          border: 1px solid var(--border-color);
          border-radius: var(--radius-md);
          padding: 11px 16px;
          transition: var(--transition);
          position: relative;
        }

        .searchbar-wrapper:hover {
          border-color: var(--border-highlight);
        }

        .searchbar-wrapper.focused {
          border-color: var(--primary);
          box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.2);
          background: var(--bg-card-subtle);
        }

        .search-icon {
          color: var(--text-muted);
          flex-shrink: 0;
          transition: var(--transition);
        }

        .searchbar-wrapper.focused .search-icon {
          color: var(--primary);
        }

        .search-input {
          flex: 1;
          background: transparent;
          border: none;
          outline: none;
          color: var(--text-primary);
          font-family: var(--font-sans);
          font-size: 0.92rem;
        }

        .search-input::placeholder {
          color: var(--text-muted);
        }

        .clear-search-btn {
          background: rgba(255, 255, 255, 0.08);
          border: none;
          color: var(--text-secondary);
          border-radius: 50%;
          width: 22px;
          height: 22px;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: var(--transition);
        }

        .clear-search-btn:hover {
          background: rgba(255, 255, 255, 0.16);
          color: #FFF;
        }

        .quick-tags-row {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-top: 10px;
          overflow-x: auto;
          padding-bottom: 4px;
        }

        .quick-label {
          display: inline-flex;
          align-items: center;
          gap: 4px;
          font-size: 0.72rem;
          font-weight: 700;
          text-transform: uppercase;
          color: var(--text-muted);
          letter-spacing: 0.05em;
          white-space: nowrap;
        }

        .quick-chip {
          background: rgba(255, 255, 255, 0.04);
          border: 1px solid rgba(255, 255, 255, 0.08);
          border-radius: var(--radius-full);
          padding: 4px 10px;
          font-size: 0.75rem;
          color: var(--text-secondary);
          cursor: pointer;
          white-space: nowrap;
          transition: var(--transition);
        }

        .quick-chip:hover {
          background: rgba(99, 102, 241, 0.12);
          border-color: rgba(99, 102, 241, 0.3);
          color: #A5B4FC;
        }

        .quick-chip.active {
          background: var(--primary);
          border-color: var(--primary);
          color: #FFF;
        }
      `}</style>
    </div>
  );
}
