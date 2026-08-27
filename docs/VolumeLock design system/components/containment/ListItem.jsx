import React from 'react';
const { useState } = React;

export function ListItem({ icon, headline, supporting, trailing, onClick }) {
  const [over, setOver] = useState(false);
  return (
    <div onClick={onClick}
      onMouseEnter={() => setOver(true)} onMouseLeave={() => setOver(false)}
      style={{
        display: 'flex', alignItems: 'center', gap: 'var(--md-space-4)',
        minHeight: 'var(--vl-touch-target)', padding: 'var(--md-space-3) var(--md-space-4)',
        background: over && onClick ? 'var(--md-surface-container)' : 'transparent',
        cursor: onClick ? 'pointer' : 'default'
      }}>
      {icon ? <span className="md-icon" style={{ fontSize: '24px', color: 'var(--md-on-surface-variant)' }}>{icon}</span> : null}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ font: 'var(--md-body-large)', color: 'var(--md-on-surface)' }}>{headline}</div>
        {supporting ? <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', color: 'var(--md-on-surface-variant)' }}>{supporting}</div> : null}
      </div>
      {trailing}
    </div>
  );
}
