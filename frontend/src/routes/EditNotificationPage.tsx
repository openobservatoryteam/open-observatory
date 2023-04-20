import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import * as z from 'zod';

import { updateUser } from '~/api';
import { Button, Slider, Text, Title, ToggleButton } from '~/components';
import { useNotifications, usePush } from '~/hooks';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

type EditNotificationData = {
  notificationEnabled: boolean;
  notificationRadius: number;
};

const EditNotificationSchema = z.object({
  notificationEnabled: z.boolean(),
  notificationRadius: z.number(),
});

function EditNotificationPage() {
  const { user } = useAuthentication();
  const notifications = useNotifications();
  const push = usePush();
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { formState, handleSubmit, setValue, register, watch } = useForm<EditNotificationData>({
    defaultValues: {
      notificationEnabled: user!.notificationEnabled,
      notificationRadius: user!.notificationRadius,
    },
    resolver: zodResolver(EditNotificationSchema),
  });
  const notificationMutation = useMutation({
    mutationFn: updateUser,
    onSuccess: (updatedUser) => queryClient.setQueryData(['users', '@me'], updatedUser),
  });
  const onSubmit = async (values: EditNotificationData) => {
    notificationMutation.mutate({ username: user!.username, ...values });
    const result = await notifications.request();
    if (values.notificationEnabled && result === 'granted' && push.supported) {
      push.subscribe();
    }
  };
  return (
    <>
      <Header className="h-16 my-1" />
      <form className="flex flex-col items-center mt-10" onSubmit={handleSubmit(onSubmit)}>
        <Title as="h2">Notifications</Title>
        <ToggleButton
          value={watch('notificationEnabled')}
          onLabel={t('common.enabled')}
          offLabel={t('common.disabled')}
          handleChange={(value) => setValue('notificationEnabled', value)}
          className="mt-10"
        />
        <Title as="h2" className="mt-10">
          {t('title.radiusNotification')}
        </Title>
        <Text as="span" className="mt-10">
          {watch('notificationRadius')} KM
        </Text>
        <Slider
          aria-label="Rayon de notifications"
          className="mt-10 w-[20rem] md:w-[40rem]"
          defaultValue={watch('notificationRadius')}
          maxValue={50}
          minValue={5}
          step={5}
          withMarks
          {...r(register, 'notificationRadius')}
        />
        <Button
          className="flex justify-between mt-20 px-4 py-2 w-3/4 md:w-1/2"
          isDisabled={formState.isSubmitting || !notifications.supported || !push.supported}
          rounded
          type="submit"
        >
          {t('common.save')}
          <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
        </Button>
        {!(notifications.supported && push.supported) && (
          <Text centered color="red">
            Cet appareil ne supporte pas la réception de notifications Push.
          </Text>
        )}
        {notifications.status === 'denied' && (
          <Text centered color="red">
            Veuillez autoriser les notifications pour ce site web.
          </Text>
        )}
      </form>
      <Footer />
    </>
  );
}

export default EditNotificationPage;
