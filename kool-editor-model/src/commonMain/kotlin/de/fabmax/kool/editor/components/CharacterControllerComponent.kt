package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.GameEntity
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.data.ComponentInfo
import de.fabmax.kool.input.WalkAxes
import de.fabmax.kool.math.*
import de.fabmax.kool.physics.RigidActor
import de.fabmax.kool.physics.RigidDynamic
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerProperties
import de.fabmax.kool.physics.character.OnHitActorListener
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.launchOnMainThread
import de.fabmax.kool.util.logE
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max

class CharacterControllerComponent(
    gameEntity: GameEntity,
    componentInfo: ComponentInfo<CharacterControllerComponentData> = ComponentInfo(CharacterControllerComponentData())
) :
    PhysicsComponent<CharacterControllerComponentData>(gameEntity, componentInfo),
    OnHitActorListener
{
    var charController: CharacterController? = null
        private set

    var referenceFrontDirection = 0f.deg

    private var axes: WalkAxes? = null

    val isRun: Boolean get() = axes?.isRun == !data.runByDefault
    val isJump: Boolean get() = axes?.isJump == true
    val isCrouch: Boolean get() = axes?.isCrouch == true

    val crouchFactor: Float get() = axes?.crouchFactor ?: 0f
    val runFactor: Float get() {
        var fac = axes?.runFactor ?: 0f
        if (data.runByDefault) {
            fac = 1f - fac
        }
        return fac
    }

    override val actorTransform: TrsTransformF? get() = charController?.actor?.transform

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
        super.destroyComponent()
        charController?.let { existing ->
            physicsWorldComponent?.characterControllerManager?.removeController(existing)
            existing.release()
        }
        axes?.release()
        axes = null
    }

    private suspend fun createCharController() {
        val physicsWorldComponent = getOrCreatePhysicsWorldComponent()
        val charManager = physicsWorldComponent.characterControllerManager
        if (charManager == null) {
            logE { "Unable to create character controller: parent physics world was not yet created" }
            return
        }

        var oldPos: Vec3d? = null
        charController?.let { existing ->
            oldPos = existing.position
            charManager.removeController(existing)
            existing.release()
        }

        val props = CharacterControllerProperties(
            height = data.shape.length.toFloat(),
            radius = data.shape.radius.toFloat() - CHARACTER_CONTACT_OFFSET,
            slopeLimit = data.slopeLimit.toFloat().deg,
            contactOffset = CHARACTER_CONTACT_OFFSET
        )
        charController = charManager.createController(props).also {
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

        charCtrl.jumpSpeed = props.jumpSpeed.toFloat()
        charCtrl.maxFallSpeed = props.maxFallSpeed.toFloat()
        charCtrl.slopeLimit = props.slopeLimit.toFloat().deg
        charCtrl.nonWalkableMode = props.nonWalkableMode
    }

    override fun applyPose(position: Vec3d, rotation: QuatD) {
        charController?.position = position
    }

    private fun updateMovement(controller: CharacterController, axes: WalkAxes) {
        val crouchSpeed = data.crouchSpeed.toFloat()
        val walkSpeed = data.walkSpeed.toFloat()
        val runSpeed = data.runSpeed.toFloat()

        var moveHeading = referenceFrontDirection.deg
        val walkDir = Vec2f(-axes.leftRight, axes.forwardBackward)
        if (walkDir.length() > 0f) {
            moveHeading += atan2(walkDir.x, walkDir.y).toDeg()
        }

        val runFactor = this.runFactor
        val crouchFactor = this.crouchFactor
        val speedFactor = max(abs(axes.forwardBackward), abs(axes.leftRight))
        var moveSpeed = walkSpeed * speedFactor
        if (runFactor > 0f) {
            moveSpeed = moveSpeed * (1f - runFactor) + runSpeed * speedFactor * runFactor
        }
        if (crouchFactor > 0f) {
            moveSpeed = moveSpeed * (1f - crouchFactor) + crouchSpeed * speedFactor * crouchFactor
        }

        // set controller.movement according to user input
        controller.movement.set(0f, 0f, -moveSpeed)
        controller.movement.rotate(moveHeading.deg, Vec3f.Y_AXIS)
        controller.jump = isJump
    }

    override fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        val pushForceFac = data.pushForce.toFloat()
        val downForceFac = data.downForce.toFloat()
        if ((pushForceFac + downForceFac) > 0f && actor is RigidDynamic && !actor.isKinematic) {
            val runMod = if (axes?.isRun == true) 2f else 1f
            val downBlend = abs(hitWorldNormal dot Vec3f.Y_AXIS)
            val forceFac = downForceFac * downBlend + pushForceFac * (1f - downBlend)
            val force = actor.mass * runMod * forceFac
            val forceVec = hitWorldNormal * -force
            actor.addForceAtPos(forceVec, hitWorldPos, isLocalForce = false, isLocalPos = false)
        }
    }

    companion object {
        const val CHARACTER_CONTACT_OFFSET = 0.05f
    }
}