import { Link } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';

import { Button, ISSPositions, Map, NearbyObservations } from '~/components';
import { Header } from '~/layout';
import { useAuthentication } from '~/providers';

function HomePage() {
  const { isLoggedIn, user } = useAuthentication();
  return (
    <>
      <DocumentTitle>Accueil – Open Observatory</DocumentTitle>
      <Header className="h-[7vh] my-[1vh]" />
      <div className="h-[10vh] gap-x-1 flex max-w-screen-lg mx-auto my-[0.5vh] px-1 md:h-[8vh] sm:gap-x-16 sm:px-16">
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? '/report-observation' : '/login'}>
          Nouvelle observation
        </Button>
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? `/users/${user!.username}` : '/login'}>
          {isLoggedIn ? 'Mon profil' : 'Se connecter'}
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
