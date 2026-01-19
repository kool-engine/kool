/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxGjkQueryProximityInfoResult : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var success: Boolean
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pointA: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var pointB: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var separatingAxis: PxVec3
    /**
     * WebIDL type: float
     */
    var separation: Float
}

fun PxGjkQueryProximityInfoResult(_module: JsAny = PhysXJsLoader.physXJs): PxGjkQueryProximityInfoResult = js("new _module.PxGjkQueryProximityInfoResult()")

fun PxGjkQueryProximityInfoResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGjkQueryProximityInfoResult = js("_module.wrapPointer(ptr, _module.PxGjkQueryProximityInfoResult)")

external interface PxGjkQueryRaycastResult : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var success: Boolean
    /**
     * WebIDL type: float
     */
    var t: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var n: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var p: PxVec3
}

fun PxGjkQueryRaycastResult(_module: JsAny = PhysXJsLoader.physXJs): PxGjkQueryRaycastResult = js("new _module.PxGjkQueryRaycastResult()")

fun PxGjkQueryRaycastResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGjkQueryRaycastResult = js("_module.wrapPointer(ptr, _module.PxGjkQueryRaycastResult)")

external interface PxGjkQuerySweepResult : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var success: Boolean
    /**
     * WebIDL type: float
     */
    var t: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var n: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var p: PxVec3
}

fun PxGjkQuerySweepResult(_module: JsAny = PhysXJsLoader.physXJs): PxGjkQuerySweepResult = js("new _module.PxGjkQuerySweepResult()")

fun PxGjkQuerySweepResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGjkQuerySweepResult = js("_module.wrapPointer(ptr, _module.PxGjkQuerySweepResult)")

external interface PxGjkQuery : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param a               WebIDL type: [Support] (Const, Ref)
     * @param b               WebIDL type: [Support] (Const, Ref)
     * @param poseA           WebIDL type: [PxTransform] (Const, Ref)
     * @param poseB           WebIDL type: [PxTransform] (Const, Ref)
     * @param contactDistance WebIDL type: float
     * @param toleranceLength WebIDL type: float
     * @param result          WebIDL type: [PxGjkQueryProximityInfoResult] (Ref)
     * @return WebIDL type: boolean
     */
    fun proximityInfo(a: Support, b: Support, poseA: PxTransform, poseB: PxTransform, contactDistance: Float, toleranceLength: Float, result: PxGjkQueryProximityInfoResult): Boolean

    /**
     * @param shape    WebIDL type: [Support] (Const, Ref)
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param rayStart WebIDL type: [PxVec3] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist  WebIDL type: float
     * @param result   WebIDL type: [PxGjkQueryRaycastResult] (Ref)
     * @return WebIDL type: boolean
     */
    fun raycast(shape: Support, pose: PxTransform, rayStart: PxVec3, unitDir: PxVec3, maxDist: Float, result: PxGjkQueryRaycastResult): Boolean

    /**
     * @param a     WebIDL type: [Support] (Const, Ref)
     * @param b     WebIDL type: [Support] (Const, Ref)
     * @param poseA WebIDL type: [PxTransform] (Const, Ref)
     * @param poseB WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun overlap(a: Support, b: Support, poseA: PxTransform, poseB: PxTransform): Boolean

    /**
     * @param a       WebIDL type: [Support] (Const, Ref)
     * @param b       WebIDL type: [Support] (Const, Ref)
     * @param poseA   WebIDL type: [PxTransform] (Const, Ref)
     * @param poseB   WebIDL type: [PxTransform] (Const, Ref)
     * @param unitDir WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist WebIDL type: float
     * @param result  WebIDL type: [PxGjkQuerySweepResult] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(a: Support, b: Support, poseA: PxTransform, poseB: PxTransform, unitDir: PxVec3, maxDist: Float, result: PxGjkQuerySweepResult): Boolean

}

fun PxGjkQueryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGjkQuery = js("_module.wrapPointer(ptr, _module.PxGjkQuery)")

external interface PxGjkQueryExt : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param a               WebIDL type: [Support] (Const, Ref)
     * @param b               WebIDL type: [Support] (Const, Ref)
     * @param poseA           WebIDL type: [PxTransform] (Const, Ref)
     * @param poseB           WebIDL type: [PxTransform] (Const, Ref)
     * @param contactDistance WebIDL type: float
     * @param toleranceLength WebIDL type: float
     * @param contactBuffer   WebIDL type: [PxContactBuffer] (Ref)
     * @return WebIDL type: boolean
     */
    fun generateContacts(a: Support, b: Support, poseA: PxTransform, poseB: PxTransform, contactDistance: Float, toleranceLength: Float, contactBuffer: PxContactBuffer): Boolean

}

fun PxGjkQueryExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGjkQueryExt = js("_module.wrapPointer(ptr, _module.PxGjkQueryExt)")

external interface Support : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: float
     */
    fun getMargin(): Float

    /**
     * @param dir WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun supportLocal(dir: PxVec3): PxVec3

}

fun SupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): Support = js("_module.wrapPointer(ptr, _module.Support)")

val Support.margin
    get() = getMargin()

external interface BoxSupport : JsAny, DestroyableNative, Support {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var halfExtents: PxVec3
    /**
     * WebIDL type: float
     */
    var margin: Float
}

/**
 * @param halfExtents WebIDL type: [PxVec3] (Const, Ref)
 */
fun BoxSupport(halfExtents: PxVec3, _module: JsAny = PhysXJsLoader.physXJs): BoxSupport = js("new _module.BoxSupport(halfExtents)")

/**
 * @param halfExtents WebIDL type: [PxVec3] (Const, Ref)
 * @param margin      WebIDL type: float
 */
fun BoxSupport(halfExtents: PxVec3, margin: Float, _module: JsAny = PhysXJsLoader.physXJs): BoxSupport = js("new _module.BoxSupport(halfExtents, margin)")

fun BoxSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): BoxSupport = js("_module.wrapPointer(ptr, _module.BoxSupport)")

external interface CapsuleSupport : JsAny, DestroyableNative, Support {
    /**
     * WebIDL type: float
     */
    var radius: Float
    /**
     * WebIDL type: float
     */
    var halfHeight: Float
}

/**
 * @param radius     WebIDL type: float
 * @param halfHeight WebIDL type: float
 */
fun CapsuleSupport(radius: Float, halfHeight: Float, _module: JsAny = PhysXJsLoader.physXJs): CapsuleSupport = js("new _module.CapsuleSupport(radius, halfHeight)")

fun CapsuleSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): CapsuleSupport = js("_module.wrapPointer(ptr, _module.CapsuleSupport)")

external interface ConvexGeomSupport : JsAny, DestroyableNative, Support

