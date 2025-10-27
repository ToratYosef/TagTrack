package com.tagtrack.app.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tagtrack.app.core.common.AppState
import com.tagtrack.app.feature.additem.AddItemScreen
import com.tagtrack.app.feature.additem.AddItemViewModel
import com.tagtrack.app.feature.auth.AuthScreen
import com.tagtrack.app.feature.auth.AuthViewModel
import com.tagtrack.app.feature.detail.ItemDetailScreen
import com.tagtrack.app.feature.detail.ItemDetailViewModel
import com.tagtrack.app.feature.home.HomeScreen
import com.tagtrack.app.feature.home.HomeViewModel
import com.tagtrack.app.feature.onboarding.OnboardingScreen
import com.tagtrack.app.feature.scan.ScanScreen
import com.tagtrack.app.feature.scan.ScanViewModel
import com.tagtrack.app.feature.settings.SettingsScreen
import com.tagtrack.app.feature.settings.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TagTrackNavHost(
    onToggleNfc: (Boolean) -> Unit,
    activity: ComponentActivity,
    appState: AppState,
) {
    val navController = rememberNavController()
    val startDestination = if (appState.isAuthenticated) {
        TagTrackDestinations.HOME
    } else {
        TagTrackDestinations.ONBOARDING
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(TagTrackDestinations.ONBOARDING) {
            OnboardingScreen(onContinue = {
                navController.navigate(TagTrackDestinations.AUTH) {
                    popUpTo(TagTrackDestinations.ONBOARDING) { inclusive = true }
                }
            })
        }
        composable(TagTrackDestinations.AUTH) {
            val viewModel: AuthViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            AuthScreen(
                state = state,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onSignIn = {
                    viewModel.signIn {
                        navController.navigate(TagTrackDestinations.HOME) {
                            popUpTo(TagTrackDestinations.AUTH) { inclusive = true }
                        }
                    }
                },
                onSignUp = {
                    viewModel.signUp {
                        navController.navigate(TagTrackDestinations.HOME) {
                            popUpTo(TagTrackDestinations.AUTH) { inclusive = true }
                        }
                    }
                },
                onMagicLink = viewModel::sendMagicLink
            )
        }
        composable(TagTrackDestinations.HOME) {
            val viewModel: HomeViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onQueryChanged = viewModel::onQueryChanged,
                onAddItem = { navController.navigate(TagTrackDestinations.ADD_ITEM) },
                onScan = { navController.navigate(TagTrackDestinations.SCAN) },
                onItemClick = { item ->
                    navController.navigate("${TagTrackDestinations.ITEM_DETAIL_BASE}/${item.id}")
                },
                isAddEnabled = appState.nfcSupported && appState.nfcEnabled,
                nfcSupported = appState.nfcSupported,
                nfcEnabled = appState.nfcEnabled
            )
        }
        composable(TagTrackDestinations.ADD_ITEM) {
            val viewModel: AddItemViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            AddItemScreen(
                state = state,
                hostActivity = activity,
                onStartScan = viewModel::startScan,
                onStateChanged = viewModel::updateState,
                onSave = {
                    viewModel.save { itemId ->
                        navController.navigate("${TagTrackDestinations.ITEM_DETAIL_BASE}/$itemId") {
                            popUpTo(TagTrackDestinations.ADD_ITEM) { inclusive = true }
                        }
                    }
                },
                onViewExisting = { itemId ->
                    viewModel.resetDuplicateError()
                    navController.navigate("${TagTrackDestinations.ITEM_DETAIL_BASE}/$itemId") {
                        popUpTo(TagTrackDestinations.ADD_ITEM) { inclusive = true }
                    }
                },
                onDuplicateDismiss = viewModel::resetDuplicateError
            )
        }
        composable(
            route = TagTrackDestinations.ITEM_DETAIL,
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) {
            val viewModel: ItemDetailViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ItemDetailScreen(
                state = state,
                onRecordWorn = viewModel::onRecordWorn,
                onEdit = { /* TODO: edit flow */ },
                onDelete = viewModel::onDelete,
                onDeleteConfirmed = {
                    viewModel.onDeleteConfirmed {
                        navController.popBackStack()
                    }
                },
                onDeleteDismissed = viewModel::onDeleteDismissed
            )
        }
        composable(TagTrackDestinations.SCAN) {
            val viewModel: ScanViewModel = hiltViewModel()
            val error by viewModel.error.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                viewModel.scannedItemId.collectLatest { itemId ->
                    navController.navigate("${TagTrackDestinations.ITEM_DETAIL_BASE}/$itemId") {
                        popUpTo(TagTrackDestinations.SCAN) { inclusive = true }
                    }
                }
            }
            ScanScreen(
                hostActivity = activity,
                onStartScan = viewModel::startScan,
                onErrorDismissed = viewModel::clearError,
                errorUid = error
            )
        }
        composable(TagTrackDestinations.SETTINGS) {
            val viewModel: SettingsViewModel = hiltViewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                state = state,
                onThemeChanged = viewModel::setTheme,
                onLanguageChanged = viewModel::setLanguage,
                onCloudBackupChanged = viewModel::setCloudBackup
            )
        }
    }
}
