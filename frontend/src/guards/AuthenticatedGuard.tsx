import { Navigate } from '@tanstack/react-location';

import { useAuthentication } from '~/providers';

export function AuthenticatedGuard({ children }: { children: React.ReactNode }): JSX.Element {
  const { user } = useAuthentication();

  if (user) {
    return <>{children}</>;
  }

  return <Navigate to="/login" replace />;
}
