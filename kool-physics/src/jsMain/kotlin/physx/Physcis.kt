package physx

external interface PxActor

external interface PxFilterData {
    val word0: Int
    val word1: Int
    val word2: Int
    val word3: Int
}

external interface PxMaterial

external interface PxRigidActor: PxActor {
    // Global Pose Manipulation
    fun getGlobalPose(): PxTransform
    fun setGlobalPose(pose: PxTransform, autowake: Boolean)

    // Shapes
    fun attachShape(shape: PxShape): Boolean
    fun detachShape(shape: PxShape, wakeOnLostTouch: Boolean)
}

external interface PxRigidBody: PxRigidActor {
    fun getMass(): Float
    fun setMass(mass: Float)

    fun setMassSpaceInertiaTensor(inertia: PxVec3)
    fun getMassSpaceInertiaTensor(): PxVec3
}

external interface PxRigidDynamic: PxRigidBody

external interface PxRigidStatic: PxRigidActor

external interface PxPhysics {
    // Basics
    fun getFoundation(): PxFoundation
    fun getTolerancesScale(): PxTolerancesScale

    // Meshes

    // Scenes
    fun createScene(sceneDesc: PxSceneDesc): PxScene

    // Actors
    fun createRigidStatic(pose: PxTransform): PxRigidStatic
    fun createRigidDynamic(pose: PxTransform): PxRigidDynamic

    // Shapes
    fun createShape(geometry: PxGeometry, material: PxMaterial, isExclusive: Boolean, shapeFlags: PxShapeFlags): PxShape
    fun getNbShapes(): Int

    // Constraints and Articulations

    // Materials
    fun createMaterial(staticFriction: Float, dynamicFriction: Float, restitution: Float): PxMaterial

    // Deletion Listeners
    fun getPhysicsInsertionCallback(): PxPhysicsInsertionCallback
}

external interface PxScene {
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure?)
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)

    // Simulation
    fun simulate(elapsedTime: Float)
    fun fetchResults(block: Boolean): Boolean
    fun getGravity(): PxVec3
    fun setGravity(vec: PxVec3)
}

external interface PxSceneDesc {
    var gravity: PxVec3
    var filterShader: PxSimulationFilterShader
    var cpuDispatcher: PxCpuDispatcher
    var flags: PxSceneFlags
}

external interface PxSceneFlag {
    val eENABLE_ACTIVE_ACTORS: Int
    val eENABLE_CCD: Int
    val eDISABLE_CCD_RESWEEP: Int
    val eADAPTIVE_FORCE: Int
    val eENABLE_PCM: Int
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int
    val eDISABLE_CONTACT_CACHE: Int
    val eREQUIRE_RW_LOCK: Int
    val eENABLE_STABILIZATION: Int
    val eENABLE_AVERAGE_POINT: Int
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int
    val eENABLE_GPU_DYNAMICS: Int
    val eENABLE_ENHANCED_DETERMINISM: Int
    val eENABLE_FRICTION_EVERY_ITERATION: Int
    val eMUTABLE_FLAGS: Int

    fun isSet(flags: PxSceneFlags, flag: Int): Boolean
    fun set(flags: PxSceneFlags, flag: Int)
    fun clear(flags: PxSceneFlags, flag: Int)
}

external interface PxSceneFlags
fun PxSceneFlags.isSet(flag: Int) = PhysX.PxSceneFlag.isSet(this, flag)
fun PxSceneFlags.set(flag: Int) = PhysX.PxSceneFlag.set(this, flag)
fun PxSceneFlags.clear(flag: Int) = PhysX.PxSceneFlag.clear(this, flag)

external interface PxShape {
    // Pose Manipulation
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform

    // Collision Filtering
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
}

external interface PxShapeFlag {
    val eSIMULATION_SHAPE: Int
    val eSCENE_QUERY_SHAPE: Int
    val eTRIGGER_SHAPE: Int
    val eVISUALIZATION: Int

    fun isSet(flags: PxShapeFlags, flag: Int): Boolean
    fun set(flags: PxShapeFlags, flag: Int)
    fun clear(flags: PxShapeFlags, flag: Int)
}

external interface PxShapeFlags
fun PxShapeFlags.isSet(flag: Int) = PhysX.PxShapeFlag.isSet(this, flag)
fun PxShapeFlags.set(flag: Int) = PhysX.PxShapeFlag.set(this, flag)
fun PxShapeFlags.clear(flag: Int) = PhysX.PxShapeFlag.clear(this, flag)

external interface PxSimulationFilterShader
