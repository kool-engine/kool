package de.fabmax.kool.physics.character

import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.PxTopLevelFunctions
import physx.*
import kotlin.math.cos

actual class CharacterControllerManager actual constructor(world: PhysicsWorld) : CommonCharacterControllerManager(world) {

    private val pxManager: PxControllerManager

    init {
        Physics.checkIsLoaded()
        pxManager = PxTopLevelFunctions.CreateControllerManager(world.pxScene)
    }

    override fun doCreateController(): JsCharacterController {
        // create controller with default configuration
        val hitCallback = ControllerHitListener(world)
        val behaviorCallback = ControllerBahaviorCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = 1f
        desc.radius = 0.3f
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.nonWalkableMode = PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
        desc.slopeLimit = cos(50f.toRad())
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = 0.1f
        desc.reportCallback = hitCallback.callback
        desc.behaviorCallback = behaviorCallback.callback
        val pxCharacter = pxManager.createController(desc)
        desc.destroy()
        return JsCharacterController(pxCharacter, hitCallback, behaviorCallback, this, world)
    }

    override fun release() {
        super.release()
        pxManager.release()
    }
}