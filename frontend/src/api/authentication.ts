import client from './client';

export type LoginData = { username: string; password: string };
export const postLogin = (data: LoginData) =>
  client.post('login', { body: new URLSearchParams(data) }).then(() => void 0);

export const postLogout = () => client.post('logout').then(() => null);
