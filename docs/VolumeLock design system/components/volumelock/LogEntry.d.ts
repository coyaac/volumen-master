/**
 * Una línea del historial: hora, stream, valor anterior → nuevo y si se revirtió.
 * @startingPoint section="VolumeLock" subtitle="Filas del historial de cambios" viewport="700x220"
 */
export interface LogEntryProps {
  /** Hora en 24h, "14:02". */
  time: string;
  stream: 'musica' | 'llamada' | 'notificacion' | 'alarma';
  streamLabel: string;
  from: number;
  to: number;
  reverted?: boolean;
}
export declare function LogEntry(props: LogEntryProps): JSX.Element;
