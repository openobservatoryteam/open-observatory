import { LatLng } from 'leaflet';
import { useEffect, useMemo, useRef, useState } from 'react';
import { Marker, Tooltip, useMap } from 'react-leaflet';

type MarkerInputProps = {
  onMove: (position: LatLng) => unknown;
};

function MarkerInput({ onMove }: MarkerInputProps) {
  const map = useMap();
  const [center, setCenter] = useState<LatLng>(map.getCenter());
  useEffect(() => {
    const formUpdater = () => onMove(map.getCenter());
    const markerUpdater = () => setCenter(map.getCenter());
    map.addEventListener('move', markerUpdater);
    map.addEventListener('moveend', formUpdater);
    return () => {
      map.removeEventListener('moveend', formUpdater);
      map.removeEventListener('move', markerUpdater);
    };
  });
  return (
    <Marker position={center}>
      <Tooltip direction="top">Faites glisser la carte pour définir la position de l&apos;observation.</Tooltip>
    </Marker>
  );
}

export { MarkerInput };
