import React from 'react';
import { Image, SafeAreaView, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useTranslation } from 'react-i18next';
import PrimaryButton from '@/components/PrimaryButton';
import { colors, spacing } from '@/styles/theme';
import type { RootStackParamList } from '../../App';

const steps = [
  {
    key: 'step1',
    titleKey: 'onboarding.step1Title',
    bodyKey: 'onboarding.step1Body',
  },
  {
    key: 'step2',
    titleKey: 'onboarding.step2Title',
    bodyKey: 'onboarding.step2Body',
  },
  {
    key: 'step3',
    titleKey: 'onboarding.step3Title',
    bodyKey: 'onboarding.step3Body',
  },
];

type Props = NativeStackScreenProps<RootStackParamList, 'Onboarding'>;

const OnboardingScreen: React.FC<Props> = ({ navigation }) => {
  const { t } = useTranslation();

  return (
    <SafeAreaView style={styles.container}>
      <Image
        accessibilityIgnoresInvertColors
        source={require('../../assets/icon.png')}
        style={styles.logo}
      />
      {steps.map((step) => (
        <View key={step.key} style={styles.step}>
          <Text style={styles.title}>{t(step.titleKey)}</Text>
          <Text style={styles.body}>{t(step.bodyKey)}</Text>
        </View>
      ))}
      <PrimaryButton label={t('actions.scan')} onPress={() => navigation.replace('Home')} />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: colors.background,
    padding: spacing.lg,
  },
  logo: {
    width: 120,
    height: 120,
    marginBottom: spacing.lg,
  },
  step: {
    marginBottom: spacing.md,
    alignItems: 'center',
  },
  title: {
    fontSize: 22,
    color: colors.primary,
    fontWeight: '700',
    textAlign: 'center',
    marginBottom: spacing.xs,
  },
  body: {
    color: colors.textSecondary,
    textAlign: 'center',
  },
});

export default OnboardingScreen;
