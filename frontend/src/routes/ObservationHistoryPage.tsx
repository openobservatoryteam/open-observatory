import { Link, useMatch } from '@tanstack/react-location';
import { useQuery } from '@tanstack/react-query';

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
      <Header />
      <Text as="h1" centered className="mt-10" color="white">
        Historique des observations
      </Text>
      <div className="flex flex-col justify-around items-center mt-10">
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
