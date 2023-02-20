import client from './client';

export type LoginData = { username: string; password: string };

export const login = (data: LoginData) => client.post('login', { body: new URLSearchParams(data) }).then(() => void 0);
export const logout = () => client.post('logout').then(() => void 0);
