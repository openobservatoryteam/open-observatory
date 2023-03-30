import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

import { getPushKey, getPushSubscriptions, pushSubscribe, pushUnsubscribe } from '~/api/notifications';
import { useAuthentication } from '~/providers';

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

type PushProps =
  | { supported: false }
  | { subscribed: boolean; supported: true; subscribe: () => Promise<boolean>; unsubscribe: () => Promise<boolean> };

function usePush(): PushProps {
  const supported = 'serviceWorker' in navigator;
  if (!supported) return { supported };
  const { isLoggedIn } = useAuthentication();
  const { refetch: fetchKey } = useQuery({ enabled: false, queryFn: getPushKey });
  const { data: pushSubscriptions } = useQuery({
    enabled: isLoggedIn,
    queryFn: getPushSubscriptions,
    queryKey: ['push-subscriptions'],
  });
  const [currentSubscription, setCurrentSubscription] = useState<PushSubscription | null>(null);
  useEffect(() => {
    (async () => {
      const registration = await navigator.serviceWorker.ready;
      const subscription = await registration.pushManager.getSubscription();
      if (subscription === null) {
        setCurrentSubscription(null);
        return;
      }
      if (pushSubscriptions?.findIndex((p) => p.endpoint === subscription.endpoint) === -1) {
        subscription.unsubscribe();
        setCurrentSubscription(null);
        return;
      }
      setCurrentSubscription(subscription);
    })();
  }, [pushSubscriptions]);
  return {
    subscribed: !!currentSubscription,
    supported,
    subscribe: async () => {
      const registration = await navigator.serviceWorker.ready;
      const { data: pushKey } = await fetchKey();
      const subscription = await registration.pushManager.subscribe({
        applicationServerKey: arrayFromString(pushKey!.key),
        userVisibleOnly: true,
      });
      try {
        await pushSubscribe({
          auth: stringFromBuffer(subscription.getKey('auth')!),
          endpoint: subscription.endpoint,
          p256dh: stringFromBuffer(subscription.getKey('p256dh')!),
        });
        setCurrentSubscription(subscription);
        return true;
      } catch {
        return false;
      }
    },
    unsubscribe: async () => {
      if (currentSubscription === null) return true;
      try {
        await pushUnsubscribe({ endpoint: currentSubscription.endpoint });
        await currentSubscription.unsubscribe();
        return true;
      } catch {
        return false;
      }
    },
  };
}

export { usePush };
