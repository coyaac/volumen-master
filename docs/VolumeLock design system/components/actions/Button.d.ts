/**
 * Botón de acción Material 3.
 * @startingPoint section="Acciones" subtitle="Botones M3 en sus cinco variantes" viewport="700x160"
 */
export interface ButtonProps {
  /** filled = acción principal; danger sólo para acciones destructivas. */
  variant?: 'filled' | 'tonal' | 'outlined' | 'text' | 'danger';
  /** Ligadura de Material Symbols, p. ej. "lock". */
  icon?: string;
  disabled?: boolean;
  fullWidth?: boolean;
  onClick?: () => void;
  children?: React.ReactNode;
}
export declare function Button(props: ButtonProps): JSX.Element;
