import { faCaretDown } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import clsx from 'clsx';
import { ComponentPropsWithoutRef, ForwardedRef, forwardRef } from 'react';

type SelectProps = ComponentPropsWithoutRef<'select'> & {
  options?: { name: string; value: string }[];
};

function Select({ className, options = [], ...props }: SelectProps, ref?: ForwardedRef<HTMLSelectElement>) {
  const classes = clsx('appearance-none bg-white px-4 py-2 rounded-3xl w-full invalid:text-gray-400', className);
  return (
    <div className="relative">
      <select {...props} className={classes} defaultValue="" ref={ref}>
        <option disabled value="">
          {props.placeholder}
        </option>
        {options.map((o) => (
          <option className="text-black" key={o.value} value={o.value}>
            {o.name}
          </option>
        ))}
      </select>
      <FontAwesomeIcon className="absolute h-6 right-5 top-2" icon={faCaretDown} />
    </div>
  );
}

const _Select = forwardRef(Select);
export { _Select as Select };
