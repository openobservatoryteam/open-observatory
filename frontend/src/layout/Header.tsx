import { Link } from '@tanstack/react-location';

import Logo from '@/assets/Logo';
import Title from '@/components/Title';

function Header() {
  return (
    <header className="mx-auto px-5 pt-6 md:px-10 md:pt-10">
      <Link to="/">
        <Title className="hidden">Open Observatory</Title>
        <Logo className="mx-auto sm:ml-0 w-64" />
      </Link>
    </header>
  );
}

export default Header;
