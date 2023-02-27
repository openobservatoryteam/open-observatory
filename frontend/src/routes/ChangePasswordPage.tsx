import { Text, TextInput, Button } from '@/components';
import { Footer, Header } from '@/layout';
import { faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { zodResolver } from '@hookform/resolvers/zod';
import { Title as DocumentTitle } from 'react-head';
import { useForm } from 'react-hook-form';
import z from 'zod';

const changePasswordSchema = z
  .object({
    oldPassword: z.string(),
    newPassword: z
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
    confirmPassword: z.string(),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: 'Les mots de passe ne correspondent pas.',
    path: ['confirmPassword'],
  });

function ChangePasswordPage(): JSX.Element {
  const form = useForm({
    defaultValues: {
      oldPassword: '',
      newPassword: '',
      confirmPassword: '',
    },
    resolver: zodResolver(changePasswordSchema),
  });

  return (
    <>
      <DocumentTitle>Changement du mot de passe</DocumentTitle>
      <Header />
      <Text as="h1" centered className="mt-10">
        Changement du mot de passe
      </Text>
      <form className="flex flex-col mt-5 mx-auto w-72 md:w-[50%]" onSubmit={form.handleSubmit(() => 0)}>
        <TextInput
          errorMessage={form.formState.errors.oldPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Ancien mot de passe"
          {...form.register('oldPassword')}
        />
        <TextInput
          errorMessage={form.formState.errors.newPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Nouveau mot de passe"
          {...form.register('newPassword')}
        />
        <TextInput
          errorMessage={form.formState.errors.confirmPassword?.message}
          type="password"
          className="mt-7"
          placeholder="Confirmation mot de passe"
          {...form.register('confirmPassword')}
        />
        <Button className="mt-10 flex justify-between" type="submit">
          Enregistrer
          <FontAwesomeIcon icon={faSave} />
        </Button>
      </form>
      <Footer />
    </>
  );
}

export default ChangePasswordPage;
