export type Achievement = {
  id: string;
  name: string;
};

export type ApplicationError = {
  message?: string;
};

export type CelestialBody = {
  id: number;
  name: string;
  image: string;
  validityTime: number;
};

export type Observation = {
  id: number;
  author: User;
  celestialBody: CelestialBody;
  description: string | null;
  latitude: number;
  longitude: number;
  orientation: number;
  visibility: ObservationVisibility;
  createdAt: string;
};

export type ObservationWithDetails = Observation & {
  currentVote: ObservationVote;
  isExpired: boolean;
  karma: number;
};

export type ObservationVisibility = 'CLEARLY_VISIBLE' | 'VISIBLE' | 'SLIGHTLY_VISIBLE' | 'BARELY_VISIBLE';

export type ObservationVote = 'UPVOTE' | 'DOWNVOTE' | null;

export type SearchResults<T> = {
  data: T[];
  count: number;
  totalCount: number;
  page: number;
  pageCount: number;
};

export type User = {
  username: string;
  avatar?: string;
  type: UserType;
};

export type UserType = 'ADMIN' | 'USER';

export type UserWithProfile = User & {
  achievements: Achievement[];
  biography: string | null;
  karma: number;
};
