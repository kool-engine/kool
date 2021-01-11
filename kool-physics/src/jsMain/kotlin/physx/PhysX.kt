@file:Suppress("UnsafeCastFromDynamic", "FunctionName", "UNUSED_PARAMETER")

package physx

import de.fabmax.kool.util.logI
import kotlin.js.Promise

object PhysX {
    @JsName("physx")
    private var physx: dynamic = null
    private val physxPromise: Promise<dynamic> = js("require('physx-js')")()

    private val onLoadListeners = mutableListOf<() -> Unit>()

    var isInitialized = false
        private set

    // enums
    val PxActorFlag: PxActorFlag get() = physx
    val PxActorType: PxActorType get() = physx
    val PxBaseFlag: PxBaseFlag get() = physx
    val PxConvexFlag: PxConvexFlag get() = physx
    val PxConvexMeshGeometryFlag: PxConvexMeshGeometryFlag get() = physx
    val PxForceMode: PxForceMode get() = physx
    val PxRevoluteJointFlag: PxRevoluteJointFlag get() = physx
    val PxRigidBodyFlag: PxRigidBodyFlag get() = physx
    val PxSceneFlag: PxSceneFlag get() = physx
    val PxShapeFlag: PxShapeFlag get() = physx

    // top-level functions
    lateinit var Px: PxStatics
        private set

    lateinit var foundation: PxFoundation
        private set
    lateinit var physics: PxPhysics
        private set
    lateinit var cooking: PxCooking
        private set

    init {
        physxPromise.then { px: dynamic ->
            physx = px
            Px = js("new px.PxStatics()")

            val errorCallback = PxDefaultErrorCallback()
            val allocator = PxDefaultAllocator()
            foundation = Px.CreateFoundation(Px.PHYSICS_VERSION, allocator, errorCallback)

            val scale = PxTolerancesScale()
            physics = Px.CreatePhysics(Px.PHYSICS_VERSION, foundation, scale)

            Px.InitExtensions(physics)

            val cookingParams = PxCookingParams(scale)
            cooking = Px.CreateCooking(Px.PHYSICS_VERSION, foundation, cookingParams)

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

    // object factories

    fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry = js("new this.physx.PxBoxGeometry(hx, hy, hz)")

    fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry = js("new this.physx.PxCapsuleGeometry(radius, halfHeight)")

    fun PxConvexFlags(flags: Int): PxConvexFlags = js("new this.physx.PxConvexFlags(flags)")

    fun PxConvexMeshDesc(): PxConvexMeshDesc = js("new this.physx.PxConvexMeshDesc()")

    fun PxConvexMeshGeometryFlags(flags: Int): PxConvexMeshGeometryFlags = js("new this.physx.PxConvexMeshGeometryFlags(flags)")

    fun PxConvexMeshGeometry(mesh: PxConvexMesh, scaling: PxMeshScale, flags: PxConvexMeshGeometryFlags): PxConvexMeshGeometry =
        js("new this.physx.PxConvexMeshGeometry(mesh, scaling, flags)")

    fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams = js("new this.physx.PxCookingParams(sc)")

    fun PxDefaultAllocator(): PxDefaultAllocator = js("new this.physx.PxDefaultAllocator()")

    fun PxDefaultErrorCallback(): PxDefaultErrorCallback = js("new this.physx.PxDefaultErrorCallback()")

    fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData = js("new this.physx.PxFilterData(w0, w1, w2, w3)")

    fun PxHullPolygon(): PxHullPolygon = js("new this.physx.PxHullPolygon()")

    fun PxMeshScale(scale: PxVec3, rotation: PxQuat): PxMeshScale = js("new this.physx.PxMeshScale(scale, rotation)")

    fun PxPlaneGeometry(): PxPlaneGeometry = js("new this.physx.PxPlaneGeometry()")

    fun PxQuat(): PxQuat = js("new this.physx.PxQuat(0, 0, 0, 1)")
    fun PxQuat(x: Float, y: Float, z: Float, w: Float): PxQuat = js("new this.physx.PxQuat(x, y, z, w)")

    fun PxRevoluteJointFlags(flags: Int): PxRevoluteJointFlags = js("new this.physx.PxRevoluteJointFlags(flags)")

    fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("new this.physx.PxSceneDesc(scale)")

    fun PxSceneFlags(flags: Int): PxSceneFlags = js("new this.physx.PxSceneFlags(flags)")

    fun PxShapeFlags(flags: Int): PxShapeFlags = js("new this.physx.PxShapeFlags(flags)")

    fun PxSphereGeometry(ir: Float): PxSphereGeometry = js("new this.physx.PxSphereGeometry(ir)")

    fun PxTolerancesScale(): PxTolerancesScale = js("new this.physx.PxTolerancesScale()")

    fun PxTransform(): PxTransform {
        val t: PxTransform = js("new this.physx.PxTransform()")
        return t.setIdentity()
    }

    fun PxVec3(): PxVec3 = js("new this.physx.PxVec3(0, 0, 0, 0)")
    fun PxVec3(x: Float, y: Float, z: Float): PxVec3 = js("new this.physx.PxVec3(x, y, z)")

    fun VectorPxVec3(): VectorPxVec3 = js("new this.physx.VectorPxVec3()")

}
