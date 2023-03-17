import react from '@vitejs/plugin-react-swc';
import { defineConfig } from 'vite';
import { VitePWA } from 'vite-plugin-pwa';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    react(),
    tsconfigPaths(),
    VitePWA({
      manifest: {
        name: 'Open Observatory',
        short_name: 'Open Obs.',
        description: "Open Observatory est une plateforme collaborative de partage d'observations d'objets célestes.",
        background_color: '#101010',
        theme_color: '#101010',
        icons: [
          {
            src: '/logo.png',
            sizes: '192x192',
            type: 'image/png',
          },
        ],
      },
      registerType: 'autoUpdate',
    }),
  ],
});
