import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { ReactElement, ReactNode } from 'react';
import { faPen, faTrash } from '@fortawesome/free-solid-svg-icons';

import { Text, Button } from '~/components';

export type ColumnsProps<T> = {
  name: string;
  render: (obj: T) => ReactNode;
};

export type OtherAction<T> = {
  element: () => JSX.Element
}

export type CustomActionProps<T> = {
  edit: { onEdit: (obj: T) => void; };
  delete: { onDelete: (obj: T) => void; };
  other?: OtherAction<T>[]
};

interface CustomTableProps<T> {
  data: T[];
  page: number;
  pageCount: number;
  columns: ColumnsProps<T>[];
  customsAction?: CustomActionProps<T>;
  className?: string;
  onPageChange: (n: number) => void;
  onItemsPerPageChange: (n : number) => void;
}

export function CustomTable<T>({ data, customsAction, columns, page, pageCount, className, onItemsPerPageChange, onPageChange }: CustomTableProps<T>) {

  return (
    <div className={clsx(className)}>
      <table className="w-full">
        <thead>
          <tr className="border-b-2 border-b-white pb-3 flex justify-between">
            {columns.map((e, index) => (
              <th key={index}>
                <Text centered bold className='w-32'>
                  {e.name}
                </Text>
              </th>
            ))}
            {customsAction && (
              <th>
              <Text centered bold className='w-32'>
                  Actions
                </Text>
              </th>
            )}
          </tr>
        </thead>
        <tbody>
          {data.map((d, index) => (
            <tr key={index} className='border-b-2 border-b-white py-3 flex justify-between items-center'>
              {columns.map((e, index) => (
                <td key={e.name + index} className='w-32 flex justify-center'>
                  {e.render(d)}
                </td>
              ))}
              {customsAction && (
                <td className='flex items-center justify-end gap-x-1 w-32'>
                  <FontAwesomeIcon color="white" icon={faPen} onClick={() => customsAction.edit.onEdit(d)} className='p-2 rounded-full hover:cursor-pointer hover:bg-slate-400' />
                  <FontAwesomeIcon icon={faTrash} onClick={() => customsAction.delete.onDelete} className='text-red-500 p-2 rounded-full hover:cursor-pointer hover:bg-slate-400' />
                  {customsAction.other?.map(a => (a.element()))}
                </td>
              )}
            </tr>
          ))}
        </tbody>
      </table>
      <div className='flex justify-end items-center gap-x-5 mt-5'>
        {/* Sélection d'item par page */}
        <div>
            <select onChange={(value) => onItemsPerPageChange(parseInt(value.currentTarget.value))} className='bg-transparent text-white border-white border-2 p-1 rounded-2xl'>
              <option value={10} selected className='text-black'>10</option>
              <option value={20} className='text-black'>20</option>
              <option value={30} className='text-black'>30</option>
              <option value={50} className='text-black'>50</option>
              <option value={100} className='text-black'>100</option>
            </select>
        </div>
        {/* Sélection de la page */}
        <div className='flex gap-2'>
        {[...Array(pageCount)].map((_, n) => (
        <Button
          color={page === n ? 'white' : 'transparent'}
          className={clsx('px-4 py-1', page === n ? 'text-black' : 'text-white', 'rounded-full')}
          key={`pagination-${n}`}
          onPress={() => (page === n ? undefined : onPageChange(n))}
          rounded
        >
          {n + 1}
        </Button>
      ))}
        </div>
      </div>
    </div>
  );
}
