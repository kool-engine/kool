package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.platform.Lwjgl3Context

actual fun OffscreenPass2dImpl(offscreenPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
    val ctx = KoolSystem.requireContext() as Lwjgl3Context
    return ctx.renderBackend.createOffscreenPass2d(offscreenPass)
}
