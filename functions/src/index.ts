import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const enforceUniqueNfcUid = functions.firestore
  .document('users/{userId}/items/{itemId}')
  .onCreate(async (snap, context) => {
    const { userId, itemId } = context.params;
    const newData = snap.data();
    const nfcUID: string = (newData.nfcUID ?? '').toUpperCase();

    if (!nfcUID) {
      await snap.ref.delete();
      throw new functions.https.HttpsError('invalid-argument', 'Missing NFC UID');
    }

    const firestore = admin.firestore();
    const dupes = await firestore
      .collection(`users/${userId}/items`)
      .where('nfcUID', '==', nfcUID)
      .get();

    const conflict = dupes.docs.find((doc) => doc.id !== itemId);
    if (conflict) {
      await snap.ref.delete();
      await firestore.collection(`users/${userId}/logs`).add({
        action: 'duplicate_nfc_uid_blocked',
        data: { nfcUID, itemId, conflictId: conflict.id },
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      });
      throw new functions.https.HttpsError('already-exists', 'NFCTAG_DUPLICATE');
    }

    await firestore.collection(`users/${userId}/logs`).add({
      action: 'item_created',
      data: { nfcUID, itemId },
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });
    return true;
  });
