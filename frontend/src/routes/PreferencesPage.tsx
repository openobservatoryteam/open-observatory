import { Link } from '@tanstack/react-location';

import { Button, Text } from '~/components';
import ToggleButton from '~/components/atoms/ToggleButton';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';

function PreferencesPage() {
  const { user } = useAuthentication();
  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center mt-20">
        <Button as={Link} className="h-16 w-3/4 md:W-1/2 rounded-full" to="/change-password" color="white">
          Changer le mot de passe
        </Button>
        <Button
          as={Link}
          className="h-16 w-3/4 md:W-1/2 mt-16 rounded-full"
          to={`/users/${user?.username}/notification`}
          color="white"
        >
          Paramètre de notification
        </Button>
        <div className="flex mt-10">
          <Text as="span">Privée :</Text>
          <ToggleButton
            value={false}
            handleChange={(value) => console.log(value)}
            onLabel="Activé"
            offLabel="Désactivé"
            className="ml-5"
          />
        </div>
      </div>
      <Footer />
    </>
  );
}

export default PreferencesPage;
