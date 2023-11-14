package de.fabmax.kool

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object Clipboard {

    fun copyToClipboard(string: String)

    fun getStringFromClipboard(receiver: (String?) -> Unit)

}