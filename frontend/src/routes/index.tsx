import { Route } from '@tanstack/react-location';
import HomePage from './HomePage';

import LoginPage from './LoginPage';
import RegistrationPage from './RegistrationPage';

const routes: Route[] = [
  { path: '/', element: <HomePage /> },
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegistrationPage /> },
];

export default routes;
