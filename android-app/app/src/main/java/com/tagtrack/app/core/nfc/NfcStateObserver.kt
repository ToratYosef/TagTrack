package com.tagtrack.app.core.nfc

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart

@Singleton
class NfcStateObserver @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val adapter: NfcAdapter? = context.getSystemService()

    val nfcState: Flow<NfcState> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == NfcAdapter.ACTION_ADAPTER_STATE_CHANGED) {
                    val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
                    trySend(
                        NfcState(
                            isSupported = adapter != null,
                            isEnabled = state == NfcAdapter.STATE_ON
                        )
                    )
                }
            }
        }
        val filter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
        context.registerReceiver(receiver, filter)
        awaitClose { context.unregisterReceiver(receiver) }
    }.onStart {
        val supported = adapter != null
        emit(NfcState(isSupported = supported, isEnabled = adapter?.isEnabled == true))
    }
}

data class NfcState(
    val isSupported: Boolean = true,
    val isEnabled: Boolean = false,
)
