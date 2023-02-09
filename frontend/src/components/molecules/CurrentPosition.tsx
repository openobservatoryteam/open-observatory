import { useEffect, useState } from 'react';
import { Marker, Popup, useMap } from 'react-leaflet';

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
    <Marker position={position}>
      <Popup>C&apos;EST MOI</Popup>
    </Marker>
  ) : null;
}
