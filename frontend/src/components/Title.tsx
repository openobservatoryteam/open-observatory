import clsx from 'clsx';
import { DetailedHTMLProps, HTMLAttributes } from 'react';

type HeadingElement = 'h1' | 'h2' | 'h3' | 'h4' | 'h5' | 'h6';

const styles: Record<HeadingElement, string> = {
  h1: 'font-bold text-2xl md:text-3xl',
  h2: 'text-2xl md:text-3xl',
  h3: 'text-xl',
  h4: 'text-lg',
  h5: 'text-md',
  h6: 'text-md',
};

type TitleProps = { as?: HeadingElement } & DetailedHTMLProps<HTMLAttributes<HTMLHeadingElement>, HTMLHeadingElement>;

function Title({ as = 'h1', className, ...props }: TitleProps) {
  const Component = as;
  return <Component className={clsx(styles[as], className)} {...props} />;
}

export default Title;