fun ConvexGeomSupport(_module: JsAny = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport()")

/**
 * @param geom WebIDL type: [PxGeometry] (Const, Ref)
 */
fun ConvexGeomSupport(geom: PxGeometry, _module: JsAny = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport(geom)")

/**
 * @param geom   WebIDL type: [PxGeometry] (Const, Ref)
 * @param margin WebIDL type: float
 */
fun ConvexGeomSupport(geom: PxGeometry, margin: Float, _module: JsAny = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport(geom, margin)")

fun ConvexGeomSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): ConvexGeomSupport = js("_module.wrapPointer(ptr, _module.ConvexGeomSupport)")

external interface ConvexMeshSupport : JsAny, DestroyableNative, Support {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var scale: PxVec3
    /**
     * WebIDL type: [PxQuat] (Value)
     */
    var scaleRotation: PxQuat
    /**
     * WebIDL type: float
     */
    var margin: Float
}

/**
 * @param convexMesh WebIDL type: [PxConvexMesh] (Const, Ref)
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, _module: JsAny = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh)")

/**
 * @param convexMesh WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale      WebIDL type: [PxVec3] (Const, Ref)
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, _module: JsAny = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale)")

/**
 * @param convexMesh    WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale         WebIDL type: [PxVec3] (Const, Ref)
 * @param scaleRotation WebIDL type: [PxQuat] (Const, Ref)
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, scaleRotation: PxQuat, _module: JsAny = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale, scaleRotation)")

/**
 * @param convexMesh    WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale         WebIDL type: [PxVec3] (Const, Ref)
 * @param scaleRotation WebIDL type: [PxQuat] (Const, Ref)
 * @param margin        WebIDL type: float
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, scaleRotation: PxQuat, margin: Float, _module: JsAny = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale, scaleRotation, margin)")

fun ConvexMeshSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): ConvexMeshSupport = js("_module.wrapPointer(ptr, _module.ConvexMeshSupport)")

external interface SphereSupport : JsAny, DestroyableNative, Support {
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param radius WebIDL type: float
 */
fun SphereSupport(radius: Float, _module: JsAny = PhysXJsLoader.physXJs): SphereSupport = js("new _module.SphereSupport(radius)")

fun SphereSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SphereSupport = js("_module.wrapPointer(ptr, _module.SphereSupport)")

external interface CustomSupport : JsAny, DestroyableNative, Support {
    /**
     * @return WebIDL type: float
     */
    fun getCustomMargin(): Float

    /**
     * @param dir    WebIDL type: [PxVec3] (Const, Ref)
     * @param result WebIDL type: [PxVec3] (Ref)
     */
    fun getCustomSupportLocal(dir: PxVec3, result: PxVec3)

}

fun CustomSupportFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): CustomSupport = js("_module.wrapPointer(ptr, _module.CustomSupport)")

val CustomSupport.customMargin
    get() = getCustomMargin()

external interface CustomSupportImpl : CustomSupport {
    /**
     * return WebIDL type: float
     */
    var getCustomMargin: () -> Float

    /**
     * param dir    WebIDL type: [PxVec3] (Const, Ref)
     * param result WebIDL type: [PxVec3] (Ref)
     */
    var getCustomSupportLocal: (dir: Int, result: Int) -> Unit

}

fun CustomSupportImpl(_module: JsAny = PhysXJsLoader.physXJs): CustomSupportImpl = js("new _module.CustomSupportImpl()")

external interface PxD6Joint : JsAny, DestroyableNative, PxJoint {
    /**
     * @param axis WebIDL type: [PxD6AxisEnum] (enum)
     * @param type WebIDL type: [PxD6MotionEnum] (enum)
     */
    fun setMotion(axis: Int, type: Int)

    /**
     * @param axis WebIDL type: [PxD6AxisEnum] (enum)
     * @return WebIDL type: [PxD6MotionEnum] (enum)
     */
    fun getMotion(axis: Int): Int

    /**
     * @return WebIDL type: float
     */
    fun getTwistAngle(): Float

    /**
     * @return WebIDL type: float
     */
    fun getSwingYAngle(): Float

    /**
     * @return WebIDL type: float
     */
    fun getSwingZAngle(): Float

    /**
     * @param limit WebIDL type: [PxJointLinearLimit] (Const, Ref)
     */
    fun setDistanceLimit(limit: PxJointLinearLimit)

    /**
     * @param axis  WebIDL type: [PxD6AxisEnum] (enum)
     * @param limit WebIDL type: [PxJointLinearLimitPair] (Const, Ref)
     */
    fun setLinearLimit(axis: Int, limit: PxJointLinearLimitPair)

    /**
     * @param limit WebIDL type: [PxJointAngularLimitPair] (Const, Ref)
     */
    fun setTwistLimit(limit: PxJointAngularLimitPair)

    /**
     * @param limit WebIDL type: [PxJointLimitCone] (Const, Ref)
     */
    fun setSwingLimit(limit: PxJointLimitCone)

    /**
     * @param limit WebIDL type: [PxJointLimitPyramid] (Const, Ref)
     */
    fun setPyramidSwingLimit(limit: PxJointLimitPyramid)

    /**
     * @param index WebIDL type: [PxD6DriveEnum] (enum)
     * @param drive WebIDL type: [PxD6JointDrive] (Const, Ref)
     */
    fun setDrive(index: Int, drive: PxD6JointDrive)

    /**
     * @param index WebIDL type: [PxD6DriveEnum] (enum)
     * @return WebIDL type: [PxD6JointDrive] (Value)
     */
    fun getDrive(index: Int): PxD6JointDrive

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setDrivePosition(pose: PxTransform)

    /**
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setDrivePosition(pose: PxTransform, autowake: Boolean)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getDrivePosition(): PxTransform

    /**
     * @param linear  WebIDL type: [PxVec3] (Const, Ref)
     * @param angular WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setDriveVelocity(linear: PxVec3, angular: PxVec3)

    /**
     * @param linear  WebIDL type: [PxVec3] (Ref)
     * @param angular WebIDL type: [PxVec3] (Ref)
     */
    fun getDriveVelocity(linear: PxVec3, angular: PxVec3)

}

fun PxD6JointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxD6Joint = js("_module.wrapPointer(ptr, _module.PxD6Joint)")

val PxD6Joint.twistAngle
    get() = getTwistAngle()
val PxD6Joint.swingYAngle
    get() = getSwingYAngle()
val PxD6Joint.swingZAngle
    get() = getSwingZAngle()

var PxD6Joint.drivePosition
    get() = getDrivePosition()
    set(value) { setDrivePosition(value) }

fun PxD6Joint.setMotion(axis: PxD6AxisEnum, type: PxD6MotionEnum) = setMotion(axis.value, type.value)
fun PxD6Joint.getMotion(axis: PxD6AxisEnum) = PxD6MotionEnum.forValue(getMotion(axis.value))
fun PxD6Joint.setLinearLimit(axis: PxD6AxisEnum, limit: PxJointLinearLimitPair) = setLinearLimit(axis.value, limit)
fun PxD6Joint.setDrive(index: PxD6DriveEnum, drive: PxD6JointDrive) = setDrive(index.value, drive)
fun PxD6Joint.getDrive(index: PxD6DriveEnum) = getDrive(index.value)

external interface PxD6JointDrive : JsAny, DestroyableNative, PxSpring {
    /**
     * WebIDL type: float
     */
    var forceLimit: Float
    /**
     * WebIDL type: [PxD6JointDriveFlags] (Value)
     */
    var flags: PxD6JointDriveFlags
}

fun PxD6JointDrive(_module: JsAny = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive()")

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, _module: JsAny = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit)")

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 * @param isAcceleration  WebIDL type: boolean
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, isAcceleration: Boolean, _module: JsAny = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit, isAcceleration)")

fun PxD6JointDriveFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxD6JointDrive = js("_module.wrapPointer(ptr, _module.PxD6JointDrive)")

