import React from 'react';

const TONES = {
  locked:  { bg: 'var(--vl-locked-container)', fg: 'var(--vl-on-locked-container)', icon: 'lock' },
  unlocked:{ bg: 'var(--md-surface-container-highest)', fg: 'var(--md-on-surface-variant)', icon: 'lock_open' },
  warning: { bg: 'var(--md-error-container)', fg: 'var(--md-on-error-container)', icon: 'error' },
  info:    { bg: 'var(--md-secondary-container)', fg: 'var(--md-on-secondary-container)', icon: 'info' }
};

export function StatusBadge({ tone = 'info', icon, children }) {
  const t = TONES[tone] || TONES.info;
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--md-space-2)', height: '32px', padding: '0 var(--md-space-3)', borderRadius: 'var(--md-shape-sm)', background: t.bg, color: t.fg, font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)' }}>
      <span className="md-icon filled" style={{ fontSize: '18px' }}>{icon || t.icon}</span>
      {children}
    </span>
  );
}
