/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

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

fun PxCollectionExt.destroy() {
    PhysXJsLoader.destroy(this)
}

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

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionLinearTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionLinearTolerance(): Float

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionAngularTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionAngularTolerance(): Float

}

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
var PxD6Joint.projectionLinearTolerance
    get() = getProjectionLinearTolerance()
    set(value) { setProjectionLinearTolerance(value) }
var PxD6Joint.projectionAngularTolerance
    get() = getProjectionAngularTolerance()
    set(value) { setProjectionAngularTolerance(value) }

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

fun PxD6JointDrive(): PxD6JointDrive {
    fun _PxD6JointDrive(_module: dynamic) = js("new _module.PxD6JointDrive()")
    return _PxD6JointDrive(PhysXJsLoader.physXJs)
}

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float): PxD6JointDrive {
    fun _PxD6JointDrive(_module: dynamic, driveStiffness: Float, driveDamping: Float, driveForceLimit: Float) = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit)")
    return _PxD6JointDrive(PhysXJsLoader.physXJs, driveStiffness, driveDamping, driveForceLimit)
}

/**
 * @param driveStiffness  WebIDL type: float
 * @param driveDamping    WebIDL type: float
 * @param driveForceLimit WebIDL type: float
 * @param isAcceleration  WebIDL type: boolean
 */
fun PxD6JointDrive(driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, isAcceleration: Boolean): PxD6JointDrive {
    fun _PxD6JointDrive(_module: dynamic, driveStiffness: Float, driveDamping: Float, driveForceLimit: Float, isAcceleration: Boolean) = js("new _module.PxD6JointDrive(driveStiffness, driveDamping, driveForceLimit, isAcceleration)")
    return _PxD6JointDrive(PhysXJsLoader.physXJs, driveStiffness, driveDamping, driveForceLimit, isAcceleration)
}

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxD6JointDriveFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxD6JointDriveFlags(flags: Int): PxD6JointDriveFlags {
    fun _PxD6JointDriveFlags(_module: dynamic, flags: Int) = js("new _module.PxD6JointDriveFlags(flags)")
    return _PxD6JointDriveFlags(PhysXJsLoader.physXJs, flags)
}

fun PxD6JointDriveFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultAllocator

fun PxDefaultAllocator(): PxDefaultAllocator {
    fun _PxDefaultAllocator(_module: dynamic) = js("new _module.PxDefaultAllocator()")
    return _PxDefaultAllocator(PhysXJsLoader.physXJs)
}

fun PxDefaultAllocator.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDefaultCpuDispatcher : PxCpuDispatcher

fun PxDefaultCpuDispatcher.destroy() {
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
fun PxDefaultMemoryInputData(data: PxU8Ptr, length: Int): PxDefaultMemoryInputData {
    fun _PxDefaultMemoryInputData(_module: dynamic, data: PxU8Ptr, length: Int) = js("new _module.PxDefaultMemoryInputData(data, length)")
    return _PxDefaultMemoryInputData(PhysXJsLoader.physXJs, data, length)
}

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

fun PxDefaultMemoryOutputStream(): PxDefaultMemoryOutputStream {
    fun _PxDefaultMemoryOutputStream(_module: dynamic) = js("new _module.PxDefaultMemoryOutputStream()")
    return _PxDefaultMemoryOutputStream(PhysXJsLoader.physXJs)
}

fun PxDefaultMemoryOutputStream.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxDefaultMemoryOutputStream.size
    get() = getSize()
val PxDefaultMemoryOutputStream.data
    get() = getData()

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxDistanceJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxDistanceJointFlags(flags: Short): PxDistanceJointFlags {
    fun _PxDistanceJointFlags(_module: dynamic, flags: Short) = js("new _module.PxDistanceJointFlags(flags)")
    return _PxDistanceJointFlags(PhysXJsLoader.physXJs, flags)
}

fun PxDistanceJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxFixedJoint : PxJoint {
    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionLinearTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionLinearTolerance(): Float

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionAngularTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionAngularTolerance(): Float

}

fun PxFixedJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxFixedJoint.projectionLinearTolerance
    get() = getProjectionLinearTolerance()
    set(value) { setProjectionLinearTolerance(value) }
var PxFixedJoint.projectionAngularTolerance
    get() = getProjectionAngularTolerance()
    set(value) { setProjectionAngularTolerance(value) }

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

    override fun release()

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

}

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
 * @param spring     WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointAngularLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring): PxJointAngularLimitPair {
    fun _PxJointAngularLimitPair(_module: dynamic, lowerLimit: Float, upperLimit: Float, spring: PxSpring) = js("new _module.PxJointAngularLimitPair(lowerLimit, upperLimit, spring)")
    return _PxJointAngularLimitPair(PhysXJsLoader.physXJs, lowerLimit, upperLimit, spring)
}

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
 * @param spring      WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitCone(yLimitAngle: Float, zLimitAngle: Float, spring: PxSpring): PxJointLimitCone {
    fun _PxJointLimitCone(_module: dynamic, yLimitAngle: Float, zLimitAngle: Float, spring: PxSpring) = js("new _module.PxJointLimitCone(yLimitAngle, zLimitAngle, spring)")
    return _PxJointLimitCone(PhysXJsLoader.physXJs, yLimitAngle, zLimitAngle, spring)
}

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
     * WebIDL type: float
     */
    var contactDistance: Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isSoft(): Boolean

}

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
 * @param spring         WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLimitPyramid(yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, spring: PxSpring): PxJointLimitPyramid {
    fun _PxJointLimitPyramid(_module: dynamic, yLimitAngleMin: Float, yLimitAngleMax: Float, zLimitAngleMin: Float, zLimitAngleMax: Float, spring: PxSpring) = js("new _module.PxJointLimitPyramid(yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring)")
    return _PxJointLimitPyramid(PhysXJsLoader.physXJs, yLimitAngleMin, yLimitAngleMax, zLimitAngleMin, zLimitAngleMax, spring)
}

