import './i18n';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';

import App from '~/App';
import '~/index.css';

const container = document.getElementById('root')!;
createRoot(container).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
