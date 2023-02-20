import client from './client';
import { CelestialBody, SearchResults } from './types';

export const getAll = () =>
  client.get('celestial-bodies').then((response) => response.json<SearchResults<CelestialBody>>());

export type CreateCelestialBodyData = { name: string; image: string; validityTime: number };
export const create = (json: CreateCelestialBodyData) =>
  client.post('celestial-bodies', { json }).then((response) => response.json<CelestialBody>());

export type UpdateCelestialBodyData = { id: number; name?: string; image?: string; validityTime?: number };
export const update = ({ id, ...json }: UpdateCelestialBodyData) =>
  client.patch(`celestial-bodies/${id}`, { json }).then((response) => response.json<CelestialBody>());

export type RemoveCelestialBodyData = { id: number };
export const remove = ({ id }: RemoveCelestialBodyData) => client.delete(`celestial-bodies/${id}`).then(() => void 0);
