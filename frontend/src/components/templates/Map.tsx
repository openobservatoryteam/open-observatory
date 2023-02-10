import leafletStylesheet from 'leaflet/dist/leaflet.css?inline';
import { Style } from 'react-head';
import { MapContainer, TileLayer } from 'react-leaflet';

import { CurrentPosition } from '@/components';

export function Map() {
  console.log(leafletStylesheet);
  return (
    <>
      <Style type="text/css">{leafletStylesheet}</Style>
      <MapContainer
        attributionControl={false}
        center={[48.866667, 2.333333]}
        className="h-[calc(100vh-19em)] md:h-[calc(100vh-21.6em)] relative top-4"
        zoom={10}
      >
        <TileLayer url="https://cartodb-basemaps-a.global.ssl.fastly.net/dark_all/{z}/{x}/{y}.png" />
        <CurrentPosition />
      </MapContainer>
    </>
  );
}
