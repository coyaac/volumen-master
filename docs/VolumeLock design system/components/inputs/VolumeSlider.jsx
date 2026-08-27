import React from 'react';

export function VolumeSlider({ icon = 'volume_up', label, value = 0, max = 15, disabled, onChange }) {
  const pct = max ? (value / max) * 100 : 0;
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '24px 1fr auto', alignItems: 'center', gap: 'var(--md-space-4)', opacity: disabled ? 0.38 : 1 }}>
      <span className="md-icon" style={{ fontSize: '24px', color: 'var(--md-on-surface-variant)' }}>{icon}</span>
      <div>
        <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', color: 'var(--md-on-surface-variant)', marginBottom: 'var(--md-space-1)' }}>{label}</div>
        <div style={{ position: 'relative', height: 'var(--vl-touch-target)', display: 'flex', alignItems: 'center' }}>
          <div style={{ position: 'absolute', inset: 'auto 0', height: '16px', borderRadius: 'var(--md-shape-sm)', background: 'var(--md-surface-container-highest)' }} />
          <div style={{ position: 'absolute', left: 0, width: pct + '%', height: '16px', borderRadius: 'var(--md-shape-sm)', background: 'var(--md-primary)', transition: 'width var(--md-duration-short-4) var(--md-easing-standard)' }} />
          <div style={{ position: 'absolute', left: 'calc(' + pct + '% - 2px)', width: '4px', height: '44px', borderRadius: 'var(--md-shape-full)', background: 'var(--md-primary)' }} />
          <input type="range" min="0" max={max} value={value} disabled={disabled}
            onChange={(e) => onChange && onChange(Number(e.target.value))}
            style={{ position: 'relative', width: '100%', height: 'var(--vl-touch-target)', opacity: 0, margin: 0, cursor: disabled ? 'default' : 'pointer' }} />
        </div>
      </div>
      <div style={{ font: 'var(--md-font-mono)', fontSize: '16px', fontVariantNumeric: 'tabular-nums', color: 'var(--md-on-surface)', minWidth: '48px', textAlign: 'right' }}>{value} / {max}</div>
    </div>
  );
}
