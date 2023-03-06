import { Outlet, ReactLocation, Router } from '@tanstack/react-location';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { HeadProvider } from 'react-head';
// trigger ci
import { AuthenticationProvider } from '~/providers';
import routes from '~/routes';

const location = new ReactLocation();
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: false,
      refetchOnWindowFocus: false,
    },
  },
});

function App() {
  return (
    <HeadProvider>
      <Router location={location} routes={routes}>
        <QueryClientProvider client={queryClient}>
          <AuthenticationProvider>
            <Outlet />
          </AuthenticationProvider>
        </QueryClientProvider>
      </Router>
    </HeadProvider>
  );
}

export default App;
