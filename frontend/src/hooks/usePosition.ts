import { useEffect, useState } from 'react';

type Position = { lat: number; lng: number };

export function usePosition() {
  const [position, setPosition] = useState<Position | null>(null);
  useEffect(() => {
    const watchId = navigator.geolocation.watchPosition(
      ({ coords: { latitude, longitude } }) => setPosition({ lat: latitude, lng: longitude }),
      () => void 0,
      { enableHighAccuracy: true, maximumAge: 120e3, timeout: 30e3 },
    );
    return () => navigator.geolocation.clearWatch(watchId);
  }, []);
  return position;
}
