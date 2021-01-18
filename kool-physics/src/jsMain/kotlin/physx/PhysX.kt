@file:Suppress("UnsafeCastFromDynamic", "FunctionName", "UNUSED_PARAMETER")

package physx

import de.fabmax.kool.physics.Material
import de.fabmax.kool.physics.PhysicsFilterData
import de.fabmax.kool.util.logI
import kotlin.js.Promise

object PhysX {
    @JsName("physx")
    internal var physx: dynamic = null
    private val physxPromise: Promise<dynamic> = js("require('physx-js')")()

    private val onLoadListeners = mutableListOf<() -> Unit>()

    var isInitialized = false
        private set

    // enum constants are available by their name in the root-module, so in theory it is possible to define them
    // as external interface and simply return the root-module. However in PhysX some enums have members with
    // the same name (e.g. PxRigidBodyFlag::eENABLE_CCD = 4 and PxSceneFlag::eENABLE_CCD = 2). In that case the value
    // accessible via the root-module is undefined.
    // As a work around enums are defined as object which return the internal emscripten mapping function which is
    // prefixed with the enum name, e.g.: physx._emscripten_enum_physx_PxRigidBodyFlag_eENABLE_CCD()
    //
    //val PxRigidBodyFlag: PxRigidBodyFlag get() = physx
    //val PxSceneFlag: PxSceneFlag get() = physx

    // top-level functions
    val Px: PxTopLevelFunctions get() = physx.PxTopLevelFunctions.prototype
    val PxVehicle: PxVehicleTopLevelFunctions get() = physx.PxVehicleTopLevelFunctions.prototype

    lateinit var foundation: PxFoundation
        private set
    lateinit var physics: PxPhysics
        private set
    lateinit var cooking: PxCooking
        private set

    lateinit var defaultBodyFlags: PxShapeFlags
        private set

    private val pxMaterials = mutableMapOf<Material, PxMaterial>()

    init {
        physxPromise.then { px: dynamic ->
            physx = px

            val errorCallback = PxDefaultErrorCallback()
            val allocator = PxDefaultAllocator()
            foundation = Px.CreateFoundation(Px.PHYSICS_VERSION, allocator, errorCallback)

            val scale = PxTolerancesScale()
            physics = Px.CreatePhysics(Px.PHYSICS_VERSION, foundation, scale)

            Px.InitExtensions(physics)

            val cookingParams = PxCookingParams(scale)
            cooking = Px.CreateCooking(Px.PHYSICS_VERSION, foundation, cookingParams)

            defaultBodyFlags = PxShapeFlags(PxShapeFlag.eSCENE_QUERY_SHAPE or PxShapeFlag.eSIMULATION_SHAPE)

            logI { "PhysX loaded, version: ${pxVersionToString(Px.PHYSICS_VERSION)}" }

            isInitialized = true
            onLoadListeners.forEach { it() }
        }
    }

    fun onLoadListener(l: () -> Unit) {
        onLoadListeners += l
        if (isInitialized) {
            l()
        }
    }

    fun pxVersionToString(pxVersion: Int): String {
        val major = pxVersion shr 24
        val minor = (pxVersion shr 16) and 0xff
        val bugfix = (pxVersion shr 8) and 0xff
        return "$major.$minor.$bugfix"
    }

    fun getPxMaterial(material: Material) = pxMaterials.getOrPut(material) {
        physics.createMaterial(material.staticFriction, material.dynamicFriction, material.restitution)
    }

    // object factories
    fun destroy(pxObject: Any) = physx.destroy(pxObject)

    fun PxBatchQueryDesc(maxRaycastsPerExecute: Int, maxSweepsPerExecute: Int, maxOverlapsPerExecute: Int): PxBatchQueryDesc =
        js("new this.physx.PxBatchQueryDesc(maxRaycastsPerExecute, maxSweepsPerExecute, maxOverlapsPerExecute)")

    fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry = js("new this.physx.PxBoxGeometry(hx, hy, hz)")

    fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry = js("new this.physx.PxCapsuleGeometry(radius, halfHeight)")

