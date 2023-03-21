import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import z from 'zod';

import { ProblemDetail, users } from '~/api';
import { ChangePasswordBody } from '~/api/users';
import { Button, Text, TextInput } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

const changePasswordSchema = z
  .object({
    oldPassword: z.string(),
    newPassword: z
      .string()
      .regex(/.*[A-Z].*/, 'Le mot de passe doit contenir au moins une majuscule.')
      .regex(/.*[a-z].*/, 'Le mot de passe doit contenir au moins une minuscule.')
      .regex(/.*\d.*/, 'Le mot de passe doit contenir au moins un chiffre.')
      .regex(/.*[`~<>?,./!@#$%^&*()\-_+="'|{}[\];:].*/, 'Le mot de passe doit contenir au moins un caractère spécial.')
      .min(8, "Le mot de passe doit être composé d'au moins 8 caractères.")
      .max(32, 'Le mot de passe ne doit pas dépasser 32 caractères.'),
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['confirmPassword'],
  });

function ChangePasswordPage() {
  const { formState, handleSubmit, register, setError } = useForm({
    defaultValues: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
    resolver: zodResolver(changePasswordSchema),
  });
  const navigate = useNavigate();
  const { user } = useAuthentication();

  const changePassword = useMutation({
    mutationFn: ({ username, json }: { username: string; json: ChangePasswordBody }) =>
      users.changePassword(username, json),
    mutationKey: ['changePassword'],
    onSuccess: () => (user != null ? navigate({ to: `/users/${user.username}` }) : null),
    onError: ({ cause }: { cause?: ProblemDetail }) => {
      if (cause?.message === 'INVALID_PASSWORD') {
        setError('oldPassword', { message: 'Le mot de passe est incorrect' });
      }
    },
  });

  return (
    <>
      <DocumentTitle>Changement du mot de passe</DocumentTitle>
      <Header />
      <Text as="h1" centered className="mt-10">
        Changement du mot de passe
      </Text>
      <form
        className="flex flex-col mt-5 mx-auto w-72 md:w-[50%]"
        onSubmit={handleSubmit((data) =>
          changePassword.mutate({
            username: user != null ? user.username : '',
            json: { newPassword: data.newPassword, oldPassword: data.oldPassword },
          }),
        )}
      >
        <TextInput
          errorMessage={formState.errors.oldPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Ancien mot de passe"
          withVisibilityToggle
          {...r(register, 'oldPassword')}
        />
        <TextInput
          errorMessage={formState.errors.newPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Nouveau mot de passe"
          withVisibilityToggle
          {...r(register, 'newPassword')}
        />
        <TextInput
          errorMessage={formState.errors.confirmPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Confirmation mot de passe"
          withVisibilityToggle
          {...r(register, 'confirmPassword')}
        />
        <Button className="flex justify-between mt-10" type="submit">
          Enregistrer
          <FontAwesomeIcon icon={faSave} />
        </Button>
      </form>
      <Footer />
    </>
  );
}

export default ChangePasswordPage;
