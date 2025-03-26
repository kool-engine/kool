/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxScene : PxSceneSQSystem {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor): Boolean

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @param bvh   WebIDL type: [PxBVH] (Const)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor, bvh: PxBVH): Boolean

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
     * @return WebIDL type: boolean
     */
    fun addAggregate(aggregate: PxAggregate): Boolean

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
     * @return WebIDL type: boolean
     */
    fun addCollection(collection: PxCollection): Boolean

    /**
     * @return WebIDL type: float
     */
    fun getWakeCounterResetValue(): Float

    /**
     * @param shift WebIDL type: [PxVec3] (Const, Ref)
     */
    fun shiftOrigin(shift: PxVec3)

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     * @return WebIDL type: boolean
     */
    fun addArticulation(articulation: PxArticulationReducedCoordinate): Boolean

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     */
    fun removeArticulation(articulation: PxArticulationReducedCoordinate)

    /**
     * @param articulation    WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     * @param wakeOnLostTouch WebIDL type: boolean
     */
    fun removeArticulation(articulation: PxArticulationReducedCoordinate, wakeOnLostTouch: Boolean)

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
     * @return WebIDL type: boolean
     */
    fun resetFiltering(actor: PxActor): Boolean

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
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float): Boolean

    /**
     * @param elapsedTime    WebIDL type: float
     * @param completionTask WebIDL type: [PxBaseTask]
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask): Boolean

    /**
     * @param elapsedTime     WebIDL type: float
     * @param completionTask  WebIDL type: [PxBaseTask]
     * @param scratchMemBlock WebIDL type: VoidPtr
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int, controlSimulation: Boolean): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun advance(): Boolean

    /**
     * @param completionTask WebIDL type: [PxBaseTask]
     * @return WebIDL type: boolean
     */
    fun advance(completionTask: PxBaseTask): Boolean

    /**
     * @param elapsedTime WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float): Boolean

    /**
     * @param elapsedTime    WebIDL type: float
     * @param completionTask WebIDL type: [PxBaseTask]
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask): Boolean

    /**
     * @param elapsedTime     WebIDL type: float
     * @param completionTask  WebIDL type: [PxBaseTask]
     * @param scratchMemBlock WebIDL type: VoidPtr
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: Any, scratchMemBlockSize: Int, controlSimulation: Boolean): Boolean

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

    fun fetchResultsParticleSystem()

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
     * @param t WebIDL type: float
     */
    fun setCCDMaxSeparation(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getCCDMaxSeparation(): Float

    /**
     * @param t WebIDL type: float
     */
    fun setCCDThreshold(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getCCDThreshold(): Float

    /**
     * @param t WebIDL type: float
     */
    fun setMaxBiasCoefficient(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxBiasCoefficient(): Float

    /**
     * @param t WebIDL type: float
     */
    fun setFrictionOffsetThreshold(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getFrictionOffsetThreshold(): Float

    /**
     * @param t WebIDL type: float
     */
    fun setFrictionCorrelationDistance(t: Float)

    /**
     * @return WebIDL type: float
     */
    fun getFrictionCorrelationDistance(): Float

    /**
     * @return WebIDL type: [PxFrictionTypeEnum] (enum)
     */
    fun getFrictionType(): Int

    /**
     * @return WebIDL type: [PxSolverTypeEnum] (enum)
     */
    fun getSolverType(): Int

    /**
     * @return WebIDL type: [PxRenderBuffer] (Const, Ref)
     */
    fun getRenderBuffer(): PxRenderBuffer

    /**
     * @param param WebIDL type: [PxVisualizationParameterEnum] (enum)
     * @param value WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun setVisualizationParameter(param: Int, value: Float): Boolean

    /**
     * @param paramEnum WebIDL type: [PxVisualizationParameterEnum] (enum)
     * @return WebIDL type: float
     */
    fun getVisualizationParameter(paramEnum: Int): Float

    /**
     * @param box WebIDL type: [PxBounds3] (Const, Ref)
     */
    fun setVisualizationCullingBox(box: PxBounds3)

    /**
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getVisualizationCullingBox(): PxBounds3

    /**
     * @param stats WebIDL type: [PxSimulationStatistics] (Ref)
     */
    fun getSimulationStatistics(stats: PxSimulationStatistics)

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
     * @param file WebIDL type: DOMString (Const)
     */
    fun lockRead(file: String)

    /**
     * @param file WebIDL type: DOMString (Const)
     * @param line WebIDL type: unsigned long
     */
    fun lockRead(file: String, line: Int)

    fun unlockRead()

    fun lockWrite()

    /**
     * @param file WebIDL type: DOMString (Const)
     */
    fun lockWrite(file: String)

    /**
     * @param file WebIDL type: DOMString (Const)
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

fun PxSceneFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxScene = js("_module.wrapPointer(ptr, _module.PxScene)")

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
val PxScene.filterShaderDataSize
    get() = getFilterShaderDataSize()
val PxScene.filterShader
    get() = getFilterShader()
val PxScene.kinematicKinematicFilteringMode
    get() = getKinematicKinematicFilteringMode()
val PxScene.staticKinematicFilteringMode
    get() = getStaticKinematicFilteringMode()
val PxScene.frictionType
    get() = getFrictionType()
val PxScene.solverType
    get() = getSolverType()
val PxScene.renderBuffer
    get() = getRenderBuffer()
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
var PxScene.cCDMaxSeparation
    get() = getCCDMaxSeparation()
    set(value) { setCCDMaxSeparation(value) }
var PxScene.cCDThreshold
    get() = getCCDThreshold()
    set(value) { setCCDThreshold(value) }
var PxScene.maxBiasCoefficient
    get() = getMaxBiasCoefficient()
    set(value) { setMaxBiasCoefficient(value) }
var PxScene.frictionOffsetThreshold
    get() = getFrictionOffsetThreshold()
    set(value) { setFrictionOffsetThreshold(value) }
var PxScene.frictionCorrelationDistance
    get() = getFrictionCorrelationDistance()
    set(value) { setFrictionCorrelationDistance(value) }
var PxScene.visualizationCullingBox
    get() = getVisualizationCullingBox()
    set(value) { setVisualizationCullingBox(value) }
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
     * Native object address.
     */
    val ptr: Int

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
    var frictionCorrelationDistance: Float
    /**
     * WebIDL type: [PxSceneFlags] (Value)
     */
    var flags: PxSceneFlags
    /**
     * WebIDL type: [PxCpuDispatcher]
     */
    var cpuDispatcher: PxCpuDispatcher
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
     * WebIDL type: unsigned long
     */
    var maxNbContactDataBlocks: Int
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
    var ccdMaxSeparation: Float
    /**
     * WebIDL type: float
     */
    var wakeCounterResetValue: Float
    /**
     * WebIDL type: [PxBounds3] (Value)
     */
    var sanityBounds: PxBounds3
    /**
     * WebIDL type: unsigned long
     */
    var gpuMaxNumPartitions: Int
    /**
     * WebIDL type: unsigned long
     */
    var gpuMaxNumStaticPartitions: Int
    /**
     * WebIDL type: unsigned long
     */
    var gpuComputeVersion: Int
    /**
     * WebIDL type: unsigned long
     */
    var contactPairSlabSize: Int
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
     * WebIDL type: [PxDynamicTreeSecondaryPrunerEnum] (enum)
     */
    var dynamicTreeSecondaryPruner: Int
    /**
     * WebIDL type: [PxBVHBuildStrategyEnum] (enum)
     */
    var staticBVHBuildStrategy: Int
    /**
     * WebIDL type: [PxBVHBuildStrategyEnum] (enum)
     */
    var dynamicBVHBuildStrategy: Int
    /**
     * WebIDL type: unsigned long
     */
    var staticNbObjectsPerNode: Int
    /**
     * WebIDL type: unsigned long
     */
    var dynamicNbObjectsPerNode: Int
    /**
     * WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    var sceneQueryUpdateMode: Int

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
fun PxSceneDesc(scale: PxTolerancesScale, _module: dynamic = PhysXJsLoader.physXJs): PxSceneDesc = js("new _module.PxSceneDesc(scale)")

fun PxSceneDescFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneDesc = js("_module.wrapPointer(ptr, _module.PxSceneDesc)")

fun PxSceneDesc.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSceneFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxSceneFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxSceneFlags(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneFlags = js("new _module.PxSceneFlags(flags)")

fun PxSceneFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneFlags = js("_module.wrapPointer(ptr, _module.PxSceneFlags)")

fun PxSceneFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSceneLimits {
    /**
     * Native object address.
     */
    val ptr: Int

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

fun PxSceneLimits(_module: dynamic = PhysXJsLoader.physXJs): PxSceneLimits = js("new _module.PxSceneLimits()")

fun PxSceneLimitsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneLimits = js("_module.wrapPointer(ptr, _module.PxSceneLimits)")

fun PxSceneLimits.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationAttachment {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param restLength WebIDL type: float
     */
    fun setRestLength(restLength: Float)

    /**
     * @return WebIDL type: float
     */
    fun getRestLength(): Float

    /**
     * @param parameters WebIDL type: [PxArticulationTendonLimit] (Const, Ref)
     */
    fun setLimitParameters(parameters: PxArticulationTendonLimit)

    /**
     * @return WebIDL type: [PxArticulationTendonLimit] (Value)
     */
    fun getLimitParameters(): PxArticulationTendonLimit

    /**
     * @param offset WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setRelativeOffset(offset: PxVec3)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getRelativeOffset(): PxVec3

    /**
     * @param coefficient WebIDL type: float
     */
    fun setCoefficient(coefficient: Float)

    /**
     * @return WebIDL type: float
     */
    fun getCoefficient(): Float

    /**
     * @return WebIDL type: [PxArticulationLink]
     */
    fun getLink(): PxArticulationLink

    /**
     * @return WebIDL type: [PxArticulationAttachment]
     */
    fun getParent(): PxArticulationAttachment

    /**
     * @return WebIDL type: boolean
     */
    fun isLeaf(): Boolean

    /**
     * @return WebIDL type: [PxArticulationSpatialTendon]
     */
    fun getTendon(): PxArticulationSpatialTendon

    fun release()

}

fun PxArticulationAttachmentFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationAttachment = js("_module.wrapPointer(ptr, _module.PxArticulationAttachment)")

fun PxArticulationAttachment.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationAttachment.link
    get() = getLink()
val PxArticulationAttachment.parent
    get() = getParent()
val PxArticulationAttachment.tendon
    get() = getTendon()

var PxArticulationAttachment.restLength
    get() = getRestLength()
    set(value) { setRestLength(value) }
var PxArticulationAttachment.limitParameters
    get() = getLimitParameters()
    set(value) { setLimitParameters(value) }
var PxArticulationAttachment.relativeOffset
    get() = getRelativeOffset()
    set(value) { setRelativeOffset(value) }
var PxArticulationAttachment.coefficient
    get() = getCoefficient()
    set(value) { setCoefficient(value) }

external interface PxArticulationCache {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxSpatialForce]
     */
    var externalForces: PxSpatialForce
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var denseJacobian: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var massMatrix: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var jointVelocity: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var jointAcceleration: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var jointPosition: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var jointForce: PxRealPtr
    /**
     * WebIDL type: [PxSpatialVelocity]
     */
    var linkVelocity: PxSpatialVelocity
    /**
     * WebIDL type: [PxSpatialVelocity]
     */
    var linkAcceleration: PxSpatialVelocity
    /**
     * WebIDL type: [PxSpatialForce]
     */
    var linkIncomingJointForce: PxSpatialForce
    /**
     * WebIDL type: [PxArticulationRootLinkData]
     */
    var rootLinkData: PxArticulationRootLinkData
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var coefficientMatrix: PxRealPtr
    /**
     * WebIDL type: [PxRealPtr] (Value)
     */
    var lambda: PxRealPtr
    /**
     * WebIDL type: VoidPtr
     */
    var scratchMemory: Any
    /**
     * WebIDL type: VoidPtr
     */
    var scratchAllocator: Any
    /**
     * WebIDL type: unsigned long
     */
    var version: Int

    fun release()

}

fun PxArticulationCacheFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationCache = js("_module.wrapPointer(ptr, _module.PxArticulationCache)")

fun PxArticulationCache.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationCacheFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxArticulationCacheFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxArticulationCacheFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxArticulationCacheFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxArticulationCacheFlags(flags: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationCacheFlags = js("new _module.PxArticulationCacheFlags(flags)")

fun PxArticulationCacheFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationCacheFlags = js("_module.wrapPointer(ptr, _module.PxArticulationCacheFlags)")

fun PxArticulationCacheFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationDrive {
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
    /**
     * WebIDL type: float
     */
    var maxForce: Float
    /**
     * WebIDL type: [PxArticulationDriveTypeEnum] (enum)
     */
    var driveType: Int
}

fun PxArticulationDrive(_module: dynamic = PhysXJsLoader.physXJs): PxArticulationDrive = js("new _module.PxArticulationDrive()")

/**
 * @param stiffness WebIDL type: float
 * @param damping   WebIDL type: float
 * @param maxForce  WebIDL type: float
 * @param driveType WebIDL type: [PxArticulationDriveTypeEnum] (enum)
 */
fun PxArticulationDrive(stiffness: Float, damping: Float, maxForce: Float, driveType: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationDrive = js("new _module.PxArticulationDrive(stiffness, damping, maxForce, driveType)")

fun PxArticulationDriveFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationDrive = js("_module.wrapPointer(ptr, _module.PxArticulationDrive)")

fun PxArticulationDrive.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationFixedTendon : PxArticulationTendon {
    /**
     * @param parent           WebIDL type: [PxArticulationTendonJoint]
     * @param axis             WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param coefficient      WebIDL type: float
     * @param recipCoefficient WebIDL type: float
     * @param link             WebIDL type: [PxArticulationLink]
     * @return WebIDL type: [PxArticulationTendonJoint]
     */
    fun createTendonJoint(parent: PxArticulationTendonJoint, axis: Int, coefficient: Float, recipCoefficient: Float, link: PxArticulationLink): PxArticulationTendonJoint

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTendonJoints(): Int

    /**
     * @param restLength WebIDL type: float
     */
    fun setRestLength(restLength: Float)

    /**
     * @return WebIDL type: float
     */
    fun getRestLength(): Float

    /**
     * @param parameter WebIDL type: [PxArticulationTendonLimit] (Const, Ref)
     */
    fun setLimitParameters(parameter: PxArticulationTendonLimit)

    /**
     * @return WebIDL type: [PxArticulationTendonLimit] (Value)
     */
    fun getLimitParameters(): PxArticulationTendonLimit

}

fun PxArticulationFixedTendonFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationFixedTendon = js("_module.wrapPointer(ptr, _module.PxArticulationFixedTendon)")

fun PxArticulationFixedTendon.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationFixedTendon.nbTendonJoints
    get() = getNbTendonJoints()

var PxArticulationFixedTendon.restLength
    get() = getRestLength()
    set(value) { setRestLength(value) }
var PxArticulationFixedTendon.limitParameters
    get() = getLimitParameters()
    set(value) { setLimitParameters(value) }

external interface PxArticulationFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxArticulationFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxArticulationFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationFlags = js("new _module.PxArticulationFlags(flags)")

fun PxArticulationFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationFlags = js("_module.wrapPointer(ptr, _module.PxArticulationFlags)")

fun PxArticulationFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationJointReducedCoordinate : PxBase {
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
     * @param axis  WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param limit WebIDL type: [PxArticulationLimit] (Const, Ref)
     */
    fun setLimitParams(axis: Int, limit: PxArticulationLimit)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: [PxArticulationLimit] (Value)
     */
    fun getLimitParams(axis: Int): PxArticulationLimit

    /**
     * @param axis  WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param drive WebIDL type: [PxArticulationDrive] (Const, Ref)
     */
    fun setDriveParams(axis: Int, drive: PxArticulationDrive)

    /**
     * @param axis   WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param target WebIDL type: float
     */
    fun setDriveTarget(axis: Int, target: Float)

    /**
     * @param axis     WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param target   WebIDL type: float
     * @param autowake WebIDL type: boolean
     */
    fun setDriveTarget(axis: Int, target: Float, autowake: Boolean)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getDriveTarget(axis: Int): Float

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param targetVel WebIDL type: float
     */
    fun setDriveVelocity(axis: Int, targetVel: Float)

    /**
     * @param axis      WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param targetVel WebIDL type: float
     * @param autowake  WebIDL type: boolean
     */
    fun setDriveVelocity(axis: Int, targetVel: Float, autowake: Boolean)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getDriveVelocity(axis: Int): Float

    /**
     * @param axis     WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param armature WebIDL type: float
     */
    fun setArmature(axis: Int, armature: Float)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getArmature(axis: Int): Float

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

    /**
     * @param axis     WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param jointPos WebIDL type: float
     */
    fun setJointPosition(axis: Int, jointPos: Float)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getJointPosition(axis: Int): Float

    /**
     * @param axis     WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param jointVel WebIDL type: float
     */
    fun setJointVelocity(axis: Int, jointVel: Float)

    /**
     * @param axis WebIDL type: [PxArticulationAxisEnum] (enum)
     * @return WebIDL type: float
     */
    fun getJointVelocity(axis: Int): Float

}

fun PxArticulationJointReducedCoordinateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationJointReducedCoordinate = js("_module.wrapPointer(ptr, _module.PxArticulationJointReducedCoordinate)")

fun PxArticulationJointReducedCoordinate.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationJointReducedCoordinate.parentArticulationLink
    get() = getParentArticulationLink()
val PxArticulationJointReducedCoordinate.childArticulationLink
    get() = getChildArticulationLink()

var PxArticulationJointReducedCoordinate.parentPose
    get() = getParentPose()
    set(value) { setParentPose(value) }
var PxArticulationJointReducedCoordinate.childPose
    get() = getChildPose()
    set(value) { setChildPose(value) }
var PxArticulationJointReducedCoordinate.jointType
    get() = getJointType()
    set(value) { setJointType(value) }
var PxArticulationJointReducedCoordinate.frictionCoefficient
    get() = getFrictionCoefficient()
    set(value) { setFrictionCoefficient(value) }
var PxArticulationJointReducedCoordinate.maxJointVelocity
    get() = getMaxJointVelocity()
    set(value) { setMaxJointVelocity(value) }

external interface PxArticulationKinematicFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxArticulationKinematicFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxArticulationKinematicFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxArticulationKinematicFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxArticulationKinematicFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationKinematicFlags = js("new _module.PxArticulationKinematicFlags(flags)")

fun PxArticulationKinematicFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationKinematicFlags = js("_module.wrapPointer(ptr, _module.PxArticulationKinematicFlags)")

fun PxArticulationKinematicFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationLink : PxRigidBody {
    /**
     * @return WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     */
    fun getArticulation(): PxArticulationReducedCoordinate

    /**
     * @return WebIDL type: [PxArticulationJointReducedCoordinate] (Nullable)
     */
    fun getInboundJoint(): PxArticulationJointReducedCoordinate?

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

    /**
     * @param cfm WebIDL type: float
     */
    fun setCfmScale(cfm: Float)

    /**
     * @return WebIDL type: float
     */
    fun getCfmScale(): Float

}

fun PxArticulationLinkFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationLink = js("_module.wrapPointer(ptr, _module.PxArticulationLink)")

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

var PxArticulationLink.cfmScale
    get() = getCfmScale()
    set(value) { setCfmScale(value) }

external interface PxArticulationLimit {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var low: Float
    /**
     * WebIDL type: float
     */
    var high: Float
}

fun PxArticulationLimit(_module: dynamic = PhysXJsLoader.physXJs): PxArticulationLimit = js("new _module.PxArticulationLimit()")

/**
 * @param low  WebIDL type: float
 * @param high WebIDL type: float
 */
fun PxArticulationLimit(low: Float, high: Float, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationLimit = js("new _module.PxArticulationLimit(low, high)")

fun PxArticulationLimitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationLimit = js("_module.wrapPointer(ptr, _module.PxArticulationLimit)")

fun PxArticulationLimit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationRootLinkData {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var transform: PxTransform
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var worldLinVel: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var worldAngVel: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var worldLinAccel: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var worldAngAccel: PxVec3
}

fun PxArticulationRootLinkData(_module: dynamic = PhysXJsLoader.physXJs): PxArticulationRootLinkData = js("new _module.PxArticulationRootLinkData()")

fun PxArticulationRootLinkDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationRootLinkData = js("_module.wrapPointer(ptr, _module.PxArticulationRootLinkData)")

fun PxArticulationRootLinkData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxArticulationReducedCoordinate : PxBase {
    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

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
     * @param parent WebIDL type: [PxArticulationLink] (Nullable)
     * @param pose   WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxArticulationLink]
     */
    fun createLink(parent: PxArticulationLink?, pose: PxTransform): PxArticulationLink

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbLinks(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbShapes(): Int

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
     * @param inflation WebIDL type: float
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(inflation: Float): PxBounds3

    /**
     * @return WebIDL type: [PxAggregate]
     */
    fun getAggregate(): PxAggregate

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
     * @param flags WebIDL type: [PxArticulationCacheFlags] (Ref)
     */
    fun applyCache(cache: PxArticulationCache, flags: PxArticulationCacheFlags)

    /**
     * @param cache    WebIDL type: [PxArticulationCache] (Ref)
     * @param flags    WebIDL type: [PxArticulationCacheFlags] (Ref)
     * @param autowake WebIDL type: boolean
     */
    fun applyCache(cache: PxArticulationCache, flags: PxArticulationCacheFlags, autowake: Boolean)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     * @param flags WebIDL type: [PxArticulationCacheFlags] (Const, Ref)
     */
    fun copyInternalStateToCache(cache: PxArticulationCache, flags: PxArticulationCacheFlags)

    fun commonInit()

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGeneralizedGravityForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGravityCompensation(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeCoriolisAndCentrifugalForce(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeCoriolisCompensation(cache: PxArticulationCache)

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
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeGeneralizedMassMatrix(cache: PxArticulationCache)

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeMassMatrix(cache: PxArticulationCache)

    /**
     * @param rootFrame WebIDL type: boolean
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun computeArticulationCOM(rootFrame: Boolean): PxVec3

    /**
     * @param cache WebIDL type: [PxArticulationCache] (Ref)
     */
    fun computeCentroidalMomentumMatrix(cache: PxArticulationCache)

    /**
     * @param joint WebIDL type: [PxConstraint]
     */
    fun addLoopJoint(joint: PxConstraint)

    /**
     * @param joint WebIDL type: [PxConstraint]
     */
    fun removeLoopJoint(joint: PxConstraint)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbLoopJoints(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getCoefficientMatrixSize(): Int

    /**
     * @param pose WebIDL type: [PxTransform] (Const, Ref)
     */
    fun setRootGlobalPose(pose: PxTransform)

    /**
     * @param pose     WebIDL type: [PxTransform] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setRootGlobalPose(pose: PxTransform, autowake: Boolean)

    /**
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getRootGlobalPose(): PxTransform

    /**
     * @param linearVelocity WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setRootLinearVelocity(linearVelocity: PxVec3)

    /**
     * @param linearVelocity WebIDL type: [PxVec3] (Const, Ref)
     * @param autowake       WebIDL type: boolean
     */
    fun setRootLinearVelocity(linearVelocity: PxVec3, autowake: Boolean)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getRootLinearVelocity(): PxVec3

    /**
     * @param angularVelocity WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setRootAngularVelocity(angularVelocity: PxVec3)

    /**
     * @param angularVelocity WebIDL type: [PxVec3] (Const, Ref)
     * @param autowake        WebIDL type: boolean
     */
    fun setRootAngularVelocity(angularVelocity: PxVec3, autowake: Boolean)

    /**
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getRootAngularVelocity(): PxVec3

    /**
     * @param linkId WebIDL type: unsigned long
     * @return WebIDL type: [PxSpatialVelocity] (Value)
     */
    fun getLinkAcceleration(linkId: Int): PxSpatialVelocity

    /**
     * @return WebIDL type: unsigned long
     */
    fun getGPUIndex(): Int

    /**
     * @return WebIDL type: [PxArticulationSpatialTendon]
     */
    fun createSpatialTendon(): PxArticulationSpatialTendon

    /**
     * @return WebIDL type: [PxArticulationFixedTendon]
     */
    fun createFixedTendon(): PxArticulationFixedTendon

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSpatialTendons(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbFixedTendons(): Int

    /**
     * @param flags WebIDL type: [PxArticulationKinematicFlags] (Ref)
     */
    fun updateKinematic(flags: PxArticulationKinematicFlags)

}

fun PxArticulationReducedCoordinateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationReducedCoordinate = js("_module.wrapPointer(ptr, _module.PxArticulationReducedCoordinate)")

fun PxArticulationReducedCoordinate.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationReducedCoordinate.scene
    get() = getScene()
val PxArticulationReducedCoordinate.nbLinks
    get() = getNbLinks()
val PxArticulationReducedCoordinate.nbShapes
    get() = getNbShapes()
val PxArticulationReducedCoordinate.worldBounds
    get() = getWorldBounds()
val PxArticulationReducedCoordinate.aggregate
    get() = getAggregate()
val PxArticulationReducedCoordinate.dofs
    get() = getDofs()
val PxArticulationReducedCoordinate.cacheDataSize
    get() = getCacheDataSize()
val PxArticulationReducedCoordinate.nbLoopJoints
    get() = getNbLoopJoints()
val PxArticulationReducedCoordinate.coefficientMatrixSize
    get() = getCoefficientMatrixSize()
val PxArticulationReducedCoordinate.gPUIndex
    get() = getGPUIndex()
val PxArticulationReducedCoordinate.nbSpatialTendons
    get() = getNbSpatialTendons()
val PxArticulationReducedCoordinate.nbFixedTendons
    get() = getNbFixedTendons()

var PxArticulationReducedCoordinate.sleepThreshold
    get() = getSleepThreshold()
    set(value) { setSleepThreshold(value) }
var PxArticulationReducedCoordinate.stabilizationThreshold
    get() = getStabilizationThreshold()
    set(value) { setStabilizationThreshold(value) }
var PxArticulationReducedCoordinate.wakeCounter
    get() = getWakeCounter()
    set(value) { setWakeCounter(value) }
var PxArticulationReducedCoordinate.name
    get() = getName()
    set(value) { setName(value) }
var PxArticulationReducedCoordinate.articulationFlags
    get() = getArticulationFlags()
    set(value) { setArticulationFlags(value) }
var PxArticulationReducedCoordinate.rootGlobalPose
    get() = getRootGlobalPose()
    set(value) { setRootGlobalPose(value) }
var PxArticulationReducedCoordinate.rootLinearVelocity
    get() = getRootLinearVelocity()
    set(value) { setRootLinearVelocity(value) }
var PxArticulationReducedCoordinate.rootAngularVelocity
    get() = getRootAngularVelocity()
    set(value) { setRootAngularVelocity(value) }

external interface PxArticulationSpatialTendon : PxArticulationTendon {
    /**
     * @param parent         WebIDL type: [PxArticulationAttachment]
     * @param coefficient    WebIDL type: float
     * @param relativeOffset WebIDL type: [PxVec3] (Const, Ref)
     * @param link           WebIDL type: [PxArticulationLink]
     * @return WebIDL type: [PxArticulationAttachment]
     */
    fun createAttachment(parent: PxArticulationAttachment, coefficient: Float, relativeOffset: PxVec3, link: PxArticulationLink): PxArticulationAttachment

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAttachments(): Int

}

fun PxArticulationSpatialTendonFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationSpatialTendon = js("_module.wrapPointer(ptr, _module.PxArticulationSpatialTendon)")

fun PxArticulationSpatialTendon.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationSpatialTendon.nbAttachments
    get() = getNbAttachments()

external interface PxArticulationTendon : PxBase {
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
     * @param stiffness WebIDL type: float
     */
    fun setLimitStiffness(stiffness: Float)

    /**
     * @return WebIDL type: float
     */
    fun getLimitStiffness(): Float

    /**
     * @param offset WebIDL type: float
     */
    fun setOffset(offset: Float)

    /**
     * @param offset   WebIDL type: float
     * @param autowake WebIDL type: boolean
     */
    fun setOffset(offset: Float, autowake: Boolean)

    /**
     * @return WebIDL type: float
     */
    fun getOffset(): Float

    /**
     * @return WebIDL type: [PxArticulationReducedCoordinate]
     */
    fun getArticulation(): PxArticulationReducedCoordinate

}

fun PxArticulationTendonFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationTendon = js("_module.wrapPointer(ptr, _module.PxArticulationTendon)")

fun PxArticulationTendon.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationTendon.articulation
    get() = getArticulation()

var PxArticulationTendon.stiffness
    get() = getStiffness()
    set(value) { setStiffness(value) }
var PxArticulationTendon.damping
    get() = getDamping()
    set(value) { setDamping(value) }
var PxArticulationTendon.limitStiffness
    get() = getLimitStiffness()
    set(value) { setLimitStiffness(value) }
var PxArticulationTendon.offset
    get() = getOffset()
    set(value) { setOffset(value) }

external interface PxArticulationTendonJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param axis             WebIDL type: [PxArticulationAxisEnum] (enum)
     * @param coefficient      WebIDL type: float
     * @param recipCoefficient WebIDL type: float
     */
    fun setCoefficient(axis: Int, coefficient: Float, recipCoefficient: Float)

    /**
     * @return WebIDL type: [PxArticulationLink]
     */
    fun getLink(): PxArticulationLink

    /**
     * @return WebIDL type: [PxArticulationTendonJoint]
     */
    fun getParent(): PxArticulationTendonJoint

    /**
     * @return WebIDL type: [PxArticulationFixedTendon]
     */
    fun getTendon(): PxArticulationFixedTendon

    fun release()

}

fun PxArticulationTendonJointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationTendonJoint = js("_module.wrapPointer(ptr, _module.PxArticulationTendonJoint)")

fun PxArticulationTendonJoint.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxArticulationTendonJoint.link
    get() = getLink()
val PxArticulationTendonJoint.parent
    get() = getParent()
val PxArticulationTendonJoint.tendon
    get() = getTendon()

external interface PxArticulationTendonLimit {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var lowLimit: Float
    /**
     * WebIDL type: float
     */
    var highLimit: Float
}

fun PxArticulationTendonLimitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxArticulationTendonLimit = js("_module.wrapPointer(ptr, _module.PxArticulationTendonLimit)")

fun PxArticulationTendonLimit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSpatialForce {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var force: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var torque: PxVec3
}

fun PxSpatialForceFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSpatialForce = js("_module.wrapPointer(ptr, _module.PxSpatialForce)")

fun PxSpatialForce.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSpatialVelocity {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var linear: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var angular: PxVec3
}

fun PxSpatialVelocityFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSpatialVelocity = js("_module.wrapPointer(ptr, _module.PxSpatialVelocity)")

fun PxSpatialVelocity.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGeomRaycastHit : PxLocationHit {
    /**
     * WebIDL type: float
     */
    var u: Float
    /**
     * WebIDL type: float
     */
    var v: Float

    /**
     * @return WebIDL type: boolean
     */
    fun hadInitialOverlap(): Boolean

}

fun PxGeomRaycastHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGeomRaycastHit = js("_module.wrapPointer(ptr, _module.PxGeomRaycastHit)")

fun PxGeomRaycastHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxGeomSweepHit : PxLocationHit {
    /**
     * @return WebIDL type: boolean
     */
    fun hadInitialOverlap(): Boolean

}

fun PxGeomSweepHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxGeomSweepHit = js("_module.wrapPointer(ptr, _module.PxGeomSweepHit)")

fun PxGeomSweepHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxHitFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxHitFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxHitFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxHitFlags = js("new _module.PxHitFlags(flags)")

fun PxHitFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxHitFlags = js("_module.wrapPointer(ptr, _module.PxHitFlags)")

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

fun PxLocationHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxLocationHit = js("_module.wrapPointer(ptr, _module.PxLocationHit)")

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

fun PxOverlapBuffer10(_module: dynamic = PhysXJsLoader.physXJs): PxOverlapBuffer10 = js("new _module.PxOverlapBuffer10()")

fun PxOverlapBuffer10FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxOverlapBuffer10 = js("_module.wrapPointer(ptr, _module.PxOverlapBuffer10)")

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

external interface PxOverlapResult : PxOverlapCallback {
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
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxOverlapHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxOverlapHit

}

fun PxOverlapResult(_module: dynamic = PhysXJsLoader.physXJs): PxOverlapResult = js("new _module.PxOverlapResult()")

fun PxOverlapResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxOverlapResult = js("_module.wrapPointer(ptr, _module.PxOverlapResult)")

fun PxOverlapResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxOverlapResult.nbAnyHits
    get() = getNbAnyHits()
val PxOverlapResult.nbTouches
    get() = getNbTouches()

external interface PxOverlapCallback {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxOverlapCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxOverlapCallback = js("_module.wrapPointer(ptr, _module.PxOverlapCallback)")

fun PxOverlapCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxOverlapHit : PxQueryHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxOverlapHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxOverlapHit = js("_module.wrapPointer(ptr, _module.PxOverlapHit)")

fun PxOverlapHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryFilterCallback

fun PxQueryFilterCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterCallback = js("_module.wrapPointer(ptr, _module.PxQueryFilterCallback)")

fun PxQueryFilterCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface SimpleQueryFilterCallback : PxQueryFilterCallback {
    /**
     * @param filterData WebIDL type: [PxFilterData] (Const, Ref)
     * @param shape      WebIDL type: [PxShape] (Const)
     * @param actor      WebIDL type: [PxRigidActor] (Const)
     * @param queryFlags WebIDL type: [PxHitFlags] (Ref)
     * @return WebIDL type: unsigned long
     */
    fun simplePreFilter(filterData: PxFilterData, shape: PxShape, actor: PxRigidActor, queryFlags: PxHitFlags): Int

    /**
     * @param filterData WebIDL type: [PxFilterData] (Const, Ref)
     * @param hit        WebIDL type: [PxQueryHit] (Const, Ref)
     * @param shape      WebIDL type: [PxShape] (Const)
     * @param actor      WebIDL type: [PxRigidActor] (Const)
     * @return WebIDL type: unsigned long
     */
    fun simplePostFilter(filterData: PxFilterData, hit: PxQueryHit, shape: PxShape, actor: PxRigidActor): Int

}

fun SimpleQueryFilterCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): SimpleQueryFilterCallback = js("_module.wrapPointer(ptr, _module.SimpleQueryFilterCallback)")

fun SimpleQueryFilterCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryFilterCallbackImpl : SimpleQueryFilterCallback {
    /**
     * param filterData WebIDL type: [PxFilterData] (Const, Ref)
     * param shape      WebIDL type: [PxShape] (Const)
     * param actor      WebIDL type: [PxRigidActor] (Const)
     * param queryFlags WebIDL type: [PxHitFlags] (Ref)
     * return WebIDL type: unsigned long
     */
    var simplePreFilter: (filterData: Int, shape: Int, actor: Int, queryFlags: Int) -> Int

    /**
     * param filterData WebIDL type: [PxFilterData] (Const, Ref)
     * param hit        WebIDL type: [PxQueryHit] (Const, Ref)
     * param shape      WebIDL type: [PxShape] (Const)
     * param actor      WebIDL type: [PxRigidActor] (Const)
     * return WebIDL type: unsigned long
     */
    var simplePostFilter: (filterData: Int, hit: Int, shape: Int, actor: Int) -> Int

}

fun PxQueryFilterCallbackImpl(_module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterCallbackImpl = js("new _module.PxQueryFilterCallbackImpl()")

external interface PxQueryFilterData {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var data: PxFilterData
    /**
     * WebIDL type: [PxQueryFlags] (Value)
     */
    var flags: PxQueryFlags
}

fun PxQueryFilterData(_module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData()")

/**
 * @param fd WebIDL type: [PxFilterData] (Const, Ref)
 * @param f  WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(fd: PxFilterData, f: PxQueryFlags, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData(fd, f)")

/**
 * @param f WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(f: PxQueryFlags, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData(f)")

fun PxQueryFilterDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFilterData = js("_module.wrapPointer(ptr, _module.PxQueryFilterData)")

fun PxQueryFilterData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxQueryFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxQueryFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFlags = js("new _module.PxQueryFlags(flags)")

fun PxQueryFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQueryFlags = js("_module.wrapPointer(ptr, _module.PxQueryFlags)")

fun PxQueryFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxQueryHit {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var faceIndex: Int
}

fun PxQueryHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxQueryHit = js("_module.wrapPointer(ptr, _module.PxQueryHit)")

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

fun PxRaycastBuffer10(_module: dynamic = PhysXJsLoader.physXJs): PxRaycastBuffer10 = js("new _module.PxRaycastBuffer10()")

fun PxRaycastBuffer10FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRaycastBuffer10 = js("_module.wrapPointer(ptr, _module.PxRaycastBuffer10)")

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

external interface PxRaycastResult : PxRaycastCallback {
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
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxRaycastHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxRaycastHit

}

fun PxRaycastResult(_module: dynamic = PhysXJsLoader.physXJs): PxRaycastResult = js("new _module.PxRaycastResult()")

fun PxRaycastResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRaycastResult = js("_module.wrapPointer(ptr, _module.PxRaycastResult)")

fun PxRaycastResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxRaycastResult.nbAnyHits
    get() = getNbAnyHits()
val PxRaycastResult.nbTouches
    get() = getNbTouches()

external interface PxRaycastCallback {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxRaycastCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRaycastCallback = js("_module.wrapPointer(ptr, _module.PxRaycastCallback)")

fun PxRaycastCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRaycastHit : PxGeomRaycastHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxRaycastHit(_module: dynamic = PhysXJsLoader.physXJs): PxRaycastHit = js("new _module.PxRaycastHit()")

fun PxRaycastHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRaycastHit = js("_module.wrapPointer(ptr, _module.PxRaycastHit)")

fun PxRaycastHit.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSceneQuerySystemBase {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param dynamicTreeRebuildRateHint WebIDL type: unsigned long
     */
    fun setDynamicTreeRebuildRateHint(dynamicTreeRebuildRateHint: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getDynamicTreeRebuildRateHint(): Int

    /**
     * @param prunerIndex WebIDL type: unsigned long
     */
    fun forceRebuildDynamicTree(prunerIndex: Int)

    /**
     * @param updateMode WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun setUpdateMode(updateMode: Int)

    /**
     * @return WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun getUpdateMode(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getStaticTimestamp(): Int

    fun flushUpdates()

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

}

fun PxSceneQuerySystemBaseFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneQuerySystemBase = js("_module.wrapPointer(ptr, _module.PxSceneQuerySystemBase)")

val PxSceneQuerySystemBase.staticTimestamp
    get() = getStaticTimestamp()

var PxSceneQuerySystemBase.dynamicTreeRebuildRateHint
    get() = getDynamicTreeRebuildRateHint()
    set(value) { setDynamicTreeRebuildRateHint(value) }
var PxSceneQuerySystemBase.updateMode
    get() = getUpdateMode()
    set(value) { setUpdateMode(value) }

external interface PxSceneSQSystem : PxSceneQuerySystemBase {
    /**
     * @param updateMode WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun setSceneQueryUpdateMode(updateMode: Int)

    /**
     * @return WebIDL type: [PxSceneQueryUpdateModeEnum] (enum)
     */
    fun getSceneQueryUpdateMode(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getSceneQueryStaticTimestamp(): Int

    fun flushQueryUpdates()

    /**
     * @param rebuildStaticStructure  WebIDL type: boolean
     * @param rebuildDynamicStructure WebIDL type: boolean
     */
    fun forceDynamicTreeRebuild(rebuildStaticStructure: Boolean, rebuildDynamicStructure: Boolean)

    /**
     * @return WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    fun getStaticStructure(): Int

    /**
     * @return WebIDL type: [PxPruningStructureTypeEnum] (enum)
     */
    fun getDynamicStructure(): Int

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

}

fun PxSceneSQSystemFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSceneSQSystem = js("_module.wrapPointer(ptr, _module.PxSceneSQSystem)")

val PxSceneSQSystem.sceneQueryStaticTimestamp
    get() = getSceneQueryStaticTimestamp()
val PxSceneSQSystem.staticStructure
    get() = getStaticStructure()
val PxSceneSQSystem.dynamicStructure
    get() = getDynamicStructure()

var PxSceneSQSystem.sceneQueryUpdateMode
    get() = getSceneQueryUpdateMode()
    set(value) { setSceneQueryUpdateMode(value) }

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

fun PxSweepBuffer10(_module: dynamic = PhysXJsLoader.physXJs): PxSweepBuffer10 = js("new _module.PxSweepBuffer10()")

fun PxSweepBuffer10FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSweepBuffer10 = js("_module.wrapPointer(ptr, _module.PxSweepBuffer10)")

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

external interface PxSweepResult : PxSweepCallback {
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
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [PxSweepHit] (Const, Ref)
     */
    fun getTouch(index: Int): PxSweepHit

}

fun PxSweepResult(_module: dynamic = PhysXJsLoader.physXJs): PxSweepResult = js("new _module.PxSweepResult()")

fun PxSweepResultFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSweepResult = js("_module.wrapPointer(ptr, _module.PxSweepResult)")

fun PxSweepResult.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxSweepResult.nbAnyHits
    get() = getNbAnyHits()
val PxSweepResult.nbTouches
    get() = getNbTouches()

external interface PxSweepCallback {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxSweepCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSweepCallback = js("_module.wrapPointer(ptr, _module.PxSweepCallback)")

fun PxSweepCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSweepHit : PxGeomSweepHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxSweepHit(_module: dynamic = PhysXJsLoader.physXJs): PxSweepHit = js("new _module.PxSweepHit()")

fun PxSweepHitFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSweepHit = js("_module.wrapPointer(ptr, _module.PxSweepHit)")

fun PxSweepHit.destroy() {
    PhysXJsLoader.destroy(this)
}

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
     * @param inflation WebIDL type: float
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(inflation: Float): PxBounds3

    /**
     * @param flag  WebIDL type: [PxActorFlagEnum] (enum)
     * @param value WebIDL type: boolean
     */
    fun setActorFlag(flag: Int, value: Boolean)

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

fun PxActorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxActor = js("_module.wrapPointer(ptr, _module.PxActor)")

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
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxActorFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxActorFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxActorFlags = js("new _module.PxActorFlags(flags)")

fun PxActorFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxActorFlags = js("_module.wrapPointer(ptr, _module.PxActorFlags)")

fun PxActorFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxActorTypeFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxActorTypeFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxActorTypeFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxActorTypeFlags = js("new _module.PxActorTypeFlags(flags)")

fun PxActorTypeFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxActorTypeFlags = js("_module.wrapPointer(ptr, _module.PxActorTypeFlags)")

fun PxActorTypeFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

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
     * @return WebIDL type: unsigned long
     */
    fun getNbShapes(): Int

    /**
     * @param userBuffer WebIDL type: [PxShapePtr]
     * @param bufferSize WebIDL type: unsigned long
     * @param startIndex WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getShapes(userBuffer: PxShapePtr, bufferSize: Int, startIndex: Int): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbConstraints(): Int

}

fun PxRigidActorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidActor = js("_module.wrapPointer(ptr, _module.PxRigidActor)")

val PxRigidActor.nbShapes
    get() = getNbShapes()
val PxRigidActor.nbConstraints
    get() = getNbConstraints()

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
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun getAngularVelocity(): PxVec3

    /**
     * @param maxLinVel WebIDL type: float
     */
    fun setMaxLinearVelocity(maxLinVel: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxLinearVelocity(): Float

    /**
     * @param maxAngVel WebIDL type: float
     */
    fun setMaxAngularVelocity(maxAngVel: Float)

    /**
     * @return WebIDL type: float
     */
    fun getMaxAngularVelocity(): Float

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
     * @param slopCoefficient WebIDL type: float
     */
    fun setContactSlopCoefficient(slopCoefficient: Float)

    /**
     * @return WebIDL type: float
     */
    fun getContactSlopCoefficient(): Float

}

fun PxRigidBodyFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidBody = js("_module.wrapPointer(ptr, _module.PxRigidBody)")

val PxRigidBody.invMass
    get() = getInvMass()
val PxRigidBody.massSpaceInvInertiaTensor
    get() = getMassSpaceInvInertiaTensor()
val PxRigidBody.linearVelocity
    get() = getLinearVelocity()
val PxRigidBody.angularVelocity
    get() = getAngularVelocity()

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
var PxRigidBody.contactSlopCoefficient
    get() = getContactSlopCoefficient()
    set(value) { setContactSlopCoefficient(value) }

external interface PxRigidBodyFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxRigidBodyFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxRigidBodyFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxRigidBodyFlags = js("new _module.PxRigidBodyFlags(flags)")

fun PxRigidBodyFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidBodyFlags = js("_module.wrapPointer(ptr, _module.PxRigidBodyFlags)")

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
     * @param linVel WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setLinearVelocity(linVel: PxVec3)

    /**
     * @param linVel   WebIDL type: [PxVec3] (Const, Ref)
     * @param autowake WebIDL type: boolean
     */
    fun setLinearVelocity(linVel: PxVec3, autowake: Boolean)

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

fun PxRigidDynamicFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidDynamic = js("_module.wrapPointer(ptr, _module.PxRigidDynamic)")

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
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxRigidDynamicLockFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxRigidDynamicLockFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxRigidDynamicLockFlags = js("new _module.PxRigidDynamicLockFlags(flags)")

fun PxRigidDynamicLockFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidDynamicLockFlags = js("_module.wrapPointer(ptr, _module.PxRigidDynamicLockFlags)")

fun PxRigidDynamicLockFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxRigidStatic : PxRigidActor

fun PxRigidStaticFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxRigidStatic = js("_module.wrapPointer(ptr, _module.PxRigidStatic)")

external interface PxShape : PxRefCounted {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param geometry WebIDL type: [PxGeometry] (Const, Ref)
     */
    fun setGeometry(geometry: PxGeometry)

    /**
     * @return WebIDL type: [PxGeometry] (Const, Ref)
     */
    fun getGeometry(): PxGeometry

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
     * @return WebIDL type: [PxBaseMaterial]
     */
    fun getMaterialFromInternalFaceIndex(faceIndex: Int): PxBaseMaterial

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

fun PxShapeFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxShape = js("_module.wrapPointer(ptr, _module.PxShape)")

val PxShape.actor
    get() = getActor()
val PxShape.nbMaterials
    get() = getNbMaterials()

var PxShape.geometry
    get() = getGeometry()
    set(value) { setGeometry(value) }
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

external interface PxShapeExt {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param shape WebIDL type: [PxShape] (Const, Ref)
     * @param actor WebIDL type: [PxRigidActor] (Const, Ref)
     * @return WebIDL type: [PxTransform] (Value)
     */
    fun getGlobalPose(shape: PxShape, actor: PxRigidActor): PxTransform

    /**
     * @param shape     WebIDL type: [PxShape] (Const, Ref)
     * @param actor     WebIDL type: [PxRigidActor] (Const, Ref)
     * @param rayOrigin WebIDL type: [PxVec3] (Const, Ref)
     * @param rayDir    WebIDL type: [PxVec3] (Const, Ref)
     * @param maxDist   WebIDL type: float
     * @param hitFlags  WebIDL type: [PxHitFlags] (Ref)
     * @param maxHits   WebIDL type: unsigned long
     * @param rayHits   WebIDL type: [PxRaycastHit]
     * @return WebIDL type: unsigned long
     */
    fun raycast(shape: PxShape, actor: PxRigidActor, rayOrigin: PxVec3, rayDir: PxVec3, maxDist: Float, hitFlags: PxHitFlags, maxHits: Int, rayHits: PxRaycastHit): Int

    /**
     * @param shape         WebIDL type: [PxShape] (Const, Ref)
     * @param actor         WebIDL type: [PxRigidActor] (Const, Ref)
     * @param otherGeom     WebIDL type: [PxGeometry] (Const, Ref)
     * @param otherGeomPose WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun overlap(shape: PxShape, actor: PxRigidActor, otherGeom: PxGeometry, otherGeomPose: PxTransform): Boolean

    /**
     * @param shape         WebIDL type: [PxShape] (Const, Ref)
     * @param actor         WebIDL type: [PxRigidActor] (Const, Ref)
     * @param unitDir       WebIDL type: [PxVec3] (Const, Ref)
     * @param distance      WebIDL type: float
     * @param otherGeom     WebIDL type: [PxGeometry] (Const, Ref)
     * @param otherGeomPose WebIDL type: [PxTransform] (Const, Ref)
     * @param sweepHit      WebIDL type: [PxSweepHit] (Ref)
     * @param hitFlags      WebIDL type: [PxHitFlags] (Ref)
     * @return WebIDL type: boolean
     */
    fun sweep(shape: PxShape, actor: PxRigidActor, unitDir: PxVec3, distance: Float, otherGeom: PxGeometry, otherGeomPose: PxTransform, sweepHit: PxSweepHit, hitFlags: PxHitFlags): Boolean

    /**
     * @param shape WebIDL type: [PxShape] (Const, Ref)
     * @param actor WebIDL type: [PxRigidActor] (Const, Ref)
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(shape: PxShape, actor: PxRigidActor): PxBounds3

    /**
     * @param shape     WebIDL type: [PxShape] (Const, Ref)
     * @param actor     WebIDL type: [PxRigidActor] (Const, Ref)
     * @param inflation WebIDL type: float
     * @return WebIDL type: [PxBounds3] (Value)
     */
    fun getWorldBounds(shape: PxShape, actor: PxRigidActor, inflation: Float): PxBounds3

}

fun PxShapeExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxShapeExt = js("_module.wrapPointer(ptr, _module.PxShapeExt)")

fun PxShapeExt.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxShapeFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxShapeFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxShapeFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxShapeFlags = js("new _module.PxShapeFlags(flags)")

fun PxShapeFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxShapeFlags = js("_module.wrapPointer(ptr, _module.PxShapeFlags)")

fun PxShapeFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxAggregate : PxBase {
    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor): Boolean

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @param bvh   WebIDL type: [PxBVH] (Const)
     * @return WebIDL type: boolean
     */
    fun addActor(actor: PxActor, bvh: PxBVH): Boolean

    /**
     * @param actor WebIDL type: [PxActor] (Ref)
     * @return WebIDL type: boolean
     */
    fun removeActor(actor: PxActor): Boolean

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     * @return WebIDL type: boolean
     */
    fun addArticulation(articulation: PxArticulationReducedCoordinate): Boolean

    /**
     * @param articulation WebIDL type: [PxArticulationReducedCoordinate] (Ref)
     * @return WebIDL type: boolean
     */
    fun removeArticulation(articulation: PxArticulationReducedCoordinate): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbActors(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbActors(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbShapes(): Int

    /**
     * @return WebIDL type: [PxScene]
     */
    fun getScene(): PxScene

    /**
     * @return WebIDL type: boolean
     */
    fun getSelfCollision(): Boolean

}

fun PxAggregateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxAggregate = js("_module.wrapPointer(ptr, _module.PxAggregate)")

val PxAggregate.nbActors
    get() = getNbActors()
val PxAggregate.maxNbActors
    get() = getMaxNbActors()
val PxAggregate.maxNbShapes
    get() = getMaxNbShapes()
val PxAggregate.scene
    get() = getScene()
val PxAggregate.selfCollision
    get() = getSelfCollision()

external interface PxBaseMaterial : PxRefCounted

fun PxBaseMaterialFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBaseMaterial = js("_module.wrapPointer(ptr, _module.PxBaseMaterial)")

fun PxBaseMaterial.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseCaps {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var mMaxNbRegions: Int
}

fun PxBroadPhaseCaps(_module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseCaps = js("new _module.PxBroadPhaseCaps()")

fun PxBroadPhaseCapsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseCaps = js("_module.wrapPointer(ptr, _module.PxBroadPhaseCaps)")

fun PxBroadPhaseCaps.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseRegion {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxBounds3] (Value)
     */
    var mBounds: PxBounds3
    /**
     * WebIDL type: VoidPtr
     */
    var mUserData: Any
}

fun PxBroadPhaseRegion(_module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseRegion = js("new _module.PxBroadPhaseRegion()")

fun PxBroadPhaseRegionFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseRegion = js("_module.wrapPointer(ptr, _module.PxBroadPhaseRegion)")

fun PxBroadPhaseRegion.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxBroadPhaseRegionInfo {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxBroadPhaseRegion] (Value)
     */
    var mRegion: PxBroadPhaseRegion
    /**
     * WebIDL type: unsigned long
     */
    var mNbStaticObjects: Int
    /**
     * WebIDL type: unsigned long
     */
    var mNbDynamicObjects: Int
    /**
     * WebIDL type: boolean
     */
    var mActive: Boolean
    /**
     * WebIDL type: boolean
     */
    var mOverlap: Boolean
}

fun PxBroadPhaseRegionInfo(_module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseRegionInfo = js("new _module.PxBroadPhaseRegionInfo()")

fun PxBroadPhaseRegionInfoFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxBroadPhaseRegionInfo = js("_module.wrapPointer(ptr, _module.PxBroadPhaseRegionInfo)")

fun PxBroadPhaseRegionInfo.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConstraint : PxBase {
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

fun PxConstraintFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConstraint = js("_module.wrapPointer(ptr, _module.PxConstraint)")

val PxConstraint.scene
    get() = getScene()

var PxConstraint.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxConstraint.minResponseThreshold
    get() = getMinResponseThreshold()
    set(value) { setMinResponseThreshold(value) }

external interface PxConstraintConnector {
    /**
     * Native object address.
     */
    val ptr: Int

    fun prepareData()

    fun updateOmniPvdProperties()

    fun onConstraintRelease()

    /**
     * @param actor WebIDL type: unsigned long
     */
    fun onComShift(actor: Int)

    /**
     * @param shift WebIDL type: [PxVec3] (Const, Ref)
     */
    fun onOriginShift(shift: PxVec3)

    /**
     * @return WebIDL type: [PxBase]
     */
    fun getSerializable(): PxBase

    /**
     * @return WebIDL type: [PxConstraintSolverPrep] (Value)
     */
    fun getPrep(): PxConstraintSolverPrep

    fun getConstantBlock()

    /**
     * @param constraint WebIDL type: [PxConstraint]
     */
    fun connectToConstraint(constraint: PxConstraint)

}

fun PxConstraintConnectorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConstraintConnector = js("_module.wrapPointer(ptr, _module.PxConstraintConnector)")

fun PxConstraintConnector.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxConstraintConnector.serializable
    get() = getSerializable()
val PxConstraintConnector.prep
    get() = getPrep()
val PxConstraintConnector.constantBlock
    get() = getConstantBlock()

external interface PxConstraintFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxConstraintFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxConstraintFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxConstraintFlags = js("new _module.PxConstraintFlags(flags)")

fun PxConstraintFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConstraintFlags = js("_module.wrapPointer(ptr, _module.PxConstraintFlags)")

fun PxConstraintFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConstraintInfo {
    /**
     * Native object address.
     */
    val ptr: Int

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

fun PxConstraintInfoFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConstraintInfo = js("_module.wrapPointer(ptr, _module.PxConstraintInfo)")

fun PxConstraintInfo.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxConstraintSolverPrep

fun PxConstraintSolverPrepFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxConstraintSolverPrep = js("_module.wrapPointer(ptr, _module.PxConstraintSolverPrep)")

fun PxConstraintSolverPrep.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairHeaderFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxContactPairHeaderFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxContactPairHeaderFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairHeaderFlags = js("new _module.PxContactPairHeaderFlags(flags)")

fun PxContactPairHeaderFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairHeaderFlags = js("_module.wrapPointer(ptr, _module.PxContactPairHeaderFlags)")

fun PxContactPairHeaderFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPair {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxShape]
     */
    fun get_shapes(index: Int): PxShape
    fun set_shapes(index: Int, value: PxShape)
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

    /**
     * @param userBuffer WebIDL type: [PxContactPairPoint]
     * @param bufferSize WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun extractContacts(userBuffer: PxContactPairPoint, bufferSize: Int): Int

}

fun PxContactPairFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPair = js("_module.wrapPointer(ptr, _module.PxContactPair)")

fun PxContactPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxContactPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxContactPairFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairFlags = js("new _module.PxContactPairFlags(flags)")

fun PxContactPairFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairFlags = js("_module.wrapPointer(ptr, _module.PxContactPairFlags)")

fun PxContactPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairHeader {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxActor]
     */
    fun get_actors(index: Int): PxActor
    fun set_actors(index: Int, value: PxActor)
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

fun PxContactPairHeaderFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairHeader = js("_module.wrapPointer(ptr, _module.PxContactPairHeader)")

fun PxContactPairHeader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxContactPairPoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var position: PxVec3
    /**
     * WebIDL type: float
     */
    var separation: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var normal: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var internalFaceIndex0: Int
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var impulse: PxVec3
    /**
     * WebIDL type: unsigned long
     */
    var internalFaceIndex1: Int
}

fun PxContactPairPointFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxContactPairPoint = js("_module.wrapPointer(ptr, _module.PxContactPairPoint)")

fun PxContactPairPoint.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxDominanceGroupPair {
    /**
     * Native object address.
     */
    val ptr: Int

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
fun PxDominanceGroupPair(a: Byte, b: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxDominanceGroupPair = js("new _module.PxDominanceGroupPair(a, b)")

fun PxDominanceGroupPairFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxDominanceGroupPair = js("_module.wrapPointer(ptr, _module.PxDominanceGroupPair)")

fun PxDominanceGroupPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxFilterData {
    /**
     * Native object address.
     */
    val ptr: Int

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

fun PxFilterData(_module: dynamic = PhysXJsLoader.physXJs): PxFilterData = js("new _module.PxFilterData()")

/**
 * @param w0 WebIDL type: unsigned long
 * @param w1 WebIDL type: unsigned long
 * @param w2 WebIDL type: unsigned long
 * @param w3 WebIDL type: unsigned long
 */
fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int, _module: dynamic = PhysXJsLoader.physXJs): PxFilterData = js("new _module.PxFilterData(w0, w1, w2, w3)")

fun PxFilterDataFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxFilterData = js("_module.wrapPointer(ptr, _module.PxFilterData)")

fun PxFilterData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxMaterial : PxBaseMaterial {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any

    /**
     * @param coef WebIDL type: float
     */
    fun setDynamicFriction(coef: Float)

    /**
     * @return WebIDL type: float
     */
    fun getDynamicFriction(): Float

    /**
     * @param coef WebIDL type: float
     */
    fun setStaticFriction(coef: Float)

    /**
     * @return WebIDL type: float
     */
    fun getStaticFriction(): Float

    /**
     * @param coef WebIDL type: float
     */
    fun setRestitution(coef: Float)

    /**
     * @return WebIDL type: float
     */
    fun getRestitution(): Float

    /**
     * @param flag WebIDL type: [PxMaterialFlagEnum] (enum)
     * @param b    WebIDL type: boolean
     */
    fun setFlag(flag: Int, b: Boolean)

    /**
     * @param flags WebIDL type: [PxMaterialFlags] (Ref)
     */
    fun setFlags(flags: PxMaterialFlags)

    /**
     * @return WebIDL type: [PxMaterialFlags] (Value)
     */
    fun getFlags(): PxMaterialFlags

    /**
     * @param combMode WebIDL type: [PxCombineModeEnum] (enum)
     */
    fun setFrictionCombineMode(combMode: Int)

    /**
     * @return WebIDL type: [PxCombineModeEnum] (enum)
     */
    fun getFrictionCombineMode(): Int

    /**
     * @param combMode WebIDL type: [PxCombineModeEnum] (enum)
     */
    fun setRestitutionCombineMode(combMode: Int)

    /**
     * @return WebIDL type: [PxCombineModeEnum] (enum)
     */
    fun getRestitutionCombineMode(): Int

}

fun PxMaterialFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMaterial = js("_module.wrapPointer(ptr, _module.PxMaterial)")

var PxMaterial.dynamicFriction
    get() = getDynamicFriction()
    set(value) { setDynamicFriction(value) }
var PxMaterial.staticFriction
    get() = getStaticFriction()
    set(value) { setStaticFriction(value) }
var PxMaterial.restitution
    get() = getRestitution()
    set(value) { setRestitution(value) }
var PxMaterial.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxMaterial.frictionCombineMode
    get() = getFrictionCombineMode()
    set(value) { setFrictionCombineMode(value) }
var PxMaterial.restitutionCombineMode
    get() = getRestitutionCombineMode()
    set(value) { setRestitutionCombineMode(value) }

external interface PxMaterialFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxMaterialFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxMaterialFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxMaterialFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxMaterialFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxMaterialFlags = js("new _module.PxMaterialFlags(flags)")

fun PxMaterialFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxMaterialFlags = js("_module.wrapPointer(ptr, _module.PxMaterialFlags)")

fun PxMaterialFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPairFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned short
 */
fun PxPairFlags(flags: Short, _module: dynamic = PhysXJsLoader.physXJs): PxPairFlags = js("new _module.PxPairFlags(flags)")

fun PxPairFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPairFlags = js("_module.wrapPointer(ptr, _module.PxPairFlags)")

fun PxPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxPhysics {
    /**
     * Native object address.
     */
    val ptr: Int

    fun release()

    /**
     * @return WebIDL type: [PxFoundation] (Ref)
     */
    fun getFoundation(): PxFoundation

    /**
     * @param maxActor            WebIDL type: unsigned long
     * @param maxShape            WebIDL type: unsigned long
     * @param enableSelfCollision WebIDL type: boolean
     * @return WebIDL type: [PxAggregate]
     */
    fun createAggregate(maxActor: Int, maxShape: Int, enableSelfCollision: Boolean): PxAggregate

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
     * @param stream WebIDL type: [PxInputData] (Ref)
     * @return WebIDL type: [PxTriangleMesh]
     */
    fun createTriangleMesh(stream: PxInputData): PxTriangleMesh

    /**
     * @param stream WebIDL type: [PxInputData] (Ref)
     * @return WebIDL type: [PxConvexMesh]
     */
    fun createConvexMesh(stream: PxInputData): PxConvexMesh

    /**
     * @return WebIDL type: long
     */
    fun getNbShapes(): Int

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
     * @return WebIDL type: [PxInsertionCallback] (Ref)
     */
    fun getPhysicsInsertionCallback(): PxInsertionCallback

}

fun PxPhysicsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxPhysics = js("_module.wrapPointer(ptr, _module.PxPhysics)")

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

external interface PxSimulationEventCallback

fun PxSimulationEventCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSimulationEventCallback = js("_module.wrapPointer(ptr, _module.PxSimulationEventCallback)")

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

fun SimpleSimulationEventCallbackFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): SimpleSimulationEventCallback = js("_module.wrapPointer(ptr, _module.SimpleSimulationEventCallback)")

fun SimpleSimulationEventCallback.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimulationEventCallbackImpl : SimpleSimulationEventCallback {
    /**
     * param constraints WebIDL type: [PxConstraintInfo]
     * param count       WebIDL type: unsigned long
     */
    var onConstraintBreak: (constraints: Int, count: Int) -> Unit

    /**
     * param actors WebIDL type: [PxActorPtr]
     * param count  WebIDL type: unsigned long
     */
    var onWake: (actors: Int, count: Int) -> Unit

    /**
     * param actors WebIDL type: [PxActorPtr]
     * param count  WebIDL type: unsigned long
     */
    var onSleep: (actors: Int, count: Int) -> Unit

    /**
     * param pairHeader WebIDL type: [PxContactPairHeader] (Const, Ref)
     * param pairs      WebIDL type: [PxContactPair] (Const)
     * param nbPairs    WebIDL type: unsigned long
     */
    var onContact: (pairHeader: Int, pairs: Int, nbPairs: Int) -> Unit

    /**
     * param pairs WebIDL type: [PxTriggerPair]
     * param count WebIDL type: unsigned long
     */
    var onTrigger: (pairs: Int, count: Int) -> Unit

}

fun PxSimulationEventCallbackImpl(_module: dynamic = PhysXJsLoader.physXJs): PxSimulationEventCallbackImpl = js("new _module.PxSimulationEventCallbackImpl()")

external interface PxSimulationFilterShader

fun PxSimulationFilterShaderFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSimulationFilterShader = js("_module.wrapPointer(ptr, _module.PxSimulationFilterShader)")

fun PxSimulationFilterShader.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxSimulationStatistics {
    /**
     * Native object address.
     */
    val ptr: Int

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
    fun get_nbShapes(index: Int): Int
    fun set_nbShapes(index: Int, value: Int)
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

fun PxSimulationStatisticsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxSimulationStatistics = js("_module.wrapPointer(ptr, _module.PxSimulationStatistics)")

fun PxSimulationStatistics.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriggerPair {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxShape]
     */
    var triggerShape: PxShape
    /**
     * WebIDL type: [PxActor]
     */
    var triggerActor: PxActor
    /**
     * WebIDL type: [PxShape]
     */
    var otherShape: PxShape
    /**
     * WebIDL type: [PxActor]
     */
    var otherActor: PxActor
    /**
     * WebIDL type: [PxPairFlagEnum] (enum)
     */
    var status: Int
    /**
     * WebIDL type: [PxTriggerPairFlags] (Value)
     */
    var flags: PxTriggerPairFlags
}

fun PxTriggerPairFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriggerPair = js("_module.wrapPointer(ptr, _module.PxTriggerPair)")

fun PxTriggerPair.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxTriggerPairFlags {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     */
    fun raise(flag: Int)

    /**
     * @param flag WebIDL type: [PxTriggerPairFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: octet
 */
fun PxTriggerPairFlags(flags: Byte, _module: dynamic = PhysXJsLoader.physXJs): PxTriggerPairFlags = js("new _module.PxTriggerPairFlags(flags)")

fun PxTriggerPairFlagsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTriggerPairFlags = js("_module.wrapPointer(ptr, _module.PxTriggerPairFlags)")

fun PxTriggerPairFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

object PxSceneFlagEnum {
    val eENABLE_ACTIVE_ACTORS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS()
    val eENABLE_CCD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_CCD()
    val eDISABLE_CCD_RESWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP()
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
    val eENABLE_DIRECT_GPU_API: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eENABLE_DIRECT_GPU_API()
    val eMUTABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS()
}

object PxSceneQueryUpdateModeEnum {
    val eBUILD_ENABLED_COMMIT_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_ENABLED()
    val eBUILD_ENABLED_COMMIT_DISABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_DISABLED()
    val eBUILD_DISABLED_COMMIT_DISABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_DISABLED_COMMIT_DISABLED()
}

object PxArticulationAxisEnum {
    val eTWIST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eTWIST()
    val eSWING1: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eSWING1()
    val eSWING2: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eSWING2()
    val eX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eX()
    val eY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eY()
    val eZ: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationAxisEnum_eZ()
}

object PxArticulationCacheFlagEnum {
    val eVELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eVELOCITY()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eACCELERATION()
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_ePOSITION()
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eFORCE()
    val eLINK_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_VELOCITY()
    val eLINK_ACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_ACCELERATION()
    val eROOT_TRANSFORM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eROOT_TRANSFORM()
    val eROOT_VELOCITIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eROOT_VELOCITIES()
    val eLINK_INCOMING_JOINT_FORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_INCOMING_JOINT_FORCE()
    val eJOINT_TARGET_POSITIONS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eJOINT_TARGET_POSITIONS()
    val eJOINT_TARGET_VELOCITIES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eJOINT_TARGET_VELOCITIES()
    val eALL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationCacheFlagEnum_eALL()
}

object PxArticulationDriveTypeEnum {
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eFORCE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eACCELERATION()
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationDriveTypeEnum_eNONE()
}

object PxArticulationFlagEnum {
    val eFIX_BASE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationFlagEnum_eFIX_BASE()
    val eDRIVE_LIMITS_ARE_FORCES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationFlagEnum_eDRIVE_LIMITS_ARE_FORCES()
    val eDISABLE_SELF_COLLISION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationFlagEnum_eDISABLE_SELF_COLLISION()
}

object PxArticulationJointTypeEnum {
    val eFIX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eFIX()
    val ePRISMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_ePRISMATIC()
    val eREVOLUTE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eREVOLUTE()
    val eSPHERICAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eSPHERICAL()
    val eUNDEFINED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationJointTypeEnum_eUNDEFINED()
}

object PxArticulationKinematicFlagEnum {
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationKinematicFlagEnum_ePOSITION()
    val eVELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationKinematicFlagEnum_eVELOCITY()
}

object PxArticulationMotionEnum {
    val eLOCKED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eLOCKED()
    val eLIMITED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eLIMITED()
    val eFREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxArticulationMotionEnum_eFREE()
}

object PxHitFlagEnum {
    val ePOSITION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePOSITION()
    val eNORMAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eNORMAL()
    val eUV: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eUV()
    val eASSUME_NO_INITIAL_OVERLAP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP()
    val eANY_HIT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eANY_HIT()
    val eMESH_MULTIPLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE()
    val eMESH_BOTH_SIDES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES()
    val ePRECISE_SWEEP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP()
    val eMTD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMTD()
    val eFACE_INDEX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eFACE_INDEX()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eDEFAULT()
    val eMODIFIABLE_FLAGS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS()
}

object PxQueryFlagEnum {
    val eSTATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eSTATIC()
    val eDYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eDYNAMIC()
    val ePREFILTER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_ePREFILTER()
    val ePOSTFILTER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_ePOSTFILTER()
    val eANY_HIT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eANY_HIT()
    val eNO_BLOCK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryFlagEnum_eNO_BLOCK()
}

object PxQueryHitType {
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryHitType_eNONE()
    val eBLOCK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryHitType_eBLOCK()
    val eTOUCH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxQueryHitType_eTOUCH()
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
    val eDEFORMABLE_SURFACE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eDEFORMABLE_SURFACE()
    val eDEFORMABLE_VOLUME: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eDEFORMABLE_VOLUME()
    val eSOFTBODY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_eSOFTBODY()
    val ePBD_PARTICLESYSTEM: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeEnum_ePBD_PARTICLESYSTEM()
}

object PxActorTypeFlagEnum {
    val eRIGID_STATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeFlagEnum_eRIGID_STATIC()
    val eRIGID_DYNAMIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxActorTypeFlagEnum_eRIGID_DYNAMIC()
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

object PxShapeFlagEnum {
    val eSIMULATION_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxShapeFlagEnum_eVISUALIZATION()
}

object PxBroadPhaseTypeEnum {
    val eSAP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eSAP()
    val eMBP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eMBP()
    val eABP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eABP()
    val ePABP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_ePABP()
    val eGPU: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBroadPhaseTypeEnum_eGPU()
}

object PxBVHBuildStrategyEnum {
    val eFAST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBVHBuildStrategyEnum_eFAST()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBVHBuildStrategyEnum_eDEFAULT()
    val eSAH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxBVHBuildStrategyEnum_eSAH()
}

object PxCombineModeEnum {
    val eAVERAGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCombineModeEnum_eAVERAGE()
    val eMIN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCombineModeEnum_eMIN()
    val eMULTIPLY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCombineModeEnum_eMULTIPLY()
    val eMAX: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxCombineModeEnum_eMAX()
}

object PxConstraintFlagEnum {
    val eBROKEN: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eBROKEN()
    val eCOLLISION_ENABLED: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eCOLLISION_ENABLED()
    val eVISUALIZATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eVISUALIZATION()
    val eDRIVE_LIMITS_ARE_FORCES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eDRIVE_LIMITS_ARE_FORCES()
    val eIMPROVED_SLERP: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eIMPROVED_SLERP()
    val eDISABLE_PREPROCESSING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eDISABLE_PREPROCESSING()
    val eENABLE_EXTENDED_LIMITS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eENABLE_EXTENDED_LIMITS()
    val eGPU_COMPATIBLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eGPU_COMPATIBLE()
    val eALWAYS_UPDATE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eALWAYS_UPDATE()
    val eDISABLE_CONSTRAINT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxConstraintFlagEnum_eDISABLE_CONSTRAINT()
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

object PxDynamicTreeSecondaryPrunerEnum {
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eNONE()
    val eBUCKET: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eBUCKET()
    val eINCREMENTAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eINCREMENTAL()
    val eBVH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eBVH()
}

object PxFilterFlagEnum {
    val eKILL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterFlagEnum_eKILL()
    val eSUPPRESS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterFlagEnum_eSUPPRESS()
    val eCALLBACK: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterFlagEnum_eCALLBACK()
    val eNOTIFY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterFlagEnum_eNOTIFY()
    val eDEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterFlagEnum_eDEFAULT()
}

object PxFilterObjectFlagEnum {
    val eKINEMATIC: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterObjectFlagEnum_eKINEMATIC()
    val eTRIGGER: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFilterObjectFlagEnum_eTRIGGER()
}

object PxForceModeEnum {
    val eFORCE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eFORCE()
    val eIMPULSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eIMPULSE()
    val eVELOCITY_CHANGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxForceModeEnum_eACCELERATION()
}

object PxFrictionTypeEnum {
    val ePATCH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_ePATCH()
    val eFRICTION_COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxFrictionTypeEnum_eFRICTION_COUNT()
}

object PxMaterialFlagEnum {
    val eDISABLE_FRICTION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMaterialFlagEnum_eDISABLE_FRICTION()
    val eDISABLE_STRONG_FRICTION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMaterialFlagEnum_eDISABLE_STRONG_FRICTION()
    val eCOMPLIANT_ACCELERATION_SPRING: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxMaterialFlagEnum_eCOMPLIANT_ACCELERATION_SPRING()
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
    val eTRIGGER_DEFAULT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPairFlagEnum_eTRIGGER_DEFAULT()
}

object PxPruningStructureTypeEnum {
    val eNONE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eNONE()
    val eDYNAMIC_AABB_TREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eDYNAMIC_AABB_TREE()
    val eSTATIC_AABB_TREE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxPruningStructureTypeEnum_eSTATIC_AABB_TREE()
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

