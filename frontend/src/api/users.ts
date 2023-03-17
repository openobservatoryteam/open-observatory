import client from './client';
import { Observation, User } from './types';

export const getCurrent = () => client.get('users/current').then((response) => response.json<User>());

type RegistrationBody = { username: string; password: string; biography?: string };
export const register = (json: RegistrationBody) =>
  client.post('users/register', { json }).then((response) => response.json<User>());

export type ChangePasswordBody = { oldPassword: string; newPassword: string };
export const changePassword = (username: string, json: ChangePasswordBody) =>
  client.patch(`users/${username}/password`, { json });

export const observations = (username: string) =>
  client.get(`users/${username}/observations`).then((response) => response.json<Observation[]>());
