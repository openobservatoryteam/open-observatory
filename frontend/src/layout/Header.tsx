import { Link } from '@tanstack/react-location';

import { Logo } from '@/assets';
import { Title } from '@/components';

export function Header() {
  return (
    <header className="mx-auto my-2 px-5 md:px-10">
      <Link to="/">
        <Title className="hidden">Open Observatory</Title>
        <Logo className="mx-auto sm:ml-0 w-64" />
      </Link>
    </header>
  );
}
