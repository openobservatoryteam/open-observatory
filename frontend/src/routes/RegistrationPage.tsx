import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import z from 'zod';

import { ProblemDetail, users } from '@/api';
import { Button, TextInput, Title } from '@/components';
import { Footer, Header } from '@/layout';

const RegistrationSchema = z
  .object({
    username: z
      .string()
      .regex(/^[A-Za-z]/, 'Le pseudonyme doit débuter par une lettre.')
      .regex(/^\w*$/, 'Le pseudonyme ne peut contenir que des lettres, chiffres et tirets du bas.')
      .max(32, 'Le pseudonyme ne doit pas dépasser 32 caractères.'),
    password: z
      .string()
      .regex(new RegExp('.*[A-Z].*'), 'Le mot de passe doit contenir au moins une majuscule.')
      .regex(new RegExp('.*[a-z].*'), 'Le mot de passe doit contenir au moins une minuscule.')
      .regex(new RegExp('.*\\d.*'), 'Le mot de passe doit contenir au moins un chiffre.')
      .regex(
        new RegExp('.*[`~<>?,./!@#$%^&*()\\-_+="\'|{}\\[\\];:\\\\].*'),
        'Le mot de passe doit contenir au moins un caractère spécial.',
      )
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
  const form = useForm({
    defaultValues: {
      username: '',
      password: '',
      passwordConfirmation: '',
      biography: '',
    },
    resolver: zodResolver(RegistrationSchema),
  });
  const registration = useMutation({
    mutationFn: users.register,
    mutationKey: ['registration'],
    onSuccess: () => navigate({ to: '/login' }),
    onError: ({ cause }: { cause?: ProblemDetail }) => {
      if (cause?.message === 'USERNAME_ALREADY_USED') {
        form.setError('username', { message: 'Ce pseudonyme est déjà utilisé.' });
      }
    },
  });
  return (
    <>
      <DocumentTitle>Inscription – Open Observatory</DocumentTitle>
      <Header />
      <Title as="h2" className="mb-10 mt-10 text-center">
        Inscription
      </Title>
      <form
        className="flex flex-col gap-8 items-stretch mx-auto px-2 w-72 sm:w-96"
        onSubmit={form.handleSubmit((data) => registration.mutate(data))}
      >
        <TextInput
          aria-label="Pseudonyme"
          errorMessage={form.getFieldState('username').error?.message}
          placeholder="Pseudonyme"
          type="text"
          {...form.register('username')}
        />
        <TextInput
          aria-label="Mot de passe"
          errorMessage={form.getFieldState('password').error?.message}
          placeholder="Mot de passe"
          type="password"
          {...form.register('password')}
        />
        <TextInput
          aria-label="Confirmation du mot de passe"
          errorMessage={form.getFieldState('passwordConfirmation').error?.message}
          placeholder="Confirmation du mot de passe"
          type="password"
          {...form.register('passwordConfirmation')}
        />
        <TextInput
          aria-label="Biographie"
          errorMessage={form.getFieldState('biography').error?.message}
          placeholder="Biographie"
          {...form.register('biography')}
        />
        <Button type="submit">S&apos;inscrire</Button>
      </form>
      <Footer />
    </>
  );
}

export default RegistrationPage;
