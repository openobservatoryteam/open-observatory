import { ForwardedRef, useEffect, useRef } from 'react';

function useForwardedRef<T>(ref?: ForwardedRef<T>) {
  const innerRef = useRef<T>(null);
  useEffect(() => {
    if (ref === null) return;
    if (typeof ref === 'function') {
      ref(innerRef.current);
    } else if (typeof ref !== 'undefined') {
      ref.current = innerRef.current;
    }
  });
  return innerRef;
}

export { useForwardedRef };
