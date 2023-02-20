import { ComponentPropsWithoutRef, ForwardedRef, forwardRef, RefObject, useRef } from 'react';
import {
  AriaSliderProps,
  mergeProps,
  useFocusRing,
  useNumberFormatter,
  useSlider,
  useSliderThumb,
  VisuallyHidden,
} from 'react-aria';
import { SliderState, useSliderState } from 'react-stately';

import { Text } from '@/components';
import { removeKeys } from '@/utils';
import { useForwardedRef } from '@/hooks';

type SliderProps = AriaSliderProps<number> &
  Omit<ComponentPropsWithoutRef<'div'>, keyof AriaSliderProps<number>> & {
    formatOptions?: Intl.NumberFormatOptions & { numberingSystem?: string };
    withMarks?: boolean;
  };

function Slider({ className, formatOptions, withMarks, ...props }: SliderProps, ref: ForwardedRef<HTMLInputElement>) {
  const inputRef = ref ? useForwardedRef(ref) : useRef<HTMLInputElement>(null);
  const trackRef = useRef(null);
  const numberFormatter = useNumberFormatter(formatOptions);
  const state = useSliderState({ numberFormatter, ...props });
  const { groupProps, labelProps, outputProps, trackProps } = useSlider(props, state, trackRef);
  const marksCount = withMarks ? Math.floor((state.getThumbMaxValue(0) - state.getThumbMinValue(0)) / state.step) : 0;
  return (
    <div className={className} {...groupProps}>
      {props.label && (
        <div className="flex justify-between mb-2">
          <Text as="label" {...removeKeys(labelProps, ['color'])}>
            {props.label}
          </Text>
          <Text as="output" {...removeKeys(outputProps, ['color'])}>
            {state.getThumbValueLabel(0)}
          </Text>
        </div>
      )}
      <div
        className="border-2 border-neutral-500 bg-neutral-500 h-2 relative rounded-md touch-none"
        {...trackProps}
        ref={trackRef}
      >
        <div
          className="absolute border-2 border-white bg-white h-2 -left-0.5 rounded-l-md -top-0.5"
          style={{
            width: `${
              ((state.getThumbValue(0) - state.getThumbMinValue(0)) /
                (state.getThumbMaxValue(0) - state.getThumbMinValue(0))) *
              100
            }%`,
          }}
        ></div>
        <Thumb index={0} inputRef={inputRef} state={state} trackRef={trackRef} />
        {marksCount > 0 &&
          [...Array(marksCount)].map((_, i) => <Mark key={`step_${i}/${marksCount}`} nth={i} total={marksCount} />)}
      </div>
    </div>
  );
}

type ThumbProps = {
  index: number;
  inputRef: RefObject<HTMLInputElement>;
  state: SliderState;
  trackRef: RefObject<Element>;
};

function Thumb({ index, inputRef, state, trackRef }: ThumbProps) {
  const { thumbProps, inputProps } = useSliderThumb(
    {
      index,
      inputRef,
      trackRef,
    },
    state,
  );
  const { focusProps } = useFocusRing();
  return (
    <div className="absolute top-0.5 bg-white h-5 rounded-full shadow-md w-5" {...thumbProps}>
      <VisuallyHidden>
        <input ref={inputRef} {...mergeProps(inputProps, focusProps)} />
      </VisuallyHidden>
    </div>
  );
}

type MarkProps = {
  nth: number;
  total: number;
};

function Mark({ nth, total }: MarkProps) {
  return (
    <div
      className="absolute bg-white h-1 w-1 rounded-full"
      style={{
        left: `calc(${((nth + 1) / total) * 100}% - 2px)`,
      }}
    />
  );
}

const _Slider = forwardRef(Slider);
export { _Slider as Slider };
