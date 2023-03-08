import { clsx } from 'clsx';
import { ComponentPropsWithoutRef } from 'react';

type ChipColor = 'red' | 'white';
type ChipProps = ComponentPropsWithoutRef<'span'> & { bold?: boolean; color?: ChipColor };

const classes = (bold: boolean, color: ChipColor) => {
  const colors: Record<ChipColor, string> = {
    red: 'bg-red-600 text-white',
    white: 'bg-white text-black',
  };
  return clsx('hover:cursor-default px-5 py-2 rounded-full select-none', bold && 'font-bold', colors[color]);
};

const Chip = ({ bold = false, children, className, color = 'white', ...props }: ChipProps) => {
  return (
    <span className={clsx(classes(bold, color), className)} {...props}>
      {children}
    </span>
  );
};

export { Chip };
