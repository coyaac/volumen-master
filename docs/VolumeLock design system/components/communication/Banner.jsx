import React from 'react';

export function Banner({ tone = 'warning', icon, title, children, action, onAction, resolved }) {
  const warn = tone === 'warning';
  return (
    <div style={{
      display: 'flex', gap: 'var(--md-space-4)',
      padding: 'var(--md-space-4)', borderRadius: 'var(--vl-shape-card)',
      background: resolved ? 'var(--vl-locked-container)' : warn ? 'var(--md-error-container)' : 'var(--md-secondary-container)',
      color: resolved ? 'var(--vl-on-locked-container)' : warn ? 'var(--md-on-error-container)' : 'var(--md-on-secondary-container)'
    }}>
      <span className="md-icon filled" style={{ fontSize: '24px', flex: '0 0 auto' }}>{resolved ? 'check_circle' : (icon || 'error')}</span>
      <div style={{ flex: 1 }}>
        <div style={{ font: 'var(--md-title-medium)', letterSpacing: 'var(--md-tracking-title-medium)' }}>{title}</div>
        {children ? <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', marginTop: 'var(--md-space-1)' }}>{children}</div> : null}
        {action && !resolved ? (
          <button type="button" onClick={onAction} style={{ marginTop: 'var(--md-space-3)', minHeight: '40px', padding: '0 var(--md-space-4)', border: '1px solid currentColor', background: 'transparent', color: 'inherit', borderRadius: 'var(--vl-shape-button)', font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)', cursor: 'pointer' }}>{action}</button>
        ) : null}
      </div>
    </div>
  );
}
