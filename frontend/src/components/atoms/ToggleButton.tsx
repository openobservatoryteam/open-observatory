import clsx from 'clsx';
import { useState } from 'react';

import { Text } from '~/components/atoms/Text';

type ToggleButtonProps = {
  value: boolean;
  handleChange: (x: boolean) => void;
  onLabel: string;
  offLabel: string;
  className?: string;
};

function ToggleButton({ value, handleChange, onLabel, offLabel, className }: ToggleButtonProps) {
  const [isChecked, setIsChecked] = useState<boolean>(value);

  const handleToggle = () => {
    setIsChecked(!isChecked);
    handleChange(!isChecked);
  };

  return (
    <div className={clsx('flex justify-center items-center', className)}>
      <button
        type="button"
        onClick={handleToggle}
        className={`relative flex items-center flex-start w-10 h-4 rounded-full transition-colors duration-300 ${
          isChecked ? 'bg-green-800' : 'bg-red-800'
        }`}
      >
        <span
          className={`inline-block w-5 h-5 rounded-full transition-transform duration-300 ease-in-out ${
            isChecked ? 'translate-x-6' : '-translate-x-1'
          } ${isChecked ? 'bg-green-500' : 'bg-red-500'} shadow-md transform ring-0`}
        ></span>
      </button>
      <Text as={'span'} className="ml-5 w-24">
        {isChecked ? onLabel : offLabel}
      </Text>
    </div>
  );
}

export { ToggleButton };
