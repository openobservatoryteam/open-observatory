import { ComponentPropsWithoutRef, ReactElement } from 'react';

import { Pagination } from '@/components';

type ListProps<T> = ComponentPropsWithoutRef<'div'> & {
  data: readonly T[];
  pageCount: number;
  render: (item: T) => ReactElement;
};

function List<T>({ data, pageCount, render, ...props }: ListProps<T>) {
  return (
    <div {...props}>
      <div className="gap-y-6 lg:gap-x-16 lg:gap-y-10 grid lg:grid-cols-2 xl:grid-cols-3">{data.map(render)}</div>
      <Pagination className="flex justify-end mt-8 mb-16" pageCount={pageCount} />
    </div>
  );
}

export { List };
