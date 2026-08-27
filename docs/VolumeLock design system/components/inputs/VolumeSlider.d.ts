/**
 * Slider de volumen objetivo para un stream, con icono, etiqueta y lectura tabular.
 * @startingPoint section="Controles" subtitle="Sliders de volumen por stream" viewport="700x260"
 */
export interface VolumeSliderProps {
  /** Ligadura de Material Symbols del stream. */
  icon?: string;
  label: string;
  value?: number;
  /** Pasos del stream en Android (música 15, alarma 7, llamada 5). */
  max?: number;
  disabled?: boolean;
  onChange?: (value: number) => void;
}
export declare function VolumeSlider(props: VolumeSliderProps): JSX.Element;
