import { Button, Map, Text } from '@/components';
import { useMatch } from '@tanstack/react-location';
import celest from '@/assets/png/celeste.png';
import iconUser from '@/assets/png/icon-user.png';
import clsx from 'clsx';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp, faPen } from '@fortawesome/free-solid-svg-icons';
import { Marker, Popup } from 'react-leaflet';

export default function ObservationPage(): JSX.Element {
  const {
    params: { id },
  } = useMatch<{ Params: { id: string } }>();
  const expired = true;
  const isUser = true;
  return (
    <div className="md:flex md:w-full">
      <div className="w-full">
        <div className="h-1/3 md:h-[50vh] relative w-full">
          {expired && (
            <Text as="span" color="black" centered className="absolute bg-white left-5 py-2 px-5 rounded-[45px] top-5">
              Expirée
            </Text>
          )}
          {isUser && (
            <Button
              className="absolute bg-white flex h-14 md:h-16 justify-center items-center right-2 rounded-[45px] top-4 w-14 md:w-16"
              unstyled
            >
              <FontAwesomeIcon icon={faPen} size="xl" />
            </Button>
          )}
          <img src={celest} alt="image de l'observation" className="h-full w-full" />
          <div className="absolute bottom-1 flex-col justify-around items-center right-2">
            <div className="flex justify-center items-center w-10">
              <Button unstyled>
                <FontAwesomeIcon icon={faArrowUp} size="2xl" color="white" />
              </Button>
            </div>
            <Text as="p" centered className="py-1 w-10" color="white">
              10
            </Text>
            <div className="flex justify-center items-center w-10">
              <Button unstyled>
                <FontAwesomeIcon icon={faArrowDown} size="2xl" color="grey" />
              </Button>
            </div>
          </div>
        </div>
        <div className="bg-slate-500 h-1/4 pb-5 md:h-[50vh] w-full">
          <div className="flex justify-between pl-3 pt-4">
            <div className="w-1/2">
              <Text as="h1" bold className="text-lg md:text-2xl">
                Galaxie Messier
              </Text>
              <Text as="p" className="text-xs md:text-base">
                le 22/11/2022 à 18h44
              </Text>
            </div>
            <div className="flex items-center pr-4 md:pr-10">
              <img className="w-10 md:w-12" src={iconUser} />
              <Text as="span" className="pl-2 md:pl-4" color="white">
                Utilisateur
              </Text>
            </div>
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
      <Map
        center={[49.2332, 1.813]}
        className="h-[calc(100vh-20rem)] md:h-[100vh] w-full"
        noFly
        withoutNotificationCircle
      >
        <Marker position={[49.2332, 1.813]}>
          <Popup>Nom de l&apos;observation</Popup>
        </Marker>
      </Map>
    </div>
  );
}
