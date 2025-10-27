import { ApiErrorCode } from '@/types/item';

export function getErrorMessage(code: ApiErrorCode): string {
  switch (code) {
    case 'NFCSCAN_DENIED':
      return 'NFC scan was denied.';
    case 'NFCTAG_DUPLICATE':
      return 'This NFC tag is already linked to another item.';
    case 'ITEM_NOT_FOUND':
      return 'Item not found.';
    case 'PERMISSION_DENIED':
      return 'Permission denied.';
    case 'NETWORK_ERROR':
      return 'Network error.';
    default:
      return 'Unknown error.';
  }
}
