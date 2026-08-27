/** Contenedor M3. Las superficies se separan por tono, no por sombra. */
export interface CardProps {
  variant?: 'filled' | 'elevated' | 'outlined';
  /** Fondo explícito, p. ej. var(--vl-locked-container). */
  tone?: string;
  padding?: string;
  children?: React.ReactNode;
}
export declare function Card(props: CardProps): JSX.Element;
