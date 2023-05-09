import { faSave, faTrash } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { OverlayTriggerState } from 'react-stately';
import * as z from 'zod';

import { Observation, UpdateObservationData, deleteObservation, updateObservation } from '~/api';
import { Button, Dialog, Modal, Text, TextInput } from '~/components';
import { registerAdapter as r } from '~/utils';

type EditObservationProps = {
  observation: Observation;
  state: OverlayTriggerState;
};

export function EditObservation({ observation, state }: EditObservationProps) {
  const queryClient = useQueryClient();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const UpdateObservationSchema = z.object({
    description: z.string().max(512, 'La longueur de la description ne doit pas dépasser 512 caractères.').optional(),
  });
  const { formState, handleSubmit, register } = useForm<UpdateObservationData>({
    defaultValues: {
      description: observation.description ?? undefined,
    },
    resolver: zodResolver(UpdateObservationSchema),
  });
  const update = useMutation({
    mutationFn: updateObservation,
    onSuccess: (observation) => {
      console.log(observation);
      queryClient.setQueryData(['observations', observation.id], observation);
      state.close();
    },
  });
  const remove = useMutation({
    mutationFn: deleteObservation,
    onSuccess: () => {
      queryClient.setQueryData(['observations', observation.id], null);
      state.close();
      navigate({ to: '/' });
    },
  });
  return (
    <Modal isDismissable state={state}>
      <Dialog onClose={() => state.close()} title={t('title.updateObservation')}>
        <form
          className="flex flex-col items-center"
          onSubmit={handleSubmit((data) => update.mutate({ id: observation.id, ...data }))}
        >
          <div className="flex items-center justify-evenly mt-8 w-full">
            <Text as="span" className="mr-5">
              {t('common.description')}
            </Text>
            <TextInput
              aria-label="Description"
              className="w-3/4"
              errorMessage={formState.errors.description?.message}
              placeholder="Description"
              {...r(register, 'description')}
            />
          </div>
          <div className="flex items-center justify-center mt-10 w-full">
            <Button className="flex justify-between px-4 py-2" rounded type="submit">
              {t('common.save')}
              <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
            </Button>
            <Button
              className="flex justify-between ml-5 px-4 py-2"
              color="red"
              onPress={() => remove.mutate({ id: observation.id })}
              rounded
            >
              {t('common.delete')}
              <FontAwesomeIcon className="ml-3" color="white" icon={faTrash} size="1x" />
            </Button>
          </div>
        </form>
      </Dialog>
    </Modal>
  );
}
