import { useQuery } from '@tanstack/react-query';

import { getPushKey, pushSubscribe } from '~/api/notifications';

function arrayFromString(base64String: string) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');

  const rawData = atob(base64);
  const outputArray = new Uint8Array(rawData.length);

  for (let i = 0; i < rawData.length; ++i) {
    outputArray[i] = rawData.charCodeAt(i);
  }
  return outputArray;
}

function stringFromBuffer(buffer: ArrayBuffer) {
  return btoa(String.fromCharCode(...new Uint8Array(buffer)));
}

function usePush() {
  const supported = 'serviceWorker' in navigator;
  if (!supported) return { supported };
  const { refetch: fetchKey } = useQuery({ enabled: false, queryFn: getPushKey });
  return {
    supported,
    subscribe: async () => {
      const registration = await navigator.serviceWorker.ready;
      const { data: pushKey } = await fetchKey();
      const existingSubscription = await registration.pushManager.getSubscription();
      if (existingSubscription) await existingSubscription.unsubscribe();
      const subscription = await registration.pushManager.subscribe({
        applicationServerKey: arrayFromString(pushKey!),
        userVisibleOnly: true,
      });
      try {
        await pushSubscribe({
          auth: stringFromBuffer(subscription.getKey('auth')!),
          endpoint: subscription.endpoint,
          p256dh: stringFromBuffer(subscription.getKey('p256dh')!),
        });
        return true;
      } catch {
        return false;
      }
    },
  };
}

export { usePush };
