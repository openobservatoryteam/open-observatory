import { Outlet, ReactLocation, Router } from '@tanstack/react-location';
import { HeadProvider } from 'react-head';

import routes from '@/routes';

const location = new ReactLocation();

function App() {
  return (
    <HeadProvider>
      <Router location={location} routes={routes}>
        <Outlet />
      </Router>
    </HeadProvider>
  );
}

export default App;
