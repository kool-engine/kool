package de.fabmax.kool.physics.util

import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.HitResult
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.scene.MatrixTransformF
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Transform
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.Time
import kotlin.math.*

class CharacterTrackingCamRig(enableCursorLock: Boolean = true) :
    Node("PointerLockCamRig") {

    var isCursorLocked: Boolean
        get() = PointerInput.cursorMode == CursorMode.LOCKED
        set(value) {
            if (value) {
                PointerInput.cursorMode = CursorMode.LOCKED
            } else {
                PointerInput.cursorMode = CursorMode.NORMAL
            }
        }

    var sensitivity = 1f
    var zoom = 4f
    var minZoom = 0.5f
    var maxZoom = 25f
    var isZoomEnabled = true

    private var actualZoom = zoom

    var trackedPose: Transform = TrsTransformF()
    val pivotPoint = MutableVec3f()

    val lookDirection = MutableVec3f(Vec3f.NEG_Z_AXIS)
    var frontAngle = 0f.deg

    var zoomModifier: (Float) -> Float = { it }

    private val poseOrigin = MutableVec3f()
    private var lookPhi = 0f
    private var lookTheta = PI.toFloat() / 2f

    private val hitSweepGeometry by lazy { BoxGeometry(Vec3f(1f, 1f, 0.1f)) }
    private val hitSweepGeometryPose = MutableMat4f()

    init {
        isCursorLocked = enableCursorLock
        transform = MatrixTransformF()

        onUpdate {
            if (isCursorLocked) {
                handlePointerInput()
            }
            updateTracking(Time.deltaT)
        }
    }

    fun setupCollisionAwareCamZoom(world: PhysicsWorld) {
        val testDir = MutableVec3f()
        val hitResult = HitResult()

        zoomModifier = { desiredZoom ->
            var zoom = desiredZoom
            transform.transform(testDir.set(0f, 0f, 1f).norm(), 0f)
            hitSweepGeometryPose.set(transform.matrixF).translate(pivotPoint).translate(0f, 0f, 1f)
            if (world.sweepTest(hitSweepGeometry, hitSweepGeometryPose, testDir, desiredZoom, hitResult)) {
                zoom = max(minZoom, hitResult.hitDistance - 1f)
            }
            zoom
        }
    }

    fun applyLookDirection() {
        lookPhi = atan2(lookDirection.z, lookDirection.x)
        lookTheta = acos(lookDirection.y)
        frontAngle = lookPhi.rad + 90f.deg
    }

    private fun handlePointerInput() {
        applyLookDirection()

        val ptr = PointerInput.primaryPointer
        val div = 500f / sensitivity * ptr.windowScale

        lookPhi -= ptr.delta.x / div
        lookTheta = (lookTheta - ptr.delta.y / div).clamp(0.0001f, PI.toFloat() - 0.0001f)

        lookDirection.x = sin(lookTheta) * cos(lookPhi)
        lookDirection.z = sin(lookTheta) * sin(lookPhi)
        lookDirection.y = cos(lookTheta)

        if (isZoomEnabled && !ptr.isConsumed(PointerInput.CONSUMED_SCROLL_Y)) {
            zoom *= 1f - PointerInput.primaryPointer.scroll.y / 10f
            zoom = zoom.clamp(minZoom, maxZoom)
        }
    }

    private fun updateTracking(deltaT: Float) {
        trackedPose.transform(poseOrigin.set(Vec3f.ZERO))

        transform.setIdentity()
        transform.translate(poseOrigin)
        transform.rotate((lookPhi.toDeg() + 90f).deg, Vec3f.Y_AXIS)
        transform.translate(pivotPoint)
        transform.rotate((lookTheta.toDeg() - 90f).deg, Vec3f.X_AXIS)

        actualZoom = actualZoom.expDecay(zoomModifier(zoom), 16f, deltaT)
        transform.scale(actualZoom)
    }
}