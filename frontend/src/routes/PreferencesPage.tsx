import { Link } from '@tanstack/react-location';

import { Button, Text } from '~/components';
import { Footer, Header } from '~/layout';

function PreferencesPage() {
  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center mt-10">
        <Button as={Link} className="h-20 w-3/4 md:W-1/2 rounded-full" to="/change-password">
          Changer le mot de passe
        </Button>
        <Button as={Link} className="h-20 w-3/4 md:W-1/2 mt-16 rounded-full" to="/change-password">
          Paramètre de notification
        </Button>
        <div className="flex mt-10">
          <Text as="span">Privée :</Text>
          <Text as="span" className="ml-5">
            Activée
          </Text>
        </div>
      </div>
      <Footer />
    </>
  );
}

export default PreferencesPage;
