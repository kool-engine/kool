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
    /*

      .function("setAngularDamping", &PxRigidBody::setAngularDamping)
      .function("getAngularDamping", &PxRigidBody::getAngularDamping)
      .function("setLinearDamping", &PxRigidBody::setLinearDamping)
      .function("getLinearDamping", &PxRigidBody::getLinearDamping)
      .function("setAngularVelocity", &PxRigidBody::setAngularVelocity)
      .function("getAngularVelocity", &PxRigidBody::getAngularVelocity)
      .function("setMass", &PxRigidBody::setMass)
      .function("getMass", &PxRigidBody::getMass)
      .function("setCMassLocalPose", &PxRigidBody::setCMassLocalPose, allow_raw_pointers())
      .function("setLinearVelocity", &PxRigidBody::setLinearVelocity)
      .function("getLinearVelocity", &PxRigidBody::getLinearVelocity)
     */
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
}

external interface PxScene {
    fun addActor(actor: PxActor, bvhStructure: PxBVHStructure?)
    fun removeActor(actor: PxActor, wakeOnLostTouch: Boolean)

    // Simulation
    fun simulate(elapsedTime: Float, controlSimulation: Boolean)
    fun fetchResults(block: Boolean): Boolean
    fun getGravity(): PxVec3
    fun setGravity(vec: PxVec3)
}

external interface PxSceneDesc

external interface PxShape {
    // Pose Manipulation
    fun setLocalPose(pose: PxTransform)
    fun getLocalPose(): PxTransform

    // Collision Filtering
    fun setSimulationFilterData(data: PxFilterData)
    fun getSimulationFilterData(): PxFilterData
}

external interface PxShapeFlag {
    val eSIMULATION_SHAPE: Enum
    val eSCENE_QUERY_SHAPE: Enum
    val eTRIGGER_SHAPE: Enum
    val eVISUALIZATION: Enum
}

external interface PxShapeFlags
