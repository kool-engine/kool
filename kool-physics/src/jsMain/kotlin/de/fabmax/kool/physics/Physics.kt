package de.fabmax.kool.physics

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.*
import kotlin.coroutines.CoroutineContext

@Suppress("UnsafeCastFromDynamic")
actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val loadingDeferred = CompletableDeferred<Unit>(job)
    private var isLoading = false
    actual val isLoaded: Boolean
        get() = loadingDeferred.isCompleted

    // static top-level PhysX functions
    val Px: PxTopLevelFunctions get() = PhysXJsLoader.physXJs.PxTopLevelFunctions.prototype
    val PxVehicle: PxVehicleTopLevelFunctions get() = PhysXJsLoader.physXJs.PxVehicleTopLevelFunctions.prototype

    // default PhysX facilities
    lateinit var foundation: PxFoundation
        private set
    lateinit var physics: PxPhysics
        private set
    lateinit var cooking: PxCooking
        private set

    lateinit var defaultBodyFlags: PxShapeFlags
        private set

    actual fun loadPhysics() {
        if (!isLoading) {
            logD { "Loading physx-js..." }
            isLoading = true

            PhysXJsLoader.addOnLoadListener {
                val errorCallback = PxDefaultErrorCallback()
                val allocator = PxDefaultAllocator()
                foundation = Px.CreateFoundation(Px.PHYSICS_VERSION, allocator, errorCallback)

                val scale = PxTolerancesScale()
                physics = Px.CreatePhysics(Px.PHYSICS_VERSION, foundation, scale)

                Px.InitExtensions(physics)

                val cookingParams = PxCookingParams(scale)
                cooking = Px.CreateCooking(Px.PHYSICS_VERSION, foundation, cookingParams)

                defaultBodyFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE or PxShapeFlagEnum.eSIMULATION_SHAPE).toByte())

                // init vehicle simulation framework
                val up = Vec3f.Y_AXIS.toPxVec3(PxVec3())
                val front = Vec3f.Z_AXIS.toPxVec3(PxVec3())
                PxVehicle.InitVehicleSDK(physics)
                PxVehicle.VehicleSetBasisVectors(up, front)
                PxVehicle.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
                PhysXJsLoader.destroy(up)
                PhysXJsLoader.destroy(front)

                logI { "PhysX loaded, version: ${pxVersionToString(Px.PHYSICS_VERSION)}" }
                loadingDeferred.complete(Unit)
            }
            PhysXJsLoader.loadModule()
        }
    }

    actual suspend fun awaitLoaded() {
        if (!isLoading) {
            loadPhysics()
        }
        loadingDeferred.await()
    }

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
}