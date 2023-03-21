import { FieldValues, Path, RegisterOptions, UseFormRegister } from 'react-hook-form';

function registerAdapter<T extends FieldValues>(
  register: UseFormRegister<T>,
  name: Path<T>,
  options?: RegisterOptions<T, Path<T>>,
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
