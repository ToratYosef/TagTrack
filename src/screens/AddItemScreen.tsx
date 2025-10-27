import React, { useState } from 'react';
import {
  Alert,
  Image,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { NativeStackScreenProps } from '@react-navigation/native-stack';
import * as ImagePicker from 'expo-image-picker';
import { useForm, Controller } from 'react-hook-form';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import PrimaryButton from '@/components/PrimaryButton';
import { colors, radii, spacing } from '@/styles/theme';
import { scanNfcAndStartAdd } from '@/services/nfc';
import { createItem } from '@/services/firebase';
import { useAuth } from '@/state/AuthContext';
import type { RootStackParamList } from '../../App';

interface FormValues {
  name: string;
  type: 'Shirt' | 'Pants' | 'Shoes' | 'Accessory' | 'Outerwear' | 'Other';
  color?: string;
  brand?: string;
  size?: string;
  tags: string;
  notes?: string;
}

type Props = NativeStackScreenProps<RootStackParamList, 'AddItem'>;

const AddItemScreen: React.FC<Props> = ({ navigation }) => {
  const { user } = useAuth();
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const [nfcUid, setNfcUid] = useState<string | null>(null);
  const [photo, setPhoto] = useState<string | null>(null);

  const { control, handleSubmit, watch } = useForm<FormValues>({
    defaultValues: {
      name: '',
      type: 'Shirt',
      tags: '',
    },
  });

  const pickPhoto = async () => {
    const result = await ImagePicker.launchImageLibraryAsync({ mediaTypes: ImagePicker.MediaTypeOptions.Images });
    if (!result.canceled) {
      setPhoto(result.assets[0].uri);
    }
  };

  const { mutateAsync, isPending } = useMutation({
    mutationFn: async (values: FormValues) => {
      if (!user) {
        throw new Error('Not authenticated');
      }
      if (!nfcUid) {
        throw new Error('NFC UID missing');
      }
      const tagsArray = values.tags
        .split(',')
        .map((tag) => tag.trim())
        .filter(Boolean);
      const item = await createItem(user, {
        name: values.name,
        type: values.type,
        color: values.color,
        brand: values.brand,
        size: values.size,
        tags: tagsArray,
        notes: values.notes,
        photoURL: photo,
        nfcUID: nfcUid,
      });
      await queryClient.invalidateQueries({ queryKey: ['items', user.uid] });
      return item;
    },
    onSuccess: (item) => {
      Alert.alert(t('messages.itemSaved'));
      navigation.replace('ItemDetail', { itemId: item.id });
    },
    onError: (error: Error & { code?: string }) => {
      if (error.code === 'NFCTAG_DUPLICATE') {
        Alert.alert(t('messages.nfcDuplicateTitle'), t('messages.nfcDuplicateBody', { itemName: '' }));
      } else {
        Alert.alert('Error', error.message);
      }
    },
  });

  const startScan = async () => {
    try {
      const { nfcUID } = await scanNfcAndStartAdd();
      setNfcUid(nfcUID);
    } catch (error) {
      Alert.alert(t('messages.nfcPermissionDenied'));
    }
  };

  const onSubmit = (values: FormValues) => {
    void mutateAsync(values);
  };

  const isValid = Boolean(watch('name')) && Boolean(photo) && Boolean(nfcUid);

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <Text style={styles.title}>{t('messages.nfcRequired')}</Text>
      <Text style={styles.subtitle}>{t('messages.nfcSubtitle')}</Text>
      <PrimaryButton label={nfcUid ?? t('actions.scan')} onPress={startScan} />
      <View accessible accessibilityLabel={t('forms.photo')} style={styles.photoContainer}>
        {photo ? <Image source={{ uri: photo }} style={styles.photo} /> : <Text>{t('forms.photo')}</Text>}
        <PrimaryButton label={t('forms.photo')} onPress={pickPhoto} />
      </View>
      <Controller
        control={control}
        name="name"
        rules={{ required: true }}
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.name')}
            placeholder={t('forms.namePlaceholder')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="type"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.type')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="color"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.color')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="brand"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.brand')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="size"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.size')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="tags"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.tags')}
            style={styles.input}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <Controller
        control={control}
        name="notes"
        render={({ field: { onChange, value } }) => (
          <TextInput
            accessibilityLabel={t('forms.notes')}
            style={[styles.input, styles.multiline]}
            multiline
            numberOfLines={3}
            onChangeText={onChange}
            value={value}
          />
        )}
      />
      <PrimaryButton
        label={t('actions.save')}
        onPress={handleSubmit(onSubmit)}
        disabled={!isValid || isPending}
      />
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    padding: spacing.lg,
    backgroundColor: colors.background,
  },
  title: {
    fontSize: 24,
    color: colors.primary,
    fontWeight: '700',
    marginBottom: spacing.sm,
  },
  subtitle: {
    color: colors.textSecondary,
    marginBottom: spacing.md,
  },
  photoContainer: {
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: spacing.md,
    alignItems: 'center',
    marginBottom: spacing.md,
  },
  photo: {
    width: 160,
    height: 160,
    borderRadius: radii.md,
    marginBottom: spacing.sm,
  },
  input: {
    backgroundColor: colors.surface,
    borderRadius: radii.md,
    padding: spacing.md,
    fontSize: 16,
    color: colors.textPrimary,
    marginBottom: spacing.sm,
  },
  multiline: {
    minHeight: 120,
    textAlignVertical: 'top',
  },
});

export default AddItemScreen;
