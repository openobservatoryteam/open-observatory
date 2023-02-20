import { authentication, User, users } from '@/api';
import { useMutation, UseMutationResult, useQuery, useQueryClient } from '@tanstack/react-query';
import { createContext, ReactNode, useContext } from 'react';

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
  const value = {
    isLoading: currentUser.isInitialLoading,
    isLoggedIn: !!currentUser.data,
    login,
    logout,
    user: currentUser.data ?? null,
  };
  return <AuthenticationContext.Provider value={value}>{children}</AuthenticationContext.Provider>;
}

export const useAuthentication = () => useContext(AuthenticationContext);
export { AuthenticationProvider };
