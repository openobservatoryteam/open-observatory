import clsx from 'clsx';
import { ComponentPropsWithoutRef, useState } from 'react';
import { Button } from '../atoms/Button';

type PaginationProps = ComponentPropsWithoutRef<'div'> & { pageCount: number };

function Pagination({ className, pageCount, ...props }: PaginationProps) {
  const [page, setPage] = useState(0);
  return (
    <div className={clsx('flex gap-2', className)} {...props}>
      {[...Array(pageCount)].map((_, n) => (
        <Button
          color={page === n ? 'white' : 'transparent'}
          className={clsx('px-4 py-1.5', page === n ? 'text-black' : 'text-white')}
          key={`pagination-${n}`}
          onPress={() => setPage(n)}
          rounded
        >
          {n}
        </Button>
      ))}
    </div>
  );
}

export { Pagination };
