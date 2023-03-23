import { useQuery } from '@tanstack/react-query';
import bezier from '@turf/bezier-spline';
import { lineString } from '@turf/helpers';
import { icon } from 'leaflet';
import { useTranslation } from 'react-i18next';
import { GeoJSON, Marker, Tooltip } from 'react-leaflet';

import { findISSPositions } from '~/api';
import satellite from '~/assets/satellite.svg';

const satelliteIcon = icon({
  iconUrl: satellite,

  iconSize: [32, 32],
  iconAnchor: [16, 16],
  popupAnchor: [16, 32],
});

function ISSPositions() {
  const { t } = useTranslation();
  const { data } = useQuery({ queryFn: findISSPositions, queryKey: ['iss-positions'] });
  if (!data) return null;
  const [firstItem] = data;
  const breakpointIdx = data.findIndex((d) => d.longitude < firstItem.longitude);
  const lines =
    breakpointIdx !== -1
      ? [
          lineString(data.slice(0, breakpointIdx - 1).map((d) => [d.longitude, d.latitude])),
          lineString(data.slice(breakpointIdx).map((d) => [d.longitude, d.latitude])),
        ]
      : [lineString(data.map((d) => [d.longitude, d.latitude]))];
  const current = data.find((d) => d.current);
  return (
    <>
      {lines.map((line) => (
        <GeoJSON data={bezier(line)} key={line.id} style={{ color: 'gray' }} />
      ))}
      {current && (
        <Marker icon={satelliteIcon} position={[current.latitude, current.longitude]}>
          <Tooltip>{t('iss.tooltip')}</Tooltip>
        </Marker>
      )}
    </>
  );
}

export { ISSPositions };
