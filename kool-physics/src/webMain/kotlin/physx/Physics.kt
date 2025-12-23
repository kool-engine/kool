/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxScene : JsAny, PxSceneSQSystem {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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
    fun setFilterShaderData(data: JsAny, dataSize: Int)

    /**
     * @return WebIDL type: VoidPtr (Const)
     */
    fun getFilterShaderData(): JsAny

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
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny, scratchMemBlockSize: Int): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun simulate(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny, scratchMemBlockSize: Int, controlSimulation: Boolean): Boolean

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
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny, scratchMemBlockSize: Int): Boolean

    /**
     * @param elapsedTime         WebIDL type: float
     * @param completionTask      WebIDL type: [PxBaseTask]
     * @param scratchMemBlock     WebIDL type: VoidPtr
     * @param scratchMemBlockSize WebIDL type: unsigned long
     * @param controlSimulation   WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun collide(elapsedTime: Float, completionTask: PxBaseTask, scratchMemBlock: JsAny, scratchMemBlockSize: Int, controlSimulation: Boolean): Boolean

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

    /**
     * @return WebIDL type: [PxPvdSceneClient]
     */
    fun getScenePvdClient(): PxPvdSceneClient

}

fun PxSceneFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxScene = js("_module.wrapPointer(ptr, _module.PxScene)")

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
val PxScene.kinematicKinematicFilteringMode: PxPairFilteringModeEnum
    get() = PxPairFilteringModeEnum.forValue(getKinematicKinematicFilteringMode())
val PxScene.staticKinematicFilteringMode: PxPairFilteringModeEnum
    get() = PxPairFilteringModeEnum.forValue(getStaticKinematicFilteringMode())
val PxScene.frictionType: PxFrictionTypeEnum
    get() = PxFrictionTypeEnum.forValue(getFrictionType())
val PxScene.solverType: PxSolverTypeEnum
    get() = PxSolverTypeEnum.forValue(getSolverType())
val PxScene.renderBuffer
    get() = getRenderBuffer()
val PxScene.broadPhaseType: PxBroadPhaseTypeEnum
    get() = PxBroadPhaseTypeEnum.forValue(getBroadPhaseType())
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
val PxScene.scenePvdClient
    get() = getScenePvdClient()

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

fun PxScene.setVisualizationParameter(param: PxVisualizationParameterEnum, value: Float) = setVisualizationParameter(param.value, value)
fun PxScene.getVisualizationParameter(paramEnum: PxVisualizationParameterEnum) = getVisualizationParameter(paramEnum.value)
fun PxScene.setFlag(flag: PxSceneFlagEnum, value: Boolean) = setFlag(flag.value, value)

