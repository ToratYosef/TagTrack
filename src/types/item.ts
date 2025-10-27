export type ClothingType = 'Shirt' | 'Pants' | 'Shoes' | 'Accessory' | 'Outerwear' | 'Other';

export interface Item {
  id: string;
  name: string;
  type: ClothingType;
  color?: string;
  brand?: string;
  size?: string;
  tags: string[];
  notes?: string;
  photoURL?: string | null;
  nfcUID: string;
  createdAt: number;
  lastWorn?: number;
  deleted?: boolean;
}

export interface UserSettings {
  cloudBackup: boolean;
  analytics: boolean;
  lastBackupAt?: number;
}

export interface UserProfile {
  id: string;
  displayName?: string;
  email: string;
  settings: UserSettings;
}

export type ApiErrorCode =
  | 'NFCSCAN_DENIED'
  | 'NFCTAG_DUPLICATE'
  | 'ITEM_NOT_FOUND'
  | 'PERMISSION_DENIED'
  | 'NETWORK_ERROR';

export interface ApiError extends Error {
  code: ApiErrorCode;
}
