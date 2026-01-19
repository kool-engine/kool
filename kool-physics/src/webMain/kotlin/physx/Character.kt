/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxBoxController : JsAny, PxController {
    /**
     * @return WebIDL type: float
     */
    fun getHalfHeight(): Float

    /**
     * @return WebIDL type: float
     */
    fun getHalfSideExtent(): Float

    /**
     * @return WebIDL type: float
     */
    fun getHalfForwardExtent(): Float

    /**
     * @param halfHeight WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setHalfHeight(halfHeight: Float): Boolean

    /**
     * @param halfSideExtent WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setHalfSideExtent(halfSideExtent: Float): Boolean

    /**
     * @param halfForwardExtent WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setHalfForwardExtent(halfForwardExtent: Float): Boolean

}

fun PxBoxControllerFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBoxController = js("_module.wrapPointer(ptr, _module.PxBoxController)")

var PxBoxController.halfHeight
    get() = getHalfHeight()
    set(value) { setHalfHeight(value) }
var PxBoxController.halfSideExtent
    get() = getHalfSideExtent()
    set(value) { setHalfSideExtent(value) }
var PxBoxController.halfForwardExtent
    get() = getHalfForwardExtent()
    set(value) { setHalfForwardExtent(value) }

external interface PxBoxControllerDesc : JsAny, DestroyableNative, PxControllerDesc {
    /**
     * WebIDL type: float
     */
    var halfHeight: Float
    /**
     * WebIDL type: float
     */
    var halfSideExtent: Float
    /**
     * WebIDL type: float
     */
    var halfForwardExtent: Float

    fun setToDefault()

}

fun PxBoxControllerDesc(_module: JsAny = PhysXJsLoader.physXJs): PxBoxControllerDesc = js("new _module.PxBoxControllerDesc()")

fun PxBoxControllerDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBoxControllerDesc = js("_module.wrapPointer(ptr, _module.PxBoxControllerDesc)")

external interface PxBoxObstacle : JsAny, DestroyableNative, PxObstacle {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var mHalfExtents: PxVec3
}

fun PxBoxObstacle(_module: JsAny = PhysXJsLoader.physXJs): PxBoxObstacle = js("new _module.PxBoxObstacle()")

fun PxBoxObstacleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBoxObstacle = js("_module.wrapPointer(ptr, _module.PxBoxObstacle)")

external interface PxCapsuleController : JsAny, PxController {
    /**
     * @return WebIDL type: float
     */
    fun getRadius(): Float

    /**
     * @param radius WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setRadius(radius: Float): Boolean

    /**
     * @return WebIDL type: float
     */
    fun getHeight(): Float

    /**
     * @param height WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setHeight(height: Float): Boolean

    /**
     * @return WebIDL type: [PxCapsuleClimbingModeEnum] (enum)
     */
    fun getClimbingMode(): Int

    /**
     * @param mode WebIDL type: [PxCapsuleClimbingModeEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun setClimbingMode(mode: Int): Boolean

}

fun PxCapsuleControllerFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCapsuleController = js("_module.wrapPointer(ptr, _module.PxCapsuleController)")

var PxCapsuleController.radius
    get() = getRadius()
    set(value) { setRadius(value) }
var PxCapsuleController.height
    get() = getHeight()
    set(value) { setHeight(value) }
var PxCapsuleController.climbingMode: PxCapsuleClimbingModeEnum
    get() = PxCapsuleClimbingModeEnum.forValue(getClimbingMode())
    set(value) { setClimbingMode(value.value) }

fun PxCapsuleController.setClimbingMode(mode: PxCapsuleClimbingModeEnum) = setClimbingMode(mode.value)

external interface PxCapsuleControllerDesc : JsAny, DestroyableNative, PxControllerDesc {
    /**
     * WebIDL type: float
     */
    var radius: Float
    /**
     * WebIDL type: float
     */
    var height: Float
    /**
     * WebIDL type: [PxCapsuleClimbingModeEnum] (enum)
     */
    var climbingMode: Int

    fun setToDefault()

}

