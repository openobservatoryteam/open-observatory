import { faCamera, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { ChangeEvent } from 'react';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { z } from 'zod';

import { findUserByUsername } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { Button, TextInput } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

type EditProfilType = {
  image: string | null;
  biography: string | null;
};

function EditProfilPage() {
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();
  const { data: user } = useQuery({
    queryKey: ['user', username],
    queryFn: () => findUserByUsername(username),
  });
  const { t } = useTranslation();
  const authentication = useAuthentication();
  if (!user || authentication.user?.username !== username) return null;
  const EditProfilSchema = z.object({
    image: z.string().optional(),
    biography: z.string().max(2048, t('errors.biography.max')!).optional(),
  });
  const { formState, handleSubmit, setValue, register, watch } = useForm<EditProfilType>({
    defaultValues: {
      image: user.avatar,
      biography: user.biography,
    },
    resolver: zodResolver(EditProfilSchema),
  });

  const handleChange = (evt: ChangeEvent<HTMLInputElement>) => {
    if (evt.target.files && evt.target.files[0]) {
      const reader = new FileReader();
      reader.onload = (e) => setValue('image', String(e.target?.result));
      reader.readAsDataURL(evt.target.files[0]);
    }
  };

  const onSubmit = () => {
    return;
  };

  return (
    <>
      <Header className="h-16 my-1" />
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col mt-6 items-center">
        <div className="relative">
          <img
            className="h-32 w-32 mx-auto rounded-full"
            src={watch('image') ?? iconUser}
            alt="Avatar de l'utilisateur"
          />
          <label className="absolute bg-[#D9D9D9] bottom-2 cursor-pointer flex items-center justify-center p-3 rounded-full -right-4">
            <input aria-label="Image de l'objet céleste" className="hidden" onChange={handleChange} type="file" />
            <FontAwesomeIcon icon={faCamera} size="xl" color="black" />
          </label>
        </div>
        <TextInput
          aria-label="Biographie"
          className="mt-10 w-3/4"
          errorMessage={formState.errors.biography?.message}
          placeholder={t('users.biography')!}
          {...r(register, 'biography')}
        />
      </form>
      <div className="flex flex-col items-center mt-10">
        <Button className="w-1/2" as={Link} to="/">
          Préférences
        </Button>
        <Button as={Link} className="mt-10 w-1/2" to="/">
          À propos
        </Button>
        <Button className="flex justify-between mt-20 px-4 py-2 w-1/2" rounded type="submit">
          {t('common.save')}
          <FontAwesomeIcon className="ml-3" color="black" icon={faSave} size="1x" />
        </Button>
      </div>
      <Footer />
    </>
  );
}

export default EditProfilPage;
