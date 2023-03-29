import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

import { pushKey, subscribePush, unsubscribePush } from '~/api/notifications';

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
  const { data: pushSubscriptionKeyData } = useQuery({ queryFn: pushKey, queryKey: ['push-key'] });
  const [currentSubscription, setCurrentSubscription] = useState<PushSubscription | null>(null);
  useEffect(() => {
    (async () => {
      const [registration] = await navigator.serviceWorker.getRegistrations();
      if (!registration) return;
      const subscription = await registration.pushManager.getSubscription();
      setCurrentSubscription(subscription);
    })();
  }, []);
  return {
    subscribed: !!currentSubscription,
    supported,
    subscribe: async () => {
      if (!pushSubscriptionKeyData) return false;
      const [registration] = await navigator.serviceWorker.getRegistrations();
      if (!registration) return false;
      const subscription = await registration.pushManager.subscribe({
        applicationServerKey: arrayFromString(pushSubscriptionKeyData.key),
        userVisibleOnly: true,
      });
      return subscribePush({
        auth: stringFromBuffer(subscription.getKey('auth')!),
        endpoint: subscription.endpoint,
        p256dh: stringFromBuffer(subscription.getKey('p256dh')!),
      })
        .then(() => {
          setCurrentSubscription(subscription);
          return true;
        })
        .catch(() => false);
    },
    unsubscribe: async () => {
      if (currentSubscription === null) return true;
      return unsubscribePush({ endpoint: currentSubscription.endpoint })
        .then(() => {
          currentSubscription.unsubscribe();
          setCurrentSubscription(null);
          return true;
        })
        .catch(() => false);
    },
  };
}

export { usePush };
