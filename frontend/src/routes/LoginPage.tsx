import { Link } from '@tanstack/react-location';
import { Helmet } from 'react-helmet';
import { useForm } from 'react-hook-form';

import { Button, TextInput, Title } from '@/components';
import { Footer, Header } from '@/layout';

function LoginPage() {
  const { handleSubmit, register } = useForm({
    defaultValues: {
      username: '',
      password: '',
    },
  });
  return (
    <>
      <Helmet>
        <title>Connexion – Open Observatory</title>
      </Helmet>
      <Header />
      <Title as="h2" className="mb-10 mt-16 text-center">
        Connexion
      </Title>
      <form className="mx-auto px-2 sm:w-96 w-72" onSubmit={handleSubmit(() => void 0)}>
        <TextInput
          aria-label="Pseudonyme"
          className="mb-10"
          placeholder="Pseudonyme"
          type="text"
          {...register('username')}
        />
        <TextInput
          aria-label="Mot de passe"
          className="mb-10"
          placeholder="Mot de passe"
          required
          type="password"
          {...register('password')}
        />
        <div className="flex justify-center">
          <Button className="mb-4" onPress={() => alert('Coucou')} type="submit">
            Se connecter
          </Button>
        </div>
      </form>
      <p className="text-center">
        <Link to="/register">Pas de compte ? Inscrivez-vous</Link>
      </p>
      <Footer />
    </>
  );
}

export default LoginPage;
