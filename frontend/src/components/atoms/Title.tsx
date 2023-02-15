import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';

import { AsProps } from '@/types';
import { removeKeys } from '@/utils';

type HeadingElement = 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6';
type TitleProps = AsProps<HeadingElement> & ComponentPropsWithoutRef<HeadingElement> & { centered?: boolean };

const classes = (as: HeadingElement, { centered, className }: Omit<TitleProps, 'as'>) => {
  const headingStyles: Record<HeadingElement, string> = {
    h1: 'font-bold text-2xl md:text-3xl',
    h2: 'text-2xl md:text-3xl',
    h3: 'text-xl',
    h4: 'text-lg',
    h5: 'text-md',
    h6: 'text-md',
  };
  return clsx(headingStyles[as], centered && 'text-center', 'text-white', className);
};

function Title({ as = 'h1', ...props }: TitleProps) {
  const Component = as;
  return <Component className={classes(as, props)} {...removeKeys(props, ['className'])} />;
}

export { Title };
