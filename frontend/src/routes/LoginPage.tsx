import { Link, useNavigate } from '@tanstack/react-location';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';

import { LoginData } from '@/api/authentication';
import { Button, Text, TextInput, Title } from '@/components';
import { Footer, Header } from '@/layout';
import { useAuthentication } from '@/providers';

function LoginPage() {
  const auth = useAuthentication();
  const navigate = useNavigate();
  const form = useForm({
    defaultValues: {
      username: '',
      password: '',
    },
  });
  const onSubmit = (data: LoginData) =>
    auth.login.mutate(data, {
      onSuccess: () => navigate({ to: '/' }),
      onError: () => form.setError('password', { message: 'Les identifiants sont incorrects.' }),
    });
  return (
    <>
      <DocumentTitle>Connexion – Open Observatory</DocumentTitle>
      <Header />
      <Title as="h2" className="mb-10 mt-16 text-center">
        Connexion
      </Title>
      <form
        className="flex flex-col gap-10 items-stretch mb-4 mx-auto px-2 w-72 sm:w-96"
        onSubmit={form.handleSubmit(onSubmit)}
      >
        <TextInput
          aria-label="Pseudonyme"
          errorMessage={form.formState.errors.username?.message}
          placeholder="Pseudonyme"
          required
          type="text"
          {...form.register('username')}
        />
        <TextInput
          aria-label="Mot de passe"
          errorMessage={form.formState.errors.password?.message}
          placeholder="Mot de passe"
          required
          type="password"
          {...form.register('password')}
        />
        <Button type="submit">Se connecter</Button>
      </form>
      <Text centered>
        <Link to="/register">Pas de compte ? Inscrivez-vous.</Link>
      </Text>
      <Footer />
    </>
  );
}

export default LoginPage;
