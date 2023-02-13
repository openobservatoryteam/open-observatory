import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType } from 'react';

import { removeKeys } from '@/utils';

type TextProps<C extends ElementType = 'p'> =
  | ComponentPropsWithoutRef<C> & { as?: C; bold?: boolean; centered?: boolean; className?: string };

const excludedProps = ['bold', 'centered', 'className'] as const;
const classes = ({ bold, centered, className, color }: TextProps) =>
  clsx(
    (color === 'white' || !color) && 'text-white',
    color === 'black' && 'text-black',
    bold && 'font-bold',
    centered && 'text-center',
    className,
  );

function Text<C extends ElementType = 'p'>({ as, ...props }: TextProps<C>) {
  const Component = as ?? 'p';
  return <Component className={classes(props)} {...removeKeys(props, excludedProps)} />;
}

export { Text };
