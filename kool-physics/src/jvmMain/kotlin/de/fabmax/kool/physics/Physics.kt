package de.fabmax.kool.physics

import de.fabmax.kool.util.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.PxTopLevelFunctions
import physx.common.*
import physx.cooking.PxCooking
import physx.cooking.PxCookingParams
import physx.cooking.PxMeshMidPhaseEnum
import physx.cooking.PxMidphaseDesc
import physx.geometry.PxConvexMesh
import physx.physics.PxPairFlagEnum
import physx.physics.PxPhysics
import physx.support.PxPvd
import physx.vehicle2.PxVehicleAxesEnum
import physx.vehicle2.PxVehicleFrame
import physx.vehicle2.PxVehicleTopLevelFunctions
import kotlin.coroutines.CoroutineContext
import kotlin.math.max
import kotlin.math.min

actual object Physics : CoroutineScope {

    actual val NOTIFY_TOUCH_FOUND: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND.value
    actual val NOTIFY_TOUCH_LOST: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_LOST.value
    actual val NOTIFY_CONTACT_POINTS: Int
        get() = PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    actual val isLoaded = true

    val defaultCpuDispatcher: PxDefaultCpuDispatcher

    actual val defaultMaterial = Material(0.5f)
    internal val vehicleFrame: PxVehicleFrame
    internal val unitCylinderSweepMesh: PxConvexMesh

    // default PhysX facilities
    val foundation: PxFoundation
    val physics: PxPhysics
    val cookingParams: PxCookingParams
    val cooking: PxCooking

    init {
        val version = PxTopLevelFunctions.getPHYSICS_VERSION()
        val allocator = PxDefaultAllocator()
        foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, KoolErrorCallback())

        val pvd: PxPvd? = null
//        val pvd = PxTopLevelFunctions.CreatePvd(foundation)
//        val socketTransport = PxTopLevelFunctions.DefaultPvdSocketTransportCreate("localhost", 5425, 10)
//        val flags = PxPvdInstrumentationFlags(PxPvdInstrumentationFlagEnum.eALL.value.toByte())
//        pvd.connect(socketTransport, flags)

        val scale = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, scale, pvd)

        PxTopLevelFunctions.InitExtensions(physics)

        cookingParams = PxCookingParams(scale)
        cookingParams.midphaseDesc = PxMidphaseDesc().apply {
            setToDefault(PxMeshMidPhaseEnum.eBVH34)
            val bvh34 = mbvH34Desc
            bvh34.numPrimsPerLeaf = 4
            mbvH34Desc = bvh34
        }
        cookingParams.suppressTriangleMeshRemapTable = true
        cooking = PxTopLevelFunctions.CreateCooking(version, foundation, cookingParams)

        // init vehicle simulation framework
        PxVehicleTopLevelFunctions.InitVehicleExtension(foundation)
        vehicleFrame = PxVehicleFrame().apply {
            lngAxis = PxVehicleAxesEnum.ePosZ
            latAxis = PxVehicleAxesEnum.ePosX
            vrtAxis = PxVehicleAxesEnum.ePosY
        }
        unitCylinderSweepMesh = PxVehicleTopLevelFunctions.VehicleUnitCylinderSweepMeshCreate(vehicleFrame, physics, cookingParams)

        // try to choose a sensible number of worker threads:
        val numWorkers = min(16, max(1, Runtime.getRuntime().availableProcessors() - 2))
        defaultCpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numWorkers)

        logI { "PhysX loaded, version: ${pxVersionToString(version)}, using $numWorkers worker threads" }
    }

    actual fun loadPhysics() { }

    actual suspend fun awaitLoaded() { }

    fun checkIsLoaded() {
        if (!isLoaded) {
            throw IllegalStateException("Physics subsystem is not loaded. Call loadPhysics() first and wait for loading to be finished.")
        }
    }

    private fun pxVersionToString(pxVersion: Int): String {
        val major = pxVersion shr 24
        val minor = (pxVersion shr 16) and 0xff
        val bugfix = (pxVersion shr 8) and 0xff
        return "$major.$minor.$bugfix"
    }

    private class KoolErrorCallback : PxErrorCallbackImpl() {
        override fun reportError(code: PxErrorCodeEnum, message: String, file: String, line: Int) {
            PhysicsLogging.logPhysics(code.value, message, file, line)
        }
    }
}
