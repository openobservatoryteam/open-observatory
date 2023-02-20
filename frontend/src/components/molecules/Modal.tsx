import { useRef } from 'react';
import { AriaModalOverlayProps, Overlay, useModalOverlay } from 'react-aria';
import { OverlayTriggerState } from 'react-stately';

type ModalProps = AriaModalOverlayProps & {
  children: React.ReactNode;
  state: OverlayTriggerState;
};

function Modal({ children, state, ...props }: ModalProps) {
  const ref = useRef(null);
  const { modalProps, underlayProps } = useModalOverlay(props, state, ref);
  return state.isOpen ? (
    <Overlay>
      <div {...underlayProps} className="fixed inset-0 flex justify-center items-center z-100 bg-slate-400/20">
        <div {...modalProps} ref={ref} className="w-full h-full flex justify-center items-center">
          {children}
        </div>
      </div>
    </Overlay>
  ) : null;
}

export { Modal };
