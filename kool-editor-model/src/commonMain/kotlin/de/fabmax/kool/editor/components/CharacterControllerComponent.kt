package de.fabmax.kool.editor.components

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.editor.api.AppState
import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.input.WalkAxes
import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerProperties
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
    EditorDataComponent<CharacterControllerComponentData>
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

    private var axes: WalkAxes? = null

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

    override fun updatePhysics() {
        super.updatePhysics()
        axes?.let {
            updateMovement(charController!!, it)
        }
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
                radius = it.shape.radius.toFloat(),
                slopeLimit = it.slopeLimit.toFloat(),
                contactOffset = it.contactOffset.toFloat(),
            )
        }
        charController = charManager.createController(props)

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

        var moveHeading = 0f
        val walkDir = Vec2f(-axes.leftRight, axes.forwardBackward)
        if (walkDir.length() > 0f) {
            moveHeading += atan2(walkDir.x, walkDir.y).toDeg()
        }

        val speedFactor = max(abs(axes.forwardBackward), abs(axes.leftRight))
        var runFactor = axes.runFactor
        if (props.runByDefault) {
            runFactor = 1f - runFactor
        }

        var moveSpeed = walkSpeed * speedFactor
        if (runFactor > 0f) {
            moveSpeed = moveSpeed * (1f - runFactor) + runSpeed * speedFactor * runFactor
        }
        if (axes.crouchFactor > 0f) {
            moveSpeed = moveSpeed * (1f - axes.crouchFactor) + crouchSpeed * speedFactor * axes.crouchFactor
        }

        // set controller.movement according to user input
        controller.movement.set(0f, 0f, -moveSpeed)
        controller.movement.rotate(moveHeading.deg, Vec3f.Y_AXIS)
        controller.jumpSpeed = props.jumpSpeed.toFloat()
        controller.jump = axes.isJump
    }

}