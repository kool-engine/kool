package de.fabmax.kool.physics.character

import de.fabmax.kool.physics.*
import de.fabmax.kool.util.memStack
import physx.PxTopLevelFunctions
import physx.character.*
import kotlin.math.cos

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

        world.onAdvancePhysics += onAdvanceListener
        world.onPhysicsUpdate += onUpdateListener
    }

    override fun doCreateController(charProperties: CharacterControllerProperties): CharacterController {
        // create controller with default configuration
        world as PhysicsWorldImpl
        val hitCallback = ControllerHitListener(world)
        val behaviorCallback = ControllerBahaviorCallback(world)
        val desc = PxCapsuleControllerDesc()
        desc.height = charProperties.height
        desc.radius = charProperties.radius
        desc.climbingMode = PxCapsuleClimbingModeEnum.eEASY
        desc.slopeLimit = cos(charProperties.slopeLimit.rad)
        desc.material = Physics.defaultMaterial.pxMaterial
        desc.contactOffset = charProperties.contactOffset
        desc.reportCallback = hitCallback
        desc.behaviorCallback = behaviorCallback
        desc.nonWalkableMode = when (charProperties.nonWalkableMode) {
            NonWalkableMode.PREVENT_CLIMBING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
            NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING
        }

        val pxCharacter = PxCapsuleController.wrapPointer(pxManager.createController(desc).address)
        desc.destroy()

        memStack {
            val shapes = createPxArray_PxShapePtr(1)
            pxCharacter.actor.getShapes(shapes.begin(), 1, 0)
            val shape = shapes.get(0)
            shape.simulationFilterData = charProperties.simulationFilterData.toPxFilterData(createPxFilterData())
            shape.queryFilterData = charProperties.queryFilterData.toPxFilterData(createPxFilterData())
        }

        return JvmCharacterController(pxCharacter, hitCallback, behaviorCallback, this, world)
    }

    override fun release() {
        world.onAdvancePhysics -= onAdvanceListener
        world.onPhysicsUpdate -= onUpdateListener
        pxManager.release()
        super.release()
    }
}