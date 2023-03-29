import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import * as z from 'zod';

import { Button, Slider, Text, Title, ToggleButton } from '~/components';
import { Footer, Header } from '~/layout';
import { registerAdapter as r } from '~/utils';

type EditNotificationData = {
  isActive: boolean;
  distance: number;
};

function EditNotificationPage() {
  const { t } = useTranslation();
  const EditNotificationSchema = z.object({
    isActive: z.boolean(),
    distance: z.number(),
  });
  const { formState, handleSubmit, setValue, register, watch } = useForm<EditNotificationData>({
    defaultValues: {
      isActive: true,
      distance: 25,
    },
    resolver: zodResolver(EditNotificationSchema),
  });

  const onSubmit = (values: EditNotificationData) => {
    console.log(values);
  };

  return (
    <>
      <Header className="h-16 my-1" />
      <form className="flex flex-col items-center mt-10" onSubmit={handleSubmit(onSubmit)}>
        <Title as="h2">Notification</Title>
        <ToggleButton
          value={true}
          onLabel="Activée"
          offLabel="Désactivée"
          handleChange={(value) => setValue('isActive', value)}
          className="mt-10"
        />
        <Title as="h2" className="mt-10">
          Rayon de notifications
        </Title>
        <Text as="span" className="mt-10">
          {watch('distance')} KM
        </Text>
        <Slider
          aria-label="Rayon de notifications"
          className="mt-10 w-[20rem] md:w-[40rem]"
          defaultValue={watch('distance')}
          maxValue={50}
          minValue={5}
          step={5}
          withMarks
          {...r(register, 'distance')}
        />
        <Button
          className="flex justify-between mt-20 px-4 py-2 w-3/4 md:w-1/2"
          rounded
          type="submit"
          disabled={formState.isSubmitting}
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
