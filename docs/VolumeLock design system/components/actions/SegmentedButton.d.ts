/** Control segmentado M3 — elección única entre 2 y 4 opciones. */
export interface SegmentedOption { value: string; label: string; }
export interface SegmentedButtonProps {
  options: SegmentedOption[];
  value?: string;
  onChange?: (value: string) => void;
}
export declare function SegmentedButton(props: SegmentedButtonProps): JSX.Element;
