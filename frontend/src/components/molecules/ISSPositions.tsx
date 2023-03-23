import { useQuery } from '@tanstack/react-query';
import bezier from '@turf/bezier-spline';
import { lineString } from '@turf/helpers';
import dayjs from 'dayjs';
import { icon } from 'leaflet';
import { useTranslation } from 'react-i18next';
import { CircleMarker, GeoJSON, Marker, Tooltip } from 'react-leaflet';

import { ISSPosition, findISSPositions } from '~/api';
import satellite from '~/assets/satellite.svg';

const satelliteIcon = icon({
  iconUrl: satellite,

  iconSize: [32, 32],
  iconAnchor: [16, 16],
  popupAnchor: [16, 32],
});

function ISSPositions() {
  const { data, dataUpdatedAt } = useQuery({
    queryFn: findISSPositions,
    queryKey: ['iss-positions'],
    refetchInterval: 30e3,
  });
  const { t } = useTranslation();
  if (typeof data === 'undefined') return null;
  const [{ longitude: fstLongitude }] = data;
  const breakpointIdx = data.findIndex((d) => d.longitude < fstLongitude);
  const [leftPoints, rightPoints] = [data.slice(0, breakpointIdx), data.slice(breakpointIdx)];
  const getPoint = ({ current, latitude, longitude, timestamp }: ISSPosition) =>
    current ? (
      <Marker icon={satelliteIcon} position={[latitude, longitude]}>
        <Tooltip>{t('iss.currentPosition')}</Tooltip>
      </Marker>
    ) : (
      <CircleMarker
        center={[latitude, longitude]}
        color="#222222"
        fill
        fillColor="#222222"
        fillOpacity={1}
        key={timestamp}
        radius={2}
      >
        <Tooltip direction="top">
          {t(`iss.${dayjs().isAfter(timestamp) ? 'pastPosition' : 'futurePosition'}`, {
            time: dayjs(timestamp).format('HH:mm:ss'),
          })}
        </Tooltip>
      </CircleMarker>
    );
  const drawLine = (data: ISSPosition[]) =>
    data.length > 1 ? (
      <>
        <GeoJSON
          data={bezier(lineString(data.map(({ latitude, longitude }) => [longitude, latitude])))}
          key={dataUpdatedAt}
          style={{ color: 'gray' }}
        />
        {data.map((p) => getPoint(p))}
      </>
    ) : (
      getPoint(data[0]!)
    );
  return (
    <>
      {leftPoints.length > 0 && drawLine(leftPoints)}
      {rightPoints.length > 0 && drawLine(rightPoints)}
    </>
  );
}

export { ISSPositions };
