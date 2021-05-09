/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxBoxController : PxController {
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
     */
    fun setHalfHeight(halfHeight: Float)

    /**
     * @param halfSideExtent WebIDL type: float
     */
    fun setHalfSideExtent(halfSideExtent: Float)

    /**
     * @param halfForwardExtent WebIDL type: float
     */
    fun setHalfForwardExtent(halfForwardExtent: Float)

}

var PxBoxController.halfHeight
    get() = getHalfHeight()
    set(value) { setHalfHeight(value) }
var PxBoxController.halfSideExtent
    get() = getHalfSideExtent()
    set(value) { setHalfSideExtent(value) }
var PxBoxController.halfForwardExtent
    get() = getHalfForwardExtent()
    set(value) { setHalfForwardExtent(value) }

external interface PxBoxControllerDesc : PxControllerDesc {
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

    /**
     * @return WebIDL type: boolean
     */
    override fun isValid(): Boolean

}

fun PxBoxControllerDesc(): PxBoxControllerDesc {
    fun _PxBoxControllerDesc(_module: dynamic) = js("new _module.PxBoxControllerDesc()")
    return _PxBoxControllerDesc(PhysXJsLoader.physXJs)
}

fun PxBoxControllerDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBoxObstacle : PxObstacle {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var mHalfExtents: PxVec3
}

fun PxBoxObstacle(): PxBoxObstacle {
    fun _PxBoxObstacle(_module: dynamic) = js("new _module.PxBoxObstacle()")
    return _PxBoxObstacle(PhysXJsLoader.physXJs)
}

