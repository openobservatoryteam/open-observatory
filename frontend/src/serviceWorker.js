const messages = {
  OBSERVATION_NEARBY: "Une observation vient d'être postée à proximité de vous !",
};

self.addEventListener('activate', () => self.clients.claim());
self.addEventListener('install', () => self.skipWaiting());

self.addEventListener('push', (event) => {
  if (event.data === null) return;
  const { icon, link, code } = event.data.json();
  if (code in messages) {
    self.registration.showNotification('Open Observatory', {
      body: messages[code],
      data: { icon, link },
      icon: '/assets/logo.png',
    });
  }
});

if ('openWindow' in self.clients) {
  self.addEventListener('notificationclick', (event) => {
    const { notification } = event;
    if (notification.data.link) self.clients.openWindow(notification.data.link);
  });
}
