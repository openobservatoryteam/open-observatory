import clsx from 'clsx';
import dayjs from 'dayjs';
import { ComponentPropsWithoutRef, ElementType } from 'react';

import { Observation } from '~/api';
import { Text } from '~/components';
import { AsProps } from '~/types';

type ObservationItemProps<C extends ElementType> = AsProps<C> &
  ComponentPropsWithoutRef<C> & {
    observation: Observation;
  };

function ObservationItem<C extends ElementType = 'div'>({
  as,
  className,
  observation,
  ...props
}: ObservationItemProps<C>): JSX.Element {
  const Component = as ?? 'div';
  return (
    <Component className={clsx('flex items-center bg-white rounded-full w-3/4 h-24 px-5', className)} {...props}>
      <img src={observation.celestialBody.image} alt="Image de l'objet céleste" className="object-contain h-20 w-20 rounded-full" />
      <div className="flex flex-col w-full h-full md:mr-20 mr-14 pt-4">
        <Text as="h1" bold centered color="black">
          {observation.celestialBody.name}
        </Text>
        <Text as="p" centered color="black" className="text-sm">
          {dayjs(observation.createdAt).format('le DD/MM/YYYY')}
        </Text>
        {observation.isExpired && (
          <Text as="p" centered color="black" className="text-[0.8em]">
            Expiré
          </Text>
        )}
      </div>
    </Component>
  );
}

export default ObservationItem;
