import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useOverlayTriggerState } from 'react-stately';

import { CelestialBody, findAllCelestialBodies } from '~/api';
import { Logo } from '~/assets';
import { Button, CreateCelestialBodyModal, EditCelestialBody, List, Text, Title } from '~/components';

function CelestialBodyAdminPage() {
  const [editedBody, setEditedBody] = useState<CelestialBody | null>(null);
  const bodies = useQuery({
    queryFn: findAllCelestialBodies,
    queryKey: ['celestial-bodies'],
  });

  const createBodyState = useOverlayTriggerState({});
  const editBodyState = useOverlayTriggerState({
    onOpenChange: (isOpen) => (isOpen ? undefined : setEditedBody(null)),
  });

  const openUpdateModal = (body: CelestialBody) => {
    setEditedBody(body);
    editBodyState.open();
  };

  return (
    <>
      {createBodyState.isOpen && <CreateCelestialBodyModal state={createBodyState} />}
      {editedBody && editBodyState.isOpen && <EditCelestialBody celestialBody={editedBody} state={editBodyState} />}
      <div className="flex">
        <aside className="bg-[#333C47] min-h-screen pt-4 px-3 md:px-12">
          <Link title="Accueil Open Observatory" to="/">
            <Logo />
          </Link>
          <Text centered className="mb-20 mt-4">
            ADMINISTRATEUR
          </Text>
          <ul>
            <li>
              <Button
                as={Link}
                className="bg-transparent mb-12 py-4 text-white text-xl hover:bg-white hover:opacity-80 hover:text-black"
                to="/admin/observations"
              >
                Observations
              </Button>
            </li>
            <li>
              <Button as={Link} className="mb-12 py-4 text-xl" color="white" to="/admin/celestial-bodies">
                Objets célestes
              </Button>
            </li>
            <li>
              <Button
                as={Link}
                className="bg-transparent mb-12 py-4 text-white text-xl hover:bg-white hover:opacity-80 hover:text-black"
                to="/admin/users"
              >
                Utilisateurs
              </Button>
            </li>
          </ul>
        </aside>
        <div className="flex-1">
          <Title as="h2" centered className="mt-4">
            Objets célestes
          </Title>
          <div className="flex justify-end mb-8 mt-2 mx-4 md:mx-16">
            <Button className="p-4" color="darkGray" onPress={createBodyState.open} rounded>
              <FontAwesomeIcon icon={faPlus} size="lg" />
            </Button>
          </div>
          {bodies.data && (
            <List
              className="mx-4 md:mx-16"
              data={bodies.data.data}
              currentPage={bodies.data.page}
              onPageChange={() => void 0}
              pageCount={bodies.data.pageCount}
              render={(e) => (
                <Button
                  className="active:brightness-90 bg-white flex flex-col gap-4 items-center rounded-2xl"
                  color="white"
                  key={e.id}
                  unstyled
                  onPress={() => openUpdateModal(e)}
                >
                  <img
                    className="h-64 object-cover rounded-t-2xl w-full"
                    src={e.image}
                    alt={`Illustration de ${e.name}`}
                  />
                  <Text color="black">{e.name}</Text>
                  <Text className="mb-3" color="black">
                    Validité : {e.validityTime}h
                  </Text>
                </Button>
              )}
            />
          )}
        </div>
      </div>
    </>
  );
}

export default CelestialBodyAdminPage;
