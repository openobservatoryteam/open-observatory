import { ApplicationError } from './@types';
import ky from 'ky';

export default ky.create({
  credentials: 'include',
  hooks: {
    beforeError: [
      async (r) => {
        try {
          const json = (await r.response.json()) as ApplicationError;
          r.cause = json;
        } catch {
          /* empty */
        }
        return r;
      },
    ],
  },
  prefixUrl: import.meta.env.VITE_API_URL ?? '/api',
});
