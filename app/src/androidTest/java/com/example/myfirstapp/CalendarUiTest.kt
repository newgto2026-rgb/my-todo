package com.example.myfirstapp

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myfirstapp.app.MainActivity
import com.example.myfirstapp.core.database.AppDatabase
import com.example.myfirstapp.core.domain.usecase.AddTodoUseCase
import com.example.myfirstapp.core.model.ReminderRepeatType
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CalendarUiTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var addTodoUseCase: AddTodoUseCase

    @Inject
    lateinit var appDatabase: AppDatabase

    private var todayTodoId: Long = -1L
    private val today: LocalDate = LocalDate.now()

    @Before
    fun setup() {
        hiltRule.inject()

        runBlocking {
            appDatabase.clearAllTables()

            todayTodoId = addTodoUseCase(
                title = "Calendar UI - today",
                dueDate = today,
                categoryId = null,
                reminderAtEpochMillis = null,
                isReminderEnabled = false,
                reminderRepeatType = ReminderRepeatType.NONE,
                reminderRepeatDaysMask = 0
            ).getOrThrow()

            addTodoUseCase(
                title = "Calendar UI - today second",
                dueDate = today,
                categoryId = null,
                reminderAtEpochMillis = null,
                isReminderEnabled = false,
                reminderRepeatType = ReminderRepeatType.NONE,
                reminderRepeatDaysMask = 0
            ).getOrThrow()
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun calendarMonthNavigation_changesMonthLabel() {
        openCalendarTab()
        val before = monthLabelText()

        composeTestRule.onNodeWithTag("calendar_next_month").performClick()
        composeTestRule.waitForIdle()
        val afterNext = monthLabelText()

        composeTestRule.onNodeWithTag("calendar_prev_month").performClick()
        composeTestRule.waitForIdle()
        val afterBack = monthLabelText()

        assertNotEquals(before, afterNext)
        assertEquals(before, afterBack)
    }

    @Test
    fun dateTap_withTodos_showsBottomSheetList() {
        openCalendarTab()

        composeTestRule.onNodeWithTag("calendar_day_$today").performClick()

        composeTestRule.onNodeWithTag("calendar_day_todo_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calendar UI - today").assertIsDisplayed()
        composeTestRule.onNodeWithText("Calendar UI - today second").assertIsDisplayed()
    }

    @Test
    fun dateTap_withoutTodos_showsEmptyState() {
        openCalendarTab()

        val emptyDate = findEmptyDateInCurrentMonth()
        composeTestRule.onNodeWithTag("calendar_day_$emptyDate").performClick()

        composeTestRule.onNodeWithTag("calendar_day_todo_sheet").assertIsDisplayed()
        composeTestRule.onNodeWithTag("calendar_day_todo_sheet_empty").assertIsDisplayed()
    }

    @Test
    fun todoClickInBottomSheet_opensTodoEditSheet() {
        openCalendarTab()

        composeTestRule.onNodeWithTag("calendar_day_$today").performClick()
        composeTestRule.onNodeWithTag("calendar_day_todo_item_$todayTodoId").performClick()

        composeTestRule.onNodeWithTag("task_title_input").assertIsDisplayed().assertTextContains("Calendar UI - today")
    }

    private fun openCalendarTab() {
        composeTestRule.onNodeWithTag("app_tab_calendar", useUnmergedTree = true).performClick()
        composeTestRule.onNodeWithTag("calendar_month_label").assertIsDisplayed()
    }

    private fun monthLabelText(): String {
        val semanticsNode = composeTestRule.onNodeWithTag("calendar_month_label").fetchSemanticsNode()
        val textList = semanticsNode.config.getOrElse(SemanticsProperties.Text) { emptyList() }
        return textList.joinToString(separator = "") { it.text }
    }

    private fun findEmptyDateInCurrentMonth(): LocalDate {
        val dayRange = 1..today.lengthOfMonth()
        return dayRange
            .asSequence()
            .map { today.withDayOfMonth(it) }
            .first { it != today }
    }
}
