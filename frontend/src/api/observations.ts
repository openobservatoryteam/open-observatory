import client from './client';
import { Observation } from './types';

type FindNearbyObservationsData = { lng: number; lat: number };
export const findNearby = ({ lng, lat }: FindNearbyObservationsData) =>
  client.get('observations/nearby', { searchParams: { lng, lat } }).then((response) => response.json<Observation[]>());
