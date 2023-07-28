package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.Pointer
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.Viewport
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.tan

/**
 * @author fabmax
 */
abstract class Camera(name: String = "camera") : Node(name) {

    /**
     * Camera position within the camera [transform] frame.
     */
    val position = MutableVec3f(0f, 0f, 1f)

    /**
     * Camera look-at position within the camera [transform] frame. Camera look direction is determined by
     * norm(lookAt - position).
     */
    val lookAt = MutableVec3f(Vec3f.ZERO)

    /**
     * Camera up direction within the camera [transform] frame. Usually, the default value of [Vec3f.Y_AXIS] should
     * work fine. But, the up-direction must not be collinear to the look direction, i.e. a different up direction
     * is needed if the camera looks exactly up- or downwards.
     */
    val up = MutableVec3f(Vec3f.Y_AXIS)

    var aspectRatio = 1.0f
    var useViewportAspectRatio = true

    val globalPos: Vec3f get() = globalPosMut
    val globalLookAt: Vec3f get() = globalLookAtMut
    val globalUp: Vec3f get() = globalUpMut
    val globalRight: Vec3f get() = globalRightMut
    val globalLookDir: Vec3f get() = globalLookDirMut
    var globalRange = 0f
        protected set

    var clipNear = 0.1f
    var clipFar = 100f

    protected val globalPosMut = MutableVec3f()
    protected val globalLookAtMut = MutableVec3f()
    protected val globalUpMut = MutableVec3f()
    protected val globalRightMut = MutableVec3f()
    protected val globalLookDirMut = MutableVec3f()

    val proj = Mat4d()
    val view = Mat4d()

    private val lazyInvProj = LazyMat4d { proj.invert(it) }
    val invProj: Mat4d get() = lazyInvProj.get()

    private val lazyInvView = LazyMat4d { view.invert(it) }
    val invView: Mat4d get() = lazyInvView.get()

    private val lazyViewProj = LazyMat4d { proj.mul(view, it) }
    val viewProj: Mat4d get() = lazyViewProj.get()

    private val lazyInvViewProj = LazyMat4d { viewProj.invert(it) }
    val invViewProj: Mat4d get() = lazyInvViewProj.get()

    var projCorrectionMode = ProjCorrectionMode.ONSCREEN

    private val projCorrected = Mat4d()

    // we need a bunch of temporary vectors, keep them as members (#perfmatters)
    private val tmpVec3 = MutableVec3f()
    private val tmpVec4 = MutableVec4f()

    val onCameraUpdated = mutableListOf<(KoolContext) -> Unit>()

    fun setupCamera(position: Vec3f? = null, up: Vec3f? = null, lookAt: Vec3f? = null) {
        position?.let { this.position.set(it) }
        up?.let { this.up.set(up) }
        lookAt?.let { this.lookAt.set(it) }
    }

    fun setClipRange(near: Float, far: Float) {
        clipNear = near
        clipFar = far
    }

    open fun updateCamera(renderPass: RenderPass, ctx: KoolContext) {
        if (useViewportAspectRatio) {
            aspectRatio = renderPass.viewport.aspectRatio
        }

        updateViewMatrix(renderPass, ctx)
        updateProjectionMatrix(renderPass, ctx)

        if (projCorrectionMode == ProjCorrectionMode.ONSCREEN) {
            ctx.projCorrectionMatrixScreen.mul(proj, projCorrected)
            proj.set(projCorrected)
        } else if (projCorrectionMode == ProjCorrectionMode.OFFSCREEN) {
            ctx.projCorrectionMatrixOffscreen.mul(proj, projCorrected)
            proj.set(projCorrected)
        }

        lazyInvProj.isDirty = true
        lazyViewProj.isDirty = true
        lazyInvViewProj.isDirty = true

        if (onCameraUpdated.isNotEmpty()) {
            for (i in onCameraUpdated.indices) {
                onCameraUpdated[i](ctx)
            }
        }
    }

    protected open fun updateViewMatrix(renderPass: RenderPass, ctx: KoolContext) {
        toGlobalCoords(globalPosMut.set(position))
        toGlobalCoords(globalLookAtMut.set(lookAt))
        toGlobalCoords(globalUpMut.set(up), 0f).norm()

        globalLookDirMut.set(globalLookAtMut).subtract(globalPosMut)
        globalRange = globalLookDirMut.length()
        globalLookDirMut.scale(1f / globalRange)

        globalLookDirMut.cross(globalUpMut, globalRightMut).norm()
        globalRightMut.cross(globalLookDirMut, globalUpMut).norm()

        view.setLookAt(globalPosMut, globalLookAtMut, globalUpMut)
        lazyInvView.isDirty = true
    }

    protected abstract fun updateProjectionMatrix(renderPass: RenderPass, ctx: KoolContext)

