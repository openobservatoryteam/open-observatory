import { Button, Text } from '@/components';
import { useMatch } from '@tanstack/react-location';
import celest from '@/assets/png/celeste.png';
import clsx from 'clsx';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';

export default function ObservationPage(): JSX.Element {
  const {
    params: { id },
  } = useMatch<{ Params: { id: string } }>();
  return (
    <>
      <div className={clsx('md:w-1/2 md:h-96 relative')}>
        <img src={celest} alt="image de l'observation" className="h-full w-full" />
        <div className={clsx('absolute bottom-3 right-3  flex-col justify-around items-center')}>
          <div className="w-10 flex justify-center items-center">
            <Button unstyled={true}>
              <FontAwesomeIcon icon={faArrowUp} size="2x" color="white" />
            </Button>
          </div>
          <Text as="p" color="white" centered={true} className={clsx('w-10 py-1')}>
            10
          </Text>
          <div className="w-10 flex justify-center items-center">
            <Button unstyled={true}>
              <FontAwesomeIcon icon={faArrowDown} size="2x" color="grey" />
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
