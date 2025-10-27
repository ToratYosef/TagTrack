package com.tagtrack.app.feature.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R

@Composable
fun OnboardingScreen(
    onContinue: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = stringResource(id = R.string.onboarding_title), style = MaterialTheme.typography.headlineLarge)
            Text(text = stringResource(id = R.string.onboarding_nfc_required), style = MaterialTheme.typography.bodyLarge)
            Text(text = stringResource(id = R.string.onboarding_privacy), style = MaterialTheme.typography.bodyLarge)
        }
        Button(onClick = onContinue) {
            Text(text = stringResource(id = R.string.action_continue))
        }
    }
}
