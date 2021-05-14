package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.physics.RigidDynamic
import kotlin.math.min

abstract class CharacterController(private val manager: CharacterControllerManager, world: PhysicsWorld) : Releasable {

    abstract val actor: RigidDynamic
    abstract var position: Vec3d
    protected val prevPosition = MutableVec3d()

    private val posBuffer = MutableVec3d()
    private val totalVelocity = MutableVec3f()

    private val gravityVelocity = MutableVec3f()
    private val moveDisp = MutableVec3f()
    private val displacement = MutableVec3f()

    val gravity = MutableVec3f(world.gravity)
    var maxFallSpeed = 30f
    var jumpSpeed = 4f
    val moveVelocity = MutableVec3f()

    var jump = false
    private var wasJump = false

    val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()

    open fun onAdvancePhysics(timeStep: Float) {
        displacement.set(Vec3f.ZERO)
        if (gravity != Vec3f.ZERO) {
            gravityVelocity.set(gravity).norm()
            val gravSpeed = min(gravityVelocity * totalVelocity, maxFallSpeed)
            gravityVelocity.scale(gravSpeed + gravity.length() * timeStep)
            displacement.set(gravityVelocity).scale(timeStep)
        }
        moveDisp.set(moveVelocity).scale(timeStep)
        displacement.add(moveDisp)

        if (jump && !wasJump) {
            // todo: check if character has contact to ground / is able to jump
            displacement.y += jumpSpeed * timeStep
        }
        wasJump = jump

        move(displacement, timeStep)
    }

    open fun onPhysicsUpdate(timeStep: Float) {
        posBuffer.set(position)
        totalVelocity.set(
            (posBuffer.x - prevPosition.x).toFloat(),
            (posBuffer.y - prevPosition.y).toFloat(),
            (posBuffer.z - prevPosition.z).toFloat()
        ).scale(1f / timeStep)
        prevPosition.set(posBuffer)

        // the controller's actor is not registered in PhysicsWorld, call it's update routine from here
        actor.onPhysicsUpdate(timeStep)

        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i](timeStep)
        }
    }

    protected abstract fun move(displacement: Vec3f, timeStep: Float)

    override fun release() {
        manager.removeController(this)
    }
}