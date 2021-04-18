package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.vehicle.FrictionPairs
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.lwjgl.system.MemoryStack
import physx.PxTopLevelFunctions
import physx.common.JavaErrorCallback
import physx.common.PxFoundation
import physx.common.PxTolerancesScale
import physx.cooking.PxCooking
import physx.cooking.PxCookingParams
import physx.cooking.PxMeshMidPhaseEnum
import physx.cooking.PxMidphaseDesc
import physx.extensions.PxDefaultAllocator
import physx.physics.PxPhysics
import physx.physics.PxShapeFlagEnum
import physx.physics.PxShapeFlags
import physx.vehicle.PxVehicleTopLevelFunctions
import physx.vehicle.PxVehicleUpdateModeEnum
import kotlin.coroutines.CoroutineContext

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    actual val isLoaded = true

    actual val defaultMaterial = Material(0.5f)
    val defaultSurfaceFrictions: FrictionPairs

    // default PhysX facilities
    val foundation: PxFoundation
    val physics: PxPhysics
    val cooking: PxCooking

    val defaultBodyFlags: PxShapeFlags

    init {
        logD { "Loading physx-js..." }

        val version = PxTopLevelFunctions.getPHYSICS_VERSION()
        val allocator = PxDefaultAllocator()
        foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, KoolErrorCallback())

        val scale = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, scale)

        PxTopLevelFunctions.InitExtensions(physics)

        val cookingParams = PxCookingParams(scale)
        cookingParams.midphaseDesc = PxMidphaseDesc().apply {
            setToDefault(PxMeshMidPhaseEnum.eBVH34)
            val bvh34 = mbvH34Desc
            bvh34.numPrimsPerLeaf = 4
            mbvH34Desc = bvh34
        }
        cookingParams.suppressTriangleMeshRemapTable = true
        cooking = PxTopLevelFunctions.CreateCooking(version, foundation, cookingParams)

        defaultBodyFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE or PxShapeFlagEnum.eSIMULATION_SHAPE).toByte())

        // init vehicle simulation framework
        MemoryStack.stackPush().use { mem ->
            val up = Vec3f.Y_AXIS.toPxVec3(mem.createPxVec3())
            val front = Vec3f.Z_AXIS.toPxVec3(mem.createPxVec3())
            PxVehicleTopLevelFunctions.InitVehicleSDK(physics)
            PxVehicleTopLevelFunctions.VehicleSetBasisVectors(up, front)
            PxVehicleTopLevelFunctions.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
        }
        defaultSurfaceFrictions = FrictionPairs(mapOf(defaultMaterial to 1.5f))

        logI { "PhysX loaded, version: ${pxVersionToString(version)}" }
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

    private class KoolErrorCallback : JavaErrorCallback() {
        override fun reportError(code: Int, message: String, file: String, line: Int) {
            PhysicsLogging.logPhysics(code, message, file, line)
        }
    }
}
