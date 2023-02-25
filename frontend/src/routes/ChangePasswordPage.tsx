import { Text, TextInput, Button } from '@/components';
import { Footer, Header } from '@/layout';
import { faRegistered, faSave } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

function ChangePasswordPage(): JSX.Element {
  return (
    <>
      <Header />
      <Text as="h1" centered className="mt-7">
        Changement du mot de passe
      </Text>
      <form className="flex flex-col mt-5 mx-auto w-[75%]">
        <TextInput type="password" name="old-password" className="mt-5" placeholder="Ancien mot de passe" />
        <TextInput type="password" name="new-password" className="mt-5" placeholder="Nouveau mot de passe" />
        <TextInput type="password" name="confirm-password" className="mt-5" placeholder="Confirmation mot de passe" />
        <Button className="mt-7 flex justify-between">
          Enregistrer
          <FontAwesomeIcon icon={faSave} />
        </Button>
      </form>
      <Footer />
    </>
  );
}

export default ChangePasswordPage;
