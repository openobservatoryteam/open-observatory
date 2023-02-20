import { ComponentPropsWithoutRef, ReactElement } from 'react';

import { Pagination } from '@/components';

type ListProps<T> = ComponentPropsWithoutRef<'div'> & {
  data: readonly T[];
  currentPage: number;
  pageCount: number;
  onPageChange: (n: number) => void;
  render: (item: T) => ReactElement;
};

function List<T>({ currentPage, data, onPageChange, pageCount, render, ...props }: ListProps<T>) {
  return (
    <div {...props}>
      <div className="gap-y-6 lg:gap-x-16 lg:gap-y-10 grid lg:grid-cols-2 xl:grid-cols-3">{data.map(render)}</div>
      <Pagination
        className="flex justify-end mt-8 mb-16"
        currentPage={currentPage}
        onPageChange={onPageChange}
        pageCount={pageCount}
      />
    </div>
  );
}

export { List };
