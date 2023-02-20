export type AsProps<C extends React.ElementType, R extends boolean = false> = R extends true ? { as: C } : { as?: C };

export type PolymorphicRef<C extends React.ElementType> = React.ComponentPropsWithRef<C>['ref'];
