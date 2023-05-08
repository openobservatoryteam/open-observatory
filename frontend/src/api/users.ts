import { Observation, SearchResults, UserWithProfile } from './@types';
import client from './client';

export const getSelfUser = () => client.get('users/@me').then((r) => r.json<UserWithProfile>());

export type CreateUserData = { username: string; password: string; passwordConfirmation: string; biography?: string };
export const postCreateUser = (json: CreateUserData) =>
  client.post('users/register', { json }).then((r) => r.json<UserWithProfile>());

export const findAllUserObservations = (username: string) =>
  client.get(`users/${username}/observations`).then((r) => r.json<Observation[]>());

export const findUserByUsername = (username: string) =>
  client.get(`users/${username}`).then((r) => r.json<UserWithProfile>());

export type ChangeUserPasswordData = {
  oldPassword: string;
  newPassword: string;
  newPasswordConfirmation: string;
};
export const changeUserPassword = ({ username, ...json }: ChangeUserPasswordData & { username: string }) =>
  client.patch(`users/${username}/password`, { json }).then(() => null);

export type UpdateUserData = {
  avatar?: string | null;
  biography?: string | null;
  isPublic?: boolean;
};

export const updateUser = ({ username, ...json }: UpdateUserData & { username: string | undefined }) =>
  client.patch(`users/${username}`, { json }).then((r) => r.json<UserWithProfile>());

export type SendCurrentPositionData = { username: string; latitude: number; longitude: number };
export const sendCurrentPosition = ({ username, ...json }: SendCurrentPositionData) =>
  client.post(`users/${username}/position`, { json }).then(() => void 0);

type findAllUserData = { page: number; itemsPerPage: number };
export const findAllUser = ({ page, itemsPerPage }: findAllUserData) =>
  client.get(`users?page=${page}&itemsPerPage=${itemsPerPage}`).then((r) => r.json<SearchResults<UserWithProfile>>());

export const deleteUser = ({ username }: { username: string }) => client.delete(`users/${username}`).then(() => null);