fun PxCapsuleControllerDesc(_module: JsAny = PhysXJsLoader.physXJs): PxCapsuleControllerDesc = js("new _module.PxCapsuleControllerDesc()")

fun PxCapsuleControllerDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCapsuleControllerDesc = js("_module.wrapPointer(ptr, _module.PxCapsuleControllerDesc)")

var PxCapsuleControllerDesc.climbingModeEnum: PxCapsuleClimbingModeEnum
    get() = PxCapsuleClimbingModeEnum.forValue(climbingMode)
    set(value) { climbingMode = value.value }

external interface PxCapsuleObstacle : JsAny, DestroyableNative, PxObstacle {
    /**
     * WebIDL type: float
     */
    var mHalfHeight: Float
    /**
     * WebIDL type: float
     */
    var mRadius: Float
}

fun PxCapsuleObstacle(_module: JsAny = PhysXJsLoader.physXJs): PxCapsuleObstacle = js("new _module.PxCapsuleObstacle()")

fun PxCapsuleObstacleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCapsuleObstacle = js("_module.wrapPointer(ptr, _module.PxCapsuleObstacle)")

external interface PxController : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: [PxControllerShapeTypeEnum] (enum)
     */
    fun getType(): Int

    fun release()

    /**
     * @param disp        WebIDL type: [PxVec3] (Const, Ref)
     * @param minDist     WebIDL type: float
     * @param elapsedTime WebIDL type: float
     * @param filters     WebIDL type: [PxControllerFilters] (Const, Ref)
     * @return WebIDL type: [PxControllerCollisionFlags] (Value)
     */
    fun move(disp: PxVec3, minDist: Float, elapsedTime: Float, filters: PxControllerFilters): PxControllerCollisionFlags

    /**
     * @param disp        WebIDL type: [PxVec3] (Const, Ref)
     * @param minDist     WebIDL type: float
     * @param elapsedTime WebIDL type: float
     * @param filters     WebIDL type: [PxControllerFilters] (Const, Ref)
     * @param obstacles   WebIDL type: [PxObstacleContext] (Const)
     * @return WebIDL type: [PxControllerCollisionFlags] (Value)
     */
    fun move(disp: PxVec3, minDist: Float, elapsedTime: Float, filters: PxControllerFilters, obstacles: PxObstacleContext): PxControllerCollisionFlags

    /**
     * @param position WebIDL type: [PxExtendedVec3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun setPosition(position: PxExtendedVec3): Boolean

    /**
     * @return WebIDL type: [PxExtendedVec3] (Const, Ref)
     */
    fun getPosition(): PxExtendedVec3

    /**
     * @param position WebIDL type: [PxExtendedVec3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun setFootPosition(position: PxExtendedVec3): Boolean

    /**
     * @return WebIDL type: [PxExtendedVec3] (Value)
     */
    fun getFootPosition(): PxExtendedVec3

    /**
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun getActor(): PxRigidDynamic

    /**
     * @param offset WebIDL type: float
     */
    fun setStepOffset(offset: Float)

    /**
     * @return WebIDL type: float
     */
    fun getStepOffset(): Float

    /**
     * @param flag WebIDL type: [PxControllerNonWalkableModeEnum] (enum)
     */
    fun setNonWalkableMode(flag: Int)

    /**
     * @return WebIDL type: [PxControllerNonWalkableModeEnum] (enum)
     */
    fun getNonWalkableMode(): Int

    /**
     * @return WebIDL type: float
     */
    fun getContactOffset(): Float

    /**
     * @param offset WebIDL type: float
     */
    fun setContactOffset(offset: Float)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getUpDirection(): PxVec3

    /**
     * @param up WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setUpDirection(up: PxVec3)

    /**
     * @return WebIDL type: float
     */
    fun getSlopeLimit(): Float

    /**
     * @param slopeLimit WebIDL type: float
     */
    fun setSlopeLimit(slopeLimit: Float)

    fun invalidateCache()

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @return WebIDL type: VoidPtr
     */
    fun getUserData(): JsAny

    /**
     * @param userData WebIDL type: VoidPtr
     */
    fun setUserData(userData: JsAny)

    /**
     * @param state WebIDL type: [PxControllerState] (Ref)
     */
    fun getState(state: PxControllerState)

    /**
     * @param stats WebIDL type: [PxControllerStats] (Ref)
     */
    fun getStats(stats: PxControllerStats)

    /**
     * @param height WebIDL type: float
     */
    fun resize(height: Float)

}

