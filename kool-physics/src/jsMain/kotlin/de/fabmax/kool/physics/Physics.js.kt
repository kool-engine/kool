@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")

package de.fabmax.kool.physics

import de.fabmax.kool.physics.geometry.ConvexMeshImpl
import de.fabmax.kool.physics.geometry.CylinderGeometry
import de.fabmax.kool.util.logI
import physx.*

// static top-level PhysX functions
val NativeArrayHelpers: NativeArrayHelpers get() = PhysXJsLoader.physXJs.NativeArrayHelpers.prototype as NativeArrayHelpers
val SupportFunctions: SupportFunctions get() = PhysXJsLoader.physXJs.SupportFunctions.prototype as SupportFunctions
val PxTopLevelFunctions: PxTopLevelFunctions get() = PhysXJsLoader.physXJs.PxTopLevelFunctions.prototype as PxTopLevelFunctions
val PxVehicleTopLevelFunctions: PxVehicleTopLevelFunctions get() = PhysXJsLoader.physXJs.PxVehicleTopLevelFunctions.prototype as PxVehicleTopLevelFunctions
val PxRigidActorExt: PxRigidActorExt get() = PhysXJsLoader.physXJs.PxRigidActorExt.prototype as PxRigidActorExt
val PxRigidBodyExt: PxRigidBodyExt get() = PhysXJsLoader.physXJs.PxRigidBodyExt.prototype as PxRigidBodyExt
val PxVehicleTireForceParamsExt: PxVehicleTireForceParamsExt get() = PhysXJsLoader.physXJs.PxVehicleTireForceParamsExt.prototype as PxVehicleTireForceParamsExt

internal actual fun PhysicsSystem(): PhysicsSystem = PhysicsImpl

object PhysicsImpl : PhysicsSystem {

    override val NOTIFY_TOUCH_FOUND: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND
    override val NOTIFY_TOUCH_LOST: Int
        get() = PxPairFlagEnum.eNOTIFY_TOUCH_LOST
    override val NOTIFY_CONTACT_POINTS: Int
        get() = PxPairFlagEnum.eNOTIFY_CONTACT_POINTS

    private var isLoading = false
    override val isLoaded: Boolean
        get() = PhysXJsLoader.physxDeferred.isCompleted

    lateinit var defaultCpuDispatcher: PxDefaultCpuDispatcher

    override val defaultMaterial = Material(0.5f)
    internal lateinit var vehicleFrame: PxVehicleFrame
    internal lateinit var unitCylinder: PxConvexMesh

    // default PhysX facilities
    lateinit var foundation: PxFoundation
        private set
    lateinit var physics: PxPhysics
        private set
    lateinit var cookingParams: PxCookingParams
        private set

    override suspend fun loadAndAwaitPhysics() {
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

                unitCylinder = ConvexMeshImpl.makePxConvexMesh(CylinderGeometry.convexMeshPoints(1f, 1f))

                // init vehicle simulation framework
                PxVehicleTopLevelFunctions.InitVehicleExtension(foundation)
                vehicleFrame = PxVehicleFrame().apply {
                    lngAxis = PxVehicleAxesEnum.ePosZ
                    latAxis = PxVehicleAxesEnum.ePosX
                    vrtAxis = PxVehicleAxesEnum.ePosY
                }

                defaultCpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(0)

                logI { "PhysX loaded, version: ${pxVersionToString(PxTopLevelFunctions.PHYSICS_VERSION)}" }
            }
            PhysXJsLoader.loadModule()
        }

        PhysXJsLoader.physxDeferred.await()
    }

    private fun pxVersionToString(pxVersion: Int): String {
        val major = pxVersion shr 24
        val minor = (pxVersion shr 16) and 0xff
        val bugfix = (pxVersion shr 8) and 0xff
        return "$major.$minor.$bugfix"
    }
}