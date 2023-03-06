import { FieldValues, UseFormRegister } from 'react-hook-form';

type FieldName<T extends FieldValues> = Parameters<UseFormRegister<T>>[0];
type FieldOptions<T extends FieldValues> = Parameters<UseFormRegister<T>>[1];

function registerAdapter<T extends FieldValues>(
  register: UseFormRegister<T>,
  name: FieldName<T>,
  options?: FieldOptions<T>,
) {
  const { onChange, ...registered } = register(name, options);
  return {
    isDisabled: !!options?.disabled,
    isRequired: !!options?.required,
    onChange: (value: unknown) => onChange({ target: { name, value } }),
    ...registered,
  };
}

export { registerAdapter };
