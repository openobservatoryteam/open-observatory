import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';
import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';

import { Button, Text } from '@/components';

type UpDownVoteProps = {
  currentVotes: number;
  onVote: (vote: boolean | null) => unknown;
  vote: boolean | null;
} & ComponentPropsWithoutRef<'div'>;

const UpDownVote = ({ className, currentVotes, onVote, vote, ...props }: UpDownVoteProps) => {
  const currentVoteValue = vote === null ? 0 : vote === true ? 1 : -1;
  return (
    <div className={clsx('flex flex-col justify-around items-center', className)} {...props}>
      <Button
        aria-label={vote === true ? 'Annuler mon vote' : 'Voter +1'}
        onPress={() => onVote(vote === true ? null : true)}
        title={vote === true ? 'Annuler mon vote' : 'Voter +1'}
        unstyled
      >
        <FontAwesomeIcon color={vote === true ? 'white' : 'gray'} icon={faArrowUp} size="2xl" />
      </Button>
      <Text as="span" centered className="mt-1.5 mb-1 w-10" color="white">
        {currentVotes + currentVoteValue}
      </Text>
      <Button
        aria-label={vote === false ? 'Annuler mon vote' : 'Voter -1'}
        onPress={() => onVote(vote === false ? null : false)}
        title={vote === false ? 'Annuler mon vote' : 'Voter -1'}
        unstyled
      >
        <FontAwesomeIcon color={vote === false ? 'white' : 'gray'} icon={faArrowDown} size="2xl" />
      </Button>
    </div>
  );
};

export { UpDownVote };
