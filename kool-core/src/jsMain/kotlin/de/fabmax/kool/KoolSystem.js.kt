package de.fabmax.kool

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Javascript)
}