fun PxControllerFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxController = js("_module.wrapPointer(ptr, _module.PxController)")

val PxController.type: PxControllerShapeTypeEnum
    get() = PxControllerShapeTypeEnum.forValue(getType())
val PxController.actor
    get() = getActor()
val PxController.scene
    get() = getScene()

var PxController.position
    get() = getPosition()
    set(value) { setPosition(value) }
var PxController.footPosition
    get() = getFootPosition()
    set(value) { setFootPosition(value) }
var PxController.stepOffset
    get() = getStepOffset()
    set(value) { setStepOffset(value) }
var PxController.nonWalkableMode: PxControllerNonWalkableModeEnum
    get() = PxControllerNonWalkableModeEnum.forValue(getNonWalkableMode())
    set(value) { setNonWalkableMode(value.value) }
var PxController.contactOffset
    get() = getContactOffset()
    set(value) { setContactOffset(value) }
var PxController.upDirection
    get() = getUpDirection()
    set(value) { setUpDirection(value) }
var PxController.slopeLimit
    get() = getSlopeLimit()
    set(value) { setSlopeLimit(value) }
var PxController.userData
    get() = getUserData()
    set(value) { setUserData(value) }

fun PxController.setNonWalkableMode(flag: PxControllerNonWalkableModeEnum) = setNonWalkableMode(flag.value)

external interface PxControllerBehaviorCallback : JsAny

fun PxControllerBehaviorCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerBehaviorCallback = js("_module.wrapPointer(ptr, _module.PxControllerBehaviorCallback)")

external interface SimpleControllerBehaviorCallback : JsAny, DestroyableNative, PxControllerBehaviorCallback {
    /**
     * @param shape WebIDL type: [PxShape] (Const, Ref)
     * @param actor WebIDL type: [PxActor] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun getShapeBehaviorFlags(shape: PxShape, actor: PxActor): Int

    /**
     * @param controller WebIDL type: [PxController] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun getControllerBehaviorFlags(controller: PxController): Int

    /**
     * @param obstacle WebIDL type: [PxObstacle] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun getObstacleBehaviorFlags(obstacle: PxObstacle): Int

}

fun SimpleControllerBehaviorCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SimpleControllerBehaviorCallback = js("_module.wrapPointer(ptr, _module.SimpleControllerBehaviorCallback)")

external interface PxControllerBehaviorCallbackImpl : SimpleControllerBehaviorCallback {
    /**
     * param shape WebIDL type: [PxShape] (Const, Ref)
     * param actor WebIDL type: [PxActor] (Const, Ref)
     * return WebIDL type: unsigned long
     */
    var getShapeBehaviorFlags: (shape: Int, actor: Int) -> Int

    /**
     * param controller WebIDL type: [PxController] (Const, Ref)
     * return WebIDL type: unsigned long
     */
    var getControllerBehaviorFlags: (controller: Int) -> Int

    /**
     * param obstacle WebIDL type: [PxObstacle] (Const, Ref)
     * return WebIDL type: unsigned long
     */
    var getObstacleBehaviorFlags: (obstacle: Int) -> Int

}

fun PxControllerBehaviorCallbackImpl(_module: JsAny = PhysXJsLoader.physXJs): PxControllerBehaviorCallbackImpl = js("new _module.PxControllerBehaviorCallbackImpl()")

external interface PxControllerBehaviorFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxControllerBehaviorFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxControllerBehaviorFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxControllerBehaviorFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxControllerBehaviorFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxControllerBehaviorFlags = js("new _module.PxControllerBehaviorFlags(flags)")

