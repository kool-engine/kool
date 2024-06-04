package de.fabmax.kool.physics.character

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import physx.*
import kotlin.math.acos
import kotlin.math.cos

class JsCharacterController(
    private val pxController: PxCapsuleController,
    hitListener: ControllerHitListener,
    private val behaviorCallback: ControllerBahaviorCallback,
    manager: CharacterControllerManager,
    world: PhysicsWorldImpl
) : CharacterController(manager, world) {

    private val bufPosition = MutableVec3d()
    private val bufPxPosition = PxExtendedVec3()
    private val bufPxVec3 = PxVec3()
    private val pxControllerFilters = PxControllerFilters()

    override var position: Vec3d
        get() = pxController.position.toVec3d(bufPosition)
        set(value) {
            pxController.position = value.toPxExtendedVec3(bufPxPosition)
            prevPosition.set(value)
        }

    override val actor: RigidDynamic = RigidDynamicImpl(1f, MutableMat4f(), false, pxController.actor)

    override var height: Float = pxController.height
        set(value) {
            field = value
            pxController.height = value
        }

    override var radius: Float = pxController.radius
        set(value) {
            field = value
            pxController.radius = value
        }

    override var slopeLimit: AngleF = acos(pxController.slopeLimit).rad
        set(value) {
            field = value
            pxController.slopeLimit = cos(value.rad)
        }

    override var nonWalkableMode: NonWalkableMode = when (pxController.nonWalkableMode) {
            PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING -> NonWalkableMode.PREVENT_CLIMBING
            PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING -> NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING
            else -> error("Invalid nonWalkable mode")
        }
        set(value) {
            field = value
            pxController.nonWalkableMode = when (value) {
                NonWalkableMode.PREVENT_CLIMBING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING
                NonWalkableMode.PREVENT_CLIMBING_AND_FORCE_SLIDING -> PxControllerNonWalkableModeEnum.ePREVENT_CLIMBING_AND_FORCE_SLIDING
            }
        }

    init {
        hitListener.controller = this
        behaviorCallback.controller = this
        world.registerActorReference(actor)
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

    override fun release() {
        super.release()
        (world as PhysicsWorldImpl).deleteActorReference(actor)
        pxController.release()
        bufPxPosition.destroy()
        bufPxVec3.destroy()
        pxControllerFilters.destroy()
        behaviorCallback.callback.destroy()
    }
}