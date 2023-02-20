export type ProblemDetail = {
  type: string;
  title: string;
  status: number;
  detail?: string;
  message?: string;
};

export type User = {
  username: string;
  avatar?: string;
  type: 'USER' | 'ADMIN';
};
