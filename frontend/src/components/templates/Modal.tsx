import { createRef, useRef, useState } from 'react';
import { AriaModalOverlayProps, Overlay, OverlayTriggerAria, useModalOverlay } from 'react-aria';
import { OverlayTriggerState } from 'react-stately';

type ModalProps = AriaModalOverlayProps & {
  children: React.ReactNode;
  state: OverlayTriggerState;
};

function Modal({ children, state, ...props }: ModalProps) {
  let ref = createRef<HTMLDivElement>();
  console.log(props, state, ref);
  let { modalProps, underlayProps } = useModalOverlay(props, state, ref);
  const [exited, setExited] = useState(!state.isOpen);

  if (!(state.isOpen || !exited)) {
    return null;
  }

  return (
    <Overlay>
      <div {...underlayProps} className="fixed inset-0 flex justify-center z-100 bg-slate-400/20">
        <div {...modalProps} ref={ref}>
          {children}
        </div>
      </div>
    </Overlay>
  );
}

export { Modal };
