import React from 'react';
const { useState } = React;

export function IconButton({ icon, label, selected, disabled, onClick }) {
  const [over, setOver] = useState(false);
  return (
    <button type="button" aria-label={label} disabled={disabled} onClick={onClick}
      onMouseEnter={() => setOver(true)} onMouseLeave={() => setOver(false)}
      style={{
        width: 'var(--vl-touch-target)', height: 'var(--vl-touch-target)',
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        borderRadius: 'var(--md-shape-full)', border: 'none',
        background: selected ? 'var(--md-primary-container)' : over ? 'var(--md-surface-container-high)' : 'transparent',
        color: selected ? 'var(--md-on-primary-container)' : 'var(--md-on-surface-variant)',
        cursor: disabled ? 'default' : 'pointer', opacity: disabled ? 0.38 : 1,
        transition: 'background var(--md-duration-short-4) var(--md-easing-standard)'
      }}>
      <span className={selected ? 'md-icon filled' : 'md-icon'} style={{ fontSize: '24px' }}>{icon}</span>
    </button>
  );
}
