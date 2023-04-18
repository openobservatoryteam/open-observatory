import { useTranslation } from 'react-i18next';

import { User } from '~/api';
import { AsideAdmin, ColumnsProps, CustomTable, Title } from '~/components';

function UserAdminPage() {
  const { t } = useTranslation();

  const columns: ColumnsProps<User>[] = [
    {
      name: 'Avatar',
      render: (obj: User) => obj.avatar,
    },
    {
      name: 'Username',
      render: (obj: User) => obj.username,
    },
    {
      name: 'Email',
      render: (obj: User) => obj.username,
    },
  ];

  return (
    <div className="flex">
      <AsideAdmin selected={2} />
      <div className="flex-1">
        <Title as="h2" centered className="mt-4 mb-4">
          {t('admin.users')}
        </Title>
        <CustomTable
          data={[]}
          columns={columns}
          page={1}
          pageCount={2}
          className="w-3/4 bg-slate-500 mx-auto p-2 rounded-2xl"
        />
      </div>
    </div>
  );
}

export default UserAdminPage;
