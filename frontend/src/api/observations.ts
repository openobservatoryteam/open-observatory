import client from './client';
import { Observation, ObservationDetail } from './types';

type FindNearbyObservationsData = { lng: number; lat: number };
export const findNearby = ({ lng, lat }: FindNearbyObservationsData) =>
  client.get('observations/nearby', { searchParams: { lng, lat } }).then((response) => response.json<Observation[]>());

export const findById = (id: string) => client.get('observations/' + id).then((r) => r.json<ObservationDetail>());

export const visibilityOptions = [
  { name: 'Bien visible', value: 'CLEARLY_VISIBLE' },
  { name: 'Visible', value: 'VISIBLE' },
  { name: 'Légèrement visible', value: 'SLIGHTLY_VISIBLE' },
  { name: 'Difficilement visible', value: 'BARELY_VISIBLE' },
] as const;

export type CreateObservationData = {
  description: string;
  celestialBodyId: number;
  lat: number;
  lng: number;
  orientation: number;
  timestamp: string;
  visibility: (typeof visibilityOptions)[number]['value'];
};
export const create = (json: CreateObservationData) =>
  client.post('observations', { json }).then((r) => r.json<Observation>());

type VoteType = 'UPVOTE' | 'DOWNVOTE' | null;
type VoteRequest = { id: string; vote: VoteType };
export const vote = ({ id, vote }: VoteRequest) => client.put(`observations/${id}/vote`, { json: { vote } });
