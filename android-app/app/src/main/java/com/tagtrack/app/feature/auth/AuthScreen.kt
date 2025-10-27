package com.tagtrack.app.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tagtrack.app.R

@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onMagicLink: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = stringResource(id = R.string.auth_title))
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.auth_email)) },
            singleLine = true
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = stringResource(id = R.string.auth_password)) }
        )
        Button(onClick = onSignIn, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_sign_in))
        }
        Button(onClick = onSignUp, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.action_sign_up))
        }
        TextButton(onClick = onMagicLink) {
            Text(text = stringResource(id = R.string.action_send_magic_link))
        }
    }
}
