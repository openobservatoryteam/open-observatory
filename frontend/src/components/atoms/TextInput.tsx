import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { ChangeEvent, ComponentPropsWithRef, ForwardedRef, forwardRef, RefObject, useState } from 'react';
import { AriaTextFieldOptions, useTextField } from 'react-aria';

import { Button } from '@/components';

type TextInputProps =
  | { withVisibilityToggle?: boolean } & Omit<AriaTextFieldOptions<'input'>, 'onChange'> &
      ComponentPropsWithRef<'input'>;

const classes = () => clsx('bg-white flex px-4 py-2 rounded-3xl text-black w-full');

function TextInput(
  { onChange, type, withVisibilityToggle = type === 'password', ...props }: TextInputProps,
  ref: ForwardedRef<HTMLInputElement>,
) {
  const [isVisible, setVisible] = withVisibilityToggle ? useState(false) : [false, () => void 0];
  const { errorMessageProps, inputProps } = useTextField(
    {
      onChange: (value) => onChange?.({ target: { value } } as ChangeEvent<HTMLInputElement>),
      type,
      ...props,
    },
    ref as RefObject<HTMLInputElement>,
  );
  return (
    <div className={props.className}>
      <div className={classes()}>
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
      {props.errorMessage && (
        <p className="text-center text-red-500" {...errorMessageProps}>
          {props.errorMessage}
        </p>
      )}
    </div>
  );
}

const _TextInput = forwardRef(TextInput);
export { _TextInput as TextInput };
