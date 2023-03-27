import { faCalendar } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { ComponentPropsWithoutRef, ForwardedRef, ReactNode, forwardRef } from 'react';

type DatePickerProps = ComponentPropsWithoutRef<'input'> & { errorMessage?: ReactNode };

function DatePicker({ className, errorMessage, ...props }: DatePickerProps, ref?: ForwardedRef<HTMLInputElement>) {
  return (
    <div>
      <div className={clsx('bg-white flex px-4 py-2 rounded-3xl text-black w-full', className)}>
        <input className="bg-inherit mr-auto outline-none w-full" ref={ref} type="datetime-local" {...props} />
        <FontAwesomeIcon className="my-auto mr-1" icon={faCalendar} />
      </div>
      {errorMessage && <p className="text-center text-red-500">{errorMessage}</p>}
    </div>
  );
}

const _DatePicker = forwardRef(DatePicker);
export { _DatePicker as DatePicker };
