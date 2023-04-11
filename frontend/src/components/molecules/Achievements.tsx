import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';
import { useTranslation } from 'react-i18next';

import { Achievement } from '~/api';
import { Text } from '~/components';

type AchievementsProps = ComponentPropsWithoutRef<'section'> & {
  data: Achievement[];
};

const ACHIEVEMENTS = {
  FAMOUS: 'FAMOUS.png',
  HUBBLE: 'HUBBLE.jpg',
  JAMES_WEBB: 'JAMES_WEBB.jpg',
  JUDGE: 'JUDGE.jpg',
  OBSERVER: 'OBSERVER.jpg',
} as const;

function Achievements({ className, data, ...props }: AchievementsProps) {
  const { t } = useTranslation();
  return (
    <section className={clsx('flex flex-col gap-y-4', className)} {...props}>
      <Text as="h3" centered>
        Récompenses
      </Text>
      <div className="grid grid-flow-col gap-x-8 overflow-x-scroll px-2">
        {data.map((d) => (
          <article className="h-40 w-24" key={d.achievement}>
            <img
              className="h-24 mx-auto rounded-full w-24"
              src={`/achievements/${ACHIEVEMENTS[d.achievement as keyof typeof ACHIEVEMENTS]}`}
              alt="Récompense"
            />
            <Text centered className="mt-2">
              {t(`users.achievement.${d.achievement}`)}
              {d.level !== 'NONE' && (
                <>
                  <br />
                  <span className="text-sm">{t(`users.achievementLevel.${d.level}`)}</span>
                </>
              )}
            </Text>
          </article>
        ))}
      </div>
    </section>
  );
}

export { Achievements };
