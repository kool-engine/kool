package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.platform.Lwjgl3Context

actual fun OffscreenPassCubeImpl(offscreenPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
    val ctx = KoolSystem.requireContext() as Lwjgl3Context
    return ctx.renderBackend.createOffscreenPassCube(offscreenPass)
}
