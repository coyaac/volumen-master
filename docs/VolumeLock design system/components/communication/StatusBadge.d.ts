/** Etiqueta de estado. `locked` es el único uso del verde --vl-locked. */
export interface StatusBadgeProps {
  tone?: 'locked' | 'unlocked' | 'warning' | 'info';
  icon?: string;
  children?: React.ReactNode;
}
export declare function StatusBadge(props: StatusBadgeProps): JSX.Element;
