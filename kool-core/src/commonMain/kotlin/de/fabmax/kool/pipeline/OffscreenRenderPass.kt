package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Camera
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

abstract class OffscreenRenderPass(drawNode: Node, texWidth: Int, texHeight: Int, val mipLevels: Int) : RenderPass(drawNode) {
    var targetMipLevel = -1
    var isEnabled = true

    var texWidth = texWidth
        protected set
    var texHeight = texHeight
        protected set

    override var camera: Camera = PerspectiveCamera().apply { projCorrectionMode = Camera.ProjCorrectionMode.OFFSCREEN }

    init {
        setMipViewport(0)
    }

    fun mipWidth(mipLevel: Int): Int {
        return if (mipLevel <= 0) {
            texWidth
        } else {
            texWidth shr mipLevel
        }
    }

    fun mipHeight(mipLevel: Int): Int {
        return if (mipLevel <= 0) {
            texHeight
        } else {
            texHeight shr mipLevel
        }
    }

    fun setMipViewport(mipLevel: Int) {
        viewport.set(0, 0, mipWidth(mipLevel), mipHeight(mipLevel))
    }

    open fun resize(width: Int, height: Int, ctx: KoolContext) {
        texWidth = width
        texHeight = height
        setMipViewport(0)
    }
}

