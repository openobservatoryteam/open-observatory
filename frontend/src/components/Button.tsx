import { Link } from '@tanstack/react-location';
import clsx from 'clsx';
import { AnchorHTMLAttributes, ButtonHTMLAttributes, DetailedHTMLProps, useRef } from 'react';
import { AriaButtonProps, useButton, useFocusVisible } from 'react-aria';

const styles = (isPressed = false) =>
  clsx(
    'bg-gray-300 px-8 py-2.5 rounded-3xl text-black',
    'disabled:cursor-not-allowed disabled:brightness-75',
    isPressed && 'brightness-90',
  );

type ButtonProps = { unstyled?: boolean } & (
  | ({ href: string } & DetailedHTMLProps<AnchorHTMLAttributes<HTMLAnchorElement>, HTMLAnchorElement>)
  | (Omit<AriaButtonProps<'button'>, 'href'> &
      DetailedHTMLProps<ButtonHTMLAttributes<HTMLButtonElement>, HTMLButtonElement>)
);

function Button({ unstyled, ...props }: ButtonProps) {
  const { isFocusVisible } = useFocusVisible();
  if ('href' in props) {
    return <Link className={clsx(!unstyled && styles())} role="button" to={props.href} {...props} />;
  }
  const ref = useRef<HTMLButtonElement>(null);
  const { buttonProps, isPressed } = useButton(props, ref);
  return (
    <button
      className={clsx(
        !unstyled && styles(isPressed),
        'outline-none',
        isFocusVisible && 'focus:ring-4',
        props.className,
      )}
      {...buttonProps}
    >
      {props.children}
    </button>
  );
}

export default Button;
