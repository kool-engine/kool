package de.fabmax.kool

import de.fabmax.kool.platform.navigator

internal actual fun PlatformProperties(): KoolSystem.PlatformProperties {
    return KoolSystem.PlatformProperties(Platform.Javascript, navigator.language)
}

actual val currentThreadName: String = "kool-main"