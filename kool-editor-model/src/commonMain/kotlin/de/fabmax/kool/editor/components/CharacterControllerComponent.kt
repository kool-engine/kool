package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.WalkAxes
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
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
    nodeModel: SceneNodeModel,
    override val componentData: CharacterControllerComponentData = CharacterControllerComponentData()
) :
    PhysicsNodeComponent(nodeModel),
    EditorDataComponent<CharacterControllerComponentData>,
    OnHitActorListener
{

    val charControllerState = mutableStateOf(componentData.properties).onChange {
        if (AppState.isEditMode) {
            componentData.properties = it
        }
        launchOnMainThread {
            createCharController()
        }
    }

    var charController: CharacterController? = null
        private set

    var referenceFrontDirection = 0f.deg

    private var axes: WalkAxes? = null

    val isRun: Boolean get() = axes?.isRun == !charControllerState.value.runByDefault
    val isJump: Boolean get() = axes?.isJump == true
    val isCrouch: Boolean get() = axes?.isCrouch == true

    val crouchFactor: Float get() = axes?.crouchFactor ?: 0f
    val runFactor: Float get() {
        var fac = axes?.runFactor ?: 0f
        if (charControllerState.value.runByDefault) {
            fac = 1f - fac
        }
        return fac
    }

    override val actorTransform: TrsTransformF? get() = charController?.actor?.transform

    override suspend fun createComponent() {
        super.createComponent()
        createCharController()
    }

    override fun onStart() {
        super.onStart()
        if (componentData.properties.enableDefaultControls) {
            axes = WalkAxes(KoolSystem.requireContext())
        }
    }

    override fun updatePhysics(dt: Float) {
        super.updatePhysics(dt)
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

        val props = componentData.properties.let {
            CharacterControllerProperties(
                height = it.shape.length.toFloat(),
                radius = it.shape.radius.toFloat() - CHARACTER_CONTACT_OFFSET,
                slopeLimit = it.slopeLimit.toFloat(),
                contactOffset = CHARACTER_CONTACT_OFFSET
            )
        }
        charController = charManager.createController(props).also {
            it.onHitActorListeners += this
        }

        if (oldPos != null) {
            applyPose(oldPos!!, QuatD.IDENTITY)
        } else {
            setPhysicsTransformFromDrawNode()
        }
    }

    override fun applyPose(position: Vec3d, rotation: QuatD) {
        charController?.position = position
    }

    private fun updateMovement(controller: CharacterController, axes: WalkAxes) {
        val props = charControllerState.value
        val crouchSpeed = props.crouchSpeed.toFloat()
        val walkSpeed = props.walkSpeed.toFloat()
        val runSpeed = props.runSpeed.toFloat()

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
        controller.jumpSpeed = props.jumpSpeed.toFloat()
        controller.jump = isJump
    }

    override fun onHitActor(actor: RigidActor, hitWorldPos: Vec3f, hitWorldNormal: Vec3f) {
        val pushForceFac = charControllerState.value.pushForce.toFloat()
        val downForceFac = charControllerState.value.downForce.toFloat()
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
        const val CHARACTER_CONTACT_OFFSET = 0.1f
    }
}