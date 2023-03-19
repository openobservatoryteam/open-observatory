import { faArrowLeft } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';

import { users } from '~/api';
import { Button, Text } from '~/components';
import ObservationItem from '~/components/molecules/ObservationItem';
import { Footer, Header } from '~/layout';

function ObservationHistoryPage(): JSX.Element {
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();

  const observationsQuery = useQuery({
    queryFn: () => users.observations(username),
    queryKey: ['user', 'observations', username],
  });

  const observations = observationsQuery.data;

  return (
    <>
      <DocumentTitle>Historique des observations</DocumentTitle>
      <Header />
      <div className="absolute left-4 md:top-28 top-4">
        <Button className="py-1 px-3 mr-5" color="white" onPress={() => history.go(-1)} rounded>
          <FontAwesomeIcon icon={faArrowLeft} size="xl" />
        </Button>
      </div>
      <Text as="h1" centered className="mt-12" color="white">
        Historique des observations
      </Text>
      <div className="flex flex-col items-center mt-12 h-[29em] overflow-y-scroll">
        {observations == null ||
          (observations != null && observations?.length === 0 && (
            <Text as="h1" centered color="white">
              L&apos;utilisateur n&apos;as pas encore effectuer d&apos;observations
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
