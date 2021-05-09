package de.fabmax.kool.physics.character

import de.fabmax.kool.math.MutableVec3d
import de.fabmax.kool.math.Vec3d
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import physx.*

class JsCharacterController(private val pxController: PxController,
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
            pxControllerFilters = PxControllerFilters(null)
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