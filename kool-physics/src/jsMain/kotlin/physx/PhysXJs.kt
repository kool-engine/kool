/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused")

package physx

external interface PxTopLevelFunctions {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var PHYSICS_VERSION: Int

    /**
     * @return WebIDL type: [PxSimulationFilterShader] (Value)
     */
    fun DefaultFilterShader(): PxSimulationFilterShader

    /**
     * @param sceneDesc    WebIDL type: [PxSceneDesc]
     * @param filterShader WebIDL type: [PassThroughFilterShader]
     */
    fun setupPassThroughFilterShader(sceneDesc: PxSceneDesc, filterShader: PassThroughFilterShader)

    /**
     * @param scene WebIDL type: [PxScene] (Ref)
     * @return WebIDL type: [PxControllerManager]
     */
    fun CreateControllerManager(scene: PxScene): PxControllerManager

    /**
     * @param scene          WebIDL type: [PxScene] (Ref)
     * @param lockingEnabled WebIDL type: boolean
     * @return WebIDL type: [PxControllerManager]
     */
    fun CreateControllerManager(scene: PxScene, lockingEnabled: Boolean): PxControllerManager

    /**
     * @param version       WebIDL type: unsigned long
     * @param allocator     WebIDL type: [PxDefaultAllocator] (Ref)
     * @param errorCallback WebIDL type: [PxErrorCallback] (Ref)
     * @return WebIDL type: [PxFoundation]
     */
    fun CreateFoundation(version: Int, allocator: PxDefaultAllocator, errorCallback: PxErrorCallback): PxFoundation

    /**
     * @param version    WebIDL type: unsigned long
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @param params     WebIDL type: [PxTolerancesScale] (Const, Ref)
     * @return WebIDL type: [PxPhysics]
     */
    fun CreatePhysics(version: Int, foundation: PxFoundation, params: PxTolerancesScale): PxPhysics

    /**
     * @param version    WebIDL type: unsigned long
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @param params     WebIDL type: [PxTolerancesScale] (Const, Ref)
     * @param pvd        WebIDL type: [PxPvd] (Nullable)
     * @return WebIDL type: [PxPhysics]
     */
    fun CreatePhysics(version: Int, foundation: PxFoundation, params: PxTolerancesScale, pvd: PxPvd?): PxPhysics

    /**
     * @param version    WebIDL type: unsigned long
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @param params     WebIDL type: [PxTolerancesScale] (Const, Ref)
     * @param pvd        WebIDL type: [PxPvd] (Nullable)
     * @param omniPvd    WebIDL type: [PxOmniPvd] (Nullable)
     * @return WebIDL type: [PxPhysics]
     */
    fun CreatePhysics(version: Int, foundation: PxFoundation, params: PxTolerancesScale, pvd: PxPvd?, omniPvd: PxOmniPvd?): PxPhysics

    /**
     * @param numThreads WebIDL type: unsigned long
     * @return WebIDL type: [PxDefaultCpuDispatcher]
     */
    fun DefaultCpuDispatcherCreate(numThreads: Int): PxDefaultCpuDispatcher

    /**
     * @param physics WebIDL type: [PxPhysics] (Ref)
     * @return WebIDL type: boolean
     */
    fun InitExtensions(physics: PxPhysics): Boolean

    fun CloseExtensions()

    /**
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @return WebIDL type: [PxPvd]
     */
    fun CreatePvd(foundation: PxFoundation): PxPvd

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxD6Joint]
     */
    fun D6JointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxD6Joint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxDistanceJoint]
     */
    fun DistanceJointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxDistanceJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxFixedJoint]
     */
    fun FixedJointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxFixedJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor]
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor]
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxGearJoint]
     */
    fun GearJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxGearJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxPrismaticJoint]
     */
    fun PrismaticJointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxPrismaticJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor]
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor]
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxRackAndPinionJoint]
     */
    fun RackAndPinionJointCreate(physics: PxPhysics, actor0: PxRigidActor, localFrame0: PxTransform, actor1: PxRigidActor, localFrame1: PxTransform): PxRackAndPinionJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxRevoluteJoint]
     */
    fun RevoluteJointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxRevoluteJoint

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param actor0      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame0 WebIDL type: [PxTransform] (Ref)
     * @param actor1      WebIDL type: [PxRigidActor] (Nullable)
     * @param localFrame1 WebIDL type: [PxTransform] (Ref)
     * @return WebIDL type: [PxSphericalJoint]
     */
    fun SphericalJointCreate(physics: PxPhysics, actor0: PxRigidActor?, localFrame0: PxTransform, actor1: PxRigidActor?, localFrame1: PxTransform): PxSphericalJoint

    /**
     * @param params WebIDL type: [PxCookingParams] (Const, Ref)
     * @param desc   WebIDL type: [PxConvexMeshDesc] (Const, Ref)
     * @return WebIDL type: [PxConvexMesh]
     */
    fun CreateConvexMesh(params: PxCookingParams, desc: PxConvexMeshDesc): PxConvexMesh

    /**
     * @param params WebIDL type: [PxCookingParams] (Const, Ref)
     * @param desc   WebIDL type: [PxTriangleMeshDesc] (Const, Ref)
     * @return WebIDL type: [PxTriangleMesh]
     */
    fun CreateTriangleMesh(params: PxCookingParams, desc: PxTriangleMeshDesc): PxTriangleMesh

    /**
     * @param desc WebIDL type: [PxHeightFieldDesc] (Const, Ref)
     * @return WebIDL type: [PxHeightField]
     */
    fun CreateHeightField(desc: PxHeightFieldDesc): PxHeightField

    /**
     * @param params WebIDL type: [PxCookingParams] (Const, Ref)
     * @param desc   WebIDL type: [PxTriangleMeshDesc] (Const, Ref)
     * @param stream WebIDL type: [PxOutputStream] (Ref)
     * @return WebIDL type: boolean
     */
    fun CookTriangleMesh(params: PxCookingParams, desc: PxTriangleMeshDesc, stream: PxOutputStream): Boolean

    /**
     * @param params WebIDL type: [PxCookingParams] (Const, Ref)
     * @param desc   WebIDL type: [PxConvexMeshDesc] (Const, Ref)
     * @param stream WebIDL type: [PxOutputStream] (Ref)
     * @return WebIDL type: boolean
     */
    fun CookConvexMesh(params: PxCookingParams, desc: PxConvexMeshDesc, stream: PxOutputStream): Boolean

    /**
     * @param sdk       WebIDL type: [PxPhysics] (Ref)
     * @param transform WebIDL type: [PxTransform] (Const, Ref)
     * @param shape     WebIDL type: [PxShape] (Ref)
     * @param density   WebIDL type: float
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateDynamicFromShape(sdk: PxPhysics, transform: PxTransform, shape: PxShape, density: Float): PxRigidDynamic

    /**
     * @param sdk       WebIDL type: [PxPhysics] (Ref)
     * @param transform WebIDL type: [PxTransform] (Const, Ref)
     * @param geometry  WebIDL type: [PxGeometry] (Const, Ref)
     * @param material  WebIDL type: [PxMaterial] (Ref)
     * @param density   WebIDL type: float
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateDynamic(sdk: PxPhysics, transform: PxTransform, geometry: PxGeometry, material: PxMaterial, density: Float): PxRigidDynamic

    /**
     * @param sdk         WebIDL type: [PxPhysics] (Ref)
     * @param transform   WebIDL type: [PxTransform] (Const, Ref)
     * @param geometry    WebIDL type: [PxGeometry] (Const, Ref)
     * @param material    WebIDL type: [PxMaterial] (Ref)
     * @param density     WebIDL type: float
     * @param shapeOffset WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateDynamic(sdk: PxPhysics, transform: PxTransform, geometry: PxGeometry, material: PxMaterial, density: Float, shapeOffset: PxTransform): PxRigidDynamic

    /**
     * @param sdk       WebIDL type: [PxPhysics] (Ref)
     * @param transform WebIDL type: [PxTransform] (Const, Ref)
     * @param shape     WebIDL type: [PxShape] (Ref)
     * @param density   WebIDL type: float
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateKinematicFromShape(sdk: PxPhysics, transform: PxTransform, shape: PxShape, density: Float): PxRigidDynamic

    /**
     * @param sdk       WebIDL type: [PxPhysics] (Ref)
     * @param transform WebIDL type: [PxTransform] (Const, Ref)
     * @param geometry  WebIDL type: [PxGeometry] (Const, Ref)
     * @param material  WebIDL type: [PxMaterial] (Ref)
     * @param density   WebIDL type: float
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateKinematic(sdk: PxPhysics, transform: PxTransform, geometry: PxGeometry, material: PxMaterial, density: Float): PxRigidDynamic

    /**
     * @param sdk         WebIDL type: [PxPhysics] (Ref)
     * @param transform   WebIDL type: [PxTransform] (Const, Ref)
     * @param geometry    WebIDL type: [PxGeometry] (Const, Ref)
     * @param material    WebIDL type: [PxMaterial] (Ref)
     * @param density     WebIDL type: float
     * @param shapeOffset WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CreateKinematic(sdk: PxPhysics, transform: PxTransform, geometry: PxGeometry, material: PxMaterial, density: Float, shapeOffset: PxTransform): PxRigidDynamic

    /**
     * @param sdk       WebIDL type: [PxPhysics] (Ref)
     * @param transform WebIDL type: [PxTransform] (Const, Ref)
     * @param shape     WebIDL type: [PxShape] (Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun CreateStaticFromShape(sdk: PxPhysics, transform: PxTransform, shape: PxShape): PxRigidStatic

    /**
     * @param sdk         WebIDL type: [PxPhysics] (Ref)
     * @param transform   WebIDL type: [PxTransform] (Const, Ref)
     * @param geometry    WebIDL type: [PxGeometry] (Const, Ref)
     * @param material    WebIDL type: [PxMaterial] (Ref)
     * @param shapeOffset WebIDL type: [PxTransform] (Const, Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun CreateStatic(sdk: PxPhysics, transform: PxTransform, geometry: PxGeometry, material: PxMaterial, shapeOffset: PxTransform): PxRigidStatic

    /**
     * @param sdk      WebIDL type: [PxPhysics] (Ref)
     * @param plane    WebIDL type: [PxPlane] (Const, Ref)
     * @param material WebIDL type: [PxMaterial] (Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun CreatePlane(sdk: PxPhysics, plane: PxPlane, material: PxMaterial): PxRigidStatic

    /**
     * @param physics     WebIDL type: [PxPhysics] (Ref)
     * @param from        WebIDL type: [PxShape] (Const, Ref)
     * @param isExclusive WebIDL type: boolean
     * @return WebIDL type: [PxShape]
     */
    fun CloneShape(physics: PxPhysics, from: PxShape, isExclusive: Boolean): PxShape

    /**
     * @param physicsSDK WebIDL type: [PxPhysics] (Ref)
     * @param transform  WebIDL type: [PxTransform] (Const, Ref)
     * @param from       WebIDL type: [PxRigidActor] (Const, Ref)
     * @return WebIDL type: [PxRigidStatic]
     */
    fun CloneStatic(physicsSDK: PxPhysics, transform: PxTransform, from: PxRigidActor): PxRigidStatic

    /**
     * @param physicsSDK WebIDL type: [PxPhysics] (Ref)
     * @param transform  WebIDL type: [PxTransform] (Const, Ref)
     * @param from       WebIDL type: [PxRigidDynamic] (Const, Ref)
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun CloneDynamic(physicsSDK: PxPhysics, transform: PxTransform, from: PxRigidDynamic): PxRigidDynamic

    /**
     * @param actor          WebIDL type: [PxRigidActor] (Ref)
     * @param scale          WebIDL type: float
     * @param scaleMassProps WebIDL type: boolean
     */
    fun ScaleRigidActor(actor: PxRigidActor, scale: Float, scaleMassProps: Boolean)

    /**
     * @param curTrans WebIDL type: [PxTransform] (Const, Ref)
     * @param linvel   WebIDL type: [PxVec3] (Const, Ref)
     * @param angvel   WebIDL type: [PxVec3] (Const, Ref)
     * @param timeStep WebIDL type: float
     * @param result   WebIDL type: [PxTransform] (Ref)
     */
    fun IntegrateTransform(curTrans: PxTransform, linvel: PxVec3, angvel: PxVec3, timeStep: Float, result: PxTransform)

}

fun PxTopLevelFunctionsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxTopLevelFunctions = js("_module.wrapPointer(ptr, _module.PxTopLevelFunctions)")

