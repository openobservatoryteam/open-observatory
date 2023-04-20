import { faShieldHalved, faUserShield } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { useTranslation } from 'react-i18next';

import { User } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { AsideAdmin, ColumnsProps, CustomActionProps, CustomTable, Text, Title } from '~/components';

function UserAdminPage() {
  const { t } = useTranslation();

  const columns: ColumnsProps<User>[] = [
    {
      name: 'Avatar',
      render: (obj: User) => <img src={obj.avatar ?? iconUser} className="w-10 h-10" />,
    },
    {
      name: 'Username',
      render: (obj: User) => <Text centered>{obj.username}</Text>,
    },
  ];

  const userData: User[] = [
    {
      username: 'TOTO',
      type: 'USER',
      notificationsEnabled: true,
      public: true,
      radius: 25,
    },
    {
      username: 'LULULU',
      type: 'ADMIN',
      notificationsEnabled: true,
      public: true,
      radius: 25,
    },
    {
      username: 'KEKE',
      type: 'USER',
      notificationsEnabled: true,
      public: true,
      radius: 25,
    },
  ];

  const customAction: CustomActionProps<User> = {
    edit: {
      onEdit: (obj) => {
        console.log(obj.username);
      },
    },
    delete: {
      onDelete: (obj) => {
        console.log(obj.username);
      },
    },
    other: [
      {
        element: (obj: User) => (
          <>
            {obj.type !== 'ADMIN' && (
              <span className="tooltip">
                <FontAwesomeIcon
                  icon={faShieldHalved}
                  color="white"
                  className="p-2 rounded-full hover:cursor-pointer hover:bg-slate-400 w-5 tooltip"
                  onClick={() => console.log(obj.username)}
                />
                <span className="tooltiptext">{t('common.user')}</span>
              </span>
            )}
            {obj.type === 'ADMIN' && (
              <span className="tooltip">
                <FontAwesomeIcon
                  icon={faUserShield}
                  color="white"
                  className="p-2 rounded-full hover:cursor-pointer hover:bg-slate-400 w-5"
                  onClick={() => console.log(obj.username)}
                />
                <span className="tooltiptext">{t('common.admin')}</span>
              </span>
            )}
          </>
        ),
      },
    ],
  };

  return (
    <div className="flex">
      <AsideAdmin selected={2} />
      <div className="flex-1">
        <Title as="h2" centered className="mt-4 mb-4">
          {t('admin.users')}
        </Title>
        <CustomTable
          data={userData}
          columns={columns}
          page={0}
          pageCount={2}
          onItemsPerPageChange={(a) => console.log(a)}
          onPageChange={(a) => console.log(a)}
          customsAction={customAction}
          className="w-3/4 bg-slate-500 mx-auto p-5 rounded-2xl"
        />
      </div>
    </div>
  );
}

export default UserAdminPage;