fun PxControllerBehaviorFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerBehaviorFlags = js("_module.wrapPointer(ptr, _module.PxControllerBehaviorFlags)")

fun PxControllerBehaviorFlags.isSet(flag: PxControllerBehaviorFlagEnum) = isSet(flag.value)
fun PxControllerBehaviorFlags.raise(flag: PxControllerBehaviorFlagEnum) = raise(flag.value)
fun PxControllerBehaviorFlags.clear(flag: PxControllerBehaviorFlagEnum) = clear(flag.value)

external interface PxControllerCollisionFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxControllerCollisionFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxControllerCollisionFlags = js("new _module.PxControllerCollisionFlags(flags)")

fun PxControllerCollisionFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerCollisionFlags = js("_module.wrapPointer(ptr, _module.PxControllerCollisionFlags)")

fun PxControllerCollisionFlags.isSet(flag: PxControllerCollisionFlagEnum) = isSet(flag.value)
fun PxControllerCollisionFlags.raise(flag: PxControllerCollisionFlagEnum) = raise(flag.value)
fun PxControllerCollisionFlags.clear(flag: PxControllerCollisionFlagEnum) = clear(flag.value)

external interface PxControllerDesc : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxExtendedVec3] (Value)
     */
    var position: PxExtendedVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var upDirection: PxVec3
    /**
     * WebIDL type: float
     */
    var slopeLimit: Float
    /**
     * WebIDL type: float
     */
    var invisibleWallHeight: Float
    /**
     * WebIDL type: float
     */
    var maxJumpHeight: Float
    /**
     * WebIDL type: float
     */
    var contactOffset: Float
    /**
     * WebIDL type: float
     */
    var stepOffset: Float
    /**
     * WebIDL type: float
     */
    var density: Float
    /**
     * WebIDL type: float
     */
    var scaleCoeff: Float
    /**
     * WebIDL type: float
     */
    var volumeGrowth: Float
    /**
     * WebIDL type: [PxUserControllerHitReport]
     */
    var reportCallback: PxUserControllerHitReport
    /**
     * WebIDL type: [PxControllerBehaviorCallback]
     */
    var behaviorCallback: PxControllerBehaviorCallback
    /**
     * WebIDL type: [PxControllerNonWalkableModeEnum] (enum)
     */
    var nonWalkableMode: Int
    /**
     * WebIDL type: [PxMaterial]
     */
    var material: PxMaterial
    /**
     * WebIDL type: boolean
     */
    var registerDeletionListener: Boolean
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @return WebIDL type: [PxControllerShapeTypeEnum] (enum)
     */
    fun getType(): Int

}

fun PxControllerDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerDesc = js("_module.wrapPointer(ptr, _module.PxControllerDesc)")

val PxControllerDesc.type: PxControllerShapeTypeEnum
    get() = PxControllerShapeTypeEnum.forValue(getType())

var PxControllerDesc.nonWalkableModeEnum: PxControllerNonWalkableModeEnum
    get() = PxControllerNonWalkableModeEnum.forValue(nonWalkableMode)
    set(value) { nonWalkableMode = value.value }

external interface PxControllerFilters : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxFilterData] (Const)
     */
    var mFilterData: PxFilterData
    /**
     * WebIDL type: [PxQueryFilterCallback]
     */
    var mFilterCallback: PxQueryFilterCallback
    /**
     * WebIDL type: [PxQueryFlags] (Value)
     */
    var mFilterFlags: PxQueryFlags
    /**
     * WebIDL type: [PxControllerFilterCallback]
     */
    var mCCTFilterCallback: PxControllerFilterCallback
}

fun PxControllerFilters(_module: JsAny = PhysXJsLoader.physXJs): PxControllerFilters = js("new _module.PxControllerFilters()")

/**
 * @param filterData WebIDL type: [PxFilterData] (Const)
 */
fun PxControllerFilters(filterData: PxFilterData, _module: JsAny = PhysXJsLoader.physXJs): PxControllerFilters = js("new _module.PxControllerFilters(filterData)")

