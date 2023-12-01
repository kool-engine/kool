package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.input.PointerInput
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.HitResult
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.util.CharacterTrackingCamRig
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.MdColor
import kotlin.math.abs
import kotlin.math.sign

class TractorGun(val physics: PhysicsObjects, val mainScene: Scene) {

    var camRig: CharacterTrackingCamRig? = null
    private val tractorBeam = TractorBeam()

    var tractorState = TractorState.IDLE
        private set

    var tractorBox: RigidDynamic? = null
    var rotationTorque = 0f
        set(value) {
            field = value.clamp(-4f, 4f)
        }
    var tractorDistance = 2.5f
        set(value) {
            field = value.clamp(0f, 10f)
        }

    init {
        mainScene.onUpdate += {
            val ptr = PointerInput.primaryPointer
            if (!ptr.isConsumed() && ptr.isLeftButtonEvent && ptr.isLeftButtonReleased) {
                tractorState = if (tractorState == TractorState.TRACTOR) {
                    TractorState.DROP
                } else {
                    TractorState.PICK_UP
                }
            } else if (!ptr.isConsumed() && ptr.isRightButtonEvent && ptr.isRightButtonReleased) {
                tractorState = TractorState.SHOOT
            }
        }
    }

    fun onPhysicsUpdate(timeStep: Float) {
        physics.debugLines.clear()

        when (tractorState) {
            TractorState.IDLE -> Unit
            TractorState.PICK_UP -> {
                tractorState = if (pickUp()) {
                    TractorState.TRACTOR
                } else {
                    TractorState.IDLE
                }
            }
            TractorState.TRACTOR -> {
                if (!tractor(timeStep)) {
                    tractorState = TractorState.IDLE
                }
            }
            TractorState.DROP -> {
                drop()
                tractorState = TractorState.IDLE
            }
            TractorState.SHOOT -> {
                shoot()
                tractorState = TractorState.IDLE
            }
        }
    }

    private fun pickUp(): Boolean {
        val ray = RayF(mainScene.camera.globalPos, mainScene.camera.globalLookDir)
        val hitResult = HitResult()
        if (physics.world.raycast(ray, tractorBeam.range, hitResult) && hitResult.nearestActor?.tags?.hasTag("isBox") == true) {
            tractorBox = hitResult.nearestActor as? RigidDynamic
            return true
        }
        return false
    }

    private fun tractor(timeStep: Float): Boolean {
        return tractorBox?.let { box ->
            tractorBeam.applyTractorForce(box, timeStep)
            true
        } ?: false
    }

    private fun drop() {
        tractorBox = null
    }

    private fun shoot() {
        if (tractorBox == null) {
            pickUp()
        }
        val impulse = MutableVec3f(mainScene.camera.globalLookDir).mul(3f).add(Vec3f.Y_AXIS).norm()
        tractorBox?.let { it.addImpulseAtPos(impulse.mul(it.mass * 35), it.position) }
        tractorBox = null
    }

