import leafletStylesheet from 'leaflet/dist/leaflet.css?inline';
import { Style } from 'react-head';
import { MapContainer, MapContainerProps, Marker, Popup, TileLayer } from 'react-leaflet';

import { CurrentPosition } from '@/components';
import { removeKeys } from '@/utils';
import { LatLngExpression } from 'leaflet';

type MarkerProps = {
  coords: LatLngExpression;
  description: string;
};

type MapProps = {
  marker?: MarkerProps | undefined;
} & MapContainerProps;

export function Map({ marker, ...props }: MapProps) {
  return (
    <>
      <Style type="text/css">{leafletStylesheet}</Style>
      <MapContainer
        attributionControl={false}
        center={props.center ? props.center : [48.866667, 2.333333]}
        zoom={10}
        {...removeKeys(props, ['attributionControl', 'center', 'zoom'])}
      >
        <TileLayer url=" https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png" />
        <CurrentPosition centeredOnUser={!marker} />
        {marker && (
          <Marker position={marker.coords}>
            <Popup>{marker.description}</Popup>
          </Marker>
        )}
      </MapContainer>
    </>
  );
}