fun PxControllerFiltersFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerFilters = js("_module.wrapPointer(ptr, _module.PxControllerFilters)")

external interface PxControllerFilterCallback : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param a WebIDL type: [PxController] (Const, Ref)
     * @param b WebIDL type: [PxController] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun filter(a: PxController, b: PxController): Boolean

}

fun PxControllerFilterCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerFilterCallback = js("_module.wrapPointer(ptr, _module.PxControllerFilterCallback)")

external interface PxControllerFilterCallbackImpl : PxControllerFilterCallback {
    /**
     * param a WebIDL type: [PxController] (Const, Ref)
     * param b WebIDL type: [PxController] (Const, Ref)
     * return WebIDL type: boolean
     */
    var filter: (a: Int, b: Int) -> Boolean

}

fun PxControllerFilterCallbackImpl(_module: JsAny = PhysXJsLoader.physXJs): PxControllerFilterCallbackImpl = js("new _module.PxControllerFilterCallbackImpl()")

external interface PxControllerHit : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxController]
     */
    var controller: PxController
    /**
     * WebIDL type: [PxExtendedVec3] (Value)
     */
    var worldPos: PxExtendedVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var worldNormal: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var dir: PxVec3
    /**
     * WebIDL type: float
     */
    var length: Float
}

fun PxControllerHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerHit = js("_module.wrapPointer(ptr, _module.PxControllerHit)")

external interface PxControllerManager : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

    /**
     * @return WebIDL type: [PxScene] (Ref)
     */
    fun getScene(): PxScene

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbControllers(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxController]
     */
    fun getController(index: Int): PxController

    /**
     * @param desc WebIDL type: [PxControllerDesc] (Const, Ref)
     * @return WebIDL type: [PxController]
     */
    fun createController(desc: PxControllerDesc): PxController

    fun purgeControllers()

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbObstacleContexts(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxObstacleContext]
     */
    fun getObstacleContext(index: Int): PxObstacleContext

    /**
     * @return WebIDL type: [PxObstacleContext]
     */
    fun createObstacleContext(): PxObstacleContext

    /**
     * @param elapsedTime WebIDL type: float
     */
    fun computeInteractions(elapsedTime: Float)

    /**
     * @param flag          WebIDL type: boolean
     * @param maxEdgeLength WebIDL type: float
     */
    fun setTessellation(flag: Boolean, maxEdgeLength: Float)

    /**
     * @param flag WebIDL type: boolean
     */
    fun setOverlapRecoveryModule(flag: Boolean)

    /**
     * @param flags WebIDL type: boolean
     */
    fun setPreciseSweeps(flags: Boolean)

    /**
     * @param flag WebIDL type: boolean
     */
    fun setPreventVerticalSlidingAgainstCeiling(flag: Boolean)

    /**
     * @param shift WebIDL type: [PxVec3] (Const, Ref)
     */
    fun shiftOrigin(shift: PxVec3)

}

fun PxControllerManagerFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerManager = js("_module.wrapPointer(ptr, _module.PxControllerManager)")

val PxControllerManager.scene
    get() = getScene()
val PxControllerManager.nbControllers
    get() = getNbControllers()
val PxControllerManager.nbObstacleContexts
    get() = getNbObstacleContexts()

external interface PxControllerObstacleHit : JsAny, DestroyableNative, PxControllerHit {
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var userData: JsAny
}

fun PxControllerObstacleHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerObstacleHit = js("_module.wrapPointer(ptr, _module.PxControllerObstacleHit)")

external interface PxControllerShapeHit : JsAny, DestroyableNative, PxControllerHit {
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: unsigned long
     */
    var triangleIndex: Int
}

fun PxControllerShapeHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerShapeHit = js("_module.wrapPointer(ptr, _module.PxControllerShapeHit)")

external interface PxControllersHit : JsAny, DestroyableNative, PxControllerHit {
    /**
     * WebIDL type: [PxController]
     */
    var other: PxController
}

