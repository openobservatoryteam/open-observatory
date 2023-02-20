import { Link } from '@tanstack/react-location';

import { Logo } from '@/assets';
import { Button, Text, Title } from '@/components';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faDoorOpen } from '@fortawesome/free-solid-svg-icons';
import { useAuthentication } from '@/providers';

export function Header() {
  const authentication = useAuthentication();
  console.log(authentication);
  return (
    <header className="flex items-center justify-between mx-auto my-2 px-5 md:px-10">
      <Link to="/">
        <Title className="hidden">Open Observatory</Title>
        <Logo className="mx-auto sm:ml-0 w-40 sm:w-64" />
      </Link>
      {authentication.isLoggedIn && (
        <div className="flex items-center gap-4">
          <Text as="span" className="hidden sm:inline">
            {authentication.user?.username}
          </Text>
          <Button
            aria-label="Se déconnecter"
            className="p-3 sm:p-4 justify-self-end"
            onPress={() => authentication.logout.mutate()}
            rounded
            title="Se déconnecter"
          >
            <FontAwesomeIcon icon={faDoorOpen} />
          </Button>
        </div>
      )}
    </header>
  );
}
