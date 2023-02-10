import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType, ReactNode, useRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

import { removeKeys } from '@/utils';

type ButtonColor = 'lightGray' | 'darkGray' | 'red';
type ButtonProps<C extends ElementType> = AriaButtonProps<C> &
  ComponentPropsWithoutRef<C> & { as?: C; children: ReactNode; color?: ButtonColor; unstyled?: boolean };

const styles = (isFocusVisible: boolean, isPressed: boolean, color?: ButtonColor, unstyled?: boolean) => {
  const colors: Record<ButtonColor, string> = {
    darkGray: 'bg-[#333C47] text-white',
    lightGray: 'bg-gray-300 text-black',
    red: 'bg-[#880000] text-white',
  };
  return clsx(
    !unstyled && [
      'flex items-center justify-center px-8 py-2.5 rounded-3xl text-center',
      'disabled:cursor-not-allowed disabled:brightness-75',
      isPressed && 'brightness-90',
      colors[color ?? 'lightGray'],
    ],
    'outline-none',
    isFocusVisible && 'focus:ring-4',
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

export const Button = <C extends ElementType>({ as, className, color, unstyled, ...props }: ButtonProps<C>) => {
  const Component = as ?? 'button';
  const ref = useRef(null);
  const { buttonProps, isPressed } = useButton(props, ref);
  const { isFocusVisible } = useFocusVisible();
  const finalProps = { ...buttonProps, ...removeKeys(props, excludedAriaHandlers) };
  return <Component className={clsx(styles(isFocusVisible, isPressed, color, unstyled), className)} {...finalProps} />;
};
