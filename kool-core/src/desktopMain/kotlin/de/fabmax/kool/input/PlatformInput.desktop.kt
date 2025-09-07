package de.fabmax.kool.input

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm

internal actual fun PlatformInput(): PlatformInput = KoolSystem.configJvm.windowSubsystem.input
