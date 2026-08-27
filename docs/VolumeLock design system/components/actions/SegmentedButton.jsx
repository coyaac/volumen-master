import React from 'react';

export function SegmentedButton({ options = [], value, onChange }) {
  return (
    <div role="group" style={{ display: 'flex', width: '100%', borderRadius: 'var(--md-shape-full)', overflow: 'hidden', border: '1px solid var(--md-outline)' }}>
      {options.map((o, i) => {
        const on = o.value === value;
        return (
          <button key={o.value} type="button" onClick={() => onChange && onChange(o.value)}
            style={{
              flex: 1, minHeight: '40px', display: 'flex', alignItems: 'center', justifyContent: 'center',
              gap: 'var(--md-space-2)', border: 'none',
              borderLeft: i ? '1px solid var(--md-outline)' : 'none',
              background: on ? 'var(--md-secondary-container)' : 'transparent',
              color: on ? 'var(--md-on-secondary-container)' : 'var(--md-on-surface)',
              font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)', cursor: 'pointer'
            }}>
            {on ? <span className="md-icon" style={{ fontSize: '18px' }}>check</span> : null}
            {o.label}
          </button>
        );
      })}
    </div>
  );
}
