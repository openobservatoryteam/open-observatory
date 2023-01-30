import { Outlet, ReactLocation, Router } from '@tanstack/react-location';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import routes from '@/routes';

const location = new ReactLocation();
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: false,
    },
  },
  logger: { error: () => void 0, log: () => void 0, warn: () => void 0 },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Router location={location} routes={routes}>
        <Outlet />
      </Router>
    </QueryClientProvider>
  );
}
