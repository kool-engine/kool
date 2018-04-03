package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.RenderPass
import de.fabmax.kool.Texture
import de.fabmax.kool.gl.Framebuffer
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.gl.GL_FRONT
import de.fabmax.kool.gl.glClear
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.scene.FrustumPlane
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera

interface ShadowMap : Disposable {
    val numMaps: Int
    val shadowMvp: Float32Buffer

    fun renderShadowMap(nodeToRender: Node, ctx: KoolContext)

    fun getShadowMapSize(map: Int): Int
    fun getShadowMap(map: Int): Texture?
    fun getClipSpaceFarZ(map: Int): Float
}

class SimpleShadowMap(val near: Float = 0f, val far: Float = 1f, private val texSize: Int = defaultMapSize) : ShadowMap {
    override val numMaps = 1
    override val shadowMvp = createFloat32Buffer(16)

    private val depthCam = OrthographicCamera()
    private val depthMvpMat = Mat4f()
    private val depthView = Mat4f()

    private val nearPlane = FrustumPlane()
    private val farPlane = FrustumPlane()
    private val bounds = BoundingBox()
    private val tmpVec4 = MutableVec4f()

    private var fbo: Framebuffer = Framebuffer(texSize, texSize).withDepth()

    private var clipSpaceFarZ = 0f

    init {
        depthCam.isKeepAspectRatio = false
    }

    override fun renderShadowMap(nodeToRender: Node, ctx: KoolContext) {
        if (!ctx.glCapabilities.depthTextures) {
            // depth textures are not supported on current platform, there's no point in going ahead
            return
        }

        val scene = nodeToRender.scene ?: return
        val camera = scene.camera

        depthCam.position.set(scene.light.direction)
        depthCam.lookAt.set(0f, 0f, 0f)
        depthView.setLookAt(depthCam.position, depthCam.lookAt, depthCam.up)

        // compute bounding box of main camera's near and far frustum planes in light space
        camera.computeFrustumPlane(near, nearPlane)
        camera.computeFrustumPlane(far, farPlane)
        clipSpaceFarZ = camera.project(farPlane.upperLeft, tmpVec4).z

        depthView.transform(nearPlane)
        depthView.transform(farPlane)
        bounds.setPlanes(nearPlane, farPlane)

        // set depth camera bounds to computed bounding box
        depthCam.left = bounds.min.x
        depthCam.right = bounds.max.x
        depthCam.bottom = bounds.min.y
        depthCam.top = bounds.max.y
        depthCam.near = -bounds.max.z - 10
        depthCam.far = -bounds.min.z

        fbo.bind(ctx)

        glClear(GL_DEPTH_BUFFER_BIT)

        ctx.mvpState.pushMatrices()
        ctx.mvpState.projMatrix.setIdentity()
        ctx.mvpState.viewMatrix.setIdentity()
        ctx.mvpState.modelMatrix.setIdentity()

        depthCam.updateCamera(ctx)
        BIAS_MATRIX.mul(ctx.mvpState.mvpMatrix, depthMvpMat).toBuffer(shadowMvp)

        val prevRenderPass = ctx.renderPass
        ctx.renderPass = RenderPass.SHADOW
        scene.camera = depthCam

        ctx.pushAttributes()
        ctx.cullFace = GL_FRONT
        ctx.applyAttributes()

        nodeToRender.render(ctx)

        ctx.popAttributes()

        scene.camera = camera
        ctx.renderPass = prevRenderPass
        ctx.mvpState.popMatrices()
        ctx.mvpState.update(ctx)
        fbo.unbind(ctx)

        // force re-binding shaders
        ctx.shaderMgr.bindShader(null, ctx)
    }

    override fun dispose(ctx: KoolContext) {
        fbo.dispose(ctx)
    }

    override fun getShadowMapSize(map: Int) = texSize

    override fun getShadowMap(map: Int) = fbo.depthAttachment

    override fun getClipSpaceFarZ(map: Int) = clipSpaceFarZ

    companion object {
        private val BIAS_MATRIX = Mat4f()

        var defaultMapSize = 1024

        init {
            BIAS_MATRIX.setIdentity()
            BIAS_MATRIX.translate(0.5f, 0.5f, 0.5f)
            BIAS_MATRIX.scale(0.5f, 0.5f, 0.5f)
        }
    }
}

class CascadedShadowMap(private val subMaps: Array<SimpleShadowMap>) : ShadowMap {
    override val numMaps: Int
        get() = subMaps.size

    override val shadowMvp = createFloat32Buffer(16 * subMaps.size)

    override fun renderShadowMap(nodeToRender: Node, ctx: KoolContext) {
        for (i in subMaps.indices) {
            subMaps[i].renderShadowMap(nodeToRender, ctx)
            shadowMvp.put(subMaps[i].shadowMvp)
        }
        shadowMvp.flip()
    }

    override fun dispose(ctx: KoolContext) {
        for (i in subMaps.indices) {
            subMaps[i].dispose(ctx)
        }
    }

    override fun getShadowMapSize(map: Int) = subMaps[map].getShadowMapSize(0)

    override fun getShadowMap(map: Int): Texture? = subMaps[map].getShadowMap(0)

    override fun getClipSpaceFarZ(map: Int) = subMaps[map].getClipSpaceFarZ(0)

    companion object {
        fun defaultCascadedShadowMap3(): CascadedShadowMap {
            val subMaps = arrayOf(
                    SimpleShadowMap(0f, 0.1f),
                    SimpleShadowMap(0.1f, 0.3f),
                    SimpleShadowMap(0.3f, 1f)
            )
            return CascadedShadowMap(subMaps)
        }
    }
}

private fun Mat4f.transform(plane: FrustumPlane) {
    transform(plane.upperLeft)
    transform(plane.upperRight)
    transform(plane.lowerLeft)
    transform(plane.lowerRight)
}

private fun BoundingBox.setPlanes(near: FrustumPlane, far: FrustumPlane) = batchUpdate {
    clear()
    add(near.upperLeft)
    add(near.upperRight)
    add(near.lowerLeft)
    add(near.lowerRight)
    add(far.upperLeft)
    add(far.upperRight)
    add(far.lowerLeft)
    add(far.lowerRight)
}
