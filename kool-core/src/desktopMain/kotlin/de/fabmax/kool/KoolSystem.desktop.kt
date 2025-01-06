package de.fabmax.kool

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    val osName = System.getProperty("os.name", "unknown")
    return KoolSystem.PlatformProperties(Platform.Desktop(osName))
}