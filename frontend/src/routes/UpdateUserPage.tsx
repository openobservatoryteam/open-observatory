import { faCamera, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useMatch, useNavigate } from '@tanstack/react-location';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { ChangeEvent } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';

import { ApplicationError, updateUser } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { Button, TextArea } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

type UpdateUserFormData = {
  biography: string | null;
  avatar: string | null;
};

function UpdateUserPage() {
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();
  const queryClient = useQueryClient();

  const { t } = useTranslation();
  const { user } = useAuthentication();

  const UpdateUserSchema = z.object({
    avatar: z.string().nullable(),
    biography: z.string().max(500, t('errors.biography.max')!).optional(),
  });

  const { formState, handleSubmit, setValue, register, watch, setError } = useForm<UpdateUserFormData>({
    defaultValues: {
      avatar: user?.avatar,
      biography: user?.biography,
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

  const navigate = useNavigate();

  const updateUserMutation = useMutation({
    mutationFn: updateUser,
    onSuccess: (updatedUser) => {
      queryClient.setQueryData(['users', '@me'], updatedUser);
      navigate({ to: `/users/${username}`, replace: true });
    },
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'BIOGRAPHY_REACHED_500_CHARACTERS')
        setError('biography', { message: t('errors.biography.max')! });
    },
  });

  const onSubmit = (values: UpdateUserFormData) => {
    updateUserMutation.mutate({ username: user?.username, ...values });
  };

  if (!user || user?.username !== username) return null;

  return (
    <>
      <Header className="h-16 my-1" />
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
        >
          {t('common.save')}
          <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
        </Button>
        <Button className="mt-10 w-64" as={Link} to="/preferences">
          {t('common.preferences')}
        </Button>
        <Button as={Link} className="mt-10 w-64" to="/about-us">
          {t('common.about')}
        </Button>
      </form>
      <Footer />
    </>
  );
}

export default UpdateUserPage;
