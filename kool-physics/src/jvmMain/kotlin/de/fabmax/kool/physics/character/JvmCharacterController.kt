package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.toPxExtendedVec3
import de.fabmax.kool.physics.toPxVec3
import de.fabmax.kool.physics.toVec3d
import org.lwjgl.system.MemoryStack
import physx.character.PxController
import physx.character.PxControllerFilters
import physx.character.PxExtendedVec3
import physx.common.PxVec3

class JvmCharacterController(private val pxController: PxController,
                             manager: CharacterControllerManager, world: PhysicsWorld) : CharacterController(manager, world) {

    private val bufPosition = MutableVec3d()
    private val bufPxPosition = PxExtendedVec3()
    private val bufPxVec3 = PxVec3()
    private val pxControllerFilters: PxControllerFilters

    override var position: Vec3d
        get() = pxController.position.toVec3d(bufPosition)
        set(value) {
            pxController.position = value.toPxExtendedVec3(bufPxPosition)
            prevPosition.set(value)
        }

    init {
        MemoryStack.stackPush().use {
            pxControllerFilters = PxControllerFilters()

//            val shape = AccessHelpers.getActorShape(pxController.actor, 0)
//            val simFilterData = FilterData()
//            simFilterData.setCollisionGroup(0)
//            simFilterData.setCollidesWithEverything()
//            simFilterData.data[2] = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND or PxPairFlagEnum.eNOTIFY_TOUCH_LOST
//            shape.simulationFilterData = simFilterData.toPxFilterData(it.createPxFilterData())
        }
    }

    override fun move(displacement: Vec3f, timeStep: Float) {
        pxController.move(displacement.toPxVec3(bufPxVec3), 0.001f, timeStep, pxControllerFilters)
    }

    override fun release() {
        super.release()
        pxController.release()
        bufPxPosition.destroy()
        bufPxVec3.destroy()
        pxControllerFilters.destroy()
    }

}