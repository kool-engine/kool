@file:Suppress("REDUNDANT_CALL_OF_CONVERSION_METHOD")

package de.fabmax.kool

import kotlinx.browser.window

actual object Clipboard {
    actual fun copyToClipboard(string: String) {
        window.navigator.clipboard.writeText(string)
    }

    actual fun getStringFromClipboard(receiver: (String?) -> Unit) {
        window.navigator.clipboard.readText().then {
            receiver(it.toString())
            it
        }
    }
}
