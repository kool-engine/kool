package de.fabmax.kool.demo

import de.fabmax.kool.platform.AndroidRenderContext
import de.fabmax.kool.platform.KoolActivity

class MainActivity : KoolActivity() {
    override fun onKoolContextCreated(ctx: AndroidRenderContext) {
        super.onKoolContextCreated(ctx)
        Demo(ctx, "modelDemo")
    }
}
