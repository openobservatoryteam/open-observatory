import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import * as z from 'zod';

import { updateUser } from '~/api';
import { Button, Slider, Text, Title, ToggleButton } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

type EditNotificationData = {
  notificationsEnabled: boolean;
  radius: number;
};

function EditNotificationPage() {
  const { t } = useTranslation();
  const { user } = useAuthentication();
  const queryClient = useQueryClient();
  const EditNotificationSchema = z.object({
    notificationsEnabled: z.boolean(),
    radius: z.number(),
  });
  const { formState, handleSubmit, setValue, register, watch } = useForm<EditNotificationData>({
    defaultValues: {
      notificationsEnabled: user!.notificationsEnabled,
      radius: user!.radius ?? 25,
    },
    resolver: zodResolver(EditNotificationSchema),
  });
  const notificationMutation = useMutation({
    mutationFn: updateUser,
    onSuccess: (updatedUser) => queryClient.setQueryData(['users', '@me'], updatedUser),
  });

  const onSubmit = (values: EditNotificationData) => {
    notificationMutation.mutate({ username: user!.username, ...values });
  };

  console.log(user);

  return (
    <>
      <Header className="h-16 my-1" />
      <form className="flex flex-col items-center mt-10" onSubmit={handleSubmit(onSubmit)}>
        <Title as="h2">Notifications</Title>
        <ToggleButton
          value={watch('notificationsEnabled')}
          onLabel={t('common.enabled')}
          offLabel={t('common.disabled')}
          handleChange={(value) => setValue('notificationsEnabled', value)}
          className="mt-10"
        />
        <Title as="h2" className="mt-10">
          {t('title.radiusNotification')}
        </Title>
        <Text as="span" className="mt-10">
          {watch('radius')} KM
        </Text>
        <Slider
          aria-label="Rayon de notifications"
          className="mt-10 w-[20rem] md:w-[40rem]"
          defaultValue={watch('radius')}
          maxValue={50}
          minValue={5}
          step={5}
          withMarks
          {...r(register, 'radius')}
        />
        <Button
          className="flex justify-between mt-20 px-4 py-2 w-3/4 md:w-1/2"
          disabled={formState.isSubmitting}
          rounded
          type="submit"
        >
          {t('common.save')}
          <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
        </Button>
      </form>
      <Footer />
    </>
  );
}

export default EditNotificationPage;
