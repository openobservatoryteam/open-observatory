/// <reference lib="ES2015" />
/// <reference lib="webworker" />
import { precacheAndRoute } from 'workbox-precaching';

declare const self: ServiceWorkerGlobalScope;

precacheAndRoute(self.__WB_MANIFEST ?? []);

self.addEventListener('activate', () => self.clients.claim());
self.addEventListener('install', () => self.skipWaiting());

type NotificationPayload = {
  icon?: string;
  link?: string;
  message: string;
};

self.addEventListener('push', (event) => {
  if (event.data === null) return;
  const { icon, link, message } = event.data.json() as NotificationPayload;
  self.registration.showNotification('Open Observatory', {
    body: message,
    data: { icon, link },
    icon: '/assets/logo.png',
  });
});

if ('openWindow' in self.clients) {
  self.addEventListener('notificationclick', (event) => {
    const { notification } = event;
    if (notification.data.link) self.clients.openWindow(notification.data.link);
  });
}
