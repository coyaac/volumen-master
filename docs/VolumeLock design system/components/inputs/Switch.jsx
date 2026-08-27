import React from 'react';

export function Switch({ checked, onChange, disabled, size = 'medium', onColor }) {
  const big = size === 'large';
  const w = big ? 88 : 52, h = big ? 52 : 32, knob = checked ? (big ? 40 : 24) : (big ? 26 : 16);
  const on = onColor || 'var(--md-primary)';
  return (
    <button type="button" role="switch" aria-checked={!!checked} disabled={disabled}
      onClick={() => onChange && onChange(!checked)}
      style={{
        width: w + 'px', height: h + 'px', padding: 0, flex: '0 0 auto',
        borderRadius: 'var(--md-shape-full)',
        border: checked ? '2px solid transparent' : '2px solid var(--md-outline)',
        background: checked ? on : 'var(--md-surface-container-highest)',
        position: 'relative', cursor: disabled ? 'default' : 'pointer',
        opacity: disabled ? 0.38 : 1,
        transition: 'background var(--vl-transition-state)'
      }}>
      <span style={{
        position: 'absolute', top: '50%', left: checked ? 'calc(100% - ' + (knob + 4) + 'px)' : '4px',
        transform: 'translateY(-50%)',
        width: knob + 'px', height: knob + 'px', borderRadius: 'var(--md-shape-full)',
        background: checked ? 'var(--md-surface-container-lowest)' : 'var(--md-outline)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: checked ? on : 'transparent',
        transition: 'left var(--vl-transition-state), width var(--vl-transition-state)'
      }}>
        {checked ? <span className="md-icon" style={{ fontSize: big ? '24px' : '16px' }}>check</span> : null}
      </span>
    </button>
  );
}
