import { Link } from '@tanstack/react-location';

import { Button, Map } from '@/components';
import Header from '@/layout/Header';

function HomePage() {
  return (
    <>
      <Header />
      <div className="flex justify-around items-center my-5 md:my-7">
        <Button
          as={Link}
          className="w-40 sm:text-[10px] md:w-96 h-20 text-[14px] md:text-[20px]"
          color="darkGray"
          to="/login"
        >
          Nouvelle observation
        </Button>
        <Button as={Link} className="w-40 md:w-96 h-20 text-[14px] md:text-[20px]" color="darkGray" to="/login">
          Se connecter
        </Button>
      </div>
      <Map />
    </>
  );
}

export default HomePage;
