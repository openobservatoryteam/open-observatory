import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';

import { Achievement } from '~/api';
import icon from '~/assets/png/icon-user.png';
import { Text } from '~/components';

type AchievementsProps = ComponentPropsWithoutRef<'section'> & {
  data: Achievement[];
};

function Achievements({ className, data, ...props }: AchievementsProps) {
  return (
    <section className={clsx('flex flex-col gap-y-4', className)} {...props}>
      <Text as="h3" centered>
        Récompenses
      </Text>
      <div className="flex justify-around">
        {data.map((d) => (
          <article className="flex flex-col gap-y-2 px-3 py-2 rounded-lg" key={d.id}>
            <img className="h-20" src={icon} alt="Récompense" />
            <Text centered>{d.name}</Text>
          </article>
        ))}
      </div>
    </section>
  );
}

export { Achievements };
