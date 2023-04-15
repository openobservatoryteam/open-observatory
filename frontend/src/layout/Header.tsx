import { faArrowRightFromBracket } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from '@tanstack/react-location';
import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';

import logo from '~/assets/logo.svg';
import { Button, Text, Title } from '~/components';
import SwitchLang from '~/components/atoms/SwitchLang';
import { useAuthentication } from '~/providers';

type HeaderProps = ComponentPropsWithoutRef<'header'>;

export function Header({ className, ...props }: HeaderProps) {
  const { isLoggedIn, logout, user } = useAuthentication();
  return (
    <header className={clsx('flex items-center justify-center mx-auto px-5 sm:justify-between', className)} {...props}>
      <Link className="h-full" title="Accueil Open Observatory" to="/">
        <Title className="hidden">Open Observatory</Title>
        <img className="h-full" src={logo} alt="Logo Open Observatory" />
      </Link>
      <div className="flex gap-x-6">
        {isLoggedIn && (
          <div className="flex items-center gap-4">
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
              <FontAwesomeIcon icon={faArrowRightFromBracket} />
            </Button>
            {user?.type === 'ADMIN' && (
              <Button as={Link} className="hidden sm:block" to="/admin/celestial-bodies">
                ADMINISTRATION
              </Button>
            )}
          </div>
        )}
        <SwitchLang />
      </div>
    </header>
  );
}
