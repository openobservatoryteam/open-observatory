import { faCamera } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useForm } from 'react-hook-form';
import { OverlayTriggerState } from 'react-stately';

import { UserWithProfile } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { Button, Dialog, Modal, TextArea } from '~/components';

type UpcdateUserFormData = {
  biography: string | null;
  avatar: string | null;
  password: string | null;
};

type EditUserModelProps = {
  user: UserWithProfile;
  state: OverlayTriggerState;
};

function EditUserModel({ state, user }: EditUserModelProps) {
  const { formState, handleSubmit, setValue, register, watch, setError } = useForm<UpdateUserFormData>({
    defaultValues: {
      avatar: user.avatar,
      biography: user.biography,
      password: null,
    },
    resolver: zodResolver(UpdateUserSchema),
  });

  return (
    <Modal state={state}>
      <Dialog title="Edition de l'utilisateur">
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
          <TextArea
            aria-label="Biographie"
            className="mt-10 md:w-[45rem] w-72"
            errorMessage={formState.errors.biography?.message}
            placeholder={t('users.biography')!}
            {...r(register, 'biography')}
          />
          <Button
            className="flex justify-between mt-10 px-4 py-2 w-64"
            rounded
            type="submit"
            disabled={formState.isSubmitting}
          ></Button>
        </form>
      </Dialog>
    </Modal>
  );
}
