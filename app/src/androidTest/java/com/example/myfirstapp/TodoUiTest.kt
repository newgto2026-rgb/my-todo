package com.example.myfirstapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfirstapp.app.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TodoUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun mainScreen_showsCoreUi() {
        composeTestRule.onNodeWithText("TODO").assertIsDisplayed()
        tabNode("all").assertIsSelected()
        tabNode("today").assertIsNotSelected()
        tabNode("completed").assertIsNotSelected()
        tabNode("calendar").assertIsNotSelected()
    }

    @Test
    fun addTaskFlow_opensBottomSheet_andAddsItem() {
        val title = "UI Test ${System.currentTimeMillis()}"

        composeTestRule.onNodeWithTag("add_fab").performClick()
        composeTestRule.onNodeWithText("New Task").assertIsDisplayed()

        composeTestRule.onNodeWithTag("task_title_input").performTextInput(title)
        composeTestRule.onNodeWithTag("save_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText(title).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(title).assertIsDisplayed()
    }

    @Test
    fun backPress_onTabRoutes_movesToPreviousVisitedTab_withoutLoop() {
        tabNode("today").performClick()
        tabNode("today").assertIsSelected()

        tabNode("completed").performClick()
        tabNode("completed").assertIsSelected()

        pressBack()
        composeTestRule.waitForIdle()
        tabNode("today").assertIsSelected()
        tabNode("completed").assertIsNotSelected()

        pressBack()
        composeTestRule.waitForIdle()
        tabNode("all").assertIsSelected()
        tabNode("today").assertIsNotSelected()
    }

    @Test
    fun backPress_whenBottomSheetOpen_closesBottomSheetFirst() {
        composeTestRule.onNodeWithTag("add_fab").performClick()
        composeTestRule.onNodeWithText("New Task").assertIsDisplayed()

        pressBack()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.onAllNodesWithText("New Task").fetchSemanticsNodes().isEmpty()
        }
        composeTestRule.onAllNodesWithText("New Task").assertCountEquals(0)
        tabNode("all").assertIsSelected()
    }

    @Test
    fun backPress_onFirstTab_finishesActivity() {
        tabNode("all").assertIsSelected()

        pressBackUnconditionally()
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED
        }
    }

    private fun tabNode(name: String) =
        composeTestRule.onNodeWithTag("app_tab_$name", useUnmergedTree = true)
}
