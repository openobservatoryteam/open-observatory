import { UseMutationResult, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { HTTPError } from 'ky';
import { ReactNode, createContext, useContext, useMemo } from 'react';

import { LoginData, UserWithProfile, getSelfUser, postLogin, postLogout } from '~/api';

type AuthenticationContextProps = {
  isLoading: boolean;
  login: UseMutationResult<undefined, HTTPError, LoginData, unknown>;
  logout: UseMutationResult<null, HTTPError, void, unknown>;
} & ({ isLoggedIn: true; user: UserWithProfile } | { isLoggedIn: false; user: null });

const AuthenticationContext = createContext<AuthenticationContextProps>(null!);

type UserProviderProps = { children?: ReactNode };
function AuthenticationProvider({ children }: UserProviderProps) {
  const queryClient = useQueryClient();
  const logout = useMutation<null, HTTPError, void, unknown>({
    mutationFn: postLogout,
    onSuccess: () => queryClient.setQueryData(['users', '@me'], null),
  });
  const login = useMutation<undefined, HTTPError, LoginData, unknown>({
    mutationFn: postLogin,
    onSuccess: () => queryClient.invalidateQueries(['users', '@me']),
  });
  const currentUser = useQuery({
    queryFn: getSelfUser,
    queryKey: ['users', '@me'],
    onError: (e: HTTPError) => {
      if (e.response.status === 503) logout.mutate();
    },
  });
  const value = useMemo<AuthenticationContextProps>(
    () => ({
      isLoading: currentUser.isInitialLoading,
      login,
      logout,
      ...(currentUser.data ? { isLoggedIn: true, user: currentUser.data } : { isLoggedIn: false, user: null }),
    }),
    [currentUser],
  );
  return <AuthenticationContext.Provider value={value}>{children}</AuthenticationContext.Provider>;
}

export const useAuthentication = () => useContext(AuthenticationContext);
export { AuthenticationProvider };
