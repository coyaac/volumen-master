/**
 * Réplica del Quick Settings Tile de Android tal y como se ve en el panel desplegable.
 * @startingPoint section="VolumeLock" subtitle="Tile de Ajustes rápidos, activo e inactivo" viewport="700x140"
 */
export interface QsTileProps {
  active?: boolean;
  label?: string;
  /** Segunda línea; por defecto Bloqueado / Desbloqueado. */
  state?: string;
}
export declare function QsTile(props: QsTileProps): JSX.Element;
