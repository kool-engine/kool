@file:Suppress("UnsafeCastFromDynamic", "FunctionName", "UNUSED_PARAMETER")

package physx

import de.fabmax.kool.util.logI

object PhysX {
    @JsName("physx")
    private lateinit var physx: StaticPhysX

    private val onLoadListeners = mutableListOf<() -> Unit>()

    var isInitialized = false
        private set

    lateinit var foundation: PxFoundation
    lateinit var physics: PxPhysics
    lateinit var cooking: PxCooking

    fun initPhysX() {
        physx = js("require('physx-js')")(object {
            /**
             * Called by PhysX loader when PhysX was loaded
             */
            @JsName("onRuntimeInitialized")
            private fun onRuntimeInitialized() {
                initialize()
            }
        })
    }

    private fun initialize() {
        logI { "PhysX loaded, version: ${pxVersionToString(physx.PX_PHYSICS_VERSION)}" }

        val errorCallback = PxDefaultErrorCallback()
        val allocator = PxDefaultAllocator()
        foundation = physx.PxCreateFoundation(physx.PX_PHYSICS_VERSION, allocator, errorCallback)

        val scale = PxTolerancesScale()
        physics = PxCreatePhysics(physx.PX_PHYSICS_VERSION, foundation, scale)

        physx.PxInitExtensions(physics, null)

        val cookingParams = PxCookingParams(scale)
        cooking = physx.PxCreateCooking(physx.PX_PHYSICS_VERSION, foundation, cookingParams)

        logI { "PhysX initialized" }

        isInitialized = true
        onLoadListeners.forEach { it() }
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

    // delegates to static functions

    val PxShapeFlag: PxShapeFlag get() = js("this.physx.PxShapeFlag")

    //fun createConvexMesh(vertices: List<PxVec3>, cooking: PxCooking, physics: PxPhysics): PxConvexMesh = js("this.physx.createConvexMesh(vertices, cooking, physics)")

    fun PxCreateCooking(version: Int, foundation: PxFoundation, cookingParams: PxCookingParams) =
        physx.PxCreateCooking(version, foundation, cookingParams)

    fun PxCreatePhysics(version: Int, foundation: PxFoundation, scale: PxTolerancesScale) =
        physx.PxCreatePhysics(version, foundation, scale, false, null)

    fun PxRevoluteJointCreate(actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform) =
        physx.PxRevoluteJointCreate(physics, actor0, localFrame0, actor1, localFrame1)

    // object factories

    fun PxBoxGeometry(hx: Float, hy: Float, hz: Float): PxBoxGeometry = js("new this.physx.PxBoxGeometry(hx, hy, hz)")

    fun PxCapsuleGeometry(radius: Float, halfHeight: Float): PxCapsuleGeometry = js("new this.physx.PxCapsuleGeometry(radius, halfHeight)")

    fun PxCookingParams(sc: PxTolerancesScale): PxCookingParams = js("new this.physx.PxCookingParams(sc)")

    fun PxDefaultAllocator(): PxDefaultAllocator = js("new this.physx.PxDefaultAllocator()")

    fun PxDefaultErrorCallback(): PxDefaultErrorCallback = js("new this.physx.PxDefaultErrorCallback()")

    fun PxFilterData(w0: Int, w1: Int, w2: Int, w3: Int): PxFilterData = js("new this.physx.PxFilterData(w0, w1, w2, w3)")

    fun PxPlaneGeometry(): PxPlaneGeometry = js("new this.physx.PxPlaneGeometry()")

    //fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("new this.physx.PxSceneDesc(scale)")
    fun PxSceneDesc(scale: PxTolerancesScale): PxSceneDesc = js("this.physx.getDefaultSceneDesc(scale, 0, null)")

    fun PxShapeFlags(flags: Int): PxShapeFlags = js("new this.physx.PxShapeFlags(flags)")

    fun PxSphereGeometry(ir: Float): PxSphereBoxGeometry = js("new this.physx.PxSphereGeometry(ir)")

    fun PxTolerancesScale(): PxTolerancesScale = js("new this.physx.PxTolerancesScale()")
}

external interface StaticPhysX {
    val PX_PHYSICS_VERSION: Int

    val PxShapeFlag: PxShapeFlag

    fun PxCreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxDefaultErrorCallback): PxFoundation
    fun PxCreatePhysics(version: Int, foundation: PxFoundation, scale: PxTolerancesScale, trackOutstandingAllocations: Boolean, pvd: PxPvd?): PxPhysics
    fun PxCreateCooking(version: Int, foundation: PxFoundation, cookingParams: PxCookingParams): PxCooking
    fun PxInitExtensions(physics: PxPhysics, pvd: PxPvd?)

    fun PxRevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRevoluteJoint
}
