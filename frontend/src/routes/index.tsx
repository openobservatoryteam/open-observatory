import { Route } from '@tanstack/react-location';

import { AuthenticatedGuard } from '~/guards';

const HomePage = () => import('./HomePage').then((page) => <page.default />);
const LoginPage = () => import('./LoginPage').then((page) => <page.default />);
const RegistrationPage = () => import('./RegistrationPage').then((page) => <page.default />);
const ObservationPage = () => import('./ObservationPage').then((page) => <page.default />);
const CelestialBodyAdminPage = () =>
  import('./CelestialBodyAdminPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));
const ChangePasswordPage = () =>
  import('./ChangePasswordPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));
const ReportObservationPage = () =>
  import('./ReportObservationPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));
const ObservationHistoryPage = () => import('./ObservationHistoryPage').then((page) => <page.default />);
const ProfilePage = () => import('./ProfilePage').then((page) => <page.default />);
const EditProfilPage = () =>
  import('./EditProfilPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));

const PreferencesPage = () =>
  import('./PreferencesPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));

const AboutPage = () =>
  import('./AboutPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));

const routes: Route[] = [
  { path: '/', element: HomePage },
  { path: '/login', element: LoginPage },
  { path: '/register', element: RegistrationPage },
  { path: '/observations/:id', element: ObservationPage },
  { path: '/admin/celestial-bodies', element: CelestialBodyAdminPage },
  {
    path: '/users/:username',
    children: [
      { path: '/', element: ProfilePage },
      { path: '/edit', element: EditProfilPage },
      { path: '/observations', element: ObservationHistoryPage },
    ],
  },
  { path: '/about-us', element: AboutPage },
  { path: '/preferences', element: PreferencesPage },
  { path: '/change-password', element: ChangePasswordPage },
  { path: '/report-observation', element: ReportObservationPage },
];

export default routes;
