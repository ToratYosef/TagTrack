import { initializeApp } from 'firebase/app';
import {
  getAuth,
  connectAuthEmulator,
  type User,
} from 'firebase/auth';
import {
  collection,
  deleteDoc,
  doc,
  getDoc,
  getDocs,
  getFirestore,
  orderBy,
  query,
  serverTimestamp,
  setDoc,
  updateDoc,
  where,
} from 'firebase/firestore';
import { getStorage } from 'firebase/storage';
import Constants from 'expo-constants';
import { Platform } from 'react-native';
import { Item } from '@/types/item';

const firebaseConfig = {
  apiKey: Constants.expoConfig?.extra?.firebase?.apiKey || process.env.EXPO_PUBLIC_FIREBASE_API_KEY || 'demo',
  authDomain:
    Constants.expoConfig?.extra?.firebase?.authDomain || process.env.EXPO_PUBLIC_FIREBASE_AUTH_DOMAIN || 'demo.firebaseapp.com',
  projectId:
    Constants.expoConfig?.extra?.firebase?.projectId || process.env.EXPO_PUBLIC_FIREBASE_PROJECT_ID || 'demo',
  storageBucket:
    Constants.expoConfig?.extra?.firebase?.storageBucket || process.env.EXPO_PUBLIC_FIREBASE_STORAGE_BUCKET || 'demo.appspot.com',
  messagingSenderId:
    Constants.expoConfig?.extra?.firebase?.messagingSenderId ||
    process.env.EXPO_PUBLIC_FIREBASE_MESSAGING_SENDER_ID || '000000000000',
  appId: Constants.expoConfig?.extra?.firebase?.appId || process.env.EXPO_PUBLIC_FIREBASE_APP_ID || '1:000000000000:web:demo',
  measurementId:
    Constants.expoConfig?.extra?.firebase?.measurementId ||
    process.env.EXPO_PUBLIC_FIREBASE_MEASUREMENT_ID || 'G-DEMO0000',
};

const app = initializeApp(firebaseConfig);

export const auth = getAuth(app);
export const firestore = getFirestore(app);
export const storage = getStorage(app);

if (__DEV__ && Platform.OS !== 'web') {
  // Optional emulator connection in dev; credentials assumed to exist locally
  try {
    connectAuthEmulator(auth, 'http://localhost:9099', { disableWarnings: true });
  } catch (error) {
    // eslint-disable-next-line no-console
    console.warn('Auth emulator connection failed', error);
  }
}

export interface CreateItemPayload {
  name: string;
  type: Item['type'];
  color?: string;
  brand?: string;
  size?: string;
  tags: string[];
  notes?: string;
  photoURL?: string | null;
  nfcUID: string;
}

export const userItemsCollection = (user: User) => collection(firestore, `users/${user.uid}/items`);

export async function getItemByNfc(user: User, nfcUID: string): Promise<Item | null> {
  const q = query(userItemsCollection(user), where('nfcUID', '==', nfcUID), orderBy('createdAt', 'desc'));
  const snapshot = await getDocs(q);
  if (snapshot.empty) {
    return null;
  }
  const docSnap = snapshot.docs[0];
  return { id: docSnap.id, ...(docSnap.data() as Item) };
}

export async function listItems(user: User): Promise<Item[]> {
  const snapshot = await getDocs(query(userItemsCollection(user), orderBy('createdAt', 'desc')));
  return snapshot.docs.map((docSnap) => ({ id: docSnap.id, ...(docSnap.data() as Item) }));
}

export async function createItem(user: User, payload: CreateItemPayload): Promise<Item> {
  const normalizedUid = payload.nfcUID.toUpperCase();
  const col = userItemsCollection(user);
  const docRef = doc(col);
  await setDoc(docRef, {
    ...payload,
    nfcUID: normalizedUid,
    createdAt: serverTimestamp(),
  });
  const created = await getDoc(docRef);
  return { id: docRef.id, ...(created.data() as Item) };
}

export async function deleteItem(user: User, itemId: string) {
  const docRef = doc(firestore, `users/${user.uid}/items/${itemId}`);
  await deleteDoc(docRef);
}

export async function recordWorn(user: User, itemId: string) {
  const docRef = doc(firestore, `users/${user.uid}/items/${itemId}`);
  await updateDoc(docRef, { lastWorn: serverTimestamp() });
}

export async function exportItems(user: User, format: 'CSV' | 'JSON'): Promise<string> {
  const items = await listItems(user);
  if (format === 'JSON') {
    const json = JSON.stringify(items, null, 2);
    return json;
  }
  const header = 'name,type,color,brand,size,tags,notes,nfcUID,createdAt,lastWorn\n';
  const rows = items
    .map((item) =>
      [
        item.name,
        item.type,
        item.color ?? '',
        item.brand ?? '',
        item.size ?? '',
        item.tags.join('|'),
        item.notes ?? '',
        item.nfcUID,
        item.createdAt,
        item.lastWorn ?? '',
      ]
        .map((value) => `"${String(value).replace(/"/g, '""')}"`)
        .join(','),
    )
    .join('\n');
  return header + rows;
}

export async function writeAuditLog(user: User, action: string, data: Record<string, unknown>) {
  const logsCollection = collection(firestore, `users/${user.uid}/logs`);
  const logDoc = doc(logsCollection);
  await setDoc(logDoc, {
    action,
    data,
    createdAt: serverTimestamp(),
  });
}