    fun computePickRay(pickRay: Ray, ptr: Pointer, viewport: Viewport, ctx: KoolContext): Boolean {
        return ptr.isValid && computePickRay(pickRay, ptr.x.toFloat(), ptr.y.toFloat(), viewport, ctx)
    }

    fun computePickRay(pickRay: Ray, screenX: Float, screenY: Float, viewport: Viewport, ctx: KoolContext): Boolean {
        var valid = unProjectScreen(tmpVec3.set(screenX, screenY, 0f), viewport, ctx, pickRay.origin)
        valid = valid && unProjectScreen(tmpVec3.set(screenX, screenY, 1f), viewport, ctx, pickRay.direction)

        if (valid) {
            pickRay.direction.subtract(pickRay.origin)
            pickRay.direction.norm()
        }

        return valid
    }

    fun initRayTes(rayTest: RayTest, ptr: Pointer, viewport: Viewport, ctx: KoolContext): Boolean {
        return ptr.isValid && initRayTes(rayTest, ptr.x.toFloat(), ptr.y.toFloat(), viewport, ctx)
    }

    fun initRayTes(rayTest: RayTest, screenX: Float, screenY: Float, viewport: Viewport, ctx: KoolContext): Boolean {
        rayTest.clear()
        return computePickRay(rayTest.ray, screenX, screenY, viewport, ctx)
    }

    abstract fun computeFrustumPlane(z: Float, result: FrustumPlane)

    /**
     * Tests if the node is inside the view frustum of this camera. For performance reasons the node's bounding sphere
     * is used instead of the bounding box. [Node.globalCenter] and [Node.globalRadius] properties of the tested node
     * must be valid.
     */
    open fun isInFrustum(node: Node): Boolean = isInFrustum(node.globalCenter, node.globalRadius)

    abstract fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean

    fun project(world: Vec3f, result: MutableVec3f): Boolean {
        tmpVec4.set(world.x, world.y, world.z, 1f)
        viewProj.transform(tmpVec4)
        if (tmpVec4.w.isFuzzyZero()) {
            result.set(Vec3f.ZERO)
            return false
        }
        result.set(tmpVec4.x, tmpVec4.y, tmpVec4.z).scale(1f / tmpVec4.w)
        return result.x in -1f..1f && result.y in -1f..1f && result.z in -1f..1f
    }

    fun project(world: Vec3f, result: MutableVec4f): MutableVec4f =
        viewProj.transform(result.set(world.x, world.y, world.z, 1f))

    fun projectViewport(world: Vec3f, viewport: Viewport, result: MutableVec3f): Boolean {
        val projectOk = project(world, result)
        result.x = (1f + result.x) * 0.5f * viewport.width
        result.y = (1f - (1f + result.y) * 0.5f) * viewport.height
        result.z = (1f + result.z) * 0.5f
        return projectOk
    }

    fun projectScreen(world: Vec3f, viewport: Viewport, ctx: KoolContext, result: MutableVec3f): Boolean {
        val projectOk = projectViewport(world, viewport, result)
        result.x += viewport.x
        result.y += ctx.windowHeight - viewport.y - viewport.height
        return projectOk
    }

    fun unProjectScreen(screen: Vec3f, viewport: Viewport, ctx: KoolContext, result: MutableVec3f): Boolean {
        val x = screen.x - viewport.x
        val y = (ctx.windowHeight - screen.y) - viewport.y

        tmpVec4.set(2f * x / viewport.width - 1f, 2f * y / viewport.height - 1f, 2f * screen.z - 1f, 1f)
        invViewProj.transform(tmpVec4)
        val s = 1f / tmpVec4.w
        result.set(tmpVec4.x * s, tmpVec4.y * s, tmpVec4.z * s)
        return true
    }

    enum class ProjCorrectionMode {
        NONE,
        ONSCREEN,
        OFFSCREEN
    }
}

open class OrthographicCamera(name: String = "orthographicCam") : Camera(name) {
    var left = -10.0f
    var right = 10.0f
    var bottom = -10.0f
    var top = 10.0f

    var isClipToViewport = false
    var isKeepAspectRatio = true

    private val tmpNodeCenter = MutableVec3f()

    fun setCentered(height: Float, near: Float, far: Float) {
        top = height * 0.5f
        bottom = -top
        right = aspectRatio * top
        left = -right
        this.clipNear = near
        this.clipFar = far
    }

    override fun updateCamera(renderPass: RenderPass, ctx: KoolContext) {
        if (isClipToViewport) {
            left = 0f
            right = renderPass.viewport.width.toFloat()
            bottom = 0f
            top = renderPass.viewport.height.toFloat()

        } else if (isKeepAspectRatio) {
            val h = top - bottom
            val w = renderPass.viewport.aspectRatio * h
            val xCenter = left + (right - left) * 0.5f
            left = xCenter - w * 0.5f
            right = xCenter + w * 0.5f
        }
        super.updateCamera(renderPass, ctx)
    }

