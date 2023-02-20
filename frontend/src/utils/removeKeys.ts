export function removeKeys<T extends object, K extends readonly (keyof T)[]>(object: T, keys: K): Omit<T, K[number]> {
  const clone = { ...object };
  keys.forEach((k) => delete clone[k]);
  return clone;
}
