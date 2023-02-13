import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faPen } from '@fortawesome/free-solid-svg-icons';
// import { useMatch } from '@tanstack/react-location';
import { LatLngTuple } from 'leaflet';
import { useState } from 'react';
import { Marker, Popup } from 'react-leaflet';

import celest from '@/assets/png/celeste.png';
import iconUser from '@/assets/png/icon-user.png';
import { Button, Chip, Map, Text, UpDownVote } from '@/components';
import { Link } from '@tanstack/react-location';

export default function ObservationPage() {
  // const {
  //   params: { id },
  // } = useMatch<{ Params: { id: string } }>();
  const pos: LatLngTuple = [49.4049375, 0.4180013];
  const expired = true;
  const isUser = true;
  const [currentVote, setCurrentVote] = useState<boolean | null>(null);
  return (
    <div className="md:flex">
      <div className="w-full">
        <div className="h-[50vh] relative">
          {expired && <Chip className="absolute left-5 top-5">Expirée</Chip>}
          {isUser && (
            <Button className="absolute h-14 md:h-16 right-2 top-4 w-14 md:w-16" color="white" rounded>
              <FontAwesomeIcon icon={faPen} size="xl" />
            </Button>
          )}
          <img src={celest} alt="image de l'observation" className="h-full object-cover w-full" />
          <UpDownVote
            className="absolute bottom-1 right-2"
            currentVotes={10}
            onVote={(v) => setCurrentVote(v)}
            vote={currentVote}
          />
        </div>
        <div className="bg-slate-500 h-1/4 pb-5 md:h-[50vh] w-full">
          <div className="flex justify-between pl-3 pt-4">
            <div className="w-1/2">
              <Text as="h2" bold className="text-lg md:text-2xl">
                Galaxie Messier
              </Text>
              <Text as="p" className="text-xs md:text-base">
                le 22/11/2022 à 18h44
              </Text>
            </div>
            <Button as={Link} className="flex items-center mr-4 md:mr-10" to="/users/a" unstyled>
              <img className="w-10 md:w-12" src={iconUser} />
              <Text as="span" className="ml-2 md:ml-4" color="white">
                Utilisateur
              </Text>
            </Button>
          </div>
          <div className="mt-3 md:mt-10 px-4">
            <Text as="span" bold className="px-1" color="white">
              Visibilité :
            </Text>
            <Text as="span" color="white">
              À l&apos;oeil nu
            </Text>
          </div>
          <div className="mt-3 md:mt-5 px-4">
            <Text as="span" bold className="px-1" color="white">
              Orientation :
            </Text>
            <Text as="span" color="white">
              98°
            </Text>
          </div>
          <div className="mt-3 md:mt-5 px-4">
            <Text as="span" bold className="px-1" color="white">
              Description :
            </Text>
            <Text as="span" color="white">
              L&apos;observation est somptueuse.
            </Text>
          </div>
        </div>
      </div>
      <Map center={pos} className="h-[calc(100vh-20rem)] md:h-[100vh] w-full" noFly withoutNotificationCircle>
        <Marker position={pos}>
          <Popup>Nom de l&apos;observation</Popup>
        </Marker>
      </Map>
    </div>
  );
}
