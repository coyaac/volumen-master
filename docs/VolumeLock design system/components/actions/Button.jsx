import React from 'react';
const { useState } = React;

const FILLS = {
  filled:   { bg: 'var(--md-primary)', fg: 'var(--md-on-primary)', border: 'none' },
  tonal:    { bg: 'var(--md-secondary-container)', fg: 'var(--md-on-secondary-container)', border: 'none' },
  outlined: { bg: 'transparent', fg: 'var(--md-primary)', border: '1px solid var(--md-outline)' },
  text:     { bg: 'transparent', fg: 'var(--md-primary)', border: 'none' },
  danger:   { bg: 'var(--md-error)', fg: 'var(--md-on-error)', border: 'none' }
};

export function Button({ variant = 'filled', icon, disabled, fullWidth, onClick, children }) {
  const [over, setOver] = useState(false);
  const s = FILLS[variant] || FILLS.filled;
  return (
    <button
      type="button"
      disabled={disabled}
      onClick={onClick}
      onMouseEnter={() => setOver(true)}
      onMouseLeave={() => setOver(false)}
      style={{
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        gap: 'var(--md-space-2)',
        minHeight: '40px', width: fullWidth ? '100%' : 'auto',
        padding: icon ? '0 var(--md-space-6) 0 var(--md-space-4)' : '0 var(--md-space-6)',
        background: s.bg, color: s.fg, border: s.border,
        borderRadius: 'var(--vl-shape-button)',
        font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)',
        cursor: disabled ? 'default' : 'pointer',
        opacity: disabled ? 0.38 : 1,
        boxShadow: over && !disabled && variant === 'filled' ? 'var(--md-elevation-1)' : 'none',
        filter: over && !disabled ? 'brightness(0.94)' : 'none',
        transition: 'filter var(--md-duration-short-4) var(--md-easing-standard)'
      }}>
      {icon ? <span className="md-icon" style={{ fontSize: '20px' }}>{icon}</span> : null}
      {children}
    </button>
  );
}
