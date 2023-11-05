package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPassCube(drawNode: Node, config: Config) : OffscreenRenderPass(drawNode, config) {

    override val views: List<View> = ViewDirection.entries.map {
        val cam = PerspectiveCamera()
        cam.fovY = 90f
        cam.clipNear = 0.01f
        cam.clipFar = 10f
        cam.setupCamera(position = Vec3f.ZERO, up = it.up, lookAt = it.lookAt)
        View(it.toString(), cam, arrayOf(Color.BLACK)).apply { setFullscreenViewport() }
    }

    val depthTexture = makeDepthAttachmentTex()
    val colorTextures = makeColorAttachmentTexs()
    val colorTexture: TextureCube?
        get() = if (colorTextures.isNotEmpty()) colorTextures[0] else null

    val copyTargetsColor = mutableListOf<TextureCube>()

    internal val impl = OffscreenPassCubeImpl(this)

    init {
        if (config.depthRenderTarget == RenderTarget.TEXTURE) {
            throw RuntimeException("CubeMapDepthTexture not yet implemented")
        }
        if (config.colorAttachments.size > 1) {
            throw RuntimeException("CubeMap multiple render targets not yet implemented")
        }
    }

    fun copyColor(): TextureCube {
        val tex = TextureCube(getColorTexProps(), "$name-${copyTargetsColor.size}")
        copyTargetsColor += tex
        return tex
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        impl.dispose(ctx)

        launchDelayed(3) {
            if (depthAttachment?.providedTexture == null) {
                depthTexture?.dispose()
            }
            colorTextures.forEachIndexed { i, tex ->
                if (colorAttachments[i].providedTexture == null) {
                    tex.dispose()
                }
            }
        }
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        super.applySize(width, height, ctx)
        impl.applySize(width, height, ctx)
    }

    private fun makeColorAttachmentTexs(): List<TextureCube> {
        return colorAttachments.mapIndexed { i, texCfg ->
            if (texCfg.isProvided) {
                texCfg.providedTexture as TextureCube
            } else {
                val name = "${name}_color[$i]"
                val props = texCfg.getTextureProps(mipLevels > 1)
                TextureCube(props, name)
            }
        }
    }

    private fun makeDepthAttachmentTex(): TextureCube? {
        return depthAttachment?.let { texCfg ->
            if (texCfg.isProvided) {
                texCfg.providedTexture as TextureCube
            } else {
                val name = "${name}_depth"
                val props = texCfg.getTextureProps(mipLevels > 1)
                TextureCube(props, name)
            }
        }
    }

    enum class ViewDirection(val index: Int, val lookAt: Vec3f, val up: Vec3f) {
        FRONT(0, Vec3f( 0f,  0f,  1f), Vec3f.NEG_Y_AXIS),
        BACK(1, Vec3f( 0f,  0f, -1f), Vec3f.NEG_Y_AXIS),
        LEFT(2, Vec3f(-1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        RIGHT(3, Vec3f( 1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        UP(4, Vec3f( 0f,  1f,  0f), Vec3f.Z_AXIS),
        DOWN(5, Vec3f( 0f, -1f,  0f), Vec3f.NEG_Z_AXIS),
    }
}

expect class OffscreenPassCubeImpl(offscreenPass: OffscreenRenderPassCube) {
    fun applySize(width: Int, height: Int, ctx: KoolContext)

    fun dispose(ctx: KoolContext)
}