import { clsx } from 'clsx';

export function Footer() {
  return (
    <footer
      className={clsx(
        'bottom-0 fixed h-screen w-full -z-10',
        'bg-mobile sm:bg-desktop sm:bg-auto bg-bottom bg-contain bg-no-repeat',
      )}
    />
  );
}

export default Footer;
