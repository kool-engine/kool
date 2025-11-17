package de.fabmax.kool

import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.MsdfFontInfo

object KoolSystem {
    private var initConfig: KoolConfig? = null
    private var defaultContext: KoolContext? = null
    private val properties: PlatformProperties = PlatformProperties()

    val platform: Platform
        get() = properties.platform
    val systemLanguage: String
        get() = properties.systemLanguage

    var isInitialized = false
        private set

    val isContextCreated: Boolean
        get() = defaultContext != null

    val config: KoolConfig
        get() = initConfig ?: throw IllegalStateException("KoolSetup is not yet initialized. Call initialize(config) before accessing KoolSetup.config")

    val features: BackendFeatures
        get() = requireContext().backend.features

    fun initialize(config: KoolConfig) {
        if (isInitialized && config != initConfig) {
            throw IllegalStateException("KoolSetup is already initialized")
        }
        initConfig = config
        isInitialized = true
    }

    internal fun onContextCreated(ctx: KoolContext) {
        defaultContext = ctx

        ctx.onShutdown += {
            initConfig = null
            isInitialized = false
            BackendStats.onDestroy()
        }
    }

    fun requireContext(): KoolContext {
        return defaultContext ?: throw IllegalStateException("KoolContext was not yet created")
    }

    fun getContextOrNull(): KoolContext? {
        return defaultContext
    }

    data class PlatformProperties(val platform: Platform, val systemLanguage: String)
}

expect val currentThreadName: String

internal expect fun PlatformProperties(): KoolSystem.PlatformProperties

sealed interface Platform {
    data class Desktop(val os: String) : Platform {
        override val isWindows: Boolean get() = "windows" in os.lowercase()
        override val isLinux: Boolean get() = "linux" in os.lowercase()
        override val isMacOs: Boolean get() {
            val osLowercase = os.lowercase()
            return "mac os" in osLowercase || "darwin" in osLowercase || "osx" in osLowercase
        }
    }
    data object Android : Platform
    data object Javascript : Platform

    val isWindows: Boolean get() = false
    val isLinux: Boolean get() = false
    val isMacOs: Boolean get() = false
    val isAndroid: Boolean get() = this == Android
    val isJavascript: Boolean get() = this == Javascript
}

interface KoolConfig {
    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    val defaultAssetLoader: AssetLoader

    val defaultFont: MsdfFontInfo

    val numSamples: Int
}