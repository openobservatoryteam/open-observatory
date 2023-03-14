import { Link, useNavigate } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';

import { LoginData } from '~/api/authentication';
import { Button, Text, TextInput, Title } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';
import { registerAdapter as r } from '~/utils';

function LoginPage() {
  const auth = useAuthentication();
  const navigate = useNavigate();
  const { formState, handleSubmit, register, setError } = useForm({
    defaultValues: {
      username: '',
      password: '',
    },
  });
  const onSubmit = (data: LoginData) =>
    auth.login.mutate(data, {
      onSuccess: () => navigate({ to: '/' }),
      onError: () => setError('root', { message: 'Les identifiants sont incorrects.' }),
    });
  return (
    <>
      <DocumentTitle>Connexion – Open Observatory</DocumentTitle>
      <Header />
      <Title as="h2" className="mb-10 mt-16 text-center">
        Connexion
      </Title>
      <form className="flex flex-col mb-4 mx-auto px-2 w-72 sm:w-96" onSubmit={handleSubmit(onSubmit)}>
        <TextInput
          aria-label="Pseudonyme"
          className="mb-10"
          errorMessage={formState.errors.username?.message}
          placeholder="Pseudonyme"
          required
          type="text"
          {...r(register, 'username')}
        />
        <TextInput
          aria-label="Mot de passe"
          className="mb-10"
          errorMessage={formState.errors.password?.message}
          placeholder="Mot de passe"
          required
          type="password"
          withVisibilityToggle
          {...r(register, 'password')}
        />
        <Button className="mb-5" type="submit">
          Se connecter
        </Button>
        {formState.errors.root && (
          <Text centered color="red">
            {formState.errors.root.message}
          </Text>
        )}
      </form>
      <Text centered>
        <Link to="/register">Pas de compte ? Inscrivez-vous.</Link>
      </Text>
      <Footer />
    </>
  );
}

export default LoginPage;
