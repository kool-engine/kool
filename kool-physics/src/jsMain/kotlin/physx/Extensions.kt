/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxGjkQueryProximityInfoResult {
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

fun PxGjkQueryProximityInfoResult(_module: dynamic = PhysXJsLoader.physXJs): PxGjkQueryProximityInfoResult = js("new _module.PxGjkQueryProximityInfoResult()")

fun PxGjkQueryProximityInfoResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGjkQueryProximityInfoResult = js("_module.wrapPointer(ptr, _module.PxGjkQueryProximityInfoResult)")

fun PxGjkQueryProximityInfoResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGjkQueryRaycastResult {
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

fun PxGjkQueryRaycastResult(_module: dynamic = PhysXJsLoader.physXJs): PxGjkQueryRaycastResult = js("new _module.PxGjkQueryRaycastResult()")

fun PxGjkQueryRaycastResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGjkQueryRaycastResult = js("_module.wrapPointer(ptr, _module.PxGjkQueryRaycastResult)")

fun PxGjkQueryRaycastResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGjkQuerySweepResult {
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

fun PxGjkQuerySweepResult(_module: dynamic = PhysXJsLoader.physXJs): PxGjkQuerySweepResult = js("new _module.PxGjkQuerySweepResult()")

fun PxGjkQuerySweepResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGjkQuerySweepResult = js("_module.wrapPointer(ptr, _module.PxGjkQuerySweepResult)")

fun PxGjkQuerySweepResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGjkQuery {
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

fun PxGjkQueryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGjkQuery = js("_module.wrapPointer(ptr, _module.PxGjkQuery)")

fun PxGjkQuery.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGjkQueryExt {
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

fun PxGjkQueryExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGjkQueryExt = js("_module.wrapPointer(ptr, _module.PxGjkQueryExt)")

fun PxGjkQueryExt.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface Support {
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

fun SupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): Support = js("_module.wrapPointer(ptr, _module.Support)")

fun Support.destroy() {
    PhysXJsLoader.destroy(this)
}

val Support.margin
    get() = getMargin()

external interface BoxSupport : Support {
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
fun BoxSupport(halfExtents: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): BoxSupport = js("new _module.BoxSupport(halfExtents)")

/**
 * @param halfExtents WebIDL type: [PxVec3] (Const, Ref)
 * @param margin      WebIDL type: float
 */
fun BoxSupport(halfExtents: PxVec3, margin: Float, _module: dynamic = PhysXJsLoader.physXJs): BoxSupport = js("new _module.BoxSupport(halfExtents, margin)")

fun BoxSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): BoxSupport = js("_module.wrapPointer(ptr, _module.BoxSupport)")

fun BoxSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface CapsuleSupport : Support {
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
fun CapsuleSupport(radius: Float, halfHeight: Float, _module: dynamic = PhysXJsLoader.physXJs): CapsuleSupport = js("new _module.CapsuleSupport(radius, halfHeight)")

fun CapsuleSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): CapsuleSupport = js("_module.wrapPointer(ptr, _module.CapsuleSupport)")

fun CapsuleSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface ConvexGeomSupport : Support

fun ConvexGeomSupport(_module: dynamic = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport()")

/**
 * @param geom WebIDL type: [PxGeometry] (Const, Ref)
 */
fun ConvexGeomSupport(geom: PxGeometry, _module: dynamic = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport(geom)")

/**
 * @param geom   WebIDL type: [PxGeometry] (Const, Ref)
 * @param margin WebIDL type: float
 */
fun ConvexGeomSupport(geom: PxGeometry, margin: Float, _module: dynamic = PhysXJsLoader.physXJs): ConvexGeomSupport = js("new _module.ConvexGeomSupport(geom, margin)")

fun ConvexGeomSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): ConvexGeomSupport = js("_module.wrapPointer(ptr, _module.ConvexGeomSupport)")

fun ConvexGeomSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface ConvexMeshSupport : Support {
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
fun ConvexMeshSupport(convexMesh: PxConvexMesh, _module: dynamic = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh)")

/**
 * @param convexMesh WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale      WebIDL type: [PxVec3] (Const, Ref)
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale)")

/**
 * @param convexMesh    WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale         WebIDL type: [PxVec3] (Const, Ref)
 * @param scaleRotation WebIDL type: [PxQuat] (Const, Ref)
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, scaleRotation: PxQuat, _module: dynamic = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale, scaleRotation)")

/**
 * @param convexMesh    WebIDL type: [PxConvexMesh] (Const, Ref)
 * @param scale         WebIDL type: [PxVec3] (Const, Ref)
 * @param scaleRotation WebIDL type: [PxQuat] (Const, Ref)
 * @param margin        WebIDL type: float
 */
fun ConvexMeshSupport(convexMesh: PxConvexMesh, scale: PxVec3, scaleRotation: PxQuat, margin: Float, _module: dynamic = PhysXJsLoader.physXJs): ConvexMeshSupport = js("new _module.ConvexMeshSupport(convexMesh, scale, scaleRotation, margin)")

fun ConvexMeshSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): ConvexMeshSupport = js("_module.wrapPointer(ptr, _module.ConvexMeshSupport)")

fun ConvexMeshSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface SphereSupport : Support {
    /**
     * WebIDL type: float
     */
    var radius: Float
}

/**
 * @param radius WebIDL type: float
 */
fun SphereSupport(radius: Float, _module: dynamic = PhysXJsLoader.physXJs): SphereSupport = js("new _module.SphereSupport(radius)")

fun SphereSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): SphereSupport = js("_module.wrapPointer(ptr, _module.SphereSupport)")

fun SphereSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface CustomSupport : Support {
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

fun CustomSupportFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): CustomSupport = js("_module.wrapPointer(ptr, _module.CustomSupport)")

fun CustomSupport.destroy() {
    PhysXJsLoader.destroy(this)
}

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

fun CustomSupportImpl(_module: dynamic = PhysXJsLoader.physXJs): CustomSupportImpl = js("new _module.CustomSupportImpl()")

external interface PxD6Joint : PxJoint {
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

fun PxD6JointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxD6Joint = js("_module.wrapPointer(ptr, _module.PxD6Joint)")

fun PxD6Joint.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxD6Joint.twistAngle
    get() = getTwistAngle()
val PxD6Joint.swingYAngle
    get() = getSwingYAngle()
val PxD6Joint.swingZAngle
    get() = getSwingZAngle()

var PxD6Joint.drivePosition
    get() = getDrivePosition()
    set(value) { setDrivePosition(value) }

external interface PxD6JointDrive : PxSpring {
    /**
     * WebIDL type: float
     */
    var forceLimit: Float
    /**
     * WebIDL type: [PxD6JointDriveFlags] (Value)
     */
    var flags: PxD6JointDriveFlags
}

fun PxD6JointDrive(_module: dynamic = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive()")

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, _module: dynamic = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit)")

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 * @param isAcceleration  WebIDL type: boolean
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, isAcceleration: Boolean, _module: dynamic = PhysXJsLoader.physXJs): PxD6JointDrive = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit, isAcceleration)")

fun PxD6JointDriveFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxD6JointDrive = js("_module.wrapPointer(ptr, _module.PxD6JointDrive)")

fun PxD6JointDrive.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxD6JointDriveFlags {
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
fun PxD6JointDriveFlags(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxD6JointDriveFlags = js("new _module.PxD6JointDriveFlags(flags)")

fun PxD6JointDriveFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxD6JointDriveFlags = js("_module.wrapPointer(ptr, _module.PxD6JointDriveFlags)")

fun PxD6JointDriveFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDistanceJoint : PxJoint {
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

fun PxDistanceJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDistanceJoint = js("_module.wrapPointer(ptr, _module.PxDistanceJoint)")

fun PxDistanceJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

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

external interface PxDistanceJointFlags {
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
fun PxDistanceJointFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxDistanceJointFlags = js("new _module.PxDistanceJointFlags(flags)")

fun PxDistanceJointFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDistanceJointFlags = js("_module.wrapPointer(ptr, _module.PxDistanceJointFlags)")

fun PxDistanceJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxFixedJoint : PxJoint

fun PxFixedJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxFixedJoint = js("_module.wrapPointer(ptr, _module.PxFixedJoint)")

fun PxFixedJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGearJoint : PxJoint {
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

fun PxGearJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGearJoint = js("_module.wrapPointer(ptr, _module.PxGearJoint)")

fun PxGearJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxGearJoint.gearRatio
    get() = getGearRatio()
    set(value) { setGearRatio(value) }

external interface PxJoint : PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

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

fun PxJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJoint = js("_module.wrapPointer(ptr, _module.PxJoint)")

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
var PxJoint.name
    get() = getName()
    set(value) { setName(value) }

external interface PxJointAngularLimitPair : PxJointLimitParameters {
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
fun PxJointAngularLimitPair(lowerLimit: Float, upperLimit: Float, _module: dynamic = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("new _module.PxJointAngularLimitPair(lowerLimit, upperLimit)")

/**
 * @param lowerLimit WebIDL type: float
 * @param upperLimit WebIDL type: float
 * @param spring     WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointAngularLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring, _module: dynamic = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("new _module.PxJointAngularLimitPair(lowerLimit, upperLimit, spring)")

fun PxJointAngularLimitPairFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointAngularLimitPair = js("_module.wrapPointer(ptr, _module.PxJointAngularLimitPair)")

fun PxJointAngularLimitPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJointLimitCone : PxJointLimitParameters {
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
fun PxJointLimitCone(yLimitAngle: Float, zLimitAngle: Float, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitCone = js("new _module.PxJointLimitCone(yLimitAngle, zLimitAngle)")

/**
 * @param yLimitAngle WebIDL type: float
 * @param zLimitAngle WebIDL type: float
 * @param spring      WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitCone(yLimitAngle: Float, zLimitAngle: Float, spring: PxSpring, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitCone = js("new _module.PxJointLimitCone(yLimitAngle, zLimitAngle, spring)")

fun PxJointLimitConeFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitCone = js("_module.wrapPointer(ptr, _module.PxJointLimitCone)")

fun PxJointLimitCone.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJointLimitParameters {
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

fun PxJointLimitParametersFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitParameters = js("_module.wrapPointer(ptr, _module.PxJointLimitParameters)")

external interface PxJointLimitPyramid : PxJointLimitParameters {
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
fun PxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("new _module.PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax)")

/**
 * @param yLimitAngleMin WebIDL type: float
 * @param yLimitAngleMax WebIDL type: float
 * @param zLimitAngleMin WebIDL type: float
 * @param zLimitAngleMax WebIDL type: float
 * @param spring         WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, spring: PxSpring, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("new _module.PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring)")

fun PxJointLimitPyramidFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointLimitPyramid = js("_module.wrapPointer(ptr, _module.PxJointLimitPyramid)")

fun PxJointLimitPyramid.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJointLinearLimit : PxJointLimitParameters {
    /**
     * WebIDL type: float
     */
    var value: Float
}

/**
 * @param extent WebIDL type: float
 * @param spring WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLinearLimit(extent: Float, spring: PxSpring, _module: dynamic = PhysXJsLoader.physXJs): PxJointLinearLimit = js("new _module.PxJointLinearLimit(extent, spring)")

fun PxJointLinearLimitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointLinearLimit = js("_module.wrapPointer(ptr, _module.PxJointLinearLimit)")

fun PxJointLinearLimit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJointLinearLimitPair : PxJointLimitParameters {
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
fun PxJointLinearLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring, _module: dynamic = PhysXJsLoader.physXJs): PxJointLinearLimitPair = js("new _module.PxJointLinearLimitPair(lowerLimit, upperLimit, spring)")

fun PxJointLinearLimitPairFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxJointLinearLimitPair = js("_module.wrapPointer(ptr, _module.PxJointLinearLimitPair)")

fun PxJointLinearLimitPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPrismaticJoint : PxJoint {
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

fun PxPrismaticJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPrismaticJoint = js("_module.wrapPointer(ptr, _module.PxPrismaticJoint)")

fun PxPrismaticJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxPrismaticJoint.position
    get() = getPosition()
val PxPrismaticJoint.velocity
    get() = getVelocity()

var PxPrismaticJoint.prismaticJointFlags
    get() = getPrismaticJointFlags()
    set(value) { setPrismaticJointFlags(value) }

external interface PxPrismaticJointFlags {
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
fun PxPrismaticJointFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxPrismaticJointFlags = js("new _module.PxPrismaticJointFlags(flags)")

fun PxPrismaticJointFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPrismaticJointFlags = js("_module.wrapPointer(ptr, _module.PxPrismaticJointFlags)")

fun PxPrismaticJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRackAndPinionJoint : PxJoint {
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

fun PxRackAndPinionJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRackAndPinionJoint = js("_module.wrapPointer(ptr, _module.PxRackAndPinionJoint)")

fun PxRackAndPinionJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxRackAndPinionJoint.ratio
    get() = getRatio()
    set(value) { setRatio(value) }

external interface PxRevoluteJoint : PxJoint {
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

fun PxRevoluteJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRevoluteJoint = js("_module.wrapPointer(ptr, _module.PxRevoluteJoint)")

fun PxRevoluteJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

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

external interface PxRevoluteJointFlags {
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
fun PxRevoluteJointFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxRevoluteJointFlags = js("new _module.PxRevoluteJointFlags(flags)")

fun PxRevoluteJointFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRevoluteJointFlags = js("_module.wrapPointer(ptr, _module.PxRevoluteJointFlags)")

fun PxRevoluteJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSphericalJoint : PxJoint {
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

fun PxSphericalJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSphericalJoint = js("_module.wrapPointer(ptr, _module.PxSphericalJoint)")

fun PxSphericalJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxSphericalJoint.swingYAngle
    get() = getSwingYAngle()
val PxSphericalJoint.swingZAngle
    get() = getSwingZAngle()

var PxSphericalJoint.sphericalJointFlags
    get() = getSphericalJointFlags()
    set(value) { setSphericalJointFlags(value) }

external interface PxSphericalJointFlags {
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
fun PxSphericalJointFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxSphericalJointFlags = js("new _module.PxSphericalJointFlags(flags)")

fun PxSphericalJointFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSphericalJointFlags = js("_module.wrapPointer(ptr, _module.PxSphericalJointFlags)")

fun PxSphericalJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSpring {
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
fun PxSpring(stiffness: Float, damping: Float, _module: dynamic = PhysXJsLoader.physXJs): PxSpring = js("new _module.PxSpring(stiffness, damping)")

fun PxSpringFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSpring = js("_module.wrapPointer(ptr, _module.PxSpring)")

fun PxSpring.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxExtensionTopLevelFunctions {
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

fun PxExtensionTopLevelFunctionsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxExtensionTopLevelFunctions = js("_module.wrapPointer(ptr, _module.PxExtensionTopLevelFunctions)")

external interface PxCollectionExt {
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

fun PxCollectionExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxCollectionExt = js("_module.wrapPointer(ptr, _module.PxCollectionExt)")

fun PxCollectionExt.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultMemoryInputData : PxInputData {
    /**
     * @param dest  WebIDL type: VoidPtr
     * @param count WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun read(dest: Any, count: Int): Int

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
fun PxDefaultMemoryInputData(data: PxU8Ptr, length: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultMemoryInputData = js("new _module.PxDefaultMemoryInputData(data, length)")

fun PxDefaultMemoryInputDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultMemoryInputData = js("_module.wrapPointer(ptr, _module.PxDefaultMemoryInputData)")

fun PxDefaultMemoryInputData.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxDefaultMemoryInputData.length
    get() = getLength()

external interface PxDefaultMemoryOutputStream : PxOutputStream {
    /**
     * @param src   WebIDL type: VoidPtr
     * @param count WebIDL type: unsigned long
     */
    fun write(src: Any, count: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSize(): Int

    /**
     * @return WebIDL type: VoidPtr
     */
    fun getData(): Any

}

fun PxDefaultMemoryOutputStream(_module: dynamic = PhysXJsLoader.physXJs): PxDefaultMemoryOutputStream = js("new _module.PxDefaultMemoryOutputStream()")

fun PxDefaultMemoryOutputStreamFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDefaultMemoryOutputStream = js("_module.wrapPointer(ptr, _module.PxDefaultMemoryOutputStream)")

fun PxDefaultMemoryOutputStream.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxDefaultMemoryOutputStream.size
    get() = getSize()
val PxDefaultMemoryOutputStream.data
    get() = getData()

external interface PxMassProperties {
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

fun PxMassProperties(_module: dynamic = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties()")

/**
 * @param m        WebIDL type: float
 * @param inertiaT WebIDL type: [PxMat33] (Const, Ref)
 * @param com      WebIDL type: [PxVec3] (Const, Ref)
 */
fun PxMassProperties(m: Float, inertiaT: PxMat33, com: PxVec3, _module: dynamic = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties(m, inertiaT, com)")

/**
 * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
 */
fun PxMassProperties(geometry: PxGeometry, _module: dynamic = PhysXJsLoader.physXJs): PxMassProperties = js("new _module.PxMassProperties(geometry)")

fun PxMassPropertiesFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMassProperties = js("_module.wrapPointer(ptr, _module.PxMassProperties)")

fun PxMassProperties.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMeshOverlapUtil {
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

fun PxMeshOverlapUtil(_module: dynamic = PhysXJsLoader.physXJs): PxMeshOverlapUtil = js("new _module.PxMeshOverlapUtil()")

fun PxMeshOverlapUtilFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMeshOverlapUtil = js("_module.wrapPointer(ptr, _module.PxMeshOverlapUtil)")

fun PxMeshOverlapUtil.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxMeshOverlapUtil.results
    get() = getResults()
val PxMeshOverlapUtil.nbResults
    get() = getNbResults()

external interface PxRigidActorExt {
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

fun PxRigidActorExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidActorExt = js("_module.wrapPointer(ptr, _module.PxRigidActorExt)")

fun PxRigidActorExt.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidBodyExt {
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

fun PxRigidBodyExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidBodyExt = js("_module.wrapPointer(ptr, _module.PxRigidBodyExt)")

fun PxRigidBodyExt.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSerialization {
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
    fun createCollectionFromBinary(memBlock: Any, sr: PxSerializationRegistry): PxCollection

    /**
     * @param memBlock     WebIDL type: VoidPtr
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromBinary(memBlock: Any, sr: PxSerializationRegistry, externalRefs: PxCollection): PxCollection

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

fun PxSerializationFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSerialization = js("_module.wrapPointer(ptr, _module.PxSerialization)")

fun PxSerialization.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSerializationRegistry {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

}

fun PxSerializationRegistryFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSerializationRegistry = js("_module.wrapPointer(ptr, _module.PxSerializationRegistry)")

object PxD6AxisEnum {
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eZ()
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eTWIST()
    val eSWING1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eSWING1()
    val eSWING2: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eSWING2()
}

object PxD6DriveEnum {
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eZ()
    val eSWING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eSWING()
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eTWIST()
    val eSLERP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eSLERP()
}

object PxD6JointDriveFlagEnum {
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6JointDriveFlagEnum_eACCELERATION()
}

object PxD6MotionEnum {
    val eLOCKED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6MotionEnum_eLOCKED()
    val eLIMITED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6MotionEnum_eLIMITED()
    val eFREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6MotionEnum_eFREE()
}

object PxDistanceJointFlagEnum {
    val eMAX_DISTANCE_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDistanceJointFlagEnum_eMAX_DISTANCE_ENABLED()
    val eMIN_DISTANCE_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDistanceJointFlagEnum_eMIN_DISTANCE_ENABLED()
    val eSPRING_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDistanceJointFlagEnum_eSPRING_ENABLED()
}

object PxJointActorIndexEnum {
    val eACTOR0: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxJointActorIndexEnum_eACTOR0()
    val eACTOR1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxJointActorIndexEnum_eACTOR1()
}

object PxPrismaticJointFlagEnum {
    val eLIMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPrismaticJointFlagEnum_eLIMIT_ENABLED()
}

object PxRevoluteJointFlagEnum {
    val eLIMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eLIMIT_ENABLED()
    val eDRIVE_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_ENABLED()
    val eDRIVE_FREESPIN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRevoluteJointFlagEnum_eDRIVE_FREESPIN()
}

object PxSphericalJointFlagEnum {
    val eLIMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSphericalJointFlagEnum_eLIMIT_ENABLED()
}

