@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics

import de.fabmax.kool.util.logI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.*
import kotlin.coroutines.CoroutineContext

// static top-level PhysX functions
val NativeArrayHelpers: NativeArrayHelpers get() = PhysXJsLoader.physXJs.NativeArrayHelpers.prototype as NativeArrayHelpers
val SupportFunctions: SupportFunctions get() = PhysXJsLoader.physXJs.SupportFunctions.prototype as SupportFunctions
val PxTopLevelFunctions: PxTopLevelFunctions get() = PhysXJsLoader.physXJs.PxTopLevelFunctions.prototype as PxTopLevelFunctions
val PxVehicleTopLevelFunctions: PxVehicleTopLevelFunctions get() = PhysXJsLoader.physXJs.PxVehicleTopLevelFunctions.prototype as PxVehicleTopLevelFunctions
val PxRigidActorExt: PxRigidActorExt get() = PhysXJsLoader.physXJs.PxRigidActorExt.prototype as PxRigidActorExt
val PxRigidBodyExt: PxRigidBodyExt get() = PhysXJsLoader.physXJs.PxRigidBodyExt.prototype as PxRigidBodyExt
val PxVehicleTireForceParamsExt: PxVehicleTireForceParamsExt get() = PhysXJsLoader.physXJs.PxVehicleTireForceParamsExt.prototype as PxVehicleTireForceParamsExt

actual object Physics : CoroutineScope {

    actual val NOTIFY_TOUCH_FOUND: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
    actual val NOTIFY_TOUCH_LOST: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_LOST
    actual val NOTIFY_CONTACT_POINTS: Int
        get() = PxPairFlagEnum.eNOTIFY_CONTACT_POINTS

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val loadingDeferred = CompletableDeferred<Unit>(job)
    private var isLoading = false
    actual val isLoaded: Boolean
        get() = loadingDeferred.isCompleted

    lateinit var defaultCpuDispatcher: PxDefaultCpuDispatcher

    actual val defaultMaterial = Material(0.5f)
    internal lateinit var vehicleFrame: PxVehicleFrame
    internal lateinit var unitCylinderSweepMesh: PxConvexMesh

    // default PhysX facilities
    lateinit var foundation: PxFoundation
        private set
    lateinit var physics: PxPhysics
        private set
    lateinit var cookingParams: PxCookingParams
        private set

    actual fun loadPhysics() {
        if (!isLoading) {
            isLoading = true
            PhysXJsLoader.addOnLoadListener {
                val allocator = PxDefaultAllocator()
                val errorCallback = PxErrorCallbackImpl()
                errorCallback.reportError = { code, message, file, line ->
                    PhysicsLogging.logPhysics(code, message, file, line)
                }
                foundation = PxTopLevelFunctions.CreateFoundation(PxTopLevelFunctions.PHYSICS_VERSION, allocator, errorCallback)

                val scale = PxTolerancesScale()
                physics = PxTopLevelFunctions.CreatePhysics(PxTopLevelFunctions.PHYSICS_VERSION, foundation, scale)

                PxTopLevelFunctions.InitExtensions(physics)

                cookingParams = PxCookingParams(scale)
                cookingParams.suppressTriangleMeshRemapTable = true

                // init vehicle simulation framework
                PxVehicleTopLevelFunctions.InitVehicleExtension(foundation)
                vehicleFrame = PxVehicleFrame().apply {
                    lngAxis = PxVehicleAxesEnum.ePosZ
                    latAxis = PxVehicleAxesEnum.ePosX
                    vrtAxis = PxVehicleAxesEnum.ePosY
                }
                unitCylinderSweepMesh = PxVehicleTopLevelFunctions.VehicleUnitCylinderSweepMeshCreate(vehicleFrame, physics, cookingParams)

                defaultCpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(0)

                logI { "PhysX loaded, version: ${pxVersionToString(PxTopLevelFunctions.PHYSICS_VERSION)}" }
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