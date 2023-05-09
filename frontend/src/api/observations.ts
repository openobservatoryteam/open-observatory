import { Observation, ObservationVisibility, ObservationVote, ObservationWithDetails, SearchResults } from './@types';
import client from './client';

type FindAllObservationsNearbyData = { longitude: number; latitude: number; radius: number };
export const findAllObservationsNearby = ({ longitude, latitude, radius }: FindAllObservationsNearbyData) =>
  client
    .get('observations/nearby', { searchParams: { longitude, latitude, radius: radius.toFixed(2) } })
    .then((r) => r.json<Observation[]>());

export const findObservationById = (id: string) =>
  client.get('observations/' + id).then((r) => r.json<ObservationWithDetails>());

export type CreateObservationData = {
  celestialBodyId: number;
  description?: string;
  longitude: number;
  latitude: number;
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

export type UpdateObservationData = { description: string };
export const updateObservation = ({ id, ...json }: { id: number } & UpdateObservationData) =>
  client.patch(`observations/${id}`, { json }).then((r) => r.json<ObservationWithDetails>());

export const deleteObservation = ({ id }: { id: number }) => client.delete(`observations/${id}`).then(() => null);
