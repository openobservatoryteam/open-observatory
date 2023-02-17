import { useRef } from 'react';
import { AriaDialogProps, useDialog } from 'react-aria';

type DialogProps = AriaDialogProps & {
  children: React.ReactNode;
  title: string;
};

function Dialog({ children, title, ...props }: DialogProps) {
  const ref = useRef(null);
  const { dialogProps, titleProps } = useDialog(
    {
      ...props,
      role: 'dialog',
    },
    ref,
  );
  return (
    <div {...dialogProps} ref={ref} style={{ padding: 30 }}>
      <h3 {...titleProps} style={{ marginTop: 0 }}>
        {title}
      </h3>
      {children}
    </div>
  );
}

export { Dialog };
