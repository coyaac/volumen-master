/**
 * Aviso de permiso pendiente. Al resolverse pasa a verde y pierde el botón; el consumidor lo retira después.
 * @startingPoint section="Permisos" subtitle="Avisos de permiso pendiente y resuelto" viewport="700x300"
 */
export interface BannerProps {
  tone?: 'warning' | 'info';
  icon?: string;
  title: React.ReactNode;
  children?: React.ReactNode;
  action?: string;
  onAction?: () => void;
  /** true cuando el permiso ya está concedido. */
  resolved?: boolean;
}
export declare function Banner(props: BannerProps): JSX.Element;
