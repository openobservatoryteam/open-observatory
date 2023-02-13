import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType, ReactNode, useRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

import { removeKeys } from '@/utils';

type ButtonColor = 'lightGray' | 'darkGray' | 'red' | 'white';
type ButtonProps<C extends ElementType> = AriaButtonProps<C> &
  ComponentPropsWithoutRef<C> & {
    as?: C;
    children: ReactNode;
    color?: ButtonColor;
    rounded?: boolean;
    unstyled?: boolean;
  };

const classes = (
  isFocusVisible: boolean,
  isPressed: boolean,
  color?: ButtonColor,
  rounded?: boolean,
  unstyled?: boolean,
) => {
  const colors: Record<ButtonColor, string> = {
    darkGray: 'bg-[#333C47] ring-[#999EA3] text-white',
    lightGray: 'bg-[#D9D9D9] ring-[#6d6d6d] text-black',
    red: 'bg-[#B33A3A] ring-[#D87979] text-white',
    white: 'bg-white ring-neutral-400 text-black',
  };
  return clsx(
    !unstyled && [
      'flex items-center justify-center px-8 py-2.5 select-none text-center',
      'disabled:cursor-not-allowed disabled:brightness-75',
      rounded ? 'rounded-full' : 'rounded-3xl',
      isPressed && 'brightness-90',
      colors[color ?? 'lightGray'],
    ],
    'outline-none',
    isFocusVisible && 'focus:ring-2',
  );
};

const excludedAriaHandlers = [
  'onBlur',
  'onFocus',
  'onFocusChange',
  'onKeyDown',
  'onKeyUp',
  'onPress',
  'onPressChange',
  'onPressEnd',
  'onPressStart',
  'onPressUp',
] as const;

function Button<C extends ElementType = 'button'>({
  as,
  className,
  color,
  rounded,
  unstyled,
  ...props
}: ButtonProps<C>) {
  const Component = as ?? 'button';
  const ref = useRef(null);
  const { buttonProps, isPressed } = useButton(props, ref);
  const { isFocusVisible } = useFocusVisible();
  const finalProps = { ...buttonProps, ...removeKeys(props, excludedAriaHandlers) };
  return (
    <Component
      className={clsx(classes(isFocusVisible, isPressed, color, rounded, unstyled), className)}
      {...finalProps}
    />
  );
}

export { Button };
