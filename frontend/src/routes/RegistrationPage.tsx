import { useForm } from 'react-hook-form';

import Button from '@/components/Button';
import TextInput from '@/components/TextInput';
import Title from '@/components/Title';

function RegistrationPage() {
  const { handleSubmit, register } = useForm({
    defaultValues: {
      username: '',
      password: '',
      passwordConfirmation: '',
      biography: '',
    },
  });
  return (
    <>
      <Title as="h2" className="mb-10 mt-10 text-center">
        Inscription
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
        <TextInput
          aria-label="Confirmation du mot de passe"
          className="mb-10"
          placeholder="Confirmation du mot de passe"
          required
          type="password"
          {...register('passwordConfirmation')}
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
    </>
  );
}

export default RegistrationPage;
