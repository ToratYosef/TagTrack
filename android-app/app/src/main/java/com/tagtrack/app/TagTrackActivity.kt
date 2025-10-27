package com.tagtrack.app

import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tagtrack.app.core.designsystem.theme.TagTrackTheme
import com.tagtrack.app.navigation.TagTrackNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TagTrackActivity : ComponentActivity() {

    private val viewModel: TagTrackActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val languageState = viewModel.language.collectAsStateWithLifecycle()
            val appState = viewModel.appState.collectAsStateWithLifecycle()
            TagTrackTheme(language = languageState.value, theme = appState.value.theme) {
                Surface {
                    TagTrackNavHost(
                        onToggleNfc = ::handleNfcToggle,
                        activity = this,
                        appState = appState.value
                    )
                }
            }
        }
    }

    private fun handleNfcToggle(enable: Boolean) {
        val adapter = NfcAdapter.getDefaultAdapter(this) ?: return
        if (enable && !adapter.isEnabled) {
            viewModel.onNfcSettingsRequested()
        }
    }
}
