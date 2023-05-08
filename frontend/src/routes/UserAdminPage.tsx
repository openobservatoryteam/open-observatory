import { useMutation, useQuery } from '@tanstack/react-query';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useOverlayTriggerState } from 'react-stately';

import { SearchResults, UserWithProfile, deleteUser, findAllUser } from '~/api';
import iconUser from '~/assets/png/icon-user.png';
import { AsideAdmin, ColumnsProps, CustomActionProps, CustomTable, EditUserModal, Text, Title } from '~/components';

function UserAdminPage() {
  const { t } = useTranslation();
  const [editUser, setEditUser] = useState<UserWithProfile | null>(null);
  const [page, setPage] = useState<number>(0);
  const [itemsPerPage, setItemsPerPage] = useState<number>(10);
  const [users, setUsers] = useState<SearchResults<UserWithProfile>>();
  const [refetch, setRefecth] = useState<number>(0);

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

  const queryUser = useQuery({
    queryFn: () => findAllUser({ page, itemsPerPage }),
    queryKey: ['page', page, 'itemsPerPage', itemsPerPage],
    onSuccess: (res) => setUsers(res),
  });

  const remove = useMutation({
    mutationFn: deleteUser,
    onSuccess: () => setRefecth((prev) => prev + 1),
  });

  const customAction: CustomActionProps<UserWithProfile> = {
    edit: {
      onEdit: (obj: UserWithProfile) => {
        setEditUser(obj);
        editUserState.open();
      },
    },
    delete: {
      onDelete: (obj: UserWithProfile) => {
        if (confirm('Voulez-vous vraiment cet utilisateur ? Cet action est irréversible.')) {
          remove.mutate({ username: obj.username });
        }
      },
    },
  };

  return (
    <>
      {editUser && editUserState.isOpen && (
        <EditUserModal user={editUser} state={editUserState} onClose={() => setRefecth((prev) => prev + 1)} />
      )}
      <div className="flex">
        <AsideAdmin selected={2} />
        <div className="flex-1">
          <Title as="h2" centered className="mt-4 mb-4">
            {t('admin.users')}
          </Title>
          {users != undefined && !queryUser.isLoading && (
            <CustomTable
              client={queryUser}
              columns={columns}
              page={users.page}
              pageCount={users.pageCount}
              onItemsPerPageChange={(a) => setItemsPerPage(a)}
              onPageChange={(a) => setPage(a)}
              customsAction={customAction}
              refetch={refetch}
              className="w-3/4 bg-slate-500 mx-auto p-5 rounded-2xl"
            />
          )}
        </div>
      </div>
    </>
  );
}

export default UserAdminPage;
