import React from 'react';

export function NavigationBar({ items = [], value, onChange }) {
  return (
    <nav style={{ display: 'flex', height: 'var(--vl-nav-bar-height)', background: 'var(--md-surface-container)', paddingTop: 'var(--md-space-3)' }}>
      {items.map((it) => {
        const on = it.value === value;
        return (
          <button key={it.value} type="button" onClick={() => onChange && onChange(it.value)}
            style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 'var(--md-space-1)', border: 'none', background: 'transparent', cursor: 'pointer', color: on ? 'var(--md-on-surface)' : 'var(--md-on-surface-variant)' }}>
            <span style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', width: '64px', height: '32px', borderRadius: 'var(--md-shape-full)', background: on ? 'var(--md-secondary-container)' : 'transparent', color: on ? 'var(--md-on-secondary-container)' : 'inherit', transition: 'background var(--md-duration-short-4) var(--md-easing-standard)' }}>
              <span className={on ? 'md-icon filled' : 'md-icon'} style={{ fontSize: '24px' }}>{it.icon}</span>
            </span>
            <span style={{ font: 'var(--md-label-medium)', letterSpacing: 'var(--md-tracking-label-medium)' }}>{it.label}</span>
          </button>
        );
      })}
    </nav>
  );
}