external interface PxSceneDesc : JsAny, DestroyableNative {
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
    var filterShaderData: JsAny
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
    var userData: JsAny
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
fun PxSceneDesc(scale: PxTolerancesScale, _module: JsAny = PhysXJsLoader.physXJs): PxSceneDesc = js("new _module.PxSceneDesc(scale)")

fun PxSceneDescFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneDesc = js("_module.wrapPointer(ptr, _module.PxSceneDesc)")

var PxSceneDesc.kineKineFilteringModeEnum: PxPairFilteringModeEnum
    get() = PxPairFilteringModeEnum.forValue(kineKineFilteringMode)
    set(value) { kineKineFilteringMode = value.value }
var PxSceneDesc.staticKineFilteringModeEnum: PxPairFilteringModeEnum
    get() = PxPairFilteringModeEnum.forValue(staticKineFilteringMode)
    set(value) { staticKineFilteringMode = value.value }
var PxSceneDesc.broadPhaseTypeEnum: PxBroadPhaseTypeEnum
    get() = PxBroadPhaseTypeEnum.forValue(broadPhaseType)
    set(value) { broadPhaseType = value.value }
var PxSceneDesc.frictionTypeEnum: PxFrictionTypeEnum
    get() = PxFrictionTypeEnum.forValue(frictionType)
    set(value) { frictionType = value.value }
var PxSceneDesc.solverTypeEnum: PxSolverTypeEnum
    get() = PxSolverTypeEnum.forValue(solverType)
    set(value) { solverType = value.value }
var PxSceneDesc.staticStructureEnum: PxPruningStructureTypeEnum
    get() = PxPruningStructureTypeEnum.forValue(staticStructure)
    set(value) { staticStructure = value.value }
var PxSceneDesc.dynamicStructureEnum: PxPruningStructureTypeEnum
    get() = PxPruningStructureTypeEnum.forValue(dynamicStructure)
    set(value) { dynamicStructure = value.value }
var PxSceneDesc.dynamicTreeSecondaryPrunerEnum: PxDynamicTreeSecondaryPrunerEnum
    get() = PxDynamicTreeSecondaryPrunerEnum.forValue(dynamicTreeSecondaryPruner)
    set(value) { dynamicTreeSecondaryPruner = value.value }
var PxSceneDesc.staticBVHBuildStrategyEnum: PxBVHBuildStrategyEnum
    get() = PxBVHBuildStrategyEnum.forValue(staticBVHBuildStrategy)
    set(value) { staticBVHBuildStrategy = value.value }
var PxSceneDesc.dynamicBVHBuildStrategyEnum: PxBVHBuildStrategyEnum
    get() = PxBVHBuildStrategyEnum.forValue(dynamicBVHBuildStrategy)
    set(value) { dynamicBVHBuildStrategy = value.value }
var PxSceneDesc.sceneQueryUpdateModeEnum: PxSceneQueryUpdateModeEnum
    get() = PxSceneQueryUpdateModeEnum.forValue(sceneQueryUpdateMode)
    set(value) { sceneQueryUpdateMode = value.value }

external interface PxSceneFlags : JsAny, DestroyableNative {
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
fun PxSceneFlags(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneFlags = js("new _module.PxSceneFlags(flags)")

fun PxSceneFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneFlags = js("_module.wrapPointer(ptr, _module.PxSceneFlags)")

fun PxSceneFlags.isSet(flag: PxSceneFlagEnum) = isSet(flag.value)
fun PxSceneFlags.raise(flag: PxSceneFlagEnum) = raise(flag.value)
fun PxSceneFlags.clear(flag: PxSceneFlagEnum) = clear(flag.value)

external interface PxSceneLimits : JsAny, DestroyableNative {
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

fun PxSceneLimits(_module: JsAny = PhysXJsLoader.physXJs): PxSceneLimits = js("new _module.PxSceneLimits()")

fun PxSceneLimitsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneLimits = js("_module.wrapPointer(ptr, _module.PxSceneLimits)")

external interface PxArticulationAttachment : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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

fun PxArticulationAttachmentFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationAttachment = js("_module.wrapPointer(ptr, _module.PxArticulationAttachment)")

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

external interface PxArticulationCache : JsAny, DestroyableNative {
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
    var scratchMemory: JsAny
    /**
     * WebIDL type: VoidPtr
     */
    var scratchAllocator: JsAny
    /**
     * WebIDL type: unsigned long
     */
    var version: Int

    fun release()

}

fun PxArticulationCacheFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationCache = js("_module.wrapPointer(ptr, _module.PxArticulationCache)")

external interface PxArticulationCacheFlags : JsAny, DestroyableNative {
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
fun PxArticulationCacheFlags(flags: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationCacheFlags = js("new _module.PxArticulationCacheFlags(flags)")

fun PxArticulationCacheFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationCacheFlags = js("_module.wrapPointer(ptr, _module.PxArticulationCacheFlags)")

fun PxArticulationCacheFlags.isSet(flag: PxArticulationCacheFlagEnum) = isSet(flag.value)
fun PxArticulationCacheFlags.raise(flag: PxArticulationCacheFlagEnum) = raise(flag.value)
fun PxArticulationCacheFlags.clear(flag: PxArticulationCacheFlagEnum) = clear(flag.value)

external interface PxArticulationDrive : JsAny, DestroyableNative {
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

fun PxArticulationDrive(_module: JsAny = PhysXJsLoader.physXJs): PxArticulationDrive = js("new _module.PxArticulationDrive()")

/**
 * @param stiffness WebIDL type: float
 * @param damping   WebIDL type: float
 * @param maxForce  WebIDL type: float
 * @param driveType WebIDL type: [PxArticulationDriveTypeEnum] (enum)
 */
fun PxArticulationDrive(stiffness: Float, damping: Float, maxForce: Float, driveType: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationDrive = js("new _module.PxArticulationDrive(stiffness, damping, maxForce, driveType)")

fun PxArticulationDriveFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationDrive = js("_module.wrapPointer(ptr, _module.PxArticulationDrive)")

var PxArticulationDrive.driveTypeEnum: PxArticulationDriveTypeEnum
    get() = PxArticulationDriveTypeEnum.forValue(driveType)
    set(value) { driveType = value.value }

external interface PxArticulationFixedTendon : JsAny, DestroyableNative, PxArticulationTendon {
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

fun PxArticulationFixedTendonFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationFixedTendon = js("_module.wrapPointer(ptr, _module.PxArticulationFixedTendon)")

val PxArticulationFixedTendon.nbTendonJoints
    get() = getNbTendonJoints()

var PxArticulationFixedTendon.restLength
    get() = getRestLength()
    set(value) { setRestLength(value) }
var PxArticulationFixedTendon.limitParameters
    get() = getLimitParameters()
    set(value) { setLimitParameters(value) }

fun PxArticulationFixedTendon.createTendonJoint(parent: PxArticulationTendonJoint, axis: PxArticulationAxisEnum, coefficient: Float, recipCoefficient: Float, link: PxArticulationLink) = createTendonJoint(parent, axis.value, coefficient, recipCoefficient, link)

external interface PxArticulationFlags : JsAny, DestroyableNative {
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
fun PxArticulationFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationFlags = js("new _module.PxArticulationFlags(flags)")

fun PxArticulationFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationFlags = js("_module.wrapPointer(ptr, _module.PxArticulationFlags)")

fun PxArticulationFlags.isSet(flag: PxArticulationFlagEnum) = isSet(flag.value)
fun PxArticulationFlags.raise(flag: PxArticulationFlagEnum) = raise(flag.value)
fun PxArticulationFlags.clear(flag: PxArticulationFlagEnum) = clear(flag.value)

external interface PxArticulationJointReducedCoordinate : JsAny, DestroyableNative, PxBase {
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

fun PxArticulationJointReducedCoordinateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationJointReducedCoordinate = js("_module.wrapPointer(ptr, _module.PxArticulationJointReducedCoordinate)")

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
var PxArticulationJointReducedCoordinate.jointType: PxArticulationJointTypeEnum
    get() = PxArticulationJointTypeEnum.forValue(getJointType())
    set(value) { setJointType(value.value) }
var PxArticulationJointReducedCoordinate.frictionCoefficient
    get() = getFrictionCoefficient()
    set(value) { setFrictionCoefficient(value) }
var PxArticulationJointReducedCoordinate.maxJointVelocity
    get() = getMaxJointVelocity()
    set(value) { setMaxJointVelocity(value) }

fun PxArticulationJointReducedCoordinate.setJointType(jointType: PxArticulationJointTypeEnum) = setJointType(jointType.value)
fun PxArticulationJointReducedCoordinate.setMotion(axis: PxArticulationAxisEnum, motion: PxArticulationMotionEnum) = setMotion(axis.value, motion.value)
fun PxArticulationJointReducedCoordinate.getMotion(axis: PxArticulationAxisEnum) = PxArticulationMotionEnum.forValue(getMotion(axis.value))
fun PxArticulationJointReducedCoordinate.setLimitParams(axis: PxArticulationAxisEnum, limit: PxArticulationLimit) = setLimitParams(axis.value, limit)
fun PxArticulationJointReducedCoordinate.getLimitParams(axis: PxArticulationAxisEnum) = getLimitParams(axis.value)
fun PxArticulationJointReducedCoordinate.setDriveParams(axis: PxArticulationAxisEnum, drive: PxArticulationDrive) = setDriveParams(axis.value, drive)
fun PxArticulationJointReducedCoordinate.setDriveTarget(axis: PxArticulationAxisEnum, target: Float) = setDriveTarget(axis.value, target)
fun PxArticulationJointReducedCoordinate.setDriveTarget(axis: PxArticulationAxisEnum, target: Float, autowake: Boolean) = setDriveTarget(axis.value, target, autowake)
fun PxArticulationJointReducedCoordinate.getDriveTarget(axis: PxArticulationAxisEnum) = getDriveTarget(axis.value)
fun PxArticulationJointReducedCoordinate.setDriveVelocity(axis: PxArticulationAxisEnum, targetVel: Float) = setDriveVelocity(axis.value, targetVel)
fun PxArticulationJointReducedCoordinate.setDriveVelocity(axis: PxArticulationAxisEnum, targetVel: Float, autowake: Boolean) = setDriveVelocity(axis.value, targetVel, autowake)
fun PxArticulationJointReducedCoordinate.getDriveVelocity(axis: PxArticulationAxisEnum) = getDriveVelocity(axis.value)
fun PxArticulationJointReducedCoordinate.setArmature(axis: PxArticulationAxisEnum, armature: Float) = setArmature(axis.value, armature)
fun PxArticulationJointReducedCoordinate.getArmature(axis: PxArticulationAxisEnum) = getArmature(axis.value)
fun PxArticulationJointReducedCoordinate.setJointPosition(axis: PxArticulationAxisEnum, jointPos: Float) = setJointPosition(axis.value, jointPos)
fun PxArticulationJointReducedCoordinate.getJointPosition(axis: PxArticulationAxisEnum) = getJointPosition(axis.value)
fun PxArticulationJointReducedCoordinate.setJointVelocity(axis: PxArticulationAxisEnum, jointVel: Float) = setJointVelocity(axis.value, jointVel)
fun PxArticulationJointReducedCoordinate.getJointVelocity(axis: PxArticulationAxisEnum) = getJointVelocity(axis.value)

external interface PxArticulationKinematicFlags : JsAny, DestroyableNative {
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
fun PxArticulationKinematicFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationKinematicFlags = js("new _module.PxArticulationKinematicFlags(flags)")

fun PxArticulationKinematicFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationKinematicFlags = js("_module.wrapPointer(ptr, _module.PxArticulationKinematicFlags)")

fun PxArticulationKinematicFlags.isSet(flag: PxArticulationKinematicFlagEnum) = isSet(flag.value)
fun PxArticulationKinematicFlags.raise(flag: PxArticulationKinematicFlagEnum) = raise(flag.value)
fun PxArticulationKinematicFlags.clear(flag: PxArticulationKinematicFlagEnum) = clear(flag.value)

external interface PxArticulationLink : JsAny, PxRigidBody {
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

fun PxArticulationLinkFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationLink = js("_module.wrapPointer(ptr, _module.PxArticulationLink)")

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

external interface PxArticulationLimit : JsAny, DestroyableNative {
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

fun PxArticulationLimit(_module: JsAny = PhysXJsLoader.physXJs): PxArticulationLimit = js("new _module.PxArticulationLimit()")

/**
 * @param low  WebIDL type: float
 * @param high WebIDL type: float
 */
fun PxArticulationLimit(low: Float, high: Float, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationLimit = js("new _module.PxArticulationLimit(low, high)")

fun PxArticulationLimitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationLimit = js("_module.wrapPointer(ptr, _module.PxArticulationLimit)")

external interface PxArticulationRootLinkData : JsAny, DestroyableNative {
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

fun PxArticulationRootLinkData(_module: JsAny = PhysXJsLoader.physXJs): PxArticulationRootLinkData = js("new _module.PxArticulationRootLinkData()")

fun PxArticulationRootLinkDataFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationRootLinkData = js("_module.wrapPointer(ptr, _module.PxArticulationRootLinkData)")

external interface PxArticulationReducedCoordinate : JsAny, DestroyableNative, PxBase {
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

fun PxArticulationReducedCoordinateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationReducedCoordinate = js("_module.wrapPointer(ptr, _module.PxArticulationReducedCoordinate)")

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

fun PxArticulationReducedCoordinate.setArticulationFlag(flag: PxArticulationFlagEnum, value: Boolean) = setArticulationFlag(flag.value, value)

external interface PxArticulationSpatialTendon : JsAny, DestroyableNative, PxArticulationTendon {
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

fun PxArticulationSpatialTendonFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationSpatialTendon = js("_module.wrapPointer(ptr, _module.PxArticulationSpatialTendon)")

val PxArticulationSpatialTendon.nbAttachments
    get() = getNbAttachments()

external interface PxArticulationTendon : JsAny, DestroyableNative, PxBase {
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

fun PxArticulationTendonFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationTendon = js("_module.wrapPointer(ptr, _module.PxArticulationTendon)")

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

external interface PxArticulationTendonJoint : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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

fun PxArticulationTendonJointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationTendonJoint = js("_module.wrapPointer(ptr, _module.PxArticulationTendonJoint)")

val PxArticulationTendonJoint.link
    get() = getLink()
val PxArticulationTendonJoint.parent
    get() = getParent()
val PxArticulationTendonJoint.tendon
    get() = getTendon()

fun PxArticulationTendonJoint.setCoefficient(axis: PxArticulationAxisEnum, coefficient: Float, recipCoefficient: Float) = setCoefficient(axis.value, coefficient, recipCoefficient)

external interface PxArticulationTendonLimit : JsAny, DestroyableNative {
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

fun PxArticulationTendonLimitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxArticulationTendonLimit = js("_module.wrapPointer(ptr, _module.PxArticulationTendonLimit)")

external interface PxSpatialForce : JsAny, DestroyableNative {
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

fun PxSpatialForceFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSpatialForce = js("_module.wrapPointer(ptr, _module.PxSpatialForce)")

external interface PxSpatialVelocity : JsAny, DestroyableNative {
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

fun PxSpatialVelocityFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSpatialVelocity = js("_module.wrapPointer(ptr, _module.PxSpatialVelocity)")

external interface PxGeomRaycastHit : JsAny, DestroyableNative, PxLocationHit {
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

fun PxGeomRaycastHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGeomRaycastHit = js("_module.wrapPointer(ptr, _module.PxGeomRaycastHit)")

external interface PxGeomSweepHit : JsAny, DestroyableNative, PxLocationHit {
    /**
     * @return WebIDL type: boolean
     */
    fun hadInitialOverlap(): Boolean

}

fun PxGeomSweepHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxGeomSweepHit = js("_module.wrapPointer(ptr, _module.PxGeomSweepHit)")

external interface PxHitFlags : JsAny, DestroyableNative {
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
fun PxHitFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxHitFlags = js("new _module.PxHitFlags(flags)")

fun PxHitFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxHitFlags = js("_module.wrapPointer(ptr, _module.PxHitFlags)")

fun PxHitFlags.isSet(flag: PxHitFlagEnum) = isSet(flag.value)
fun PxHitFlags.raise(flag: PxHitFlagEnum) = raise(flag.value)
fun PxHitFlags.clear(flag: PxHitFlagEnum) = clear(flag.value)

external interface PxLocationHit : JsAny, DestroyableNative, PxQueryHit {
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

fun PxLocationHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxLocationHit = js("_module.wrapPointer(ptr, _module.PxLocationHit)")

external interface PxOverlapBuffer10 : JsAny, DestroyableNative, PxOverlapCallback {
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

fun PxOverlapBuffer10(_module: JsAny = PhysXJsLoader.physXJs): PxOverlapBuffer10 = js("new _module.PxOverlapBuffer10()")

fun PxOverlapBuffer10FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxOverlapBuffer10 = js("_module.wrapPointer(ptr, _module.PxOverlapBuffer10)")

val PxOverlapBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxOverlapBuffer10.nbTouches
    get() = getNbTouches()
val PxOverlapBuffer10.touches
    get() = getTouches()
val PxOverlapBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxOverlapResult : JsAny, DestroyableNative, PxOverlapCallback {
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

    fun clear()

}

fun PxOverlapResult(_module: JsAny = PhysXJsLoader.physXJs): PxOverlapResult = js("new _module.PxOverlapResult()")

fun PxOverlapResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxOverlapResult = js("_module.wrapPointer(ptr, _module.PxOverlapResult)")

val PxOverlapResult.nbAnyHits
    get() = getNbAnyHits()
val PxOverlapResult.nbTouches
    get() = getNbTouches()

external interface PxOverlapCallback : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxOverlapCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxOverlapCallback = js("_module.wrapPointer(ptr, _module.PxOverlapCallback)")

external interface PxOverlapHit : JsAny, DestroyableNative, PxQueryHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxOverlapHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxOverlapHit = js("_module.wrapPointer(ptr, _module.PxOverlapHit)")

external interface PxQueryFilterCallback : JsAny, DestroyableNative

fun PxQueryFilterCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterCallback = js("_module.wrapPointer(ptr, _module.PxQueryFilterCallback)")

external interface SimpleQueryFilterCallback : JsAny, DestroyableNative, PxQueryFilterCallback {
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

fun SimpleQueryFilterCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SimpleQueryFilterCallback = js("_module.wrapPointer(ptr, _module.SimpleQueryFilterCallback)")

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

fun PxQueryFilterCallbackImpl(_module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterCallbackImpl = js("new _module.PxQueryFilterCallbackImpl()")

external interface PxQueryFilterData : JsAny, DestroyableNative {
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

fun PxQueryFilterData(_module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData()")

/**
 * @param fd WebIDL type: [PxFilterData] (Const, Ref)
 * @param f  WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(fd: PxFilterData, f: PxQueryFlags, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData(fd, f)")

/**
 * @param f WebIDL type: [PxQueryFlags] (Ref)
 */
fun PxQueryFilterData(f: PxQueryFlags, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterData = js("new _module.PxQueryFilterData(f)")

fun PxQueryFilterDataFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFilterData = js("_module.wrapPointer(ptr, _module.PxQueryFilterData)")

external interface PxQueryFlags : JsAny, DestroyableNative {
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
fun PxQueryFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFlags = js("new _module.PxQueryFlags(flags)")

fun PxQueryFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxQueryFlags = js("_module.wrapPointer(ptr, _module.PxQueryFlags)")

fun PxQueryFlags.isSet(flag: PxQueryFlagEnum) = isSet(flag.value)
fun PxQueryFlags.raise(flag: PxQueryFlagEnum) = raise(flag.value)
fun PxQueryFlags.clear(flag: PxQueryFlagEnum) = clear(flag.value)

external interface PxQueryHit : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var faceIndex: Int
}

fun PxQueryHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxQueryHit = js("_module.wrapPointer(ptr, _module.PxQueryHit)")

external interface PxRaycastBuffer10 : JsAny, DestroyableNative, PxRaycastCallback {
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

fun PxRaycastBuffer10(_module: JsAny = PhysXJsLoader.physXJs): PxRaycastBuffer10 = js("new _module.PxRaycastBuffer10()")

fun PxRaycastBuffer10FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRaycastBuffer10 = js("_module.wrapPointer(ptr, _module.PxRaycastBuffer10)")

val PxRaycastBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxRaycastBuffer10.nbTouches
    get() = getNbTouches()
val PxRaycastBuffer10.touches
    get() = getTouches()
val PxRaycastBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxRaycastResult : JsAny, DestroyableNative, PxRaycastCallback {
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

    fun clear()

}

fun PxRaycastResult(_module: JsAny = PhysXJsLoader.physXJs): PxRaycastResult = js("new _module.PxRaycastResult()")

fun PxRaycastResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRaycastResult = js("_module.wrapPointer(ptr, _module.PxRaycastResult)")

val PxRaycastResult.nbAnyHits
    get() = getNbAnyHits()
val PxRaycastResult.nbTouches
    get() = getNbTouches()

external interface PxRaycastCallback : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxRaycastCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRaycastCallback = js("_module.wrapPointer(ptr, _module.PxRaycastCallback)")

external interface PxRaycastHit : JsAny, DestroyableNative, PxGeomRaycastHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxRaycastHit(_module: JsAny = PhysXJsLoader.physXJs): PxRaycastHit = js("new _module.PxRaycastHit()")

fun PxRaycastHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRaycastHit = js("_module.wrapPointer(ptr, _module.PxRaycastHit)")

external interface PxSceneQuerySystemBase : JsAny {
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

fun PxSceneQuerySystemBaseFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneQuerySystemBase = js("_module.wrapPointer(ptr, _module.PxSceneQuerySystemBase)")

val PxSceneQuerySystemBase.staticTimestamp
    get() = getStaticTimestamp()

var PxSceneQuerySystemBase.dynamicTreeRebuildRateHint
    get() = getDynamicTreeRebuildRateHint()
    set(value) { setDynamicTreeRebuildRateHint(value) }
var PxSceneQuerySystemBase.updateMode: PxSceneQueryUpdateModeEnum
    get() = PxSceneQueryUpdateModeEnum.forValue(getUpdateMode())
    set(value) { setUpdateMode(value.value) }

fun PxSceneQuerySystemBase.setUpdateMode(updateMode: PxSceneQueryUpdateModeEnum) = setUpdateMode(updateMode.value)

external interface PxSceneSQSystem : JsAny, PxSceneQuerySystemBase {
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

fun PxSceneSQSystemFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSceneSQSystem = js("_module.wrapPointer(ptr, _module.PxSceneSQSystem)")

val PxSceneSQSystem.sceneQueryStaticTimestamp
    get() = getSceneQueryStaticTimestamp()
val PxSceneSQSystem.staticStructure: PxPruningStructureTypeEnum
    get() = PxPruningStructureTypeEnum.forValue(getStaticStructure())
val PxSceneSQSystem.dynamicStructure: PxPruningStructureTypeEnum
    get() = PxPruningStructureTypeEnum.forValue(getDynamicStructure())

var PxSceneSQSystem.sceneQueryUpdateMode: PxSceneQueryUpdateModeEnum
    get() = PxSceneQueryUpdateModeEnum.forValue(getSceneQueryUpdateMode())
    set(value) { setSceneQueryUpdateMode(value.value) }

fun PxSceneSQSystem.setSceneQueryUpdateMode(updateMode: PxSceneQueryUpdateModeEnum) = setSceneQueryUpdateMode(updateMode.value)

external interface PxSweepBuffer10 : JsAny, DestroyableNative, PxSweepCallback {
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

fun PxSweepBuffer10(_module: JsAny = PhysXJsLoader.physXJs): PxSweepBuffer10 = js("new _module.PxSweepBuffer10()")

fun PxSweepBuffer10FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSweepBuffer10 = js("_module.wrapPointer(ptr, _module.PxSweepBuffer10)")

val PxSweepBuffer10.nbAnyHits
    get() = getNbAnyHits()
val PxSweepBuffer10.nbTouches
    get() = getNbTouches()
val PxSweepBuffer10.touches
    get() = getTouches()
val PxSweepBuffer10.maxNbTouches
    get() = getMaxNbTouches()

external interface PxSweepResult : JsAny, DestroyableNative, PxSweepCallback {
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

    fun clear()

}

fun PxSweepResult(_module: JsAny = PhysXJsLoader.physXJs): PxSweepResult = js("new _module.PxSweepResult()")

fun PxSweepResultFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSweepResult = js("_module.wrapPointer(ptr, _module.PxSweepResult)")

val PxSweepResult.nbAnyHits
    get() = getNbAnyHits()
val PxSweepResult.nbTouches
    get() = getNbTouches()

external interface PxSweepCallback : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: boolean
     */
    fun hasAnyHits(): Boolean

}

fun PxSweepCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSweepCallback = js("_module.wrapPointer(ptr, _module.PxSweepCallback)")

external interface PxSweepHit : JsAny, DestroyableNative, PxGeomSweepHit {
    /**
     * WebIDL type: [PxRigidActor]
     */
    var actor: PxRigidActor
    /**
     * WebIDL type: [PxShape]
     */
    var shape: PxShape
}

fun PxSweepHit(_module: JsAny = PhysXJsLoader.physXJs): PxSweepHit = js("new _module.PxSweepHit()")

fun PxSweepHitFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSweepHit = js("_module.wrapPointer(ptr, _module.PxSweepHit)")

external interface PxActor : JsAny, PxBase {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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

fun PxActorFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxActor = js("_module.wrapPointer(ptr, _module.PxActor)")

val PxActor.type: PxActorTypeEnum
    get() = PxActorTypeEnum.forValue(getType())
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

fun PxActor.setActorFlag(flag: PxActorFlagEnum, value: Boolean) = setActorFlag(flag.value, value)

external interface PxActorFlags : JsAny, DestroyableNative {
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
fun PxActorFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxActorFlags = js("new _module.PxActorFlags(flags)")

fun PxActorFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxActorFlags = js("_module.wrapPointer(ptr, _module.PxActorFlags)")

fun PxActorFlags.isSet(flag: PxActorFlagEnum) = isSet(flag.value)
fun PxActorFlags.raise(flag: PxActorFlagEnum) = raise(flag.value)
fun PxActorFlags.clear(flag: PxActorFlagEnum) = clear(flag.value)

external interface PxActorTypeFlags : JsAny, DestroyableNative {
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
fun PxActorTypeFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxActorTypeFlags = js("new _module.PxActorTypeFlags(flags)")

fun PxActorTypeFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxActorTypeFlags = js("_module.wrapPointer(ptr, _module.PxActorTypeFlags)")

fun PxActorTypeFlags.isSet(flag: PxActorTypeFlagEnum) = isSet(flag.value)
fun PxActorTypeFlags.raise(flag: PxActorTypeFlagEnum) = raise(flag.value)
fun PxActorTypeFlags.clear(flag: PxActorTypeFlagEnum) = clear(flag.value)

external interface PxRigidActor : JsAny, PxActor {
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

fun PxRigidActorFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidActor = js("_module.wrapPointer(ptr, _module.PxRigidActor)")

val PxRigidActor.nbShapes
    get() = getNbShapes()
val PxRigidActor.nbConstraints
    get() = getNbConstraints()

var PxRigidActor.globalPose
    get() = getGlobalPose()
    set(value) { setGlobalPose(value) }

external interface PxRigidBody : JsAny, PxRigidActor {
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

fun PxRigidBodyFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidBody = js("_module.wrapPointer(ptr, _module.PxRigidBody)")

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

fun PxRigidBody.addForce(force: PxVec3, mode: PxForceModeEnum) = addForce(force, mode.value)
fun PxRigidBody.addForce(force: PxVec3, mode: PxForceModeEnum, autowake: Boolean) = addForce(force, mode.value, autowake)
fun PxRigidBody.addTorque(torque: PxVec3, mode: PxForceModeEnum) = addTorque(torque, mode.value)
fun PxRigidBody.addTorque(torque: PxVec3, mode: PxForceModeEnum, autowake: Boolean) = addTorque(torque, mode.value, autowake)
fun PxRigidBody.clearForce(mode: PxForceModeEnum) = clearForce(mode.value)
fun PxRigidBody.clearTorque(mode: PxForceModeEnum) = clearTorque(mode.value)
fun PxRigidBody.setForceAndTorque(force: PxVec3, torque: PxVec3, mode: PxForceModeEnum) = setForceAndTorque(force, torque, mode.value)
fun PxRigidBody.setRigidBodyFlag(flag: PxRigidBodyFlagEnum, value: Boolean) = setRigidBodyFlag(flag.value, value)

external interface PxRigidBodyFlags : JsAny, DestroyableNative {
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
fun PxRigidBodyFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxRigidBodyFlags = js("new _module.PxRigidBodyFlags(flags)")

fun PxRigidBodyFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidBodyFlags = js("_module.wrapPointer(ptr, _module.PxRigidBodyFlags)")

fun PxRigidBodyFlags.isSet(flag: PxRigidBodyFlagEnum) = isSet(flag.value)
fun PxRigidBodyFlags.raise(flag: PxRigidBodyFlagEnum) = raise(flag.value)
fun PxRigidBodyFlags.clear(flag: PxRigidBodyFlagEnum) = clear(flag.value)

external interface PxRigidDynamic : JsAny, PxRigidBody {
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

fun PxRigidDynamicFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidDynamic = js("_module.wrapPointer(ptr, _module.PxRigidDynamic)")

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

fun PxRigidDynamic.setRigidDynamicLockFlag(flag: PxRigidDynamicLockFlagEnum, value: Boolean) = setRigidDynamicLockFlag(flag.value, value)

external interface PxRigidDynamicLockFlags : JsAny, DestroyableNative {
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
fun PxRigidDynamicLockFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxRigidDynamicLockFlags = js("new _module.PxRigidDynamicLockFlags(flags)")

fun PxRigidDynamicLockFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidDynamicLockFlags = js("_module.wrapPointer(ptr, _module.PxRigidDynamicLockFlags)")

fun PxRigidDynamicLockFlags.isSet(flag: PxRigidDynamicLockFlagEnum) = isSet(flag.value)
fun PxRigidDynamicLockFlags.raise(flag: PxRigidDynamicLockFlagEnum) = raise(flag.value)
fun PxRigidDynamicLockFlags.clear(flag: PxRigidDynamicLockFlagEnum) = clear(flag.value)

external interface PxRigidStatic : JsAny, PxRigidActor

fun PxRigidStaticFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxRigidStatic = js("_module.wrapPointer(ptr, _module.PxRigidStatic)")

external interface PxShape : JsAny, PxRefCounted {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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

fun PxShapeFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxShape = js("_module.wrapPointer(ptr, _module.PxShape)")

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

fun PxShape.setFlag(flag: PxShapeFlagEnum, value: Boolean) = setFlag(flag.value, value)

external interface PxShapeExt : JsAny, DestroyableNative {
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

fun PxShapeExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxShapeExt = js("_module.wrapPointer(ptr, _module.PxShapeExt)")

external interface PxShapeFlags : JsAny, DestroyableNative {
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
fun PxShapeFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxShapeFlags = js("new _module.PxShapeFlags(flags)")

fun PxShapeFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxShapeFlags = js("_module.wrapPointer(ptr, _module.PxShapeFlags)")

fun PxShapeFlags.isSet(flag: PxShapeFlagEnum) = isSet(flag.value)
fun PxShapeFlags.raise(flag: PxShapeFlagEnum) = raise(flag.value)
fun PxShapeFlags.clear(flag: PxShapeFlagEnum) = clear(flag.value)

external interface PxAggregate : JsAny, PxBase {
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

fun PxAggregateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxAggregate = js("_module.wrapPointer(ptr, _module.PxAggregate)")

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

external interface PxBaseMaterial : JsAny, DestroyableNative, PxRefCounted

fun PxBaseMaterialFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBaseMaterial = js("_module.wrapPointer(ptr, _module.PxBaseMaterial)")

external interface PxBroadPhaseCaps : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var mMaxNbRegions: Int
}

fun PxBroadPhaseCaps(_module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseCaps = js("new _module.PxBroadPhaseCaps()")

fun PxBroadPhaseCapsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseCaps = js("_module.wrapPointer(ptr, _module.PxBroadPhaseCaps)")

external interface PxBroadPhaseRegion : JsAny, DestroyableNative {
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
    var mUserData: JsAny
}

fun PxBroadPhaseRegion(_module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseRegion = js("new _module.PxBroadPhaseRegion()")

fun PxBroadPhaseRegionFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseRegion = js("_module.wrapPointer(ptr, _module.PxBroadPhaseRegion)")

external interface PxBroadPhaseRegionInfo : JsAny, DestroyableNative {
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

fun PxBroadPhaseRegionInfo(_module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseRegionInfo = js("new _module.PxBroadPhaseRegionInfo()")

fun PxBroadPhaseRegionInfoFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxBroadPhaseRegionInfo = js("_module.wrapPointer(ptr, _module.PxBroadPhaseRegionInfo)")

external interface PxConstraint : JsAny, PxBase {
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

fun PxConstraintFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConstraint = js("_module.wrapPointer(ptr, _module.PxConstraint)")

val PxConstraint.scene
    get() = getScene()

var PxConstraint.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxConstraint.minResponseThreshold
    get() = getMinResponseThreshold()
    set(value) { setMinResponseThreshold(value) }

fun PxConstraint.setFlag(flag: PxConstraintFlagEnum, value: Boolean) = setFlag(flag.value, value)

external interface PxConstraintConnector : JsAny, DestroyableNative {
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

fun PxConstraintConnectorFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConstraintConnector = js("_module.wrapPointer(ptr, _module.PxConstraintConnector)")

val PxConstraintConnector.serializable
    get() = getSerializable()
val PxConstraintConnector.prep
    get() = getPrep()
val PxConstraintConnector.constantBlock
    get() = getConstantBlock()

external interface PxConstraintFlags : JsAny, DestroyableNative {
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
fun PxConstraintFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxConstraintFlags = js("new _module.PxConstraintFlags(flags)")

fun PxConstraintFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConstraintFlags = js("_module.wrapPointer(ptr, _module.PxConstraintFlags)")

fun PxConstraintFlags.isSet(flag: PxConstraintFlagEnum) = isSet(flag.value)
fun PxConstraintFlags.raise(flag: PxConstraintFlagEnum) = raise(flag.value)
fun PxConstraintFlags.clear(flag: PxConstraintFlagEnum) = clear(flag.value)

external interface PxConstraintInfo : JsAny, DestroyableNative {
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
    var externalReference: JsAny
    /**
     * WebIDL type: unsigned long
     */
    var type: Int
}

fun PxConstraintInfoFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConstraintInfo = js("_module.wrapPointer(ptr, _module.PxConstraintInfo)")

external interface PxConstraintSolverPrep : JsAny, DestroyableNative

fun PxConstraintSolverPrepFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxConstraintSolverPrep = js("_module.wrapPointer(ptr, _module.PxConstraintSolverPrep)")

external interface PxContactPairHeaderFlags : JsAny, DestroyableNative {
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
fun PxContactPairHeaderFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairHeaderFlags = js("new _module.PxContactPairHeaderFlags(flags)")

fun PxContactPairHeaderFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairHeaderFlags = js("_module.wrapPointer(ptr, _module.PxContactPairHeaderFlags)")

fun PxContactPairHeaderFlags.isSet(flag: PxContactPairHeaderFlagEnum) = isSet(flag.value)
fun PxContactPairHeaderFlags.raise(flag: PxContactPairHeaderFlagEnum) = raise(flag.value)
fun PxContactPairHeaderFlags.clear(flag: PxContactPairHeaderFlagEnum) = clear(flag.value)

external interface PxContactPair : JsAny, DestroyableNative {
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

fun PxContactPairFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPair = js("_module.wrapPointer(ptr, _module.PxContactPair)")

inline fun PxContactPair.getShapes(index: Int) = get_shapes(index)
inline fun PxContactPair.setShapes(index: Int, value: PxShape) = set_shapes(index, value)

external interface PxContactPairFlags : JsAny, DestroyableNative {
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
fun PxContactPairFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairFlags = js("new _module.PxContactPairFlags(flags)")

fun PxContactPairFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairFlags = js("_module.wrapPointer(ptr, _module.PxContactPairFlags)")

fun PxContactPairFlags.isSet(flag: PxContactPairFlagEnum) = isSet(flag.value)
fun PxContactPairFlags.raise(flag: PxContactPairFlagEnum) = raise(flag.value)
fun PxContactPairFlags.clear(flag: PxContactPairFlagEnum) = clear(flag.value)

external interface PxContactPairHeader : JsAny, DestroyableNative {
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

fun PxContactPairHeaderFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairHeader = js("_module.wrapPointer(ptr, _module.PxContactPairHeader)")

inline fun PxContactPairHeader.getActors(index: Int) = get_actors(index)
inline fun PxContactPairHeader.setActors(index: Int, value: PxActor) = set_actors(index, value)

external interface PxContactPairPoint : JsAny, DestroyableNative {
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

fun PxContactPairPointFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxContactPairPoint = js("_module.wrapPointer(ptr, _module.PxContactPairPoint)")

external interface PxDominanceGroupPair : JsAny, DestroyableNative {
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
fun PxDominanceGroupPair(a: Byte, b: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxDominanceGroupPair = js("new _module.PxDominanceGroupPair(a, b)")

fun PxDominanceGroupPairFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxDominanceGroupPair = js("_module.wrapPointer(ptr, _module.PxDominanceGroupPair)")

external interface PxFilterData : JsAny, DestroyableNative {
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

fun PxFilterData(_module: JsAny = PhysXJsLoader.physXJs): PxFilterData = js("new _module.PxFilterData()")

/**
 * @param w0 WebIDL type: unsigned long
 * @param w1 WebIDL type: unsigned long
 * @param w2 WebIDL type: unsigned long
 * @param w3 WebIDL type: unsigned long
 */
fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int, _module: JsAny = PhysXJsLoader.physXJs): PxFilterData = js("new _module.PxFilterData(w0, w1, w2, w3)")

fun PxFilterDataFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxFilterData = js("_module.wrapPointer(ptr, _module.PxFilterData)")

external interface PxMaterial : JsAny, PxBaseMaterial {
    /**
     * WebIDL type: VoidPtr
     */
    var userData: JsAny

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

fun PxMaterialFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMaterial = js("_module.wrapPointer(ptr, _module.PxMaterial)")

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
var PxMaterial.frictionCombineMode: PxCombineModeEnum
    get() = PxCombineModeEnum.forValue(getFrictionCombineMode())
    set(value) { setFrictionCombineMode(value.value) }
var PxMaterial.restitutionCombineMode: PxCombineModeEnum
    get() = PxCombineModeEnum.forValue(getRestitutionCombineMode())
    set(value) { setRestitutionCombineMode(value.value) }

fun PxMaterial.setFlag(flag: PxMaterialFlagEnum, b: Boolean) = setFlag(flag.value, b)
fun PxMaterial.setFrictionCombineMode(combMode: PxCombineModeEnum) = setFrictionCombineMode(combMode.value)
fun PxMaterial.setRestitutionCombineMode(combMode: PxCombineModeEnum) = setRestitutionCombineMode(combMode.value)

external interface PxMaterialFlags : JsAny, DestroyableNative {
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
fun PxMaterialFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxMaterialFlags = js("new _module.PxMaterialFlags(flags)")

fun PxMaterialFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxMaterialFlags = js("_module.wrapPointer(ptr, _module.PxMaterialFlags)")

fun PxMaterialFlags.isSet(flag: PxMaterialFlagEnum) = isSet(flag.value)
fun PxMaterialFlags.raise(flag: PxMaterialFlagEnum) = raise(flag.value)
fun PxMaterialFlags.clear(flag: PxMaterialFlagEnum) = clear(flag.value)

external interface PxPairFlags : JsAny, DestroyableNative {
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
fun PxPairFlags(flags: Short, _module: JsAny = PhysXJsLoader.physXJs): PxPairFlags = js("new _module.PxPairFlags(flags)")

fun PxPairFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPairFlags = js("_module.wrapPointer(ptr, _module.PxPairFlags)")

fun PxPairFlags.isSet(flag: PxPairFlagEnum) = isSet(flag.value)
fun PxPairFlags.raise(flag: PxPairFlagEnum) = raise(flag.value)
fun PxPairFlags.clear(flag: PxPairFlagEnum) = clear(flag.value)

external interface PxPhysics : JsAny, DestroyableNative {
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

fun PxPhysicsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxPhysics = js("_module.wrapPointer(ptr, _module.PxPhysics)")

val PxPhysics.foundation
    get() = getFoundation()
val PxPhysics.tolerancesScale
    get() = getTolerancesScale()
val PxPhysics.nbShapes
    get() = getNbShapes()
val PxPhysics.physicsInsertionCallback
    get() = getPhysicsInsertionCallback()

external interface PxSimulationEventCallback : JsAny, DestroyableNative

fun PxSimulationEventCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSimulationEventCallback = js("_module.wrapPointer(ptr, _module.PxSimulationEventCallback)")

external interface SimpleSimulationEventCallback : JsAny, DestroyableNative, PxSimulationEventCallback {
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

fun SimpleSimulationEventCallbackFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): SimpleSimulationEventCallback = js("_module.wrapPointer(ptr, _module.SimpleSimulationEventCallback)")

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

fun PxSimulationEventCallbackImpl(_module: JsAny = PhysXJsLoader.physXJs): PxSimulationEventCallbackImpl = js("new _module.PxSimulationEventCallbackImpl()")

external interface PxSimulationFilterShader : JsAny, DestroyableNative

fun PxSimulationFilterShaderFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSimulationFilterShader = js("_module.wrapPointer(ptr, _module.PxSimulationFilterShader)")

external interface PxSimulationStatistics : JsAny, DestroyableNative {
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

fun PxSimulationStatisticsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxSimulationStatistics = js("_module.wrapPointer(ptr, _module.PxSimulationStatistics)")

inline fun PxSimulationStatistics.getNbShapes(index: Int) = get_nbShapes(index)
inline fun PxSimulationStatistics.setNbShapes(index: Int, value: Int) = set_nbShapes(index, value)

external interface PxTriggerPair : JsAny, DestroyableNative {
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

fun PxTriggerPairFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriggerPair = js("_module.wrapPointer(ptr, _module.PxTriggerPair)")

var PxTriggerPair.statusEnum: PxPairFlagEnum
    get() = PxPairFlagEnum.forValue(status)
    set(value) { status = value.value }

external interface PxTriggerPairFlags : JsAny, DestroyableNative {
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
fun PxTriggerPairFlags(flags: Byte, _module: JsAny = PhysXJsLoader.physXJs): PxTriggerPairFlags = js("new _module.PxTriggerPairFlags(flags)")

fun PxTriggerPairFlagsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxTriggerPairFlags = js("_module.wrapPointer(ptr, _module.PxTriggerPairFlags)")

fun PxTriggerPairFlags.isSet(flag: PxTriggerPairFlagEnum) = isSet(flag.value)
fun PxTriggerPairFlags.raise(flag: PxTriggerPairFlagEnum) = raise(flag.value)
fun PxTriggerPairFlags.clear(flag: PxTriggerPairFlagEnum) = clear(flag.value)

value class PxSceneFlagEnum private constructor(val value: Int) {
    companion object {
        val eENABLE_ACTIVE_ACTORS: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS(PhysXJsLoader.physXJs))
        val eENABLE_CCD: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_CCD(PhysXJsLoader.physXJs))
        val eDISABLE_CCD_RESWEEP: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eDISABLE_CCD_RESWEEP(PhysXJsLoader.physXJs))
        val eENABLE_PCM: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_PCM(PhysXJsLoader.physXJs))
        val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE(PhysXJsLoader.physXJs))
        val eDISABLE_CONTACT_CACHE: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eDISABLE_CONTACT_CACHE(PhysXJsLoader.physXJs))
        val eREQUIRE_RW_LOCK: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eREQUIRE_RW_LOCK(PhysXJsLoader.physXJs))
        val eENABLE_STABILIZATION: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_STABILIZATION(PhysXJsLoader.physXJs))
        val eENABLE_AVERAGE_POINT: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_AVERAGE_POINT(PhysXJsLoader.physXJs))
        val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS(PhysXJsLoader.physXJs))
        val eENABLE_GPU_DYNAMICS: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_GPU_DYNAMICS(PhysXJsLoader.physXJs))
        val eENABLE_ENHANCED_DETERMINISM: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM(PhysXJsLoader.physXJs))
        val eENABLE_FRICTION_EVERY_ITERATION: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION(PhysXJsLoader.physXJs))
        val eENABLE_DIRECT_GPU_API: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eENABLE_DIRECT_GPU_API(PhysXJsLoader.physXJs))
        val eMUTABLE_FLAGS: PxSceneFlagEnum = PxSceneFlagEnum(PxSceneFlagEnum_eMUTABLE_FLAGS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eENABLE_ACTIVE_ACTORS.value -> eENABLE_ACTIVE_ACTORS
            eENABLE_CCD.value -> eENABLE_CCD
            eDISABLE_CCD_RESWEEP.value -> eDISABLE_CCD_RESWEEP
            eENABLE_PCM.value -> eENABLE_PCM
            eDISABLE_CONTACT_REPORT_BUFFER_RESIZE.value -> eDISABLE_CONTACT_REPORT_BUFFER_RESIZE
            eDISABLE_CONTACT_CACHE.value -> eDISABLE_CONTACT_CACHE
            eREQUIRE_RW_LOCK.value -> eREQUIRE_RW_LOCK
            eENABLE_STABILIZATION.value -> eENABLE_STABILIZATION
            eENABLE_AVERAGE_POINT.value -> eENABLE_AVERAGE_POINT
            eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS.value -> eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS
            eENABLE_GPU_DYNAMICS.value -> eENABLE_GPU_DYNAMICS
            eENABLE_ENHANCED_DETERMINISM.value -> eENABLE_ENHANCED_DETERMINISM
            eENABLE_FRICTION_EVERY_ITERATION.value -> eENABLE_FRICTION_EVERY_ITERATION
            eENABLE_DIRECT_GPU_API.value -> eENABLE_DIRECT_GPU_API
            eMUTABLE_FLAGS.value -> eMUTABLE_FLAGS
            else -> error("Invalid enum value $value for enum PxSceneFlagEnum")
        }
    }
}

private fun PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_ACTIVE_ACTORS()")
private fun PxSceneFlagEnum_eENABLE_CCD(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_CCD()")
private fun PxSceneFlagEnum_eDISABLE_CCD_RESWEEP(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eDISABLE_CCD_RESWEEP()")
private fun PxSceneFlagEnum_eENABLE_PCM(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_PCM()")
private fun PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE()")
private fun PxSceneFlagEnum_eDISABLE_CONTACT_CACHE(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eDISABLE_CONTACT_CACHE()")
private fun PxSceneFlagEnum_eREQUIRE_RW_LOCK(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eREQUIRE_RW_LOCK()")
private fun PxSceneFlagEnum_eENABLE_STABILIZATION(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_STABILIZATION()")
private fun PxSceneFlagEnum_eENABLE_AVERAGE_POINT(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_AVERAGE_POINT()")
private fun PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()")
private fun PxSceneFlagEnum_eENABLE_GPU_DYNAMICS(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_GPU_DYNAMICS()")
private fun PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_ENHANCED_DETERMINISM()")
private fun PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_FRICTION_EVERY_ITERATION()")
private fun PxSceneFlagEnum_eENABLE_DIRECT_GPU_API(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eENABLE_DIRECT_GPU_API()")
private fun PxSceneFlagEnum_eMUTABLE_FLAGS(module: JsAny): Int = js("module._emscripten_enum_PxSceneFlagEnum_eMUTABLE_FLAGS()")

value class PxSceneQueryUpdateModeEnum private constructor(val value: Int) {
    companion object {
        val eBUILD_ENABLED_COMMIT_ENABLED: PxSceneQueryUpdateModeEnum = PxSceneQueryUpdateModeEnum(PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_ENABLED(PhysXJsLoader.physXJs))
        val eBUILD_ENABLED_COMMIT_DISABLED: PxSceneQueryUpdateModeEnum = PxSceneQueryUpdateModeEnum(PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_DISABLED(PhysXJsLoader.physXJs))
        val eBUILD_DISABLED_COMMIT_DISABLED: PxSceneQueryUpdateModeEnum = PxSceneQueryUpdateModeEnum(PxSceneQueryUpdateModeEnum_eBUILD_DISABLED_COMMIT_DISABLED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eBUILD_ENABLED_COMMIT_ENABLED.value -> eBUILD_ENABLED_COMMIT_ENABLED
            eBUILD_ENABLED_COMMIT_DISABLED.value -> eBUILD_ENABLED_COMMIT_DISABLED
            eBUILD_DISABLED_COMMIT_DISABLED.value -> eBUILD_DISABLED_COMMIT_DISABLED
            else -> error("Invalid enum value $value for enum PxSceneQueryUpdateModeEnum")
        }
    }
}

private fun PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_ENABLED()")
private fun PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_DISABLED(module: JsAny): Int = js("module._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_ENABLED_COMMIT_DISABLED()")
private fun PxSceneQueryUpdateModeEnum_eBUILD_DISABLED_COMMIT_DISABLED(module: JsAny): Int = js("module._emscripten_enum_PxSceneQueryUpdateModeEnum_eBUILD_DISABLED_COMMIT_DISABLED()")

value class PxArticulationAxisEnum private constructor(val value: Int) {
    companion object {
        val eTWIST: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eTWIST(PhysXJsLoader.physXJs))
        val eSWING1: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eSWING1(PhysXJsLoader.physXJs))
        val eSWING2: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eSWING2(PhysXJsLoader.physXJs))
        val eX: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eX(PhysXJsLoader.physXJs))
        val eY: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eY(PhysXJsLoader.physXJs))
        val eZ: PxArticulationAxisEnum = PxArticulationAxisEnum(PxArticulationAxisEnum_eZ(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eTWIST.value -> eTWIST
            eSWING1.value -> eSWING1
            eSWING2.value -> eSWING2
            eX.value -> eX
            eY.value -> eY
            eZ.value -> eZ
            else -> error("Invalid enum value $value for enum PxArticulationAxisEnum")
        }
    }
}

private fun PxArticulationAxisEnum_eTWIST(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eTWIST()")
private fun PxArticulationAxisEnum_eSWING1(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eSWING1()")
private fun PxArticulationAxisEnum_eSWING2(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eSWING2()")
private fun PxArticulationAxisEnum_eX(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eX()")
private fun PxArticulationAxisEnum_eY(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eY()")
private fun PxArticulationAxisEnum_eZ(module: JsAny): Int = js("module._emscripten_enum_PxArticulationAxisEnum_eZ()")

value class PxArticulationCacheFlagEnum private constructor(val value: Int) {
    companion object {
        val eVELOCITY: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eVELOCITY(PhysXJsLoader.physXJs))
        val eACCELERATION: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eACCELERATION(PhysXJsLoader.physXJs))
        val ePOSITION: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_ePOSITION(PhysXJsLoader.physXJs))
        val eFORCE: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eFORCE(PhysXJsLoader.physXJs))
        val eLINK_VELOCITY: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eLINK_VELOCITY(PhysXJsLoader.physXJs))
        val eLINK_ACCELERATION: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eLINK_ACCELERATION(PhysXJsLoader.physXJs))
        val eROOT_TRANSFORM: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eROOT_TRANSFORM(PhysXJsLoader.physXJs))
        val eROOT_VELOCITIES: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eROOT_VELOCITIES(PhysXJsLoader.physXJs))
        val eLINK_INCOMING_JOINT_FORCE: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eLINK_INCOMING_JOINT_FORCE(PhysXJsLoader.physXJs))
        val eJOINT_TARGET_POSITIONS: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eJOINT_TARGET_POSITIONS(PhysXJsLoader.physXJs))
        val eJOINT_TARGET_VELOCITIES: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eJOINT_TARGET_VELOCITIES(PhysXJsLoader.physXJs))
        val eALL: PxArticulationCacheFlagEnum = PxArticulationCacheFlagEnum(PxArticulationCacheFlagEnum_eALL(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eVELOCITY.value -> eVELOCITY
            eACCELERATION.value -> eACCELERATION
            ePOSITION.value -> ePOSITION
            eFORCE.value -> eFORCE
            eLINK_VELOCITY.value -> eLINK_VELOCITY
            eLINK_ACCELERATION.value -> eLINK_ACCELERATION
            eROOT_TRANSFORM.value -> eROOT_TRANSFORM
            eROOT_VELOCITIES.value -> eROOT_VELOCITIES
            eLINK_INCOMING_JOINT_FORCE.value -> eLINK_INCOMING_JOINT_FORCE
            eJOINT_TARGET_POSITIONS.value -> eJOINT_TARGET_POSITIONS
            eJOINT_TARGET_VELOCITIES.value -> eJOINT_TARGET_VELOCITIES
            eALL.value -> eALL
            else -> error("Invalid enum value $value for enum PxArticulationCacheFlagEnum")
        }
    }
}

private fun PxArticulationCacheFlagEnum_eVELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eVELOCITY()")
private fun PxArticulationCacheFlagEnum_eACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eACCELERATION()")
private fun PxArticulationCacheFlagEnum_ePOSITION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_ePOSITION()")
private fun PxArticulationCacheFlagEnum_eFORCE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eFORCE()")
private fun PxArticulationCacheFlagEnum_eLINK_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_VELOCITY()")
private fun PxArticulationCacheFlagEnum_eLINK_ACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_ACCELERATION()")
private fun PxArticulationCacheFlagEnum_eROOT_TRANSFORM(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eROOT_TRANSFORM()")
private fun PxArticulationCacheFlagEnum_eROOT_VELOCITIES(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eROOT_VELOCITIES()")
private fun PxArticulationCacheFlagEnum_eLINK_INCOMING_JOINT_FORCE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eLINK_INCOMING_JOINT_FORCE()")
private fun PxArticulationCacheFlagEnum_eJOINT_TARGET_POSITIONS(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eJOINT_TARGET_POSITIONS()")
private fun PxArticulationCacheFlagEnum_eJOINT_TARGET_VELOCITIES(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eJOINT_TARGET_VELOCITIES()")
private fun PxArticulationCacheFlagEnum_eALL(module: JsAny): Int = js("module._emscripten_enum_PxArticulationCacheFlagEnum_eALL()")

value class PxArticulationDriveTypeEnum private constructor(val value: Int) {
    companion object {
        val eFORCE: PxArticulationDriveTypeEnum = PxArticulationDriveTypeEnum(PxArticulationDriveTypeEnum_eFORCE(PhysXJsLoader.physXJs))
        val eACCELERATION: PxArticulationDriveTypeEnum = PxArticulationDriveTypeEnum(PxArticulationDriveTypeEnum_eACCELERATION(PhysXJsLoader.physXJs))
        val eNONE: PxArticulationDriveTypeEnum = PxArticulationDriveTypeEnum(PxArticulationDriveTypeEnum_eNONE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFORCE.value -> eFORCE
            eACCELERATION.value -> eACCELERATION
            eNONE.value -> eNONE
            else -> error("Invalid enum value $value for enum PxArticulationDriveTypeEnum")
        }
    }
}

private fun PxArticulationDriveTypeEnum_eFORCE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationDriveTypeEnum_eFORCE()")
private fun PxArticulationDriveTypeEnum_eACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationDriveTypeEnum_eACCELERATION()")
private fun PxArticulationDriveTypeEnum_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationDriveTypeEnum_eNONE()")

value class PxArticulationFlagEnum private constructor(val value: Int) {
    companion object {
        val eFIX_BASE: PxArticulationFlagEnum = PxArticulationFlagEnum(PxArticulationFlagEnum_eFIX_BASE(PhysXJsLoader.physXJs))
        val eDRIVE_LIMITS_ARE_FORCES: PxArticulationFlagEnum = PxArticulationFlagEnum(PxArticulationFlagEnum_eDRIVE_LIMITS_ARE_FORCES(PhysXJsLoader.physXJs))
        val eDISABLE_SELF_COLLISION: PxArticulationFlagEnum = PxArticulationFlagEnum(PxArticulationFlagEnum_eDISABLE_SELF_COLLISION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFIX_BASE.value -> eFIX_BASE
            eDRIVE_LIMITS_ARE_FORCES.value -> eDRIVE_LIMITS_ARE_FORCES
            eDISABLE_SELF_COLLISION.value -> eDISABLE_SELF_COLLISION
            else -> error("Invalid enum value $value for enum PxArticulationFlagEnum")
        }
    }
}

private fun PxArticulationFlagEnum_eFIX_BASE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationFlagEnum_eFIX_BASE()")
private fun PxArticulationFlagEnum_eDRIVE_LIMITS_ARE_FORCES(module: JsAny): Int = js("module._emscripten_enum_PxArticulationFlagEnum_eDRIVE_LIMITS_ARE_FORCES()")
private fun PxArticulationFlagEnum_eDISABLE_SELF_COLLISION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationFlagEnum_eDISABLE_SELF_COLLISION()")

value class PxArticulationJointTypeEnum private constructor(val value: Int) {
    companion object {
        val eFIX: PxArticulationJointTypeEnum = PxArticulationJointTypeEnum(PxArticulationJointTypeEnum_eFIX(PhysXJsLoader.physXJs))
        val ePRISMATIC: PxArticulationJointTypeEnum = PxArticulationJointTypeEnum(PxArticulationJointTypeEnum_ePRISMATIC(PhysXJsLoader.physXJs))
        val eREVOLUTE: PxArticulationJointTypeEnum = PxArticulationJointTypeEnum(PxArticulationJointTypeEnum_eREVOLUTE(PhysXJsLoader.physXJs))
        val eSPHERICAL: PxArticulationJointTypeEnum = PxArticulationJointTypeEnum(PxArticulationJointTypeEnum_eSPHERICAL(PhysXJsLoader.physXJs))
        val eUNDEFINED: PxArticulationJointTypeEnum = PxArticulationJointTypeEnum(PxArticulationJointTypeEnum_eUNDEFINED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFIX.value -> eFIX
            ePRISMATIC.value -> ePRISMATIC
            eREVOLUTE.value -> eREVOLUTE
            eSPHERICAL.value -> eSPHERICAL
            eUNDEFINED.value -> eUNDEFINED
            else -> error("Invalid enum value $value for enum PxArticulationJointTypeEnum")
        }
    }
}

private fun PxArticulationJointTypeEnum_eFIX(module: JsAny): Int = js("module._emscripten_enum_PxArticulationJointTypeEnum_eFIX()")
private fun PxArticulationJointTypeEnum_ePRISMATIC(module: JsAny): Int = js("module._emscripten_enum_PxArticulationJointTypeEnum_ePRISMATIC()")
private fun PxArticulationJointTypeEnum_eREVOLUTE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationJointTypeEnum_eREVOLUTE()")
private fun PxArticulationJointTypeEnum_eSPHERICAL(module: JsAny): Int = js("module._emscripten_enum_PxArticulationJointTypeEnum_eSPHERICAL()")
private fun PxArticulationJointTypeEnum_eUNDEFINED(module: JsAny): Int = js("module._emscripten_enum_PxArticulationJointTypeEnum_eUNDEFINED()")

value class PxArticulationKinematicFlagEnum private constructor(val value: Int) {
    companion object {
        val ePOSITION: PxArticulationKinematicFlagEnum = PxArticulationKinematicFlagEnum(PxArticulationKinematicFlagEnum_ePOSITION(PhysXJsLoader.physXJs))
        val eVELOCITY: PxArticulationKinematicFlagEnum = PxArticulationKinematicFlagEnum(PxArticulationKinematicFlagEnum_eVELOCITY(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePOSITION.value -> ePOSITION
            eVELOCITY.value -> eVELOCITY
            else -> error("Invalid enum value $value for enum PxArticulationKinematicFlagEnum")
        }
    }
}

private fun PxArticulationKinematicFlagEnum_ePOSITION(module: JsAny): Int = js("module._emscripten_enum_PxArticulationKinematicFlagEnum_ePOSITION()")
private fun PxArticulationKinematicFlagEnum_eVELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxArticulationKinematicFlagEnum_eVELOCITY()")

value class PxArticulationMotionEnum private constructor(val value: Int) {
    companion object {
        val eLOCKED: PxArticulationMotionEnum = PxArticulationMotionEnum(PxArticulationMotionEnum_eLOCKED(PhysXJsLoader.physXJs))
        val eLIMITED: PxArticulationMotionEnum = PxArticulationMotionEnum(PxArticulationMotionEnum_eLIMITED(PhysXJsLoader.physXJs))
        val eFREE: PxArticulationMotionEnum = PxArticulationMotionEnum(PxArticulationMotionEnum_eFREE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLOCKED.value -> eLOCKED
            eLIMITED.value -> eLIMITED
            eFREE.value -> eFREE
            else -> error("Invalid enum value $value for enum PxArticulationMotionEnum")
        }
    }
}

private fun PxArticulationMotionEnum_eLOCKED(module: JsAny): Int = js("module._emscripten_enum_PxArticulationMotionEnum_eLOCKED()")
private fun PxArticulationMotionEnum_eLIMITED(module: JsAny): Int = js("module._emscripten_enum_PxArticulationMotionEnum_eLIMITED()")
private fun PxArticulationMotionEnum_eFREE(module: JsAny): Int = js("module._emscripten_enum_PxArticulationMotionEnum_eFREE()")

value class PxHitFlagEnum private constructor(val value: Int) {
    companion object {
        val ePOSITION: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_ePOSITION(PhysXJsLoader.physXJs))
        val eNORMAL: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eNORMAL(PhysXJsLoader.physXJs))
        val eUV: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eUV(PhysXJsLoader.physXJs))
        val eASSUME_NO_INITIAL_OVERLAP: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP(PhysXJsLoader.physXJs))
        val eANY_HIT: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eANY_HIT(PhysXJsLoader.physXJs))
        val eMESH_MULTIPLE: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eMESH_MULTIPLE(PhysXJsLoader.physXJs))
        val eMESH_BOTH_SIDES: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eMESH_BOTH_SIDES(PhysXJsLoader.physXJs))
        val ePRECISE_SWEEP: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_ePRECISE_SWEEP(PhysXJsLoader.physXJs))
        val eMTD: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eMTD(PhysXJsLoader.physXJs))
        val eFACE_INDEX: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eFACE_INDEX(PhysXJsLoader.physXJs))
        val eDEFAULT: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eDEFAULT(PhysXJsLoader.physXJs))
        val eMODIFIABLE_FLAGS: PxHitFlagEnum = PxHitFlagEnum(PxHitFlagEnum_eMODIFIABLE_FLAGS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePOSITION.value -> ePOSITION
            eNORMAL.value -> eNORMAL
            eUV.value -> eUV
            eASSUME_NO_INITIAL_OVERLAP.value -> eASSUME_NO_INITIAL_OVERLAP
            eANY_HIT.value -> eANY_HIT
            eMESH_MULTIPLE.value -> eMESH_MULTIPLE
            eMESH_BOTH_SIDES.value -> eMESH_BOTH_SIDES
            ePRECISE_SWEEP.value -> ePRECISE_SWEEP
            eMTD.value -> eMTD
            eFACE_INDEX.value -> eFACE_INDEX
            eDEFAULT.value -> eDEFAULT
            eMODIFIABLE_FLAGS.value -> eMODIFIABLE_FLAGS
            else -> error("Invalid enum value $value for enum PxHitFlagEnum")
        }
    }
}

private fun PxHitFlagEnum_ePOSITION(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_ePOSITION()")
private fun PxHitFlagEnum_eNORMAL(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eNORMAL()")
private fun PxHitFlagEnum_eUV(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eUV()")
private fun PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eASSUME_NO_INITIAL_OVERLAP()")
private fun PxHitFlagEnum_eANY_HIT(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eANY_HIT()")
private fun PxHitFlagEnum_eMESH_MULTIPLE(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eMESH_MULTIPLE()")
private fun PxHitFlagEnum_eMESH_BOTH_SIDES(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eMESH_BOTH_SIDES()")
private fun PxHitFlagEnum_ePRECISE_SWEEP(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_ePRECISE_SWEEP()")
private fun PxHitFlagEnum_eMTD(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eMTD()")
private fun PxHitFlagEnum_eFACE_INDEX(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eFACE_INDEX()")
private fun PxHitFlagEnum_eDEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eDEFAULT()")
private fun PxHitFlagEnum_eMODIFIABLE_FLAGS(module: JsAny): Int = js("module._emscripten_enum_PxHitFlagEnum_eMODIFIABLE_FLAGS()")

value class PxQueryFlagEnum private constructor(val value: Int) {
    companion object {
        val eSTATIC: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_eSTATIC(PhysXJsLoader.physXJs))
        val eDYNAMIC: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_eDYNAMIC(PhysXJsLoader.physXJs))
        val ePREFILTER: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_ePREFILTER(PhysXJsLoader.physXJs))
        val ePOSTFILTER: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_ePOSTFILTER(PhysXJsLoader.physXJs))
        val eANY_HIT: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_eANY_HIT(PhysXJsLoader.physXJs))
        val eNO_BLOCK: PxQueryFlagEnum = PxQueryFlagEnum(PxQueryFlagEnum_eNO_BLOCK(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSTATIC.value -> eSTATIC
            eDYNAMIC.value -> eDYNAMIC
            ePREFILTER.value -> ePREFILTER
            ePOSTFILTER.value -> ePOSTFILTER
            eANY_HIT.value -> eANY_HIT
            eNO_BLOCK.value -> eNO_BLOCK
            else -> error("Invalid enum value $value for enum PxQueryFlagEnum")
        }
    }
}

private fun PxQueryFlagEnum_eSTATIC(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_eSTATIC()")
private fun PxQueryFlagEnum_eDYNAMIC(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_eDYNAMIC()")
private fun PxQueryFlagEnum_ePREFILTER(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_ePREFILTER()")
private fun PxQueryFlagEnum_ePOSTFILTER(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_ePOSTFILTER()")
private fun PxQueryFlagEnum_eANY_HIT(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_eANY_HIT()")
private fun PxQueryFlagEnum_eNO_BLOCK(module: JsAny): Int = js("module._emscripten_enum_PxQueryFlagEnum_eNO_BLOCK()")

value class PxQueryHitType private constructor(val value: Int) {
    companion object {
        val eNONE: PxQueryHitType = PxQueryHitType(PxQueryHitType_eNONE(PhysXJsLoader.physXJs))
        val eBLOCK: PxQueryHitType = PxQueryHitType(PxQueryHitType_eBLOCK(PhysXJsLoader.physXJs))
        val eTOUCH: PxQueryHitType = PxQueryHitType(PxQueryHitType_eTOUCH(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNONE.value -> eNONE
            eBLOCK.value -> eBLOCK
            eTOUCH.value -> eTOUCH
            else -> error("Invalid enum value $value for enum PxQueryHitType")
        }
    }
}

private fun PxQueryHitType_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxQueryHitType_eNONE()")
private fun PxQueryHitType_eBLOCK(module: JsAny): Int = js("module._emscripten_enum_PxQueryHitType_eBLOCK()")
private fun PxQueryHitType_eTOUCH(module: JsAny): Int = js("module._emscripten_enum_PxQueryHitType_eTOUCH()")

value class PxActorFlagEnum private constructor(val value: Int) {
    companion object {
        val eVISUALIZATION: PxActorFlagEnum = PxActorFlagEnum(PxActorFlagEnum_eVISUALIZATION(PhysXJsLoader.physXJs))
        val eDISABLE_GRAVITY: PxActorFlagEnum = PxActorFlagEnum(PxActorFlagEnum_eDISABLE_GRAVITY(PhysXJsLoader.physXJs))
        val eSEND_SLEEP_NOTIFIES: PxActorFlagEnum = PxActorFlagEnum(PxActorFlagEnum_eSEND_SLEEP_NOTIFIES(PhysXJsLoader.physXJs))
        val eDISABLE_SIMULATION: PxActorFlagEnum = PxActorFlagEnum(PxActorFlagEnum_eDISABLE_SIMULATION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eVISUALIZATION.value -> eVISUALIZATION
            eDISABLE_GRAVITY.value -> eDISABLE_GRAVITY
            eSEND_SLEEP_NOTIFIES.value -> eSEND_SLEEP_NOTIFIES
            eDISABLE_SIMULATION.value -> eDISABLE_SIMULATION
            else -> error("Invalid enum value $value for enum PxActorFlagEnum")
        }
    }
}

private fun PxActorFlagEnum_eVISUALIZATION(module: JsAny): Int = js("module._emscripten_enum_PxActorFlagEnum_eVISUALIZATION()")
private fun PxActorFlagEnum_eDISABLE_GRAVITY(module: JsAny): Int = js("module._emscripten_enum_PxActorFlagEnum_eDISABLE_GRAVITY()")
private fun PxActorFlagEnum_eSEND_SLEEP_NOTIFIES(module: JsAny): Int = js("module._emscripten_enum_PxActorFlagEnum_eSEND_SLEEP_NOTIFIES()")
private fun PxActorFlagEnum_eDISABLE_SIMULATION(module: JsAny): Int = js("module._emscripten_enum_PxActorFlagEnum_eDISABLE_SIMULATION()")

value class PxActorTypeEnum private constructor(val value: Int) {
    companion object {
        val eRIGID_STATIC: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eRIGID_STATIC(PhysXJsLoader.physXJs))
        val eRIGID_DYNAMIC: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eRIGID_DYNAMIC(PhysXJsLoader.physXJs))
        val eARTICULATION_LINK: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eARTICULATION_LINK(PhysXJsLoader.physXJs))
        val eDEFORMABLE_SURFACE: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eDEFORMABLE_SURFACE(PhysXJsLoader.physXJs))
        val eDEFORMABLE_VOLUME: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eDEFORMABLE_VOLUME(PhysXJsLoader.physXJs))
        val eSOFTBODY: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_eSOFTBODY(PhysXJsLoader.physXJs))
        val ePBD_PARTICLESYSTEM: PxActorTypeEnum = PxActorTypeEnum(PxActorTypeEnum_ePBD_PARTICLESYSTEM(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eRIGID_STATIC.value -> eRIGID_STATIC
            eRIGID_DYNAMIC.value -> eRIGID_DYNAMIC
            eARTICULATION_LINK.value -> eARTICULATION_LINK
            eDEFORMABLE_SURFACE.value -> eDEFORMABLE_SURFACE
            eDEFORMABLE_VOLUME.value -> eDEFORMABLE_VOLUME
            eSOFTBODY.value -> eSOFTBODY
            ePBD_PARTICLESYSTEM.value -> ePBD_PARTICLESYSTEM
            else -> error("Invalid enum value $value for enum PxActorTypeEnum")
        }
    }
}

private fun PxActorTypeEnum_eRIGID_STATIC(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eRIGID_STATIC()")
private fun PxActorTypeEnum_eRIGID_DYNAMIC(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eRIGID_DYNAMIC()")
private fun PxActorTypeEnum_eARTICULATION_LINK(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eARTICULATION_LINK()")
private fun PxActorTypeEnum_eDEFORMABLE_SURFACE(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eDEFORMABLE_SURFACE()")
private fun PxActorTypeEnum_eDEFORMABLE_VOLUME(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eDEFORMABLE_VOLUME()")
private fun PxActorTypeEnum_eSOFTBODY(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_eSOFTBODY()")
private fun PxActorTypeEnum_ePBD_PARTICLESYSTEM(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeEnum_ePBD_PARTICLESYSTEM()")

value class PxActorTypeFlagEnum private constructor(val value: Int) {
    companion object {
        val eRIGID_STATIC: PxActorTypeFlagEnum = PxActorTypeFlagEnum(PxActorTypeFlagEnum_eRIGID_STATIC(PhysXJsLoader.physXJs))
        val eRIGID_DYNAMIC: PxActorTypeFlagEnum = PxActorTypeFlagEnum(PxActorTypeFlagEnum_eRIGID_DYNAMIC(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eRIGID_STATIC.value -> eRIGID_STATIC
            eRIGID_DYNAMIC.value -> eRIGID_DYNAMIC
            else -> error("Invalid enum value $value for enum PxActorTypeFlagEnum")
        }
    }
}

private fun PxActorTypeFlagEnum_eRIGID_STATIC(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeFlagEnum_eRIGID_STATIC()")
private fun PxActorTypeFlagEnum_eRIGID_DYNAMIC(module: JsAny): Int = js("module._emscripten_enum_PxActorTypeFlagEnum_eRIGID_DYNAMIC()")

value class PxRigidBodyFlagEnum private constructor(val value: Int) {
    companion object {
        val eKINEMATIC: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eKINEMATIC(PhysXJsLoader.physXJs))
        val eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES(PhysXJsLoader.physXJs))
        val eENABLE_CCD: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eENABLE_CCD(PhysXJsLoader.physXJs))
        val eENABLE_CCD_FRICTION: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION(PhysXJsLoader.physXJs))
        val eENABLE_POSE_INTEGRATION_PREVIEW: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW(PhysXJsLoader.physXJs))
        val eENABLE_SPECULATIVE_CCD: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD(PhysXJsLoader.physXJs))
        val eENABLE_CCD_MAX_CONTACT_IMPULSE: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE(PhysXJsLoader.physXJs))
        val eRETAIN_ACCELERATIONS: PxRigidBodyFlagEnum = PxRigidBodyFlagEnum(PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eKINEMATIC.value -> eKINEMATIC
            eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES.value -> eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES
            eENABLE_CCD.value -> eENABLE_CCD
            eENABLE_CCD_FRICTION.value -> eENABLE_CCD_FRICTION
            eENABLE_POSE_INTEGRATION_PREVIEW.value -> eENABLE_POSE_INTEGRATION_PREVIEW
            eENABLE_SPECULATIVE_CCD.value -> eENABLE_SPECULATIVE_CCD
            eENABLE_CCD_MAX_CONTACT_IMPULSE.value -> eENABLE_CCD_MAX_CONTACT_IMPULSE
            eRETAIN_ACCELERATIONS.value -> eRETAIN_ACCELERATIONS
            else -> error("Invalid enum value $value for enum PxRigidBodyFlagEnum")
        }
    }
}

private fun PxRigidBodyFlagEnum_eKINEMATIC(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eKINEMATIC()")
private fun PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eUSE_KINEMATIC_TARGET_FOR_SCENE_QUERIES()")
private fun PxRigidBodyFlagEnum_eENABLE_CCD(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD()")
private fun PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_FRICTION()")
private fun PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_POSE_INTEGRATION_PREVIEW()")
private fun PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_SPECULATIVE_CCD()")
private fun PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eENABLE_CCD_MAX_CONTACT_IMPULSE()")
private fun PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS(module: JsAny): Int = js("module._emscripten_enum_PxRigidBodyFlagEnum_eRETAIN_ACCELERATIONS()")

value class PxRigidDynamicLockFlagEnum private constructor(val value: Int) {
    companion object {
        val eLOCK_LINEAR_X: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X(PhysXJsLoader.physXJs))
        val eLOCK_LINEAR_Y: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y(PhysXJsLoader.physXJs))
        val eLOCK_LINEAR_Z: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z(PhysXJsLoader.physXJs))
        val eLOCK_ANGULAR_X: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X(PhysXJsLoader.physXJs))
        val eLOCK_ANGULAR_Y: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y(PhysXJsLoader.physXJs))
        val eLOCK_ANGULAR_Z: PxRigidDynamicLockFlagEnum = PxRigidDynamicLockFlagEnum(PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLOCK_LINEAR_X.value -> eLOCK_LINEAR_X
            eLOCK_LINEAR_Y.value -> eLOCK_LINEAR_Y
            eLOCK_LINEAR_Z.value -> eLOCK_LINEAR_Z
            eLOCK_ANGULAR_X.value -> eLOCK_ANGULAR_X
            eLOCK_ANGULAR_Y.value -> eLOCK_ANGULAR_Y
            eLOCK_ANGULAR_Z.value -> eLOCK_ANGULAR_Z
            else -> error("Invalid enum value $value for enum PxRigidDynamicLockFlagEnum")
        }
    }
}

private fun PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_X()")
private fun PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Y()")
private fun PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_LINEAR_Z()")
private fun PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_X()")
private fun PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Y()")
private fun PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z(module: JsAny): Int = js("module._emscripten_enum_PxRigidDynamicLockFlagEnum_eLOCK_ANGULAR_Z()")

value class PxShapeFlagEnum private constructor(val value: Int) {
    companion object {
        val eSIMULATION_SHAPE: PxShapeFlagEnum = PxShapeFlagEnum(PxShapeFlagEnum_eSIMULATION_SHAPE(PhysXJsLoader.physXJs))
        val eSCENE_QUERY_SHAPE: PxShapeFlagEnum = PxShapeFlagEnum(PxShapeFlagEnum_eSCENE_QUERY_SHAPE(PhysXJsLoader.physXJs))
        val eTRIGGER_SHAPE: PxShapeFlagEnum = PxShapeFlagEnum(PxShapeFlagEnum_eTRIGGER_SHAPE(PhysXJsLoader.physXJs))
        val eVISUALIZATION: PxShapeFlagEnum = PxShapeFlagEnum(PxShapeFlagEnum_eVISUALIZATION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSIMULATION_SHAPE.value -> eSIMULATION_SHAPE
            eSCENE_QUERY_SHAPE.value -> eSCENE_QUERY_SHAPE
            eTRIGGER_SHAPE.value -> eTRIGGER_SHAPE
            eVISUALIZATION.value -> eVISUALIZATION
            else -> error("Invalid enum value $value for enum PxShapeFlagEnum")
        }
    }
}

private fun PxShapeFlagEnum_eSIMULATION_SHAPE(module: JsAny): Int = js("module._emscripten_enum_PxShapeFlagEnum_eSIMULATION_SHAPE()")
private fun PxShapeFlagEnum_eSCENE_QUERY_SHAPE(module: JsAny): Int = js("module._emscripten_enum_PxShapeFlagEnum_eSCENE_QUERY_SHAPE()")
private fun PxShapeFlagEnum_eTRIGGER_SHAPE(module: JsAny): Int = js("module._emscripten_enum_PxShapeFlagEnum_eTRIGGER_SHAPE()")
private fun PxShapeFlagEnum_eVISUALIZATION(module: JsAny): Int = js("module._emscripten_enum_PxShapeFlagEnum_eVISUALIZATION()")

value class PxBroadPhaseTypeEnum private constructor(val value: Int) {
    companion object {
        val eSAP: PxBroadPhaseTypeEnum = PxBroadPhaseTypeEnum(PxBroadPhaseTypeEnum_eSAP(PhysXJsLoader.physXJs))
        val eMBP: PxBroadPhaseTypeEnum = PxBroadPhaseTypeEnum(PxBroadPhaseTypeEnum_eMBP(PhysXJsLoader.physXJs))
        val eABP: PxBroadPhaseTypeEnum = PxBroadPhaseTypeEnum(PxBroadPhaseTypeEnum_eABP(PhysXJsLoader.physXJs))
        val ePABP: PxBroadPhaseTypeEnum = PxBroadPhaseTypeEnum(PxBroadPhaseTypeEnum_ePABP(PhysXJsLoader.physXJs))
        val eGPU: PxBroadPhaseTypeEnum = PxBroadPhaseTypeEnum(PxBroadPhaseTypeEnum_eGPU(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSAP.value -> eSAP
            eMBP.value -> eMBP
            eABP.value -> eABP
            ePABP.value -> ePABP
            eGPU.value -> eGPU
            else -> error("Invalid enum value $value for enum PxBroadPhaseTypeEnum")
        }
    }
}

private fun PxBroadPhaseTypeEnum_eSAP(module: JsAny): Int = js("module._emscripten_enum_PxBroadPhaseTypeEnum_eSAP()")
private fun PxBroadPhaseTypeEnum_eMBP(module: JsAny): Int = js("module._emscripten_enum_PxBroadPhaseTypeEnum_eMBP()")
private fun PxBroadPhaseTypeEnum_eABP(module: JsAny): Int = js("module._emscripten_enum_PxBroadPhaseTypeEnum_eABP()")
private fun PxBroadPhaseTypeEnum_ePABP(module: JsAny): Int = js("module._emscripten_enum_PxBroadPhaseTypeEnum_ePABP()")
private fun PxBroadPhaseTypeEnum_eGPU(module: JsAny): Int = js("module._emscripten_enum_PxBroadPhaseTypeEnum_eGPU()")

value class PxBVHBuildStrategyEnum private constructor(val value: Int) {
    companion object {
        val eFAST: PxBVHBuildStrategyEnum = PxBVHBuildStrategyEnum(PxBVHBuildStrategyEnum_eFAST(PhysXJsLoader.physXJs))
        val eDEFAULT: PxBVHBuildStrategyEnum = PxBVHBuildStrategyEnum(PxBVHBuildStrategyEnum_eDEFAULT(PhysXJsLoader.physXJs))
        val eSAH: PxBVHBuildStrategyEnum = PxBVHBuildStrategyEnum(PxBVHBuildStrategyEnum_eSAH(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFAST.value -> eFAST
            eDEFAULT.value -> eDEFAULT
            eSAH.value -> eSAH
            else -> error("Invalid enum value $value for enum PxBVHBuildStrategyEnum")
        }
    }
}

private fun PxBVHBuildStrategyEnum_eFAST(module: JsAny): Int = js("module._emscripten_enum_PxBVHBuildStrategyEnum_eFAST()")
private fun PxBVHBuildStrategyEnum_eDEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxBVHBuildStrategyEnum_eDEFAULT()")
private fun PxBVHBuildStrategyEnum_eSAH(module: JsAny): Int = js("module._emscripten_enum_PxBVHBuildStrategyEnum_eSAH()")

value class PxCombineModeEnum private constructor(val value: Int) {
    companion object {
        val eAVERAGE: PxCombineModeEnum = PxCombineModeEnum(PxCombineModeEnum_eAVERAGE(PhysXJsLoader.physXJs))
        val eMIN: PxCombineModeEnum = PxCombineModeEnum(PxCombineModeEnum_eMIN(PhysXJsLoader.physXJs))
        val eMULTIPLY: PxCombineModeEnum = PxCombineModeEnum(PxCombineModeEnum_eMULTIPLY(PhysXJsLoader.physXJs))
        val eMAX: PxCombineModeEnum = PxCombineModeEnum(PxCombineModeEnum_eMAX(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eAVERAGE.value -> eAVERAGE
            eMIN.value -> eMIN
            eMULTIPLY.value -> eMULTIPLY
            eMAX.value -> eMAX
            else -> error("Invalid enum value $value for enum PxCombineModeEnum")
        }
    }
}

private fun PxCombineModeEnum_eAVERAGE(module: JsAny): Int = js("module._emscripten_enum_PxCombineModeEnum_eAVERAGE()")
private fun PxCombineModeEnum_eMIN(module: JsAny): Int = js("module._emscripten_enum_PxCombineModeEnum_eMIN()")
private fun PxCombineModeEnum_eMULTIPLY(module: JsAny): Int = js("module._emscripten_enum_PxCombineModeEnum_eMULTIPLY()")
private fun PxCombineModeEnum_eMAX(module: JsAny): Int = js("module._emscripten_enum_PxCombineModeEnum_eMAX()")

value class PxConstraintFlagEnum private constructor(val value: Int) {
    companion object {
        val eBROKEN: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eBROKEN(PhysXJsLoader.physXJs))
        val eCOLLISION_ENABLED: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eCOLLISION_ENABLED(PhysXJsLoader.physXJs))
        val eVISUALIZATION: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eVISUALIZATION(PhysXJsLoader.physXJs))
        val eDRIVE_LIMITS_ARE_FORCES: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eDRIVE_LIMITS_ARE_FORCES(PhysXJsLoader.physXJs))
        val eIMPROVED_SLERP: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eIMPROVED_SLERP(PhysXJsLoader.physXJs))
        val eDISABLE_PREPROCESSING: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eDISABLE_PREPROCESSING(PhysXJsLoader.physXJs))
        val eENABLE_EXTENDED_LIMITS: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eENABLE_EXTENDED_LIMITS(PhysXJsLoader.physXJs))
        val eGPU_COMPATIBLE: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eGPU_COMPATIBLE(PhysXJsLoader.physXJs))
        val eALWAYS_UPDATE: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eALWAYS_UPDATE(PhysXJsLoader.physXJs))
        val eDISABLE_CONSTRAINT: PxConstraintFlagEnum = PxConstraintFlagEnum(PxConstraintFlagEnum_eDISABLE_CONSTRAINT(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eBROKEN.value -> eBROKEN
            eCOLLISION_ENABLED.value -> eCOLLISION_ENABLED
            eVISUALIZATION.value -> eVISUALIZATION
            eDRIVE_LIMITS_ARE_FORCES.value -> eDRIVE_LIMITS_ARE_FORCES
            eIMPROVED_SLERP.value -> eIMPROVED_SLERP
            eDISABLE_PREPROCESSING.value -> eDISABLE_PREPROCESSING
            eENABLE_EXTENDED_LIMITS.value -> eENABLE_EXTENDED_LIMITS
            eGPU_COMPATIBLE.value -> eGPU_COMPATIBLE
            eALWAYS_UPDATE.value -> eALWAYS_UPDATE
            eDISABLE_CONSTRAINT.value -> eDISABLE_CONSTRAINT
            else -> error("Invalid enum value $value for enum PxConstraintFlagEnum")
        }
    }
}

private fun PxConstraintFlagEnum_eBROKEN(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eBROKEN()")
private fun PxConstraintFlagEnum_eCOLLISION_ENABLED(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eCOLLISION_ENABLED()")
private fun PxConstraintFlagEnum_eVISUALIZATION(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eVISUALIZATION()")
private fun PxConstraintFlagEnum_eDRIVE_LIMITS_ARE_FORCES(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eDRIVE_LIMITS_ARE_FORCES()")
private fun PxConstraintFlagEnum_eIMPROVED_SLERP(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eIMPROVED_SLERP()")
private fun PxConstraintFlagEnum_eDISABLE_PREPROCESSING(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eDISABLE_PREPROCESSING()")
private fun PxConstraintFlagEnum_eENABLE_EXTENDED_LIMITS(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eENABLE_EXTENDED_LIMITS()")
private fun PxConstraintFlagEnum_eGPU_COMPATIBLE(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eGPU_COMPATIBLE()")
private fun PxConstraintFlagEnum_eALWAYS_UPDATE(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eALWAYS_UPDATE()")
private fun PxConstraintFlagEnum_eDISABLE_CONSTRAINT(module: JsAny): Int = js("module._emscripten_enum_PxConstraintFlagEnum_eDISABLE_CONSTRAINT()")

value class PxContactPairHeaderFlagEnum private constructor(val value: Int) {
    companion object {
        val eREMOVED_ACTOR_0: PxContactPairHeaderFlagEnum = PxContactPairHeaderFlagEnum(PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_0(PhysXJsLoader.physXJs))
        val eREMOVED_ACTOR_1: PxContactPairHeaderFlagEnum = PxContactPairHeaderFlagEnum(PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_1(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eREMOVED_ACTOR_0.value -> eREMOVED_ACTOR_0
            eREMOVED_ACTOR_1.value -> eREMOVED_ACTOR_1
            else -> error("Invalid enum value $value for enum PxContactPairHeaderFlagEnum")
        }
    }
}

private fun PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_0(module: JsAny): Int = js("module._emscripten_enum_PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_0()")
private fun PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_1(module: JsAny): Int = js("module._emscripten_enum_PxContactPairHeaderFlagEnum_eREMOVED_ACTOR_1()")

value class PxContactPairFlagEnum private constructor(val value: Int) {
    companion object {
        val eREMOVED_SHAPE_0: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eREMOVED_SHAPE_0(PhysXJsLoader.physXJs))
        val eREMOVED_SHAPE_1: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eREMOVED_SHAPE_1(PhysXJsLoader.physXJs))
        val eACTOR_PAIR_HAS_FIRST_TOUCH: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eACTOR_PAIR_HAS_FIRST_TOUCH(PhysXJsLoader.physXJs))
        val eACTOR_PAIR_LOST_TOUCH: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eACTOR_PAIR_LOST_TOUCH(PhysXJsLoader.physXJs))
        val eINTERNAL_HAS_IMPULSES: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eINTERNAL_HAS_IMPULSES(PhysXJsLoader.physXJs))
        val eINTERNAL_CONTACTS_ARE_FLIPPED: PxContactPairFlagEnum = PxContactPairFlagEnum(PxContactPairFlagEnum_eINTERNAL_CONTACTS_ARE_FLIPPED(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eREMOVED_SHAPE_0.value -> eREMOVED_SHAPE_0
            eREMOVED_SHAPE_1.value -> eREMOVED_SHAPE_1
            eACTOR_PAIR_HAS_FIRST_TOUCH.value -> eACTOR_PAIR_HAS_FIRST_TOUCH
            eACTOR_PAIR_LOST_TOUCH.value -> eACTOR_PAIR_LOST_TOUCH
            eINTERNAL_HAS_IMPULSES.value -> eINTERNAL_HAS_IMPULSES
            eINTERNAL_CONTACTS_ARE_FLIPPED.value -> eINTERNAL_CONTACTS_ARE_FLIPPED
            else -> error("Invalid enum value $value for enum PxContactPairFlagEnum")
        }
    }
}

private fun PxContactPairFlagEnum_eREMOVED_SHAPE_0(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eREMOVED_SHAPE_0()")
private fun PxContactPairFlagEnum_eREMOVED_SHAPE_1(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eREMOVED_SHAPE_1()")
private fun PxContactPairFlagEnum_eACTOR_PAIR_HAS_FIRST_TOUCH(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eACTOR_PAIR_HAS_FIRST_TOUCH()")
private fun PxContactPairFlagEnum_eACTOR_PAIR_LOST_TOUCH(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eACTOR_PAIR_LOST_TOUCH()")
private fun PxContactPairFlagEnum_eINTERNAL_HAS_IMPULSES(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eINTERNAL_HAS_IMPULSES()")
private fun PxContactPairFlagEnum_eINTERNAL_CONTACTS_ARE_FLIPPED(module: JsAny): Int = js("module._emscripten_enum_PxContactPairFlagEnum_eINTERNAL_CONTACTS_ARE_FLIPPED()")

value class PxDynamicTreeSecondaryPrunerEnum private constructor(val value: Int) {
    companion object {
        val eNONE: PxDynamicTreeSecondaryPrunerEnum = PxDynamicTreeSecondaryPrunerEnum(PxDynamicTreeSecondaryPrunerEnum_eNONE(PhysXJsLoader.physXJs))
        val eBUCKET: PxDynamicTreeSecondaryPrunerEnum = PxDynamicTreeSecondaryPrunerEnum(PxDynamicTreeSecondaryPrunerEnum_eBUCKET(PhysXJsLoader.physXJs))
        val eINCREMENTAL: PxDynamicTreeSecondaryPrunerEnum = PxDynamicTreeSecondaryPrunerEnum(PxDynamicTreeSecondaryPrunerEnum_eINCREMENTAL(PhysXJsLoader.physXJs))
        val eBVH: PxDynamicTreeSecondaryPrunerEnum = PxDynamicTreeSecondaryPrunerEnum(PxDynamicTreeSecondaryPrunerEnum_eBVH(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNONE.value -> eNONE
            eBUCKET.value -> eBUCKET
            eINCREMENTAL.value -> eINCREMENTAL
            eBVH.value -> eBVH
            else -> error("Invalid enum value $value for enum PxDynamicTreeSecondaryPrunerEnum")
        }
    }
}

private fun PxDynamicTreeSecondaryPrunerEnum_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eNONE()")
private fun PxDynamicTreeSecondaryPrunerEnum_eBUCKET(module: JsAny): Int = js("module._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eBUCKET()")
private fun PxDynamicTreeSecondaryPrunerEnum_eINCREMENTAL(module: JsAny): Int = js("module._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eINCREMENTAL()")
private fun PxDynamicTreeSecondaryPrunerEnum_eBVH(module: JsAny): Int = js("module._emscripten_enum_PxDynamicTreeSecondaryPrunerEnum_eBVH()")

value class PxFilterFlagEnum private constructor(val value: Int) {
    companion object {
        val eKILL: PxFilterFlagEnum = PxFilterFlagEnum(PxFilterFlagEnum_eKILL(PhysXJsLoader.physXJs))
        val eSUPPRESS: PxFilterFlagEnum = PxFilterFlagEnum(PxFilterFlagEnum_eSUPPRESS(PhysXJsLoader.physXJs))
        val eCALLBACK: PxFilterFlagEnum = PxFilterFlagEnum(PxFilterFlagEnum_eCALLBACK(PhysXJsLoader.physXJs))
        val eNOTIFY: PxFilterFlagEnum = PxFilterFlagEnum(PxFilterFlagEnum_eNOTIFY(PhysXJsLoader.physXJs))
        val eDEFAULT: PxFilterFlagEnum = PxFilterFlagEnum(PxFilterFlagEnum_eDEFAULT(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eKILL.value -> eKILL
            eSUPPRESS.value -> eSUPPRESS
            eCALLBACK.value -> eCALLBACK
            eNOTIFY.value -> eNOTIFY
            eDEFAULT.value -> eDEFAULT
            else -> error("Invalid enum value $value for enum PxFilterFlagEnum")
        }
    }
}

private fun PxFilterFlagEnum_eKILL(module: JsAny): Int = js("module._emscripten_enum_PxFilterFlagEnum_eKILL()")
private fun PxFilterFlagEnum_eSUPPRESS(module: JsAny): Int = js("module._emscripten_enum_PxFilterFlagEnum_eSUPPRESS()")
private fun PxFilterFlagEnum_eCALLBACK(module: JsAny): Int = js("module._emscripten_enum_PxFilterFlagEnum_eCALLBACK()")
private fun PxFilterFlagEnum_eNOTIFY(module: JsAny): Int = js("module._emscripten_enum_PxFilterFlagEnum_eNOTIFY()")
private fun PxFilterFlagEnum_eDEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxFilterFlagEnum_eDEFAULT()")

value class PxFilterObjectFlagEnum private constructor(val value: Int) {
    companion object {
        val eKINEMATIC: PxFilterObjectFlagEnum = PxFilterObjectFlagEnum(PxFilterObjectFlagEnum_eKINEMATIC(PhysXJsLoader.physXJs))
        val eTRIGGER: PxFilterObjectFlagEnum = PxFilterObjectFlagEnum(PxFilterObjectFlagEnum_eTRIGGER(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eKINEMATIC.value -> eKINEMATIC
            eTRIGGER.value -> eTRIGGER
            else -> error("Invalid enum value $value for enum PxFilterObjectFlagEnum")
        }
    }
}

private fun PxFilterObjectFlagEnum_eKINEMATIC(module: JsAny): Int = js("module._emscripten_enum_PxFilterObjectFlagEnum_eKINEMATIC()")
private fun PxFilterObjectFlagEnum_eTRIGGER(module: JsAny): Int = js("module._emscripten_enum_PxFilterObjectFlagEnum_eTRIGGER()")

value class PxForceModeEnum private constructor(val value: Int) {
    companion object {
        val eFORCE: PxForceModeEnum = PxForceModeEnum(PxForceModeEnum_eFORCE(PhysXJsLoader.physXJs))
        val eIMPULSE: PxForceModeEnum = PxForceModeEnum(PxForceModeEnum_eIMPULSE(PhysXJsLoader.physXJs))
        val eVELOCITY_CHANGE: PxForceModeEnum = PxForceModeEnum(PxForceModeEnum_eVELOCITY_CHANGE(PhysXJsLoader.physXJs))
        val eACCELERATION: PxForceModeEnum = PxForceModeEnum(PxForceModeEnum_eACCELERATION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eFORCE.value -> eFORCE
            eIMPULSE.value -> eIMPULSE
            eVELOCITY_CHANGE.value -> eVELOCITY_CHANGE
            eACCELERATION.value -> eACCELERATION
            else -> error("Invalid enum value $value for enum PxForceModeEnum")
        }
    }
}

private fun PxForceModeEnum_eFORCE(module: JsAny): Int = js("module._emscripten_enum_PxForceModeEnum_eFORCE()")
private fun PxForceModeEnum_eIMPULSE(module: JsAny): Int = js("module._emscripten_enum_PxForceModeEnum_eIMPULSE()")
private fun PxForceModeEnum_eVELOCITY_CHANGE(module: JsAny): Int = js("module._emscripten_enum_PxForceModeEnum_eVELOCITY_CHANGE()")
private fun PxForceModeEnum_eACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxForceModeEnum_eACCELERATION()")

value class PxFrictionTypeEnum private constructor(val value: Int) {
    companion object {
        val ePATCH: PxFrictionTypeEnum = PxFrictionTypeEnum(PxFrictionTypeEnum_ePATCH(PhysXJsLoader.physXJs))
        val eFRICTION_COUNT: PxFrictionTypeEnum = PxFrictionTypeEnum(PxFrictionTypeEnum_eFRICTION_COUNT(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePATCH.value -> ePATCH
            eFRICTION_COUNT.value -> eFRICTION_COUNT
            else -> error("Invalid enum value $value for enum PxFrictionTypeEnum")
        }
    }
}

private fun PxFrictionTypeEnum_ePATCH(module: JsAny): Int = js("module._emscripten_enum_PxFrictionTypeEnum_ePATCH()")
private fun PxFrictionTypeEnum_eFRICTION_COUNT(module: JsAny): Int = js("module._emscripten_enum_PxFrictionTypeEnum_eFRICTION_COUNT()")

value class PxMaterialFlagEnum private constructor(val value: Int) {
    companion object {
        val eDISABLE_FRICTION: PxMaterialFlagEnum = PxMaterialFlagEnum(PxMaterialFlagEnum_eDISABLE_FRICTION(PhysXJsLoader.physXJs))
        val eDISABLE_STRONG_FRICTION: PxMaterialFlagEnum = PxMaterialFlagEnum(PxMaterialFlagEnum_eDISABLE_STRONG_FRICTION(PhysXJsLoader.physXJs))
        val eCOMPLIANT_ACCELERATION_SPRING: PxMaterialFlagEnum = PxMaterialFlagEnum(PxMaterialFlagEnum_eCOMPLIANT_ACCELERATION_SPRING(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eDISABLE_FRICTION.value -> eDISABLE_FRICTION
            eDISABLE_STRONG_FRICTION.value -> eDISABLE_STRONG_FRICTION
            eCOMPLIANT_ACCELERATION_SPRING.value -> eCOMPLIANT_ACCELERATION_SPRING
            else -> error("Invalid enum value $value for enum PxMaterialFlagEnum")
        }
    }
}

private fun PxMaterialFlagEnum_eDISABLE_FRICTION(module: JsAny): Int = js("module._emscripten_enum_PxMaterialFlagEnum_eDISABLE_FRICTION()")
private fun PxMaterialFlagEnum_eDISABLE_STRONG_FRICTION(module: JsAny): Int = js("module._emscripten_enum_PxMaterialFlagEnum_eDISABLE_STRONG_FRICTION()")
private fun PxMaterialFlagEnum_eCOMPLIANT_ACCELERATION_SPRING(module: JsAny): Int = js("module._emscripten_enum_PxMaterialFlagEnum_eCOMPLIANT_ACCELERATION_SPRING()")

value class PxPairFilteringModeEnum private constructor(val value: Int) {
    companion object {
        val eKEEP: PxPairFilteringModeEnum = PxPairFilteringModeEnum(PxPairFilteringModeEnum_eKEEP(PhysXJsLoader.physXJs))
        val eSUPPRESS: PxPairFilteringModeEnum = PxPairFilteringModeEnum(PxPairFilteringModeEnum_eSUPPRESS(PhysXJsLoader.physXJs))
        val eKILL: PxPairFilteringModeEnum = PxPairFilteringModeEnum(PxPairFilteringModeEnum_eKILL(PhysXJsLoader.physXJs))
        val eDEFAULT: PxPairFilteringModeEnum = PxPairFilteringModeEnum(PxPairFilteringModeEnum_eDEFAULT(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eKEEP.value -> eKEEP
            eSUPPRESS.value -> eSUPPRESS
            eKILL.value -> eKILL
            eDEFAULT.value -> eDEFAULT
            else -> error("Invalid enum value $value for enum PxPairFilteringModeEnum")
        }
    }
}

private fun PxPairFilteringModeEnum_eKEEP(module: JsAny): Int = js("module._emscripten_enum_PxPairFilteringModeEnum_eKEEP()")
private fun PxPairFilteringModeEnum_eSUPPRESS(module: JsAny): Int = js("module._emscripten_enum_PxPairFilteringModeEnum_eSUPPRESS()")
private fun PxPairFilteringModeEnum_eKILL(module: JsAny): Int = js("module._emscripten_enum_PxPairFilteringModeEnum_eKILL()")
private fun PxPairFilteringModeEnum_eDEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxPairFilteringModeEnum_eDEFAULT()")

value class PxPairFlagEnum private constructor(val value: Int) {
    companion object {
        val eSOLVE_CONTACT: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eSOLVE_CONTACT(PhysXJsLoader.physXJs))
        val eMODIFY_CONTACTS: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eMODIFY_CONTACTS(PhysXJsLoader.physXJs))
        val eNOTIFY_TOUCH_FOUND: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_TOUCH_FOUND(PhysXJsLoader.physXJs))
        val eNOTIFY_TOUCH_PERSISTS: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_TOUCH_PERSISTS(PhysXJsLoader.physXJs))
        val eNOTIFY_TOUCH_LOST: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_TOUCH_LOST(PhysXJsLoader.physXJs))
        val eNOTIFY_TOUCH_CCD: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_TOUCH_CCD(PhysXJsLoader.physXJs))
        val eNOTIFY_THRESHOLD_FORCE_FOUND: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_FOUND(PhysXJsLoader.physXJs))
        val eNOTIFY_THRESHOLD_FORCE_PERSISTS: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_PERSISTS(PhysXJsLoader.physXJs))
        val eNOTIFY_THRESHOLD_FORCE_LOST: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_LOST(PhysXJsLoader.physXJs))
        val eNOTIFY_CONTACT_POINTS: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNOTIFY_CONTACT_POINTS(PhysXJsLoader.physXJs))
        val eDETECT_DISCRETE_CONTACT: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eDETECT_DISCRETE_CONTACT(PhysXJsLoader.physXJs))
        val eDETECT_CCD_CONTACT: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eDETECT_CCD_CONTACT(PhysXJsLoader.physXJs))
        val ePRE_SOLVER_VELOCITY: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_ePRE_SOLVER_VELOCITY(PhysXJsLoader.physXJs))
        val ePOST_SOLVER_VELOCITY: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_ePOST_SOLVER_VELOCITY(PhysXJsLoader.physXJs))
        val eCONTACT_EVENT_POSE: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eCONTACT_EVENT_POSE(PhysXJsLoader.physXJs))
        val eNEXT_FREE: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eNEXT_FREE(PhysXJsLoader.physXJs))
        val eCONTACT_DEFAULT: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eCONTACT_DEFAULT(PhysXJsLoader.physXJs))
        val eTRIGGER_DEFAULT: PxPairFlagEnum = PxPairFlagEnum(PxPairFlagEnum_eTRIGGER_DEFAULT(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSOLVE_CONTACT.value -> eSOLVE_CONTACT
            eMODIFY_CONTACTS.value -> eMODIFY_CONTACTS
            eNOTIFY_TOUCH_FOUND.value -> eNOTIFY_TOUCH_FOUND
            eNOTIFY_TOUCH_PERSISTS.value -> eNOTIFY_TOUCH_PERSISTS
            eNOTIFY_TOUCH_LOST.value -> eNOTIFY_TOUCH_LOST
            eNOTIFY_TOUCH_CCD.value -> eNOTIFY_TOUCH_CCD
            eNOTIFY_THRESHOLD_FORCE_FOUND.value -> eNOTIFY_THRESHOLD_FORCE_FOUND
            eNOTIFY_THRESHOLD_FORCE_PERSISTS.value -> eNOTIFY_THRESHOLD_FORCE_PERSISTS
            eNOTIFY_THRESHOLD_FORCE_LOST.value -> eNOTIFY_THRESHOLD_FORCE_LOST
            eNOTIFY_CONTACT_POINTS.value -> eNOTIFY_CONTACT_POINTS
            eDETECT_DISCRETE_CONTACT.value -> eDETECT_DISCRETE_CONTACT
            eDETECT_CCD_CONTACT.value -> eDETECT_CCD_CONTACT
            ePRE_SOLVER_VELOCITY.value -> ePRE_SOLVER_VELOCITY
            ePOST_SOLVER_VELOCITY.value -> ePOST_SOLVER_VELOCITY
            eCONTACT_EVENT_POSE.value -> eCONTACT_EVENT_POSE
            eNEXT_FREE.value -> eNEXT_FREE
            eCONTACT_DEFAULT.value -> eCONTACT_DEFAULT
            eTRIGGER_DEFAULT.value -> eTRIGGER_DEFAULT
            else -> error("Invalid enum value $value for enum PxPairFlagEnum")
        }
    }
}

private fun PxPairFlagEnum_eSOLVE_CONTACT(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eSOLVE_CONTACT()")
private fun PxPairFlagEnum_eMODIFY_CONTACTS(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eMODIFY_CONTACTS()")
private fun PxPairFlagEnum_eNOTIFY_TOUCH_FOUND(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_FOUND()")
private fun PxPairFlagEnum_eNOTIFY_TOUCH_PERSISTS(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_PERSISTS()")
private fun PxPairFlagEnum_eNOTIFY_TOUCH_LOST(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_LOST()")
private fun PxPairFlagEnum_eNOTIFY_TOUCH_CCD(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_TOUCH_CCD()")
private fun PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_FOUND(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_FOUND()")
private fun PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_PERSISTS(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_PERSISTS()")
private fun PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_LOST(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_THRESHOLD_FORCE_LOST()")
private fun PxPairFlagEnum_eNOTIFY_CONTACT_POINTS(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNOTIFY_CONTACT_POINTS()")
private fun PxPairFlagEnum_eDETECT_DISCRETE_CONTACT(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eDETECT_DISCRETE_CONTACT()")
private fun PxPairFlagEnum_eDETECT_CCD_CONTACT(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eDETECT_CCD_CONTACT()")
private fun PxPairFlagEnum_ePRE_SOLVER_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_ePRE_SOLVER_VELOCITY()")
private fun PxPairFlagEnum_ePOST_SOLVER_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_ePOST_SOLVER_VELOCITY()")
private fun PxPairFlagEnum_eCONTACT_EVENT_POSE(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eCONTACT_EVENT_POSE()")
private fun PxPairFlagEnum_eNEXT_FREE(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eNEXT_FREE()")
private fun PxPairFlagEnum_eCONTACT_DEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eCONTACT_DEFAULT()")
private fun PxPairFlagEnum_eTRIGGER_DEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxPairFlagEnum_eTRIGGER_DEFAULT()")

value class PxPruningStructureTypeEnum private constructor(val value: Int) {
    companion object {
        val eNONE: PxPruningStructureTypeEnum = PxPruningStructureTypeEnum(PxPruningStructureTypeEnum_eNONE(PhysXJsLoader.physXJs))
        val eDYNAMIC_AABB_TREE: PxPruningStructureTypeEnum = PxPruningStructureTypeEnum(PxPruningStructureTypeEnum_eDYNAMIC_AABB_TREE(PhysXJsLoader.physXJs))
        val eSTATIC_AABB_TREE: PxPruningStructureTypeEnum = PxPruningStructureTypeEnum(PxPruningStructureTypeEnum_eSTATIC_AABB_TREE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNONE.value -> eNONE
            eDYNAMIC_AABB_TREE.value -> eDYNAMIC_AABB_TREE
            eSTATIC_AABB_TREE.value -> eSTATIC_AABB_TREE
            else -> error("Invalid enum value $value for enum PxPruningStructureTypeEnum")
        }
    }
}

private fun PxPruningStructureTypeEnum_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxPruningStructureTypeEnum_eNONE()")
private fun PxPruningStructureTypeEnum_eDYNAMIC_AABB_TREE(module: JsAny): Int = js("module._emscripten_enum_PxPruningStructureTypeEnum_eDYNAMIC_AABB_TREE()")
private fun PxPruningStructureTypeEnum_eSTATIC_AABB_TREE(module: JsAny): Int = js("module._emscripten_enum_PxPruningStructureTypeEnum_eSTATIC_AABB_TREE()")

value class PxSolverTypeEnum private constructor(val value: Int) {
    companion object {
        val ePGS: PxSolverTypeEnum = PxSolverTypeEnum(PxSolverTypeEnum_ePGS(PhysXJsLoader.physXJs))
        val eTGS: PxSolverTypeEnum = PxSolverTypeEnum(PxSolverTypeEnum_eTGS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePGS.value -> ePGS
            eTGS.value -> eTGS
            else -> error("Invalid enum value $value for enum PxSolverTypeEnum")
        }
    }
}

private fun PxSolverTypeEnum_ePGS(module: JsAny): Int = js("module._emscripten_enum_PxSolverTypeEnum_ePGS()")
private fun PxSolverTypeEnum_eTGS(module: JsAny): Int = js("module._emscripten_enum_PxSolverTypeEnum_eTGS()")

value class PxTriggerPairFlagEnum private constructor(val value: Int) {
    companion object {
        val eREMOVED_SHAPE_TRIGGER: PxTriggerPairFlagEnum = PxTriggerPairFlagEnum(PxTriggerPairFlagEnum_eREMOVED_SHAPE_TRIGGER(PhysXJsLoader.physXJs))
        val eREMOVED_SHAPE_OTHER: PxTriggerPairFlagEnum = PxTriggerPairFlagEnum(PxTriggerPairFlagEnum_eREMOVED_SHAPE_OTHER(PhysXJsLoader.physXJs))
        val eNEXT_FREE: PxTriggerPairFlagEnum = PxTriggerPairFlagEnum(PxTriggerPairFlagEnum_eNEXT_FREE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eREMOVED_SHAPE_TRIGGER.value -> eREMOVED_SHAPE_TRIGGER
            eREMOVED_SHAPE_OTHER.value -> eREMOVED_SHAPE_OTHER
            eNEXT_FREE.value -> eNEXT_FREE
            else -> error("Invalid enum value $value for enum PxTriggerPairFlagEnum")
        }
    }
}

private fun PxTriggerPairFlagEnum_eREMOVED_SHAPE_TRIGGER(module: JsAny): Int = js("module._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_TRIGGER()")
private fun PxTriggerPairFlagEnum_eREMOVED_SHAPE_OTHER(module: JsAny): Int = js("module._emscripten_enum_PxTriggerPairFlagEnum_eREMOVED_SHAPE_OTHER()")
private fun PxTriggerPairFlagEnum_eNEXT_FREE(module: JsAny): Int = js("module._emscripten_enum_PxTriggerPairFlagEnum_eNEXT_FREE()")

