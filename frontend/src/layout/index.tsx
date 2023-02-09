import { ReactNode } from 'react';

import Footer from './Footer';
import Header from './Header';

interface LayoutProps {
  children?: ReactNode;
}

function Layout({ children }: LayoutProps) {
  return (
    <>
      <Header />
      <main className="my-4">{children}</main>
      <Footer />
    </>
  );
}

export default Layout;
