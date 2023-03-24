import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import z from 'zod';

import { ApplicationError, CreateUserData, postCreateUser } from '~/api';
import { Button, Text, TextInput, Title } from '~/components';
import { Footer, Header } from '~/layout';
import { registerAdapter as r } from '~/utils';

const RegistrationSchema = z
  .object({
    username: z
      .string()
      .regex(/^[A-Za-z]/, 'Le pseudonyme doit débuter par une lettre.')
      .regex(/^\w*$/, 'Le pseudonyme ne peut contenir que des lettres, chiffres et tirets du bas.')
      .max(32, 'Le pseudonyme ne doit pas dépasser 32 caractères.'),
    password: z
      .string()
      .regex(/.*[A-Z].*/, 'Le mot de passe doit contenir au moins une majuscule.')
      .regex(/.*[a-z].*/, 'Le mot de passe doit contenir au moins une minuscule.')
      .regex(/.*\d.*/, 'Le mot de passe doit contenir au moins un chiffre.')
      .regex(/.*[`~<>?,./!@#$%^&*()\-_+="'|{}[\];:].*/, 'Le mot de passe doit contenir au moins un caractère spécial.')
      .min(8, "Le mot de passe doit être composé d'au moins 8 caractères.")
      .max(32, 'Le mot de passe ne doit pas dépasser 32 caractères.'),
    passwordConfirmation: z.string(),
    biography: z.string().max(2048, 'La biographie ne doit pas dépasser 2048 caractères.').optional(),
  })
  .refine((data) => data.password === data.passwordConfirmation, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['passwordConfirmation'],
  });

function RegistrationPage() {
  const navigate = useNavigate();
  const { formState, handleSubmit, register, setError } = useForm<CreateUserData>({
    resolver: zodResolver(RegistrationSchema),
  });
  const registration = useMutation({
    mutationFn: postCreateUser,
    onSuccess: () => navigate({ to: '/login' }),
    onError: ({ cause }: { cause?: ApplicationError }) => {
      if (cause?.message === 'USERNAME_ALREADY_USED')
        setError('username', { message: 'Ce pseudonyme est déjà utilisé.' });
      else setError('root', { message: 'Une erreur inconnue est survenue lors de votre inscription.' });
    },
  });
  return (
    <>
      <DocumentTitle>Inscription – Open Observatory</DocumentTitle>
      <Header className="h-[7vh] my-[0.5vh]" />
      <Title as="h2" className="mb-10 mt-10 text-center">
        Inscription
      </Title>
      <form
        className="flex flex-col gap-8 items-stretch mx-auto px-2 w-72 sm:w-96"
        onSubmit={handleSubmit((data) => registration.mutate(data))}
      >
        <TextInput
          aria-label="Pseudonyme"
          errorMessage={formState.errors.username?.message}
          placeholder="Pseudonyme"
          type="text"
          {...r(register, 'username')}
        />
        <TextInput
          aria-label="Mot de passe"
          errorMessage={formState.errors.password?.message}
          placeholder="Mot de passe"
          type="password"
          withVisibilityToggle
          {...r(register, 'password')}
        />
        <TextInput
          aria-label="Confirmation du mot de passe"
          errorMessage={formState.errors.passwordConfirmation?.message}
          placeholder="Confirmation du mot de passe"
          type="password"
          withVisibilityToggle
          {...r(register, 'passwordConfirmation')}
        />
        <TextInput
          aria-label="Biographie"
          errorMessage={formState.errors.biography?.message}
          placeholder="Biographie"
          {...r(register, 'biography')}
        />
        <Button type="submit">S&apos;inscrire</Button>
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
