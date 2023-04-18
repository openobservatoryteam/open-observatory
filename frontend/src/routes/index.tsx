import { Route } from '@tanstack/react-location';

import { AdminGuard, AuthenticatedGuard } from '~/guards';

const HomePage = () => import('./HomePage').then((page) => <page.default />);
const LoginPage = () => import('./LoginPage').then((page) => <page.default />);
const RegistrationPage = () => import('./RegistrationPage').then((page) => <page.default />);
const ObservationPage = () => import('./ObservationPage').then((page) => <page.default />);
const CelestialBodyAdminPage = () =>
  import('./CelestialBodyAdminPage').then((page) => (
    <AdminGuard>
      <page.default />
    </AdminGuard>
  ));

const ObservationAdminPage = () =>
  import('./ObservationAdminPage').then((page) => (
    <AdminGuard>
      <page.default />
    </AdminGuard>
  ));

const UserAdminPage = () =>
  import('./UserAdminPage').then((page) => (
    <AdminGuard>
      <page.default />
    </AdminGuard>
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
const UpdateUserPage = () =>
  import('./UpdateUserPage').then((page) => (
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

const EditNotificationPage = () =>
  import('./EditNotificationPage').then((page) => (
    <AuthenticatedGuard>
      <page.default />
    </AuthenticatedGuard>
  ));

const routes: Route[] = [
  { path: '/', element: HomePage },
  { path: '/login', element: LoginPage },
  { path: '/register', element: RegistrationPage },
  { path: '/observations/:id', element: ObservationPage },
  {
    path: '/admin',
    children: [
      { path: 'celestial-bodies', element: CelestialBodyAdminPage },
      { path: 'observations', element: ObservationAdminPage },
      { path: 'users', element: UserAdminPage },
    ],
  },
  {
    path: '/users/:username',
    children: [
      { path: '/', element: ProfilePage },
      { path: '/edit', element: UpdateUserPage },
      { path: '/observations', element: ObservationHistoryPage },
    ],
  },
  { path: '/about-us', element: AboutPage },
  { path: '/preferences', element: PreferencesPage },
  { path: '/change-password', element: ChangePasswordPage },
  { path: '/edit-notification', element: EditNotificationPage },
  { path: '/report-observation', element: ReportObservationPage },
];

export default routes;
