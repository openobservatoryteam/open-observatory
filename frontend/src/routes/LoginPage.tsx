import { Link, useNavigate } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import { useTranslation } from 'react-i18next';

import { LoginData } from '~/api/authentication';
import { Button, Text, TextInput, Title } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

function LoginPage() {
  const auth = useAuthentication();
  const navigate = useNavigate();
  const { t } = useTranslation();
  const { formState, handleSubmit, register, setError } = useForm<LoginData>();
  const onSubmit = (data: LoginData) =>
    auth.login.mutate(data, {
      onSuccess: () => navigate({ to: '/' }),
      onError: (e) => {
        if (e.response.status === 401) setError('root', { message: t('errors.credentials')! });
        else
          setError('root', {
            message: t('errors.unknowConnection')!,
          });
      },
    });
  return (
    <>
      <DocumentTitle>{t('document.title.signin')}</DocumentTitle>
      <Header className="h-[7vh] my-[1vh]" />
      <Title as="h2" className="mb-10 mt-16 text-center">
        {t('title.connection')}
      </Title>
      <form className="flex flex-col mb-4 mx-auto px-2 w-72 sm:w-96" onSubmit={handleSubmit(onSubmit)}>
        <TextInput
          aria-label="Pseudonyme"
          className="mb-10"
          errorMessage={formState.errors.username?.message}
          placeholder={t('users.username')!}
          type="text"
          {...r(register, 'username', { required: { message: t('errors.required'), value: true } })}
        />
        <TextInput
          aria-label="Mot de passe"
          className="mb-10"
          errorMessage={formState.errors.password?.message}
          placeholder={t('users.password')!}
          type="password"
          withVisibilityToggle
          {...r(register, 'password', { required: { message: t('errors.required'), value: true } })}
        />
        <Button className="mb-5" type="submit">
          {t('users.login')}
        </Button>
        {formState.errors.root && (
          <Text centered color="red">
            {formState.errors.root.message}
          </Text>
        )}
      </form>
      <Text centered>
        <Link to="/register">{t('users.notAccount')}.</Link>
      </Text>
      <Footer />
    </>
  );
}

export default LoginPage;
