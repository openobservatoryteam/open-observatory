import clsx from 'clsx';
import { ComponentPropsWithoutRef, ForwardedRef, ReactNode, forwardRef } from 'react';

type DatePickerProps = ComponentPropsWithoutRef<'input'> & { errorMessage?: ReactNode };

function DatePicker({ className, errorMessage, ...props }: DatePickerProps, ref?: ForwardedRef<HTMLInputElement>) {
  return (
    <div>
      <input
        className={clsx('bg-white flex mr-auto px-4 py-2 outline-none rounded-3xl text-black w-full', className)}
        ref={ref}
        type="datetime-local"
        {...props}
      />
      {errorMessage && <p className="text-center text-red-500">{errorMessage}</p>}
    </div>
  );
}

const _DatePicker = forwardRef(DatePicker);
export { _DatePicker as DatePicker };
