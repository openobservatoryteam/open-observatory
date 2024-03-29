import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Circle, CircleMarker, Tooltip, useMap } from 'react-leaflet';

import { sendCurrentPosition } from '~/api';
import { usePosition } from '~/hooks';
import { useAuthentication } from '~/providers';

type CurrentPositionProps = { noFly?: boolean; withoutNotificationCircle?: boolean; radius?: number };

export function CurrentPosition({
  noFly = false,
  withoutNotificationCircle = false,
  radius = 25000,
}: CurrentPositionProps) {
  const { user } = useAuthentication();
  const [hasCentered, setCentered] = useState(noFly);
  const { t } = useTranslation();
  const map = useMap();
  const position = usePosition();
  useEffect(() => {
    if (position !== null && !hasCentered) {
      map.setView(position, 12);
      setCentered(true);
      if (user !== null && position !== null) {
        sendCurrentPosition({ username: user.username, latitude: position.lat, longitude: position.lng });
      }
    }
  }, [position]);
  return position ? (
    <>
      {!withoutNotificationCircle && <Circle center={position} radius={radius * 1000} color="#999999" />}
      <CircleMarker center={position} color="#990000" fill fillColor="#990000" fillOpacity={1} radius={2}>
        <Tooltip direction="top">{t('users.here')}</Tooltip>
      </CircleMarker>
    </>
  ) : null;
}
