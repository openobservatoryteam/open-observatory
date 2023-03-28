import client from '~/api/client';

type SubscribeNotificationsData = { auth: string; endpoint: string; p256dh: string };

export const subscribeNotifications = (json: SubscribeNotificationsData) =>
  client.post('notifications/subscribe', { json }).then(() => null);

type UnsubscribeNotificationsData = { endpoint: string };

export const unsubscribeNotifications = (json: UnsubscribeNotificationsData) =>
  client.post('notifications/unsubscribe', { json }).then(() => null);
