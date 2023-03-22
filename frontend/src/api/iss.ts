import { ISSPosition } from '~/api/@types';
import client from '~/api/client';

export const findISSPositions = () => client.get('iss/positions').then((r) => r.json<ISSPosition[]>());