    fun PxConvexFlags(flags: Int): PxConvexFlags = js("new this.physx.PxConvexFlags(flags)")

    fun PxConvexMeshDesc(): PxConvexMeshDesc = js("new this.physx.PxConvexMeshDesc()")

    fun PxConvexMeshGeometryFlags(flags: Int): PxConvexMeshGeometryFlags = js("new this.physx.PxConvexMeshGeometryFlags(flags)")

    fun PxConvexMeshGeometry(mesh: PxConvexMesh): PxConvexMeshGeometry = js("new this.physx.PxConvexMeshGeometry(mesh)")
    fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale): PxConvexMeshGeometry = js("new this.physx.PxConvexMeshGeometry(mesh, scaling)")
    fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry =
        js("new this.physx.PxConvexMeshGeometry(mesh, scaling, flags)")

    fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams = js("new this.physx.PxCookingParams(sc)")

    fun PxDefaultAllocator(): PxDefaultAllocator = js("new this.physx.PxDefaultAllocator()")

    fun PxDefaultErrorCallback(): PxDefaultErrorCallback = js("new this.physx.PxDefaultErrorCallback()")

    fun PxFilterData(filterData: PhysicsFilterData) = PxFilterData(filterData.data[0], filterData.data[1], filterData.data[2], filterData.data[3])
    fun PxFilterData(w0: Int = 0, w1: Int = 0, w2: Int = 0, w3: Int = 0): PxFilterData = js("new this.physx.PxFilterData(w0, w1, w2, w3)")

    fun PxHullPolygon(): PxHullPolygon = js("new this.physx.PxHullPolygon()")

    fun PxMeshScale(scale: PxVec3, rotation: PxQuat): PxMeshScale = js("new this.physx.PxMeshScale(scale, rotation)")

    fun PxPlaneGeometry(): PxPlaneGeometry = js("new this.physx.PxPlaneGeometry()")

    fun PxQuat(): PxQuat = js("new this.physx.PxQuat(0, 0, 0, 1)")
    fun PxQuat(x: Float, y: Float, z: Float, w: Float): PxQuat = js("new this.physx.PxQuat(x, y, z, w)")

    fun PxRevoluteJointFlags(flags: Int): PxRevoluteJointFlags = js("new this.physx.PxRevoluteJointFlags(flags)")

    fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("new this.physx.PxSceneDesc(scale)")

    fun PxSceneFlags(flags: Int): PxSceneFlags = js("new this.physx.PxSceneFlags(flags)")

    fun PxShapeFlags(flags: Int): PxShapeFlags = js("new this.physx.PxShapeFlags(flags)")

    fun PxSimulationEventCallback(): PxSimulationEventCallback = js("new this.physx.JsPxSimulationEventCallback()")

    fun PxSphereGeometry(ir: Float): PxSphereGeometry = js("new this.physx.PxSphereGeometry(ir)")

    fun PxTolerancesScale(): PxTolerancesScale = js("new this.physx.PxTolerancesScale()")

    fun PxTransform(): PxTransform {
        val t: PxTransform = js("new this.physx.PxTransform()")
        return t.setIdentity()
    }

    fun PxVec3(): PxVec3 = js("new this.physx.PxVec3(0, 0, 0, 0)")
    fun PxVec3(x: Float, y: Float, z: Float): PxVec3 = js("new this.physx.PxVec3(x, y, z)")

    fun PxVehicleAntiRollBarData(): PxVehicleAntiRollBarData = js("new this.physx.PxVehicleAntiRollBarData()")

    fun PxVehicleChassisData(): PxVehicleChassisData = js("new this.physx.PxVehicleChassisData()")

    fun PxVehicleDifferential4WData(): PxVehicleDifferential4WData = js("new this.physx.PxVehicleDifferential4WData()")

    fun PxVehicleDrivableSurfaceToTireFrictionPairs(maxNbTireTypes: Int, maxNbSurfaceTypes: Int): PxVehicleDrivableSurfaceToTireFrictionPairs =
        this.physx.PxVehicleDrivableSurfaceToTireFrictionPairs.prototype.allocate(maxNbTireTypes, maxNbSurfaceTypes)

    fun PxVehicleDrivableSurfaceType(): PxVehicleDrivableSurfaceType = js("new this.physx.PxVehicleDrivableSurfaceType()")

    fun PxVehicleDrive4W_allocate(nbWheels: Int): PxVehicleDrive4W = physx.PxVehicleDrive4W.prototype.allocate(nbWheels)

    fun PxVehicleDriveSimData4W(): PxVehicleDriveSimData4W = js("new this.physx.PxVehicleDriveSimData4W()")

    fun PxVehicleEngineData(): PxVehicleEngineData = js("new this.physx.PxVehicleEngineData()")

    fun PxVehicleSuspensionData(): PxVehicleSuspensionData = js("new this.physx.PxVehicleSuspensionData()")

    fun PxVehicleTireData(): PxVehicleTireData = js("new this.physx.PxVehicleTireData()")

    fun PxVehicleWheelData(): PxVehicleWheelData = js("new this.physx.PxVehicleWheelData()")

    fun PxVehicleWheelQueryResult(): PxVehicleWheelQueryResult = js("new this.physx.PxVehicleWheelQueryResult()")

    fun PxVehicleWheelsSimData_allocate(nbWheels: Int): PxVehicleWheelsSimData = physx.PxVehicleWheelsSimData.prototype.allocate(nbWheels)

    fun PxWheelQueryResult(): PxWheelQueryResult = js("new this.physx.PxWheelQueryResult()")

    fun Vector_PxReal(size: Int = 0): Vector_PxReal = if (size == 0) {
        js("new this.physx.Vector_PxReal()")
    } else {
        js("new this.physx.Vector_PxReal(size)")
    }

    fun Vector_PxVec3(size: Int = 0): Vector_PxVec3 = if (size == 0) {
        js("new this.physx.Vector_PxVec3()")
    } else {
        js("new this.physx.Vector_PxVec3(size)")
    }

    fun Vector_PxRaycastHit(size: Int = 0): Vector_PxRaycastHit = if (size == 0) {
        js("new this.physx.Vector_PxRaycastHit()")
    } else {
        js("new this.physx.Vector_PxRaycastHit(size)")
    }

    fun Vector_PxRaycastQueryResult(size: Int = 0): Vector_PxRaycastQueryResult = if (size == 0) {
        js("new this.physx.Vector_PxRaycastQueryResult()")
    } else {
        js("new this.physx.Vector_PxRaycastQueryResult(size)")
    }

    fun Vector_PxSweepHit(size: Int = 0): Vector_PxSweepHit = if (size == 0) {
        js("new this.physx.Vector_PxSweepHit()")
    } else {
        js("new this.physx.Vector_PxSweepHit(size)")
    }

    fun Vector_PxSweepQueryResult(size: Int = 0): Vector_PxSweepQueryResult = if (size == 0) {
        js("new this.physx.Vector_PxSweepQueryResult()")
    } else {
        js("new this.physx.Vector_PxSweepQueryResult(size)")
    }

    fun Vector_PxVehicleDrivableSurfaceType(size: Int = 0): Vector_PxVehicleDrivableSurfaceType = if (size == 0) {
        js("new this.physx.Vector_PxVehicleDrivableSurfaceType()")
    } else {
        js("new this.physx.Vector_PxVehicleDrivableSurfaceType(size)")
    }

    fun Vector_PxWheelQueryResult(size: Int = 0): Vector_PxWheelQueryResult = if (size == 0) {
        js("new this.physx.Vector_PxWheelQueryResult()")
    } else {
        js("new this.physx.Vector_PxWheelQueryResult(size)")
    }

    fun Vector_PxVehicleWheels(size: Int = 0): Vector_PxVehicleWheels = if (size == 0) {
        js("new this.physx.Vector_PxVehicleWheels()")
    } else {
        js("new this.physx.Vector_PxVehicleWheels(size)")
    }

}
