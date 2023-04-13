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
  HUBBLE: 'HUBBLE.png',
  JAMES_WEBB: 'JAMES_WEBB.png',
  JUDGE: 'JUDGE.png',
  OBSERVER: 'OBSERVER.png',
} as const;

function Achievements({ className, data, ...props }: AchievementsProps) {
  const { t } = useTranslation();
  const [achievement, setAchievement] = useState<Achievement | null>(null);

  return (
    <section className={clsx('flex flex-col gap-y-4', className)} {...props}>
      <Text as="h3" centered>
        Récompenses
      </Text>
      <div className="grid grid-flow-col gap-x-8 overflow-x-scroll px-2 max-w-[100vw] mx-auto">
        {data.map((d) => (
          <article
            className="h-40 w-24 hover:cursor-pointer"
            key={d.achievement}
            onClick={() => (achievement === d ? setAchievement(null) : setAchievement(d))}
          >
            <img
              className="h-24 mx-auto object-cover w-24"
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
        <div className="bg-white max-h-36 rounded-xl mx-auto p-4 overflow-y-scroll max-w-[calc(100vw-4vw)] w-[30rem]">
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
