package de.fabmax.kool.physics.character

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.min

abstract class CharacterController(private val manager: CharacterControllerManager, val world: PhysicsWorld) : BaseReleasable() {

    abstract val actor: RigidDynamic
    abstract var position: Vec3d

    abstract var height: Float
    abstract var radius: Float
    abstract var slopeLimit: AngleF
    abstract var nonWalkableMode: NonWalkableMode

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

    private var lastGroundTouch = 0f
    private var isStandingOnGround = false
    private var isStaticGroundHit = false

    private val slopeObserver = GroundSlopeObserver()
    private val slopeSlideFac: Float
        get() = if (nonWalkableMode == NonWalkableMode.PREVENT_CLIMBING) 0f else {
            smoothStep(slopeLimit.rad * 0.7f, slopeLimit.rad, slopeObserver.groundSlopeRad)
        }

    val onPhysicsUpdate = mutableListOf<(Float) -> Unit>()
    val onHitActorListeners = mutableListOf<OnHitActorListener>()
    var hitActorBehaviorCallback: HitActorBehaviorCallback? = HitActorBehaviorCallback { actor: RigidActor ->
        actor.characterControllerHitBehavior
    }

    open fun onAdvancePhysics(timeStep: Float) {
        val isNoMove = movement.length().isFuzzyZero()
        if (!isNoMove || jump) {
            // not sure about ground, but we are certainly moving (i.e. not standing)
            isStandingOnGround = false
            isStaticGroundHit = false
        }

        if (!isDownCollision && !isStandingOnGround) {
            // character falls
            if (lastGroundTouch == 0f) {
                gravityVelocity.set(Vec3f(0f, velocity.y, 0f))
            }
            gravityVelocity.add(tmpVec.set(gravity).mul(timeStep))
            lastGroundTouch += timeStep

        } else {
            // character touches ground, keep a downwards velocity component to stay in touch with ground and
            // slide downwards at a plausible speed if sliding is enabled
            gravityVelocity.set(gravity * (0.25f * slopeSlideFac).coerceAtLeast(0.001f))
            lastGroundTouch = 0f

            if (isNoMove && isDownCollision && isStaticGroundHit) {
                isStandingOnGround = true
            }
        }

        val fallSpeed = tmpVec.set(gravity).norm().dot(gravityVelocity)
        if (jump && lastGroundTouch < 0.25f && fallSpeed >= 0f) {
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
        slopeObserver.onTouch(hitWorldNormal)

        if (actor is RigidStatic) {
            val upDir = gravity.normed(tmpVec) * -1f
            if (hitWorldNormal dot upDir > cos(slopeLimit.rad)) {
                isStaticGroundHit = true
            }
        }

        for (i in onHitActorListeners.indices) {
            onHitActorListeners[i].onHitActor(actor, hitWorldPos, hitWorldNormal)
        }
    }

    protected abstract fun move(displacement: Vec3f, timeStep: Float)

    abstract fun resize(height: Float)

    override fun release() {
        manager.removeController(this)
        super.release()
    }

    private class GroundSlopeObserver {
        var groundSlopeRad = 0f
        private var frameIdx = -1

        fun onTouch(normal: Vec3f) {
            val slope = acos(normal dot Vec3f.Y_AXIS)
            groundSlopeRad = if (Time.frameCount != frameIdx) slope else min(slope, groundSlopeRad)
        }
    }
}

data class CharacterControllerProperties(
    val height: Float = 1f,
    val radius: Float = 0.3f,
    val slopeLimit: AngleF = 45f.deg,
    val nonWalkableMode: NonWalkableMode = NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING,
    val contactOffset: Float = 0.05f,
    val simulationFilterData: FilterData = FilterData { setCollisionGroup(0); setCollidesWithEverything() },
    val queryFilterData: FilterData = FilterData()
)

enum class NonWalkableMode {
    PREVENT_CLIMBING,
    PREVENT_CLIMBING_AND_FORCE_SLIDING,
}