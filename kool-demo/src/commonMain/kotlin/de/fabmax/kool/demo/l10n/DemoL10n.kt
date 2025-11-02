package de.fabmax.kool.demo.l10n

import de.fabmax.kool.util.L10n

object DemoL10n {

    fun registerStrings() {
        // no need to register any en strings as long as meaningful string keys are used
        L10n.registerLanguage("en", "English") { }

        L10n.registerLanguage("de", "Deutsch") { deTranslation() }
        L10n.registerLanguage("ru", "Русский") { ruTranslation() }
    }
}