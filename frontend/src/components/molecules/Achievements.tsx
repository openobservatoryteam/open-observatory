import clsx from 'clsx';
import { ComponentPropsWithoutRef, useState } from 'react';
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
  const [achievement, setAchievement] = useState<Achievement | null>(null);

  return (
    <section className={clsx('flex flex-col gap-y-4', className)} {...props}>
      <Text as="h3" centered>
        Récompenses
      </Text>
      <div className="grid grid-flow-col gap-x-8 overflow-x-scroll px-2 md:w-[40rem] w-96 mx-auto">
        {data.map((d) => (
          <article
            className="h-40 w-24"
            key={d.achievement}
            onClick={() => (achievement === d ? setAchievement(null) : setAchievement(d))}
          >
            <img
              className="h-24 mx-auto rounded-full w-24"
              src={`/achievements/${ACHIEVEMENTS[d.achievement as keyof typeof ACHIEVEMENTS]}`}
              alt="Récompense"
            />
            <Text centered className="mt-2">
              {t(`users.achievement.${d.achievement}`)}
              {d.level.name !== 'NONE' && (
                <>
                  <br />
                  <span className="text-sm">{t(`users.achievementLevel.${d.level.name}`)}</span>
                </>
              )}
            </Text>
          </article>
        ))}
      </div>
      {achievement && (
        <div className="bg-white mx-auto max-h-36 rounded-xl p-4 overflow-y-scroll md:w-[500px] w-96">
          <Text as="h3" bold centered color="black">
            {t(`users.achievement.${achievement.achievement}`) + ' '}
            {achievement.level.name !== 'NONE' && (
              <Text as="span" color="black">
                ({t(`users.achievementLevel.${achievement.level.name}`)})
              </Text>
            )}
          </Text>
          <Text as="p" centered className="mt-4" color="black">
            {t(`achievement.description.${achievement.achievement}`, {
              count: achievement.level.count !== 0 ? achievement.level.count : undefined,
            })}
          </Text>
        </div>
      )}
    </section>
  );
}

export { Achievements };
