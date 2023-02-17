import { createRef } from 'react';
import { AriaDialogProps, useDialog } from 'react-aria';

type DialogProps = AriaDialogProps & {
  title: string;
  children: React.ReactNode;
};

function Dialog({ title, children, ...props }: DialogProps) {
  let ref = createRef<HTMLDivElement>();
  let { dialogProps, titleProps } = useDialog(
    {
      ...props,
      role: 'dialog',
    },
    ref,
  );

  console.log(ref);

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
