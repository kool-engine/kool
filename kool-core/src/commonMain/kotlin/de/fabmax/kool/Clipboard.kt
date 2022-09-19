package de.fabmax.kool

expect object Clipboard {

    fun copyToClipboard(string: String)

    fun getStringFromClipboard(receiver: (String?) -> Unit)

}