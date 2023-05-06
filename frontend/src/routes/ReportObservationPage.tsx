import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useNavigate } from '@tanstack/react-location';
import { useMutation, useQuery } from '@tanstack/react-query';
import dayjs from 'dayjs';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';

import { CreateObservationData, ObservationVisibility, createObservation, findAllCelestialBodies } from '~/api';
import celeste from '~/assets/png/celeste.png';
import { Button, DatePicker, MarkerInput, Select, TextInput, Title, WorldMap } from '~/components';
import { Header } from '~/layout';
import { registerAdapter as r } from '~/utils';

function ReportObservationPage() {
  const { t } = useTranslation();
  const visibilityLevels: { name: string; value: ObservationVisibility }[] = [
    { name: t('visibility.clearly'), value: 'CLEARLY_VISIBLE' },
    { name: t('visibility.visible'), value: 'VISIBLE' },
    { name: t('visibility.slightly'), value: 'SLIGHTLY_VISIBLE' },
    { name: t('visibility.barely'), value: 'BARELY_VISIBLE' },
  ];
  const { data: celestialBodiesData } = useQuery({
    queryFn: findAllCelestialBodies,
    queryKey: ['celestial-bodies'],
  });
  const { formState, handleSubmit, register, setValue, watch } = useForm<CreateObservationData>();
  const navigate = useNavigate();
  const { isLoading, mutate } = useMutation({
    mutationFn: createObservation,
    onSuccess: ({ id }) => navigate({ to: `/observations/${id}` }),
  });
  const selectedBody = watch('celestialBodyId') ?? -1;
  const maxDate = dayjs().format('YYYY-MM-DDTHH:mm:ss.SSS');
  return (
    <>
      <DocumentTitle>{t('document.title.newObservation')}</DocumentTitle>
      <Header className="h-16 mt-1" />
      <Title as="h2" className="mb-2" centered>
        {t('title.newObservation')}
      </Title>
      <img
        className="object-cover h-64 w-full"
        src={celestialBodiesData?.data.find((c) => c.id === +selectedBody)?.image ?? celeste}
        alt="Objet céleste"
      />
      <form
        onSubmit={handleSubmit(({ celestialBodyId, orientation, timestamp, ...data }) =>
          mutate({
            celestialBodyId: +celestialBodyId,
            orientation: +orientation,
            timestamp: dayjs(timestamp).toISOString(),
            ...data,
          }),
        )}
      >
        <div className="bg-slate-600">
          <div className="flex flex-col gap-y-4 max-w-screen-sm mx-auto py-4 w-3/4">
            <Select
              options={celestialBodiesData?.data.map((c) => ({ name: c.name, value: `${c.id}` })) ?? []}
              placeholder={t('celestialBody.observed')!}
              {...register('celestialBodyId', { required: true })}
            />
            <DatePicker
              aria-label="Date"
              errorMessage={formState.errors.timestamp?.message}
              max={maxDate}
              placeholder="Date"
              {...register('timestamp', { max: maxDate, required: true })}
            />
            <TextInput
              aria-label={t('observation.description')!}
              errorMessage={formState.errors.description?.message}
              placeholder={t('observation.description')!}
              {...r(register, 'description', {
                required: false,
                validate: (o) =>
                  o == null || String(o)?.length <= 500 ? undefined : t('errors.observation.description')!,
              })}
            />
            <TextInput
              aria-label="Degré d'orientation"
              errorMessage={formState.errors.orientation?.message}
              placeholder={t('observation.angle')!}
              {...r(register, 'orientation', {
                required: true,
                validate: (o) => (/\d+/.test(String(o)) ? undefined : t('errors.angle')!),
              })}
            />
            <Select
              options={visibilityLevels}
              placeholder={t('observation.visibility')!}
              {...register('visibility', { required: true })}
            />
          </div>
        </div>
        <WorldMap className="h-60" worldCopyJump>
          <MarkerInput
            onMove={(p) => {
              setValue('latitude', p.lat);
              setValue('longitude', p.lng);
            }}
          />
        </WorldMap>
        <div className="bg-slate-600 flex justify-center py-3">
          <Button isDisabled={isLoading} type="submit">
            {t('common.save')}
            <FontAwesomeIcon className="ml-20" icon={faSave} />
          </Button>
        </div>
      </form>
    </>
  );
}

export default ReportObservationPage;
