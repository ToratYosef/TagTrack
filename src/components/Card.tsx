import React from 'react';
import { StyleSheet, View } from 'react-native';
import { colors, radii, spacing } from '@/styles/theme';

const Card: React.FC<{ children: React.ReactNode; testID?: string }> = ({ children, testID }) => (
  <View accessibilityRole="summary" style={styles.container} testID={testID}>
    {children}
  </View>
);

const styles = StyleSheet.create({
  container: {
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: spacing.md,
    shadowColor: '#000000',
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 3,
  },
});

export default Card;
