import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';
import { ComponentPropsWithoutRef } from 'react';

import { Button, Text } from '~/components';

type UpDownVoteProps = {
  currentVotes: number;
  onVote: (vote: boolean | null) => unknown;
  vote: boolean | null;
} & ComponentPropsWithoutRef<'div'>;

const UpDownVote = ({ currentVotes, onVote, vote, ...props }: UpDownVoteProps) => {
  return (
    <div {...props}>
      <Button
        aria-label={vote === true ? 'Annuler mon vote' : 'Voter +1'}
        fullWidth
        onPress={() => onVote(vote === true ? null : true)}
        title={vote === true ? 'Annuler mon vote' : 'Voter +1'}
        unstyled
      >
        <FontAwesomeIcon color={vote === true ? 'white' : 'gray'} icon={faArrowUp} size="2xl" />
      </Button>
      <Text as="p" centered className="mt-1.5 mb-0.5">
        {currentVotes}
      </Text>
      <Button
        aria-label={vote === false ? 'Annuler mon vote' : 'Voter -1'}
        fullWidth
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
