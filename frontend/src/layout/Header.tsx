import { faDoorOpen } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from '@tanstack/react-location';

import { Logo } from '~/assets';
import { Button, Text, Title } from '~/components';
import { useAuthentication } from '~/providers';

export function Header() {
  const { isLoggedIn, logout, user } = useAuthentication();
  return (
    <header className="flex items-center justify-center mx-auto my-2 px-5 md:px-10 md:justify-between">
      <Link to="/">
        <Title className="hidden">Open Observatory</Title>
        <Logo className="mx-auto sm:ml-0 w-96 sm:w-64 max-w-full" />
      </Link>
      {isLoggedIn && (
        <div className="flex items-center gap-4 ml-5">
          <Text as="span" className="hidden sm:inline">
            {user?.username}
          </Text>
          <Button
            aria-label="Se déconnecter"
            className="p-3 sm:p-3.5 justify-self-end"
            onPress={() => logout.mutate()}
            rounded
            title="Se déconnecter"
          >
            <FontAwesomeIcon icon={faDoorOpen} />
          </Button>
          {user?.type === 'ADMIN' && (
            <Button as={Link} to="/admin/celestial-bodies">
              ADMINISTRATION
            </Button>
          )}
        </div>
      )}
    </header>
  );
}
