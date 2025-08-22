package de.fabmax.kool

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Android)
}

actual val currentThreadName: String get() = Thread.currentThread().name