fun PxBoxObstacle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxCapsuleController : PxController {
    /**
     * @return WebIDL type: float
     */
    fun getRadius(): Float

    /**
     * @param radius WebIDL type: float
     */
    fun setRadius(radius: Float)

    /**
     * @return WebIDL type: float
     */
    fun getHeight(): Float

    /**
     * @param height WebIDL type: float
     */
    fun setHeight(height: Float)

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

var PxCapsuleController.radius
    get() = getRadius()
    set(value) { setRadius(value) }
var PxCapsuleController.height
    get() = getHeight()
    set(value) { setHeight(value) }
var PxCapsuleController.climbingMode
    get() = getClimbingMode()
    set(value) { setClimbingMode(value) }

external interface PxCapsuleControllerDesc : PxControllerDesc {
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

    /**
     * @return WebIDL type: boolean
     */
    override fun isValid(): Boolean

}

fun PxCapsuleControllerDesc(): PxCapsuleControllerDesc {
    fun _PxCapsuleControllerDesc(_module: dynamic) = js("new _module.PxCapsuleControllerDesc()")
    return _PxCapsuleControllerDesc(PhysXJsLoader.physXJs)
}

fun PxCapsuleControllerDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxCapsuleObstacle : PxObstacle {
    /**
     * WebIDL type: float
     */
    var mHalfHeight: Float
    /**
     * WebIDL type: float
     */
    var mRadius: Float
}

fun PxCapsuleObstacle(): PxCapsuleObstacle {
    fun _PxCapsuleObstacle(_module: dynamic) = js("new _module.PxCapsuleObstacle()")
    return _PxCapsuleObstacle(PhysXJsLoader.physXJs)
}

fun PxCapsuleObstacle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxController {
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
    fun getUserData(): Any

    /**
     * @param userData WebIDL type: VoidPtr
     */
    fun setUserData(userData: Any)

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

val PxController.type
    get() = getType()
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
var PxController.nonWalkableMode
    get() = getNonWalkableMode()
    set(value) { setNonWalkableMode(value) }
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

external interface PxControllerCollisionFlags {
    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxControllerCollisionFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxControllerCollisionFlags(flags: Byte): PxControllerCollisionFlags {
    fun _PxControllerCollisionFlags(_module: dynamic, flags: Byte) = js("new _module.PxControllerCollisionFlags(flags)")
    return _PxControllerCollisionFlags(PhysXJsLoader.physXJs, flags)
}

fun PxControllerCollisionFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerDesc {
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
    var userData: Any

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @return WebIDL type: [PxControllerShapeTypeEnum] (enum)
     */
    fun getType(): Int

}

val PxControllerDesc.type
    get() = getType()

external interface PxControllerFilters {
    /**
     * WebIDL type: [PxFilterData] (Const)
     */
    var mFilterData: PxFilterData
    /**
     * WebIDL type: [PxQueryFlags] (Value)
     */
    var mFilterFlags: PxQueryFlags
}

/**
 * @param filterData WebIDL type: [PxFilterData] (Const)
 */
fun PxControllerFilters(filterData: PxFilterData?): PxControllerFilters {
    fun _PxControllerFilters(_module: dynamic, filterData: PxFilterData?) = js("new _module.PxControllerFilters(filterData)")
    return _PxControllerFilters(PhysXJsLoader.physXJs, filterData)
}

fun PxControllerFilters.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerHit {
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

fun PxControllerHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerManager {
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

val PxControllerManager.scene
    get() = getScene()
val PxControllerManager.nbControllers
    get() = getNbControllers()
val PxControllerManager.nbObstacleContexts
    get() = getNbObstacleContexts()

external interface PxControllerObstacleHit : PxControllerHit {
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var userData: Any
}

fun PxControllerObstacleHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerShapeHit : PxControllerHit {
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

fun PxControllerShapeHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllersHit : PxControllerHit {
    /**
     * WebIDL type: [PxController]
     */
    var other: PxController
}

fun PxControllersHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerState {
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

fun PxControllerState(): PxControllerState {
    fun _PxControllerState(_module: dynamic) = js("new _module.PxControllerState()")
    return _PxControllerState(PhysXJsLoader.physXJs)
}

fun PxControllerState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxControllerStats {
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

fun PxControllerStats.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxExtendedVec3 {
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

fun PxExtendedVec3(): PxExtendedVec3 {
    fun _PxExtendedVec3(_module: dynamic) = js("new _module.PxExtendedVec3()")
    return _PxExtendedVec3(PhysXJsLoader.physXJs)
}

/**
 * @param x WebIDL type: double
 * @param y WebIDL type: double
 * @param z WebIDL type: double
 */
fun PxExtendedVec3(x: Double, y: Double, z: Double): PxExtendedVec3 {
    fun _PxExtendedVec3(_module: dynamic, x: Double, y: Double, z: Double) = js("new _module.PxExtendedVec3(x, y, z)")
    return _PxExtendedVec3(PhysXJsLoader.physXJs, x, y, z)
}

fun PxExtendedVec3.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxObstacle {
    /**
     * WebIDL type: VoidPtr
     */
    var mUserData: Any
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

fun PxObstacle.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxObstacle.type
    get() = getType()

external interface PxObstacleContext {
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

fun PxObstacleContext.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxObstacleContext.controllerManager
    get() = getControllerManager()
val PxObstacleContext.nbObstacles
    get() = getNbObstacles()

external interface PxUserControllerHitReport {
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

external interface JavaUserControllerHitReport : PxUserControllerHitReport {
    /**
     * param hit WebIDL type: [PxControllerShapeHit] (Const, Ref)
     */
    var onShapeHit: (hit: PxControllerShapeHit) -> Unit

    /**
     * param hit WebIDL type: [PxControllersHit] (Const, Ref)
     */
    var onControllerHit: (hit: PxControllersHit) -> Unit

    /**
     * param hit WebIDL type: [PxControllerObstacleHit] (Const, Ref)
     */
    var onObstacleHit: (hit: PxControllerObstacleHit) -> Unit

}

fun JavaUserControllerHitReport(): JavaUserControllerHitReport {
    fun _JavaUserControllerHitReport(_module: dynamic) = js("new _module.JavaUserControllerHitReport()")
    return _JavaUserControllerHitReport(PhysXJsLoader.physXJs)
}

object PxCapsuleClimbingModeEnum {
    val eEASY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCapsuleClimbingModeEnum_eEASY()
    val eCONSTRAINED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCapsuleClimbingModeEnum_eCONSTRAINED()
}

object PxControllerCollisionFlagEnum {
    val eCOLLISION_SIDES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_SIDES()
    val eCOLLISION_UP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_UP()
    val eCOLLISION_DOWN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerCollisionFlagEnum_eCOLLISION_DOWN()
}

object PxControllerNonWalkableModeEnum {
    val ePREVENT_CLIMBING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING()
    val ePREVENT_CLIMBING_AND_FORCE_SLIDING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerNonWalkableModeEnum_ePREVENT_CLIMBING_AND_FORCE_SLIDING()
}

object PxControllerShapeTypeEnum {
    val eBOX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerShapeTypeEnum_eBOX()
    val eCAPSULE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxControllerShapeTypeEnum_eCAPSULE()
}

