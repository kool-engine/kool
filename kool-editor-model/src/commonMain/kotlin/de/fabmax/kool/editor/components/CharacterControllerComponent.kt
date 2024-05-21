package de.fabmax.kool.editor.components

import de.fabmax.kool.editor.data.CharacterControllerComponentData
import de.fabmax.kool.editor.model.SceneNodeModel
import de.fabmax.kool.math.QuatD
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.physics.character.CharacterController
import de.fabmax.kool.physics.character.CharacterControllerProperties
import de.fabmax.kool.scene.TrsTransformF
import de.fabmax.kool.util.logE

class CharacterControllerComponent(
    nodeModel: SceneNodeModel,
    override val componentData: CharacterControllerComponentData = CharacterControllerComponentData()
) :
    PhysicsNodeComponent(nodeModel),
    EditorDataComponent<CharacterControllerComponentData>
{

    var charController: CharacterController? = null
        private set

    override val actorTransform: TrsTransformF? get() = charController?.actor?.transform

    override suspend fun createComponent() {
        super.createComponent()
        createCharController()
    }

    override fun destroyComponent() {
        super.destroyComponent()
        charController?.let { existing ->
            physicsWorldComponent?.characterControllerManager?.removeController(existing)
            existing.release()
        }
    }

    private suspend fun createCharController() {
        val physicsWorldComponent = getOrCreatePhysicsWorldComponent()
        val charManager = physicsWorldComponent.characterControllerManager
        if (charManager == null) {
            logE { "Unable to create character controller: parent physics world was not yet created" }
            return
        }

        charController?.let { existing ->
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

        setPhysicsTransformFromDrawNode()
    }

    override fun applyPose(position: Vec3d, rotation: QuatD) {
        charController?.position = position
    }
}