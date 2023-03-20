import { CelestialBody, SearchResults } from './@types';
import client from './client';

export const findAllCelestialBodies = () =>
  client.get('celestial-bodies').then((r) => r.json<SearchResults<CelestialBody>>());

export type CreateCelestialBodyData = { name: string; image: string; validityTime: number };
export const createCelestialBody = (json: CreateCelestialBodyData) =>
  client.post('celestial-bodies', { json }).then((r) => r.json<CelestialBody>());

export type UpdateCelestialBodyData = { name: string; image: string; validityTime: number };
export const updateCelestialBody = ({ id, ...json }: UpdateCelestialBodyData & { id: number }) =>
  client.patch(`celestial-bodies/${id}`, { json }).then((r) => r.json<CelestialBody>());

export type DeleteCelestialBodyData = { id: number };
export const deleteCelestialBody = ({ id }: DeleteCelestialBodyData) =>
  client.delete(`celestial-bodies/${id}`).then(() => null);
