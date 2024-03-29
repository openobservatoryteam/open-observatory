import client from '~/api/client';

export const getPushKey = () => client.get('push/public-key').then((response) => response.text());

type PushSubscribeData = { auth: string; endpoint: string; p256dh: string };
export const pushSubscribe = (json: PushSubscribeData) => client.post('push/subscribe', { json }).then(() => null);

type PushUnsubscribeData = { endpoint: string };
export const pushUnsubscribe = (json: PushUnsubscribeData) =>
  client.post('push/unsubscribe', { json }).then(() => null);
