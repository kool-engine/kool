/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxActor : PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @return WebIDL type: [PxActorTypeEnum] (enum)
     */
    fun getType(): Int

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @param name WebIDL type: DOMString
     */
    fun setName(name: String)

    /**
     * @return WebIDL type: DOMString (Const)
     */
    fun getName(): String

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(): PxBounds3

    /**
     * @param inflation WebIDL type: float
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(inflation: Float): PxBounds3

    /**
     * @param flags WebIDL type: [PxActorFlags] (Ref)
     */
    fun setActorFlags(flags: PxActorFlags)

    /**
     * @return WebIDL type: [PxActorFlags] (Value)
     */
    fun getActorFlags(): PxActorFlags

    /**
     * @param dominanceGroup WebIDL type: octet
     */
    fun setDominanceGroup(dominanceGroup: Byte)

    /**
     * @return WebIDL type: octet
     */
    fun getDominanceGroup(): Byte

    /**
     * @param inClient WebIDL type: octet
     */
    fun setOwnerClient(inClient: Byte)

    /**
     * @return WebIDL type: octet
     */
    fun getOwnerClient(): Byte

}

val PxActor.type
    get() = getType()
val PxActor.scene
    get() = getScene()
val PxActor.worldBounds
    get() = getWorldBounds()

var PxActor.name
    get() = getName()
    set(value) { setName(value) }
var PxActor.actorFlags
    get() = getActorFlags()
    set(value) { setActorFlags(value) }
var PxActor.dominanceGroup
    get() = getDominanceGroup()
    set(value) { setDominanceGroup(value) }
var PxActor.ownerClient
    get() = getOwnerClient()
    set(value) { setOwnerClient(value) }

