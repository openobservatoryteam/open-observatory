import client from './client';
import { Observation, ObservationDetail } from './types';

type FindNearbyObservationsData = { lng: number; lat: number };
export const findNearby = ({ lng, lat }: FindNearbyObservationsData) =>
  client.get('observations/nearby', { searchParams: { lng, lat } }).then((response) => response.json<Observation[]>());

export const findById = (id: string) => client.get('observations/' + id).then((r) => r.json<ObservationDetail>());

type VoteType = 'UPVOTE' | 'DOWNVOTE' | null;
type VoteRequest = { id: string; vote: VoteType };
export const vote = ({ id, vote }: VoteRequest) => client.put(`observations/${id}/vote`, { json: { vote } });
