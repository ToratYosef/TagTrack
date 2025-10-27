import React from 'react';
import { Alert, Image, ScrollView, StyleSheet, Text, View } from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useTranslation } from 'react-i18next';
import { useAuth } from '@/state/AuthContext';
import { deleteItem, listItems, recordWorn } from '@/services/firebase';
import PrimaryButton from '@/components/PrimaryButton';
import { colors, radii, spacing } from '@/styles/theme';
import type { RootStackParamList } from '../../App';

const ItemDetailScreen: React.FC<NativeStackScreenProps<RootStackParamList, 'ItemDetail'>> = ({
  route,
  navigation,
}) => {
  const { itemId } = route.params;
  const { user } = useAuth();
  const { t } = useTranslation();
  const queryClient = useQueryClient();

  const { data: item } = useQuery({
    queryKey: ['item', user?.uid, itemId],
    queryFn: async () => {
      if (!user) {
        return null;
      }
      const items = await listItems(user);
      return items.find((candidate) => candidate.id === itemId) ?? null;
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async () => {
      if (!user) {
        return;
      }
      await deleteItem(user, itemId);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['items', user?.uid] });
      Alert.alert(t('messages.itemDeleted'));
      navigation.goBack();
    },
  });

  const wornMutation = useMutation({
    mutationFn: async () => {
      if (!user) {
        return;
      }
      await recordWorn(user, itemId);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ['item', user?.uid, itemId] });
    },
  });

  if (!item) {
    return (
      <View style={styles.empty}>
        <Text>{t('messages.nfcMissing')}</Text>
      </View>
    );
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      {item.photoURL ? (
        <Image source={{ uri: item.photoURL }} style={styles.photo} />
      ) : (
        <View style={styles.placeholder} />
      )}
      <Text style={styles.title}>{item.name}</Text>
      <Text style={styles.subtitle}>{item.type}</Text>
      <View style={styles.row}>
        <Text style={styles.label}>{t('forms.color')}</Text>
        <Text style={styles.value}>{item.color}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>{t('forms.brand')}</Text>
        <Text style={styles.value}>{item.brand}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>{t('forms.size')}</Text>
        <Text style={styles.value}>{item.size}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>{t('forms.tags')}</Text>
        <Text style={styles.value}>{item.tags.join(', ')}</Text>
      </View>
      <View style={styles.row}>
        <Text style={styles.label}>{t('forms.notes')}</Text>
        <Text style={styles.value}>{item.notes}</Text>
      </View>
      <PrimaryButton label={t('actions.recordWorn')} onPress={() => wornMutation.mutate()} />
      <PrimaryButton
        label={t('actions.delete')}
        onPress={() => deleteMutation.mutate()}
        disabled={deleteMutation.isPending}
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: spacing.lg,
    backgroundColor: colors.background,
  },
  photo: {
    width: '100%',
    height: 320,
    borderRadius: radii.lg,
    marginBottom: spacing.md,
  },
  placeholder: {
    backgroundColor: colors.surface,
    height: 320,
    borderRadius: radii.lg,
    marginBottom: spacing.md,
  },
  title: {
    fontSize: 28,
    fontWeight: '700',
    color: colors.primary,
  },
  subtitle: {
    fontSize: 18,
    color: colors.textSecondary,
    marginBottom: spacing.md,
  },
  row: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    marginBottom: spacing.sm,
  },
  label: {
    color: colors.textSecondary,
    fontWeight: '600',
  },
  value: {
    color: colors.textPrimary,
  },
  empty: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});

export default ItemDetailScreen;
