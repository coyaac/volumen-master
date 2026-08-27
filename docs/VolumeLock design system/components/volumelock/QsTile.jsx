import React from 'react';

export function QsTile({ active, label = 'VolumeLock', state }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 'var(--md-space-3)',
      width: '100%', maxWidth: '176px', minWidth: 0, height: '80px', padding: 'var(--md-space-4)',
      borderRadius: 'var(--md-shape-xl)',
      background: active ? 'var(--vl-locked)' : 'var(--md-surface-container-highest)',
      color: active ? 'var(--vl-on-locked)' : 'var(--md-on-surface-variant)',
      transition: 'background var(--vl-transition-state)'
    }}>
      <span className="md-icon filled" style={{ fontSize: '24px' }}>{active ? 'lock' : 'lock_open'}</span>
      <div style={{ minWidth: 0 }}>
        <div style={{ font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{label}</div>
        <div style={{ font: 'var(--md-label-medium)', letterSpacing: 'var(--md-tracking-label-medium)', opacity: 0.8 }}>{state || (active ? 'Bloqueado' : 'Desbloqueado')}</div>
      </div>
    </div>
  );
}