    override fun updateProjectionMatrix(renderPass: RenderPass, ctx: KoolContext) {
        if (left != right && bottom != top && clipNear != clipFar) {
            proj.setOrthographic(left, right, bottom, top, clipNear, clipFar)
        }
    }

    override fun computeFrustumPlane(z: Float, result: FrustumPlane) {
        invView.transform(result.upperLeft.set(left, top, -z))
        invView.transform(result.upperRight.set(right, top, -z))
        invView.transform(result.lowerLeft.set(left, bottom, -z))
        invView.transform(result.lowerRight.set(right, bottom, -z))
    }

    /**
     * Tests whether a bounding sphere intersects this camera's view frustum.
     * Similar approach as for perspective camera, but even simpler because no perspective is involved.
     */
    override fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean {
        tmpNodeCenter.set(globalCenter)
        tmpNodeCenter.subtract(globalPos)

        val x = tmpNodeCenter.dot(globalRight)
        if (x > right + globalRadius || x < left - globalRadius) {
            // node's bounding sphere is either left or right of frustum
            return false
        }

        val y = tmpNodeCenter.dot(globalUp)
        if (y > top + globalRadius || y < bottom - globalRadius) {
            // node's bounding sphere is either above or below frustum
            return false
        }

        val z = tmpNodeCenter.dot(globalLookDir)
        if (z > clipFar + globalRadius || z < clipNear - globalRadius) {
            // node's bounding sphere is either in front of near or behind far plane
            return false
        }
        return true
    }
}

open class PerspectiveCamera(name: String = "perspectiveCam") : Camera(name) {
    var fovY = 60.0f
    var fovX = 0f
        private set

    private var sphereFacX = 1f
    private var sphereFacY = 1f
    private var tangX = 1f
    private var tangY = 1f

    private val tmpNodeCenter = MutableVec3f()

    override fun updateProjectionMatrix(renderPass: RenderPass, ctx: KoolContext) {
        proj.setPerspective(fovY, aspectRatio, clipNear, clipFar)

        // compute intermediate values needed for view frustum culling
        val angY = fovY.toRad() / 2f
        sphereFacY = 1f / cos(angY)
        tangY = tan(angY)

        val angX = atan(tangY * aspectRatio)
        sphereFacX = 1f / cos(angX)
        tangX = tan(angX)
        fovX = (angX * 2).toDeg()
    }

    override fun computeFrustumPlane(z: Float, result: FrustumPlane) {
        val x = z * tangX
        val y = z * tangY

        invView.transform(result.upperLeft.set(-x, y, -z))
        invView.transform(result.upperRight.set(x, y, -z))
        invView.transform(result.lowerLeft.set(-x, -y, -z))
        invView.transform(result.lowerRight.set(x, -y, -z))
    }

    /**
     * Tests whether a bounding sphere intersects this camera's view frustum.
     * Implements the radar approach from http://www.lighthouse3d.com/tutorials/view-frustum-culling/
     */
    override fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean {
        tmpNodeCenter.set(globalCenter)
        tmpNodeCenter.subtract(globalPos)

        var z = tmpNodeCenter.dot(globalLookDir)
        if (z > clipFar + globalRadius || z < clipNear - globalRadius) {
            // node's bounding sphere is either in front of near or behind far plane
            return false
        }

        val y = tmpNodeCenter.dot(globalUp)
        var d = globalRadius * sphereFacY
        z *= tangY
        if (y > z + d || y < -z - d) {
            // node's bounding sphere is either above or below view frustum
            return false
        }

        val x = tmpNodeCenter.dot(globalRight)
        d = globalRadius * sphereFacX
        z *= aspectRatio
        if (x > z + d || x < -z - d) {
            // node's bounding sphere is either left or right of view frustum
            return false
        }

        return true
    }
}

open class PerspectiveProxyCam(var sceneCam: PerspectiveCamera) : PerspectiveCamera() {
    init {
        useViewportAspectRatio = false
        projCorrectionMode = ProjCorrectionMode.OFFSCREEN
    }

    open fun sync(renderPass: RenderPass, ctx: KoolContext) {
        sceneCam.updateCamera(renderPass, ctx)

        position.set(sceneCam.globalPos)
        lookAt.set(sceneCam.globalLookAt)
        up.set(sceneCam.globalUp)

        aspectRatio = sceneCam.aspectRatio
        fovY = sceneCam.fovY
        clipNear = sceneCam.clipNear
        clipFar = sceneCam.clipFar
    }
}

class FrustumPlane {
    val upperLeft = MutableVec3f()
    val upperRight = MutableVec3f()
    val lowerLeft = MutableVec3f()
    val lowerRight = MutableVec3f()

    fun set(other: FrustumPlane) {
        upperLeft.set(other.upperLeft)
        upperRight.set(other.upperRight)
        lowerLeft.set(other.lowerLeft)
        lowerRight.set(other.lowerRight)
    }
}