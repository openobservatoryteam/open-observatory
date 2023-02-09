import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType, ReactNode, useRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

type ButtonColor = 'lightGray' | 'darkGray' | 'red';

const styles = (color: ButtonColor, isPressed = false) =>
  clsx(
    'px-8 py-2.5 rounded-3xl',
    'disabled:cursor-not-allowed disabled:brightness-75',
    isPressed && 'brightness-90',
    color === 'lightGray'
      ? 'bg-gray-300 text-black'
      : color === 'darkGray'
      ? 'bg-[#333C47] text-white'
      : color === 'red'
      ? 'bg-[#880000] text-white'
      : '',
  );

type ButtonProps<C extends ElementType> =
  | { as?: C; children: ReactNode; color?: ButtonColor; unstyled?: boolean } & AriaButtonProps<C> &
      ComponentPropsWithoutRef<C>;

export const Button = <C extends ElementType>({
  as,
  className,
  children,
  color = 'lightGray',
  onPress,
  unstyled,
  ...props
}: ButtonProps<C>) => {
  const Component = as ?? 'button';
  const ref = useRef(null);
  const { buttonProps, isPressed } = useButton({ onPress, ...props }, ref);
  const { isFocusVisible } = useFocusVisible();
  return (
    <Component
      className={clsx(
        !unstyled && styles(color, isPressed),
        'outline-none',
        isFocusVisible && 'focus:ring-4',
        'block',
        'flex items-center justify-center text-center',
        className,
      )}
      {...props}
      {...buttonProps}
    >
      {children}
    </Component>
  );
};
