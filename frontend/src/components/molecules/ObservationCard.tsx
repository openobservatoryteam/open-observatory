import { Link } from '@tanstack/react-location';
import dayjs from 'dayjs';
import { useTranslation } from 'react-i18next';

import { Observation } from '~/api';
import userIcon from '~/assets/png/icon-user.png';
import { Button, Chip, Text } from '~/components';

interface ObservationCardProps {
  observation: Observation;
}

export function ObservationCard({ observation }: ObservationCardProps) {
  const { t } = useTranslation();
  return (
    <Button
      as={Link}
      color="white"
      to={`/observations/${observation.id}/edit`}
      className="active:brightness-90 bg-white flex flex-col rounded-3xl"
      unstyled
    >
      <div className="relative w-full h-40 rounded-t-3xl">
        <img
          className="object-cover rounded-t-3xl h-full w-full"
          src={observation.celestialBody.image}
          alt={t('celestialBody.illustration', { name: observation.celestialBody.name })!}
        />
        <div className="absolute top-5 left-2">{observation.expired && <Chip>{t('observation.expired')}</Chip>}</div>
      </div>
      <Text as="h2" bold centered color="black" className="mt-2">
        {observation.celestialBody.name}
      </Text>
      <Text as="span" color="black" className="ml-4 mt-2">
        {t('common.on-at', {
          date: dayjs(observation.createdAt).format('DD/MM/YYYY'),
          heure: dayjs(observation.createdAt).format('HH:mm'),
        })}
      </Text>
      <div className=" gap-4 py-1.5 ml-4 flex items-center" color="transparent">
        <img className="rounded-full w-10 md:w-12" src={observation.author.avatar ?? userIcon} />
        <Text as="span" color="black">
          {observation.author.username}
        </Text>
      </div>
    </Button>
  );
}