fun PxControllersHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllersHit = js("_module.wrapPointer(ptr, _module.PxControllersHit)")

external interface PxControllerState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var deltaXP: PxVec3
    /**
     * WebIDL type: [PxShape]
     */
    var touchedShape: PxShape
    /**
     * WebIDL type: [PxRigidActor]
     */
    var touchedActor: PxRigidActor
    /**
     * WebIDL type: unsigned long
     */
    var touchedObstacleHandle: Int
    /**
     * WebIDL type: unsigned long
     */
    var collisionFlags: Int
    /**
     * WebIDL type: boolean
     */
    var standOnAnotherCCT: Boolean
    /**
     * WebIDL type: boolean
     */
    var standOnObstacle: Boolean
    /**
     * WebIDL type: boolean
     */
    var isMovingUp: Boolean
}

fun PxControllerState(_module: JsAny = PhysXJsLoader.physXJs): PxControllerState = js("new _module.PxControllerState()")

fun PxControllerStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerState = js("_module.wrapPointer(ptr, _module.PxControllerState)")

external interface PxControllerStats : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var nbIterations: Short
    /**
     * WebIDL type: unsigned short
     */
    var nbFullUpdates: Short
    /**
     * WebIDL type: unsigned short
     */
    var nbPartialUpdates: Short
    /**
     * WebIDL type: unsigned short
     */
    var nbTessellation: Short
}

fun PxControllerStatsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxControllerStats = js("_module.wrapPointer(ptr, _module.PxControllerStats)")

external interface PxExtendedVec3 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: double
     */
    var x: Double
    /**
     * WebIDL type: double
     */
    var y: Double
    /**
     * WebIDL type: double
     */
    var z: Double
}

fun PxExtendedVec3(_module: JsAny = PhysXJsLoader.physXJs): PxExtendedVec3 = js("new _module.PxExtendedVec3()")

/**
 * @param x WebIDL type: double
 * @param y WebIDL type: double
 * @param z WebIDL type: double
 */
fun PxExtendedVec3(x: Double, y: Double, z: Double, _module: JsAny = PhysXJsLoader.physXJs): PxExtendedVec3 = js("new _module.PxExtendedVec3(x, y, z)")

fun PxExtendedVec3FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxExtendedVec3 = js("_module.wrapPointer(ptr, _module.PxExtendedVec3)")

external interface PxObstacle : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var mUserData: JsAny
    /**
     * WebIDL type: [PxExtendedVec3] (Value)
     */
    var mPos: PxExtendedVec3
    /**
     * WebIDL type: [PxQuat] (Value)
     */
    var mRot: PxQuat

    /**
     * @return WebIDL type: [PxGeometryTypeEnum] (enum)
     */
    fun getType(): Int

}

fun PxObstacleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxObstacle = js("_module.wrapPointer(ptr, _module.PxObstacle)")

val PxObstacle.type: PxGeometryTypeEnum
    get() = PxGeometryTypeEnum.forValue(getType())

external interface PxObstacleContext : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

    /**
     * @return WebIDL type: [PxControllerManager] (Ref)
     */
    fun getControllerManager(): PxControllerManager

    /**
     * @param obstacle WebIDL type: [PxObstacle] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun addObstacle(obstacle: PxObstacle): Int

    /**
     * @param handle WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun removeObstacle(handle: Int): Boolean

    /**
     * @param handle   WebIDL type: unsigned long
     * @param obstacle WebIDL type: [PxObstacle] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun updateObstacle(handle: Int, obstacle: PxObstacle): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbObstacles(): Int

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: [PxObstacle] (Const)
     */
    fun getObstacle(i: Int): PxObstacle

    /**
     * @param handle WebIDL type: unsigned long
     * @return WebIDL type: [PxObstacle] (Const)
     */
    fun getObstacleByHandle(handle: Int): PxObstacle

}

fun PxObstacleContextFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxObstacleContext = js("_module.wrapPointer(ptr, _module.PxObstacleContext)")

val PxObstacleContext.controllerManager
    get() = getControllerManager()
