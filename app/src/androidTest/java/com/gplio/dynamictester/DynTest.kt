package com.gplio.dynamictester

import android.content.Context
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Dynamic Instrumented test, which will execute on an Android device according to the provided arguments.
 * Examples:
 * adb shell am instrument -w -r -e debug false -e class 'com.gplio.dynamictester.DynTest'  -e cmd set_text_in_selected_view -e text "Text"  com.gplio.dynamictester.test/androidx.test.runner.AndroidJUnitRunner
 *
 * adb shell am instrument -w -r -e debug false -e class 'com.gplio.dynamictester.DynTest'  -e cmd set_text_in_view -e package "your.package.name" -e id "TheIdOrTextOfTheView"  -e text "Text"  com.gplio.dynamictester.test/androidx.test.runner.AndroidJUnitRunner
 *
 * adb shell am instrument -w -r -e debug false -e class 'com.gplio.dynamictester.DynTest'  -e cmd tap_view -e package "your.package.name" -e id "TheIdOrTextOfTheView"  -e text "Text"  com.gplio.dynamictester.test/androidx.test.runner.AndroidJUnitRunner
 *
 */
@RunWith(AndroidJUnit4::class)
class DynTest {
    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var arguments: Bundle

    private val validCommands = Command.values()

    @Before
    fun prepareTest() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = InstrumentationRegistry.getInstrumentation().targetContext
        arguments = InstrumentationRegistry.getArguments()
    }

    @Test
    fun runTestCommand() {
        val commandString = arguments.getString(CMD_KEY)
        assertNotNull(
            "You have to provide a command.\nExamples:\n${getSupportedCommandsHelp()}",
            commandString
        )

        val command = validCommands.find { it.command == commandString!! }
        assertNotNull(
            "Unknown command '$commandString' provided. Valid commands are: \n${validCommands
                .joinToString(separator = "\n") { it.command }}\n", command
        )

        when (command!!) {
            Command.SET_TEXT_IN_SELECTED_VIEW -> handleWriteTextInSelectedView(
                arguments.getString(ARG_TEXT)
            )
            Command.SET_TEXT_IN_VIEW -> handleWriteTextInView(
                arguments.getString(ARG_PACKAGE),
                arguments.getString(ARG_ID),
                arguments.getString(ARG_TEXT)
            )
            Command.TAP_VIEW -> handleTapView(
                arguments.getString(ARG_PACKAGE),
                arguments.getString(ARG_ID)
            )
        }
    }

    private fun handleWriteTextInSelectedView(textArgument: String?) {
        log("Running handleWriteTextInSelectedView")
        val text = textArgument ?: ""

        val focusedObjects = device.findObjects(By.focused(true))

        assertNotNull("Couldn't find focused views", focusedObjects)

        setText(text, focusedObjects)
    }

    private fun handleWriteTextInView(
        packageArgument: String?,
        idArgument: String?,
        textArgument: String?
    ) {
        log("Running handleWriteTextInView")

        assertNotNull(MISSING_ARG_PACKAGE_MESSAGE, packageArgument)
        assertNotNull(MISSING_ARG_ID_MESSAGE, packageArgument)

        val packageName = packageArgument!!
        val id = idArgument!!
        val text = textArgument ?: ""

        val view = findFirstViewWithIdOrTextOrDesc(device, packageName, id)

        if (view == null) {
            log("Couldn't find view with id, text nor content description. packageName: $packageName id: $id")
        } else {
            if (!view.isFocused) {
                log("Focusing on view")
                view.click()
            }
            setText(text, view)
        }
    }

    private fun handleTapView(packageArgument: String?, idArgument: String?) {
        log("Running handleTapView")

        assertNotNull(MISSING_ARG_PACKAGE_MESSAGE, packageArgument)
        assertNotNull(MISSING_ARG_ID_MESSAGE, packageArgument)

        val packageName = packageArgument!!
        val id = idArgument!!

        val view = findFirstViewWithIdOrTextOrDesc(device, packageName, id)
        if (view == null) {
            log("Couldn't find view with id, text nor content description. packageName: $packageName id: $id")
        } else {
            view.click()
        }
    }
}