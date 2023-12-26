package de.fabmax.kool

import kotlin.js.Promise

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Clipboard {
    actual fun copyToClipboard(string: String) {
        js("navigator.clipboard.writeText(string)")
    }

    actual fun getStringFromClipboard(receiver: (String?) -> Unit) {
        val promise = js("navigator.clipboard.readText()") as Promise<String>
        promise.then { receiver(it) }
    }
}