external interface PxD6JointDriveFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxD6JointDriveFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxD6JointDriveFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxD6JointDriveFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxD6JointDriveFlags(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxD6JointDriveFlags = js("new _module.PxD6JointDriveFlags(flags)")

fun PxD6JointDriveFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxD6JointDriveFlags = js("_module.wrapPointer(ptr, _module.PxD6JointDriveFlags)")

fun PxD6JointDriveFlags.isSet(flag: PxD6JointDriveFlagEnum) = isSet(flag.value)
fun PxD6JointDriveFlags.raise(flag: PxD6JointDriveFlagEnum) = raise(flag.value)
fun PxD6JointDriveFlags.clear(flag: PxD6JointDriveFlagEnum) = clear(flag.value)

external interface PxDistanceJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @return WebIDL type: float
     */
    fun getDistance(): Float

    /**
     * @param distance WebIDL type: float
     */
    fun setMinDistance(distance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMinDistance(): Float

    /**
     * @param distance WebIDL type: float
     */
    fun setMaxDistance(distance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxDistance(): Float

    /**
     * @param tolerance WebIDL type: float
     */
    fun setTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getTolerance(): Float

    /**
     * @param stiffness WebIDL type: float
     */
    fun setStiffness(stiffness: Float)

    /**
     * @return WebIDL type: float
     */
    fun getStiffness(): Float

    /**
     * @param damping WebIDL type: float
     */
    fun setDamping(damping: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDamping(): Float

    /**
     * @param flags WebIDL type: [PxDistanceJointFlags] (Ref)
     */
    fun setDistanceJointFlags(flags: PxDistanceJointFlags)

    /**
     * @param flag  WebIDL type: [PxDistanceJointFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setDistanceJointFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxDistanceJointFlags] (Value)
     */
    fun getDistanceJointFlags(): PxDistanceJointFlags

}

fun PxDistanceJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDistanceJoint = js("_module.wrapPointer(ptr, _module.PxDistanceJoint)")

val PxDistanceJoint.distance
    get() = getDistance()

var PxDistanceJoint.minDistance
    get() = getMinDistance()
    set(value) { setMinDistance(value) }
var PxDistanceJoint.maxDistance
    get() = getMaxDistance()
    set(value) { setMaxDistance(value) }
var PxDistanceJoint.tolerance
    get() = getTolerance()
    set(value) { setTolerance(value) }
var PxDistanceJoint.stiffness
    get() = getStiffness()
    set(value) { setStiffness(value) }
var PxDistanceJoint.damping
    get() = getDamping()
    set(value) { setDamping(value) }
var PxDistanceJoint.distanceJointFlags
    get() = getDistanceJointFlags()
    set(value) { setDistanceJointFlags(value) }

fun PxDistanceJoint.setDistanceJointFlag(flag: PxDistanceJointFlagEnum, value: Boolean) = setDistanceJointFlag(flag.value, value)

external interface PxDistanceJointFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxDistanceJointFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxDistanceJointFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxDistanceJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxDistanceJointFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxDistanceJointFlags = js("new _module.PxDistanceJointFlags(flags)")

fun PxDistanceJointFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDistanceJointFlags = js("_module.wrapPointer(ptr, _module.PxDistanceJointFlags)")

fun PxDistanceJointFlags.isSet(flag: PxDistanceJointFlagEnum) = isSet(flag.value)
fun PxDistanceJointFlags.raise(flag: PxDistanceJointFlagEnum) = raise(flag.value)
fun PxDistanceJointFlags.clear(flag: PxDistanceJointFlagEnum) = clear(flag.value)

external interface PxFixedJoint : JsAny, DestroyableNative, PxJoint

fun PxFixedJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxFixedJoint = js("_module.wrapPointer(ptr, _module.PxFixedJoint)")

external interface PxGearJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @param hinge0 WebIDL type: [PxBase] (Const)
     * @param hinge1 WebIDL type: [PxBase] (Const)
     * @return WebIDL type: boolean
     */
    fun setHinges(hinge0: PxBase, hinge1: PxBase): Boolean

    /**
     * @param ratio WebIDL type: float
     */
    fun setGearRatio(ratio: Float)

    /**
     * @return WebIDL type: float
     */
    fun getGearRatio(): Float

}

fun PxGearJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGearJoint = js("_module.wrapPointer(ptr, _module.PxGearJoint)")

var PxGearJoint.gearRatio
    get() = getGearRatio()
    set(value) { setGearRatio(value) }

external interface PxJoint : JsAny, PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

    /**
     * @param actor0 WebIDL type: [PxRigidActor]
     * @param actor1 WebIDL type: [PxRigidActor]
     */
    fun setActors(actor0: PxRigidActor, actor1: PxRigidActor)

    /**
     * @param actor     WebIDL type: [PxJointActorIndexEnum] (enum)
     * @param localPose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setLocalPose(actor: Int, localPose: PxTransform)

    /**
     * @param actor WebIDL type: [PxJointActorIndexEnum] (enum)
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getLocalPose(actor: Int): PxTransform

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getRelativeTransform(): PxTransform

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getRelativeLinearVelocity(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getRelativeAngularVelocity(): PxVec3

    /**
     * @param force  WebIDL type: float
     * @param torque WebIDL type: float
     */
    fun setBreakForce(force: Float, torque: Float)

    /**
     * @param flags WebIDL type: [PxConstraintFlags] (Ref)
     */
    fun setConstraintFlags(flags: PxConstraintFlags)

    /**
     * @param flag  WebIDL type: [PxConstraintFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setConstraintFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxConstraintFlags] (Value)
     */
    fun getConstraintFlags(): PxConstraintFlags

    /**
     * @param invMassScale WebIDL type: float
     */
    fun setInvMassScale0(invMassScale: Float)

    /**
     * @return WebIDL type: float
     */
    fun getInvMassScale0(): Float

    /**
     * @param invMassScale WebIDL type: float
     */
    fun setInvMassScale1(invMassScale: Float)

    /**
     * @return WebIDL type: float
     */
    fun getInvMassScale1(): Float

    /**
     * @param invInertiaScale WebIDL type: float
     */
    fun setInvInertiaScale0(invInertiaScale: Float)

    /**
     * @return WebIDL type: float
     */
    fun getInvInertiaScale0(): Float

    /**
     * @param invInertiaScale WebIDL type: float
     */
    fun setInvInertiaScale1(invInertiaScale: Float)

    /**
     * @return WebIDL type: float
     */
    fun getInvInertiaScale1(): Float

    /**
     * @return WebIDL type: [PxConstraint]
     */
    fun getConstraint(): PxConstraint

    /**
     * @param name WebIDL type: DOMString (Const)
     */
    fun setName(name: String)

    /**
     * @return WebIDL type: DOMString (Const)
     */
    fun getName(): String

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

}

fun PxJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJoint = js("_module.wrapPointer(ptr, _module.PxJoint)")

val PxJoint.relativeTransform
    get() = getRelativeTransform()
val PxJoint.relativeLinearVelocity
    get() = getRelativeLinearVelocity()
val PxJoint.relativeAngularVelocity
    get() = getRelativeAngularVelocity()
val PxJoint.constraint
    get() = getConstraint()
val PxJoint.scene
    get() = getScene()

var PxJoint.constraintFlags
    get() = getConstraintFlags()
    set(value) { setConstraintFlags(value) }
var PxJoint.invMassScale0
    get() = getInvMassScale0()
    set(value) { setInvMassScale0(value) }
var PxJoint.invMassScale1
    get() = getInvMassScale1()
    set(value) { setInvMassScale1(value) }
var PxJoint.invInertiaScale0
    get() = getInvInertiaScale0()
    set(value) { setInvInertiaScale0(value) }
var PxJoint.invInertiaScale1
    get() = getInvInertiaScale1()
    set(value) { setInvInertiaScale1(value) }
var PxJoint.name
    get() = getName()
    set(value) { setName(value) }

fun PxJoint.setLocalPose(actor: PxJointActorIndexEnum, localPose: PxTransform) = setLocalPose(actor.value, localPose)
fun PxJoint.getLocalPose(actor: PxJointActorIndexEnum) = getLocalPose(actor.value)
fun PxJoint.setConstraintFlag(flag: PxConstraintFlagEnum, value: Boolean) = setConstraintFlag(flag.value, value)

external interface PxJointAngularLimitPair : JsAny, DestroyableNative, PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var upper: Float
    /**
     * WebIDL type: float
     */
    var lower: Float
}

/**
 * @param lowerLimit WebIDL type: float
 * @param upperLimit WebIDL type: float
 */
fun PxJointAngularLimitPair(lowerLimit: Float, upperLimit: Float, _module: JsAny = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("new _module.PxJointAngularLimitPair(lowerLimit, upperLimit)")

/**
 * @param lowerLimit WebIDL type: float
 * @param upperLimit WebIDL type: float
 * @param spring     WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointAngularLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring, _module: JsAny = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("new _module.PxJointAngularLimitPair(lowerLimit, upperLimit, spring)")

fun PxJointAngularLimitPairFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("_module.wrapPointer(ptr, _module.PxJointAngularLimitPair)")

external interface PxJointLimitCone : JsAny, DestroyableNative, PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var yAngle: Float
    /**
     * WebIDL type: float
     */
    var zAngle: Float
}

