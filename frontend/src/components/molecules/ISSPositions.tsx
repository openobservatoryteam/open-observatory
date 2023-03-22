import { useQuery } from '@tanstack/react-query';
import bezier from '@turf/bezier-spline';
import { lineString } from '@turf/helpers';
import { CircleMarker, GeoJSON, Tooltip } from 'react-leaflet';

import { findISSPositions } from '~/api';

function ISSPositions() {
  const { data } = useQuery({ queryFn: findISSPositions, queryKey: ['iss-positions'] });
  if (!data) return null;
  const line = lineString(data.sort((a, b) => a.longitude - b.longitude).map((p) => [p.longitude, p.latitude]));
  const current = data.find((d) => d.current);
  return (
    <>
      <GeoJSON data={bezier(line)} />
      {current && (
        <CircleMarker
          center={[current.latitude, current.longitude]}
          color="#0000FF"
          fill
          fillColor="#0000FF"
          fillOpacity={1}
          radius={4}
        >
          <Tooltip>Station spatiale internationale</Tooltip>
        </CircleMarker>
      )}
    </>
  );
}

export { ISSPositions };
