import React from 'react';

export function Dialog({ open, icon, title, children, actions }) {
  if (!open) return null;
  return (
    <div style={{ position: 'absolute', inset: 0, background: 'color-mix(in srgb, var(--md-scrim) 32%, transparent)', display: 'flex', alignItems: 'center', justifyContent: 'center', padding: 'var(--md-space-6)', zIndex: 40 }}>
      <div role="dialog" style={{ width: '100%', maxWidth: '312px', background: 'var(--md-surface-container-high)', borderRadius: 'var(--md-shape-xl)', boxShadow: 'var(--md-elevation-3)', padding: 'var(--md-space-6)' }}>
        {icon ? <div style={{ textAlign: 'center', marginBottom: 'var(--md-space-4)' }}><span className="md-icon" style={{ fontSize: '24px', color: 'var(--md-secondary)' }}>{icon}</span></div> : null}
        <div style={{ font: 'var(--md-headline-small)', color: 'var(--md-on-surface)', textAlign: icon ? 'center' : 'left', marginBottom: 'var(--md-space-4)' }}>{title}</div>
        <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', color: 'var(--md-on-surface-variant)' }}>{children}</div>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 'var(--md-space-2)', marginTop: 'var(--md-space-6)' }}>{actions}</div>
      </div>
    </div>
  );
}
