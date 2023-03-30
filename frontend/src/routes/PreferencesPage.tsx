import { Link } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';

import { updateUser } from '~/api';
import { Button, Text, ToggleButton } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';

function PreferencesPage() {
  const { user } = useAuthentication();
  const { t } = useTranslation();
  const publicMutation = useMutation({
    mutationFn: updateUser,
  });

  const handleChange = (isPublic: boolean) => {
    publicMutation.mutate({ username: user!.username, isPublic: isPublic });
  };

  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center mt-20">
        <Button as={Link} className="h-16 w-3/4 md:W-1/2 rounded-full" to="/change-password" color="white">
          {t('common.changePassword')}
        </Button>
        <Button as={Link} className="h-16 w-3/4 md:W-1/2 mt-16 rounded-full" to="/edit-notification" color="white">
          {t('common.notificationParameter')}
        </Button>
        <div className="flex mt-10">
          <Text as="span">{t('common.publicProfile')}</Text>
          <ToggleButton
            value={user!.public}
            handleChange={handleChange}
            onLabel={t('common.yes')}
            offLabel={t('common.no')}
            className="ml-5"
          />
        </div>
      </div>
      <Footer />
    </>
  );
}

export default PreferencesPage;
