import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import App from '~/App';
import '~/i18n';
import '~/index.css';

const container = document.getElementById('root')!;
createRoot(container).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
