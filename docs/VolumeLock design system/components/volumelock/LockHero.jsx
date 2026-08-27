import React from 'react';
import { Switch } from '../inputs/Switch.jsx';

export function LockHero({ locked, since, onToggle }) {
  return (
    <div style={{
      borderRadius: 'var(--vl-shape-hero)', padding: 'var(--md-space-6)',
      background: locked ? 'var(--vl-locked-container)' : 'var(--md-surface-container-high)',
      color: locked ? 'var(--vl-on-locked-container)' : 'var(--md-on-surface)',
      display: 'flex', alignItems: 'center', gap: 'var(--md-space-4)',
      transition: 'background var(--vl-transition-state), color var(--vl-transition-state)'
    }}>
      <span className="md-icon filled" style={{ fontSize: '40px', flex: '0 0 auto' }}>{locked ? 'lock' : 'lock_open'}</span>
      <div style={{ flex: 1, minWidth: 0, overflowWrap: 'anywhere' }}>
        <div style={{ font: 'var(--md-headline-small)' }}>{locked ? 'Bloqueado' : 'Desbloqueado'}</div>
        <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', opacity: 0.85, marginTop: 'var(--md-space-1)' }}>
          {locked ? (since ? 'Activo desde las ' + since : 'El volumen está fijo') : 'El volumen puede cambiar'}
        </div>
      </div>
      <Switch size="large" checked={locked} onChange={onToggle} onColor="var(--vl-locked)" />
    </div>
  );
}
