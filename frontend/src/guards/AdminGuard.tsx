import { Navigate } from '@tanstack/react-location';

import { useAuthentication } from '~/providers';

export function AdminGuard({ children }: { children: React.ReactNode }) {
  const { isLoading, user } = useAuthentication();
  if (isLoading) return null;
  return user !== null && user?.type === 'ADMIN' ? <>{children}</> : <Navigate to={null} replace />;
}
