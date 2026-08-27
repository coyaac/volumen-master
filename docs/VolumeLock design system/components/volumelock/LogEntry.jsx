import React from 'react';

const STREAMS = { musica: 'volume_up', llamada: 'ring_volume', notificacion: 'notifications', alarma: 'alarm' };

export function LogEntry({ time, stream, streamLabel, from, to, reverted }) {
  return (
    <div style={{ display: 'grid', gridTemplateColumns: '48px 20px 1fr auto', alignItems: 'center', gap: 'var(--md-space-2)', minHeight: 'var(--vl-touch-target)', padding: 'var(--md-space-2) var(--md-space-4)' }}>
      <span style={{ fontFamily: 'var(--md-font-mono)', fontSize: '12px', fontVariantNumeric: 'tabular-nums', color: 'var(--md-on-surface-variant)' }}>{time}</span>
      <span className="md-icon" style={{ fontSize: '20px', color: 'var(--md-on-surface-variant)' }}>{STREAMS[stream] || 'volume_up'}</span>
      <span style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', color: 'var(--md-on-surface)', whiteSpace: 'nowrap' }}>
        {streamLabel}
        <span style={{ fontFamily: 'var(--md-font-mono)', fontVariantNumeric: 'tabular-nums', color: 'var(--md-on-surface-variant)', marginLeft: 'var(--md-space-2)' }}>{from} → {to}</span>
      </span>
      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--md-space-1)', font: 'var(--md-label-medium)', letterSpacing: 'var(--md-tracking-label-medium)', color: reverted ? 'var(--vl-locked)' : 'var(--md-on-surface-variant)' }}>
        <span className="md-icon" style={{ fontSize: '18px' }}>{reverted ? 'undo' : 'remove'}</span>
        {reverted ? 'Revertido' : 'Permitido'}
      </span>
    </div>
  );
}
