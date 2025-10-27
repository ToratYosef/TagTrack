import React from 'react';
import { Alert, SafeAreaView, StyleSheet, Switch, Text, View } from 'react-native';
import { useTranslation } from 'react-i18next';
import PrimaryButton from '@/components/PrimaryButton';
import { useSettings } from '@/state/SettingsContext';
import { colors, spacing } from '@/styles/theme';

const SettingsScreen: React.FC = () => {
  const { t } = useTranslation();
  const { settings, dispatch } = useSettings();

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.row}>
        <View>
          <Text style={styles.title}>{t('settings.cloudBackup')}</Text>
          <Text style={styles.body}>{t('messages.cloudBackupDisabled')}</Text>
        </View>
        <Switch
          accessibilityLabel={t('settings.cloudBackup')}
          value={settings.cloudBackup}
          onValueChange={() => dispatch({ type: 'TOGGLE_CLOUD_BACKUP' })}
        />
      </View>
      <View style={styles.row}>
        <View>
          <Text style={styles.title}>{t('settings.analytics')}</Text>
          <Text style={styles.body}>{t('messages.analyticsOptOut')}</Text>
        </View>
        <Switch
          accessibilityLabel={t('settings.analytics')}
          value={settings.analytics}
          onValueChange={() => dispatch({ type: 'TOGGLE_ANALYTICS' })}
        />
      </View>
      <PrimaryButton
        label={t('settings.export')}
        onPress={() => Alert.alert(t('settings.exportDescription'))}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    padding: spacing.lg,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.lg,
  },
  title: {
    color: colors.primary,
    fontSize: 18,
    fontWeight: '600',
  },
  body: {
    color: colors.textSecondary,
  },
});

export default SettingsScreen;
