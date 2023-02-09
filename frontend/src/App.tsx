import { Outlet, ReactLocation, Router } from '@tanstack/react-location';

import Layout from '@/layout';
import routes from '@/routes';

const location = new ReactLocation();

function App() {
  return (
    <Router location={location} routes={routes}>
      <Outlet />
    </Router>
  );
}

export default App;
