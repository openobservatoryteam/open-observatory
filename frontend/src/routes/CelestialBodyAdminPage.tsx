import { faPlus } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useOverlayTriggerState } from 'react-stately';

import { CelestialBody, findAllCelestialBodies } from '~/api';
import { AsideAdmin, Button, CreateCelestialBodyModal, EditCelestialBody, List, Text, Title } from '~/components';

function CelestialBodyAdminPage() {
  const [editedBody, setEditedBody] = useState<CelestialBody | null>(null);
  const { t } = useTranslation();
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
        <AsideAdmin selected={1} />
        <div className="flex-1">
          <Title as="h2" centered className="mt-4">
            {t('common.celestialBody')}
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
                    alt={t('celestialBody.illustration', { name: e.name })!}
                  />
                  <Text color="black">{e.name}</Text>
                  <Text className="mb-3" color="black">
                    {t('common.validity')} : {e.validityTime}h
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
