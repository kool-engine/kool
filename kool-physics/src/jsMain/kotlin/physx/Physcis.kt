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

    fun setLinearDamping(linDamp: Float)
    fun getLinearDamping(): Float
    fun setAngularDamping(angDamp: Float)
    fun getAngularDamping(): Float
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

@Suppress("UnsafeCastFromDynamic")
object PxSceneFlag {
    val eENABLE_ACTIVE_ACTORS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_ACTIVE_ACTORS()
    val eENABLE_CCD: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_CCD()
    val eDISABLE_CCD_RESWEEP: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CCD_RESWEEP()
    val eADAPTIVE_FORCE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eADAPTIVE_FORCE()
    val eENABLE_PCM: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_PCM()
    val eDISABLE_CONTACT_REPORT_BUFFER_RESIZE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CONTACT_REPORT_BUFFER_RESIZE()
    val eDISABLE_CONTACT_CACHE: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eDISABLE_CONTACT_CACHE()
    val eREQUIRE_RW_LOCK: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eREQUIRE_RW_LOCK()
    val eENABLE_STABILIZATION: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_STABILIZATION()
    val eENABLE_AVERAGE_POINT: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_AVERAGE_POINT()
    val eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eEXCLUDE_KINEMATICS_FROM_ACTIVE_ACTORS()
    val eENABLE_GPU_DYNAMICS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_GPU_DYNAMICS()
    val eENABLE_ENHANCED_DETERMINISM: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_ENHANCED_DETERMINISM()
    val eENABLE_FRICTION_EVERY_ITERATION: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eENABLE_FRICTION_EVERY_ITERATION()
    val eMUTABLE_FLAGS: Int get() = PhysX.physx._emscripten_enum_physx_PxSceneFlag_eMUTABLE_FLAGS()
}

external interface PxSceneFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}

external interface PxShape {
    // Pose Manipulation
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform

    // Collision Filtering
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
}

@Suppress("UnsafeCastFromDynamic")
object PxShapeFlag {
    val eSIMULATION_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eSIMULATION_SHAPE()
    val eSCENE_QUERY_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eSCENE_QUERY_SHAPE()
    val eTRIGGER_SHAPE: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eTRIGGER_SHAPE()
    val eVISUALIZATION: Int get() = PhysX.physx._emscripten_enum_physx_PxShapeFlag_eVISUALIZATION()
}

external interface PxShapeFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}

external interface PxSimulationFilterShader
