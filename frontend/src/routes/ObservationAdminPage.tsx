import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';

import { findAllObservation } from '~/api';
import { AsideAdmin, List, ObservationCard, Title } from '~/components';

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
          {t('admin.observations')}
        </Title>
        {observations.data && (
          <List
            className="mx-4 md:mx-16"
            data={observations.data.data}
            currentPage={observations.data.page}
            pageCount={observations.data.pageCount}
            onPageChange={() => void 0}
            render={(e) => <ObservationCard observation={e} key={e.id} />}
          />
        )}
      </div>
    </div>
  );
}

export default ObservationAdminPage;
