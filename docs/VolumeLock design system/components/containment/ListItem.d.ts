/** Fila de lista M3 de una o dos líneas. */
export interface ListItemProps {
  icon?: string;
  headline: React.ReactNode;
  supporting?: React.ReactNode;
  trailing?: React.ReactNode;
  onClick?: () => void;
}
export declare function ListItem(props: ListItemProps): JSX.Element;
