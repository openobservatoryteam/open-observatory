import { Link } from '@tanstack/react-location';
import { useTranslation } from 'react-i18next';

import logo from '~/assets/logo.svg';
import { Button, Text } from '~/components';

type AsideAdminProps = {
  selected: 0 | 1 | 2;
};

export function AsideAdmin({ selected = 0 }: AsideAdminProps) {
  const { t } = useTranslation();

  return (
    <aside className="bg-[#333C47] min-h-screen pt-4 px-3 md:px-12">
      <Link title="Accueil Open Observatory" to="/">
        <img src={logo} alt="Logo Open Observatory" />
      </Link>
      <Text centered className="mb-20 mt-4">
        {t('common.administrator')}
      </Text>
      <ul>
        <li>
          <Button
            as={Link}
            className={`mb-12 py-4 text-xl hover:bg-white hover:opacity-80 hover:text-black ${
              selected !== 0 ? 'bg-transparent text-white' : 'bg-white text-black'
            }`}
            to="/admin/observations"
          >
            {t('common.observations')}
          </Button>
        </li>
        <li>
          <Button
            as={Link}
            className={`mb-12 py-4 text-xl ${
              selected !== 1
                ? 'bg-transparent hover:bg-white hover:opacity-80 hover:text-black text-white'
                : 'bg-white text-black'
            }`}
            to="/admin/celestial-bodies"
          >
            {t('common.celestialBody')}
          </Button>
        </li>
        <li>
          <Button
            as={Link}
            className={`mb-12 py-4 text-xl ${
              selected !== 2
                ? 'bg-transparent hover:bg-white hover:opacity-80 hover:text-black text-white'
                : 'bg-white text-black'
            }`}
            to="/admin/users"
          >
            {t('common.users')}
          </Button>
        </li>
      </ul>
    </aside>
  );
}
