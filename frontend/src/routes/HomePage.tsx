import { Link } from '@tanstack/react-location';

import { Button } from '@/components';

function HomePage() {
  return (
    <>
      <h1>Accueil</h1>
      <Button as={Link} to="/login">
        Ici
      </Button>
    </>
  );
}

export default HomePage;
