import { clsx } from 'clsx';
import { ForwardedRef, forwardRef } from 'react';
import { AriaTextFieldOptions, useTextField } from 'react-aria';

import { useForwardedRef } from '~/hooks';

type TextAreaProps = AriaTextFieldOptions<'textarea'> & { className?: string };

function TextArea({ ...props }: TextAreaProps, forwardedRef?: ForwardedRef<HTMLTextAreaElement>) {
  const ref = useForwardedRef(forwardedRef);
  const { errorMessageProps, inputProps } = useTextField(props, ref);
  const { errorMessage } = props;
  return (
    <div className={props.className}>
      <div className="bg-white flex px-4 py-2 rounded-3xl text-black w-full">
        <textarea {...inputProps} className={clsx('bg-inherit outline-none w-full resize-none h-32')} ref={ref} />
      </div>
      {errorMessage && (
        <p className="text-center text-red-500" {...errorMessageProps}>
          {errorMessage}
        </p>
      )}
    </div>
  );
}

const _TextArea = forwardRef(TextArea);
export { _TextArea as TextArea };
