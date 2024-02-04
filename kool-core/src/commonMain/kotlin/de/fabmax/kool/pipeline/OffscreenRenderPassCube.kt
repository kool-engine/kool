package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.launchDelayed

open class OffscreenRenderPassCube(drawNode: Node, config: Config) : OffscreenRenderPass(config) {

    override val views: List<View> = ViewDirection.entries.map {
        val cam = PerspectiveCamera()
        cam.fovY = 90f.deg
        cam.clipNear = 0.01f
        cam.clipFar = 10f
        cam.setupCamera(position = Vec3f.ZERO, up = it.up, lookAt = it.lookAt)
        View(it.toString(), drawNode, cam, arrayOf(Color.BLACK)).apply { setFullscreenViewport() }
    }

    var drawNode: Node = drawNode
        set(value) {
            field = value
            views.forEach { it.drawNode = value }
        }

    val depthTexture = makeDepthAttachmentTex()
    val colorTextures = makeColorAttachmentTexs()
    val colorTexture: TextureCube?
        get() = if (colorTextures.isNotEmpty()) colorTextures[0] else null

    val copyTargetsColor = mutableListOf<TextureCube>()

    override val isReverseDepth: Boolean
        get() = impl.isReverseDepth

    internal val impl = KoolSystem.requireContext().backend.createOffscreenPassCube(this)

    init {
        if (config.depthAttachment is TextureDepthAttachment) {
            throw RuntimeException("CubeMapDepthTexture not yet implemented")
        }
        if (numColorTextures > 1) {
            throw RuntimeException("CubeMap multiple render targets not yet implemented")
        }
    }

    // fixme: replace copyColor() by releaseKeepingTextures()
    fun copyColor(): TextureCube {
        val tex = TextureCube(getColorTexProps(), "$name-${copyTargetsColor.size}")
        copyTargetsColor += tex
        return tex
    }

    override fun release() {
        super.release()
        impl.release()

        launchDelayed(3) {
            if (depthAttachment !is TextureDepthAttachment || depthAttachment.attachment.providedTexture == null) {
                depthTexture?.dispose()
            }
            colorTextures.forEachIndexed { i, tex ->
                if (colorAttachment !is TextureColorAttachment || colorAttachment.attachments[i].providedTexture == null) {
                    tex.dispose()
                }
            }
        }
    }

    override fun applySize(width: Int, height: Int, depth: Int) {
        check(depth == 1) { "OffscreenRenderPassCube depth must be == 1" }
        super.applySize(width, height, depth)
        impl.applySize(width, height)
    }

    private fun makeColorAttachmentTexs(): List<TextureCube> {
        return if (colorAttachment is TextureColorAttachment) {
            colorAttachment.attachments.mapIndexed { i, texCfg ->
                if (texCfg.isProvided) {
                    texCfg.providedTexture!! as TextureCube
                } else {
                    val name = "${name}_color[$i]"
                    val props = texCfg.getTextureProps(mipLevels > 1)
                    TextureCube(props, name)
                }
            }
        } else {
            emptyList()
        }
    }

    private fun makeDepthAttachmentTex(): TextureCube? {
        return if (depthAttachment is TextureDepthAttachment) {
            val cfg = depthAttachment.attachment
            if (cfg.isProvided) {
                cfg.providedTexture!! as TextureCube
            } else {
                val name = "${name}_depth"
                val props = cfg.getTextureProps(mipLevels > 1)
                TextureCube(props, name)
            }
        } else {
            null
        }
    }

    enum class ViewDirection(val index: Int, val lookAt: Vec3f, val up: Vec3f) {
        POS_X(0, Vec3f( 1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        NEG_X(1, Vec3f(-1f,  0f,  0f), Vec3f.NEG_Y_AXIS),
        POS_Y(2, Vec3f( 0f,  1f,  0f), Vec3f.Z_AXIS),
        NEG_Y(3, Vec3f( 0f, -1f,  0f), Vec3f.NEG_Z_AXIS),
        POS_Z(4, Vec3f( 0f,  0f,  1f), Vec3f.NEG_Y_AXIS),
        NEG_Z(5, Vec3f( 0f,  0f, -1f), Vec3f.NEG_Y_AXIS),
    }
}

interface OffscreenPassCubeImpl {
    val isReverseDepth: Boolean

    fun applySize(width: Int, height: Int)

    fun release()
}