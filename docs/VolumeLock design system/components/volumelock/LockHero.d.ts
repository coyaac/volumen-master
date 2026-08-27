/**
 * Bloque de estado principal: el candado se lee a un metro de distancia.
 * @startingPoint section="VolumeLock" subtitle="Bloque de estado del candado" viewport="700x180"
 */
export interface LockHeroProps {
  locked?: boolean;
  /** Hora de activación, p. ej. "08:14". */
  since?: string;
  onToggle?: (next: boolean) => void;
}
export declare function LockHero(props: LockHeroProps): JSX.Element;
