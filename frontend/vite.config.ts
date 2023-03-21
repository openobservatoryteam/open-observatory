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
      includeAssets: ['favicon.ico', 'apple-touch-icon.png', 'safari-pinned-tag.svg'],
      manifest: {
        name: 'Open Observatory',
        short_name: 'Open Obs.',
        description: "Open Observatory est une plateforme collaborative de partage d'observations d'objets célestes.",
        theme_color: '#303030',
        display: 'standalone',
        icons: [
          {
            src: '/android-chrome-36x36.png',
            sizes: '36x36',
            type: 'image/png',
          },
          {
            src: '/android-chrome-48x48.png',
            sizes: '48x48',
            type: 'image/png',
          },
          {
            src: '/android-chrome-72x72.png',
            sizes: '72x72',
            type: 'image/png',
          },
          {
            src: '/android-chrome-96x96.png',
            sizes: '96x96',
            type: 'image/png',
          },
          {
            src: '/android-chrome-144x144.png',
            sizes: '144x144',
            type: 'image/png',
          },
          {
            src: '/android-chrome-192x192.png',
            sizes: '192x192',
            type: 'image/png',
          },
          {
            src: '/android-chrome-256x256.png',
            sizes: '256x256',
            type: 'image/png',
          },
          {
            src: '/android-chrome-384x384.png',
            sizes: '384x384',
            type: 'image/png',
          },
          {
            src: '/android-chrome-512x512.png',
            sizes: '512x512',
            type: 'image/png',
          },
          {
            src: '/android-chrome-512x512.png',
            sizes: '512x512',
            type: 'image/png',
            purpose: 'any maskable',
          },
        ],
      },
      registerType: 'autoUpdate',
    }),
  ],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
