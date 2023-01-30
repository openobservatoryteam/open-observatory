import { type Route } from '@tanstack/react-location';

// Lazy loading
const HomePage = () => import('./HomePage').then((page) => <page.default />);

export default [{ path: '/', element: HomePage }] satisfies Route[];
