import { faCamera, faSave, faTrash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ChangeEvent } from 'react';
import { useForm } from 'react-hook-form';
import { OverlayTriggerState } from 'react-stately';
import * as z from 'zod';

import { celestialBodies, CelestialBody } from '@/api';
import { Button, Dialog, Modal, Slider, Text, TextInput } from '@/components';
import { registerAdapter as r } from '@/utils';

const UpdateCelestialBodySchema = z.object({
  name: z
    .string()
    .min(4, "Le nom de l'objet céleste doit être composé d'au moins 4 caractères.")
    .max(64, "Le nom de l'objet céleste ne doit pas dépasser 64 caractères."),
  validityTime: z
    .number()
    .int("La validité de l'objet céleste doit s'exprimer avec un nombre entier (heures).")
    .min(1, "La validité de l'objet céleste doit être d'au moins 1 heure.")
    .max(12, "La validité de l'objet céleste ne peut dépasser 12 heures."),
  image: z.string().optional(),
});

type EditCelestialBodyProps = {
  celestialBody: CelestialBody;
  state: OverlayTriggerState;
};

export function EditCelestialBody({ celestialBody, state }: EditCelestialBodyProps) {
  const queryClient = useQueryClient();
  const { formState, handleSubmit, setValue, register, watch } = useForm({
    defaultValues: {
      name: celestialBody.name,
      image: celestialBody.image,
      validityTime: celestialBody.validityTime,
    },
    resolver: zodResolver(UpdateCelestialBodySchema),
  });
  const update = useMutation({
    mutationFn: celestialBodies.update,
    mutationKey: ['celestial-bodies', celestialBody.id],
    onSuccess: () => {
      queryClient.invalidateQueries(['celestial-bodies']);
      state.close();
    },
  });
  const remove = useMutation({
    mutationFn: celestialBodies.remove,
    mutationKey: ['celestial-bodies', celestialBody.id],
    onSuccess: () => {
      queryClient.invalidateQueries(['celestial-bodies']);
      state.close();
    },
  });
  const handleChange = (evt: ChangeEvent<HTMLInputElement>) => {
    if (evt.target.files && evt.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => setValue('image', e.target?.result?.toString());
      reader.readAsDataURL(evt.target.files[0]);
    }
  };
  return (
    <Modal isDismissable state={state}>
      <Dialog title="Modification d'un objet céleste">
        <form
          className="flex flex-col items-center"
          onSubmit={handleSubmit((data) => update.mutate({ id: celestialBody.id, ...data }))}
        >
          {watch('image') ? (
            <div className="flex items-center justify-center relative w-3/4">
              <img src={watch('image')} className="h-60 object-cover rounded-2xl w-full" />
              <label className="absolute bg-[#D9D9D9] bottom-3 cursor-pointer flex items-center justify-center p-3 right-5 rounded-full">
                <input aria-label="Image de l'objet céleste" className="hidden" onChange={handleChange} type="file" />
                <FontAwesomeIcon icon={faCamera} size="xl" color="black" />
              </label>
            </div>
          ) : (
            <label className="bg-[#D9D9D9] cursor-pointer flex h-60 items-center justify-center py-10 rounded-2xl w-3/4">
              <input aria-label="Image de l'objet céleste" className="hidden" onChange={handleChange} type="file" />
              <FontAwesomeIcon icon={faCamera} size="5x" color="black" />
            </label>
          )}
          <div className="flex items-center justify-evenly mt-8 w-full">
            <Text as="span" className="mr-5">
              Nom
            </Text>
            <TextInput
              aria-label="Nom de l'objet céleste"
              className="w-3/4"
              errorMessage={formState.errors.name?.message}
              placeholder="Nom de l'objet céleste"
              {...r(register, 'name')}
            />
          </div>
          <div className="flex items-center justify-evenly mt-8 w-full">
            <Text as="span" className="mr-5">
              Validité
            </Text>
            <div className="flex items-center w-3/4">
              <Slider
                aria-label="Validité de l'objet céleste"
                className="w-full"
                defaultValue={celestialBody.validityTime}
                maxValue={10}
                minValue={1}
                step={1}
                withMarks
                {...r(register, 'validityTime')}
              />
              <Text as="span" className="ml-5 w-1/5">
                {watch('validityTime')} {watch('validityTime') > 1 ? ' heures' : ' heure'}
              </Text>
            </div>
          </div>
          <div className="flex items-center justify-center mt-10 w-full">
            <Button className="flex justify-between px-4 py-2 w-1/4" rounded type="submit">
              Enregistrer
              <FontAwesomeIcon color="black" icon={faSave} size="1x" />
            </Button>
            <Button
              className="flex justify-between ml-5 px-4 py-2 w-1/4"
              color="red"
              onPress={() => remove.mutate({ id: celestialBody.id })}
              rounded
            >
              Supprimer
              <FontAwesomeIcon color="white" icon={faTrash} size="1x" />
            </Button>
          </div>
        </form>
      </Dialog>
    </Modal>
  );
}
