/** Barra de navegación inferior M3, 80dp. */
export interface NavigationBarItem { value: string; label: string; icon: string; }
export interface NavigationBarProps {
  items: NavigationBarItem[];
  value?: string;
  onChange?: (value: string) => void;
}
export declare function NavigationBar(props: NavigationBarProps): JSX.Element;
