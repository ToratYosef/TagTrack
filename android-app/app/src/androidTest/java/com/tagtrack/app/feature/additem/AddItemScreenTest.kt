package com.tagtrack.app.feature.additem

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.tagtrack.app.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class AddItemScreenTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun waitingForScan_messageVisible() {
        composeRule.setContent {
            AddItemScreen(
                state = AddItemUiState(),
                hostActivity = null,
                onStartScan = {},
                onStateChanged = {},
                onSave = {},
                onViewExisting = { _ -> },
                onDuplicateDismiss = {}
            )
        }
        composeRule.onNodeWithText(composeRule.activity.getString(R.string.add_item_waiting_for_scan))
            .assertIsDisplayed()
    }
}
