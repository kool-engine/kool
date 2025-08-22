package de.fabmax.kool

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Javascript)
}

actual val currentThreadName: String = "kool-main"