/**
 * @param yLimitAngle WebIDL type: float
 * @param zLimitAngle WebIDL type: float
 */
fun PxJointLimitCone(yLimitAngle: Float, zLimitAngle: Float, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitCone = js("new _module.PxJointLimitCone(yLimitAngle, zLimitAngle)")

/**
 * @param yLimitAngle WebIDL type: float
 * @param zLimitAngle WebIDL type: float
 * @param spring      WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitCone(yLimitAngle: Float, zLimitAngle: Float, spring: PxSpring, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitCone = js("new _module.PxJointLimitCone(yLimitAngle, zLimitAngle, spring)")

fun PxJointLimitConeFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitCone = js("_module.wrapPointer(ptr, _module.PxJointLimitCone)")

external interface PxJointLimitParameters : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var restitution: Float
    /**
     * WebIDL type: float
     */
    var bounceThreshold: Float
    /**
     * WebIDL type: float
     */
    var stiffness: Float
    /**
     * WebIDL type: float
     */
    var damping: Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isSoft(): Boolean

}

fun PxJointLimitParametersFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitParameters = js("_module.wrapPointer(ptr, _module.PxJointLimitParameters)")

external interface PxJointLimitPyramid : JsAny, DestroyableNative, PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var yAngleMin: Float
    /**
     * WebIDL type: float
     */
    var yAngleMax: Float
    /**
     * WebIDL type: float
     */
    var zAngleMin: Float
    /**
     * WebIDL type: float
     */
    var zAngleMax: Float
}

/**
 * @param yLimitAngleMin WebIDL type: float
 * @param yLimitAngleMax WebIDL type: float
 * @param zLimitAngleMin WebIDL type: float
 * @param zLimitAngleMax WebIDL type: float
 */
fun PxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("new _module.PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax)")

/**
 * @param yLimitAngleMin WebIDL type: float
 * @param yLimitAngleMax WebIDL type: float
 * @param zLimitAngleMin WebIDL type: float
 * @param zLimitAngleMax WebIDL type: float
 * @param spring         WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, spring: PxSpring, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("new _module.PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring)")

fun PxJointLimitPyramidFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("_module.wrapPointer(ptr, _module.PxJointLimitPyramid)")

external interface PxJointLinearLimit : JsAny, DestroyableNative, PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var value: Float
}

/**
 * @param extent WebIDL type: float
 * @param spring WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLinearLimit(extent: Float, spring: PxSpring, _module: JsAny = PhysXJsLoader.physXJs): PxJointLinearLimit = js("new _module.PxJointLinearLimit(extent, spring)")

fun PxJointLinearLimitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointLinearLimit = js("_module.wrapPointer(ptr, _module.PxJointLinearLimit)")

external interface PxJointLinearLimitPair : JsAny, DestroyableNative, PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var upper: Float
    /**
     * WebIDL type: float
     */
    var lower: Float
}

/**
 * @param lowerLimit WebIDL type: float
 * @param upperLimit WebIDL type: float
 * @param spring     WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLinearLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring, _module: JsAny = PhysXJsLoader.physXJs): PxJointLinearLimitPair = js("new _module.PxJointLinearLimitPair(lowerLimit, upperLimit, spring)")

fun PxJointLinearLimitPairFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxJointLinearLimitPair = js("_module.wrapPointer(ptr, _module.PxJointLinearLimitPair)")

external interface PxPrismaticJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @return WebIDL type: float
     */
    fun getPosition(): Float

    /**
     * @return WebIDL type: float
     */
    fun getVelocity(): Float

    /**
     * @param limit WebIDL type: [PxJointLinearLimitPair] (Const, Ref)
     */
    fun setLimit(limit: PxJointLinearLimitPair)

    /**
     * @param flags WebIDL type: [PxPrismaticJointFlags] (Ref)
     */
    fun setPrismaticJointFlags(flags: PxPrismaticJointFlags)

    /**
     * @param flag  WebIDL type: [PxPrismaticJointFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setPrismaticJointFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxPrismaticJointFlags] (Value)
     */
    fun getPrismaticJointFlags(): PxPrismaticJointFlags

}

fun PxPrismaticJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPrismaticJoint = js("_module.wrapPointer(ptr, _module.PxPrismaticJoint)")

val PxPrismaticJoint.position
    get() = getPosition()
val PxPrismaticJoint.velocity
    get() = getVelocity()

var PxPrismaticJoint.prismaticJointFlags
    get() = getPrismaticJointFlags()
    set(value) { setPrismaticJointFlags(value) }

fun PxPrismaticJoint.setPrismaticJointFlag(flag: PxPrismaticJointFlagEnum, value: Boolean) = setPrismaticJointFlag(flag.value, value)

external interface PxPrismaticJointFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxPrismaticJointFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxPrismaticJointFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxPrismaticJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxPrismaticJointFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxPrismaticJointFlags = js("new _module.PxPrismaticJointFlags(flags)")

fun PxPrismaticJointFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPrismaticJointFlags = js("_module.wrapPointer(ptr, _module.PxPrismaticJointFlags)")

fun PxPrismaticJointFlags.isSet(flag: PxPrismaticJointFlagEnum) = isSet(flag.value)
fun PxPrismaticJointFlags.raise(flag: PxPrismaticJointFlagEnum) = raise(flag.value)
fun PxPrismaticJointFlags.clear(flag: PxPrismaticJointFlagEnum) = clear(flag.value)

