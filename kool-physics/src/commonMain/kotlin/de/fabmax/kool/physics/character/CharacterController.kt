package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.util.BaseReleasable

abstract class CharacterController(private val manager: CharacterControllerManager, val world: PhysicsWorld) : BaseReleasable() {

    abstract val actor: RigidDynamic
    abstract var position: Vec3d
    protected val prevPosition = MutableVec3d()

    private val posBuffer = MutableVec3d()
    private val tmpVec = MutableVec3f()

    private val gravityVelocity = MutableVec3f()
    private val jumpVelocity = MutableVec3f()
    private val displacement = MutableVec3f()
    private val mutVelocity = MutableVec3f()

    private val gravity: Vec3f get() = world.gravity

    val movement = MutableVec3f()
    val velocity: Vec3f
        get() = mutVelocity
    var maxFallSpeed = 30f
    var jumpSpeed = 6f

    var jump = false

    var isDownCollision = false
        protected set
    var isUpCollision = false
        protected set
    var isSideCollision = false
        protected set

    private var lastGroundTuch = 0f

    val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()
    val onHitActorListeners = mutableListOf<OnHitActorListener>()
    var hitActorBehaviorCallback: HitActorBehaviorCallback? = HitActorBehaviorCallback { actor: RigidActor ->
        actor.characterControllerHitBehavior
    }

    open fun onAdvancePhysics(timeStep: Float) {
        if (!isDownCollision) {
            // character falls
            gravityVelocity.add(tmpVec.set(gravity).mul(timeStep))
            lastGroundTuch += timeStep
        } else {
            // character touches ground, small downwards velocity component need to be kept to stay in touch with ground
            gravityVelocity.set(gravity * timeStep)
            lastGroundTuch = 0f
        }

        val fallSpeed = tmpVec.set(gravity).norm().dot(gravityVelocity)
        if (jump && lastGroundTuch < 0.25f && fallSpeed >= 0f) {
            // character touches ground (or did so recently) and jump is requested but not yet executed
            gravityVelocity.set(tmpVec.set(gravity).norm().mul(-jumpSpeed))
        }

        displacement.set(movement).mul(timeStep).add(tmpVec.set(gravityVelocity).mul(timeStep))
        move(displacement, timeStep)
    }

    open fun onPhysicsUpdate(timeStep: Float) {
        posBuffer.set(position)
        mutVelocity.set(
                (posBuffer.x - prevPosition.x).toFloat(),
                (posBuffer.y - prevPosition.y).toFloat(),
                (posBuffer.z - prevPosition.z).toFloat()
            ).mul(1f / timeStep)
        prevPosition.set(posBuffer)

        // the controller's actor is not registered in PhysicsWorld, call its update routine from here
        actor.onPhysicsUpdate(timeStep)

        for (i in onPhysicsUpdate.indices) {
            onPhysicsUpdate[i](timeStep)
        }
    }

    internal fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        for (i in onHitActorListeners.indices) {
            onHitActorListeners[i].onHitActor(actor, hitWorldPos, hitWorldNormal)
        }
    }

    protected abstract fun move(displacement: Vec3f, timeStep: Float)

    override fun release() {
        manager.removeController(this)
        super.release()
    }
}