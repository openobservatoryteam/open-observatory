import { clsx } from 'clsx';
import { ComponentPropsWithoutRef, ElementType, ForwardedRef, ReactNode, forwardRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

import { useForwardedRef } from '~/hooks';
import { AsProps } from '~/types';
import { removeKeys } from '~/utils';

type ButtonColor = 'lightGray' | 'darkGray' | 'red' | 'transparent' | 'white';
type ButtonProps<C extends ElementType> = AsProps<C> &
  AriaButtonProps<C> &
  ComponentPropsWithoutRef<C> & {
    children: ReactNode;
    color?: ButtonColor;
    fullWidth?: boolean;
    rounded?: boolean;
    unstyled?: boolean;
  };

const classes = (
  isFocusVisible: boolean,
  isPressed: boolean,
  color?: ButtonColor,
  fullWidth?: boolean,
  rounded?: boolean,
  unstyled?: boolean,
) => {
  const colors: Record<ButtonColor, string> = {
    darkGray: 'bg-[#333C47] ring-[#999EA3] text-white',
    lightGray: 'bg-[#D9D9D9] ring-[#6d6d6d] text-black',
    red: 'bg-[#B33A3A] ring-[#D87979] text-white',
    transparent: 'bg-transparent hover:bg-opacity-20 hover:bg-white ring-white',
    white: 'bg-white ring-neutral-400 text-black',
  };
  return clsx(
    !unstyled && [
      'flex items-center justify-center select-none text-center',
      'disabled:cursor-not-allowed disabled:brightness-75',
      rounded ? 'rounded-full' : 'px-8 py-2.5 rounded-3xl',
      isPressed && 'brightness-90',
      colors[color ?? 'lightGray'],
    ],
    'outline-none',
    fullWidth && 'w-full',
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

function Button<C extends ElementType = 'button'>(
  { as, className, color, fullWidth, rounded, unstyled, ...props }: ButtonProps<C>,
  forwardedRef?: ForwardedRef<Element>,
) {
  const Component = as ?? 'button';
  const ref = useForwardedRef(forwardedRef);
  const { buttonProps, isPressed } = useButton(props, ref);
  const { isFocusVisible } = useFocusVisible();
  const finalProps = { ...buttonProps, ...removeKeys(props, excludedAriaHandlers) };
  return (
    <Component
      className={clsx(classes(isFocusVisible, isPressed, color, fullWidth, rounded, unstyled), className)}
      {...finalProps}
    />
  );
}

const _Button = forwardRef(Button);
export { _Button as Button };
