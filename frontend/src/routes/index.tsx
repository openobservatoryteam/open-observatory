import { Route } from '@tanstack/react-location';

const HomePage = () => import('./HomePage').then((page) => <page.default />);
const LoginPage = () => import('./LoginPage').then((page) => <page.default />);
const RegistrationPage = () => import('./RegistrationPage').then((page) => <page.default />);

const routes: Route[] = [
  { path: '/', element: HomePage },
  { path: '/login', element: LoginPage },
  { path: '/register', element: RegistrationPage },
];

export default routes;
