import { faArrowLeft, faPen } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useMatch } from '@tanstack/react-location';
import { useMutation, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { useTranslation } from 'react-i18next';
import { Marker, Popup } from 'react-leaflet';

import { findObservationById, putVote } from '~/api';
import celestialBodyImage from '~/assets/png/celeste.png';
import userIcon from '~/assets/png/icon-user.png';
import { Button, Chip, WorldMap, Text, UpDownVote } from '~/components';
import { useAuthentication } from '~/providers';

type VoteType = 'UPVOTE' | 'DOWNVOTE' | null;

function ObservationPage(): JSX.Element {
  const {
    params: { id },
  } = useMatch<{ Params: { id: string } }>();
  const { t } = useTranslation();
  const { user } = useAuthentication();
  const visibilityLevels = {
    CLEARLY_VISIBLE: t('visibility.clearly'),
    VISIBLE: t('visibility.visible'),
    SLIGHTLY_VISIBLE: t('visibility.slightly'),
    BARELY_VISIBLE: t('visibility.barely'),
  };
  const observationQuery = useQuery({
    queryFn: () => findObservationById(id),
    queryKey: ['observation', id],
  });

  const vote = useMutation({
    mutationFn: ({ id, vote }: { id: string; vote: VoteType }) => putVote({ id, vote }),
    onSuccess: () => observationQuery.refetch(),
  });

  if (observationQuery.isLoading) return <Text as="h2">Chargement en cours...</Text>;
  if (observationQuery.isError) return <Text as="h2">{t('observation.notFound')}</Text>;

  const observation = observationQuery.data;
  const isAuthor = user != null && observation.author.username === user.username;
  return (
    <div className="md:flex">
      <div className="w-full">
        <div className="h-[50vh] relative">
          <div className="absolute left-5 top-5 flex">
            <Button as={Link} className="py-1 px-3 mr-5" color="white" rounded to="/">
              <FontAwesomeIcon icon={faArrowLeft} size="xl" />
            </Button>
            {observation.expired && <Chip>{t('observation.expired')}</Chip>}
          </div>
          {isAuthor && (
            <Button
              as={Link}
              className="absolute h-14 md:h-16 right-2 top-4 w-14 md:w-16"
              color="white"
              rounded
              to={`/observations/${id}/edit`}
            >
              <FontAwesomeIcon icon={faPen} size="xl" />
            </Button>
          )}
          <img
            src={observation.celestialBody.image ?? celestialBodyImage}
            alt="Objet céleste de l'observation"
            className="h-full object-cover w-full"
          />
          <UpDownVote
            className="absolute bottom-1 right-2"
            currentVotes={observation.karma}
            onVote={(value) => vote.mutate({ id, vote: value })}
            vote={observation.currentVote}
            disabled={vote.isLoading}
          />
        </div>
        <div className="bg-slate-500 h-1/4 pb-5 md:h-[50vh] w-full">
          <div className="flex justify-between pl-3 pt-4">
            <div className="w-1/2">
              <Text as="h2" bold className="text-lg md:text-2xl">
                {observation.celestialBody.name}
              </Text>
              <Text as="p" className="text-xs md:text-base">
                {t('common.on-at', {
                  date: dayjs(observation.createdAt).format('DD/MM/YYYY'),
                  heure: dayjs(observation.createdAt).format('HH:mm'),
                })}
              </Text>
            </div>
            <Button
              as={Link}
              className="gap-2 md:gap-4 mr-4 py-1.5"
              color="transparent"
              to={`/users/${observation.author.username}`}
            >
              <img className="rounded-full w-10 md:w-12" src={observation.author.avatar ?? userIcon} />
              <Text as="span" color="white">
                {observation.author.username}
              </Text>
            </Button>
          </div>
          <Text className="mt-3 md:mt-10 px-5">
            <Text as="span" bold>
              {t('common.visibility') + ' : '}
            </Text>{' '}
            {visibilityLevels[observation.visibility]}
          </Text>
          <Text className="mt-3 md:mt-10 px-5">
            <Text as="span" bold>
              {t('common.orientation') + ' : '}
            </Text>{' '}
            {observation.orientation}°
          </Text>
          {observation.description && (
            <Text className="mt-3 md:mt-10 px-5">
              <Text as="span" bold>
                {t('common.description') + ' : '}
              </Text>{' '}
              {observation.description}
            </Text>
          )}
        </div>
      </div>
      <WorldMap
        center={[observation.latitude, observation.longitude]}
        className="h-[calc(100vh-20rem)] md:h-[100vh] w-full"
        noFly
        withoutNotificationCircle
      >
        <Marker position={[observation.latitude, observation.longitude]}>
          <Popup>{observation.celestialBody.name}</Popup>
        </Marker>
      </WorldMap>
    </div>
  );
}

export default ObservationPage;
