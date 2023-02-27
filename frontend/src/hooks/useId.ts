import { useMemo } from 'react';

const generateId = () => (Math.random() + 1).toString(36).substring(7);

function useId() {
  return useMemo(generateId, []);
}

export { useId };
