import React, { useState } from 'react';
import { ActivityIndicator, Alert, SafeAreaView, StyleSheet, Text } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useTranslation } from 'react-i18next';
import { useAuth } from '@/state/AuthContext';
import { getItemByNfc } from '@/services/firebase';
import { scanNfcAndStartAdd } from '@/services/nfc';
import { colors, spacing } from '@/styles/theme';
import type { RootStackParamList } from '../../App';

const ScanScreen: React.FC<NativeStackScreenProps<RootStackParamList, 'Scan'>> = ({ navigation }) => {
  const { t } = useTranslation();
  const { user } = useAuth();
  const [isScanning, setIsScanning] = useState(false);

  const startScan = async () => {
    setIsScanning(true);
    try {
      const { nfcUID } = await scanNfcAndStartAdd();
      if (!user) {
        Alert.alert(t('messages.nfcMissing'));
        return;
      }
      const item = await getItemByNfc(user, nfcUID);
      if (item) {
        navigation.replace('ItemDetail', { itemId: item.id });
      } else {
        Alert.alert(t('messages.nfcMissing'), undefined, [
          {
            text: t('actions.cancel'),
            style: 'cancel',
          },
          {
            text: t('actions.addItem'),
            onPress: () => navigation.replace('AddItem'),
          },
        ]);
      }
    } catch (error) {
      Alert.alert(t('messages.nfcPermissionDenied'));
    } finally {
      setIsScanning(false);
    }
  };

  React.useEffect(() => {
    void startScan();
  }, []);

  return (
    <SafeAreaView style={styles.container}>
      <Text style={styles.title}>{t('messages.nfcRequired')}</Text>
      <Text style={styles.subtitle}>{t('messages.nfcSubtitle')}</Text>
      {isScanning && <ActivityIndicator color={colors.primary} size="large" />}
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: colors.background,
    padding: spacing.lg,
  },
  title: {
    fontSize: 24,
    color: colors.primary,
    fontWeight: '700',
    marginBottom: spacing.sm,
  },
  subtitle: {
    color: colors.textSecondary,
    textAlign: 'center',
    marginBottom: spacing.md,
  },
});

export default ScanScreen;
