import { faClose } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { clsx } from 'clsx';
import { useRef } from 'react';
import { AriaDialogProps, useDialog } from 'react-aria';

import { Button, Title } from '~/components';

type DialogProps = AriaDialogProps & {
  children: React.ReactNode;
  className?: string;
  onClose?: () => unknown;
  title: string;
};

function Dialog({ children, className, onClose, title, ...props }: DialogProps) {
  const ref = useRef(null);
  const { dialogProps, titleProps } = useDialog(
    {
      ...props,
      role: 'dialog',
    },
    ref,
  );
  return (
    <div
      {...dialogProps}
      className={clsx(
        'text-white bg-[#333C47] rounded-2xl w-[95%] sm:w-[90%] md:w-[85%] lg:w-[75%] xl:w-[50%] relative z-[1001]',
        className,
      )}
      ref={ref}
      style={{ padding: 30 }}
    >
      {onClose && (
        <Button className="absolute h-8 w-8" onClick={onClose} rounded>
          <FontAwesomeIcon color="black" icon={faClose} size="2x" />
        </Button>
      )}
      <Title as="h2" centered style={{ marginBottom: 30 }} {...titleProps}>
        {title}
      </Title>
      {children}
    </div>
  );
}

export { Dialog };
