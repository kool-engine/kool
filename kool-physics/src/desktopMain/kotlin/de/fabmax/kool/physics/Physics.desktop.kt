package de.fabmax.kool.physics

import de.fabmax.kool.physics.geometry.ConvexMeshImpl
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.util.logI
import physx.PxTopLevelFunctions
import physx.common.*
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
import kotlin.math.max
import kotlin.math.min

internal actual fun PhysicsSystem(): PhysicsSystem = PhysicsImpl

object PhysicsImpl : PhysicsSystem {

    override val NOTIFY_TOUCH_FOUND = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND.value
    override val NOTIFY_TOUCH_LOST = PxPairFlagEnum.eNOTIFY_TOUCH_LOST.value
    override val NOTIFY_CONTACT_POINTS = PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value

    override val isLoaded = true

    val defaultCpuDispatcher: PxDefaultCpuDispatcher

    override val defaultMaterial = Material(0.5f)
    internal val vehicleFrame: PxVehicleFrame
    internal val unitCylinder: PxConvexMesh

    // default PhysX facilities
    val foundation: PxFoundation
    val physics: PxPhysics
    val cookingParams: PxCookingParams

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

        unitCylinder = ConvexMeshImpl.makePxConvexMesh(CylinderGeometry.convexMeshPoints(1f, 1f))

        // init vehicle simulation framework
        PxVehicleTopLevelFunctions.InitVehicleExtension(foundation)
        vehicleFrame = PxVehicleFrame().apply {
            lngAxis = PxVehicleAxesEnum.ePosZ
            latAxis = PxVehicleAxesEnum.ePosX
            vrtAxis = PxVehicleAxesEnum.ePosY
        }

        // try to choose a sensible number of worker threads:
        val numWorkers = min(16, max(1, Runtime.getRuntime().availableProcessors() - 2))
        defaultCpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numWorkers)

        logI { "PhysX loaded, version: ${pxVersionToString(version)}, using $numWorkers worker threads" }
    }

    override suspend fun loadAndAwaitPhysics() {
        // on JVM, there's nothing to do here
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
