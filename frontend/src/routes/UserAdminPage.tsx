import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useOverlayTriggerState } from 'react-stately';

import { UserWithProfile } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { AsideAdmin, ColumnsProps, CustomActionProps, CustomTable, EditUserModal, Text, Title } from '~/components';

function UserAdminPage() {
  const { t } = useTranslation();
  const [editUser, setEditUser] = useState<UserWithProfile | null>(null);
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

  const editUserState = useOverlayTriggerState({
    onOpenChange: (isOpen) => (isOpen ? undefined : setEditUser(null)),
  });

  const userData: UserWithProfile[] = [
    {
      username: 'TOTO',
      type: 'USER',
      notificationEnabled: true,
      public: true,
      notificationRadius: 25,
      biography: '',
      karma: 1,
      achievements: [],
    },
    {
      username: 'LULULU',
      type: 'ADMIN',
      notificationEnabled: true,
      public: true,
      notificationRadius: 25,
      biography: '',
      karma: 1,
      achievements: [],
    },
    {
      username: 'KEKE',
      type: 'USER',
      notificationEnabled: true,
      public: true,
      notificationRadius: 25,
      biography: '',
      karma: 1,
      achievements: [],
    },
  ];

  const customAction: CustomActionProps<UserWithProfile> = {
    edit: {
      onEdit: (obj: UserWithProfile) => {
        setEditUser(obj);
        editUserState.open();
      },
    },
    delete: {
      onDelete: (obj: UserWithProfile) => {
        console.log(obj.username);
      },
    },
  };

  return (
    <>
      {editUser && editUserState.isOpen && <EditUserModal user={editUser} state={editUserState} />}
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
    </>
  );
}

export default UserAdminPage;
