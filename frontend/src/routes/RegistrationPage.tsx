import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';

import { Button, Slider, TextInput, Title } from '@/components';
import { Footer, Header } from '@/layout';

function RegistrationPage() {
  const { handleSubmit, register } = useForm({
    defaultValues: {
      username: '',
      password: '',
      passwordConfirmation: '',
      biography: '',
      visibility: 10,
    },
  });
  const { onChange: changeVisibility, ...visibilityProps } = register('visibility');
  return (
    <>
      <DocumentTitle>Inscription – Open Observatory</DocumentTitle>
      <Header />
      <Title as="h2" className="mb-10 mt-10 text-center">
        Inscription
      </Title>
      <form className="mx-auto px-2 sm:w-96 w-72" onSubmit={handleSubmit((v) => console.table(v))}>
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
        <TextInput
          aria-label="Confirmation du mot de passe"
          className="mb-10"
          placeholder="Confirmation du mot de passe"
          required
          type="password"
          {...register('passwordConfirmation')}
        />
        <Slider
          className="mb-10"
          label="Péremption"
          minValue={5}
          maxValue={30}
          onChange={(value) => changeVisibility({ target: { name: visibilityProps.name, value } })}
          step={5}
          withMarks
          {...visibilityProps}
        />
        <TextInput
          aria-label="Biographie"
          className="mb-10"
          placeholder="Biographie"
          required
          {...register('biography')}
        />
        <div className="flex justify-center">
          <Button type="submit">S&apos;inscrire</Button>
        </div>
      </form>
      <Footer />
    </>
  );
}

export default RegistrationPage;
