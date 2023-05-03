import { useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';

import { Observation, SearchResults, findAllObservation } from '~/api';
import { AsideAdmin, List, ObservationCard, Title } from '~/components';

function ObservationAdminPage() {
  const { t } = useTranslation();
  const [page, setPage] = useState<number>(0);
  const [observations, setObservation] = useState<SearchResults<Observation>>();
  const itemsPerPage = 6;

  useQuery({
    queryFn: () => findAllObservation({ page, itemsPerPage }),
    queryKey: ['page', page, 'itemsPerPage', itemsPerPage],
    onSuccess: (res) => setObservation(res),
  });

  const handlePageChange = (p: number) => {
    setPage(p);
  };

  return (
    <div className="flex">
      <AsideAdmin selected={0} />
      <div className="flex-1">
        <Title as="h2" centered className="mt-4 mb-4">
          {t('admin.observations')}
        </Title>
        {observations && (
          <List
            className="mx-4 md:mx-16"
            data={observations.data}
            currentPage={observations.page}
            pageCount={observations.pageCount}
            onPageChange={(p) => handlePageChange(p)}
            render={(e) => <ObservationCard observation={e} key={e.id} />}
          />
        )}
      </div>
    </div>
  );
}

export default ObservationAdminPage;
