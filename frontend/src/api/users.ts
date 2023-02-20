import client from './client';
import { User } from './types';

export const getCurrent = () => client.get('users/current').then((response) => response.json<User>());

type RegistrationBody = { username: string; password: string; biography?: string };
export const register = (json: RegistrationBody) =>
  client.post('users/register', { json }).then((response) => response.json<User>());
