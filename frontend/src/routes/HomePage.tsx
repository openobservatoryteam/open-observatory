import { Link } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';
import { useTranslation } from 'react-i18next';

import { Button, ISSPositions, Map, NearbyObservations } from '~/components';
import { Header } from '~/layout';
import { useAuthentication } from '~/providers';

function HomePage() {
  const { isLoggedIn, user } = useAuthentication();
  const { t } = useTranslation();
  return (
    <>
      <DocumentTitle>{t('document.title.home')}</DocumentTitle>
      <Header />
      <div className="flex justify-around items-center my-2">
        <Button
          as={Link}
          className="h-16 w-44 md:w-96"
          color="darkGray"
          to={isLoggedIn ? '/report-observation' : '/login'}
        >
          {t('observation.new')}
        </Button>
        <Button
          as={Link}
          className="h-16 w-44 md:w-96"
          color="darkGray"
          to={isLoggedIn ? `/users/${user!.username}` : '/login'}
        >
          {isLoggedIn ? t('users.profil') : t('users.login')}
        </Button>
      </div>
      <Map className="h-[calc(100vh-8.1rem)] md:h-[calc(100vh-9.7rem)]">
        <ISSPositions />
        <NearbyObservations />
      </Map>
    </>
  );
}

export default HomePage;
