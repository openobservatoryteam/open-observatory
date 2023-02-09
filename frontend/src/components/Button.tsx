import clsx from 'clsx';
import { ComponentPropsWithoutRef, ElementType, ReactNode, useRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

const styles = (isPressed = false) =>
  clsx(
    'bg-gray-300 px-8 py-2.5 rounded-3xl text-black',
    'disabled:cursor-not-allowed disabled:brightness-75',
    isPressed && 'brightness-90',
  );

type ButtonProps<C extends ElementType> =
  | { as?: C; children: ReactNode; unstyled?: boolean } & AriaButtonProps<C> & ComponentPropsWithoutRef<C>;

export const Button = <C extends ElementType>({
  as,
  className,
  children,
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
      className={clsx(!unstyled && styles(isPressed), 'outline-none', isFocusVisible && 'focus:ring-4', className)}
      {...props}
      {...buttonProps}
    >
      {children}
    </Component>
  );
};
