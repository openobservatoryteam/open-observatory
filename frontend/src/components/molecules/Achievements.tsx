import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';
import { useTranslation } from 'react-i18next';

import { Achievement } from '~/api';
import icon from '~/assets/png/icon-user.png';
import { Text } from '~/components';

type AchievementsProps = ComponentPropsWithoutRef<'section'> & {
  data: Achievement[];
};

function Achievements({ className, data, ...props }: AchievementsProps) {
  const { t } = useTranslation();
  return (
    <section className={clsx('flex flex-col gap-y-4', className)} {...props}>
      <Text as="h3" centered>
        Récompenses
      </Text>
      <div className="flex gap-x-8 mx-auto">
        {data.map((d) => (
          <article key={d.achievement}>
            <img className="h-20 mx-auto rounded-lg" src={icon} alt="Récompense" />
            <Text centered className="mt-2">
              {t(`users.achievement.${d.achievement}`)} ({t(`users.achievementLevel.${d.level}`)})
            </Text>
          </article>
        ))}
      </div>
    </section>
  );
}

export { Achievements };
