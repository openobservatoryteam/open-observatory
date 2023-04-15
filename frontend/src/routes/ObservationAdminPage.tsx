import { Link } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';

import { findAllObservation } from '~/api';
import { AsideAdmin, Button, List, Title } from '~/components';

function ObservationAdminPage() {
  const { t } = useTranslation();

  const observations = useQuery({
    queryFn: findAllObservation,
    queryKey: ['observations'],
    onSuccess: (data) => console.log(data),
    onError: (e) => console.log(e),
  });

  return (
    <div className="flex">
      <AsideAdmin selected={0} />
      <div className="flex-1">
        <Title as="h2" centered className="mt-4 mb-4">
          {t('common.celestialBody')}
        </Title>
        {observations.data && (
          <List
            className="mx-4 md:mx-16"
            data={observations.data}
            currentPage={1}
            pageCount={10}
            onPageChange={() => void 0}
            render={(e) => (
              <Button
                as={Link}
                className="active:brightness-90 bg-white flex flex-col gap-4 items-center rounded-2xl"
                color="white"
                key={e.id}
                unstyled
                to={`observation/${e.id}/edit`}
              >
                <img
                  className="h-64 object-cover rounded-t-2xl w-full"
                  src={e.celestialBody.image}
                  alt={t('celestialBody.illustration', { name: e.celestialBody.name })!}
                />
              </Button>
            )}
          />
        )}
      </div>
    </div>
  );
}

export default ObservationAdminPage;
