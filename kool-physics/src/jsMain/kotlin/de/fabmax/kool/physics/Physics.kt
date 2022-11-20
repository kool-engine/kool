package de.fabmax.kool.physics

import de.fabmax.kool.physics.vehicle.FrictionPairs
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.*
import kotlin.coroutines.CoroutineContext

@Suppress("UnsafeCastFromDynamic")
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

    actual val defaultMaterial = Material(0.5f)
    lateinit var defaultSurfaceFrictions: FrictionPairs
        private set

    // static top-level PhysX functions
    val TypeHelpers: TypeHelpers get() = PhysXJsLoader.physXJs.TypeHelpers.prototype
    val SupportFunctions: SupportFunctions get() = PhysXJsLoader.physXJs.SupportFunctions.prototype
    val Px: PxTopLevelFunctions get() = PhysXJsLoader.physXJs.PxTopLevelFunctions.prototype
    //val PxVehicle: PxVehicleTopLevelFunctions get() = PhysXJsLoader.physXJs.PxVehicleTopLevelFunctions.prototype
    val PxRigidActorExt: PxRigidActorExt get() = PhysXJsLoader.physXJs.PxRigidActorExt.prototype
    val PxRigidBodyExt: PxRigidBodyExt get() = PhysXJsLoader.physXJs.PxRigidBodyExt.prototype

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
            isLoading = true
            PhysXJsLoader.addOnLoadListener {
                val allocator = PxDefaultAllocator()
                val errorCallback = PxErrorCallbackImpl()
                errorCallback.reportError = { code, message, file, line ->
                    PhysicsLogging.logPhysics(code, message, file, line)
                }
                foundation = Px.CreateFoundation(Px.PHYSICS_VERSION, allocator, errorCallback)

                val scale = PxTolerancesScale()
                physics = Px.CreatePhysics(Px.PHYSICS_VERSION, foundation, scale)

                Px.InitExtensions(physics)

                val cookingParams = PxCookingParams(scale)
                cookingParams.suppressTriangleMeshRemapTable = true
                cooking = Px.CreateCooking(Px.PHYSICS_VERSION, foundation, cookingParams)

                defaultBodyFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE or PxShapeFlagEnum.eSIMULATION_SHAPE).toByte())

                // init vehicle simulation framework
//                MemoryStack.stackPush().use { mem ->
//                    val up = Vec3f.Y_AXIS.toPxVec3(mem.createPxVec3())
//                    val front = Vec3f.Z_AXIS.toPxVec3(mem.createPxVec3())
//                    PxVehicle.InitVehicleSDK(physics)
//                    PxVehicle.VehicleSetBasisVectors(up, front)
//                    PxVehicle.VehicleSetUpdateMode(PxVehicleUpdateModeEnum.eVELOCITY_CHANGE)
//                }

                logI { "PhysX loaded, version: ${pxVersionToString(Px.PHYSICS_VERSION)}" }
                loadingDeferred.complete(Unit)

                // create defaultSurfaceFrictions after loadingDeferred is complete, otherwise creation fails because
                // physics isn't loaded...
                defaultSurfaceFrictions = FrictionPairs(mapOf(defaultMaterial to 1.5f))
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