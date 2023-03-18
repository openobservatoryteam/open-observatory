import { Route } from '@tanstack/react-location';

const HomePage = () => import('./HomePage').then((page) => <page.default />);
const LoginPage = () => import('./LoginPage').then((page) => <page.default />);
const RegistrationPage = () => import('./RegistrationPage').then((page) => <page.default />);
const ObservationPage = () => import('./ObservationPage').then((page) => <page.default />);
const CelestialBodyAdminPage = () => import('./CelestialBodyAdminPage').then((page) => <page.default />);
const ChangePasswordPage = () => import('./ChangePasswordPage').then((page) => <page.default />);
<<<<<<< HEAD
const ReportObservationPage = () => import('./ReportObservationPage').then((page) => <page.default />);
=======
>>>>>>> frontend-history-observation
const ObservationHistoryPage = () => import('./ObservationHistoryPage').then((page) => <page.default />);

const routes: Route[] = [
  { path: '/', element: HomePage },
  { path: '/login', element: LoginPage },
  { path: '/register', element: RegistrationPage },
  { path: '/observations/:id', element: ObservationPage },
  { path: '/admin/celestial-bodies', element: CelestialBodyAdminPage },
  { path: '/change-password', element: ChangePasswordPage },
<<<<<<< HEAD
  { path: '/report-observation', element: ReportObservationPage },
=======
>>>>>>> frontend-history-observation
  { path: '/users/:username/observations', element: ObservationHistoryPage },
];

export default routes;
