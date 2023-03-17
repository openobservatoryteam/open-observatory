export type CelestialBody = {
  id: number;
  name: string;
  image?: string;
  validityTime: number;
};

export type ProblemDetail = {
  type: string;
  title: string;
  status: number;
  detail?: string;
  message?: string;
};

export type Observation = {
  id: number;
  description: string | null;
  latitude: number;
  longitude: number;
  orientation: number;
  celestialBody: CelestialBody;
  author: User;
  time: string;
};

export type ObservationDetail = {
  id: number;
  description: string | null;
  latitude: number;
  longitude: number;
  orientation: number;
  celestialBody: CelestialBody;
  author: User;
  createdAt: string;
  hasExpired: boolean;
  karma: number;
  currentVote: 'UPVOTE' | 'DOWNVOTE' | null;
  visibility: 'CLEARLY_VISIBLE' | 'VISIBLE' | 'SLIGHTLY_VISIBLE' | 'BARELY_VISIBLE';
};

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
  type: 'USER' | 'ADMIN';
};
