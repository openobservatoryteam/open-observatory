import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType } from 'react';

import { removeKeys } from '@/utils';

type TextColor = 'black' | 'white';
type TextProps<C extends ElementType = 'p'> =
  | ComponentPropsWithoutRef<C> & { as?: C; bold?: boolean; centered?: boolean; className?: string; color?: TextColor };

const excludedProps = ['bold', 'centered', 'className', 'color'] as const;
const classes = ({ bold, centered, className, color }: TextProps) => {
  const colors: Record<TextColor, string> = {
    black: 'text-black',
    white: 'text-white',
  };
  return clsx(bold && 'font-bold', centered && 'text-center', colors[color ?? 'white'], className);
};

function Text<C extends ElementType = 'p'>({ as, ...props }: TextProps<C>) {
  const Component = as ?? 'p';
  return <Component className={classes(props)} {...removeKeys(props, excludedProps)} />;
}

export { Text };
