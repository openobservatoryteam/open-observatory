import { useQuery } from '@tanstack/react-query';
import { useEffect } from 'react';
import { Marker, useMap } from 'react-leaflet';

import { observations } from '@/api';
import { useNavigate } from '@tanstack/react-location';

function NearbyObservations() {
  const map = useMap();
  const navigate = useNavigate();
  const nearbyObservations = useQuery({
    queryFn: () => observations.findNearby(map.getCenter()),
    queryKey: ['observations', 'nearby'],
  });
  useEffect(() => {
    const updateObservations = () => nearbyObservations.refetch();
    map.addEventListener('moveend', updateObservations);
    return () => void map.removeEventListener('moveend', updateObservations);
  }, []);
  return (
    nearbyObservations.data?.map((o) => (
      <Marker
        key={o.id}
        eventHandlers={{ click: () => navigate({ to: `/observations/${o.id}` }) }}
        position={{ lng: o.longitude, lat: o.latitude }}
      />
    )) ?? null
  );
}

export { NearbyObservations };
