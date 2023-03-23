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
  const line = lineString(data.sort((a, b) => a.longitude - b.longitude).map((p) => [p.longitude, p.latitude]));
  const current = data.find((d) => d.current);
  return (
    <>
      <GeoJSON data={bezier(line)} style={{ color: 'gray' }} />
      {current && (
        <Marker icon={satelliteIcon} position={[current.latitude, current.longitude]}>
          <Tooltip>{t('iss.tooltip')}</Tooltip>
        </Marker>
      )}
    </>
  );
}

export { ISSPositions };
