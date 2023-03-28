import { Link } from '@tanstack/react-location';
import { useEffect, useState } from 'react';
import { Title as DocumentTitle } from 'react-head';
import { useTranslation } from 'react-i18next';

import { subscribeNotifications, unsubscribeNotifications } from '~/api/notifications';
import { Button, ISSPositions, Map, NearbyObservations, Text } from '~/components';
import { Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { bufToString, strToIntArray } from '~/utils/arrayBuffer';

const applicationServerKey = strToIntArray(
  'BBa0zJTVfJBuHa0ud9BVgaH4bO1o2Dpe5bddHCskRG7LYRaOZdqBL7zlu_4qJashpNhrr9PrhAfYB1O1AiEW6vs',
);

function HomePage() {
  const { isLoggedIn, user } = useAuthentication();
  const { t } = useTranslation();
  const [isSubscribed, setSubscribed] = useState(false);
  useEffect(() => {
    const main = async () => {
      const registration = (await navigator.serviceWorker.getRegistrations())[0];
      if (!registration) return;
      const subscription = await registration.pushManager.getSubscription();
      setSubscribed(!!subscription);
    };
    main();
  }, []);
  const enableNotifications = async () => {
    const status = await Notification.requestPermission();
    if (status !== 'granted') {
      alert("Si vous changez d'avis, autorisez les notifications dans les paramètres de votre navigateur.");
      return;
    }
    const registration = (await navigator.serviceWorker.getRegistrations())[0];
    if (!registration) return;
    const subscription = await registration.pushManager.subscribe({
      applicationServerKey,
      userVisibleOnly: true,
    });
    subscribeNotifications({
      auth: bufToString(subscription.getKey('auth')!),
      endpoint: subscription.endpoint,
      p256dh: bufToString(subscription.getKey('p256dh')!),
    })
      .then(() => setSubscribed(true))
      .catch(() => alert("Une erreur est survenue lors de l'abonnement aux notifications."));
  };
  const disableNotifications = async () => {
    const registration = (await navigator.serviceWorker.getRegistrations())[0];
    const subscription = await registration.pushManager.getSubscription();
    if (!subscription) return;
    unsubscribeNotifications({ endpoint: subscription.endpoint })
      .then(() => {
        setSubscribed(false);
        subscription.unsubscribe();
      })
      .catch(() => alert('Une erreur est survenue lors de votre désinscription.'));
  };
  return (
    <>
      <DocumentTitle>{t('document.title.home')}</DocumentTitle>
      <Header className="h-16 my-1" />
      <div className="h-16 gap-x-1 flex max-w-screen-lg mx-auto my-1 px-1 sm:gap-x-16 sm:px-16">
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? '/report-observation' : '/login'}>
          {t('observation.new')}
        </Button>
        <Button as={Link} color="darkGray" fullWidth to={isLoggedIn ? `/users/${user.username}` : '/login'}>
          {isLoggedIn ? t('users.profil') : t('users.login')}
        </Button>
      </div>
      <div className="flex justify-center my-3">
        {Notification.permission === 'denied' ? (
          <Text>
            Pour bénéficier des notifications, autorisez ce site à vous le demander dans les paramètres de votre
            navigateur puis rechargez la page.
          </Text>
        ) : isSubscribed ? (
          <Button onPress={disableNotifications}>Désactiver les notifications</Button>
        ) : (
          <Button onPress={enableNotifications}>Activer les notifications</Button>
        )}
      </div>
    <Map
        className="h-[calc(100vh-8.75rem)]"
        minZoom={3}
        worldCopyJump
        radius={user != null ? user!.radius : 0}
        withoutNotificationCircle={user != null ? !user!.notificationsEnabled : false}
    >
        <ISSPositions />
        <NearbyObservations />
      </Map>
    </>
  );
}

export default HomePage;
