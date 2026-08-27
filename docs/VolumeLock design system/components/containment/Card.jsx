import React from 'react';

const BG = {
  filled: 'var(--md-surface-container-high)',
  elevated: 'var(--md-surface-container-low)',
  outlined: 'var(--md-surface)'
};

export function Card({ variant = 'filled', tone, padding = 'var(--md-space-4)', children }) {
  return (
    <div style={{
      background: tone || BG[variant],
      color: 'var(--md-on-surface)',
      border: variant === 'outlined' ? '1px solid var(--md-outline-variant)' : 'none',
      boxShadow: variant === 'elevated' ? 'var(--md-elevation-1)' : 'none',
      borderRadius: 'var(--vl-shape-card)',
      padding
    }}>{children}</div>
  );
}