external interface PxRackAndPinionJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @param hinge     WebIDL type: [PxBase] (Const)
     * @param prismatic WebIDL type: [PxBase] (Const)
     * @return WebIDL type: boolean
     */
    fun setJoints(hinge: PxBase, prismatic: PxBase): Boolean

    /**
     * @param ratio WebIDL type: float
     */
    fun setRatio(ratio: Float)

    /**
     * @return WebIDL type: float
     */
    fun getRatio(): Float

    /**
     * @param nbRackTeeth   WebIDL type: unsigned long
     * @param nbPinionTeeth WebIDL type: unsigned long
     * @param rackLength    WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setData(nbRackTeeth: Int, nbPinionTeeth: Int, rackLength: Float): Boolean

}

fun PxRackAndPinionJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRackAndPinionJoint = js("_module.wrapPointer(ptr, _module.PxRackAndPinionJoint)")

var PxRackAndPinionJoint.ratio
    get() = getRatio()
    set(value) { setRatio(value) }

external interface PxRevoluteJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @return WebIDL type: float
     */
    fun getAngle(): Float

    /**
     * @return WebIDL type: float
     */
    fun getVelocity(): Float

    /**
     * @param limits WebIDL type: [PxJointAngularLimitPair] (Const, Ref)
     */
    fun setLimit(limits: PxJointAngularLimitPair)

    /**
     * @param velocity WebIDL type: float
     */
    fun setDriveVelocity(velocity: Float)

    /**
     * @param velocity WebIDL type: float
     * @param autowake WebIDL type: boolean
     */
    fun setDriveVelocity(velocity: Float, autowake: Boolean)

    /**
     * @return WebIDL type: float
     */
    fun getDriveVelocity(): Float

    /**
     * @param limit WebIDL type: float
     */
    fun setDriveForceLimit(limit: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDriveForceLimit(): Float

    /**
     * @param ratio WebIDL type: float
     */
    fun setDriveGearRatio(ratio: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDriveGearRatio(): Float

    /**
     * @param flags WebIDL type: [PxRevoluteJointFlags] (Ref)
     */
    fun setRevoluteJointFlags(flags: PxRevoluteJointFlags)

    /**
     * @param flag  WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setRevoluteJointFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxRevoluteJointFlags] (Value)
     */
    fun getRevoluteJointFlags(): PxRevoluteJointFlags

}

fun PxRevoluteJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRevoluteJoint = js("_module.wrapPointer(ptr, _module.PxRevoluteJoint)")

val PxRevoluteJoint.angle
    get() = getAngle()
val PxRevoluteJoint.velocity
    get() = getVelocity()

var PxRevoluteJoint.driveVelocity
    get() = getDriveVelocity()
    set(value) { setDriveVelocity(value) }
var PxRevoluteJoint.driveForceLimit
    get() = getDriveForceLimit()
    set(value) { setDriveForceLimit(value) }
var PxRevoluteJoint.driveGearRatio
    get() = getDriveGearRatio()
    set(value) { setDriveGearRatio(value) }
var PxRevoluteJoint.revoluteJointFlags
    get() = getRevoluteJointFlags()
    set(value) { setRevoluteJointFlags(value) }

fun PxRevoluteJoint.setRevoluteJointFlag(flag: PxRevoluteJointFlagEnum, value: Boolean) = setRevoluteJointFlag(flag.value, value)

external interface PxRevoluteJointFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxRevoluteJointFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxRevoluteJointFlags = js("new _module.PxRevoluteJointFlags(flags)")

fun PxRevoluteJointFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRevoluteJointFlags = js("_module.wrapPointer(ptr, _module.PxRevoluteJointFlags)")

fun PxRevoluteJointFlags.isSet(flag: PxRevoluteJointFlagEnum) = isSet(flag.value)
fun PxRevoluteJointFlags.raise(flag: PxRevoluteJointFlagEnum) = raise(flag.value)
fun PxRevoluteJointFlags.clear(flag: PxRevoluteJointFlagEnum) = clear(flag.value)

external interface PxSphericalJoint : JsAny, DestroyableNative, PxJoint {
    /**
     * @param limitCone WebIDL type: [PxJointLimitCone] (Const, Ref)
     */
    fun setLimitCone(limitCone: PxJointLimitCone)

    /**
     * @return WebIDL type: float
     */
    fun getSwingYAngle(): Float

    /**
     * @return WebIDL type: float
     */
    fun getSwingZAngle(): Float

    /**
     * @param flags WebIDL type: [PxSphericalJointFlags] (Ref)
     */
    fun setSphericalJointFlags(flags: PxSphericalJointFlags)

    /**
     * @param flag  WebIDL type: [PxSphericalJointFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setSphericalJointFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxSphericalJointFlags] (Value)
     */
    fun getSphericalJointFlags(): PxSphericalJointFlags

}

fun PxSphericalJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSphericalJoint = js("_module.wrapPointer(ptr, _module.PxSphericalJoint)")

val PxSphericalJoint.swingYAngle
    get() = getSwingYAngle()
val PxSphericalJoint.swingZAngle
    get() = getSwingZAngle()

var PxSphericalJoint.sphericalJointFlags
    get() = getSphericalJointFlags()
    set(value) { setSphericalJointFlags(value) }

fun PxSphericalJoint.setSphericalJointFlag(flag: PxSphericalJointFlagEnum, value: Boolean) = setSphericalJointFlag(flag.value, value)

external interface PxSphericalJointFlags : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxSphericalJointFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxSphericalJointFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxSphericalJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxSphericalJointFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxSphericalJointFlags = js("new _module.PxSphericalJointFlags(flags)")

fun PxSphericalJointFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSphericalJointFlags = js("_module.wrapPointer(ptr, _module.PxSphericalJointFlags)")

fun PxSphericalJointFlags.isSet(flag: PxSphericalJointFlagEnum) = isSet(flag.value)
fun PxSphericalJointFlags.raise(flag: PxSphericalJointFlagEnum) = raise(flag.value)
fun PxSphericalJointFlags.clear(flag: PxSphericalJointFlagEnum) = clear(flag.value)

external interface PxSpring : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var stiffness: Float
    /**
     * WebIDL type: float
     */
    var damping: Float
}

/**
 * @param stiffness WebIDL type: float
 * @param damping   WebIDL type: float
 */
fun PxSpring(stiffness: Float, damping: Float, _module: JsAny = PhysXJsLoader.physXJs): PxSpring = js("new _module.PxSpring(stiffness, damping)")

fun PxSpringFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSpring = js("_module.wrapPointer(ptr, _module.PxSpring)")

external interface PxExtensionTopLevelFunctions : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param sdk        WebIDL type: [PxPhysics] (Ref)
     * @param plane      WebIDL type: [PxPlane] (Const, Ref)
     * @param material   WebIDL type: [PxMaterial] (Ref)
     * @param filterData WebIDL type: [PxFilterData] (Const, Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun CreatePlane(sdk: PxPhysics, plane: PxPlane, material: PxMaterial, filterData: PxFilterData): PxRigidStatic

}

fun PxExtensionTopLevelFunctionsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxExtensionTopLevelFunctions = js("_module.wrapPointer(ptr, _module.PxExtensionTopLevelFunctions)")

external interface PxCollectionExt : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param collection WebIDL type: [PxCollection] (Ref)
     */
    fun releaseObjects(collection: PxCollection)

    /**
     * @param collection             WebIDL type: [PxCollection] (Ref)
     * @param releaseExclusiveShapes WebIDL type: boolean
     */
    fun releaseObjects(collection: PxCollection, releaseExclusiveShapes: Boolean)

    /**
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param concreteType WebIDL type: unsigned short
     */
    fun remove(collection: PxCollection, concreteType: Short)

    /**
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param concreteType WebIDL type: unsigned short
     * @param to           WebIDL type: [PxCollection]
     */
    fun remove(collection: PxCollection, concreteType: Short, to: PxCollection)

    /**
     * @param scene WebIDL type: [PxScene] (Ref)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollection(scene: PxScene): PxCollection

}

fun PxCollectionExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxCollectionExt = js("_module.wrapPointer(ptr, _module.PxCollectionExt)")

external interface PxDefaultMemoryInputData : JsAny, DestroyableNative, PxInputData {
    /**
     * @param dest  WebIDL type: VoidPtr
     * @param count WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun read(dest: JsAny, count: Int): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getLength(): Int

    /**
     * @param pos WebIDL type: unsigned long
     */
    fun seek(pos: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun tell(): Int

}

/**
 * @param data   WebIDL type: [PxU8Ptr] (Ref)
 * @param length WebIDL type: unsigned long
 */
fun PxDefaultMemoryInputData(data: PxU8Ptr, length: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDefaultMemoryInputData = js("new _module.PxDefaultMemoryInputData(data, length)")

fun PxDefaultMemoryInputDataFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDefaultMemoryInputData = js("_module.wrapPointer(ptr, _module.PxDefaultMemoryInputData)")

val PxDefaultMemoryInputData.length
    get() = getLength()

external interface PxDefaultMemoryOutputStream : JsAny, DestroyableNative, PxOutputStream {
    /**
     * @param src   WebIDL type: VoidPtr
     * @param count WebIDL type: unsigned long
     */
    fun write(src: JsAny, count: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSize(): Int

    /**
     * @return WebIDL type: VoidPtr
     */
    fun getData(): JsAny

}

fun PxDefaultMemoryOutputStream(_module: JsAny = PhysXJsLoader.physXJs): PxDefaultMemoryOutputStream = js("new _module.PxDefaultMemoryOutputStream()")

fun PxDefaultMemoryOutputStreamFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDefaultMemoryOutputStream = js("_module.wrapPointer(ptr, _module.PxDefaultMemoryOutputStream)")

val PxDefaultMemoryOutputStream.size
    get() = getSize()
val PxDefaultMemoryOutputStream.data
    get() = getData()

external interface PxMassProperties : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxMat33] (Value)
     */
    var inertiaTensor: PxMat33
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var centerOfMass: PxVec3
    /**
     * WebIDL type: float
     */
    var mass: Float

    /**
     * @param t WebIDL type: [PxVec3] (Const, Ref)
     */
    fun translate(t: PxVec3)

    /**
     * @param inertia   WebIDL type: [PxMat33] (Const, Ref)
     * @param massFrame WebIDL type: [PxQuat] (Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getMassSpaceInertia(inertia: PxMat33, massFrame: PxQuat): PxVec3

    /**
     * @param inertia WebIDL type: [PxMat33] (Const, Ref)
     * @param mass    WebIDL type: float
     * @param t       WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun translateInertia(inertia: PxMat33, mass: Float, t: PxVec3): PxMat33

    /**
     * @param inertia WebIDL type: [PxMat33] (Const, Ref)
     * @param q       WebIDL type: [PxQuat] (Const, Ref)
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun rotateInertia(inertia: PxMat33, q: PxQuat): PxMat33

    /**
     * @param inertia       WebIDL type: [PxMat33] (Const, Ref)
     * @param scaleRotation WebIDL type: [PxQuat] (Const, Ref)
     * @param scale         WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun scaleInertia(inertia: PxMat33, scaleRotation: PxQuat, scale: PxVec3): PxMat33

    /**
     * @param props      WebIDL type: [PxMassProperties] (Const)
     * @param transforms WebIDL type: [PxTransform] (Const)
     * @param count      WebIDL type: unsigned long
     * @return WebIDL type: [PxMassProperties] (Value)
     */
    fun sum(props: PxMassProperties, transforms: PxTransform, count: Int): PxMassProperties

}

