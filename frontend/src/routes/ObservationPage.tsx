import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPen } from '@fortawesome/free-solid-svg-icons';
import { Link } from '@tanstack/react-location';
// import { useMatch } from '@tanstack/react-location';
import dayjs from 'dayjs';
import { useState } from 'react';
import { Marker, Popup } from 'react-leaflet';

import celestialBodyImage from '@/assets/png/celeste.png';
import userIcon from '@/assets/png/icon-user.png';
import { Button, Chip, Map, Text, UpDownVote } from '@/components';

const visibilityLevels = {
  NAKED_EYE: "à l'oeil nu",
  UNKNOWN: 'inconnue',
};

export default function ObservationPage() {
  // const {
  //   params: { id },
  // } = useMatch<{ Params: { id: string } }>();
  const observation = {
    author: {
      avatarURL: null,
      username: 'EikjosTV',
    },
    description: "L'observation est somptueuse",
    hasExpired: true,
    name: 'Observation de test',
    orientation: 98,
    position: [49.4049375, 0.4180013],
    sentAt: '2023-02-13T13:12:44+01:00',
    visibility: 'NAKED_EYE',
    votes: 200,
  } as const;
  const isAuthor = observation.author.username === 'EikjosTV';
  const [myVote, submitVote] = useState<boolean | null>(null);
  return (
    <div className="md:flex">
      <div className="w-full">
        <div className="h-[50vh] relative">
          {observation.hasExpired && <Chip className="absolute left-5 top-5">Expirée</Chip>}
          {isAuthor && (
            <Button className="absolute h-14 md:h-16 right-2 top-4 w-14 md:w-16" color="white" rounded>
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
                {observation.name}
              </Text>
              <Text as="p" className="text-xs md:text-base">
                {dayjs(observation.sentAt).format('le DD/MM/YYYY à HH:mm')}
              </Text>
            </div>
            <Button as={Link} className="gap-2 md:gap-4 mr-4 py-1.5" color="transparent" to="/users/a">
              <img className="rounded-full w-10 md:w-12" src={observation.author.avatarURL ?? userIcon} />
              <Text as="span" color="white">
                {observation.author.username}
              </Text>
            </Button>
          </div>
          <Text className="mt-3 md:mt-10 px-5">
            <Text as="span" bold>
              Visibilité :
            </Text>{' '}
            {visibilityLevels[observation.visibility ?? 'UNKNOWN']}
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
        center={[...observation.position]}
        className="h-[calc(100vh-20rem)] md:h-[100vh] w-full"
        noFly
        withoutNotificationCircle
      >
        <Marker position={[...observation.position]}>
          <Popup>{observation.name}</Popup>
        </Marker>
      </Map>
    </div>
  );
}
