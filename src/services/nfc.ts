import NfcManager, { NfcTech, TagEvent } from 'react-native-nfc-manager';
import { Platform } from 'react-native';
import { ApiError } from '@/types/item';

const NFC_TIMEOUT_MS = 15000;

export async function ensureNfcIsSupported(): Promise<boolean> {
  const isSupported = await NfcManager.isSupported();
  if (!isSupported) {
    return false;
  }
  await NfcManager.start();
  return true;
}

export interface ScanResult {
  nfcUID: string;
  tag: TagEvent;
}

export async function scanNfcAndStartAdd(): Promise<ScanResult> {
  const supported = await ensureNfcIsSupported();
  if (!supported) {
    const error: ApiError = Object.assign(new Error('NFC not supported'), { code: 'NFCSCAN_DENIED' });
    throw error;
  }

  return new Promise(async (resolve, reject) => {
    let resolved = false;

    const timer = setTimeout(() => {
      if (!resolved) {
        const error: ApiError = Object.assign(new Error('NFC scan timed out'), {
          code: 'NFCSCAN_DENIED',
        });
        reject(error);
        NfcManager.cancelTechnologyRequest().catch(() => null);
      }
    }, NFC_TIMEOUT_MS);

    try {
      await NfcManager.requestTechnology(NfcTech.Ndef);
      const tag = await NfcManager.getTag();
      const nfcUID = (tag?.id ?? '').toUpperCase();
      if (!nfcUID) {
        throw Object.assign(new Error('Unable to read NFC UID'), { code: 'NFCSCAN_DENIED' });
      }
      resolved = true;
      clearTimeout(timer);
      resolve({ nfcUID, tag: tag as TagEvent });
    } catch (error) {
      clearTimeout(timer);
      const apiError: ApiError = Object.assign(
        error instanceof Error ? error : new Error('NFC scan failed'),
        { code: 'NFCSCAN_DENIED' },
      );
      reject(apiError);
    } finally {
      if (Platform.OS !== 'android') {
        NfcManager.cancelTechnologyRequest().catch(() => null);
      }
    }
  });
}
