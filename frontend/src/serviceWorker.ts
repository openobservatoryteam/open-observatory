/// <reference lib="webworker" />
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
const worker = self as unknown as ServiceWorkerGlobalScope;

worker.addEventListener('activate', () => worker.clients.claim());
worker.addEventListener('install', () => worker.skipWaiting());

type NotificationPayload = {
  link?: string;
  message: string;
};

worker.addEventListener('push', (event) => {
  if (event.data === null) return;
  const { link, message } = event.data.json() as NotificationPayload;
  console.log('Reçu un message: ' + message);
  worker.registration.showNotification('Open Observatory', {
    body: message,
    data: { link },
    icon: '/assets/logo.png',
  });
});

if ('openWindow' in worker.clients) {
  worker.addEventListener('notificationclick', (event) => {
    const { notification } = event;
    if (notification.data.link) worker.clients.openWindow(notification.data.link);
  });
}
