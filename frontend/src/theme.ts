import { createTheme } from '@mui/material/styles';
import PoppinsRegular from '@/fonts/Poppins-Regular.ttf';
import PoppinsSemiBold from '@/fonts/Poppins-SemiBold.ttf';

const theme = createTheme({
  components: {
    MuiCssBaseline: {
      styleOverrides: `
        @font-face {
          font-family: 'PoppinsRegular';
          src: local('Raleway'), local('Raleway-Regular'), url(${PoppinsRegular}) format('ttf');
        }
        @font-face {
          font-family: 'PoppinsSemiBold';
          src: local('Raleway'), local('Raleway-Regular'), url(${PoppinsSemiBold}) format('ttf');
        }
      `,
    },
  },
});

theme.typography.h1 = {
  fontFamily: 'PoppinsRegular, Arial',
  fontSize: 35,
  [theme.breakpoints.down('sm')]: {
    fontSize: 16,
  },
};

theme.typography.h2 = {
  fontFamily: 'PoppinsSemiRegular, Arial',
  fontSize: 26,
  [theme.breakpoints.down('sm')]: {
    fontSize: 16,
  },
};

theme.typography.body1 = {
  fontFamily: 'PoppinsRegular, Arial',
  fontSize: 20,
  [theme.breakpoints.down('sm')]: {
    fontSize: 16,
  },
};

theme.typography.caption = {
  fontFamily: 'PoppinsRegular, Arial',
  fontSize: 16,
  [theme.breakpoints.down('sm')]: {
    fontSize: 13,
  },
};

export default theme;
