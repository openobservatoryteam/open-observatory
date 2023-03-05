import { faArrowLeft, faPen } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useMatch } from '@tanstack/react-location';
import dayjs from 'dayjs';
import { useState } from 'react';
import { Marker, Popup } from 'react-leaflet';

import celestialBodyImage from '@/assets/png/celeste.png';
import userIcon from '@/assets/png/icon-user.png';
import { Button, Chip, Map, Text, UpDownVote } from '@/components';
import { useQuery } from '@tanstack/react-query';
import { observations } from '@/api';

interface visibilyType {
  CLEARLY_VISIBLE: string;
  VISIBLE: string;
  SLIGHTLY_VISIBLE: string;
  BARELY_VISIBLE: string;
  [key: string]: string;
}

const visibilityLevels: visibilyType = {
  CLEARLY_VISIBLE: "À l'oeil nue",
  VISIBLE: 'Visible',
  SLIGHTLY_VISIBLE: 'Légèrement visible',
  BARELY_VISIBLE: 'Peu visible',
};

function ObservationPage(): JSX.Element {
  const {
    params: { id },
  } = useMatch<{ Params: { id: string } }>();

  const [myVote, submitVote] = useState<boolean | null>(null);
  const observation = useQuery({
    queryFn: () => observations.findById(id),
    queryKey: ['observation'],
  }).data;

  if (observation) {
    const isAuthor = observation.author.username === 'EikjosTV';
    return (
      <div className="md:flex">
        <div className="w-full">
          <div className="h-[50vh] relative">
            <div className="absolute left-5 top-5 flex">
              <Button as={Link} to="/" className="px-3 mr-5" color="white" rounded>
                <FontAwesomeIcon icon={faArrowLeft} size="xl" />
              </Button>
              {observation.hasExpired && <Chip>Expirée</Chip>}
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
            <img src={celestialBodyImage} alt="Objet céleste de l'observation" className="h-full object-cover w-full" />
            <UpDownVote
              className="absolute bottom-1 right-2"
              currentVotes={observation.votes}
              onVote={submitVote}
              vote={myVote}
            />
          </div>
          <div className="bg-slate-500 h-1/4 pb-5 md:h-[50vh] w-full">
            <div className="flex justify-between pl-3 pt-4">
              <div className="w-1/2">
                <Text as="h2" bold className="text-lg md:text-2xl">
                  {observation.celestialBody.name}
                </Text>
                <Text as="p" className="text-xs md:text-base">
                  {dayjs(observation.time).format('le DD/MM/YYYY à HH:mm')}
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

  return <Text as="h1">Observation introuvable</Text>;
}

export default ObservationPage;
