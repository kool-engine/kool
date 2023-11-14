package de.fabmax.kool.demo.physics.terrain

import de.fabmax.kool.KoolContext
import de.fabmax.kool.input.WalkAxes
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.character.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.TrsTransformF
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class PlayerController(
    private val physicsObjects: PhysicsObjects,
    mainScene: Scene,
    ctx: KoolContext
) : OnHitActorListener, HitActorBehaviorCallback {

    val controller: CharacterController
    private val charManager: CharacterControllerManager = CharacterControllerManager(physicsObjects.world)

    val position: Vec3d
        get() = controller.position
    var frontHeading = 0f
    var moveHeading = 0f
        private set
    var moveSpeed = 0f
        private set

    val playerTransform = TrsTransformF()

    private val axes: WalkAxes

    var pushForceFac = 0.75f
    private val tmpForce = MutableVec3f()

    private var bridgeSegment: RigidDynamic? = null
    private val bridgeHitPt = MutableVec3f()
    private val bridgeHitForce = MutableVec3f()
    private var bridgeHitTime = 0.0f

    val tractorGun = TractorGun(physicsObjects, mainScene)

    init {
        // create character controller
        controller = charManager.createController()
        controller.onHitActorListeners += this
        controller.hitActorBehaviorCallback = this

        // create user input listener (wasd / cursor keys)
        axes = WalkAxes(ctx)
    }

    fun release() {
        // apparently character controller is released automatically when the scene is destroyed
        //controller.release()
        //charManager.release()
        axes.release()
    }

    fun onPhysicsUpdate(timeStep: Float) {
        updateMovement()
        tractorGun.onPhysicsUpdate(timeStep)

        bridgeSegment?.let {
            it.addForceAtPos(bridgeHitForce, bridgeHitPt, isLocalForce = false, isLocalPos = true)
            bridgeHitTime -= timeStep
            if (bridgeHitTime < 0f) {
                bridgeSegment = null
            }
        }
    }

    private fun updateMovement() {
        moveHeading = frontHeading
        val walkDir = Vec2f(-axes.leftRight, axes.forwardBackward)
        if (walkDir.length() > 0f) {
            moveHeading += atan2(walkDir.x, walkDir.y).toDeg()
        }

        val speedFactor = max(abs(axes.forwardBackward), abs(axes.leftRight))
        val runFactor = 1f - axes.runFactor
        moveSpeed = walkSpeed * speedFactor
        if (runFactor > 0f) {
            moveSpeed = moveSpeed * (1f - runFactor) + runSpeed * speedFactor * runFactor
            controller.jumpSpeed = 6f
        } else {
            controller.jumpSpeed = 4f
        }
        if (axes.crouchFactor > 0f) {
            moveSpeed = moveSpeed * (1f - axes.crouchFactor) + crouchSpeed * speedFactor * axes.crouchFactor
        }

        // set controller.movement according to user input
        controller.movement.set(0f, 0f, -moveSpeed)
        controller.movement.rotate(moveHeading.deg, Vec3f.Y_AXIS)
        controller.jump = axes.isJump

        playerTransform
            .setIdentity()
            .translate(position)
            .rotate(moveHeading.toDouble().deg, Vec3d.Y_AXIS)
    }

    override fun hitActorBehavior(actor: RigidActor): HitActorBehavior {
        return if (physicsObjects.chainBridge.isBridge(actor)) {
            HitActorBehavior.RIDE
        } else {
            HitActorBehavior.DEFAULT
        }
    }

    override fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        if (actor is RigidDynamic) {
            if (physicsObjects.chainBridge.isBridge(actor)) {
                updateBridgeForce(actor, hitWorldPos)
            } else {
                bridgeSegment = null
                applyBoxForce(actor, hitWorldPos, hitWorldNormal)
            }
        }
    }

    private fun updateBridgeForce(actor: RigidDynamic, hitWorldPos: Vec3f) {
        // apply some force (100 kg / 150 kg) to the bridge segment in straight down direction
        val force = if (axes.isRun) -1500f else -1000f
        bridgeHitForce.set(0f, force, 0f)

        // force is cached and applied in onPhysicsUpdate to reduce jitter
        actor.toLocal(bridgeHitPt.set(hitWorldPos))
        bridgeHitPt.y = 0f
        bridgeHitPt.z = 0f
        bridgeSegment = actor
        bridgeHitTime = 0.2f
    }

    private fun applyBoxForce(actor: RigidDynamic, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        // apply some fixed force to the hit actor
        val force = if (axes.isRun) -4000f else -2000f
        tmpForce.set(hitWorldNormal).mul(force * pushForceFac)
        actor.addForceAtPos(tmpForce, hitWorldPos, isLocalForce = false, isLocalPos = false)
    }

    companion object {
        // movement speeds, tuned to roughly match the model animation speed
        const val walkSpeed = 1.3f
        const val crouchSpeed = 0.5f
        const val runSpeed = 5f
    }
}