fun PxMassProperties(_module: JsAny = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties()")

/**
 * @param m        WebIDL type: float
 * @param inertiaT WebIDL type: [PxMat33] (Const, Ref)
 * @param com      WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxMassProperties(m: Float, inertiaT: PxMat33, com: PxVec3, _module: JsAny = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties(m, inertiaT, com)")

/**
 * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
 */
fun PxMassProperties(geometry: PxGeometry, _module: JsAny = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties(geometry)")

fun PxMassPropertiesFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMassProperties = js("_module.wrapPointer(ptr, _module.PxMassProperties)")

external interface PxMeshOverlapUtil : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param geom     WebIDL type: [PxGeometry] (Const, Ref)
     * @param geomPose WebIDL type: [PxTransform] (Const, Ref)
     * @param meshGeom WebIDL type: [PxTriangleMeshGeometry] (Const, Ref)
     * @param meshPose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun findOverlap(geom: PxGeometry, geomPose: PxTransform, meshGeom: PxTriangleMeshGeometry, meshPose: PxTransform): Int

    /**
     * @return WebIDL type: [PxU32ConstPtr] (Value)
     */
    fun getResults(): PxU32ConstPtr

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbResults(): Int

}

fun PxMeshOverlapUtil(_module: JsAny = PhysXJsLoader.physXJs): PxMeshOverlapUtil = js("new _module.PxMeshOverlapUtil()")

fun PxMeshOverlapUtilFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMeshOverlapUtil = js("_module.wrapPointer(ptr, _module.PxMeshOverlapUtil)")

val PxMeshOverlapUtil.results
    get() = getResults()
val PxMeshOverlapUtil.nbResults
    get() = getNbResults()

external interface PxRigidActorExt : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param actor    WebIDL type: [PxRigidActor] (Ref)
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param material WebIDL type: [PxMaterial] (Const, Ref)
     * @return WebIDL type: [PxShape]
     */
    fun createExclusiveShape(actor: PxRigidActor, geometry: PxGeometry, material: PxMaterial): PxShape

    /**
     * @param actor    WebIDL type: [PxRigidActor] (Ref)
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param material WebIDL type: [PxMaterial] (Const, Ref)
     * @param flags    WebIDL type: [PxShapeFlags] (Ref)
     * @return WebIDL type: [PxShape]
     */
    fun createExclusiveShape(actor: PxRigidActor, geometry: PxGeometry, material: PxMaterial, flags: PxShapeFlags): PxShape

}

fun PxRigidActorExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidActorExt = js("_module.wrapPointer(ptr, _module.PxRigidActorExt)")

external interface PxRigidBodyExt : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param body    WebIDL type: [PxRigidBody] (Ref)
     * @param density WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun updateMassAndInertia(body: PxRigidBody, density: Float): Boolean

    /**
     * @param body          WebIDL type: [PxRigidBody] (Ref)
     * @param density       WebIDL type: float
     * @param massLocalPose WebIDL type: [PxVec3]
     * @return WebIDL type: boolean
     */
    fun updateMassAndInertia(body: PxRigidBody, density: Float, massLocalPose: PxVec3): Boolean

    /**
     * @param body                WebIDL type: [PxRigidBody] (Ref)
     * @param density             WebIDL type: float
     * @param massLocalPose       WebIDL type: [PxVec3]
     * @param includeNonSimShapes WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun updateMassAndInertia(body: PxRigidBody, density: Float, massLocalPose: PxVec3, includeNonSimShapes: Boolean): Boolean

    /**
     * @param body WebIDL type: [PxRigidBody] (Ref)
     * @param mass WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setMassAndUpdateInertia(body: PxRigidBody, mass: Float): Boolean

    /**
     * @param body          WebIDL type: [PxRigidBody] (Ref)
     * @param mass          WebIDL type: float
     * @param massLocalPose WebIDL type: [PxVec3]
     * @return WebIDL type: boolean
     */
    fun setMassAndUpdateInertia(body: PxRigidBody, mass: Float, massLocalPose: PxVec3): Boolean

    /**
     * @param body                WebIDL type: [PxRigidBody] (Ref)
     * @param mass                WebIDL type: float
     * @param massLocalPose       WebIDL type: [PxVec3]
     * @param includeNonSimShapes WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun setMassAndUpdateInertia(body: PxRigidBody, mass: Float, massLocalPose: PxVec3, includeNonSimShapes: Boolean): Boolean

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     * @param mode  WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int)

    /**
     * @param body   WebIDL type: [PxRigidBody] (Ref)
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param pos    WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     * @param wakeup WebIDL type: boolean
     */
    fun addForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int, wakeup: Boolean)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     * @param mode  WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int)

    /**
     * @param body   WebIDL type: [PxRigidBody] (Ref)
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param pos    WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     * @param wakeup WebIDL type: boolean
     */
    fun addForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int, wakeup: Boolean)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addLocalForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     * @param mode  WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addLocalForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int)

    /**
     * @param body   WebIDL type: [PxRigidBody] (Ref)
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param pos    WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     * @param wakeup WebIDL type: boolean
     */
    fun addLocalForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int, wakeup: Boolean)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addLocalForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3)

    /**
     * @param body  WebIDL type: [PxRigidBody] (Ref)
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param pos   WebIDL type: [PxVec3] (Const, Ref)
     * @param mode  WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addLocalForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int)

    /**
     * @param body   WebIDL type: [PxRigidBody] (Ref)
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param pos    WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     * @param wakeup WebIDL type: boolean
     */
    fun addLocalForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: Int, wakeup: Boolean)

    /**
     * @param body WebIDL type: [PxRigidBody] (Const, Ref)
     * @param pos  WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getVelocityAtPos(body: PxRigidBody, pos: PxVec3): PxVec3

    /**
     * @param body WebIDL type: [PxRigidBody] (Const, Ref)
     * @param pos  WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getLocalVelocityAtLocalPos(body: PxRigidBody, pos: PxVec3): PxVec3

    /**
     * @param body WebIDL type: [PxRigidBody] (Const, Ref)
     * @param pos  WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getVelocityAtOffset(body: PxRigidBody, pos: PxVec3): PxVec3

    /**
     * @param body                 WebIDL type: [PxRigidBody] (Const, Ref)
     * @param impulsiveForce       WebIDL type: [PxVec3] (Const, Ref)
     * @param impulsiveTorque      WebIDL type: [PxVec3] (Const, Ref)
     * @param deltaLinearVelocity  WebIDL type: [PxVec3] (Ref)
     * @param deltaAngularVelocity WebIDL type: [PxVec3] (Ref)
     */
    fun computeVelocityDeltaFromImpulse(body: PxRigidBody, impulsiveForce: PxVec3, impulsiveTorque: PxVec3, deltaLinearVelocity: PxVec3, deltaAngularVelocity: PxVec3)

    /**
     * @param body                 WebIDL type: [PxRigidBody] (Const, Ref)
     * @param globalPose           WebIDL type: [PxTransform] (Const, Ref)
     * @param point                WebIDL type: [PxVec3] (Const, Ref)
     * @param impulse              WebIDL type: [PxVec3] (Const, Ref)
     * @param invMassScale         WebIDL type: float
     * @param invInertiaScale      WebIDL type: float
     * @param deltaLinearVelocity  WebIDL type: [PxVec3] (Ref)
     * @param deltaAngularVelocity WebIDL type: [PxVec3] (Ref)
     */
    fun computeVelocityDeltaFromImpulse(body: PxRigidBody, globalPose: PxTransform, point: PxVec3, impulse: PxVec3, invMassScale: Float, invInertiaScale: Float, deltaLinearVelocity: PxVec3, deltaAngularVelocity: PxVec3)

    /**
     * @param body            WebIDL type: [PxRigidBody] (Const, Ref)
     * @param globalPose      WebIDL type: [PxTransform] (Const, Ref)
     * @param point           WebIDL type: [PxVec3] (Const, Ref)
     * @param impulse         WebIDL type: [PxVec3] (Const, Ref)
     * @param invMassScale    WebIDL type: float
     * @param invInertiaScale WebIDL type: float
     * @param linearImpulse   WebIDL type: [PxVec3] (Ref)
     * @param angularImpulse  WebIDL type: [PxVec3] (Ref)
     */
    fun computeLinearAngularImpulse(body: PxRigidBody, globalPose: PxTransform, point: PxVec3, impulse: PxVec3, invMassScale: Float, invInertiaScale: Float, linearImpulse: PxVec3, angularImpulse: PxVec3)

}

