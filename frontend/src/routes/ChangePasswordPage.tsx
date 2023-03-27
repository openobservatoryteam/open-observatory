import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import z from 'zod';

import { ApplicationError, ChangeUserPasswordData, changeUserPassword } from '~/api';
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
    newPasswordConfirmation: z.string(),
  })
  .refine((data) => data.newPassword === data.newPasswordConfirmation, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['newPasswordConfirmation'],
  });

function ChangePasswordPage() {
  const { user } = useAuthentication();
  const { formState, handleSubmit, register, setError } = useForm<ChangeUserPasswordData>({
    resolver: zodResolver(changePasswordSchema),
  });
  const navigate = useNavigate();
  const changePassword = useMutation({
    mutationFn: changeUserPassword,
    onSuccess: () => navigate({ to: `/users/${user!.username}` }),
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'PASSWORD_MISMATCH') {
        setError('oldPassword', { message: 'Le mot de passe est incorrect' });
      }
    },
  });
  return (
    <>
      <DocumentTitle>Changement du mot de passe – Open Observatory</DocumentTitle>
      <Header className="h-16 my-1" />
      <Text as="h1" centered className="mt-10">
        Changement du mot de passe
      </Text>
      <form
        className="flex flex-col mt-5 mx-auto w-72 md:w-[50%]"
        onSubmit={handleSubmit((d) => changePassword.mutate({ username: user!.username, ...d }))}
      >
        <TextInput
          aria-label="Ancien mot de passe"
          className="mt-7"
          errorMessage={formState.errors.oldPassword?.message}
          placeholder="Ancien mot de passe"
          type="password"
          withVisibilityToggle
          {...r(register, 'oldPassword')}
        />
        <TextInput
          aria-label="Nouveau mot de passe"
          className="mt-7"
          errorMessage={formState.errors.newPassword?.message}
          placeholder="Nouveau mot de passe"
          type="password"
          withVisibilityToggle
          {...r(register, 'newPassword')}
        />
        <TextInput
          aria-label="Confirmation du nouveau mot de passe"
          className="mt-7"
          errorMessage={formState.errors.newPasswordConfirmation?.message}
          placeholder="Confirmation du nouveau mot de passe"
          type="password"
          withVisibilityToggle
          {...r(register, 'newPasswordConfirmation')}
        />
        <Button className="flex justify-between mt-10" type="submit">
          Enregistrer
          <FontAwesomeIcon icon={faSave} />
        </Button>
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

export default ChangePasswordPage;
