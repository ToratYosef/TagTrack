package com.tagtrack.app.core.nfc

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class NfcReaderManager @Inject constructor() {

    fun enableReaderMode(activity: ComponentActivity): Flow<String> = callbackFlow {
        val callback = NfcAdapter.ReaderCallback { tag ->
            val uid = tag.id?.joinToString(separator = "") { byte ->
                "%02X".format(byte)
            }
            if (uid != null) {
                trySend(uid)
            }
        }
        val adapter = NfcAdapter.getDefaultAdapter(activity)
        if (adapter == null) {
            close(IllegalStateException("NFC not supported"))
            return@callbackFlow
        }
        val flags = NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_B
        adapter.enableReaderMode(activity, callback, flags, Bundle())
        awaitClose { adapter.disableReaderMode(activity) }
    }
}
