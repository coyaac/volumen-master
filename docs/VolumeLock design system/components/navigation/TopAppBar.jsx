import React from 'react';

export function TopAppBar({ title, leading, actions }) {
  return (
    <header style={{ display: 'flex', alignItems: 'center', gap: 'var(--md-space-2)', height: 'var(--vl-app-bar-height)', padding: '0 var(--md-space-1) 0 var(--md-space-4)', background: 'var(--md-surface)', color: 'var(--md-on-surface)' }}>
      {leading}
      <h1 style={{ flex: 1, margin: 0, font: 'var(--md-title-large)' }}>{title}</h1>
      <div style={{ display: 'flex', alignItems: 'center' }}>{actions}</div>
    </header>
  );
}
