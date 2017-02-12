package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.MatrixMath
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */
class Camera(name: String = "camera") : Node(name) {

    val position = MutableVec3f(0f, 0f, 10f)
    val lookAt = MutableVec3f(Vec3f.Companion.ZERO)
    val up = MutableVec3f(Vec3f.Companion.Y_AXIS)

    var fovy = 60.0f
    var clipNear = 0.2f
    var clipFar = 200.0f
    var aspectRatio = 1.0f
        private set

    private val tmpPos = MutableVec3f()
    private val tmpLookAt = MutableVec3f()
    private val tmpUp = MutableVec3f()

    fun updateCamera(ctx: RenderContext) {
        aspectRatio = ctx.viewportWidth.toFloat() / ctx.viewportHeight.toFloat()

        updateViewMatrix(ctx)
        updateProjectionMatrix(ctx)
        ctx.mvpState.update(ctx)
    }

    fun updateViewMatrix(ctx: RenderContext) {
        toGlobalCoords(tmpPos.set(position))
        toGlobalCoords(tmpLookAt.set(lookAt))
        toGlobalCoords(tmpUp.set(up), 0f)
        ctx.mvpState.viewMatrix.setLookAt(tmpPos, tmpLookAt, tmpUp)
    }

    fun updateProjectionMatrix(ctx: RenderContext) {
        ctx.mvpState.projMatrix.setPerspective(fovy, aspectRatio, clipNear, clipFar)
    }

}
