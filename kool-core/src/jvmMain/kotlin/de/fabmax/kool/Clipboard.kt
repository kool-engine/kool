package de.fabmax.kool

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection

actual object Clipboard {
    actual fun copyToClipboard(string: String) {
        val selection = StringSelection(string)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, selection)
    }

    actual fun getStringFromClipboard(): String? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val t = clipboard.getContents(null)
        return if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            t.getTransferData(DataFlavor.stringFlavor) as? String
        } else {
            null
        }
    }

}