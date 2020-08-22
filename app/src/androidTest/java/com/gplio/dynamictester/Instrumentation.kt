package com.gplio.dynamictester

import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.UiObjectNotFoundException

const val RETRIES = 3

/**
 * Finds the first view that matches [idOrText] in its id, text or content description.
 */
fun findFirstViewWithIdOrTextOrDesc(
    device: UiDevice,
    packageName: String,
    idOrText: String
): UiObject2? {
    val viewsWithIds = device.findObjects(By.res(packageName, idOrText))
    if (viewsWithIds.isNotEmpty()) return viewsWithIds.firstOrNull()

    val viewsWithText = device.findObjects(By.text(idOrText))
    if (viewsWithIds.isNotEmpty()) return viewsWithText.firstOrNull()

    return device.findObjects(By.desc(idOrText)).firstOrNull()
}

fun setText(
    text: String,
    viewObjects: MutableList<UiObject2>
) {
    runCatching {
        viewObjects.forEach { setText(text, it) }
    }
}

fun setText(text: String, viewObject: UiObject2) {
    for (i in 0..RETRIES) {
        try {
            viewObject.text = text
            return
        } catch (e: UiObjectNotFoundException) {
            log("Couldn't find view object")
        }
    }
}

@Suppress("unused")
private fun buildViewId(packageName: String, viewId: String): String {
    return "$packageName:id/$viewId"
}