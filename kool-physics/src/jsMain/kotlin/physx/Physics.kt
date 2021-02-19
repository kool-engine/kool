/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxActor : PxBase {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxActorFlags(flags)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxBatchQueryDesc(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxConstraintFlags(flags)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxContactPairHeaderFlags(flags)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxContactPairFlags(flags)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxFilterData()")
}

/**
 * @param w0 WebIDL type: unsigned long
 * @param w1 WebIDL type: unsigned long
 * @param w2 WebIDL type: unsigned long
 * @param w3 WebIDL type: unsigned long
 */
fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxFilterData(w0, w1, w2, w3)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxHitFlags(flags)")
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

external interface PxMaterial : PxBase

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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxPairFlags(flags)")
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

external interface PxQueryHit : PxActorShape {
    /**
     * WebIDL type: unsigned long
     */
    var faceIndex: Int
}

fun PxQueryHit.destroy() {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRaycastHit()")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRigidBodyFlags(flags)")
}

fun PxRigidBodyFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidDynamic : PxRigidBody {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxRigidDynamicLockFlags(flags)")
}

fun PxRigidDynamicLockFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidStatic : PxRigidActor

external interface PxScene {
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
     * @param scratchMemBlock WebIDL type: any
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: any
     * @param scratchMemBlockSize WebIDL type: unsigned long
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int, scratchMemBlockSize: Int)

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: any
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Int, scratchMemBlockSize: Int, controlSimulation: Boolean)

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
     * @param vec WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setGravity(vec: PxVec3)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getGravity(): PxVec3

    /**
     * @param desc WebIDL type: [PxBatchQueryDesc] (Const, Ref)
     * @return WebIDL type: [PxBatchQuery]
     */
    fun createBatchQuery(desc: PxBatchQueryDesc): PxBatchQuery

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
     * @return WebIDL type: [PxPhysics] (Ref)
     */
    fun getPhysics(): PxPhysics

    /**
     * @return WebIDL type: unsigned long
     */
    fun getTimestamp(): Int

}

val PxScene.flags
    get() = getFlags()
val PxScene.physics
    get() = getPhysics()
val PxScene.timestamp
    get() = getTimestamp()

var PxScene.gravity
    get() = getGravity()
    set(value) { setGravity(value) }

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
     * WebIDL type: [PxSimulationFilterShader] (Value)
     */
    var filterShader: PxSimulationFilterShader
    /**
     * WebIDL type: [PxCpuDispatcher]
     */
    var cpuDispatcher: PxCpuDispatcher
    /**
     * WebIDL type: [PxSceneFlags] (Value)
     */
    var flags: PxSceneFlags
}

/**
 * @param scale WebIDL type: [PxTolerancesScale] (Const, Ref)
 */
fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSceneDesc(scale)")
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxSceneFlags(flags)")
}

fun PxSceneFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxShape : PxBase {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxShapeFlags(flags)")
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
    var JavaSimpleSimulationEventCallback: () -> Unit

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

external interface PxSimulationFilterShader

fun PxSimulationFilterShader.destroy() {
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
    val module = PhysXJsLoader.physXJs
    return js("new module.PxTriggerPairFlags(flags)")
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

object PxShapeFlagEnum {
    val eSIMULATION_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eVISUALIZATION()
}

object PxTriggerPairFlagEnum {
    val eREMOVED_SHAPE_TRIGGER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_TRIGGER()
    val eREMOVED_SHAPE_OTHER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_OTHER()
    val eNEXT_FREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxTriggerPairFlagEnum_eNEXT_FREE()
}

