import { Outlet, ReactLocation, Router } from '@tanstack/react-location';
import { HeadProvider } from 'react-head';

import routes from '@/routes';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthenticationProvider } from './providers';

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
