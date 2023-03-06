import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { clsx } from 'clsx';
import { ForwardedRef, forwardRef, useState } from 'react';
import { AriaTextFieldOptions, useTextField } from 'react-aria';

import { Button } from '@/components';
import { useForwardedRef } from '@/hooks';

type TextInputProps = AriaTextFieldOptions<'input'> & { className?: string; withVisibilityToggle?: boolean };

function TextInput({ withVisibilityToggle, ...props }: TextInputProps, forwardedRef?: ForwardedRef<HTMLInputElement>) {
  const ref = useForwardedRef(forwardedRef);
  const [isVisible, setVisible] = useState(false);
  const { errorMessageProps, inputProps } = useTextField(props, ref);
  const { errorMessage, type } = props;
  return (
    <div className={props.className}>
      <div className="bg-white flex px-4 py-2 rounded-3xl text-black w-full">
        <input
          {...inputProps}
          className={clsx('bg-inherit outline-none w-full')}
          ref={ref}
          type={isVisible ? 'text' : type}
        />
        {withVisibilityToggle && (
          <Button onPress={() => setVisible(!isVisible)} unstyled>
            <FontAwesomeIcon color="gray" icon={isVisible ? faEyeSlash : faEye} />
          </Button>
        )}
      </div>
      {errorMessage && (
        <p className="text-center text-red-500" {...errorMessageProps}>
          {errorMessage}
        </p>
      )}
    </div>
  );
}

const _TextInput = forwardRef(TextInput);
export { _TextInput as TextInput };
