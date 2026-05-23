package de.fabmax.kool

import java.util.*

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    val osName = System.getProperty("os.name", "unknown")
    val isWayland = System.getenv("XDG_SESSION_TYPE") == "wayland"
    return KoolSystem.PlatformProperties(Platform.Desktop(osName, isWayland), Locale.getDefault().language)
}

actual val currentThreadName: String get() = Thread.currentThread().name