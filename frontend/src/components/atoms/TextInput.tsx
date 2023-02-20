import { faEye, faEyeSlash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { ChangeEvent, ComponentPropsWithRef, ElementType, forwardRef, useRef, useState } from 'react';
import { AriaTextFieldOptions, useTextField } from 'react-aria';

import { Button } from '@/components';
import { AsProps, PolymorphicRef } from '@/types';

type TextInputProps<C extends ElementType> = AsProps<C> &
  Omit<AriaTextFieldOptions<'input'>, 'onChange'> &
  ComponentPropsWithRef<'input'> & { withVisibilityToggle?: boolean };

const classes = () => clsx('bg-white flex px-4 py-2 rounded-3xl text-black w-full');

function TextInput<C extends ElementType = 'input'>(
  { as, onChange, type, withVisibilityToggle = type === 'password', ...props }: TextInputProps<C>,
  ref: PolymorphicRef<C>,
) {
  ref ??= useRef(null);
  const Component = as ?? 'input';
  const [isVisible, setVisible] = withVisibilityToggle ? useState(false) : [false, () => void 0];
  const { errorMessageProps, inputProps } = useTextField(
    {
      onChange: (value) => onChange?.({ target: { value } } as ChangeEvent<HTMLInputElement>),
      type,
      ...props,
    },
    ref,
  );
  return (
    <div className={props.className}>
      <div className={classes()}>
        <Component
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
