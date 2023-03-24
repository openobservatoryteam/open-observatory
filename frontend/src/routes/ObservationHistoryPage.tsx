import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';
import { useTranslation } from 'react-i18next';

import { findAllUserObservations } from '~/api';
import { Button, Text, Title } from '~/components';
import ObservationItem from '~/components/molecules/ObservationItem';
import { Footer, Header } from '~/layout';

function ObservationHistoryPage() {
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();
  const observationsQuery = useQuery({
    queryFn: () => findAllUserObservations(username),
    queryKey: ['user', username, 'observations'],
  });
  const observations = observationsQuery.data;
  const { t } = useTranslation();
  return (
    <>
      <DocumentTitle>Historique des observations</DocumentTitle>
      <Header className="h-[7vh] my-[0.5vh]" />
      <div className="absolute left-4 top-30">
        <Button className="py-1 px-3 mr-5" color="white" onPress={() => history.go(-1)} rounded>
          <FontAwesomeIcon icon={faArrowLeft} size="xl" />
        </Button>
      </div>
      <Title centered className="mt-12" color="white">
        {t('title.history')}
      </Title>
      <div className="flex flex-col items-center mt-12 h-[29em] overflow-y-scroll">
        {observations == null ||
          (observations != null && observations?.length === 0 && (
            <Text as="h1" centered color="white" className="mt-10">
              {t('observation.empty')}
            </Text>
          ))}
        {observations != null &&
          observations?.length > 0 &&
          observations.map((obs) => (
            <ObservationItem as={Link} key={obs.id} observation={obs} className="my-3" to={`/observations/${obs.id}`} />
          ))}
      </div>
      <Footer />
    </>
  );
}

export default ObservationHistoryPage;
