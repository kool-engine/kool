package de.fabmax.kool

import de.fabmax.kool.util.MsdfFontInfo

object KoolSystem {
    private var initConfig: KoolConfig? = null
    private var defaultContext: KoolContext? = null

    var isInitialized = false
        private set

    val isContextCreated: Boolean
        get() = defaultContext != null

    val config: KoolConfig
        get() = initConfig ?: throw IllegalStateException("KoolSetup is not yet initialized. Call initialize(config) before accessing KoolSetup.config")

    val isJavascript: Boolean
        get() = requireContext().isJavascript
    val isJvm: Boolean
        get() = requireContext().isJvm

    fun initialize(config: KoolConfig) {
        if (isInitialized && config != initConfig) {
            throw IllegalStateException("KoolSetup is already initialized")
        }
        initConfig = config
        isInitialized = true
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
}

interface KoolConfig {
    /**
     * Default asset loader used by [Assets] to load textures, models, etc.
     */
    val defaultAssetLoader: AssetLoader

    val defaultFont: MsdfFontInfo
}