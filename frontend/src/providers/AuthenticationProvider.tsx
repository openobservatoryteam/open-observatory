import { UseMutationResult, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ReactNode, createContext, useContext, useMemo } from 'react';

import { User, authentication, users } from '~/api';

type AuthenticationContextProps = {
  isLoading: boolean;
  isLoggedIn: boolean;
  login: UseMutationResult<undefined, unknown, authentication.LoginData, unknown>;
  logout: UseMutationResult<undefined, unknown, void, unknown>;
  user: User | null;
};

const AuthenticationContext = createContext<AuthenticationContextProps>(null!);

type UserProviderProps = { children?: ReactNode };
function AuthenticationProvider({ children }: UserProviderProps) {
  const queryClient = useQueryClient();
  const currentUser = useQuery({
    queryFn: users.getCurrent,
    queryKey: ['users', 'current'],
  });
  const login = useMutation({
    mutationFn: authentication.login,
    mutationKey: ['login'],
    onSuccess: () => queryClient.invalidateQueries(['users', 'current']),
  });
  const logout = useMutation({
    mutationFn: authentication.logout,
    mutationKey: ['logout'],
    onSuccess: () => queryClient.setQueryData(['users', 'current'], null),
  });
  const value = useMemo(
    () => ({
      isLoading: currentUser.isInitialLoading,
      isLoggedIn: !!currentUser.data,
      login,
      logout,
      user: currentUser.data ?? null,
    }),
    [currentUser],
  );
  return <AuthenticationContext.Provider value={value}>{children}</AuthenticationContext.Provider>;
}

export const useAuthentication = () => useContext(AuthenticationContext);
export { AuthenticationProvider };
