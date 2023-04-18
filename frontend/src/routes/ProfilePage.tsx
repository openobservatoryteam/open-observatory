import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useTranslation } from 'react-i18next';

import { findUserByUsername } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { Achievements, Button, Text } from '~/components';
import { Header } from '~/layout';
import { useAuthentication } from '~/providers';

function ProfilePage() {
  const { t } = useTranslation();
  const authentication = useAuthentication();
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();
  const { data: user } = useQuery({
    queryKey: ['user', username],
    queryFn: () => findUserByUsername(username),
  });
  if (!user) return null;
  const isSelf = authentication.user?.username === user.username;
  return (
    <>
      <DocumentTitle>
        {t(isSelf ? 'document.title.selfProfile' : 'document.title.userProfile', { name: user.username })}
      </DocumentTitle>
      <Header className="h-16 my-1" />
      <div className="flex flex-col mt-6">
        <img className="h-32 w-32 mx-auto rounded-full" src={user.avatar ?? iconUser} alt="Avatar de l'utilisateur" />
        <Text as="h2" centered className="mt-3">
          {user.username}
        </Text>
        <Text centered as="p" className="mt-6 md:w-[45rem] w-72 mx-auto break-words">
          {user.biography}
        </Text>
        {user.achievements.length > 0 && <Achievements className="mt-12" data={user.achievements} />}
        <section className="flex flex-col gap-y-4 mt-12">
          <Text as="h3" centered>
            {t('users.karma')}
          </Text>
          <Text centered className="text-8xl">
            {user.karma}
          </Text>
        </section>
        <div
          className={`grid ${
            isSelf ? 'grid-cols-2' : 'grid-cols-1'
          } h-20 gap-x-4 max-w-screen-md mt-12 mx-auto px-4 mb-4`}
        >
          <Button as={Link} className="w-full" to="observations">
            {t('users.observationsHistory')}
          </Button>
          {isSelf && (
            <Button as={Link} className="w-full" to="edit">
              {t('users.edit')}
            </Button>
          )}
        </div>
      </div>
    </>
  );
}

export default ProfilePage;
