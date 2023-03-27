import { faCamera, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ChangeEvent } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { OverlayTriggerState } from 'react-stately';
import * as z from 'zod';

import { CreateCelestialBodyData, createCelestialBody } from '~/api';
import { Button, Dialog, Modal, Slider, Text, TextInput } from '~/components';
import { registerAdapter as r } from '~/utils';

type CreateCelestialBodyModalProps = { state: OverlayTriggerState };
export function CreateCelestialBodyModal({ state }: CreateCelestialBodyModalProps) {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const CreateCelestialBodySchema = z.object({
    name: z.string().min(4, t('errors.celestialBody.name.min')!).max(64, t('errors.celestialBody.name.max')!),
    validityTime: z
      .number()
      .int(t('errors.celestialBody.validity.type')!)
      .min(1, t('errors.celestialBody.validity.min')!)
      .max(12, t('errors.celestialBody.validity.max')!),
    image: z.string().optional(),
  });
  const { formState, handleSubmit, register, setValue, watch } = useForm<CreateCelestialBodyData>({
    defaultValues: {
      name: '',
      image: '',
      validityTime: 1,
    },
    resolver: zodResolver(CreateCelestialBodySchema),
  });
  const create = useMutation({
    mutationFn: createCelestialBody,
    onSuccess: () => {
      queryClient.invalidateQueries(['celestial-bodies']);
      state.close();
    },
  });
  const handleChange = (evt: ChangeEvent<HTMLInputElement>) => {
    if (evt.target.files && evt.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => e.target?.result && setValue('image', e.target?.result?.toString());
      reader.readAsDataURL(evt.target.files[0]);
    }
  };
  return (
    <Modal isDismissable state={state}>
      <Dialog onClose={() => state.close()} title={t('title.createCelestialBody')}>
        <form className="flex flex-col items-center" onSubmit={handleSubmit((data) => create.mutate(data))}>
          {watch('image') ? (
            <div className="flex items-center justify-center relative w-3/4">
              <img src={watch('image')} className="h-60 object-cover rounded-2xl w-full" />
              <label className="absolute bg-[#D9D9D9] bottom-3 cursor-pointer flex items-center justify-center p-3 right-5 rounded-full">
                <input className="hidden" onChange={handleChange} type="file" />
                <FontAwesomeIcon icon={faCamera} size="xl" color="black" />
              </label>
            </div>
          ) : (
            <label className="bg-[#D9D9D9] cursor-pointer flex h-60 items-center justify-center py-10 rounded-2xl w-3/4">
              <input className="hidden" onChange={handleChange} type="file" />
              <FontAwesomeIcon icon={faCamera} size="5x" color="black" />
            </label>
          )}
          <div className="flex items-center justify-evenly mt-8 w-full">
            <Text as="span" className="mr-5">
              {t('common.name')}
            </Text>
            <TextInput
              aria-label="Nom de l'objet céleste"
              className="w-3/4"
              defaultValue={formState.defaultValues?.name}
              errorMessage={formState.errors.name?.message}
              placeholder={t('celestialBody.name')!}
              {...r(register, 'name')}
            />
          </div>
          <div className="flex items-center justify-evenly mt-8 w-full">
            <Text as="span" className="mr-5">
              {t('common.validity')}
            </Text>
            <div className="flex items-center w-3/4">
              <Slider
                aria-label="Validité des observations"
                className="w-full"
                defaultValue={formState.defaultValues?.validityTime}
                maxValue={10}
                minValue={1}
                step={1}
                withMarks
                {...r(register, 'validityTime')}
              />
              <Text as="span" className="ml-5 w-1/5">
                {watch('validityTime')} {watch('validityTime') > 1 ? ' ' + t('common.hours') : ' ' + t('common.hour')}
              </Text>
            </div>
          </div>
          <Button className="px-4 py-2 mt-10 flex justify-between" rounded type="submit">
            {t('common.save')}
            <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
          </Button>
        </form>
      </Dialog>
    </Modal>
  );
}
