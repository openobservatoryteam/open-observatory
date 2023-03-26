import { useNavigate } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';
import { Marker, useMap } from 'react-leaflet';

import { findAllObservationsNearby } from '~/api';

const EARTH_CIRCUMFERENCE = 40075;

const getRadius = (latitude: number, zoom: number) =>
  (Math.max(window.innerWidth, window.innerHeight) * (EARTH_CIRCUMFERENCE * Math.cos(latitude))) /
  Math.pow(2, zoom + 8);

function NearbyObservations() {
  const map = useMap();
  const navigate = useNavigate();
  const nearbyObservations = useQuery({
    queryFn: () =>
      findAllObservationsNearby({
        radius: getRadius(map.getCenter().lat, map.getZoom()) / 2,
        ...map.getCenter(),
      }),
    queryKey: ['observations', 'nearby'],
  });
  useEffect(() => {
    const updateObservations = () => nearbyObservations.refetch();
    map.addEventListener('moveend', updateObservations);
    return () => {
      map.removeEventListener('moveend', updateObservations);
    };
  }, []);
  return (
    <>
      {nearbyObservations.data?.map((o) => (
        <Marker
          key={o.id}
          eventHandlers={{ click: () => navigate({ to: `/observations/${o.id}` }) }}
          position={{ lng: o.longitude, lat: o.latitude }}
        />
      ))}
    </>
  );
}

export { NearbyObservations };
