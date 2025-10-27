import React from 'react';
import { FlatList, SafeAreaView, StyleSheet, Text, View } from 'react-native';
import { useQuery } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { FloatingAction } from 'react-native-floating-action';
import { colors, spacing } from '@/styles/theme';
import { useAuth } from '@/state/AuthContext';
import { listItems } from '@/services/firebase';
import Card from '@/components/Card';
import { Item } from '@/types/item';
import PrimaryButton from '@/components/PrimaryButton';
import type { RootStackParamList } from '../../App';

const actions = [
  {
    text: 'Scan',
    icon: require('../../assets/icons/nfc.png'),
    name: 'scan',
    position: 1,
    color: colors.primary,
  },
];

type Props = NativeStackScreenProps<RootStackParamList, 'Home'>;

const HomeScreen: React.FC<Props> = ({ navigation }) => {
  const { t } = useTranslation();
  const { user } = useAuth();

  const { data: items } = useQuery({
    queryKey: ['items', user?.uid],
    queryFn: async () => {
      if (!user) {
        return [] as Item[];
      }
      return listItems(user);
    },
  });

  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>{t('appName')}</Text>
        <PrimaryButton label={t('actions.addItem')} onPress={() => navigation.navigate('AddItem')} />
      </View>
      <FlatList
        accessibilityRole="list"
        data={items ?? []}
        numColumns={2}
        columnWrapperStyle={styles.row}
        keyExtractor={(item) => item.id}
        ListEmptyComponent={() => (
          <View style={styles.emptyState}>
            <Text style={styles.emptyTitle}>{t('messages.nfcRequired')}</Text>
            <Text style={styles.emptyBody}>{t('messages.nfcSubtitle')}</Text>
          </View>
        )}
        renderItem={({ item }) => (
          <Card>
            <Text style={styles.cardTitle}>{item.name}</Text>
            <Text style={styles.cardSubtitle}>{item.type}</Text>
          </Card>
        )}
      />
      <FloatingAction
        actions={actions}
        color={colors.primary}
        onPressItem={(name) => {
          if (name === 'scan') {
            navigation.navigate('Scan');
          }
        }}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    padding: spacing.md,
  },
  header: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  title: {
    fontSize: 24,
    color: colors.primary,
    fontWeight: '700',
  },
  row: {
    justifyContent: 'space-between',
    marginBottom: spacing.md,
  },
  cardTitle: {
    fontSize: 18,
    color: colors.textPrimary,
    fontWeight: '600',
  },
  cardSubtitle: {
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  emptyState: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    padding: spacing.lg,
  },
  emptyTitle: {
    fontSize: 20,
    color: colors.primary,
    textAlign: 'center',
    marginBottom: spacing.sm,
  },
  emptyBody: {
    color: colors.textSecondary,
    textAlign: 'center',
  },
});

export default HomeScreen;
