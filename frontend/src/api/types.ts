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
