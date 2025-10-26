package de.fabmax.kool.util

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ui2.UiScope
import de.fabmax.kool.modules.ui2.mutableStateOf

object L10n {

    var defaultLanguage = "en"
    val selectedLanguageState = mutableStateOf(KoolSystem.systemLanguage)

    private val localizations = mutableMapOf<String, LocalizedStrings>()
    private val warningKeys = mutableSetOf<String>()

    val availableLanguages: List<LocalizedStrings>
        get() = localizations.values.toList()

    fun hasLanguage(language: String): Boolean {
        if (language in localizations) {
            return true
        }
        return language.substringBefore('-') in localizations
    }

    fun localizedString(key: String, language: String): String {
        val str = getStrings(language).strings[key]
        if (str != null) {
            return str
        }
        if (language != defaultLanguage && warningKeys.add("$language-$key")) {
            logW { "Missing translation for $language: \"$key\"" }
        }
        return getStrings(defaultLanguage).strings[key] ?: key
    }

    private fun getStrings(language: String): LocalizedStrings {
        var strings = localizations[language]
        if (strings != null) {
            return strings
        }
        if ('-' in language) {
            // use 'en' in case 'en-US' or similar
            strings = localizations[language.substringBefore('-')]
        }
        if (strings != null) {
            return strings
        }
        if (warningKeys.add(language)) {
            logE { "No localization for language: \"$language\", falling back to default language \"$defaultLanguage\"" }
        }
        return checkNotNull(localizations[defaultLanguage]) {
            "No string values for default language \"$defaultLanguage\" registered"
        }
    }

    fun registerLanguage(language: String, languageName: String, block: MutableMap<String, String>.() -> Unit) {
        val strings = buildMap(block)
        localizations[language] = LocalizedStrings(language, languageName, strings)
    }

    class LocalizedStrings(val languageKey: String, val languageName: String, val strings: Map<String, String>)

    data class Language(val languageKey: String, val languageName: String)
}

fun String.l(language: String = L10n.selectedLanguageState.value): String = L10n.localizedString(this, language)

context(ui: UiScope)
val String.l: String get() {
    with(ui) {
        return L10n.localizedString(this@l, L10n.selectedLanguageState.use())
    }
}
