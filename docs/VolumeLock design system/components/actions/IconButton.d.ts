/** Botón de icono de 48dp. */
export interface IconButtonProps {
  /** Ligadura de Material Symbols. */
  icon: string;
  /** Etiqueta accesible obligatoria: el icono no lleva texto. */
  label: string;
  selected?: boolean;
  disabled?: boolean;
  onClick?: () => void;
}
export declare function IconButton(props: IconButtonProps): JSX.Element;
