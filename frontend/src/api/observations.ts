import { Observation, ObservationVisibility, ObservationVote, ObservationWithDetails, SearchResults } from './@types';
import client from './client';

type FindAllObservationsNearbyData = { lng: number; lat: number; radius: number };
export const findAllObservationsNearby = ({ lng, lat, radius }: FindAllObservationsNearbyData) =>
  client
    .get('observations/nearby', { searchParams: { lng, lat, radius: radius.toFixed(2) } })
    .then((r) => r.json<Observation[]>());

export const findObservationById = (id: string) =>
  client.get('observations/' + id).then((r) => r.json<ObservationWithDetails>());

export type CreateObservationData = {
  celestialBodyId: number;
  description?: string;
  lng: number;
  lat: number;
  orientation: number;
  visibility: ObservationVisibility;
  timestamp: string;
};
export const createObservation = (json: CreateObservationData) =>
  client.post('observations', { json }).then((r) => r.json<ObservationWithDetails>());

type PutVoteData = { id: string; vote: ObservationVote };
export const putVote = ({ id, vote }: PutVoteData) =>
  client.put(`observations/${id}/vote`, { json: { vote } }).then(() => null);

type findAllObservationData = { page: number; itemsPerPage: number };
export const findAllObservation = ({ page, itemsPerPage }: findAllObservationData) =>
  client
    .get(`observations?page=${page}&itemsPerPage=${itemsPerPage}`)
    .then((r) => r.json<SearchResults<Observation>>());
