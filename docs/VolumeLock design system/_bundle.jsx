const { useState } = React;
/* Bundle generado desde components/. No editar a mano: edita el .jsx correspondiente. */


const FILLS = {
  filled:   { bg: 'var(--md-primary)', fg: 'var(--md-on-primary)', border: 'none' },
  tonal:    { bg: 'var(--md-secondary-container)', fg: 'var(--md-on-secondary-container)', border: 'none' },
  outlined: { bg: 'transparent', fg: 'var(--md-primary)', border: '1px solid var(--md-outline)' },
  text:     { bg: 'transparent', fg: 'var(--md-primary)', border: 'none' },
  danger:   { bg: 'var(--md-error)', fg: 'var(--md-on-error)', border: 'none' }
};

function Button({ variant = 'filled', icon, disabled, fullWidth, onClick, children }) {
  const [over, setOver] = useState(false);
  const s = FILLS[variant] || FILLS.filled;
  return (
    <button
      type="button"
      disabled={disabled}
      onClick={onClick}
      onMouseEnter={() => setOver(true)}
      onMouseLeave={() => setOver(false)}
      style={{
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        gap: 'var(--md-space-2)',
        minHeight: '40px', width: fullWidth ? '100%' : 'auto',
        padding: icon ? '0 var(--md-space-6) 0 var(--md-space-4)' : '0 var(--md-space-6)',
        background: s.bg, color: s.fg, border: s.border,
        borderRadius: 'var(--vl-shape-button)',
        font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)',
        cursor: disabled ? 'default' : 'pointer',
        opacity: disabled ? 0.38 : 1,
        boxShadow: over && !disabled && variant === 'filled' ? 'var(--md-elevation-1)' : 'none',
        filter: over && !disabled ? 'brightness(0.94)' : 'none',
        transition: 'filter var(--md-duration-short-4) var(--md-easing-standard)'
      }}>
      {icon ? <span className="md-icon" style={{ fontSize: '20px' }}>{icon}</span> : null}
      {children}
    </button>
  );
}


function IconButton({ icon, label, selected, disabled, onClick }) {
  const [over, setOver] = useState(false);
  return (
    <button type="button" aria-label={label} disabled={disabled} onClick={onClick}
      onMouseEnter={() => setOver(true)} onMouseLeave={() => setOver(false)}
      style={{
        width: 'var(--vl-touch-target)', height: 'var(--vl-touch-target)',
        display: 'inline-flex', alignItems: 'center', justifyContent: 'center',
        borderRadius: 'var(--md-shape-full)', border: 'none',
        background: selected ? 'var(--md-primary-container)' : over ? 'var(--md-surface-container-high)' : 'transparent',
        color: selected ? 'var(--md-on-primary-container)' : 'var(--md-on-surface-variant)',
        cursor: disabled ? 'default' : 'pointer', opacity: disabled ? 0.38 : 1,
        transition: 'background var(--md-duration-short-4) var(--md-easing-standard)'
      }}>
      <span className={selected ? 'md-icon filled' : 'md-icon'} style={{ fontSize: '24px' }}>{icon}</span>
    </button>
  );
}


function SegmentedButton({ options = [], value, onChange }) {
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


function Switch({ checked, onChange, disabled, size = 'medium', onColor }) {
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


function VolumeSlider({ icon = 'volume_up', label, value = 0, max = 15, disabled, onChange }) {
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


const BG = {
  filled: 'var(--md-surface-container-high)',
  elevated: 'var(--md-surface-container-low)',
  outlined: 'var(--md-surface)'
};

function Card({ variant = 'filled', tone, padding = 'var(--md-space-4)', children }) {
  return (
    <div style={{
      background: tone || BG[variant],
      color: 'var(--md-on-surface)',
      border: variant === 'outlined' ? '1px solid var(--md-outline-variant)' : 'none',
      boxShadow: variant === 'elevated' ? 'var(--md-elevation-1)' : 'none',
      borderRadius: 'var(--vl-shape-card)',
      padding
    }}>{children}</div>
  );
}


function ListItem({ icon, headline, supporting, trailing, onClick }) {
  const [over, setOver] = useState(false);
  return (
    <div onClick={onClick}
      onMouseEnter={() => setOver(true)} onMouseLeave={() => setOver(false)}
      style={{
        display: 'flex', alignItems: 'center', gap: 'var(--md-space-4)',
        minHeight: 'var(--vl-touch-target)', padding: 'var(--md-space-3) var(--md-space-4)',
        background: over && onClick ? 'var(--md-surface-container)' : 'transparent',
        cursor: onClick ? 'pointer' : 'default'
      }}>
      {icon ? <span className="md-icon" style={{ fontSize: '24px', color: 'var(--md-on-surface-variant)' }}>{icon}</span> : null}
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ font: 'var(--md-body-large)', color: 'var(--md-on-surface)' }}>{headline}</div>
        {supporting ? <div style={{ font: 'var(--md-body-medium)', letterSpacing: 'var(--md-tracking-body-medium)', color: 'var(--md-on-surface-variant)' }}>{supporting}</div> : null}
      </div>
      {trailing}
    </div>
  );
}


function Dialog({ open, icon, title, children, actions }) {
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


const TONES = {
  locked:  { bg: 'var(--vl-locked-container)', fg: 'var(--vl-on-locked-container)', icon: 'lock' },
  unlocked:{ bg: 'var(--md-surface-container-highest)', fg: 'var(--md-on-surface-variant)', icon: 'lock_open' },
  warning: { bg: 'var(--md-error-container)', fg: 'var(--md-on-error-container)', icon: 'error' },
  info:    { bg: 'var(--md-secondary-container)', fg: 'var(--md-on-secondary-container)', icon: 'info' }
};

function StatusBadge({ tone = 'info', icon, children }) {
  const t = TONES[tone] || TONES.info;
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 'var(--md-space-2)', height: '32px', padding: '0 var(--md-space-3)', borderRadius: 'var(--md-shape-sm)', background: t.bg, color: t.fg, font: 'var(--md-label-large)', letterSpacing: 'var(--md-tracking-label-large)' }}>
      <span className="md-icon filled" style={{ fontSize: '18px' }}>{icon || t.icon}</span>
      {children}
    </span>
  );
}


function Banner({ tone = 'warning', icon, title, children, action, onAction, resolved }) {
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


function TopAppBar({ title, leading, actions }) {
  return (
    <header style={{ display: 'flex', alignItems: 'center', gap: 'var(--md-space-2)', height: 'var(--vl-app-bar-height)', padding: '0 var(--md-space-1) 0 var(--md-space-4)', background: 'var(--md-surface)', color: 'var(--md-on-surface)' }}>
      {leading}
      <h1 style={{ flex: 1, margin: 0, font: 'var(--md-title-large)' }}>{title}</h1>
      <div style={{ display: 'flex', alignItems: 'center' }}>{actions}</div>
    </header>
  );
}


function NavigationBar({ items = [], value, onChange }) {
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


function LockHero({ locked, since, onToggle }) {
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


const STREAMS = { musica: 'volume_up', llamada: 'ring_volume', notificacion: 'notifications', alarma: 'alarm' };

function LogEntry({ time, stream, streamLabel, from, to, reverted }) {
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


function QsTile({ active, label = 'VolumeLock', state }) {
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

window.VL = { Button, IconButton, SegmentedButton, Switch, VolumeSlider, Card, ListItem, Dialog, StatusBadge, Banner, TopAppBar, NavigationBar, LockHero, LogEntry, QsTile };
