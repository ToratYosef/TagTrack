declare module 'react-native-floating-action' {
  import * as React from 'react';
  import { StyleProp, ViewStyle } from 'react-native';

  export interface ActionProps {
    text: string;
    icon?: number;
    name: string;
    position?: number;
    color?: string;
  }

  export interface FloatingActionProps {
    actions: ActionProps[];
    color?: string;
    onPressItem?: (name?: string) => void;
    floatingIcon?: number;
    overlayColor?: string;
    distanceToEdge?: number;
    showBackground?: boolean;
    visible?: boolean;
    position?: 'left' | 'right' | 'center';
    animated?: boolean;
    actionsPaddingTopBottom?: number;
    actionsPaddingRight?: number;
    actionsPaddingLeft?: number;
    iconWidth?: number;
    iconHeight?: number;
    iconColor?: string;
    floatingIcon?: React.ReactNode;
    shadow?: StyleProp<ViewStyle>;
  }

  export class FloatingAction extends React.Component<FloatingActionProps> {}
  export default FloatingAction;
}
