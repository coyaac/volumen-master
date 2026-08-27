/** Diálogo modal M3, elevación 3. Reservado a confirmaciones destructivas. */
export interface DialogProps {
  open?: boolean;
  icon?: string;
  title: React.ReactNode;
  children?: React.ReactNode;
  actions?: React.ReactNode;
}
export declare function Dialog(props: DialogProps): JSX.Element | null;
