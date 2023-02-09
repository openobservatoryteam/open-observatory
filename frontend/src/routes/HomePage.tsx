import { Link } from '@tanstack/react-location';

function HomePage() {
  return (
    <>
      <h1>Accueil</h1>
      <Link to="/login">Se connecter</Link>
    </>
  );
}

export default HomePage;