fun PxJointLimitPyramid.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxJointLinearLimit {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var value: Float
}

/**
 * @param extent WebIDL type: float
 * @param spring WebIDL type: [PxSpring] (Const, Ref)
 */
fun PxJointLinearLimit(extent: Float, spring: PxSpring): PxJointLinearLimit {
    fun _PxJointLinearLimit(_module: dynamic, extent: Float, spring: PxSpring) = js("new _module.PxJointLinearLimit(extent, spring)")
    return _PxJointLinearLimit(PhysXJsLoader.physXJs, extent, spring)
}

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
fun PxJointLinearLimitPair(lowerLimit: Float, upperLimit: Float, spring: PxSpring): PxJointLinearLimitPair {
    fun _PxJointLinearLimitPair(_module: dynamic, lowerLimit: Float, upperLimit: Float, spring: PxSpring) = js("new _module.PxJointLinearLimitPair(lowerLimit, upperLimit, spring)")
    return _PxJointLinearLimitPair(PhysXJsLoader.physXJs, lowerLimit, upperLimit, spring)
}

fun PxJointLinearLimitPair.destroy() {
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

fun PxMeshOverlapUtil(): PxMeshOverlapUtil {
    fun _PxMeshOverlapUtil(_module: dynamic) = js("new _module.PxMeshOverlapUtil()")
    return _PxMeshOverlapUtil(PhysXJsLoader.physXJs)
}

fun PxMeshOverlapUtil.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxMeshOverlapUtil.results
    get() = getResults()
val PxMeshOverlapUtil.nbResults
    get() = getNbResults()

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

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionLinearTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionLinearTolerance(): Float

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionAngularTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionAngularTolerance(): Float

}

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
var PxPrismaticJoint.projectionLinearTolerance
    get() = getProjectionLinearTolerance()
    set(value) { setProjectionLinearTolerance(value) }
var PxPrismaticJoint.projectionAngularTolerance
    get() = getProjectionAngularTolerance()
    set(value) { setProjectionAngularTolerance(value) }

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxPrismaticJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxPrismaticJointFlags(flags: Short): PxPrismaticJointFlags {
    fun _PxPrismaticJointFlags(_module: dynamic, flags: Short) = js("new _module.PxPrismaticJointFlags(flags)")
    return _PxPrismaticJointFlags(PhysXJsLoader.physXJs, flags)
}

fun PxPrismaticJointFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

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

fun PxRigidBodyExt.destroy() {
    PhysXJsLoader.destroy(this)
}

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

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionLinearTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionLinearTolerance(): Float

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionAngularTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionAngularTolerance(): Float

}

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
var PxRevoluteJoint.projectionLinearTolerance
    get() = getProjectionLinearTolerance()
    set(value) { setProjectionLinearTolerance(value) }
var PxRevoluteJoint.projectionAngularTolerance
    get() = getProjectionAngularTolerance()
    set(value) { setProjectionAngularTolerance(value) }

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxRevoluteJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxRevoluteJointFlags(flags: Short): PxRevoluteJointFlags {
    fun _PxRevoluteJointFlags(_module: dynamic, flags: Short) = js("new _module.PxRevoluteJointFlags(flags)")
    return _PxRevoluteJointFlags(PhysXJsLoader.physXJs, flags)
}

