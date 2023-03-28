import { useState } from 'react';

function useNotifications() {
  const supported = 'Notification' in window;
  const [status, setStatus] = useState<NotificationPermission | null>(supported ? Notification.permission : null);
  const request = async () => {
    if (!supported) return null;
    const status = await Notification.requestPermission();
    setStatus(status);
    return status;
  };
  return { supported, request, status };
}

export { useNotifications };
