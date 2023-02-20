import { Link } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';

import { Button, Map } from '@/components';
import { Header } from '@/layout';

function HomePage() {
  return (
    <>
      <DocumentTitle>Accueil – Open Observatory</DocumentTitle>
      <Header />
      <div className="flex justify-around items-center my-2">
        <Button as={Link} className="h-16 w-44 md:w-96" color="darkGray" to="/login">
          Nouvelle observation
        </Button>
        <Button as={Link} className="h-16 w-44 md:w-96" color="darkGray" to="/login">
          Se connecter
        </Button>
      </div>
      <Map className="h-[calc(100vh-8.1rem)] md:h-[calc(100vh-9.7rem)]" />
    </>
  );
}

export default HomePage;
