import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType } from 'react';

import { removeKeys } from '@/utils';

type TextProps<C extends ElementType = 'p'> =
  | ComponentPropsWithoutRef<C> & { as?: C; bold?: boolean; centered?: boolean };

const excludedProps = ['bold', 'centered'] as const;
const classes = ({ bold, centered, className }: TextProps) =>
  clsx('text-white', bold && 'font-bold', centered && 'text-center', className);

function Text<C extends ElementType>({ as, ...props }: TextProps<C>) {
  const Component = as ?? 'p';
  return <Component className={classes(props)} {...removeKeys(props, excludedProps)} />;
}

export { Text };
