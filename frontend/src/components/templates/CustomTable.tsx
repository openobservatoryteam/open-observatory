import clsx from 'clsx';

import { Text } from '~/components';

export type ColumnsProps<T> = {
  name: string;
  render: (obj: T) => string | number | undefined;
};

export type CustomActionProps<T> = {
  edit: { onEdit: (obj: T) => void; icon?: JSX.Element };
  delete: { onDelete: (id: number) => void; icon?: JSX.Element };
};

interface CustomTableProps<T> {
  data: T[];
  page: number;
  pageCount: number;
  columns: ColumnsProps<T>[];
  customsAction?: CustomActionProps<T>;
  className?: string;
}

export function CustomTable<T>({ data, customsAction, columns, page, pageCount, className }: CustomTableProps<T>) {
  if (customsAction) {
    columns.push({ name: 'Action', render: (obj) => '' });
  }
  return (
    <div className={clsx(className)}>
      <table className="w-full">
        <thead>
          <tr className="border-b-2 border-b-slate-500 pb-3 flex justify-between">
            {columns.map((e, index) => (
              <th key={index}>
                <Text centered bold>
                  {e.name}
                </Text>
              </th>
            ))}
          </tr>
        </thead>
        <tbody></tbody>
      </table>
    </div>
  );
}
