package de.fabmax.kool

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Android, Locale.getDefault().language)
}

actual val currentThreadName: String get() = Thread.currentThread().name