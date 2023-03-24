import { faArrowLeft, faPen } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useMatch } from '@tanstack/react-location';
import { useMutation, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { Marker, Popup } from 'react-leaflet';

import { findObservationById, putVote } from '~/api';
import celestialBodyImage from '~/assets/png/celeste.png';
import userIcon from '~/assets/png/icon-user.png';
import { Button, Chip, Map, Text, UpDownVote } from '~/components';

const visibilityLevels = {
  CLEARLY_VISIBLE: "À l'oeil nue",
  VISIBLE: 'Visible',
  SLIGHTLY_VISIBLE: 'Légèrement visible',
  BARELY_VISIBLE: 'Peu visible',
};

type VoteType = 'UPVOTE' | 'DOWNVOTE' | null;

function ObservationPage(): JSX.Element {
  const {
    params: { id },
  } = useMatch<{ Params: { id: string } }>();

  const observationQuery = useQuery({
    queryFn: () => findObservationById(id),
    queryKey: ['observation', id],
  });

  const vote = useMutation({
    mutationFn: ({ id, vote }: { id: string; vote: VoteType }) => putVote({ id, vote }),
    onSuccess: () => observationQuery.refetch(),
  });

  if (observationQuery.isLoading) return <Text as="h2">Chargement en cours...</Text>;
  if (observationQuery.isError) return <Text as="h2">Observation introuvable</Text>;

  const observation = observationQuery.data;
  const isAuthor = observation.author.username === 'EikjosTV';
  return (
    <div className="md:flex">
      <div className="w-full">
        <div className="h-[50vh] relative">
          <div className="absolute left-5 top-5 flex">
            <Button className="py-1 px-3 mr-5" color="white" onPress={() => history.go(-1)} rounded>
              <FontAwesomeIcon icon={faArrowLeft} size="xl" />
            </Button>
            {observation.expired && <Chip>Expirée</Chip>}
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
                {dayjs(observation.createdAt).format('le DD/MM/YYYY à HH:mm')}
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
              Visibilité :
            </Text>{' '}
            {visibilityLevels[observation.visibility]}
          </Text>
          <Text className="mt-3 md:mt-10 px-5">
            <Text as="span" bold>
              Orientation :
            </Text>{' '}
            {observation.orientation}°
          </Text>
          <Text className="mt-3 md:mt-10 px-5">
            <Text as="span" bold>
              Description :
            </Text>{' '}
            {observation.description}
          </Text>
        </div>
      </div>
      <Map
        center={[observation.latitude, observation.longitude]}
        className="h-[calc(100vh-20rem)] md:h-[100vh] w-full"
        noFly
        withoutNotificationCircle
      >
        <Marker position={[observation.latitude, observation.longitude]}>
          <Popup>{observation.celestialBody.name}</Popup>
        </Marker>
      </Map>
    </div>
  );
}

export default ObservationPage;
