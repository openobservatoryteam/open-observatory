import * as Sentry from '@sentry/react';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import App from '~/App';
import '~/i18n';
import '~/index.css';

Sentry.init({
  dsn: 'https://12aaaa71614245679cb8b3cbe9e0e3eb@o4504931937812480.ingest.sentry.io/4504931987357696',
  integrations: [new Sentry.BrowserTracing()],

  // Set tracesSampleRate to 1.0 to capture 100%
  // of transactions for performance monitoring.
  // We recommend adjusting this value in production
  tracesSampleRate: 2.0,
});

const container = document.getElementById('root')!;
createRoot(container).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
