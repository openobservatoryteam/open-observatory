import { faCamera, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ChangeEvent } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { OverlayTriggerState } from 'react-stately';
import { z } from 'zod';

import { ApplicationError, UserWithProfile, updateUser } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { Button, Dialog, Modal, TextArea, TextInput } from '~/components';
import { registerAdapter as r } from '~/utils';

type UpdateUserFormData = {
  biography: string | null;
  avatar: string | null;
  password: string | null;
};

type EditUserModelProps = {
  user: UserWithProfile;
  state: OverlayTriggerState;
  onClose: () => void;
};

export function EditUserModal({ state, user, onClose }: EditUserModelProps) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const UpdateUserSchema = z.object({
    avatar: z.string().nullable(),
    biography: z.string().max(500, t('errors.biography.max')!).optional(),
    password: z
      .string()
      .regex(/.*[A-Z].*/, t('errors.password.upper')!)
      .regex(/.*[a-z].*/, t('errors.password.lower')!)
      .regex(/.*\d.*/, t('errors.password.number')!)
      .regex(/.*[`~<>?,./!@#$%^&*()\-_+="'|{}[\];:].*/, t('errors.password.specialCharacter')!)
      .min(8, t('errors.password.min')!)
      .max(32, t('errors.password.max')!)
      .or(z.string().optional()),
  });
  const { formState, handleSubmit, setValue, register, watch, setError } = useForm<UpdateUserFormData>({
    defaultValues: {
      avatar: user.avatar,
      biography: user.biography,
      password: '',
    },
    resolver: zodResolver(UpdateUserSchema),
  });

  const handleChange = (evt: ChangeEvent<HTMLInputElement>) => {
    if (evt.target.files && evt.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => setValue('avatar', String(e.target?.result));
      reader.readAsDataURL(evt.target.files[0]);
    }
  };

  const updateUserMutation = useMutation({
    mutationFn: updateUser,
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['users', '@me'], updatedUser);
      state.close();
      onClose();
    },
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'BIOGRAPHY_REACHED_500_CHARACTERS')
        setError('biography', { message: t('errors.biography.max')! });
    },
  });

  const onSubmit = ({ password, ...values }: UpdateUserFormData) => {
    updateUserMutation.mutate({ username: user?.username, password: password || undefined, ...values });
  };

  return (
    <Modal state={state}>
      <Dialog title="Edition de l'utilisateur" onClose={() => state.close()}>
        <form className="flex flex-col mt-6 items-center w-full" onSubmit={handleSubmit(onSubmit)}>
          <div className="relative">
            <img
              className="h-32 w-32 mx-auto rounded-full"
              src={watch('avatar') ?? iconUser}
              alt="Avatar de l'utilisateur"
            />
            <label className="absolute bg-[#D9D9D9] bottom-2 cursor-pointer flex items-center justify-center p-3 rounded-full -right-4">
              <input aria-label="Image de l'objet céleste" className="hidden" onChange={handleChange} type="file" />
              <FontAwesomeIcon icon={faCamera} size="xl" color="black" />
            </label>
          </div>
          <TextInput
            className="mt-10 mx-16"
            errorMessage={formState.errors.password?.message}
            placeholder={t('users.password')!}
            type="password"
            {...r(register, 'password')}
          />
          <TextArea
            className="mt-10 mx-12"
            errorMessage={formState.errors.biography?.message}
            placeholder={t('users.biography')!}
            {...r(register, 'biography')}
          />
          <Button
            className="flex justify-between mt-10 px-4 py-2 w-64"
            rounded
            type="submit"
            disabled={formState.isSubmitting}
          >
            {t('common.save')}
            <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
          </Button>
        </form>
      </Dialog>
    </Modal>
  );
}
