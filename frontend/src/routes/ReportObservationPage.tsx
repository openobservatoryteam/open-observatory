import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { LatLng } from 'leaflet';
import { useEffect, useState } from 'react';
import { useForm } from 'react-hook-form';

import celeste from '~/assets/png/celeste.png';
import { Button, Map, Select, TextInput, Title } from '~/components';
import { MarkerInput } from '~/components/molecules/MarkerInput';
import { registerAdapter as r } from '~/utils';

const celestialBodies = [
  { name: 'Mars', value: 'mars' },
  { name: 'Neptune', value: 'neptune' },
  { name: 'Sagittarius', value: 'sagittarius' },
];

const visibilityOptions = [
  { name: 'Bien visible', value: 'CLEARLY_VISIBLE' },
  { name: 'Visible', value: 'VISIBLE' },
  { name: 'Légèrement visible', value: 'SLIGHTLY_VISIBLE' },
  { name: 'Difficilement visible', value: 'BARELY_VISIBLE' },
];

function ReportObservationPage() {
  const { handleSubmit, register, setValue } = useForm({
    defaultValues: { celestialBody: null, date: null, orientation: null, visibility: null, position: [0, 0] },
  });
  return (
    <>
      <Title as="h2" className="mt-4 mb-2" centered>
        Création d&apos;une observation
      </Title>
      <img className="object-cover h-64 w-full" src={celeste} alt="Objet céleste" />
      <form onSubmit={handleSubmit((v) => console.table(v))}>
        <div className="bg-slate-600">
          <div className="flex flex-col gap-y-4 max-w-screen-sm mx-auto py-4 w-3/4">
            <Select
              options={celestialBodies}
              placeholder="Objet céleste observé"
              {...register('celestialBody', { required: true })}
            />
            <TextInput aria-label="Date" placeholder="Date" {...r(register, 'date', { required: true })} />
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