val PxObstacleContext.nbObstacles
    get() = getNbObstacles()

external interface PxUserControllerHitReport : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param hit WebIDL type: [PxControllerShapeHit] (Const, Ref)
     */
    fun onShapeHit(hit: PxControllerShapeHit)

    /**
     * @param hit WebIDL type: [PxControllersHit] (Const, Ref)
     */
    fun onControllerHit(hit: PxControllersHit)

    /**
     * @param hit WebIDL type: [PxControllerObstacleHit] (Const, Ref)
     */
    fun onObstacleHit(hit: PxControllerObstacleHit)

}

fun PxUserControllerHitReportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxUserControllerHitReport = js("_module.wrapPointer(ptr, _module.PxUserControllerHitReport)")

external interface PxUserControllerHitReportImpl : PxUserControllerHitReport {
    /**
     * param hit WebIDL type: [PxControllerShapeHit] (Const, Ref)
     */
    var onShapeHit: (hit: Int) -> Unit

    /**
     * param hit WebIDL type: [PxControllersHit] (Const, Ref)
     */
    var onControllerHit: (hit: Int) -> Unit

    /**
     * param hit WebIDL type: [PxControllerObstacleHit] (Const, Ref)
     */
    var onObstacleHit: (hit: Int) -> Unit

}

fun PxUserControllerHitReportImpl(_module: JsAny = PhysXJsLoader.physXJs): PxUserControllerHitReportImpl = js("new _module.PxUserControllerHitReportImpl()")

value class PxCapsuleClimbingModeEnum private constructor(val value: Int) {
    companion object {
        val eEASY: PxCapsuleClimbingModeEnum = PxCapsuleClimbingModeEnum(PxCapsuleClimbingModeEnum_eEASY(PhysXJsLoader.physXJs))
        val eCONSTRAINED: PxCapsuleClimbingModeEnum = PxCapsuleClimbingModeEnum(PxCapsuleClimbingModeEnum_eCONSTRAINED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eEASY.value -> eEASY
            eCONSTRAINED.value -> eCONSTRAINED
            else -> error("Invalid enum value $value for enum PxCapsuleClimbingModeEnum")
        }
    }
}

private fun PxCapsuleClimbingModeEnum_eEASY(module: JsAny): Int = js("module._emscripten_enum_PxCapsuleClimbingModeEnum_eEASY()")
private fun PxCapsuleClimbingModeEnum_eCONSTRAINED(module: JsAny): Int = js("module._emscripten_enum_PxCapsuleClimbingModeEnum_eCONSTRAINED()")

value class PxControllerBehaviorFlagEnum private constructor(val value: Int) {
    companion object {
        val eCCT_CAN_RIDE_ON_OBJECT: PxControllerBehaviorFlagEnum = PxControllerBehaviorFlagEnum(PxControllerBehaviorFlagEnum_eCCT_CAN_RIDE_ON_OBJECT(PhysXJsLoader.physXJs))
        val eCCT_SLIDE: PxControllerBehaviorFlagEnum = PxControllerBehaviorFlagEnum(PxControllerBehaviorFlagEnum_eCCT_SLIDE(PhysXJsLoader.physXJs))
        val eCCT_USER_DEFINED_RIDE: PxControllerBehaviorFlagEnum = PxControllerBehaviorFlagEnum(PxControllerBehaviorFlagEnum_eCCT_USER_DEFINED_RIDE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eCCT_CAN_RIDE_ON_OBJECT.value -> eCCT_CAN_RIDE_ON_OBJECT
            eCCT_SLIDE.value -> eCCT_SLIDE
            eCCT_USER_DEFINED_RIDE.value -> eCCT_USER_DEFINED_RIDE
            else -> error("Invalid enum value $value for enum PxControllerBehaviorFlagEnum")
        }
    }
}

