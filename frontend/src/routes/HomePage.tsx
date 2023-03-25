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
      <Header className="h-[7vh] my-[1vh]" />
      <div className="h-[10vh] gap-x-1 flex max-w-screen-lg mx-auto my-[0.5vh] px-1 md:h-[8vh] sm:gap-x-16 sm:px-16">
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? '/report-observation' : '/login'}>
          {t('observation.new')}
        </Button>
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? `/users/${user.username}` : '/login'}>
          {isLoggedIn ? t('users.profil') : t('users.login')}
        </Button>
      </div>
      <Map className="h-[80.5vh] md:h-[82.5vh]" minZoom={3} worldCopyJump>
        <ISSPositions />
        <NearbyObservations />
      </Map>
    </>
  );
}

export default HomePage;
