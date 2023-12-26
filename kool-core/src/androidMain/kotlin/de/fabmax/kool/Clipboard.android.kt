package de.fabmax.kool

import de.fabmax.kool.util.logE

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Clipboard {
    actual fun copyToClipboard(string: String) {
        logE { "Clipboard not yet supported on Android" }
    }

    actual fun getStringFromClipboard(receiver: (String?) -> Unit) {
        logE { "Clipboard not yet supported on Android" }
    }
}