package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.api.isDestroyed
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.input.WalkAxes
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.OnHitActorListener
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.launchOnMainThread
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class CharacterControllerComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<CharacterControllerComponentData> = ComponentInfo(CharacterControllerComponentData())
) :
    PhysicsActorComponent<CharacterControllerComponentData>(gameEntity, componentInfo),
    OnHitActorListener
{
    var charController: CharacterController? = null; internal set

    var referenceFrontDirection = 0f.deg

    private var axes: WalkAxes? = null
    private var smoothDir = MutableVec2f()
    private var pushForce = MutableVec3f()

    val isRun: Boolean get() = axes?.isRun == !data.runByDefault
    val isJump: Boolean get() = axes?.isJump == true
    val isCrouch: Boolean get() = axes?.isCrouch == true

    var moveSpeed: Float = 0f
        private set
    var moveHeading: AngleF = 0f.deg
        private set
    private val _moveDir = MutableVec3f()
    val moveDir: Vec3f get() = _moveDir

    val crouchFactor: Float get() = axes?.crouchFactor ?: 0f
    val runFactor: Float get() {
        var fac = axes?.runFactor ?: 0f
        if (data.runByDefault) {
            fac = 1f - fac
        }
        return fac
    }

    override val physicsActorTransform: TrsTransformF? get() = charController?.actor?.transform

    override fun onDataChanged(oldData: CharacterControllerComponentData, newData: CharacterControllerComponentData) {
        launchOnMainThread {
            updateControllerProps(newData)
        }
    }

    override suspend fun applyComponent() {
        super.applyComponent()
        createCharController()
        updateControllerProps(data)
    }

    override fun onStart() {
        super.onStart()
        if (data.enableDefaultControls) {
            axes = WalkAxes(KoolSystem.requireContext())
        }
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        super.onPhysicsUpdate(timeStep)
        axes?.let { updateMovement(charController!!, it) }
    }

    override fun destroyComponent() {
        physicsWorldComponent?.let { world ->
            if (!world.isDestroyed) {
                charController?.let { ctrl ->
                    world.removeCharController(this)
                    ctrl.release()
                    charController = null
                }
            }
        }
        isAttachedToSimulation = false
        charController = null
        axes?.release()
        axes = null
        super.destroyComponent()
    }

    private suspend fun createCharController() {
        val physicsWorldComponent = getOrCreatePhysicsWorldComponent(gameEntity.scene)

        var oldPos: Vec3d? = null
        charController?.let { existing ->
            oldPos = existing.position
            physicsWorldComponent.removeCharController(this)
            existing.release()
            charController = null
        }

        charController = physicsWorldComponent.addCharController(this)?.also {
            it.onHitActorListeners += this
        }

        if (oldPos != null) {
            applyPose(oldPos!!, QuatD.IDENTITY)
        } else {
            setPhysicsTransformFromDrawNode()
        }
    }

    private fun updateControllerProps(props: CharacterControllerComponentData) {
        val charCtrl = charController ?: return

        val height = props.shape.length.toFloat()
        if (charCtrl.height != height) {
            charCtrl.resize(height)
        }
        charCtrl.radius = props.shape.radius.toFloat() - CHARACTER_CONTACT_OFFSET

        charCtrl.jumpSpeed = props.jumpSpeed
        charCtrl.maxFallSpeed = props.maxFallSpeed
        charCtrl.slopeLimit = props.slopeLimit.deg
        charCtrl.nonWalkableMode = props.nonWalkableMode
    }

    override fun applyPose(position: Vec3d, rotation: QuatD) {
        charController?.position = position
    }

    private fun updateMovement(controller: CharacterController, axes: WalkAxes) {
        val crouchSpeed = data.crouchSpeed
        val walkSpeed = data.walkSpeed
        val runSpeed = data.runSpeed

        var head = referenceFrontDirection.rad
        val walkDir = Vec2f(-axes.leftRight, axes.forwardBackward)
        if (walkDir.length() > 0f) {
            head += atan2(walkDir.x, walkDir.y)
        }
        head = (head + PI_F * 2f) % (PI_F * 2f)

        val current = moveHeading.rad
        if (abs(current - head) > PI_F) {
            if (current > head) {
                moveHeading -= 360f.deg
            } else {
                moveHeading += 360f.deg
            }
        }
        moveHeading = moveHeading.rad.expDecay(head, 24f).rad

        val runFactor = this.runFactor
        val crouchFactor = this.crouchFactor
        val speedFactor = max(abs(axes.forwardBackward), abs(axes.leftRight))
        moveSpeed = walkSpeed * speedFactor
        if (runFactor > 0f) {
            moveSpeed = moveSpeed * (1f - runFactor) + runSpeed * speedFactor * runFactor
        }
        if (crouchFactor > 0f) {
            moveSpeed = moveSpeed * (1f - crouchFactor) + crouchSpeed * speedFactor * crouchFactor
        }

        // set controller.movement according to user input
        _moveDir.set(0f, 0f, -moveSpeed).rotate(moveHeading, Vec3f.Y_AXIS)
        controller.movement.set(moveDir)
        controller.jump = isJump
    }

    override fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        val pushForceFac = data.pushForce
        val downForceFac = data.downForce
        if ((pushForceFac + downForceFac) > 0f && actor is RigidDynamic && !actor.isKinematic) {
            val runMod = if (axes?.isRun == true) 2f else 1f
            val downBlend = abs(hitWorldNormal dot Vec3f.Y_AXIS)
            val forceFac = downForceFac * downBlend + pushForceFac * (1f - downBlend)
            val force = actor.mass * runMod * forceFac
            val forceVec = pushForce.set(moveDir).mul(-1f).add(hitWorldNormal).norm().mul(-force)
            actor.addForceAtPos(forceVec, hitWorldPos, isLocalForce = false, isLocalPos = false)
        }
    }

    companion object {
        const val CHARACTER_CONTACT_OFFSET = 0.05f
    }
}