package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext

class DeferredPasses(val materialPass: MaterialPass, val lightingPass: PbrLightingPass) {

    val onActivate = mutableListOf<() -> Unit>()
    val onDeactivate = mutableListOf<() -> Unit>()

    var isEnabled: Boolean
        get() = materialPass.isEnabled && lightingPass.isEnabled
        set(value) {
            materialPass.isEnabled = value
            lightingPass.isEnabled = value

            if (value) {
                for (i in onActivate.indices) {
                    onActivate[i]()
                }
            } else {
                for (i in onDeactivate.indices) {
                    onDeactivate[i]()
                }
            }
        }

    fun checkSize(viewportW: Int, viewportH: Int, ctx: KoolContext) {
        if (viewportW > 0 && viewportH > 0 && (viewportW != materialPass.width || viewportH != materialPass.height)) {
            materialPass.resize(viewportW, viewportH, ctx)
            lightingPass.resize(viewportW, viewportH, ctx)
        }
    }
}