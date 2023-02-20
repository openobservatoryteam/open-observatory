import ky from 'ky';
import { API_URL } from './constants';
import { ProblemDetail } from './types';

export default ky.create({
  credentials: 'include',
  hooks: {
    beforeError: [
      async (r) => {
        try {
          const json: ProblemDetail = await r.response.json();
          r.cause = json;
        } catch {
          /* empty */
        }
        return r;
      },
    ],
  },
  prefixUrl: API_URL,
});
