import { useEffect, useState } from 'react';
import { Circle, useMap, CircleMarker, Tooltip } from 'react-leaflet';

import { usePosition } from '@/hooks';

export function CurrentPosition() {
  const [hasCentered, setCentered] = useState(false);
  const map = useMap();
  const position = usePosition();
  useEffect(() => {
    if (position !== null && !hasCentered) {
      map.flyTo(position, 12);
      setCentered(true);
    }
  }, [position]);
  return position ? (
    <>
      <Circle center={position} radius={25000} color="#444444" />
      <CircleMarker center={position} color="#990000" fill fillColor="#990000" fillOpacity={1} radius={2}>
        <Tooltip direction="top">Vous êtes ici</Tooltip>
      </CircleMarker>
    </>
  ) : null;
}