private fun PxControllerBehaviorFlagEnum_eCCT_CAN_RIDE_ON_OBJECT(module: JsAny): Int = js("module._emscripten_enum_PxControllerBehaviorFlagEnum_eCCT_CAN_RIDE_ON_OBJECT()")
private fun PxControllerBehaviorFlagEnum_eCCT_SLIDE(module: JsAny): Int = js("module._emscripten_enum_PxControllerBehaviorFlagEnum_eCCT_SLIDE()")
private fun PxControllerBehaviorFlagEnum_eCCT_USER_DEFINED_RIDE(module: JsAny): Int = js("module._emscripten_enum_PxControllerBehaviorFlagEnum_eCCT_USER_DEFINED_RIDE()")

value class PxControllerCollisionFlagEnum private constructor(val value: Int) {
    companion object {
        val eCOLLISION_SIDES: PxControllerCollisionFlagEnum = PxControllerCollisionFlagEnum(PxControllerCollisionFlagEnum_eCOLLISION_SIDES(PhysXJsLoader.physXJs))
        val eCOLLISION_UP: PxControllerCollisionFlagEnum = PxControllerCollisionFlagEnum(PxControllerCollisionFlagEnum_eCOLLISION_UP(PhysXJsLoader.physXJs))
        val eCOLLISION_DOWN: PxControllerCollisionFlagEnum = PxControllerCollisionFlagEnum(PxControllerCollisionFlagEnum_eCOLLISION_DOWN(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eCOLLISION_SIDES.value -> eCOLLISION_SIDES
            eCOLLISION_UP.value -> eCOLLISION_UP
            eCOLLISION_DOWN.value -> eCOLLISION_DOWN
            else -> error("Invalid enum value $value for enum PxControllerCollisionFlagEnum")
        }
    }
}

private fun PxControllerCollisionFlagEnum_eCOLLISION_SIDES(module: JsAny): Int = js("module._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_SIDES()")
private fun PxControllerCollisionFlagEnum_eCOLLISION_UP(module: JsAny): Int = js("module._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_UP()")
private fun PxControllerCollisionFlagEnum_eCOLLISION_DOWN(module: JsAny): Int = js("module._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_DOWN()")

value class PxControllerNonWalkableModeEnum private constructor(val value: Int) {
    companion object {
        val ePREVENT_CLIMBING: PxControllerNonWalkableModeEnum = PxControllerNonWalkableModeEnum(PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING(PhysXJsLoader.physXJs))
        val ePREVENT_CLIMBING_AND_FORCE_SLIDING: PxControllerNonWalkableModeEnum = PxControllerNonWalkableModeEnum(PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING_AND_FORCE_SLIDING(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePREVENT_CLIMBING.value -> ePREVENT_CLIMBING
            ePREVENT_CLIMBING_AND_FORCE_SLIDING.value -> ePREVENT_CLIMBING_AND_FORCE_SLIDING
            else -> error("Invalid enum value $value for enum PxControllerNonWalkableModeEnum")
        }
    }
}

private fun PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING(module: JsAny): Int = js("module._emscripten_enum_PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING()")
private fun PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING_AND_FORCE_SLIDING(module: JsAny): Int = js("module._emscripten_enum_PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING_AND_FORCE_SLIDING()")

value class PxControllerShapeTypeEnum private constructor(val value: Int) {
    companion object {
        val eBOX: PxControllerShapeTypeEnum = PxControllerShapeTypeEnum(PxControllerShapeTypeEnum_eBOX(PhysXJsLoader.physXJs))
        val eCAPSULE: PxControllerShapeTypeEnum = PxControllerShapeTypeEnum(PxControllerShapeTypeEnum_eCAPSULE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eBOX.value -> eBOX
            eCAPSULE.value -> eCAPSULE
            else -> error("Invalid enum value $value for enum PxControllerShapeTypeEnum")
        }
    }
}

private fun PxControllerShapeTypeEnum_eBOX(module: JsAny): Int = js("module._emscripten_enum_PxControllerShapeTypeEnum_eBOX()")
private fun PxControllerShapeTypeEnum_eCAPSULE(module: JsAny): Int = js("module._emscripten_enum_PxControllerShapeTypeEnum_eCAPSULE()")

