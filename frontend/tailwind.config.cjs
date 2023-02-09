/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['index.html', 'src/**/*.{css,ts,tsx}'],
  theme: {
    extend: {
      backgroundImage: {
        desktop: "url('/assets/background.png')",
        mobile: "url('/assets/background-mobile.png')",
      },
    },
  },
  plugins: [],
};
