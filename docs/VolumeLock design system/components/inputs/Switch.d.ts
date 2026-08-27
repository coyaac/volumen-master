/** Interruptor M3. `size="large"` es el switch maestro del candado. */
export interface SwitchProps {
  checked?: boolean;
  onChange?: (next: boolean) => void;
  disabled?: boolean;
  size?: 'medium' | 'large';
  /** Color del estado activo; usa var(--vl-locked) para el candado. */
  onColor?: string;
}
export declare function Switch(props: SwitchProps): JSX.Element;
