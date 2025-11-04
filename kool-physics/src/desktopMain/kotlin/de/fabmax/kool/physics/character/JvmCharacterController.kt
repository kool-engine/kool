package de.fabmax.kool.physics.character

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import physx.character.*
import physx.common.PxVec3

class JvmCharacterController(
    private val pxController: PxCapsuleController,
    private val hitListener: ControllerHitListener,
    private val behaviorCallback: ControllerBahaviorCallback,
    manager: CharacterControllerManager,
    world: PhysicsWorldImpl
) : CharacterController(manager, world) {

    private val bufPxPosition = PxExtendedVec3()
    private val bufPxVec3 = PxVec3()
    private val pxControllerFilters = PxControllerFilters()

    override val actor: RigidDynamic = RigidDynamicImpl(1f, Mat4f.IDENTITY, false, pxController.actor)

    override var nonWalkableMode: NonWalkableMode =
        when (pxController.nonWalkableMode!!) {
            PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING -> NonWalkableMode.PREVENT_CLIMBING
            PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING -> NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING
        }
        set(value) {
            field = value
            pxController.nonWalkableMode = when (value) {
                NonWalkableMode.PREVENT_CLIMBING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
                NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING
            }
        }

    private val tmpPos = MutableVec3d()

    init {
        hitListener.controller = this
        behaviorCallback.controller = this
        world.registerActorReference(actor)
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        bufPosition.writeIfDirty {
            pxController.position = tmpPos.set(it).toPxExtendedVec3(bufPxPosition)
            posA.set(it)
            posB.set(it)
        }
        bufHeight.writeIfDirty { pxController.height = it }
        bufRadius.writeIfDirty { pxController.radius = it }
        bufSlopeLimit.writeIfDirty { pxController.slopeLimit = it }

        bufPosition.read { pxController.position.toVec3d(tmpPos).toMutableVec3f(it) }
        bufHeight.read(pxController.height)
        bufRadius.read(pxController.radius)
        bufSlopeLimit.read(pxController.slopeLimit)
        super.onPhysicsUpdate(timeStep)
    }

    override fun move(displacement: Vec3f, timeStep: Float) {
        val flags = pxController.move(displacement.toPxVec3(bufPxVec3), 0.001f, timeStep, pxControllerFilters)
        isDownCollision = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_DOWN)
        isUpCollision = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_UP)
        isSideCollision = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_SIDES)
    }

    override fun resize(height: Float) {
        pxController.resize(height)
    }

    override fun doRelease() {
        super.doRelease()
        (world as PhysicsWorldImpl).deleteActorReference(actor)
        pxController.release()
        bufPxPosition.destroy()
        bufPxVec3.destroy()
        pxControllerFilters.destroy()
        hitListener.destroy()
        behaviorCallback.destroy()
    }
}