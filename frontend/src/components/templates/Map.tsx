import leafletStylesheet from 'leaflet/dist/leaflet.css?inline';
import { Style } from 'react-head';
import { MapContainer, MapContainerProps, TileLayer } from 'react-leaflet';

import { CurrentPosition } from '@/components';
import { removeKeys } from '@/utils';

type MapProps = MapContainerProps;

export function Map(props: MapProps) {
  return (
    <>
      <Style type="text/css">{leafletStylesheet}</Style>
      <MapContainer
        attributionControl={false}
        center={[48.866667, 2.333333]}
        zoom={10}
        {...removeKeys(props, ['attributionControl', 'center', 'zoom'])}
      >
        <TileLayer url=" https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png" />
        <CurrentPosition />
      </MapContainer>
    </>
  );
}
