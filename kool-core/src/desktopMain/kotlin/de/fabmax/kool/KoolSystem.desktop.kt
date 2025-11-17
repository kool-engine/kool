package de.fabmax.kool

import java.util.*

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    val osName = System.getProperty("os.name", "unknown")
    return KoolSystem.PlatformProperties(Platform.Desktop(osName), Locale.getDefault().language)
}

actual val currentThreadName: String get() = Thread.currentThread().name