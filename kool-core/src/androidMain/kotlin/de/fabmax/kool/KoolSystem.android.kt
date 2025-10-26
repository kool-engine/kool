package de.fabmax.kool

import java.util.Locale

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Android, Locale.getDefault().language)
}

actual val currentThreadName: String get() = Thread.currentThread().name