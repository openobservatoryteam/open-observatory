import { Navigate } from '@tanstack/react-location';

import { useAuthentication } from '~/providers';

export function AuthenticatedGuard({ children }: { children: React.ReactNode }) {
  const { isLoading, user } = useAuthentication();
  if (isLoading) return null;
  return user ? <>{children}</> : <Navigate to="/login" replace />;
}
