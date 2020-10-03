 package com.gplio.dynamictester

import android.app.UiAutomation
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test that logs accessibility events to logcat.
 *
 * You could also create a specific application that implements a proper AccessibilityService
 * but you don't have go into the settings and enable the accessibility service explicitly.
 * In addition you not only can use [AccessibilityNodeInfo] to perform actions over the views but also use uiautomator methods.
 *
 * If [WRITE_TEXT_EXAMPLE_ENABLED] it will also write "Hello!" in every text field that is focused or tapped.
 *
 * Start with:
 * adb shell am instrument -w -r    -e debug false -e class 'com.gplio.dynamictester.AccessibilityLoggerTest' com.gplio.dynamictester.test/androidx.test.runner.AndroidJUnitRunner
 *
 */
const val WRITE_TEXT_EXAMPLE_ENABLED = false

@RunWith(AndroidJUnit4::class)
class AccessibilityLoggerTest {
    private lateinit var device: UiDevice
    private lateinit var uiAutomation: UiAutomation
    private lateinit var context: Context
    private lateinit var arguments: Bundle

    private val quitLock = Object() // HUH

    @Before
    fun prepareTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiAutomation = InstrumentationRegistry.getInstrumentation().uiAutomation
        context = InstrumentationRegistry.getInstrumentation().targetContext
        arguments = InstrumentationRegistry.getArguments()
    }

    @Test
    fun runAccessibilityService() {
        log("Listening to accessibility events")

        uiAutomation.setOnAccessibilityEventListener { event ->
            log("onAccessibilityEvent: $event")

            // For example, if you wanted to write "Hello!" in every edittext view that is focused or clicked:
            if (WRITE_TEXT_EXAMPLE_ENABLED) {
                if (event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED || event.eventType == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                    // insertText inserts the text through AccessibilityNodeInfo but at this point you could use uiautomator methods.
                    insertText(event.source, "Hello!")
                }
            }
        }

        // Wait forever. I don't think this can be interrupted directly by the user (?) but it should be kiled as soon the test suite exits.
        synchronized(quitLock) {
            try {
                quitLock.wait()
            } catch (e: InterruptedException) {
                log("Interrupted: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Inserts [text] in [node] using accessibility APIs.
     */
    private fun insertText(node: AccessibilityNodeInfo, text: String) {
        val arguments = Bundle()
        arguments.putCharSequence(
            AccessibilityNodeInfo
                .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text
        )
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
    }
}
