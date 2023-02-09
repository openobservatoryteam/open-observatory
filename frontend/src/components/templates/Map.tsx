import { MapContainer, TileLayer } from 'react-leaflet';

import { CurrentPosition } from '@/components';

export function Map() {
  return (
    <MapContainer
      className="h-[calc(100vh-19em)] md:h-[calc(100vh-21.6em)] relative top-4"
      center={[48.866667, 2.333333]}
      zoom={10}
      attributionControl={false}
    >
      <TileLayer url="https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png" />
      <CurrentPosition />
    </MapContainer>
  );
}