fun PxRevoluteJointFlags.destroy() {
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
     * @param cooking   WebIDL type: [PxCooking] (Ref)
     * @param sr        WebIDL type: [PxSerializationRegistry] (Ref)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromXml(inputData: PxInputData, cooking: PxCooking, sr: PxSerializationRegistry): PxCollection

    /**
     * @param inputData    WebIDL type: [PxInputData] (Ref)
     * @param cooking      WebIDL type: [PxCooking] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: [PxCollection]
     */
    fun createCollectionFromXml(inputData: PxInputData, cooking: PxCooking, sr: PxSerializationRegistry, externalRefs: PxCollection): PxCollection

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
     * @param cooking      WebIDL type: [PxCooking]
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToXml(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, cooking: PxCooking): Boolean

    /**
     * @param outputStream WebIDL type: [PxOutputStream] (Ref)
     * @param collection   WebIDL type: [PxCollection] (Ref)
     * @param sr           WebIDL type: [PxSerializationRegistry] (Ref)
     * @param cooking      WebIDL type: [PxCooking]
     * @param externalRefs WebIDL type: [PxCollection] (Const)
     * @return WebIDL type: boolean
     */
    fun serializeCollectionToXml(outputStream: PxOutputStream, collection: PxCollection, sr: PxSerializationRegistry, cooking: PxCooking, externalRefs: PxCollection): Boolean

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

    /**
     * @param tolerance WebIDL type: float
     */
    fun setProjectionLinearTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getProjectionLinearTolerance(): Float

}

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
var PxSphericalJoint.projectionLinearTolerance
    get() = getProjectionLinearTolerance()
    set(value) { setProjectionLinearTolerance(value) }

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
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxSphericalJointFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxSphericalJointFlags(flags: Short): PxSphericalJointFlags {
    fun _PxSphericalJointFlags(_module: dynamic, flags: Short) = js("new _module.PxSphericalJointFlags(flags)")
    return _PxSphericalJointFlags(PhysXJsLoader.physXJs, flags)
}

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
fun PxSpring(stiffness: Float, damping: Float): PxSpring {
    fun _PxSpring(_module: dynamic, stiffness: Float, damping: Float) = js("new _module.PxSpring(stiffness, damping)")
    return _PxSpring(PhysXJsLoader.physXJs, stiffness, damping)
}

fun PxSpring.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface BatchVehicleUpdateDesc {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxFoundation]
     */
    var foundation: PxFoundation
    /**
     * WebIDL type: [PxScene]
     */
    var scene: PxScene
    /**
     * WebIDL type: [PxVehicleDrivableSurfaceToTireFrictionPairs]
     */
    var frictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs
    /**
     * WebIDL type: unsigned long
     */
    var maxNbVehicles: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbWheelsPerVehicle: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbHitPointsPerWheel: Int
    /**
     * WebIDL type: unsigned long
     */
    var numWorkers: Int
    /**
     * WebIDL type: unsigned long
     */
    var batchSize: Int
    /**
     * WebIDL type: [PxBatchQueryPreFilterShader] (Value)
     */
    var preFilterShader: PxBatchQueryPreFilterShader
    /**
     * WebIDL type: [PxBatchQueryPostFilterShader] (Value)
     */
    var postFilterShader: PxBatchQueryPostFilterShader
}

fun BatchVehicleUpdateDesc(): BatchVehicleUpdateDesc {
    fun _BatchVehicleUpdateDesc(_module: dynamic) = js("new _module.BatchVehicleUpdateDesc()")
    return _BatchVehicleUpdateDesc(PhysXJsLoader.physXJs)
}

fun BatchVehicleUpdateDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface BatchVehicleUpdate {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param vehicle WebIDL type: [PxVehicleWheels]
     */
    fun addVehicle(vehicle: PxVehicleWheels)

    /**
     * @param vehicle WebIDL type: [PxVehicleWheels]
     */
    fun removeVehicle(vehicle: PxVehicleWheels)

    fun removeAllVehicles()

    /**
     * @param vehicle WebIDL type: [PxVehicleWheels]
     * @return WebIDL type: long
     */
    fun indexOf(vehicle: PxVehicleWheels): Int

    /**
     * @param timestep WebIDL type: float
     */
    fun batchUpdate(timestep: Float)

    /**
     * @param vehicleId WebIDL type: unsigned long
     * @param wheelId   WebIDL type: unsigned long
     * @return WebIDL type: [PxWheelQueryResult]
     */
    fun getWheelQueryResult(vehicleId: Int, wheelId: Int): PxWheelQueryResult

}

/**
 * @param desc WebIDL type: [BatchVehicleUpdateDesc] (Ref)
 */
fun BatchVehicleUpdate(desc: BatchVehicleUpdateDesc): BatchVehicleUpdate {
    fun _BatchVehicleUpdate(_module: dynamic, desc: BatchVehicleUpdateDesc) = js("new _module.BatchVehicleUpdate(desc)")
    return _BatchVehicleUpdate(PhysXJsLoader.physXJs, desc)
}

fun BatchVehicleUpdate.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxD6AxisEnum {
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eZ()
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eTWIST()
    val eSWING1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eSWING1()
    val eSWING2: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eSWING2()
    val eCOUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6AxisEnum_eCOUNT()
}

object PxD6DriveEnum {
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eZ()
    val eSWING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eSWING()
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eTWIST()
    val eSLERP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eSLERP()
    val eCOUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxD6DriveEnum_eCOUNT()
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
    val COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxJointActorIndexEnum_COUNT()
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

