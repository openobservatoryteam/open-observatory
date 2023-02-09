import { Link } from '@tanstack/react-location';

import { Button } from '@/components';
import Header from '@/layout/Header';
import { MapContainer, TileLayer } from 'react-leaflet';

function HomePage() {
  return (
    <>
      <Header />
      <div className="flex justify-around items-center my-5 md:my-7">
        <Button
          as={Link}
          to="/login"
          className="w-40 sm:text-[10px] bg-[#333C47] md:w-96 h-20 text-white text-[14px] md:text-[20px] "
        >
          Nouvelle observation
        </Button>
        <Button as={Link} to="/login" className="w-40 md:w-96 h-20 bg-[#333C47] text-white text-[14px] md:text-[20px]">
          Se connecter
        </Button>
      </div>
      <MapContainer
        className="h-[calc(100vh-19em)] md:h-[calc(100vh-21.6em)] relative top-4"
        center={[51.505, -0.09]}
        zoom={13}
        attributionControl={false}
      >
        <TileLayer url="https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png" />
      </MapContainer>
    </>
  );
}

export default HomePage;
