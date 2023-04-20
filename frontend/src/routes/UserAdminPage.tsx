import { useTranslation } from 'react-i18next';

import { User, UserWithProfile } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { AsideAdmin, ColumnsProps, CustomActionProps, CustomTable, Text, Title } from '~/components';

function UserAdminPage() {
  const { t } = useTranslation();

  const columns: ColumnsProps<UserWithProfile>[] = [
    {
      name: 'Avatar',
      render: (obj: UserWithProfile) => <img src={obj.avatar ?? iconUser} className="w-10 h-10" />,
    },
    {
      name: 'Username',
      render: (obj: UserWithProfile) => <Text centered>{obj.username}</Text>,
    },
  ];

  const userData: UserWithProfile[] = [
    {
      username: 'TOTO',
      type: 'USER',
      notificationsEnabled: true,
      public: true,
      radius: 25,
      biography: '',
      karma: 1,
      achievements: [],
    },
    {
      username: 'LULULU',
      type: 'ADMIN',
      notificationsEnabled: true,
      public: true,
      radius: 25,
      biography: '',
      karma: 1,
      achievements: [],
    },
    {
      username: 'KEKE',
      type: 'USER',
      notificationsEnabled: true,
      public: true,
      radius: 25,
      biography: '',
      karma: 1,
      achievements: [],
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
