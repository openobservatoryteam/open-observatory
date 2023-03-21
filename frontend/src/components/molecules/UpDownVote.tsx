import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { ComponentPropsWithoutRef } from 'react';

import { Button, Text } from '~/components';
import { useAuthentication } from '~/providers';

type VoteType = 'UPVOTE' | 'DOWNVOTE' | null;

type UpDownVoteProps = {
  currentVotes: number;
  onVote: (vote: VoteType) => unknown;
  disabled?: boolean;
  vote: VoteType;
} & ComponentPropsWithoutRef<'div'>;

const UpDownVote = ({ currentVotes, onVote, vote, disabled = false, ...props }: UpDownVoteProps) => {
  const { user } = useAuthentication();

  if (user) {
    return (
      <div {...props}>
        <Button
          disabled={disabled}
          aria-label={vote === 'UPVOTE' ? 'Annuler mon vote' : 'Voter +1'}
          fullWidth
          onPress={() => onVote(vote === 'UPVOTE' ? null : 'UPVOTE')}
          title={vote === 'UPVOTE' ? 'Annuler mon vote' : 'Voter +1'}
          unstyled
        >
          <FontAwesomeIcon color={vote === 'UPVOTE' ? 'white' : 'gray'} icon={faArrowUp} size="2xl" />
        </Button>
        <Text as="p" centered className="mt-1.5 mb-0.5">
          {currentVotes}
        </Text>
        <Button
          disabled={disabled}
          aria-label={vote === 'DOWNVOTE' ? 'Annuler mon vote' : 'Voter -1'}
          fullWidth
          onPress={() => onVote(vote === 'DOWNVOTE' ? null : 'DOWNVOTE')}
          title={vote === 'DOWNVOTE' ? 'Annuler mon vote' : 'Voter -1'}
          unstyled
        >
          <FontAwesomeIcon color={vote === 'DOWNVOTE' ? 'white' : 'gray'} icon={faArrowDown} size="2xl" />
        </Button>
      </div>
    );
  } else {
    return (
      <div {...props}>
        <Button disabled fullWidth unstyled>
          <FontAwesomeIcon color={'gray'} icon={faArrowUp} size="2xl" />
        </Button>
        <Text as="p" centered className="mt-1.5 mb-0.5">
          {currentVotes}
        </Text>
        <Button disabled fullWidth unstyled>
          <FontAwesomeIcon color={'gray'} icon={faArrowDown} size="2xl" />
        </Button>
      </div>
    );
  }
};

export { UpDownVote };
