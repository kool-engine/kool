package de.fabmax.kool

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object Clipboard {
    internal var impl: ClipboardImpl = SwingClipboardImpl

    actual fun copyToClipboard(string: String) = impl.copyToClipboard(string)

    actual fun getStringFromClipboard(receiver: (String?) -> Unit) = impl.getStringFromClipboard(receiver)
}

internal interface ClipboardImpl {
    fun copyToClipboard(string: String)
    fun getStringFromClipboard(receiver: (String?) -> Unit)
}

internal object SwingClipboardImpl : ClipboardImpl {
    override fun copyToClipboard(string: String) {
        val selection = StringSelection(string)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }

    override fun getStringFromClipboard(receiver: (String?) -> Unit) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val t = clipboard.getContents(null)
        val clipboardText = if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            t.getTransferData(DataFlavor.stringFlavor) as? String
        } else {
            null
        }
        receiver(clipboardText)
    }
}