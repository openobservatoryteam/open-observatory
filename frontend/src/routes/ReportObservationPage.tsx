import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useQuery } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';

import { celestialBodies } from '~/api';
import celeste from '~/assets/png/celeste.png';
import { Button, Map, Select, TextInput, Title } from '~/components';
import { MarkerInput } from '~/components/molecules/MarkerInput';
import { registerAdapter as r } from '~/utils';

const visibilityOptions = [
  { name: 'Bien visible', value: 'CLEARLY_VISIBLE' },
  { name: 'Visible', value: 'VISIBLE' },
  { name: 'Légèrement visible', value: 'SLIGHTLY_VISIBLE' },
  { name: 'Difficilement visible', value: 'BARELY_VISIBLE' },
] as const;

function ReportObservationPage() {
  const { data: celestialBodiesData } = useQuery({
    queryFn: celestialBodies.getAll,
    queryKey: ['celestial-bodies'],
  });
  const { handleSubmit, register, setValue, watch } = useForm<{
    celestialBody: number | null;
    date: string | null;
    orientation: number | null;
    visibility: (typeof visibilityOptions)[number]['value'] | null;
    position: [number, number];
  }>({
    defaultValues: {
      celestialBody: null,
      date: null,
      orientation: null,
      visibility: null,
      position: [0, 0],
    },
  });
  const selectedBody = watch('celestialBody') ?? -1;
  return (
    <>
      <Title as="h2" className="mt-4 mb-2" centered>
        Création d&apos;une observation
      </Title>
      <img
        className="object-cover h-64 w-full"
        src={celestialBodiesData?.data.find((c) => c.id === +selectedBody)?.image ?? celeste}
        alt="Objet céleste"
      />
      <form onSubmit={handleSubmit((v) => console.table(v))}>
        <div className="bg-slate-600">
          <div className="flex flex-col gap-y-4 max-w-screen-sm mx-auto py-4 w-3/4">
            <Select
              options={celestialBodiesData?.data.map((c) => ({ name: c.name, value: `${c.id}` })) ?? []}
              placeholder="Objet céleste observé"
              {...register('celestialBody', { required: true })}
            />
            <input
              className="px-3 py-2 rounded-3xl"
              type="datetime-local"
              aria-label="Date"
              placeholder="Date"
              {...register('date', { required: true })}
            />
            <TextInput
              aria-label="Degré d'orientation"
              placeholder="Degré d'orientation"
              {...r(register, 'orientation', { required: true })}
            />
            <Select
              options={visibilityOptions}
              placeholder="Visibilité de l'observation"
              {...register('visibility', { required: true })}
            />
          </div>
        </div>
        <Map className="h-64">
          <MarkerInput onMove={(p) => setValue('position', [p.lng, p.lat])} />
        </Map>
        <div className="bg-slate-600 flex justify-center py-8">
          <Button type="submit">
            Enregistrer
            <FontAwesomeIcon className="ml-20" icon={faSave} />
          </Button>
        </div>
      </form>
    </>
  );
}

export default ReportObservationPage;
