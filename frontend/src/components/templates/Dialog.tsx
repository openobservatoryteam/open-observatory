import clsx from 'clsx';
import { useRef } from 'react';
import { AriaDialogProps, useDialog } from 'react-aria';
import { Title } from '../atoms/Title';

type DialogProps = AriaDialogProps & {
  children: React.ReactNode;
  title: string;
  className?: string;
};

function Dialog({ children, title, className, ...props }: DialogProps) {
  const ref = useRef(null);
  const { dialogProps, titleProps } = useDialog(
    {
      ...props,
      role: 'dialog',
    },
    ref,
  );
  return (
    <div {...dialogProps} ref={ref} style={{ padding: 30 }} className={clsx('text-white bg-[#333C47] rounded-2xl w-1/2', className)}>
      <Title as='h2' {...titleProps} style={{ marginTop: 0, paddingBottom: 30 }} centered>
        {title}
      </Title>
      {children}
    </div>
  );
}

export { Dialog };
