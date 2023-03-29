import { Link } from '@tanstack/react-location';
import { useMutation } from '@tanstack/react-query';

import { editProfil } from '~/api';
import { Button, Text, ToggleButton } from '~/components';
import { Footer, Header } from '~/layout';
import { useAuthentication } from '~/providers';

function PreferencesPage() {
  const { user } = useAuthentication();

  const publicMutation = useMutation({
    mutationFn: editProfil,
  });

  const handleChange = (isPublic: boolean) => {
    publicMutation.mutate({ username: user!.username, isPublic: isPublic });
  };

  return (
    <>
      <Header className="h-16 my-1" />
      <div className="flex flex-col items-center mt-20">
        <Button as={Link} className="h-16 w-3/4 md:W-1/2 rounded-full" to="/change-password" color="white">
          Changer le mot de passe
        </Button>
        <Button as={Link} className="h-16 w-3/4 md:W-1/2 mt-16 rounded-full" to="/edit-notification" color="white">
          Paramètre de notifications
        </Button>
        <div className="flex mt-10">
          <Text as="span">Profil public :</Text>
          <ToggleButton
            value={user!.public}
            handleChange={handleChange}
            onLabel="Oui"
            offLabel="Non"
            className="ml-5"
          />
        </div>
      </div>
      <Footer />
    </>
  );
}

export default PreferencesPage;
