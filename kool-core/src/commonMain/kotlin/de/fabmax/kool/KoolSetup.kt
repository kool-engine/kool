package de.fabmax.kool

object KoolSetup {
    private var initConfig: KoolConfig? = null
    var isInitialized = false
        private set

    val config: KoolConfig
        get() = initConfig ?: throw IllegalStateException("KoolSetup is not yet initialized. Call initialize(config) before accessing KoolSetup.config")

    fun initialize(config: KoolConfig) {
        if (isInitialized && config != initConfig) {
            throw IllegalStateException("KoolSetup is already initialized")
        }
        initConfig = config
        isInitialized = true
    }
}

expect class KoolConfig {
    val assetPath: String
}