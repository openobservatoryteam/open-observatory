import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import z from 'zod';

import { ApplicationError, CreateUserData, postCreateUser } from '~/api';
import { Button, Text, TextInput, Title } from '~/components';
import { Footer, Header } from '~/layout';
import { registerAdapter as r } from '~/utils';

function RegistrationPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const RegistrationSchema = z
    .object({
      username: z
        .string()
        .regex(/^[A-Za-z]/, t('errors.username.beginLetter')!)
        .regex(/^\w*$/, t('errors.username.contain')!)
        .max(32, t('errors.username.max')!),
      password: z
        .string()
        .regex(/.*[A-Z].*/, t('errors.password.upper')!)
        .regex(/.*[a-z].*/, t('errors.password.lower')!)
        .regex(/.*\d.*/, t('errors.password.number')!)
        .regex(/.*[`~<>?,./!@#$%^&*()\-_+="'|{}[\];:].*/, t('errors.password.specialCharacter')!)
        .min(8, t('errors.password.min')!)
        .max(32, t('errors.password.max')!),
      passwordConfirmation: z.string(),
      biography: z.string().max(2048, t('errors.biography.max')!).optional(),
    })
    .refine((data) => data.password === data.passwordConfirmation, {
      message: t('errors.password.notMatch')!,
      path: ['passwordConfirmation'],
    });
  const { formState, handleSubmit, register, setError } = useForm<CreateUserData>({
    resolver: zodResolver(RegistrationSchema),
  });
  const registration = useMutation({
    mutationFn: postCreateUser,
    onSuccess: () => navigate({ to: '/login' }),
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'USERNAME_ALREADY_USED') setError('username', { message: t('errors.username.exist')! });
      else setError('root', { message: t('errors.unknownRegister')! });
    },
  });
  return (
    <>
      <DocumentTitle>{t('document.title.registration')}</DocumentTitle>
      <Header className="h-[7vh] my-[1vh]" />
      <Title as="h2" className="mb-10 mt-10 text-center">
        {t('title.registration')}
      </Title>
      <form
        className="flex flex-col gap-8 items-stretch mx-auto px-2 w-72 sm:w-96"
        onSubmit={handleSubmit((data) => registration.mutate(data))}
      >
        <TextInput
          aria-label="Pseudonyme"
          errorMessage={formState.errors.username?.message}
          placeholder={t('users.username')!}
          type="text"
          {...r(register, 'username')}
        />
        <TextInput
          aria-label="Mot de passe"
          errorMessage={formState.errors.password?.message}
          placeholder={t('users.password')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'password')}
        />
        <TextInput
          aria-label="Confirmation du mot de passe"
          errorMessage={formState.errors.passwordConfirmation?.message}
          placeholder={t('users.confirmPassword')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'passwordConfirmation')}
        />
        <TextInput
          aria-label="Biographie"
          errorMessage={formState.errors.biography?.message}
          placeholder={t('users.biography')!}
          {...r(register, 'biography')}
        />
        <Button type="submit">{t('users.register')}</Button>
        {formState.errors.root && (
          <Text centered color="red">
            {formState.errors.root.message}
          </Text>
        )}
      </form>
      <Footer />
    </>
  );
}

export default RegistrationPage;
