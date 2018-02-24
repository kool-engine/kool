package de.fabmax.kool.util

import de.fabmax.kool.RenderContext
import de.fabmax.kool.RenderPass
import de.fabmax.kool.Texture
import de.fabmax.kool.gl.Framebuffer
import de.fabmax.kool.gl.GL_DEPTH_BUFFER_BIT
import de.fabmax.kool.gl.glClear
import de.fabmax.kool.glCapabilities
import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec4f
import de.fabmax.kool.scene.FrustumPlane
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.OrthographicCamera

class ShadowMap(val near: Float = 0f, val far: Float = 1f, val texSize: Int = 1024) {
    private val depthCam = OrthographicCamera()
    private val depthMvpMat = Mat4f()
    private val depthView = Mat4f()

    private val nearPlane = FrustumPlane()
    private val farPlane = FrustumPlane()
    private val bounds = BoundingBox()
    private val tmpVec4 = MutableVec4f()

    private var fbo: Framebuffer = Framebuffer(texSize, texSize).withDepth()

    val depthMvp = createFloat32Buffer(16)
    val depthTexture: Texture?
        get() = fbo.depthAttachment
    var clipSpaceFarZ = 0f
        private set

    fun renderShadowMap(nodeToRender: Node, ctx: RenderContext) {
        if (!glCapabilities.depthTextures) {
            // depth textures are not supported on current platform, there's no point in going ahead
            return
        }

        val scene = nodeToRender.scene ?: return
        val camera = scene.camera

        depthCam.position.set(scene.light.direction)
        depthCam.lookAt.set(0f, 0f, 0f)
        //scene.light.direction.cross(scene.camera.globalLookDir, depthCam.up).norm()
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
        BIAS_MATRIX.mul(ctx.mvpState.mvpMatrix, depthMvpMat).toBuffer(depthMvp)

        val prevRenderPass = ctx.renderPass
        ctx.renderPass = RenderPass.SHADOW
        scene.camera = depthCam

        nodeToRender.render(ctx)

        scene.camera = camera
        ctx.renderPass = prevRenderPass
        ctx.mvpState.popMatrices()
        ctx.mvpState.update(ctx)
        fbo.unbind(ctx)
    }

    fun dispose(ctx: RenderContext) {
        fbo.delete(ctx)
    }

    companion object {
        private val BIAS_MATRIX = Mat4f()

        init {
            BIAS_MATRIX.setIdentity()
            BIAS_MATRIX.translate(0.5f, 0.5f, 0.5f)
            BIAS_MATRIX.scale(0.5f, 0.5f, 0.5f)
        }
    }
}

class CascadedShadowMap(val subMaps: Array<ShadowMap>) {
    val shadowMvp = createFloat32Buffer(16 * subMaps.size)

    fun renderShadowMap(nodeToRender: Node, ctx: RenderContext) {
        for (i in subMaps.indices) {
            subMaps[i].renderShadowMap(nodeToRender, ctx)
            shadowMvp.put(subMaps[i].depthMvp)
        }
        shadowMvp.flip()
    }

    fun dispose(ctx: RenderContext) {
        for (i in subMaps.indices) {
            subMaps[i].dispose(ctx)
        }
    }

    companion object {
        fun defaultCascadedShadowMap3(): CascadedShadowMap {
            val subMaps = arrayOf(
                    ShadowMap(0f, 0.1f),
                    ShadowMap(0.1f, 0.25f),
                    ShadowMap(0.25f, 1f)
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

private fun BoundingBox.setPlanes(near: FrustumPlane, far: FrustumPlane) {
    batchUpdate = true
    clear()
    add(near.upperLeft)
    add(near.upperRight)
    add(near.lowerLeft)
    add(near.lowerRight)
    add(far.upperLeft)
    add(far.upperRight)
    add(far.lowerLeft)
    add(far.lowerRight)
    batchUpdate = false
}
