package de.fabmax.kool.demo

import de.fabmax.kool.KeyValueStore
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.Sizes
import de.fabmax.kool.util.logD

/**
 * Object containing all demo related global settings.
 */
object Settings {

    val defaultUiSizes = mapOf(
        "Small" to UiSizeSetting("Small", Sizes.small),
        "Medium" to UiSizeSetting("Medium", Sizes.medium),
        "Large" to UiSizeSetting("Large", Sizes.large),
    )
    val defaultUiSize = UiSizeSetting("Large", Sizes.large)

    private val settings = mutableListOf<MutableStateSettings<*>>()

    val isFullscreen = MutableStateSettings("koolDemo.isFullscreen", false) { it.toBoolean() }
    val showHiddenDemos = MutableStateSettings("koolDemo.showHiddenDemos", false) { it.toBoolean() }
    val showDebugOverlay = MutableStateSettings("koolDemo.showDebugOverlay", true) { it.toBoolean() }
    val showMenuOnStartup = MutableStateSettings("koolDemo.showMenuOnStartup", true) { it.toBoolean() }

    val uiSize = MutableStateSettings("koolDemo.uiSize", defaultUiSize) {
        defaultUiSizes[it] ?: defaultUiSize
    }

    val selectedDemo = MutableStateSettings("koolDemo.selectedDemo", Demos.defaultDemo) { it }

    fun loadSettings() {
        settings.forEach { it.load() }
    }

    class MutableStateSettings<T>(val key: String, initValue: T, val parser: (String) -> T)
        : MutableStateValue<T>(initValue)
    {
        init {
            settings += this
            onChange {
                KeyValueStore.storeString(key, "$it")
                logD { "Stored $key: $it" }
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