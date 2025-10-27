import React from 'react';
import { Pressable, StyleSheet, Text } from 'react-native';
import { colors, radii, spacing } from '@/styles/theme';

type Props = {
  label: string;
  onPress: () => void;
  disabled?: boolean;
  testID?: string;
};

const PrimaryButton: React.FC<Props> = ({ label, onPress, disabled, testID }) => (
  <Pressable
    accessibilityRole="button"
    accessibilityState={{ disabled }}
    onPress={disabled ? undefined : onPress}
    style={({ pressed }) => [
      styles.button,
      pressed && styles.pressed,
      disabled && styles.disabled,
    ]}
    testID={testID}
  >
    <Text style={styles.label}>{label}</Text>
  </Pressable>
);

const styles = StyleSheet.create({
  button: {
    backgroundColor: colors.primary,
    borderRadius: radii.md,
    paddingVertical: spacing.md,
    alignItems: 'center',
  },
  pressed: {
    opacity: 0.8,
  },
  disabled: {
    backgroundColor: colors.accent,
    opacity: 0.5,
  },
  label: {
    color: '#FFFFFF',
    fontSize: 16,
    fontWeight: '600',
  },
});

export default PrimaryButton;
