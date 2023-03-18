import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';
import { Title as DocumentTitle } from 'react-head';

import { users } from '~/api';
import { Text } from '~/components';
import ObservationItem from '~/components/molecules/ObservationItem';
import { Footer, Header } from '~/layout';

function ObservationHistoryPage(): JSX.Element {
  const {
    params: { username },
  } = useMatch<{ Params: { username: string } }>();

  const observationsQuery = useQuery({
    queryFn: () => users.observations(username),
    queryKey: ['user', 'observations'],
  });

  const observations = observationsQuery.data;

  return (
    <>
      <DocumentTitle>Historique des observations</DocumentTitle>
      <Header />
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
