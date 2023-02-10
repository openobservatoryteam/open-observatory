export function removeKeys<T extends object>(object: T, keys: readonly (keyof T)[]) {
  const clone = { ...object };
  keys.forEach((k) => delete clone[k]);
  return clone;
}