    private inner class TractorBeam {
        val range = 40f

        val lookTransform = MutableMat4f()
        val desiredPos = MutableVec3f()

        var prevPullError = 0f
        var kpPull = 50f
        var kdPull = 1f

        var prevVelocityError = 0f
        var kpVelocity = 2.5f
        var kdVelocity = 0f

        fun applyTractorForce(box: RigidDynamic, timeStep: Float) {
            updateDesiredPos()

            val p0 = MutableVec3f(box.position)
            val p1 = MutableVec3f(desiredPos)
            p0.y += 1.5f
            p1.y += 1.5f
            physics.debugLines.addLine(p0, p1, MdColor.GREEN)

            val posError = pullTowardsDesired(box, timeStep)
            reduceVelocity(box, timeStep, posError)
            controlRotation(box, timeStep)
        }

        private fun pullTowardsDesired(box: RigidDynamic, timeStep: Float): Float {
            val delta = desiredPos.subtract(box.position, MutableVec3f())
            val error = delta.length()
            if (error > 0.01f) {
                val s = box.mass * kPos
                val fk = error * kpPull * s
                val fd = (error - prevPullError) / timeStep * kdPull * s
                prevPullError = error

                val f = (fk + fd).clamp(-10000f, 10000f)
                delta.norm().mul(f)
                box.addForceAtPos(delta, box.position, false)

                val p0 = MutableVec3f(box.position)
                val p1 = MutableVec3f(delta).mul(0.001f).add(box.position)
                p0.y += 1.5f
                p1.y += 1.5f
                physics.debugLines.addLine(p0, p1, MdColor.PINK)
            }
            return error
        }

        private fun reduceVelocity(box: RigidDynamic, timeStep: Float, posError: Float) {
            val delta = MutableVec3f(box.linearVelocity)
            val error = delta.length()
            if (error > 0.01f) {
                val posFac = (2f / posError).clamp(0.1f, 2f)
                val s = box.mass * kVel * posFac
                val fk = error * kpVelocity * s
                val fd = (error - prevVelocityError) / timeStep * kdVelocity * s
                prevVelocityError = error

                delta.norm().mul(-fk - fd)
                box.addForceAtPos(delta, box.position, false)

                val p0 = MutableVec3f(box.position)
                val p1 = MutableVec3f(delta).mul(0.001f).add(box.position)
                p0.y += 1.5f
                p1.y += 1.5f
                physics.debugLines.addLine(p0, p1, MdColor.BLUE)
            }
        }

        private fun controlRotation(box: RigidDynamic, timeStep: Float) {
            val torque = MutableVec3f(box.angularVelocity).mul(-box.mass)
            box.addTorque(torque)

            val bestUp = box.transform.transform(MutableVec3f(Vec3f.X_AXIS), 0f)
            val testUp = MutableVec3f(Vec3f.Y_AXIS)
            if (abs(box.transform.transform(testUp, 0f).dot(Vec3f.Y_AXIS)) > abs(bestUp.dot(Vec3f.Y_AXIS))) {
                bestUp.set(testUp)
            }
            testUp.set(Vec3f.Z_AXIS)
            if (abs(box.transform.transform(testUp, 0f).dot(Vec3f.Y_AXIS)) > abs(bestUp.dot(Vec3f.Y_AXIS))) {
                bestUp.set(testUp)
            }

            var dot = bestUp.dot(Vec3f.Y_AXIS)
            if (dot < 0f) {
                bestUp *= -1f
                dot *= -1f
            }
            if (dot < 0.999f) {
                bestUp.cross(Vec3f.Y_AXIS, torque).norm().mul(box.mass * (1f - dot).clamp(0.05f, 0.3f) * 20f)
                box.addTorque(torque)

                val p0 = MutableVec3f(box.position)
                val p1 = MutableVec3f(bestUp).mul(3f).add(box.position)
                physics.debugLines.addLine(p0, p1, MdColor.AMBER)

                p1.set(torque).mul(0.05f).add(box.position)
                physics.debugLines.addLine(p0, p1, MdColor.PURPLE)
            }

            if (rotationTorque != 0f) {
                box.addTorque(Vec3f(0f, -rotationTorque * box.mass, 0f))
                if (abs(rotationTorque) > timeStep) {
                    rotationTorque -= timeStep * sign(rotationTorque) * 4f
                } else {
                    rotationTorque = 0f
                }
            }
        }

        private fun updateDesiredPos() {
            camRig?.let {
                lookTransform.set(it.transform.matrixF).scale(1f / it.zoom)
                lookTransform.transform(desiredPos.set(0f, 2.5f, -tractorDistance))
            }
        }
    }

    enum class TractorState {
        IDLE,
        PICK_UP,
        TRACTOR,
        DROP,
        SHOOT
    }

    companion object {
        var kPos = 1f
        var kVel = 1f
    }
}