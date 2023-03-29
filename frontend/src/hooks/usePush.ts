import { useQuery } from '@tanstack/react-query';
import { useEffect, useState } from 'react';

import { getPushKey, getPushSubscriptions, pushSubscribe, pushUnsubscribe } from '~/api/notifications';

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
  const { data: pushKey } = useQuery({ queryFn: getPushKey, queryKey: ['push-key'] });
  const { data: pushSubscriptions } = useQuery({ queryFn: getPushSubscriptions, queryKey: ['push-subscriptions'] });
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
      const [registration] = await navigator.serviceWorker.getRegistrations();
      if (!pushKey || !registration) return false;
      const subscription = await registration.pushManager.subscribe({
        applicationServerKey: arrayFromString(pushKey.key),
        userVisibleOnly: true,
      });
      return pushSubscribe({
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
      return pushUnsubscribe({ endpoint: currentSubscription.endpoint })
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
