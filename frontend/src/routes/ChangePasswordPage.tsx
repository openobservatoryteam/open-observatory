import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import z from 'zod';

import { ApplicationError, ChangeUserPasswordData, changeUserPassword } from '~/api';
import { Button, Text, TextInput, Title } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

function ChangePasswordPage() {
  const { user } = useAuthentication();
  const { t } = useTranslation();
  const changePasswordSchema = z
    .object({
      oldPassword: z.string(),
      newPassword: z
        .string()
        .regex(/.*[A-Z].*/, t('errors.password.upper')!)
        .regex(/.*[a-z].*/, t('errors.password.lower')!)
        .regex(/.*\d.*/, t('errors.password.number')!)
        .regex(/.*[`~<>?,./!@#$%^&*()\-_+="'|{}[\];:].*/, t('errors.password.specialCharacter')!)
        .min(8, t('errors.password.min')!)
        .max(32, t('errors.password.max')!),
      newPasswordConfirmation: z.string(),
    })
    .refine((data) => data.newPassword === data.newPasswordConfirmation, {
      message: t('errors.password.notMatch')!,
      path: ['newPasswordConfirmation'],
    });
  const { formState, handleSubmit, register, setError } = useForm<ChangeUserPasswordData>({
    resolver: zodResolver(changePasswordSchema),
  });
  const navigate = useNavigate();
  const changePassword = useMutation({
    mutationFn: changeUserPassword,
    onSuccess: () => navigate({ to: `/users/${user!.username}` }),
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'PASSWORD_MISMATCH') {
        setError('oldPassword', { message: t('errors.password.incorrect')! });
      }
    },
  });
  return (
    <>
      <DocumentTitle>Changement du mot de passe – Open Observatory</DocumentTitle>
      <Header className="h-[7vh] my-[0.5vh]" />
      <Title as="h1" centered className="mt-10">
        {t('title.changePassword')}
      </Title>
      <form
        className="flex flex-col mt-5 mx-auto w-72 md:w-[50%]"
        onSubmit={handleSubmit((d) => changePassword.mutate({ username: user!.username, ...d }))}
      >
        <TextInput
          aria-label="Ancien mot de passe"
          className="mt-7"
          errorMessage={formState.errors.oldPassword?.message}
          placeholder={t('users.oldPassword')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'oldPassword')}
        />
        <TextInput
          aria-label="Nouveau mot de passe"
          className="mt-7"
          errorMessage={formState.errors.newPassword?.message}
          placeholder={t('users.newPassword')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'newPassword')}
        />
        <TextInput
          aria-label="Confirmation du nouveau mot de passe"
          className="mt-7"
          errorMessage={formState.errors.newPasswordConfirmation?.message}
          placeholder={t('users.confirmNewPassword')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'newPasswordConfirmation')}
        />
        <Button className="flex justify-between mt-10" type="submit">
          {t('common.save')}
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
