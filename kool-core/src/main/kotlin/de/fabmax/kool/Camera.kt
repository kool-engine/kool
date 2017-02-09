package de.fabmax.kool

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.MatrixMath
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */
class Camera {

    val position = MutableVec3f(0f, 0f, 10f)
    val lookAt = MutableVec3f(Vec3f.ZERO)
    val up = MutableVec3f(Vec3f.Y_AXIS)

    var fovy = 60.0f
    var clipNear = 0.1f
    var clipFar = 100.0f
    var aspectRatio = 1.0f
        private set

    fun updateCamera(ctx: RenderContext) {
        aspectRatio = ctx.viewportWidth.toFloat() / ctx.viewportHeight.toFloat()

        updateViewMatrix(ctx)
        updateProjectionMatrix(ctx)
        ctx.mvpState.update(ctx)
    }

    fun updateViewMatrix(ctx: RenderContext) {
        ctx.mvpState.viewMatrix.setLookAt(position, lookAt, up)
    }

    fun updateProjectionMatrix(ctx: RenderContext) {
        ctx.mvpState.projMatrix.setPerspective(fovy, aspectRatio, clipNear, clipFar)
    }

}
