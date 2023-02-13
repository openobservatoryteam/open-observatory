import leafletStylesheet from 'leaflet/dist/leaflet.css?inline';
import { Style } from 'react-head';
import { MapContainer, MapContainerProps, TileLayer } from 'react-leaflet';

import { CurrentPosition } from '@/components';
import { removeKeys } from '@/utils';

type MapProps = {
  noFly?: boolean;
  withoutNotificationCircle?: boolean;
} & MapContainerProps;

export function Map({ center, children, noFly, withoutNotificationCircle, ...props }: MapProps) {
  return (
    <>
      <Style type="text/css">{leafletStylesheet}</Style>
      <MapContainer
        attributionControl={false}
        center={center ?? [48.866667, 2.333333]}
        zoom={10}
        {...removeKeys(props, ['attributionControl', 'zoom'])}
      >
        <TileLayer url=" https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png" />
        <CurrentPosition noFly={noFly} withoutNotificationCircle={withoutNotificationCircle} />
        {children}
      </MapContainer>
    </>
  );
}
