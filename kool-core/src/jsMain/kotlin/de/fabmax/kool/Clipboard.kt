package de.fabmax.kool

actual object Clipboard {
    actual fun copyToClipboard(string: String) { }

    actual fun getStringFromClipboard(): String? {
        TODO("Not yet implemented")
    }
}