fun PxRigidBodyExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidBodyExt = js("_module.wrapPointer(ptr, _module.PxRigidBodyExt)")

fun PxRigidBodyExt.addForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum) = addForceAtPos(body, force, pos, mode.value)
fun PxRigidBodyExt.addForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum, wakeup: Boolean) = addForceAtPos(body, force, pos, mode.value, wakeup)
fun PxRigidBodyExt.addForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum) = addForceAtLocalPos(body, force, pos, mode.value)
fun PxRigidBodyExt.addForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum, wakeup: Boolean) = addForceAtLocalPos(body, force, pos, mode.value, wakeup)
fun PxRigidBodyExt.addLocalForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum) = addLocalForceAtPos(body, force, pos, mode.value)
fun PxRigidBodyExt.addLocalForceAtPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum, wakeup: Boolean) = addLocalForceAtPos(body, force, pos, mode.value, wakeup)
fun PxRigidBodyExt.addLocalForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum) = addLocalForceAtLocalPos(body, force, pos, mode.value)
fun PxRigidBodyExt.addLocalForceAtLocalPos(body: PxRigidBody, force: PxVec3, pos: PxVec3, mode: PxForceModeEnum, wakeup: Boolean) = addLocalForceAtLocalPos(body, force, pos, mode.value, wakeup)

external interface PxSerialization : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param collection WebIDL type: [PxCollection] (Ref)
     * @param sr         WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: boolean
     */
    fun isSerializable(collection: PxCollection, sr: PxSerializationRegistry): Boolean

    /**
     * @param collection         WebIDL type: [PxCollection] (Ref)
     * @param sr                 WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalReferences WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: boolean
     */
    fun isSerializable(collection: PxCollection, sr: PxSerializationRegistry, externalReferences: PxCollection): Boolean

    /**
     * @param collection WebIDL type: [PxCollection] (Ref)
     * @param sr         WebIDL type: [PxSerializationRegistry] (Ref)
     */
    fun complete(collection: PxCollection, sr: PxSerializationRegistry)

    /**
     * @param collection WebIDL type: [PxCollection] (Ref)
     * @param sr         WebIDL type: [PxSerializationRegistry] (Ref)
     * @param exceptFor  WebIDL type: [PxCollection] (Const)
     */
    fun complete(collection: PxCollection, sr: PxSerializationRegistry, exceptFor: PxCollection)

    /**
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param exceptFor    WebIDL type: [PxCollection] (Const)
     * @param followJoints WebIDL type: boolean
     */
    fun complete(collection: PxCollection, sr: PxSerializationRegistry, exceptFor: PxCollection, followJoints: Boolean)

    /**
     * @param collection WebIDL type: [PxCollection] (Ref)
     * @param base       WebIDL type: unsigned long long
     */
    fun createSerialObjectIds(collection: PxCollection, base: Long)

    /**
     * @param inputData WebIDL type: [PxInputData] (Ref)
     * @param params    WebIDL type: [PxCookingParams] (Const, Ref)
     * @param sr        WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromXml(inputData: PxInputData, params: PxCookingParams, sr: PxSerializationRegistry): PxCollection

    /**
     * @param inputData    WebIDL type: [PxInputData] (Ref)
     * @param params       WebIDL type: [PxCookingParams] (Const, Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromXml(inputData: PxInputData, params: PxCookingParams, sr: PxSerializationRegistry, externalRefs: PxCollection): PxCollection

    /**
     * @param memBlock WebIDL type: VoidPtr
     * @param sr       WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromBinary(memBlock: JsAny, sr: PxSerializationRegistry): PxCollection

    /**
     * @param memBlock     WebIDL type: VoidPtr
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromBinary(memBlock: JsAny, sr: PxSerializationRegistry, externalRefs: PxCollection): PxCollection

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToXml(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param params       WebIDL type: [PxCookingParams] (Const)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToXml(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, params: PxCookingParams): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param params       WebIDL type: [PxCookingParams] (Const)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToXml(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, params: PxCookingParams, externalRefs: PxCollection): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToBinary(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToBinary(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, externalRefs: PxCollection): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @param exportNames  WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToBinary(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, externalRefs: PxCollection, exportNames: Boolean): Boolean

    /**
     * @param physics WebIDL type: [PxPhysics] (Ref)
     * @return WebIDL type: [PxSerializationRegistry]
     */
    fun createSerializationRegistry(physics: PxPhysics): PxSerializationRegistry

}

fun PxSerializationFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSerialization = js("_module.wrapPointer(ptr, _module.PxSerialization)")

external interface PxSerializationRegistry : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

}

fun PxSerializationRegistryFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSerializationRegistry = js("_module.wrapPointer(ptr, _module.PxSerializationRegistry)")

value class PxD6AxisEnum private constructor(val value: Int) {
    companion object {
        val eX: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eX(PhysXJsLoader.physXJs))
        val eY: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eY(PhysXJsLoader.physXJs))
        val eZ: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eZ(PhysXJsLoader.physXJs))
        val eTWIST: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eTWIST(PhysXJsLoader.physXJs))
        val eSWING1: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eSWING1(PhysXJsLoader.physXJs))
        val eSWING2: PxD6AxisEnum = PxD6AxisEnum(PxD6AxisEnum_eSWING2(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eX.value -> eX
            eY.value -> eY
            eZ.value -> eZ
            eTWIST.value -> eTWIST
            eSWING1.value -> eSWING1
            eSWING2.value -> eSWING2
            else -> error("Invalid enum value $value for enum PxD6AxisEnum")
        }
    }
}

