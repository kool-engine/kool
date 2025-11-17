package de.fabmax.kool.demo

import de.fabmax.kool.KeyValueStore
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.util.L10n
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logT

/**
 * Object containing all demo related global settings.
 */
object Settings {

    val defaultUiSizes = mapOf(
        "Small" to UiSizeSetting("Small", Sizes.small),
        "Medium" to UiSizeSetting("Medium", Sizes.medium),
        "Large" to UiSizeSetting("Large", Sizes.large),
    )
    val defaultUiSize = UiSizeSetting("Medium", Sizes.medium)

    private val settings = mutableListOf<MutableStateSettings<*>>()

    val language = MutableStateSettings("koolDemo.language", KoolSystem.systemLanguage.substringBefore('-')) { it }

    val isFullscreen = MutableStateSettings("koolDemo.isFullscreen", false) { it.toBoolean() }
    val showHiddenDemos = MutableStateSettings("koolDemo.showHiddenDemos", false) { it.toBoolean() }
    val showDebugOverlay = MutableStateSettings("koolDemo.showDebugOverlay", true) { it.toBoolean() }
    val showMenuOnStartup = MutableStateSettings("koolDemo.showMenuOnStartup", true) { it.toBoolean() }
    val renderScale = MutableStateSettings("koolDemo.renderScale", 100) { it.toInt() }

    val uiSize = MutableStateSettings("koolDemo.uiSize", defaultUiSize) {
        defaultUiSizes[it] ?: defaultUiSize
    }

    val selectedDemo = MutableStateSettings("koolDemo.selectedDemo", Demos.defaultDemo) { it }

    fun loadSettings() {
        settings.forEach { it.load() }
        if (L10n.hasLanguage(language.value)) {
            logI { "Using language translations for \"${language.value}\"" }
            L10n.selectedLanguageState.set(language.value)
        } else {
            logI { "No translations for current language \"${language.value}\", using default language \"${L10n.defaultLanguage}\"" }
            language.set(L10n.defaultLanguage)
            L10n.selectedLanguageState.set(L10n.defaultLanguage)
        }
    }

    class MutableStateSettings<T>(val key: String, initValue: T, val parser: (String) -> T)
        : MutableStateValue<T>(initValue)
    {
        init {
            settings += this
            onChange { _, new ->
                KeyValueStore.storeString(key, "$new")
                logT { "Stored $key: $new" }
            }
        }

        fun load() {
            KeyValueStore.loadString(key)?.let { set(parser(it)) }
        }
    }

    data class UiSizeSetting(val name: String, val sizes: Sizes) {
        override fun toString(): String = name
    }
}