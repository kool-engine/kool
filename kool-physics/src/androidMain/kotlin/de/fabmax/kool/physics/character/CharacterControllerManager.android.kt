package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.*
import de.fabmax.kool.util.memStack
import physxandroid.PxTopLevelFunctions
import physxandroid.character.PxCapsuleClimbingModeEnum
import physxandroid.character.PxCapsuleControllerDesc
import physxandroid.character.PxControllerManager
import physxandroid.character.PxControllerNonWalkableModeEnum
import kotlin.math.cos

// GENERATED CODE BELOW:
// Transformed from desktop source

actual fun CharacterControllerManager(world: PhysicsWorld): CharacterControllerManager {
    return CharacterControllerManagerImpl(world)
}

class CharacterControllerManagerImpl(private val world: PhysicsWorld) : CharacterControllerManager() {
    private val pxManager: PxControllerManager

    init {
        PhysicsImpl.checkIsLoaded()
        world as PhysicsWorldImpl
        pxManager = checkNotNull(PxTopLevelFunctions.CreateControllerManager(world.pxScene)) {
            "Failed creating PxControllerManager"
        }
        world.physicsStepListeners += onUpdateListener
    }

    override fun doCreateController(charProperties: CharacterControllerProperties): CharacterController {
        // create controller with default configuration
        world as PhysicsWorldImpl
        val hitCallback = ControllerHitListener(world)
        val behaviorCallback = ControllerBehaviorCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = charProperties.height
        desc.radius = charProperties.radius
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.slopeLimit = cos(charProperties.slopeLimit.rad)
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = charProperties.contactOffset
        desc.reportCallback = hitCallback.callback
        desc.behaviorCallback = behaviorCallback.callback
        desc.nonWalkableMode = when (charProperties.nonWalkableMode) {
            NonWalkableMode.PREVENT_CLIMBING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
            NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING
        }

        val pxCharacter = WrapPointer.PxCapsuleController(pxManager.createController(desc).ptr)
        desc.destroy()

        memStack {
            val shapes = createPxArray_PxShapePtr(1)
            pxCharacter.actor.getShapes(shapes.begin(), 1, 0)
            val shape = shapes.get(0)
            shape.simulationFilterData = charProperties.simulationFilterData.toPxFilterData(createPxFilterData())
            shape.queryFilterData = charProperties.queryFilterData.toPxFilterData(createPxFilterData())
        }

        return CharacterControllerImpl(pxCharacter, hitCallback, behaviorCallback, this, world)
    }

    override fun doRelease() {
        super.doRelease()
        world.physicsStepListeners -= onUpdateListener
        pxManager.release()
    }
}