private fun PxD6AxisEnum_eX(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eX()")
private fun PxD6AxisEnum_eY(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eY()")
private fun PxD6AxisEnum_eZ(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eZ()")
private fun PxD6AxisEnum_eTWIST(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eTWIST()")
private fun PxD6AxisEnum_eSWING1(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eSWING1()")
private fun PxD6AxisEnum_eSWING2(module: JsAny): Int = js("module._emscripten_enum_PxD6AxisEnum_eSWING2()")

value class PxD6DriveEnum private constructor(val value: Int) {
    companion object {
        val eX: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eX(PhysXJsLoader.physXJs))
        val eY: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eY(PhysXJsLoader.physXJs))
        val eZ: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eZ(PhysXJsLoader.physXJs))
        val eSWING: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eSWING(PhysXJsLoader.physXJs))
        val eTWIST: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eTWIST(PhysXJsLoader.physXJs))
        val eSLERP: PxD6DriveEnum = PxD6DriveEnum(PxD6DriveEnum_eSLERP(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eX.value -> eX
            eY.value -> eY
            eZ.value -> eZ
            eSWING.value -> eSWING
            eTWIST.value -> eTWIST
            eSLERP.value -> eSLERP
            else -> error("Invalid enum value $value for enum PxD6DriveEnum")
        }
    }
}

private fun PxD6DriveEnum_eX(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eX()")
private fun PxD6DriveEnum_eY(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eY()")
private fun PxD6DriveEnum_eZ(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eZ()")
private fun PxD6DriveEnum_eSWING(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eSWING()")
private fun PxD6DriveEnum_eTWIST(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eTWIST()")
private fun PxD6DriveEnum_eSLERP(module: JsAny): Int = js("module._emscripten_enum_PxD6DriveEnum_eSLERP()")

value class PxD6JointDriveFlagEnum private constructor(val value: Int) {
    companion object {
        val eACCELERATION: PxD6JointDriveFlagEnum = PxD6JointDriveFlagEnum(PxD6JointDriveFlagEnum_eACCELERATION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eACCELERATION.value -> eACCELERATION
            else -> error("Invalid enum value $value for enum PxD6JointDriveFlagEnum")
        }
    }
}

private fun PxD6JointDriveFlagEnum_eACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxD6JointDriveFlagEnum_eACCELERATION()")

value class PxD6MotionEnum private constructor(val value: Int) {
    companion object {
        val eLOCKED: PxD6MotionEnum = PxD6MotionEnum(PxD6MotionEnum_eLOCKED(PhysXJsLoader.physXJs))
        val eLIMITED: PxD6MotionEnum = PxD6MotionEnum(PxD6MotionEnum_eLIMITED(PhysXJsLoader.physXJs))
        val eFREE: PxD6MotionEnum = PxD6MotionEnum(PxD6MotionEnum_eFREE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLOCKED.value -> eLOCKED
            eLIMITED.value -> eLIMITED
            eFREE.value -> eFREE
            else -> error("Invalid enum value $value for enum PxD6MotionEnum")
        }
    }
}

private fun PxD6MotionEnum_eLOCKED(module: JsAny): Int = js("module._emscripten_enum_PxD6MotionEnum_eLOCKED()")
private fun PxD6MotionEnum_eLIMITED(module: JsAny): Int = js("module._emscripten_enum_PxD6MotionEnum_eLIMITED()")
private fun PxD6MotionEnum_eFREE(module: JsAny): Int = js("module._emscripten_enum_PxD6MotionEnum_eFREE()")

value class PxDistanceJointFlagEnum private constructor(val value: Int) {
    companion object {
        val eMAX_DISTANCE_ENABLED: PxDistanceJointFlagEnum = PxDistanceJointFlagEnum(PxDistanceJointFlagEnum_eMAX_DISTANCE_ENABLED(PhysXJsLoader.physXJs))
        val eMIN_DISTANCE_ENABLED: PxDistanceJointFlagEnum = PxDistanceJointFlagEnum(PxDistanceJointFlagEnum_eMIN_DISTANCE_ENABLED(PhysXJsLoader.physXJs))
        val eSPRING_ENABLED: PxDistanceJointFlagEnum = PxDistanceJointFlagEnum(PxDistanceJointFlagEnum_eSPRING_ENABLED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eMAX_DISTANCE_ENABLED.value -> eMAX_DISTANCE_ENABLED
            eMIN_DISTANCE_ENABLED.value -> eMIN_DISTANCE_ENABLED
            eSPRING_ENABLED.value -> eSPRING_ENABLED
            else -> error("Invalid enum value $value for enum PxDistanceJointFlagEnum")
        }
    }
}

private fun PxDistanceJointFlagEnum_eMAX_DISTANCE_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxDistanceJointFlagEnum_eMAX_DISTANCE_ENABLED()")
private fun PxDistanceJointFlagEnum_eMIN_DISTANCE_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxDistanceJointFlagEnum_eMIN_DISTANCE_ENABLED()")
private fun PxDistanceJointFlagEnum_eSPRING_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxDistanceJointFlagEnum_eSPRING_ENABLED()")

value class PxJointActorIndexEnum private constructor(val value: Int) {
    companion object {
        val eACTOR0: PxJointActorIndexEnum = PxJointActorIndexEnum(PxJointActorIndexEnum_eACTOR0(PhysXJsLoader.physXJs))
        val eACTOR1: PxJointActorIndexEnum = PxJointActorIndexEnum(PxJointActorIndexEnum_eACTOR1(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eACTOR0.value -> eACTOR0
            eACTOR1.value -> eACTOR1
            else -> error("Invalid enum value $value for enum PxJointActorIndexEnum")
        }
    }
}

private fun PxJointActorIndexEnum_eACTOR0(module: JsAny): Int = js("module._emscripten_enum_PxJointActorIndexEnum_eACTOR0()")
private fun PxJointActorIndexEnum_eACTOR1(module: JsAny): Int = js("module._emscripten_enum_PxJointActorIndexEnum_eACTOR1()")

value class PxPrismaticJointFlagEnum private constructor(val value: Int) {
    companion object {
        val eLIMIT_ENABLED: PxPrismaticJointFlagEnum = PxPrismaticJointFlagEnum(PxPrismaticJointFlagEnum_eLIMIT_ENABLED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLIMIT_ENABLED.value -> eLIMIT_ENABLED
            else -> error("Invalid enum value $value for enum PxPrismaticJointFlagEnum")
        }
    }
}

private fun PxPrismaticJointFlagEnum_eLIMIT_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxPrismaticJointFlagEnum_eLIMIT_ENABLED()")

value class PxRevoluteJointFlagEnum private constructor(val value: Int) {
    companion object {
        val eLIMIT_ENABLED: PxRevoluteJointFlagEnum = PxRevoluteJointFlagEnum(PxRevoluteJointFlagEnum_eLIMIT_ENABLED(PhysXJsLoader.physXJs))
        val eDRIVE_ENABLED: PxRevoluteJointFlagEnum = PxRevoluteJointFlagEnum(PxRevoluteJointFlagEnum_eDRIVE_ENABLED(PhysXJsLoader.physXJs))
        val eDRIVE_FREESPIN: PxRevoluteJointFlagEnum = PxRevoluteJointFlagEnum(PxRevoluteJointFlagEnum_eDRIVE_FREESPIN(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLIMIT_ENABLED.value -> eLIMIT_ENABLED
            eDRIVE_ENABLED.value -> eDRIVE_ENABLED
            eDRIVE_FREESPIN.value -> eDRIVE_FREESPIN
            else -> error("Invalid enum value $value for enum PxRevoluteJointFlagEnum")
        }
    }
}

private fun PxRevoluteJointFlagEnum_eLIMIT_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED()")
private fun PxRevoluteJointFlagEnum_eDRIVE_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED()")
private fun PxRevoluteJointFlagEnum_eDRIVE_FREESPIN(module: JsAny): Int = js("module._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN()")

value class PxSphericalJointFlagEnum private constructor(val value: Int) {
    companion object {
        val eLIMIT_ENABLED: PxSphericalJointFlagEnum = PxSphericalJointFlagEnum(PxSphericalJointFlagEnum_eLIMIT_ENABLED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLIMIT_ENABLED.value -> eLIMIT_ENABLED
            else -> error("Invalid enum value $value for enum PxSphericalJointFlagEnum")
        }
    }
}

private fun PxSphericalJointFlagEnum_eLIMIT_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxSphericalJointFlagEnum_eLIMIT_ENABLED()")

