package de.fabmax.kool

import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.MsdfFontInfo

object KoolSystem {
    private var initConfig: KoolConfig? = null
    private var defaultContext: KoolContext? = null
    private val properties: PlatformProperties = PlatformProperties()

    internal val onDestroyContext = mutableListOf<() -> Unit>()

    val platform: Platform
        get() = properties.platform

    var isInitialized = false
        private set

    val isContextCreated: Boolean
        get() = defaultContext != null

    val config: KoolConfig
        get() = initConfig ?: throw IllegalStateException("KoolSetup is not yet initialized. Call initialize(config) before accessing KoolSetup.config")

    fun initialize(config: KoolConfig) {
        if (isInitialized && config != initConfig) {
            throw IllegalStateException("KoolSetup is already initialized")
        }
        initConfig = config
        isInitialized = true
    }

    internal fun destroyContext() {
        initConfig = null
        defaultContext = null
        isInitialized = false
        BackendStats.onDestroy()
        onDestroyContext.forEach { it() }
    }

    internal fun onContextCreated(ctx: KoolContext) {
        defaultContext = ctx
    }

    fun requireContext(): KoolContext {
        return defaultContext ?: throw IllegalStateException("KoolContext was not yet created")
    }

    fun getContextOrNull(): KoolContext? {
        return defaultContext
    }

    data class PlatformProperties(val platform: Platform)
}

internal expect fun PlatformProperties(): KoolSystem.PlatformProperties

enum class Platform {
    JVM_DESKTOP,
    JVM_ANDROID,
    JAVASCRIPT
}

interface KoolConfig {
    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    val defaultAssetLoader: AssetLoader

    val defaultFont: MsdfFontInfo
}