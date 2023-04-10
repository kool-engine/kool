package de.fabmax.kool.physics.util

import de.fabmax.kool.input.CursorMode
import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.HitResult
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.scene.Node
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

    private var actualZoom = zoom

    var trackedPose = Mat4f()
    val pivotPoint = MutableVec3f()

    val lookDirection = MutableVec3f(Vec3f.NEG_Z_AXIS)

    var zoomModifier: (Float) -> Float = { it }

    private val poseOrigin = MutableVec3f()
    private var lookPhi = 0f
    private var lookTheta = PI.toFloat() / 2f

    private val hitSweepGeometry by lazy { BoxGeometry(Vec3f(1f, 1f, 0.1f)) }
    private val hitSweepGeometryPose = Mat4f()

    init {
        isCursorLocked = enableCursorLock

        onUpdate += {
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
            hitSweepGeometryPose.set(transform.matrix)
            if (world.sweepTest(hitSweepGeometry, hitSweepGeometryPose, testDir, desiredZoom, hitResult)) {
                zoom = max(minZoom, hitResult.hitDistance)
            }
            zoom
        }
    }

    fun applyLookDirection() {
        lookPhi = atan2(lookDirection.z, lookDirection.x)
        lookTheta = acos(lookDirection.y)
    }

    private fun handlePointerInput() {
        applyLookDirection()

        val div = 1000f / sensitivity
        val ptr = PointerInput.primaryPointer

        lookPhi -= ptr.deltaX.toFloat() / div
        lookTheta = (lookTheta - ptr.deltaY.toFloat() / div).clamp(0.0001f, PI.toFloat() - 0.0001f)

        lookDirection.x = sin(lookTheta) * cos(lookPhi)
        lookDirection.z = sin(lookTheta) * sin(lookPhi)
        lookDirection.y = cos(lookTheta)

        if (!ptr.isConsumed(PointerInput.CONSUMED_SCROLL_Y)) {
            zoom *= 1f - PointerInput.primaryPointer.deltaScroll.toFloat() / 10f
            zoom = zoom.clamp(minZoom, maxZoom)
        }
    }

    private fun updateTracking(deltaT: Float) {
        trackedPose.transform(poseOrigin.set(Vec3f.ZERO))

        transform.setIdentity()
        transform.translate(poseOrigin)
        transform.rotate(lookPhi.toDeg() + 90f, Vec3f.Y_AXIS)
        transform.translate(pivotPoint)
        transform.rotate(lookTheta.toDeg() - 90f, Vec3f.X_AXIS)

        val modZoom = zoomModifier(zoom)
        val wMod = (15f * deltaT).clamp(0.05f, 0.95f)
        actualZoom = modZoom * wMod + actualZoom * (1f - wMod)
        transform.scale(actualZoom)
    }
}