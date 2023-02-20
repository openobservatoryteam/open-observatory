import clsx from 'clsx';
import { ComponentPropsWithoutRef } from 'react';
import { Button } from '../atoms/Button';

type PaginationProps = ComponentPropsWithoutRef<'div'> & {
  currentPage: number;
  pageCount: number;
  onPageChange: (n: number) => void;
};

function Pagination({ className, currentPage, onPageChange, pageCount, ...props }: PaginationProps) {
  return (
    <div className={clsx('flex gap-2', className)} {...props}>
      {[...Array(pageCount)].map((_, n) => (
        <Button
          color={currentPage === n ? 'white' : 'transparent'}
          className={clsx('px-4 py-1.5', currentPage === n ? 'text-black' : 'text-white')}
          key={`pagination-${n}`}
          onPress={() => (currentPage === n ? undefined : onPageChange(n))}
          rounded
        >
          {n + 1}
        </Button>
      ))}
    </div>
  );
}

export { Pagination };