external interface PxActorFlags {
    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxActorFlags(flags: Byte): PxActorFlags {
    fun _PxActorFlags(_module: dynamic, flags: Byte) = js("new _module.PxActorFlags(flags)")
    return _PxActorFlags(PhysXJsLoader.physXJs, flags)
}

fun PxActorFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxActorShape {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxActorShape.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxActorTypeFlags {
    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxActorTypeFlags(flags: Short): PxActorTypeFlags {
    fun _PxActorTypeFlags(_module: dynamic, flags: Short) = js("new _module.PxActorTypeFlags(flags)")
    return _PxActorTypeFlags(PhysXJsLoader.physXJs, flags)
}

fun PxActorTypeFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxAggregate : PxBase {
    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor): Boolean

    /**
     * @param actor        WebIDL type: [PxActor] (Ref)
     * @param bvhStructure WebIDL type: [PxBVHStructure] (Const)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure): Boolean

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @return WebIDL type: boolean
     */
    fun removeActor(actor: PxActor): Boolean

    /**
     * @param articulation WebIDL type: [PxArticulationBase] (Ref)
     * @return WebIDL type: boolean
     */
    fun addArticulation(articulation: PxArticulationBase): Boolean

    /**
     * @param articulation WebIDL type: [PxArticulationBase] (Ref)
     * @return WebIDL type: boolean
     */
    fun removeArticulation(articulation: PxArticulationBase): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbActors(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbActors(): Int

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @return WebIDL type: boolean
     */
    fun getSelfCollision(): Boolean

}

val PxAggregate.nbActors
    get() = getNbActors()
val PxAggregate.maxNbActors
    get() = getMaxNbActors()
val PxAggregate.scene
    get() = getScene()
val PxAggregate.selfCollision
    get() = getSelfCollision()

external interface PxArticulation : PxArticulationBase {
    /**
     * @param iterations WebIDL type: unsigned long
     */
    fun setMaxProjectionIterations(iterations: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxProjectionIterations(): Int

    /**
     * @param tolerance WebIDL type: float
     */
    fun setSeparationTolerance(tolerance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getSeparationTolerance(): Float

    /**
     * @param iterations WebIDL type: unsigned long
     */
    fun setInternalDriveIterations(iterations: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getInternalDriveIterations(): Int

    /**
     * @param iterations WebIDL type: unsigned long
     */
    fun setExternalDriveIterations(iterations: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getExternalDriveIterations(): Int

    /**
     * @param compliance      WebIDL type: float
     * @param driveIterations WebIDL type: unsigned long
     * @return WebIDL type: [PxArticulationDriveCache]
     */
    fun createDriveCache(compliance: Float, driveIterations: Int): PxArticulationDriveCache

    /**
     * @param driveCache      WebIDL type: [PxArticulationDriveCache] (Ref)
     * @param compliance      WebIDL type: float
     * @param driveIterations WebIDL type: unsigned long
     */
    fun updateDriveCache(driveCache: PxArticulationDriveCache, compliance: Float, driveIterations: Int)

    /**
     * @param driveCache WebIDL type: [PxArticulationDriveCache] (Ref)
     */
    fun releaseDriveCache(driveCache: PxArticulationDriveCache)

    /**
     * @param link           WebIDL type: [PxArticulationLink]
     * @param driveCache     WebIDL type: [PxArticulationDriveCache] (Const, Ref)
     * @param linearImpulse  WebIDL type: [PxVec3] (Const, Ref)
     * @param angularImpulse WebIDL type: [PxVec3] (Const, Ref)
     */
    fun applyImpulse(link: PxArticulationLink, driveCache: PxArticulationDriveCache, linearImpulse: PxVec3, angularImpulse: PxVec3)

    /**
     * @param link            WebIDL type: [PxArticulationLink]
     * @param linearResponse  WebIDL type: [PxVec3] (Ref)
     * @param angularResponse WebIDL type: [PxVec3] (Ref)
     * @param driveCache      WebIDL type: [PxArticulationDriveCache] (Const, Ref)
     * @param linearImpulse   WebIDL type: [PxVec3] (Const, Ref)
     * @param angularImpulse  WebIDL type: [PxVec3] (Const, Ref)
     */
    fun computeImpulseResponse(link: PxArticulationLink, linearResponse: PxVec3, angularResponse: PxVec3, driveCache: PxArticulationDriveCache, linearImpulse: PxVec3, angularImpulse: PxVec3)

}

var PxArticulation.maxProjectionIterations
    get() = getMaxProjectionIterations()
    set(value) { setMaxProjectionIterations(value) }
var PxArticulation.separationTolerance
    get() = getSeparationTolerance()
    set(value) { setSeparationTolerance(value) }
var PxArticulation.internalDriveIterations
    get() = getInternalDriveIterations()
    set(value) { setInternalDriveIterations(value) }
var PxArticulation.externalDriveIterations
    get() = getExternalDriveIterations()
    set(value) { setExternalDriveIterations(value) }

external interface PxArticulationBase : PxBase {
    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @param minPositionIters WebIDL type: unsigned long
     * @param minVelocityIters WebIDL type: unsigned long
     */
    fun setSolverIterationCounts(minPositionIters: Int, minVelocityIters: Int)

    /**
     * @return WebIDL type: boolean
     */
    fun isSleeping(): Boolean

    /**
     * @param threshold WebIDL type: float
     */
    fun setSleepThreshold(threshold: Float)

    /**
     * @return WebIDL type: float
     */
    fun getSleepThreshold(): Float

    /**
     * @param threshold WebIDL type: float
     */
    fun setStabilizationThreshold(threshold: Float)

    /**
     * @return WebIDL type: float
     */
    fun getStabilizationThreshold(): Float

    /**
     * @param wakeCounterValue WebIDL type: float
     */
    fun setWakeCounter(wakeCounterValue: Float)

    /**
     * @return WebIDL type: float
     */
    fun getWakeCounter(): Float

    fun wakeUp()

    fun putToSleep()

    /**
     * @param parent WebIDL type: [PxArticulationLink]
     * @param pose   WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxArticulationLink]
     */
    fun createLink(parent: PxArticulationLink?, pose: PxTransform): PxArticulationLink

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbLinks(): Int

    /**
     * @param name WebIDL type: DOMString (Const)
     */
    fun setName(name: String)

    /**
     * @return WebIDL type: DOMString (Const)
     */
    fun getName(): String

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(): PxBounds3

    /**
     * @return WebIDL type: [PxAggregate]
     */
    fun getAggregate(): PxAggregate

}

val PxArticulationBase.scene
    get() = getScene()
val PxArticulationBase.nbLinks
    get() = getNbLinks()
val PxArticulationBase.worldBounds
    get() = getWorldBounds()
val PxArticulationBase.aggregate
    get() = getAggregate()

var PxArticulationBase.sleepThreshold
    get() = getSleepThreshold()
    set(value) { setSleepThreshold(value) }
var PxArticulationBase.stabilizationThreshold
    get() = getStabilizationThreshold()
    set(value) { setStabilizationThreshold(value) }
var PxArticulationBase.wakeCounter
    get() = getWakeCounter()
    set(value) { setWakeCounter(value) }
var PxArticulationBase.name
    get() = getName()
    set(value) { setName(value) }

external interface PxArticulationCache

external interface PxArticulationCacheFlags {
    /**
     * @param flag WebIDL type: [PxArticulationCacheEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxArticulationCacheEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxArticulationCacheEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxArticulationCacheFlags(flags: Byte): PxArticulationCacheFlags {
    fun _PxArticulationCacheFlags(_module: dynamic, flags: Byte) = js("new _module.PxArticulationCacheFlags(flags)")
    return _PxArticulationCacheFlags(PhysXJsLoader.physXJs, flags)
}

fun PxArticulationCacheFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationDriveCache

external interface PxArticulationFlags {
    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxArticulationFlags(flags: Byte): PxArticulationFlags {
    fun _PxArticulationFlags(_module: dynamic, flags: Byte) = js("new _module.PxArticulationFlags(flags)")
    return _PxArticulationFlags(PhysXJsLoader.physXJs, flags)
}

fun PxArticulationFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationJoint : PxArticulationJointBase {
    /**
     * @param orientation WebIDL type: [PxQuat] (Const, Ref)
     */
    fun setTargetOrientation(orientation: PxQuat)

    /**
     * @return WebIDL type: [PxQuat] (Value)
     */
    fun getTargetOrientation(): PxQuat

    /**
     * @param velocity WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setTargetVelocity(velocity: PxVec3)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getTargetVelocity(): PxVec3

    /**
     * @param driveType WebIDL type: [PxArticulationJointDriveTypeEnum] (enum)
     */
    fun setDriveType(driveType: Int)

    /**
     * @return WebIDL type: [PxArticulationJointDriveTypeEnum] (enum)
     */
    fun getDriveType(): Int

    /**
     * @param spring WebIDL type: float
     */
    fun setStiffness(spring: Float)

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
     * @param compliance WebIDL type: float
     */
    fun setInternalCompliance(compliance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getInternalCompliance(): Float

    /**
     * @param compliance WebIDL type: float
     */
    fun setExternalCompliance(compliance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getExternalCompliance(): Float

    /**
     * @param zLimit WebIDL type: float
     * @param yLimit WebIDL type: float
     */
    fun setSwingLimit(zLimit: Float, yLimit: Float)

    /**
     * @param spring WebIDL type: float
     */
    fun setTangentialStiffness(spring: Float)

    /**
     * @return WebIDL type: float
     */
    fun getTangentialStiffness(): Float

    /**
     * @param damping WebIDL type: float
     */
    fun setTangentialDamping(damping: Float)

    /**
     * @return WebIDL type: float
     */
    fun getTangentialDamping(): Float

    /**
     * @param contactDistance WebIDL type: float
     */
    fun setSwingLimitContactDistance(contactDistance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getSwingLimitContactDistance(): Float

    /**
     * @param enabled WebIDL type: boolean
     */
    fun setSwingLimitEnabled(enabled: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun getSwingLimitEnabled(): Boolean

    /**
     * @param lower WebIDL type: float
     * @param upper WebIDL type: float
     */
    fun setTwistLimit(lower: Float, upper: Float)

    /**
     * @param enabled WebIDL type: boolean
     */
    fun setTwistLimitEnabled(enabled: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun getTwistLimitEnabled(): Boolean

    /**
     * @param contactDistance WebIDL type: float
     */
    fun setTwistLimitContactDistance(contactDistance: Float)

    /**
     * @return WebIDL type: float
     */
    fun getTwistLimitContactDistance(): Float

}

val PxArticulationJoint.targetOrientation
    get() = getTargetOrientation()
val PxArticulationJoint.targetVelocity
    get() = getTargetVelocity()

var PxArticulationJoint.driveType
    get() = getDriveType()
    set(value) { setDriveType(value) }
var PxArticulationJoint.stiffness
    get() = getStiffness()
    set(value) { setStiffness(value) }
var PxArticulationJoint.damping
    get() = getDamping()
    set(value) { setDamping(value) }
var PxArticulationJoint.internalCompliance
    get() = getInternalCompliance()
    set(value) { setInternalCompliance(value) }
var PxArticulationJoint.externalCompliance
    get() = getExternalCompliance()
    set(value) { setExternalCompliance(value) }
var PxArticulationJoint.tangentialStiffness
    get() = getTangentialStiffness()
    set(value) { setTangentialStiffness(value) }
var PxArticulationJoint.tangentialDamping
    get() = getTangentialDamping()
    set(value) { setTangentialDamping(value) }
var PxArticulationJoint.swingLimitContactDistance
    get() = getSwingLimitContactDistance()
    set(value) { setSwingLimitContactDistance(value) }
var PxArticulationJoint.swingLimitEnabled
    get() = getSwingLimitEnabled()
    set(value) { setSwingLimitEnabled(value) }
var PxArticulationJoint.twistLimitEnabled
    get() = getTwistLimitEnabled()
    set(value) { setTwistLimitEnabled(value) }
var PxArticulationJoint.twistLimitContactDistance
    get() = getTwistLimitContactDistance()
    set(value) { setTwistLimitContactDistance(value) }

external interface PxArticulationJointBase : PxBase {
    /**
     * @return WebIDL type: [PxArticulationLink] (Ref)
     */
    fun getParentArticulationLink(): PxArticulationLink

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setParentPose(pose: PxTransform)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getParentPose(): PxTransform

    /**
     * @return WebIDL type: [PxArticulationLink] (Ref)
     */
    fun getChildArticulationLink(): PxArticulationLink

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setChildPose(pose: PxTransform)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getChildPose(): PxTransform

}

val PxArticulationJointBase.parentArticulationLink
    get() = getParentArticulationLink()
val PxArticulationJointBase.childArticulationLink
    get() = getChildArticulationLink()

var PxArticulationJointBase.parentPose
    get() = getParentPose()
    set(value) { setParentPose(value) }
var PxArticulationJointBase.childPose
    get() = getChildPose()
    set(value) { setChildPose(value) }

external interface PxArticulationJointReducedCoordinate : PxArticulationJointBase {
    /**
     * @param jointType WebIDL type: [PxArticulationJointTypeEnum] (enum)
     */
    fun setJointType(jointType: Int)

    /**
     * @return WebIDL type: [PxArticulationJointTypeEnum] (enum)
     */
    fun getJointType(): Int

    /**
     * @param axis   WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param motion WebIDL type: [PxArticulationMotionEnum] (enum)
     */
    fun setMotion(axis: Int, motion: Int)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: [PxArticulationMotionEnum] (enum)
     */
    fun getMotion(axis: Int): Int

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param lowLimit  WebIDL type: float
     * @param highLimit WebIDL type: float
     */
    fun setLimit(axis: Int, lowLimit: Float, highLimit: Float)

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param stiffness WebIDL type: float
     * @param damping   WebIDL type: float
     * @param maxForce  WebIDL type: float
     */
    fun setDrive(axis: Int, stiffness: Float, damping: Float, maxForce: Float)

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param stiffness WebIDL type: float
     * @param damping   WebIDL type: float
     * @param maxForce  WebIDL type: float
     * @param driveType WebIDL type: [PxArticulationDriveTypeEnum] (enum)
     */
    fun setDrive(axis: Int, stiffness: Float, damping: Float, maxForce: Float, driveType: Int)

    /**
     * @param axis   WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param target WebIDL type: float
     */
    fun setDriveTarget(axis: Int, target: Float)

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param targetVel WebIDL type: float
     */
    fun setDriveVelocity(axis: Int, targetVel: Float)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getDriveTarget(axis: Int): Float

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getDriveVelocity(axis: Int): Float

    /**
     * @param coefficient WebIDL type: float
     */
    fun setFrictionCoefficient(coefficient: Float)

    /**
     * @return WebIDL type: float
     */
    fun getFrictionCoefficient(): Float

    /**
     * @param maxJointV WebIDL type: float
     */
    fun setMaxJointVelocity(maxJointV: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxJointVelocity(): Float

}

var PxArticulationJointReducedCoordinate.jointType
    get() = getJointType()
    set(value) { setJointType(value) }
var PxArticulationJointReducedCoordinate.frictionCoefficient
    get() = getFrictionCoefficient()
    set(value) { setFrictionCoefficient(value) }
var PxArticulationJointReducedCoordinate.maxJointVelocity
    get() = getMaxJointVelocity()
    set(value) { setMaxJointVelocity(value) }

external interface PxArticulationLink : PxRigidBody {
    /**
     * @return WebIDL type: [PxArticulationBase] (Ref)
     */
    fun getArticulation(): PxArticulationBase

    /**
     * @return WebIDL type: [PxArticulationJointBase]
     */
    fun getInboundJoint(): PxArticulationJointBase?

    /**
     * @return WebIDL type: unsigned long
     */
    fun getInboundJointDof(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbChildren(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getLinkIndex(): Int

}

val PxArticulationLink.articulation
    get() = getArticulation()
val PxArticulationLink.inboundJoint
    get() = getInboundJoint()
val PxArticulationLink.inboundJointDof
    get() = getInboundJointDof()
val PxArticulationLink.nbChildren
    get() = getNbChildren()
val PxArticulationLink.linkIndex
    get() = getLinkIndex()

external interface PxArticulationReducedCoordinate : PxArticulationBase {
    /**
     * @param flags WebIDL type: [PxArticulationFlags] (Ref)
     */
    fun setArticulationFlags(flags: PxArticulationFlags)

    /**
     * @param flag  WebIDL type: [PxArticulationFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setArticulationFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxArticulationFlags] (Value)
     */
    fun getArticulationFlags(): PxArticulationFlags

    /**
     * @return WebIDL type: unsigned long
     */
    fun getDofs(): Int

    /**
     * @return WebIDL type: [PxArticulationCache]
     */
    fun createCache(): PxArticulationCache

    /**
     * @return WebIDL type: unsigned long
     */
    fun getCacheDataSize(): Int

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun zeroCache(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     * @param flag  WebIDL type: [PxArticulationCacheFlags] (Const, Ref)
     */
    fun applyCache(cache: PxArticulationCache, flag: PxArticulationCacheFlags)

    /**
     * @param cache    WebIDL type: [PxArticulationCache] (Ref)
     * @param flag     WebIDL type: [PxArticulationCacheFlags] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun applyCache(cache: PxArticulationCache, flag: PxArticulationCacheFlags, autowake: Boolean)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     * @param flag  WebIDL type: [PxArticulationCacheFlags] (Const, Ref)
     */
    fun copyInternalStateToCache(cache: PxArticulationCache, flag: PxArticulationCacheFlags)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun releaseCache(cache: PxArticulationCache)

    /**
     * @param maximum WebIDL type: [PxRealPtr] (Ref)
     * @param reduced WebIDL type: [PxRealPtr] (Ref)
     */
    fun packJointData(maximum: PxRealPtr, reduced: PxRealPtr)

    /**
     * @param reduced WebIDL type: [PxRealPtr] (Ref)
     * @param maximum WebIDL type: [PxRealPtr] (Ref)
     */
    fun unpackJointData(reduced: PxRealPtr, maximum: PxRealPtr)

    fun commonInit()

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGeneralizedGravityForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeCoriolisAndCentrifugalForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGeneralizedExternalForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeJointAcceleration(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeJointForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeCoefficientMatrix(cache: PxArticulationCache)

    /**
     * @param cache        WebIDL type: [PxArticulationCache] (Ref)
     * @param initialState WebIDL type: [PxArticulationCache] (Ref)
     * @param jointTorque  WebIDL type: [PxRealPtr] (Ref)
     * @param maxIter      WebIDL type: unsigned long
     */
    fun computeLambda(cache: PxArticulationCache, initialState: PxArticulationCache, jointTorque: PxRealPtr, maxIter: Int)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGeneralizedMassMatrix(cache: PxArticulationCache)

    /**
     * @param joint WebIDL type: [PxJoint]
     */
    fun addLoopJoint(joint: PxJoint)

    /**
     * @param joint WebIDL type: [PxJoint]
     */
    fun removeLoopJoint(joint: PxJoint)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbLoopJoints(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getCoefficientMatrixSize(): Int

    /**
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun teleportRootLink(pose: PxTransform, autowake: Boolean)

    /**
     * @param linkId WebIDL type: unsigned long
     * @return WebIDL type: [PxSpatialVelocity] (Value)
     */
    fun getLinkVelocity(linkId: Int): PxSpatialVelocity

    /**
     * @param linkId WebIDL type: unsigned long
     * @return WebIDL type: [PxSpatialVelocity] (Value)
     */
    fun getLinkAcceleration(linkId: Int): PxSpatialVelocity

}

val PxArticulationReducedCoordinate.dofs
    get() = getDofs()
val PxArticulationReducedCoordinate.cacheDataSize
    get() = getCacheDataSize()
val PxArticulationReducedCoordinate.nbLoopJoints
    get() = getNbLoopJoints()
val PxArticulationReducedCoordinate.coefficientMatrixSize
    get() = getCoefficientMatrixSize()

var PxArticulationReducedCoordinate.articulationFlags
    get() = getArticulationFlags()
    set(value) { setArticulationFlags(value) }

external interface PxBatchQuery {
    fun execute()

    /**
     * @return WebIDL type: [PxBatchQueryPreFilterShader] (Value)
     */
    fun getPreFilterShader(): PxBatchQueryPreFilterShader

    /**
     * @return WebIDL type: [PxBatchQueryPostFilterShader] (Value)
     */
    fun getPostFilterShader(): PxBatchQueryPostFilterShader

    /**
     * @return WebIDL type: any (Const)
     */
    fun getFilterShaderData(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getFilterShaderDataSize(): Int

    /**
     * @param userMemory WebIDL type: [PxBatchQueryMemory] (Const, Ref)
     */
    fun setUserMemory(userMemory: PxBatchQueryMemory)

    /**
     * @return WebIDL type: [PxBatchQueryMemory] (Const, Ref)
     */
    fun getUserMemory(): PxBatchQueryMemory

    fun release()

}

val PxBatchQuery.preFilterShader
    get() = getPreFilterShader()
val PxBatchQuery.postFilterShader
    get() = getPostFilterShader()
val PxBatchQuery.filterShaderData
    get() = getFilterShaderData()
val PxBatchQuery.filterShaderDataSize
    get() = getFilterShaderDataSize()

var PxBatchQuery.userMemory
    get() = getUserMemory()
    set(value) { setUserMemory(value) }

external interface PxBatchQueryDesc {
    /**
     * WebIDL type: any
     */
    var filterShaderData: Int
    /**
     * WebIDL type: unsigned long
     */
    var filterShaderDataSize: Int
    /**
     * WebIDL type: [PxBatchQueryPreFilterShader] (Value)
     */
    var preFilterShader: PxBatchQueryPreFilterShader?
    /**
     * WebIDL type: [PxBatchQueryPostFilterShader] (Value)
     */
    var postFilterShader: PxBatchQueryPostFilterShader?
    /**
     * WebIDL type: [PxBatchQueryMemory] (Value)
     */
    var queryMemory: PxBatchQueryMemory

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

/**
 * @param maxRaycastsPerExecute WebIDL type: unsigned long
 * @param maxSweepsPerExecute   WebIDL type: unsigned long
 * @param maxOverlapsPerExecute WebIDL type: unsigned long
 */
fun PxBatchQueryDesc(maxRaycastsPerExecute: Int, maxSweepsPerExecute: Int, maxOverlapsPerExecute: Int): PxBatchQueryDesc {
    fun _PxBatchQueryDesc(_module: dynamic, maxRaycastsPerExecute: Int, maxSweepsPerExecute: Int, maxOverlapsPerExecute: Int) = js("new _module.PxBatchQueryDesc(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute)")
    return _PxBatchQueryDesc(PhysXJsLoader.physXJs, maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute)
}

fun PxBatchQueryDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBatchQueryMemory {
    /**
     * WebIDL type: [PxRaycastQueryResult]
     */
    var userRaycastResultBuffer: PxRaycastQueryResult
    /**
     * WebIDL type: [PxRaycastHit]
     */
    var userRaycastTouchBuffer: PxRaycastHit
    /**
     * WebIDL type: [PxSweepQueryResult]
     */
    var userSweepResultBuffer: PxSweepQueryResult
    /**
     * WebIDL type: [PxSweepHit]
     */
    var userSweepTouchBuffer: PxSweepHit
    /**
     * WebIDL type: [PxOverlapQueryResult]
     */
    var userOverlapResultBuffer: PxOverlapQueryResult
    /**
     * WebIDL type: [PxOverlapHit]
     */
    var userOverlapTouchBuffer: PxOverlapHit
    /**
     * WebIDL type: unsigned long
     */
    var raycastTouchBufferSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var sweepTouchBufferSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var overlapTouchBufferSize: Int
}

fun PxBatchQueryMemory.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBatchQueryPostFilterShader

fun PxBatchQueryPostFilterShader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBatchQueryPreFilterShader

fun PxBatchQueryPreFilterShader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseCaps {
    /**
     * WebIDL type: unsigned long
     */
    var maxNbRegions: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbObjects: Int
    /**
     * WebIDL type: boolean
     */
    var needsPredefinedBounds: Boolean
}

fun PxBroadPhaseCaps(): PxBroadPhaseCaps {
    fun _PxBroadPhaseCaps(_module: dynamic) = js("new _module.PxBroadPhaseCaps()")
    return _PxBroadPhaseCaps(PhysXJsLoader.physXJs)
}

fun PxBroadPhaseCaps.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseRegion {
    /**
     * WebIDL type: [PxBounds3] (Value)
     */
    var bounds: PxBounds3
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun PxBroadPhaseRegion(): PxBroadPhaseRegion {
    fun _PxBroadPhaseRegion(_module: dynamic) = js("new _module.PxBroadPhaseRegion()")
    return _PxBroadPhaseRegion(PhysXJsLoader.physXJs)
}

fun PxBroadPhaseRegion.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseRegionInfo {
    /**
     * WebIDL type: [PxBroadPhaseRegion] (Value)
     */
    var region: PxBroadPhaseRegion
    /**
     * WebIDL type: unsigned long
     */
    var nbStaticObjects: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbDynamicObjects: Int
    /**
     * WebIDL type: boolean
     */
    var active: Boolean
    /**
     * WebIDL type: boolean
     */
    var overlap: Boolean
}

fun PxBroadPhaseRegionInfo(): PxBroadPhaseRegionInfo {
    fun _PxBroadPhaseRegionInfo(_module: dynamic) = js("new _module.PxBroadPhaseRegionInfo()")
    return _PxBroadPhaseRegionInfo(PhysXJsLoader.physXJs)
}

fun PxBroadPhaseRegionInfo.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConstraint : PxBase {
    override fun release()

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @param actor0 WebIDL type: [PxRigidActor]
     * @param actor1 WebIDL type: [PxRigidActor]
     */
    fun setActors(actor0: PxRigidActor, actor1: PxRigidActor)

    fun markDirty()

    /**
     * @param flags WebIDL type: [PxConstraintFlags] (Ref)
     */
    fun setFlags(flags: PxConstraintFlags)

    /**
     * @return WebIDL type: [PxConstraintFlags] (Value)
     */
    fun getFlags(): PxConstraintFlags

    /**
     * @param flag  WebIDL type: [PxConstraintFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setFlag(flag: Int, value: Boolean)

    /**
     * @param linear  WebIDL type: [PxVec3] (Ref)
     * @param angular WebIDL type: [PxVec3] (Ref)
     */
    fun getForce(linear: PxVec3, angular: PxVec3)

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

    /**
     * @param linear  WebIDL type: float
     * @param angular WebIDL type: float
     */
    fun setBreakForce(linear: Float, angular: Float)

    /**
     * @param threshold WebIDL type: float
     */
    fun setMinResponseThreshold(threshold: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMinResponseThreshold(): Float

}

val PxConstraint.scene
    get() = getScene()

var PxConstraint.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxConstraint.minResponseThreshold
    get() = getMinResponseThreshold()
    set(value) { setMinResponseThreshold(value) }

external interface PxConstraintFlags {
    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxConstraintFlags(flags: Short): PxConstraintFlags {
    fun _PxConstraintFlags(_module: dynamic, flags: Short) = js("new _module.PxConstraintFlags(flags)")
    return _PxConstraintFlags(PhysXJsLoader.physXJs, flags)
}

fun PxConstraintFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConstraintInfo {
    /**
     * WebIDL type: [PxConstraint]
     */
    var constraint: PxConstraint
    /**
     * WebIDL type: VoidPtr
     */
    var externalReference: Any
    /**
     * WebIDL type: unsigned long
     */
    var type: Int
}

fun PxConstraintInfo.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairHeaderFlags {
    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxContactPairHeaderFlags(flags: Short): PxContactPairHeaderFlags {
    fun _PxContactPairHeaderFlags(_module: dynamic, flags: Short) = js("new _module.PxContactPairHeaderFlags(flags)")
    return _PxContactPairHeaderFlags(PhysXJsLoader.physXJs, flags)
}

fun PxContactPairHeaderFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPair {
    /**
     * WebIDL type: [PxShape]
     */
    var shapes: Array<PxShape>
    /**
     * WebIDL type: octet
     */
    var contactCount: Byte
    /**
     * WebIDL type: octet
     */
    var patchCount: Byte
    /**
     * WebIDL type: [PxContactPairFlags] (Value)
     */
    var flags: PxContactPairFlags
    /**
     * WebIDL type: [PxPairFlags] (Value)
     */
    var events: PxPairFlags
}

fun PxContactPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairFlags {
    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxContactPairFlags(flags: Short): PxContactPairFlags {
    fun _PxContactPairFlags(_module: dynamic, flags: Short) = js("new _module.PxContactPairFlags(flags)")
    return _PxContactPairFlags(PhysXJsLoader.physXJs, flags)
}

fun PxContactPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairHeader {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actors: Array<PxRigidActor>
    /**
     * WebIDL type: [PxContactPairHeaderFlags] (Value)
     */
    var flags: PxContactPairHeaderFlags
    /**
     * WebIDL type: [PxContactPair] (Const)
     */
    var pairs: PxContactPair
    /**
     * WebIDL type: unsigned long
     */
    var nbPairs: Int
}

fun PxContactPairHeader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDominanceGroupPair {
    /**
     * WebIDL type: octet
     */
    var dominance0: Byte
    /**
     * WebIDL type: octet
     */
    var dominance1: Byte
}

/**
 * @param a WebIDL type: octet
 * @param b WebIDL type: octet
 */
fun PxDominanceGroupPair(a: Byte, b: Byte): PxDominanceGroupPair {
    fun _PxDominanceGroupPair(_module: dynamic, a: Byte, b: Byte) = js("new _module.PxDominanceGroupPair(a, b)")
    return _PxDominanceGroupPair(PhysXJsLoader.physXJs, a, b)
}

fun PxDominanceGroupPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxgDynamicsMemoryConfig {
    /**
     * WebIDL type: unsigned long
     */
    var constraintBufferCapacity: Int
    /**
     * WebIDL type: unsigned long
     */
    var contactBufferCapacity: Int
    /**
     * WebIDL type: unsigned long
     */
    var tempBufferCapacity: Int
    /**
     * WebIDL type: unsigned long
     */
    var contactStreamSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var patchStreamSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var forceStreamCapacity: Int
    /**
     * WebIDL type: unsigned long
     */
    var heapCapacity: Int
    /**
     * WebIDL type: unsigned long
     */
    var foundLostPairsCapacity: Int
}

fun PxgDynamicsMemoryConfig(): PxgDynamicsMemoryConfig {
    fun _PxgDynamicsMemoryConfig(_module: dynamic) = js("new _module.PxgDynamicsMemoryConfig()")
    return _PxgDynamicsMemoryConfig(PhysXJsLoader.physXJs)
}

fun PxgDynamicsMemoryConfig.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxFilterData {
    /**
     * WebIDL type: unsigned long
     */
    var word0: Int
    /**
     * WebIDL type: unsigned long
     */
    var word1: Int
    /**
     * WebIDL type: unsigned long
     */
    var word2: Int
    /**
     * WebIDL type: unsigned long
     */
    var word3: Int
}

fun PxFilterData(): PxFilterData {
    fun _PxFilterData(_module: dynamic) = js("new _module.PxFilterData()")
    return _PxFilterData(PhysXJsLoader.physXJs)
}

/**
 * @param w0 WebIDL type: unsigned long
 * @param w1 WebIDL type: unsigned long
 * @param w2 WebIDL type: unsigned long
 * @param w3 WebIDL type: unsigned long
 */
fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData {
    fun _PxFilterData(_module: dynamic, w0: Int, w1: Int, w2: Int, w3: Int) = js("new _module.PxFilterData(w0, w1, w2, w3)")
    return _PxFilterData(PhysXJsLoader.physXJs, w0, w1, w2, w3)
}

fun PxFilterData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHitFlags {
    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxHitFlags(flags: Short): PxHitFlags {
    fun _PxHitFlags(_module: dynamic, flags: Short) = js("new _module.PxHitFlags(flags)")
    return _PxHitFlags(PhysXJsLoader.physXJs, flags)
}

fun PxHitFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxLocationHit : PxQueryHit {
    /**
     * WebIDL type: [PxHitFlags] (Value)
     */
    var flags: PxHitFlags
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var position: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var normal: PxVec3
    /**
     * WebIDL type: float
     */
    var distance: Float
}

fun PxLocationHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxOverlapBuffer10 : PxOverlapCallback {
    /**
     * WebIDL type: [PxOverlapHit] (Value)
     */
    var block: PxOverlapHit
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxOverlapHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxOverlapHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTouches(): Int

    /**
     * @return WebIDL type: [PxOverlapHit] (Const)
     */
    fun getTouches(): PxOverlapHit

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxOverlapHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxOverlapHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbTouches(): Int

}

fun PxOverlapBuffer10(): PxOverlapBuffer10 {
    fun _PxOverlapBuffer10(_module: dynamic) = js("new _module.PxOverlapBuffer10()")
    return _PxOverlapBuffer10(PhysXJsLoader.physXJs)
}

fun PxOverlapBuffer10.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxOverlapBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxOverlapBuffer10.nbTouches
    get() = getNbTouches()
val PxOverlapBuffer10.touches
    get() = getTouches()
val PxOverlapBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxOverlapCallback {
    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxOverlapCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxOverlapHit : PxQueryHit

fun PxOverlapHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxOverlapQueryResult {
    /**
     * WebIDL type: [PxOverlapHit] (Value)
     */
    var block: PxOverlapHit
    /**
     * WebIDL type: [PxOverlapHit]
     */
    var touches: PxOverlapHit
    /**
     * WebIDL type: unsigned long
     */
    var nbTouches: Int
    /**
     * WebIDL type: any
     */
    var userData: Int
    /**
     * WebIDL type: octet
     */
    var queryStatus: Byte
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxOverlapHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxOverlapHit

}

fun PxOverlapQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxOverlapQueryResult.nbAnyHits
    get() = getNbAnyHits()

external interface PxMaterial : PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

external interface PxPairFlags {
    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxPairFlags(flags: Short): PxPairFlags {
    fun _PxPairFlags(_module: dynamic, flags: Short) = js("new _module.PxPairFlags(flags)")
    return _PxPairFlags(PhysXJsLoader.physXJs, flags)
}

fun PxPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPhysics {
    fun release()

    /**
     * @return WebIDL type: [PxFoundation] (Ref)
     */
    fun getFoundation(): PxFoundation

    /**
     * @param size                WebIDL type: unsigned long
     * @param enableSelfCollision WebIDL type: boolean
     * @return WebIDL type: [PxAggregate]
     */
    fun createAggregate(size: Int, enableSelfCollision: Boolean): PxAggregate

    /**
     * @return WebIDL type: [PxTolerancesScale] (Const, Ref)
     */
    fun getTolerancesScale(): PxTolerancesScale

    /**
     * @param sceneDesc WebIDL type: [PxSceneDesc] (Const, Ref)
     * @return WebIDL type: [PxScene]
     */
    fun createScene(sceneDesc: PxSceneDesc): PxScene

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun createRigidStatic(pose: PxTransform): PxRigidStatic

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun createRigidDynamic(pose: PxTransform): PxRigidDynamic

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param material WebIDL type: [PxMaterial] (Const, Ref)
     * @return WebIDL type: [PxShape]
     */
    fun createShape(geometry: PxGeometry, material: PxMaterial): PxShape

    /**
     * @param geometry    WebIDL type: [PxGeometry] (Const, Ref)
     * @param material    WebIDL type: [PxMaterial] (Const, Ref)
     * @param isExclusive WebIDL type: boolean
     * @return WebIDL type: [PxShape]
     */
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean): PxShape

    /**
     * @param geometry    WebIDL type: [PxGeometry] (Const, Ref)
     * @param material    WebIDL type: [PxMaterial] (Const, Ref)
     * @param isExclusive WebIDL type: boolean
     * @param shapeFlags  WebIDL type: [PxShapeFlags] (Ref)
     * @return WebIDL type: [PxShape]
     */
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean, shapeFlags: PxShapeFlags): PxShape

    /**
     * @return WebIDL type: long
     */
    fun getNbShapes(): Int

    /**
     * @return WebIDL type: [PxArticulation]
     */
    fun createArticulation(): PxArticulation

    /**
     * @return WebIDL type: [PxArticulationReducedCoordinate]
     */
    fun createArticulationReducedCoordinate(): PxArticulationReducedCoordinate

    /**
     * @param staticFriction  WebIDL type: float
     * @param dynamicFriction WebIDL type: float
     * @param restitution     WebIDL type: float
     * @return WebIDL type: [PxMaterial]
     */
    fun createMaterial(staticFriction: Float, dynamicFriction: Float, restitution: Float): PxMaterial

    /**
     * @return WebIDL type: [PxPhysicsInsertionCallback] (Ref)
     */
    fun getPhysicsInsertionCallback(): PxPhysicsInsertionCallback

}

fun PxPhysics.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxPhysics.foundation
    get() = getFoundation()
val PxPhysics.tolerancesScale
    get() = getTolerancesScale()
val PxPhysics.nbShapes
    get() = getNbShapes()
val PxPhysics.physicsInsertionCallback
    get() = getPhysicsInsertionCallback()

external interface PxQueryFilterData {
    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var data: PxFilterData
    /**
     * WebIDL type: [PxQueryFlags] (Value)
     */
    var flags: PxQueryFlags
}

fun PxQueryFilterData(): PxQueryFilterData {
    fun _PxQueryFilterData(_module: dynamic) = js("new _module.PxQueryFilterData()")
    return _PxQueryFilterData(PhysXJsLoader.physXJs)
}

/**
 * @param fd WebIDL type: [PxFilterData] (Const, Ref)
 * @param f  WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(fd: PxFilterData, f: PxQueryFlags): PxQueryFilterData {
    fun _PxQueryFilterData(_module: dynamic, fd: PxFilterData, f: PxQueryFlags) = js("new _module.PxQueryFilterData(fd, f)")
    return _PxQueryFilterData(PhysXJsLoader.physXJs, fd, f)
}

/**
 * @param f WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(f: PxQueryFlags): PxQueryFilterData {
    fun _PxQueryFilterData(_module: dynamic, f: PxQueryFlags) = js("new _module.PxQueryFilterData(f)")
    return _PxQueryFilterData(PhysXJsLoader.physXJs, f)
}

fun PxQueryFilterData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryFlags {
    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxQueryFlags(flags: Short): PxQueryFlags {
    fun _PxQueryFlags(_module: dynamic, flags: Short) = js("new _module.PxQueryFlags(flags)")
    return _PxQueryFlags(PhysXJsLoader.physXJs, flags)
}

fun PxQueryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryHit : PxActorShape {
    /**
     * WebIDL type: unsigned long
     */
    var faceIndex: Int
}

fun PxQueryHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRaycastBuffer10 : PxRaycastCallback {
    /**
     * WebIDL type: [PxRaycastHit] (Value)
     */
    var block: PxRaycastHit
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxRaycastHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTouches(): Int

    /**
     * @return WebIDL type: [PxRaycastHit] (Const)
     */
    fun getTouches(): PxRaycastHit

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxRaycastHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbTouches(): Int

}

fun PxRaycastBuffer10(): PxRaycastBuffer10 {
    fun _PxRaycastBuffer10(_module: dynamic) = js("new _module.PxRaycastBuffer10()")
    return _PxRaycastBuffer10(PhysXJsLoader.physXJs)
}

fun PxRaycastBuffer10.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxRaycastBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxRaycastBuffer10.nbTouches
    get() = getNbTouches()
val PxRaycastBuffer10.touches
    get() = getTouches()
val PxRaycastBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxRaycastCallback {
    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxRaycastCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRaycastHit : PxLocationHit {
    /**
     * WebIDL type: float
     */
    var u: Float
    /**
     * WebIDL type: float
     */
    var v: Float
}

fun PxRaycastHit(): PxRaycastHit {
    fun _PxRaycastHit(_module: dynamic) = js("new _module.PxRaycastHit()")
    return _PxRaycastHit(PhysXJsLoader.physXJs)
}

fun PxRaycastHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRaycastQueryResult {
    /**
     * WebIDL type: [PxRaycastHit] (Value)
     */
    var block: PxRaycastHit
    /**
     * WebIDL type: [PxRaycastHit]
     */
    var touches: PxRaycastHit
    /**
     * WebIDL type: unsigned long
     */
    var nbTouches: Int
    /**
     * WebIDL type: any
     */
    var userData: Int
    /**
     * WebIDL type: octet
     */
    var queryStatus: Byte
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxRaycastHit

}

fun PxRaycastQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxRaycastQueryResult.nbAnyHits
    get() = getNbAnyHits()

external interface PxRigidActor : PxActor {
    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getGlobalPose(): PxTransform

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setGlobalPose(pose: PxTransform)

    /**
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setGlobalPose(pose: PxTransform, autowake: Boolean)

    /**
     * @param shape WebIDL type: [PxShape] (Ref)
     * @return WebIDL type: boolean
     */
    fun attachShape(shape: PxShape): Boolean

    /**
     * @param shape WebIDL type: [PxShape] (Ref)
     */
    fun detachShape(shape: PxShape)

    /**
     * @param shape           WebIDL type: [PxShape] (Ref)
     * @param wakeOnLostTouch WebIDL type: boolean
     */
    fun detachShape(shape: PxShape, wakeOnLostTouch: Boolean)

    /**
     * @return WebIDL type: long
     */
    fun getNbShapes(): Int

}

val PxRigidActor.nbShapes
    get() = getNbShapes()

var PxRigidActor.globalPose
    get() = getGlobalPose()
    set(value) { setGlobalPose(value) }

external interface PxRigidBody : PxRigidActor {
    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setCMassLocalPose(pose: PxTransform)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getCMassLocalPose(): PxTransform

    /**
     * @param mass WebIDL type: float
     */
    fun setMass(mass: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMass(): Float

    /**
     * @return WebIDL type: float
     */
    fun getInvMass(): Float

    /**
     * @param m WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setMassSpaceInertiaTensor(m: PxVec3)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getMassSpaceInertiaTensor(): PxVec3

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getMassSpaceInvInertiaTensor(): PxVec3

    /**
     * @param linDamp WebIDL type: float
     */
    fun setLinearDamping(linDamp: Float)

    /**
     * @return WebIDL type: float
     */
    fun getLinearDamping(): Float

    /**
     * @param angDamp WebIDL type: float
     */
    fun setAngularDamping(angDamp: Float)

    /**
     * @return WebIDL type: float
     */
    fun getAngularDamping(): Float

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getLinearVelocity(): PxVec3

    /**
     * @param linVel WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setLinearVelocity(linVel: PxVec3)

    /**
     * @param linVel   WebIDL type: [PxVec3] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setLinearVelocity(linVel: PxVec3, autowake: Boolean)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getAngularVelocity(): PxVec3

    /**
     * @param angVel WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setAngularVelocity(angVel: PxVec3)

    /**
     * @param angVel   WebIDL type: [PxVec3] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setAngularVelocity(angVel: PxVec3, autowake: Boolean)

    /**
     * @return WebIDL type: float
     */
    fun getMaxLinearVelocity(): Float

    /**
     * @param maxLinVel WebIDL type: float
     */
    fun setMaxLinearVelocity(maxLinVel: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxAngularVelocity(): Float

    /**
     * @param maxAngVel WebIDL type: float
     */
    fun setMaxAngularVelocity(maxAngVel: Float)

    /**
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addForce(force: PxVec3)

    /**
     * @param force WebIDL type: [PxVec3] (Const, Ref)
     * @param mode  WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addForce(force: PxVec3, mode: Int)

    /**
     * @param force    WebIDL type: [PxVec3] (Const, Ref)
     * @param mode     WebIDL type: [PxForceModeEnum] (enum)
     * @param autowake WebIDL type: boolean
     */
    fun addForce(force: PxVec3, mode: Int, autowake: Boolean)

    /**
     * @param torque WebIDL type: [PxVec3] (Const, Ref)
     */
    fun addTorque(torque: PxVec3)

    /**
     * @param torque WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     */
    fun addTorque(torque: PxVec3, mode: Int)

    /**
     * @param torque   WebIDL type: [PxVec3] (Const, Ref)
     * @param mode     WebIDL type: [PxForceModeEnum] (enum)
     * @param autowake WebIDL type: boolean
     */
    fun addTorque(torque: PxVec3, mode: Int, autowake: Boolean)

    /**
     * @param mode WebIDL type: [PxForceModeEnum] (enum)
     */
    fun clearForce(mode: Int)

    /**
     * @param mode WebIDL type: [PxForceModeEnum] (enum)
     */
    fun clearTorque(mode: Int)

    /**
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param torque WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setForceAndTorque(force: PxVec3, torque: PxVec3)

    /**
     * @param force  WebIDL type: [PxVec3] (Const, Ref)
     * @param torque WebIDL type: [PxVec3] (Const, Ref)
     * @param mode   WebIDL type: [PxForceModeEnum] (enum)
     */
    fun setForceAndTorque(force: PxVec3, torque: PxVec3, mode: Int)

    /**
     * @param flag  WebIDL type: [PxRigidBodyFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setRigidBodyFlag(flag: Int, value: Boolean)

    /**
     * @param inFlags WebIDL type: [PxRigidBodyFlags] (Ref)
     */
    fun setRigidBodyFlags(inFlags: PxRigidBodyFlags)

    /**
     * @return WebIDL type: [PxRigidBodyFlags] (Value)
     */
    fun getRigidBodyFlags(): PxRigidBodyFlags

    /**
     * @param advanceCoefficient WebIDL type: float
     */
    fun setMinCCDAdvanceCoefficient(advanceCoefficient: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMinCCDAdvanceCoefficient(): Float

    /**
     * @param biasClamp WebIDL type: float
     */
    fun setMaxDepenetrationVelocity(biasClamp: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxDepenetrationVelocity(): Float

    /**
     * @param maxImpulse WebIDL type: float
     */
    fun setMaxContactImpulse(maxImpulse: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxContactImpulse(): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getInternalIslandNodeIndex(): Int

}

val PxRigidBody.invMass
    get() = getInvMass()
val PxRigidBody.massSpaceInvInertiaTensor
    get() = getMassSpaceInvInertiaTensor()
val PxRigidBody.internalIslandNodeIndex
    get() = getInternalIslandNodeIndex()

var PxRigidBody.cMassLocalPose
    get() = getCMassLocalPose()
    set(value) { setCMassLocalPose(value) }
var PxRigidBody.mass
    get() = getMass()
    set(value) { setMass(value) }
var PxRigidBody.massSpaceInertiaTensor
    get() = getMassSpaceInertiaTensor()
    set(value) { setMassSpaceInertiaTensor(value) }
var PxRigidBody.linearDamping
    get() = getLinearDamping()
    set(value) { setLinearDamping(value) }
var PxRigidBody.angularDamping
    get() = getAngularDamping()
    set(value) { setAngularDamping(value) }
var PxRigidBody.linearVelocity
    get() = getLinearVelocity()
    set(value) { setLinearVelocity(value) }
var PxRigidBody.angularVelocity
    get() = getAngularVelocity()
    set(value) { setAngularVelocity(value) }
var PxRigidBody.maxLinearVelocity
    get() = getMaxLinearVelocity()
    set(value) { setMaxLinearVelocity(value) }
var PxRigidBody.maxAngularVelocity
    get() = getMaxAngularVelocity()
    set(value) { setMaxAngularVelocity(value) }
var PxRigidBody.rigidBodyFlags
    get() = getRigidBodyFlags()
    set(value) { setRigidBodyFlags(value) }
var PxRigidBody.minCCDAdvanceCoefficient
    get() = getMinCCDAdvanceCoefficient()
    set(value) { setMinCCDAdvanceCoefficient(value) }
var PxRigidBody.maxDepenetrationVelocity
    get() = getMaxDepenetrationVelocity()
    set(value) { setMaxDepenetrationVelocity(value) }
var PxRigidBody.maxContactImpulse
    get() = getMaxContactImpulse()
    set(value) { setMaxContactImpulse(value) }

external interface PxRigidBodyFlags {
    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxRigidBodyFlags(flags: Byte): PxRigidBodyFlags {
    fun _PxRigidBodyFlags(_module: dynamic, flags: Byte) = js("new _module.PxRigidBodyFlags(flags)")
    return _PxRigidBodyFlags(PhysXJsLoader.physXJs, flags)
}

fun PxRigidBodyFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidDynamic : PxRigidBody {
    /**
     * @param destination WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setKinematicTarget(destination: PxTransform)

    /**
     * @param target WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: boolean
     */
    fun getKinematicTarget(target: PxTransform): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun isSleeping(): Boolean

    /**
     * @param threshold WebIDL type: float
     */
    fun setSleepThreshold(threshold: Float)

    /**
     * @return WebIDL type: float
     */
    fun getSleepThreshold(): Float

    /**
     * @param threshold WebIDL type: float
     */
    fun setStabilizationThreshold(threshold: Float)

    /**
     * @return WebIDL type: float
     */
    fun getStabilizationThreshold(): Float

    /**
     * @return WebIDL type: [PxRigidDynamicLockFlags] (Value)
     */
    fun getRigidDynamicLockFlags(): PxRigidDynamicLockFlags

    /**
     * @param flag  WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setRigidDynamicLockFlag(flag: Int, value: Boolean)

    /**
     * @param flags WebIDL type: [PxRigidDynamicLockFlags] (Ref)
     */
    fun setRigidDynamicLockFlags(flags: PxRigidDynamicLockFlags)

    /**
     * @param wakeCounterValue WebIDL type: float
     */
    fun setWakeCounter(wakeCounterValue: Float)

    /**
     * @return WebIDL type: float
     */
    fun getWakeCounter(): Float

    fun wakeUp()

    fun putToSleep()

    /**
     * @param minPositionIters WebIDL type: unsigned long
     */
    fun setSolverIterationCounts(minPositionIters: Int)

    /**
     * @param minPositionIters WebIDL type: unsigned long
     * @param minVelocityIters WebIDL type: unsigned long
     */
    fun setSolverIterationCounts(minPositionIters: Int, minVelocityIters: Int)

    /**
     * @return WebIDL type: float
     */
    fun getContactReportThreshold(): Float

    /**
     * @param threshold WebIDL type: float
     */
    fun setContactReportThreshold(threshold: Float)

}

var PxRigidDynamic.sleepThreshold
    get() = getSleepThreshold()
    set(value) { setSleepThreshold(value) }
var PxRigidDynamic.stabilizationThreshold
    get() = getStabilizationThreshold()
    set(value) { setStabilizationThreshold(value) }
var PxRigidDynamic.rigidDynamicLockFlags
    get() = getRigidDynamicLockFlags()
    set(value) { setRigidDynamicLockFlags(value) }
var PxRigidDynamic.wakeCounter
    get() = getWakeCounter()
    set(value) { setWakeCounter(value) }
var PxRigidDynamic.contactReportThreshold
    get() = getContactReportThreshold()
    set(value) { setContactReportThreshold(value) }

external interface PxRigidDynamicLockFlags {
    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxRigidDynamicLockFlags(flags: Byte): PxRigidDynamicLockFlags {
    fun _PxRigidDynamicLockFlags(_module: dynamic, flags: Byte) = js("new _module.PxRigidDynamicLockFlags(flags)")
    return _PxRigidDynamicLockFlags(PhysXJsLoader.physXJs, flags)
}

fun PxRigidDynamicLockFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidStatic : PxRigidActor

external interface PxScene {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     */
    fun addActor(actor: PxActor)

    /**
     * @param actor        WebIDL type: [PxActor] (Ref)
     * @param bvhStructure WebIDL type: [PxBVHStructure] (Const)
     */
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure)

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     */
    fun removeActor(actor: PxActor)

    /**
     * @param actor           WebIDL type: [PxActor] (Ref)
     * @param wakeOnLostTouch WebIDL type: boolean
     */
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)

    /**
     * @param aggregate WebIDL type: [PxAggregate] (Ref)
     */
    fun addAggregate(aggregate: PxAggregate)

    /**
     * @param aggregate WebIDL type: [PxAggregate] (Ref)
     */
    fun removeAggregate(aggregate: PxAggregate)

    /**
     * @param aggregate       WebIDL type: [PxAggregate] (Ref)
     * @param wakeOnLostTouch WebIDL type: boolean
     */
    fun removeAggregate(aggregate: PxAggregate, wakeOnLostTouch: Boolean)

    /**
     * @param collection WebIDL type: [PxCollection] (Const, Ref)
     */
    fun addCollection(collection: PxCollection)

    /**
     * @return WebIDL type: float
     */
    fun getWakeCounterResetValue(): Float

    /**
     * @param shift WebIDL type: [PxVec3] (Const, Ref)
     */
    fun shiftOrigin(shift: PxVec3)

    /**
     * @param articulation WebIDL type: [PxArticulationBase] (Ref)
     */
    fun addArticulation(articulation: PxArticulationBase)

    /**
     * @param articulation WebIDL type: [PxArticulationBase] (Ref)
     */
    fun removeArticulation(articulation: PxArticulationBase)

    /**
     * @param articulation    WebIDL type: [PxArticulationBase] (Ref)
     * @param wakeOnLostTouch WebIDL type: boolean
     */
    fun removeArticulation(articulation: PxArticulationBase, wakeOnLostTouch: Boolean)

    /**
     * @param types WebIDL type: [PxActorTypeFlags] (Ref)
     * @return WebIDL type: unsigned long
     */
    fun getNbActors(types: PxActorTypeFlags): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbArticulations(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbConstraints(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAggregates(): Int

    /**
     * @param group1    WebIDL type: octet
     * @param group2    WebIDL type: octet
     * @param dominance WebIDL type: [PxDominanceGroupPair] (Const, Ref)
     */
    fun setDominanceGroupPair(group1: Byte, group2: Byte, dominance: PxDominanceGroupPair)

    /**
     * @return WebIDL type: [PxCpuDispatcher]
     */
    fun getCpuDispatcher(): PxCpuDispatcher

    /**
     * @return WebIDL type: [PxCudaContextManager]
     */
    fun getCudaContextManager(): PxCudaContextManager

    /**
     * @return WebIDL type: octet
     */
    fun createClient(): Byte

    /**
     * @param callback WebIDL type: [PxSimulationEventCallback]
     */
    fun setSimulationEventCallback(callback: PxSimulationEventCallback)

    /**
     * @return WebIDL type: [PxSimulationEventCallback]
     */
    fun getSimulationEventCallback(): PxSimulationEventCallback

    /**
     * @param data     WebIDL type: VoidPtr (Const)
     * @param dataSize WebIDL type: unsigned long
     */
    fun setFilterShaderData(data: Any, dataSize: Int)

    /**
     * @return WebIDL type: VoidPtr (Const)
     */
    fun getFilterShaderData(): Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun getFilterShaderDataSize(): Int

    /**
     * @return WebIDL type: [PxSimulationFilterShader] (Value)
     */
    fun getFilterShader(): PxSimulationFilterShader

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     */
    fun resetFiltering(actor: PxActor)

    /**
     * @return WebIDL type: [PxPairFilteringModeEnum] (enum)
     */
    fun getKinematicKinematicFilteringMode(): Int

    /**
     * @return WebIDL type: [PxPairFilteringModeEnum] (enum)
     */
    fun getStaticKinematicFilteringMode(): Int

    /**
     * @param elapsedTime WebIDL type: float
     */
    fun simulate(elapsedTime: Float)

    /**
     * @param elapsedTime    WebIDL type: float
     * @param completionTask WebIDL type: [PxBaseTask]
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask)

    /**
     * @param elapsedTime     WebIDL type: float
     * @param completionTask  WebIDL type: [PxBaseTask]
     * @param scratchMemBlock WebIDL type: VoidPtr
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int, controlSimulation: Boolean)

    fun advance()

    /**
     * @param completionTask WebIDL type: [PxBaseTask]
     */
    fun advance(completionTask: PxBaseTask)

    /**
     * @param elapsedTime WebIDL type: float
     */
    fun collide(elapsedTime: Float)

    /**
     * @param elapsedTime    WebIDL type: float
     * @param completionTask WebIDL type: [PxBaseTask]
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask)

    /**
     * @param elapsedTime     WebIDL type: float
     * @param completionTask  WebIDL type: [PxBaseTask]
     * @param scratchMemBlock WebIDL type: VoidPtr
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int, controlSimulation: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun checkResults(): Boolean

    /**
     * @param block WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun checkResults(block: Boolean): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun fetchCollision(): Boolean

    /**
     * @param block WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun fetchCollision(block: Boolean): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun fetchResults(): Boolean

    /**
     * @param block WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun fetchResults(block: Boolean): Boolean

    /**
     * @param continuation WebIDL type: [PxBaseTask]
     */
    fun processCallbacks(continuation: PxBaseTask)

    fun flushSimulation()

    /**
     * @param sendPendingReports WebIDL type: boolean
     */
    fun flushSimulation(sendPendingReports: Boolean)

    /**
     * @param vec WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setGravity(vec: PxVec3)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getGravity(): PxVec3

    /**
     * @param t WebIDL type: float
     */
    fun setBounceThresholdVelocity(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getBounceThresholdVelocity(): Float

    /**
     * @param ccdMaxPasses WebIDL type: unsigned long
     */
    fun setCCDMaxPasses(ccdMaxPasses: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getCCDMaxPasses(): Int

    /**
     * @return WebIDL type: float
     */
    fun getFrictionOffsetThreshold(): Float

    /**
     * @param frictionType WebIDL type: [PxFrictionTypeEnum] (enum)
     */
    fun setFrictionType(frictionType: Int)

    /**
     * @return WebIDL type: [PxFrictionTypeEnum] (enum)
     */
    fun getFrictionType(): Int

    /**
     * @param stats WebIDL type: [PxSimulationStatistics] (Ref)
     */
    fun getSimulationStatistics(stats: PxSimulationStatistics)

    /**
     * @return WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    fun getStaticStructure(): Int

    /**
     * @return WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    fun getDynamicStructure(): Int

    fun flushQueryUpdates()

    /**
     * @param desc WebIDL type: [PxBatchQueryDesc] (Const, Ref)
     * @return WebIDL type: [PxBatchQuery]
     */
    fun createBatchQuery(desc: PxBatchQueryDesc): PxBatchQuery

    /**
     * @param dynamicTreeRebuildRateHint WebIDL type: unsigned long
     */
    fun setDynamicTreeRebuildRateHint(dynamicTreeRebuildRateHint: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getDynamicTreeRebuildRateHint(): Int

    /**
     * @param rebuildStaticStructure  WebIDL type: boolean
     * @param rebuildDynamicStructure WebIDL type: boolean
     */
    fun forceDynamicTreeRebuild(rebuildStaticStructure: Boolean, rebuildDynamicStructure: Boolean)

    /**
     * @param updateMode WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun setSceneQueryUpdateMode(updateMode: Int)

    /**
     * @return WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun getSceneQueryUpdateMode(): Int

    fun sceneQueriesUpdate()

    /**
     * @param completionTask WebIDL type: [PxBaseTask]
     */
    fun sceneQueriesUpdate(completionTask: PxBaseTask)

    /**
     * @param completionTask    WebIDL type: [PxBaseTask]
     * @param controlSimulation WebIDL type: boolean
     */
    fun sceneQueriesUpdate(completionTask: PxBaseTask, controlSimulation: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun checkQueries(): Boolean

    /**
     * @param block WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun checkQueries(block: Boolean): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun fetchQueries(): Boolean

    /**
     * @param block WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun fetchQueries(block: Boolean): Boolean

    /**
     * @param origin   WebIDL type: [PxVec3] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param distance WebIDL type: float
     * @param hitCall  WebIDL type: [PxRaycastCallback] (Ref)
     * @return WebIDL type: boolean
     */
    fun raycast(origin: PxVec3, unitDir: PxVec3, distance: Float, hitCall: PxRaycastCallback): Boolean

    /**
     * @param origin   WebIDL type: [PxVec3] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param distance WebIDL type: float
     * @param hitCall  WebIDL type: [PxRaycastCallback] (Ref)
     * @param hitFlags WebIDL type: [PxHitFlags] (Ref)
     * @return WebIDL type: boolean
     */
    fun raycast(origin: PxVec3, unitDir: PxVec3, distance: Float, hitCall: PxRaycastCallback, hitFlags: PxHitFlags): Boolean

    /**
     * @param origin     WebIDL type: [PxVec3] (Const, Ref)
     * @param unitDir    WebIDL type: [PxVec3] (Const, Ref)
     * @param distance   WebIDL type: float
     * @param hitCall    WebIDL type: [PxRaycastCallback] (Ref)
     * @param hitFlags   WebIDL type: [PxHitFlags] (Ref)
     * @param filterData WebIDL type: [PxQueryFilterData] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun raycast(origin: PxVec3, unitDir: PxVec3, distance: Float, hitCall: PxRaycastCallback, hitFlags: PxHitFlags, filterData: PxQueryFilterData): Boolean

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param distance WebIDL type: float
     * @param hitCall  WebIDL type: [PxSweepCallback] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(geometry: PxGeometry, pose: PxTransform, unitDir: PxVec3, distance: Float, hitCall: PxSweepCallback): Boolean

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param unitDir  WebIDL type: [PxVec3] (Const, Ref)
     * @param distance WebIDL type: float
     * @param hitCall  WebIDL type: [PxSweepCallback] (Ref)
     * @param hitFlags WebIDL type: [PxHitFlags] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(geometry: PxGeometry, pose: PxTransform, unitDir: PxVec3, distance: Float, hitCall: PxSweepCallback, hitFlags: PxHitFlags): Boolean

    /**
     * @param geometry   WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose       WebIDL type: [PxTransform] (Const, Ref)
     * @param unitDir    WebIDL type: [PxVec3] (Const, Ref)
     * @param distance   WebIDL type: float
     * @param hitCall    WebIDL type: [PxSweepCallback] (Ref)
     * @param hitFlags   WebIDL type: [PxHitFlags] (Ref)
     * @param filterData WebIDL type: [PxQueryFilterData] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(geometry: PxGeometry, pose: PxTransform, unitDir: PxVec3, distance: Float, hitCall: PxSweepCallback, hitFlags: PxHitFlags, filterData: PxQueryFilterData): Boolean

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param hitCall  WebIDL type: [PxOverlapCallback] (Ref)
     * @return WebIDL type: boolean
     */
    fun overlap(geometry: PxGeometry, pose: PxTransform, hitCall: PxOverlapCallback): Boolean

    /**
     * @param geometry   WebIDL type: [PxGeometry] (Const, Ref)
     * @param pose       WebIDL type: [PxTransform] (Const, Ref)
     * @param hitCall    WebIDL type: [PxOverlapCallback] (Ref)
     * @param filterData WebIDL type: [PxQueryFilterData] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun overlap(geometry: PxGeometry, pose: PxTransform, hitCall: PxOverlapCallback, filterData: PxQueryFilterData): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSceneQueryStaticTimestamp(): Int

    /**
     * @return WebIDL type: [PxBroadPhaseTypeEnum] (enum)
     */
    fun getBroadPhaseType(): Int

    /**
     * @param caps WebIDL type: [PxBroadPhaseCaps] (Ref)
     * @return WebIDL type: boolean
     */
    fun getBroadPhaseCaps(caps: PxBroadPhaseCaps): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbBroadPhaseRegions(): Int

    /**
     * @param userBuffer WebIDL type: [PxBroadPhaseRegionInfo]
     * @param bufferSize WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getBroadPhaseRegions(userBuffer: PxBroadPhaseRegionInfo, bufferSize: Int): Int

    /**
     * @param userBuffer WebIDL type: [PxBroadPhaseRegionInfo]
     * @param bufferSize WebIDL type: unsigned long
     * @param startIndex WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getBroadPhaseRegions(userBuffer: PxBroadPhaseRegionInfo, bufferSize: Int, startIndex: Int): Int

    /**
     * @param region WebIDL type: [PxBroadPhaseRegion] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun addBroadPhaseRegion(region: PxBroadPhaseRegion): Int

    /**
     * @param region         WebIDL type: [PxBroadPhaseRegion] (Const, Ref)
     * @param populateRegion WebIDL type: boolean
     * @return WebIDL type: unsigned long
     */
    fun addBroadPhaseRegion(region: PxBroadPhaseRegion, populateRegion: Boolean): Int

    /**
     * @param handle WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun removeBroadPhaseRegion(handle: Int): Boolean

    fun lockRead()

    /**
     * @param file WebIDL type: DOMString
     */
    fun lockRead(file: String)

    /**
     * @param file WebIDL type: DOMString
     * @param line WebIDL type: unsigned long
     */
    fun lockRead(file: String, line: Int)

    fun unlockRead()

    fun lockWrite()

    /**
     * @param file WebIDL type: DOMString
     */
    fun lockWrite(file: String)

    /**
     * @param file WebIDL type: DOMString
     * @param line WebIDL type: unsigned long
     */
    fun lockWrite(file: String, line: Int)

    fun unlockWrite()

    /**
     * @param numBlocks WebIDL type: unsigned long
     */
    fun setNbContactDataBlocks(numBlocks: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbContactDataBlocksUsed(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbContactDataBlocksUsed(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getContactReportStreamBufferSize(): Int

    /**
     * @param solverBatchSize WebIDL type: unsigned long
     */
    fun setSolverBatchSize(solverBatchSize: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSolverBatchSize(): Int

    /**
     * @param solverBatchSize WebIDL type: unsigned long
     */
    fun setSolverArticulationBatchSize(solverBatchSize: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSolverArticulationBatchSize(): Int

    fun release()

    /**
     * @param flag  WebIDL type: [PxSceneFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setFlag(flag: Int, value: Boolean)

    /**
     * @return WebIDL type: [PxSceneFlags] (Value)
     */
    fun getFlags(): PxSceneFlags

    /**
     * @param limits WebIDL type: [PxSceneLimits] (Const, Ref)
     */
    fun setLimits(limits: PxSceneLimits)

    /**
     * @return WebIDL type: [PxSceneLimits] (Value)
     */
    fun getLimits(): PxSceneLimits

    /**
     * @return WebIDL type: [PxPhysics] (Ref)
     */
    fun getPhysics(): PxPhysics

    /**
     * @return WebIDL type: unsigned long
     */
    fun getTimestamp(): Int

}

val PxScene.wakeCounterResetValue
    get() = getWakeCounterResetValue()
val PxScene.nbArticulations
    get() = getNbArticulations()
val PxScene.nbConstraints
    get() = getNbConstraints()
val PxScene.nbAggregates
    get() = getNbAggregates()
val PxScene.cpuDispatcher
    get() = getCpuDispatcher()
val PxScene.cudaContextManager
    get() = getCudaContextManager()
val PxScene.filterShaderDataSize
    get() = getFilterShaderDataSize()
val PxScene.filterShader
    get() = getFilterShader()
val PxScene.kinematicKinematicFilteringMode
    get() = getKinematicKinematicFilteringMode()
val PxScene.staticKinematicFilteringMode
    get() = getStaticKinematicFilteringMode()
val PxScene.frictionOffsetThreshold
    get() = getFrictionOffsetThreshold()
val PxScene.staticStructure
    get() = getStaticStructure()
val PxScene.dynamicStructure
    get() = getDynamicStructure()
val PxScene.sceneQueryStaticTimestamp
    get() = getSceneQueryStaticTimestamp()
val PxScene.broadPhaseType
    get() = getBroadPhaseType()
val PxScene.nbBroadPhaseRegions
    get() = getNbBroadPhaseRegions()
val PxScene.nbContactDataBlocksUsed
    get() = getNbContactDataBlocksUsed()
val PxScene.maxNbContactDataBlocksUsed
    get() = getMaxNbContactDataBlocksUsed()
val PxScene.contactReportStreamBufferSize
    get() = getContactReportStreamBufferSize()
val PxScene.flags
    get() = getFlags()
val PxScene.physics
    get() = getPhysics()
val PxScene.timestamp
    get() = getTimestamp()

var PxScene.simulationEventCallback
    get() = getSimulationEventCallback()
    set(value) { setSimulationEventCallback(value) }
var PxScene.gravity
    get() = getGravity()
    set(value) { setGravity(value) }
var PxScene.bounceThresholdVelocity
    get() = getBounceThresholdVelocity()
    set(value) { setBounceThresholdVelocity(value) }
var PxScene.cCDMaxPasses
    get() = getCCDMaxPasses()
    set(value) { setCCDMaxPasses(value) }
var PxScene.frictionType
    get() = getFrictionType()
    set(value) { setFrictionType(value) }
var PxScene.dynamicTreeRebuildRateHint
    get() = getDynamicTreeRebuildRateHint()
    set(value) { setDynamicTreeRebuildRateHint(value) }
var PxScene.sceneQueryUpdateMode
    get() = getSceneQueryUpdateMode()
    set(value) { setSceneQueryUpdateMode(value) }
var PxScene.solverBatchSize
    get() = getSolverBatchSize()
    set(value) { setSolverBatchSize(value) }
var PxScene.solverArticulationBatchSize
    get() = getSolverArticulationBatchSize()
    set(value) { setSolverArticulationBatchSize(value) }
var PxScene.limits
    get() = getLimits()
    set(value) { setLimits(value) }

external interface PxSceneDesc {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var gravity: PxVec3
    /**
     * WebIDL type: [PxSimulationEventCallback]
     */
    var simulationEventCallback: PxSimulationEventCallback
    /**
     * WebIDL type: VoidPtr (Const)
     */
    var filterShaderData: Any
    /**
     * WebIDL type: unsigned long
     */
    var filterShaderDataSize: Int
    /**
     * WebIDL type: [PxSimulationFilterShader] (Value)
     */
    var filterShader: PxSimulationFilterShader
    /**
     * WebIDL type: [PxPairFilteringModeEnum] (enum)
     */
    var kineKineFilteringMode: Int
    /**
     * WebIDL type: [PxPairFilteringModeEnum] (enum)
     */
    var staticKineFilteringMode: Int
    /**
     * WebIDL type: [PxBroadPhaseTypeEnum] (enum)
     */
    var broadPhaseType: Int
    /**
     * WebIDL type: [PxSceneLimits] (Value)
     */
    var limits: PxSceneLimits
    /**
     * WebIDL type: [PxFrictionTypeEnum] (enum)
     */
    var frictionType: Int
    /**
     * WebIDL type: [PxSolverTypeEnum] (enum)
     */
    var solverType: Int
    /**
     * WebIDL type: float
     */
    var bounceThresholdVelocity: Float
    /**
     * WebIDL type: float
     */
    var frictionOffsetThreshold: Float
    /**
     * WebIDL type: float
     */
    var ccdMaxSeparation: Float
    /**
     * WebIDL type: float
     */
    var solverOffsetSlop: Float
    /**
     * WebIDL type: [PxSceneFlags] (Value)
     */
    var flags: PxSceneFlags
    /**
     * WebIDL type: [PxCpuDispatcher]
     */
    var cpuDispatcher: PxCpuDispatcher
    /**
     * WebIDL type: [PxCudaContextManager]
     */
    var cudaContextManager: PxCudaContextManager
    /**
     * WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    var staticStructure: Int
    /**
     * WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    var dynamicStructure: Int
    /**
     * WebIDL type: unsigned long
     */
    var dynamicTreeRebuildRateHint: Int
    /**
     * WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    var sceneQueryUpdateMode: Int
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
    /**
     * WebIDL type: unsigned long
     */
    var solverBatchSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var solverArticulationBatchSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbContactDataBlocks: Int
    /**
     * WebIDL type: float
     */
    var maxBiasCoefficient: Float
    /**
     * WebIDL type: unsigned long
     */
    var contactReportStreamBufferSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var ccdMaxPasses: Int
    /**
     * WebIDL type: float
     */
    var ccdThreshold: Float
    /**
     * WebIDL type: float
     */
    var wakeCounterResetValue: Float
    /**
     * WebIDL type: [PxBounds3] (Value)
     */
    var sanityBounds: PxBounds3
    /**
     * WebIDL type: [PxgDynamicsMemoryConfig] (Value)
     */
    var gpuDynamicsConfig: PxgDynamicsMemoryConfig
    /**
     * WebIDL type: unsigned long
     */
    var gpuMaxNumPartitions: Int
    /**
     * WebIDL type: unsigned long
     */
    var gpuComputeVersion: Int

    /**
     * @param scale WebIDL type: [PxTolerancesScale] (Const, Ref)
     */
    fun setToDefault(scale: PxTolerancesScale)

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

/**
 * @param scale WebIDL type: [PxTolerancesScale] (Const, Ref)
 */
fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc {
    fun _PxSceneDesc(_module: dynamic, scale: PxTolerancesScale) = js("new _module.PxSceneDesc(scale)")
    return _PxSceneDesc(PhysXJsLoader.physXJs, scale)
}

fun PxSceneDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSceneFlags {
    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxSceneFlags(flags: Int): PxSceneFlags {
    fun _PxSceneFlags(_module: dynamic, flags: Int) = js("new _module.PxSceneFlags(flags)")
    return _PxSceneFlags(PhysXJsLoader.physXJs, flags)
}

fun PxSceneFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSceneLimits {
    /**
     * WebIDL type: unsigned long
     */
    var maxNbActors: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbStaticShapes: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbDynamicShapes: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbAggregates: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbConstraints: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbRegions: Int
    /**
     * WebIDL type: unsigned long
     */
    var maxNbBroadPhaseOverlaps: Int

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxSceneLimits(): PxSceneLimits {
    fun _PxSceneLimits(_module: dynamic) = js("new _module.PxSceneLimits()")
    return _PxSceneLimits(PhysXJsLoader.physXJs)
}

fun PxSceneLimits.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxShape : PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @return WebIDL type: unsigned long
     */
    fun getReferenceCount(): Int

    fun acquireReference()

    /**
     * @return WebIDL type: [PxGeometryTypeEnum] (enum)
     */
    fun getGeometryType(): Int

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     */
    fun setGeometry(geometry: PxGeometry)

    /**
     * @return WebIDL type: [PxGeometryHolder] (Value)
     */
    fun getGeometry(): PxGeometryHolder

    /**
     * @param geometry WebIDL type: [PxBoxGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getBoxGeometry(geometry: PxBoxGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxSphereGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getSphereGeometry(geometry: PxSphereGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxCapsuleGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getCapsuleGeometry(geometry: PxCapsuleGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxPlaneGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getPlaneGeometry(geometry: PxPlaneGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxConvexMeshGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getConvexMeshGeometry(geometry: PxConvexMeshGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxTriangleMeshGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getTriangleMeshGeometry(geometry: PxTriangleMeshGeometry): Boolean

    /**
     * @param geometry WebIDL type: [PxHeightFieldGeometry] (Ref)
     * @return WebIDL type: boolean
     */
    fun getHeightFieldGeometry(geometry: PxHeightFieldGeometry): Boolean

    /**
     * @return WebIDL type: [PxRigidActor]
     */
    fun getActor(): PxRigidActor

    /**
     * @param materials     WebIDL type: [PxMaterialPtr]
     * @param materialCount WebIDL type: unsigned short
     */
    fun setMaterials(materials: PxMaterialPtr, materialCount: Short)

    /**
     * @return WebIDL type: unsigned short
     */
    fun getNbMaterials(): Short

    /**
     * @param userBuffer WebIDL type: [PxMaterialPtr]
     * @param bufferSize WebIDL type: unsigned long
     * @param startIndex WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getMaterials(userBuffer: PxMaterialPtr, bufferSize: Int, startIndex: Int): Int

    /**
     * @param faceIndex WebIDL type: unsigned long
     * @return WebIDL type: [PxMaterial]
     */
    fun getMaterialFromInternalFaceIndex(faceIndex: Int): PxMaterial

    /**
     * @param contactOffset WebIDL type: float
     */
    fun setContactOffset(contactOffset: Float)

    /**
     * @return WebIDL type: float
     */
    fun getContactOffset(): Float

    /**
     * @param restOffset WebIDL type: float
     */
    fun setRestOffset(restOffset: Float)

    /**
     * @return WebIDL type: float
     */
    fun getRestOffset(): Float

    /**
     * @param radius WebIDL type: float
     */
    fun setTorsionalPatchRadius(radius: Float)

    /**
     * @return WebIDL type: float
     */
    fun getTorsionalPatchRadius(): Float

    /**
     * @param radius WebIDL type: float
     */
    fun setMinTorsionalPatchRadius(radius: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMinTorsionalPatchRadius(): Float

    /**
     * @param flag  WebIDL type: [PxShapeFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setFlag(flag: Int, value: Boolean)

    /**
     * @param inFlags WebIDL type: [PxShapeFlags] (Ref)
     */
    fun setFlags(inFlags: PxShapeFlags)

    /**
     * @return WebIDL type: [PxShapeFlags] (Value)
     */
    fun getFlags(): PxShapeFlags

    /**
     * @return WebIDL type: boolean
     */
    fun isExclusive(): Boolean

    /**
     * @param name WebIDL type: DOMString (Const)
     */
    fun setName(name: String)

    /**
     * @return WebIDL type: DOMString (Const)
     */
    fun getName(): String

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setLocalPose(pose: PxTransform)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getLocalPose(): PxTransform

    /**
     * @param data WebIDL type: [PxFilterData] (Const, Ref)
     */
    fun setSimulationFilterData(data: PxFilterData)

    /**
     * @return WebIDL type: [PxFilterData] (Value)
     */
    fun getSimulationFilterData(): PxFilterData

    /**
     * @param data WebIDL type: [PxFilterData] (Const, Ref)
     */
    fun setQueryFilterData(data: PxFilterData)

    /**
     * @return WebIDL type: [PxFilterData] (Value)
     */
    fun getQueryFilterData(): PxFilterData

}

val PxShape.referenceCount
    get() = getReferenceCount()
val PxShape.geometryType
    get() = getGeometryType()
val PxShape.actor
    get() = getActor()
val PxShape.nbMaterials
    get() = getNbMaterials()

var PxShape.contactOffset
    get() = getContactOffset()
    set(value) { setContactOffset(value) }
var PxShape.restOffset
    get() = getRestOffset()
    set(value) { setRestOffset(value) }
var PxShape.torsionalPatchRadius
    get() = getTorsionalPatchRadius()
    set(value) { setTorsionalPatchRadius(value) }
var PxShape.minTorsionalPatchRadius
    get() = getMinTorsionalPatchRadius()
    set(value) { setMinTorsionalPatchRadius(value) }
var PxShape.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxShape.name
    get() = getName()
    set(value) { setName(value) }
var PxShape.localPose
    get() = getLocalPose()
    set(value) { setLocalPose(value) }
var PxShape.simulationFilterData
    get() = getSimulationFilterData()
    set(value) { setSimulationFilterData(value) }
var PxShape.queryFilterData
    get() = getQueryFilterData()
    set(value) { setQueryFilterData(value) }

external interface PxShapeFlags {
    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxShapeFlags(flags: Byte): PxShapeFlags {
    fun _PxShapeFlags(_module: dynamic, flags: Byte) = js("new _module.PxShapeFlags(flags)")
    return _PxShapeFlags(PhysXJsLoader.physXJs, flags)
}

fun PxShapeFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimulationEventCallback

fun PxSimulationEventCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface SimpleSimulationEventCallback : PxSimulationEventCallback {
    /**
     * @param constraints WebIDL type: [PxConstraintInfo]
     * @param count       WebIDL type: unsigned long
     */
    fun onConstraintBreak(constraints: PxConstraintInfo, count: Int)

    /**
     * @param actors WebIDL type: [PxActorPtr]
     * @param count  WebIDL type: unsigned long
     */
    fun onWake(actors: PxActorPtr, count: Int)

    /**
     * @param actors WebIDL type: [PxActorPtr]
     * @param count  WebIDL type: unsigned long
     */
    fun onSleep(actors: PxActorPtr, count: Int)

    /**
     * @param pairHeader WebIDL type: [PxContactPairHeader] (Const, Ref)
     * @param pairs      WebIDL type: [PxContactPair] (Const)
     * @param nbPairs    WebIDL type: unsigned long
     */
    fun onContact(pairHeader: PxContactPairHeader, pairs: PxContactPair, nbPairs: Int)

    /**
     * @param pairs WebIDL type: [PxTriggerPair]
     * @param count WebIDL type: unsigned long
     */
    fun onTrigger(pairs: PxTriggerPair, count: Int)

}

fun SimpleSimulationEventCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface JavaSimulationEventCallback : SimpleSimulationEventCallback {
    /**
     * param constraints WebIDL type: [PxConstraintInfo]
     * param count       WebIDL type: unsigned long
     */
    var onConstraintBreak: (constraints: PxConstraintInfo, count: Int) -> Unit

    /**
     * param actors WebIDL type: [PxActorPtr]
     * param count  WebIDL type: unsigned long
     */
    var onWake: (actors: PxActorPtr, count: Int) -> Unit

    /**
     * param actors WebIDL type: [PxActorPtr]
     * param count  WebIDL type: unsigned long
     */
    var onSleep: (actors: PxActorPtr, count: Int) -> Unit

    /**
     * param pairHeader WebIDL type: [PxContactPairHeader] (Const, Ref)
     * param pairs      WebIDL type: [PxContactPair] (Const)
     * param nbPairs    WebIDL type: unsigned long
     */
    var onContact: (pairHeader: PxContactPairHeader, pairs: PxContactPair, nbPairs: Int) -> Unit

    /**
     * param pairs WebIDL type: [PxTriggerPair]
     * param count WebIDL type: unsigned long
     */
    var onTrigger: (pairs: PxTriggerPair, count: Int) -> Unit

}

fun JavaSimulationEventCallback(): JavaSimulationEventCallback {
    fun _JavaSimulationEventCallback(_module: dynamic) = js("new _module.JavaSimulationEventCallback()")
    return _JavaSimulationEventCallback(PhysXJsLoader.physXJs)
}

external interface PxSimulationFilterShader

fun PxSimulationFilterShader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimulationStatistics {
    /**
     * WebIDL type: unsigned long
     */
    var nbActiveConstraints: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbActiveDynamicBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbActiveKinematicBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbStaticBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbDynamicBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbKinematicBodies: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbShapes: Array<Int>
    /**
     * WebIDL type: unsigned long
     */
    var nbAggregates: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbArticulations: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbAxisSolverConstraints: Int
    /**
     * WebIDL type: unsigned long
     */
    var compressedContactSize: Int
    /**
     * WebIDL type: unsigned long
     */
    var requiredContactConstraintMemory: Int
    /**
     * WebIDL type: unsigned long
     */
    var peakConstraintMemory: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbDiscreteContactPairsTotal: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbDiscreteContactPairsWithCacheHits: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbDiscreteContactPairsWithContacts: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbNewPairs: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbLostPairs: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbNewTouches: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbLostTouches: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbPartitions: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbBroadPhaseAdds: Int
    /**
     * WebIDL type: unsigned long
     */
    var nbBroadPhaseRemoves: Int
}

fun PxSimulationStatistics.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSpatialVelocity {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var linear: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var angular: PxVec3
}

fun PxSpatialVelocity.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSweepBuffer10 : PxSweepCallback {
    /**
     * WebIDL type: [PxSweepHit] (Value)
     */
    var block: PxSweepHit
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxSweepHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTouches(): Int

    /**
     * @return WebIDL type: [PxSweepHit] (Const)
     */
    fun getTouches(): PxSweepHit

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxSweepHit

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbTouches(): Int

}

fun PxSweepBuffer10(): PxSweepBuffer10 {
    fun _PxSweepBuffer10(_module: dynamic) = js("new _module.PxSweepBuffer10()")
    return _PxSweepBuffer10(PhysXJsLoader.physXJs)
}

fun PxSweepBuffer10.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxSweepBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxSweepBuffer10.nbTouches
    get() = getNbTouches()
val PxSweepBuffer10.touches
    get() = getTouches()
val PxSweepBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxSweepCallback {
    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxSweepCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSweepHit : PxLocationHit

fun PxSweepHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSweepQueryResult {
    /**
     * WebIDL type: [PxSweepHit] (Value)
     */
    var block: PxSweepHit
    /**
     * WebIDL type: [PxSweepHit]
     */
    var touches: PxSweepHit
    /**
     * WebIDL type: unsigned long
     */
    var nbTouches: Int
    /**
     * WebIDL type: any
     */
    var userData: Int
    /**
     * WebIDL type: octet
     */
    var queryStatus: Byte
    /**
     * WebIDL type: boolean
     */
    var hasBlock: Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnyHits(): Int

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Const, Ref)
     */
    fun getAnyHit(index: Int): PxSweepHit

}

fun PxSweepQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxSweepQueryResult.nbAnyHits
    get() = getNbAnyHits()

external interface PxTriggerPair {
    /**
     * WebIDL type: [PxShape]
     */
    var triggerShape: PxShape
    /**
     * WebIDL type: [PxRigidActor]
     */
    var triggerActor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var otherShape: PxShape
    /**
     * WebIDL type: [PxRigidActor]
     */
    var otherActor: PxRigidActor
    /**
     * WebIDL type: [PxPairFlagEnum] (enum)
     */
    var status: Int
    /**
     * WebIDL type: [PxTriggerPairFlags] (Value)
     */
    var flags: PxTriggerPairFlags
}

fun PxTriggerPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriggerPairFlags {
    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxTriggerPairFlags(flags: Byte): PxTriggerPairFlags {
    fun _PxTriggerPairFlags(_module: dynamic, flags: Byte) = js("new _module.PxTriggerPairFlags(flags)")
    return _PxTriggerPairFlags(PhysXJsLoader.physXJs, flags)
}

fun PxTriggerPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxActorFlagEnum {
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eVISUALIZATION()
    val eDISABLE_GRAVITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY()
    val eSEND_SLEEP_NOTIFIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES()
    val eDISABLE_SIMULATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION()
}

object PxActorTypeEnum {
    val eRIGID_STATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eRIGID_STATIC()
    val eRIGID_DYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC()
    val eARTICULATION_LINK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK()
    val eACTOR_COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eACTOR_COUNT()
    val eACTOR_FORCE_DWORD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eACTOR_FORCE_DWORD()
}

object PxActorTypeFlagEnum {
    val eRIGID_STATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeFlagEnum_eRIGID_STATIC()
    val eRIGID_DYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeFlagEnum_eRIGID_DYNAMIC()
}

object PxArticulationAxisEnum {
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eTWIST()
    val eSWING1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eSWING1()
    val eSWING2: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eSWING2()
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eZ()
    val eCOUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eCOUNT()
}

object PxArticulationCacheEnum {
    val eVELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eVELOCITY()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eACCELERATION()
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_ePOSITION()
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eFORCE()
    val eLINKVELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eLINKVELOCITY()
    val eLINKACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eLINKACCELERATION()
    val eROOT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eROOT()
    val eALL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheEnum_eALL()
}

object PxArticulationDriveTypeEnum {
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eFORCE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eACCELERATION()
    val eTARGET: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eTARGET()
    val eVELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eVELOCITY()
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eNONE()
}

object PxArticulationFlagEnum {
    val eFIX_BASE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationFlagEnum_eFIX_BASE()
    val eDRIVE_LIMITS_ARE_FORCES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationFlagEnum_eDRIVE_LIMITS_ARE_FORCES()
}

object PxArticulationJointDriveTypeEnum {
    val eTARGET: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointDriveTypeEnum_eTARGET()
    val eERROR: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointDriveTypeEnum_eERROR()
}

object PxArticulationMotionEnum {
    val eLOCKED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eLOCKED()
    val eLIMITED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eLIMITED()
    val eFREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eFREE()
}

object PxArticulationJointTypeEnum {
    val ePRISMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_ePRISMATIC()
    val eREVOLUTE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eREVOLUTE()
    val eSPHERICAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eSPHERICAL()
    val eFIX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eFIX()
    val eUNDEFINED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eUNDEFINED()
}

object PxBroadPhaseTypeEnum {
    val eSAP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eSAP()
    val eMBP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eMBP()
    val eABP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eABP()
    val eGPU: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eGPU()
    val eLAST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eLAST()
}

object PxConstraintFlagEnum {
    val eBROKEN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eBROKEN()
    val ePROJECT_TO_ACTOR0: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_ePROJECT_TO_ACTOR0()
    val ePROJECT_TO_ACTOR1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_ePROJECT_TO_ACTOR1()
    val ePROJECTION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_ePROJECTION()
    val eCOLLISION_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eCOLLISION_ENABLED()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eVISUALIZATION()
    val eDRIVE_LIMITS_ARE_FORCES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eDRIVE_LIMITS_ARE_FORCES()
    val eIMPROVED_SLERP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eIMPROVED_SLERP()
    val eDISABLE_PREPROCESSING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eDISABLE_PREPROCESSING()
    val eENABLE_EXTENDED_LIMITS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eENABLE_EXTENDED_LIMITS()
    val eGPU_COMPATIBLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eGPU_COMPATIBLE()
}

object PxContactPairHeaderFlagEnum {
    val eREMOVED_ACTOR_0: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_0()
    val eREMOVED_ACTOR_1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_1()
}

object PxContactPairFlagEnum {
    val eREMOVED_SHAPE_0: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eREMOVED_SHAPE_0()
    val eREMOVED_SHAPE_1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eREMOVED_SHAPE_1()
    val eACTOR_PAIR_HAS_FIRST_TOUCH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eACTOR_PAIR_HAS_FIRST_TOUCH()
    val eACTOR_PAIR_LOST_TOUCH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eACTOR_PAIR_LOST_TOUCH()
    val eINTERNAL_HAS_IMPULSES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eINTERNAL_HAS_IMPULSES()
    val eINTERNAL_CONTACTS_ARE_FLIPPED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxContactPairFlagEnum_eINTERNAL_CONTACTS_ARE_FLIPPED()
}

object PxForceModeEnum {
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eFORCE()
    val eIMPULSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eIMPULSE()
    val eVELOCITY_CHANGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eACCELERATION()
}

object PxFrictionTypeEnum {
    val ePATCH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_ePATCH()
    val eONE_DIRECTIONAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_eONE_DIRECTIONAL()
    val eTWO_DIRECTIONAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_eTWO_DIRECTIONAL()
    val eFRICTION_COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_eFRICTION_COUNT()
}

object PxHitFlagEnum {
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePOSITION()
    val eNORMAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eNORMAL()
    val eUV: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eUV()
    val eASSUME_NO_INITIAL_OVERLAP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP()
    val eMESH_MULTIPLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE()
    val eMESH_ANY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_ANY()
    val eMESH_BOTH_SIDES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES()
    val ePRECISE_SWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP()
    val eMTD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMTD()
    val eFACE_INDEX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eFACE_INDEX()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eDEFAULT()
    val eMODIFIABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS()
}

object PxPairFilteringModeEnum {
    val eKEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFilteringModeEnum_eKEEP()
    val eSUPPRESS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFilteringModeEnum_eSUPPRESS()
    val eKILL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFilteringModeEnum_eKILL()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFilteringModeEnum_eDEFAULT()
}

object PxPairFlagEnum {
    val eSOLVE_CONTACT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eSOLVE_CONTACT()
    val eMODIFY_CONTACTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eMODIFY_CONTACTS()
    val eNOTIFY_TOUCH_FOUND: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_FOUND()
    val eNOTIFY_TOUCH_PERSISTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_PERSISTS()
    val eNOTIFY_TOUCH_LOST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_LOST()
    val eNOTIFY_TOUCH_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_CCD()
    val eNOTIFY_THRESHOLD_FORCE_FOUND: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_FOUND()
    val eNOTIFY_THRESHOLD_FORCE_PERSISTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_PERSISTS()
    val eNOTIFY_THRESHOLD_FORCE_LOST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_LOST()
    val eNOTIFY_CONTACT_POINTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNOTIFY_CONTACT_POINTS()
    val eDETECT_DISCRETE_CONTACT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eDETECT_DISCRETE_CONTACT()
    val eDETECT_CCD_CONTACT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eDETECT_CCD_CONTACT()
    val ePRE_SOLVER_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_ePRE_SOLVER_VELOCITY()
    val ePOST_SOLVER_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_ePOST_SOLVER_VELOCITY()
    val eCONTACT_EVENT_POSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eCONTACT_EVENT_POSE()
    val eNEXT_FREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eNEXT_FREE()
    val eCONTACT_DEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eCONTACT_DEFAULT()
}

object PxPruningStructureTypeEnum {
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eNONE()
    val eDYNAMIC_AABB_TREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eDYNAMIC_AABB_TREE()
    val eSTATIC_AABB_TREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eSTATIC_AABB_TREE()
    val eLAST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eLAST()
}

object PxQueryFlagEnum {
    val eSTATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eSTATIC()
    val eDYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eDYNAMIC()
    val ePREFILTER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_ePREFILTER()
    val ePOSTFILTER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_ePOSTFILTER()
    val eANY_HIT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eANY_HIT()
    val eNO_BLOCK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eNO_BLOCK()
}

object PxRigidBodyFlagEnum {
    val eKINEMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC()
    val eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES()
    val eENABLE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD()
    val eENABLE_CCD_FRICTION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION()
    val eENABLE_POSE_INTEGRATION_PREVIEW: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW()
    val eENABLE_SPECULATIVE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD()
    val eENABLE_CCD_MAX_CONTACT_IMPULSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE()
    val eRETAIN_ACCELERATIONS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS()
}

object PxRigidDynamicLockFlagEnum {
    val eLOCK_LINEAR_X: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X()
    val eLOCK_LINEAR_Y: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y()
    val eLOCK_LINEAR_Z: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z()
    val eLOCK_ANGULAR_X: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X()
    val eLOCK_ANGULAR_Y: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y()
    val eLOCK_ANGULAR_Z: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z()
}

object PxSceneFlagEnum {
    val eENABLE_ACTIVE_ACTORS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS()
    val eENABLE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_CCD()
    val eDISABLE_CCD_RESWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP()
    val eADAPTIVE_FORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eADAPTIVE_FORCE()
    val eENABLE_PCM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_PCM()
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE()
    val eDISABLE_CONTACT_CACHE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE()
    val eREQUIRE_RW_LOCK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK()
    val eENABLE_STABILIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION()
    val eENABLE_AVERAGE_POINT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT()
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()
    val eENABLE_GPU_DYNAMICS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS()
    val eENABLE_ENHANCED_DETERMINISM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM()
    val eENABLE_FRICTION_EVERY_ITERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION()
    val eMUTABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS()
}

object PxSceneQueryUpdateModeEnum {
    val eBUILD_ENABLED_COMMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_ENABLED()
    val eBUILD_ENABLED_COMMIT_DISABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_DISABLED()
    val eBUILD_DISABLED_COMMIT_DISABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_DISABLED_COMMIT_DISABLED()
}

object PxShapeFlagEnum {
    val eSIMULATION_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eVISUALIZATION()
}

object PxSolverTypeEnum {
    val ePGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSolverTypeEnum_ePGS()
    val eTGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSolverTypeEnum_eTGS()
}

object PxTriggerPairFlagEnum {
    val eREMOVED_SHAPE_TRIGGER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_TRIGGER()
    val eREMOVED_SHAPE_OTHER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_OTHER()
    val eNEXT_FREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eNEXT_FREE()
}

