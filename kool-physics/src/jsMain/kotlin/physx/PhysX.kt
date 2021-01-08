@file:Suppress("UnsafeCastFromDynamic", "FunctionName", "UNUSED_PARAMETER")

package physx

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.logI
import kotlin.js.Promise

object PhysX {
    @JsName("physx")
    private lateinit var physx: StaticPhysX
    private val physxPromise: Promise<dynamic> = js("require('physx-js')")()

    private val onLoadListeners = mutableListOf<() -> Unit>()

    var isInitialized = false
        private set

    lateinit var foundation: PxFoundation
    lateinit var physics: PxPhysics
    lateinit var cooking: PxCooking

    init {
        physxPromise.then { px: StaticPhysX ->
            physx = px

            val errorCallback = PxDefaultErrorCallback()
            val allocator = PxDefaultAllocator()
            foundation = px.PxCreateFoundation(px.PX_PHYSICS_VERSION, allocator, errorCallback)

            val scale = PxTolerancesScale()
            physics = px.PxCreatePhysics(px.PX_PHYSICS_VERSION, foundation, scale, false, null)

            px.PxInitExtensions(physics, null)

            val cookingParams = PxCookingParams(scale)
            cooking = px.PxCreateCooking(px.PX_PHYSICS_VERSION, foundation, cookingParams)

            logI { "PhysX loaded, version: ${pxVersionToString(px.PX_PHYSICS_VERSION)}" }

            //test()

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

    // helper functions

    fun List<Vec3f>.toPxVec3Vector(): PxVec3Vector {
        val vector: PxVec3Vector = js("new this.physx.PxVec3Vector()")
        forEach { vector.push_back(it.toPxVec3()) }
        return vector
    }

    fun createConvexMesh(vertices: PxVec3Vector, cooking: PxCooking, physics: PxPhysics): PxConvexMesh = js("this.physx.createConvexMesh(vertices, cooking, physics)")

    fun PxConvexMesh_getVertex(mesh: PxConvexMesh, index: Int): PxVec3 = js("this.physx.PxConvexMesh_getVertex(mesh, index)")

    fun PxConvexMesh_getIndex(mesh: PxConvexMesh, index: Int): Int = js("this.physx.PxConvexMesh_getIndex(mesh, index)")

    fun PxConvexMesh_getPolyAttribs(mesh: PxConvexMesh, polyIndex: Int): Int = js("this.physx.PxConvexMesh_getPolyAttribs(mesh, polyIndex)")

    // delegates to static functions

    val PxShapeFlag: PxShapeFlag get() = js("this.physx.PxShapeFlag")
    fun PxShapeFlags(flags: Int): PxShapeFlags = js("new this.physx.PxShapeFlags(flags)")

    val PxConvexMeshGeometryFlag: PxConvexMeshGeometryFlag get() = js("this.physx.PxConvexMeshGeometryFlag")
    fun PxConvexMeshGeometryFlags(flags: Int): PxConvexMeshGeometryFlags = js("new this.physx.PxConvexMeshGeometryFlags(flags)")

    val PxRevoluteJointFlag: PxRevoluteJointFlag get() = js("this.physx.PxRevoluteJointFlag")
    //fun PxRevoluteJointFlags(flags: Int): PxRevoluteJointFlags = js("new this.physx.PxRevoluteJointFlags(flags)")

//    fun PxCreateCooking(version: Int, foundation: PxFoundation, cookingParams: PxCookingParams) =
//        physx.PxCreateCooking(version, foundation, cookingParams)

    fun PxRevoluteJointCreate(actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform) =
        physx.PxRevoluteJointCreate(physics, actor0, localFrame0, actor1, localFrame1)

    // object factories

    fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry = js("new this.physx.PxBoxGeometry(hx, hy, hz)")

    fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry = js("new this.physx.PxCapsuleGeometry(radius, halfHeight)")

    fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry =
        js("new this.physx.PxConvexMeshGeometry(mesh, scaling, flags)")

    fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams = js("new this.physx.PxCookingParams(sc)")

    fun PxDefaultAllocator(): PxDefaultAllocator = js("new this.physx.PxDefaultAllocator()")

    fun PxDefaultErrorCallback(): PxDefaultErrorCallback = js("new this.physx.PxDefaultErrorCallback()")

    fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData = js("new this.physx.PxFilterData(w0, w1, w2, w3)")

    fun PxMeshScale(scale: PxVec3, rotation: PxQuat): PxMeshScale = js("new this.physx.PxMeshScale(scale, rotation)")

    fun PxPlaneGeometry(): PxPlaneGeometry = js("new this.physx.PxPlaneGeometry()")

    //fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("new this.physx.PxSceneDesc(scale)")
    fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("this.physx.getDefaultSceneDesc(scale, 0, null)")

    fun PxSphereGeometry(ir: Float): PxSphereBoxGeometry = js("new this.physx.PxSphereGeometry(ir)")

    fun PxTolerancesScale(): PxTolerancesScale = js("new this.physx.PxTolerancesScale()")
}

external interface StaticPhysX {
    val PX_PHYSICS_VERSION: Int

    fun PxCreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxDefaultErrorCallback): PxFoundation
    fun PxCreatePhysics(version: Int, foundation: PxFoundation, scale: PxTolerancesScale, trackOutstandingAllocations: Boolean, pvd: PxPvd?): PxPhysics
    fun PxCreateCooking(version: Int, foundation: PxFoundation, cookingParams: PxCookingParams): PxCooking
    fun PxInitExtensions(physics: PxPhysics, pvd: PxPvd?)

    fun PxRevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRevoluteJoint
}
