package de.fabmax.kool.demo

import de.fabmax.kool.AndroidRenderContext
import de.fabmax.kool.KoolActivity

class MainActivity : KoolActivity() {
    override fun onKoolContextCreated(ctx: AndroidRenderContext) {
        super.onKoolContextCreated(ctx)
        Demo(ctx)
    }
}
