package com.gplio.dynamictester

const val CMD_KEY = "cmd"
const val ARG_PACKAGE = "package"
const val ARG_TEXT = "text"
const val ARG_ID = "id"

enum class Command(val command: String) {
    SET_TEXT_IN_SELECTED_VIEW("set_text_in_selected_view"),
    SET_TEXT_IN_VIEW("set_text_in_view"),
    TAP_VIEW("tap_view"),
    ROTATE_RIGHT("rotate_right"),
    ROTATE_LEFT("rotate_left"),
    ROTATE_NATURAL("rotate_natural")
}

const val MISSING_ARG_PACKAGE_MESSAGE =
    "$ARG_PACKAGE argument must be provided (package name of the application under test)"
const val MISSING_ARG_ID_MESSAGE = "$ARG_ID argument must be provided (view id or text of the view)"

const val EXAMPLE_COMMAND_START =
    "adb shell am instrument -w -r -e debug false -e class 'com.gplio.dynamictester.DynTest' "
const val EXAMPLE_COMMAND_END =
    " com.gplio.dynamictester.test/androidx.test.runner.AndroidJUnitRunner"

fun getSupportedCommandsHelp(): String {
    val sb = StringBuilder()
    val cmdArgument = "-e $CMD_KEY"
    val argText = """-e $ARG_TEXT "Text""""
    val argPackage = """-e $ARG_PACKAGE "your.package.name""""
    val argId = """-e $ARG_ID "TheIdOrTextOfTheView" """

    Command.values().forEach {
        when (it) {
            Command.SET_TEXT_IN_SELECTED_VIEW -> sb.append("$EXAMPLE_COMMAND_START $cmdArgument ${it.command} $argText $EXAMPLE_COMMAND_END\n")
            Command.SET_TEXT_IN_VIEW -> sb.append("$EXAMPLE_COMMAND_START $cmdArgument ${it.command} $argPackage $argId $argText $EXAMPLE_COMMAND_END\n")
            Command.TAP_VIEW -> sb.append("$EXAMPLE_COMMAND_START $cmdArgument ${it.command} $argPackage $argId $argText $EXAMPLE_COMMAND_END\n")
        }
    }
    return sb.toString()
}