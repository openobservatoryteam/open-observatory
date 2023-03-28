import { PushKey } from './@types';
import { HTTPError } from 'ky';

import client from '~/api/client';

export const pushKey = () => client.get('notifications/key').then((response) => response.json<PushKey>());

type PushStatusData = { endpoint: string };
export const pushStatus = (searchParams: PushStatusData) =>
  client
    .get('notifications/status', { searchParams })
    .then(() => 'subscribed')
    .catch((error: HTTPError) => (error.response.status === 404 ? 'unsubscribed' : 'error'));

type SubscribePushData = { auth: string; endpoint: string; p256dh: string };
export const subscribePush = (json: SubscribePushData) =>
  client.post('notifications/subscribe', { json }).then(() => null);

type UnsubscribePushData = { endpoint: string };
export const unsubscribePush = (json: UnsubscribePushData) =>
  client.post('notifications/unsubscribe', { json }).then(() => null);
