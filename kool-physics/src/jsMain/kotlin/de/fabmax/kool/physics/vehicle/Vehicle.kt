package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.CollisionGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import physx.*
import kotlin.math.PI

actual class Vehicle actual constructor(vehicleProps: VehicleProperties, private val world: PhysicsWorld): CommonVehicle() {

    val vehicle: PxVehicleDrive4W
    private val vehActor: PxRigidDynamic

    private val vehicleAsVector: Vector_PxVehicleWheels
    private val wheelQueryResults: Vector_PxWheelQueryResult
    private val vehicleWheelQueryResult: PxVehicleWheelQueryResult

    val queryData: VehicleSceneQueryData
    val query: PxBatchQuery
    val frictionPairs: FrictionPairs

    actual val chassisTransform = Mat4f()
    actual val wheelTransforms = List(4) { Mat4f() }

    private var chassisVelocity = MutableVec3f()
    actual val velocity: Vec3f
        get() = chassisVelocity

    private var fwdSpeed = 0f
    actual val forwardSpeed: Float
        get() = fwdSpeed

    actual var isReverse = false

    init {
        Physics.checkIsLoaded()

        queryData = VehicleSceneQueryData(1, 4, 1, 1)
        query = queryData.setupBatchedSceneQuery(world.scene)
        //frictionPairs = FrictionPairs(1, listOf(PhysX.physics.createMaterial(0.25f, 0.25f, 0.25f)))

        val gndMaterials = if (vehicleProps.groundMaterialFrictions.isEmpty()) {
            listOf(Material(0.5f).pxMaterial)
        } else {
            vehicleProps.groundMaterialFrictions.keys.map { it.pxMaterial }.toList()
        }
        frictionPairs = FrictionPairs(1, gndMaterials)
        vehicleProps.groundMaterialFrictions.forEach { (mat, friction) ->
            frictionPairs.setTypePairFriction(mat.pxMaterial, 0, friction)
        }

        vehicle = createVehicle4w(vehicleProps)
        vehicleAsVector = Vector_PxVehicleWheels()
        vehicleAsVector.push_back(vehicle)

        wheelQueryResults = Vector_PxWheelQueryResult(vehicleProps.numWheels)
        vehicleWheelQueryResult = PxVehicleWheelQueryResult()
        vehicleWheelQueryResult.nbWheelQueryResults = wheelQueryResults.size()
        vehicleWheelQueryResult.wheelQueryResults = wheelQueryResults.data()

        vehActor = vehicle.getRigidDynamicActor()
        val startTransform = PxTransform()
        startTransform.p.set(Vec3f(0f, 3f, 0f))
        startTransform.toMat4f(chassisTransform)
        vehActor.setGlobalPose(startTransform)

        vehicle.setToRestState()
        vehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eFIRST)
        vehicle.mDriveDynData.mUseAutoGears = true
    }

    override fun fixedUpdate(timeStep: Float) {
        if (isReverse && vehicle.mDriveDynData.mTargetGear != PxVehicleGearEnum.eREVERSE) {
            vehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eREVERSE)
        } else if (!isReverse && vehicle.mDriveDynData.mTargetGear == PxVehicleGearEnum.eREVERSE) {
            vehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eFIRST)
        }

        Physics.PxVehicle.PxVehicleSuspensionRaycasts(query, vehicleAsVector, queryData.numQueriesPerBatch, queryData.raycastResults.data())
        Physics.PxVehicle.PxVehicleUpdates(timeStep, world.scene.getGravity(), frictionPairs.frictionPairs, vehicleAsVector, vehicleWheelQueryResult)

        val globalPose = vehicle.getRigidDynamicActor().getGlobalPose()
        globalPose.toMat4f(chassisTransform)
        for (i in 0 until 4) {
            wheelQueryResults.at(i).apply {
                localPose.toMat4f(wheelTransforms[i])
            }
        }

        vehActor.getLinearVelocity().toVec3f(chassisVelocity)
        fwdSpeed = vehicle.computeForwardSpeed()

        super.fixedUpdate(timeStep)
    }

    override fun setSteerInput(value: Float) {
        if (value < 0) {
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, 0f)
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, -value)
        } else {
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, 0f)
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, value)
        }
    }

    override fun setThrottleInput(value: Float) {
        vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_ACCEL, value)
    }

    override fun setBrakeInput(value: Float) {
        vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_BRAKE, value)
    }

    private fun computeWheelCenterActorOffsets(vehicleProps: VehicleProperties): List<MutableVec3f> {
        val tw = vehicleProps.trackWidth * 0.5f
        val offsets = List(4) { MutableVec3f() }
        offsets[FRONT_LEFT].set(-tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ)
        offsets[FRONT_RIGHT].set(tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelFrontZ)
        offsets[REAR_LEFT].set(-tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ)
        offsets[REAR_RIGHT].set(tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelRearZ)
        return offsets
    }

    private fun setupWheelsSimulationData(vehicleProps: VehicleProperties, wheelCenterActorOffsets: List<Vec3f>, wheelsSimData: PxVehicleWheelsSimData) {
        val numWheels = vehicleProps.numWheels
        val centerOfMass = vehicleProps.chassisCMOffset.toPxVec3(PxVec3())
        val pxWheelCenterActorOffsets = wheelCenterActorOffsets.toVector_PxVec3()

        // Set up the wheels.
        val wheels = List(numWheels) {
            val wheel = PxVehicleWheelData()
            wheel.mMass = vehicleProps.wheelMass
            wheel.mMOI = vehicleProps.wheelMOI
            wheel.mRadius = vehicleProps.wheelRadius
            wheel.mWidth = vehicleProps.wheelWidth
            wheel.mMaxBrakeTorque = vehicleProps.maxBrakeTorque
            wheel
        }
        // Enable the handbrake for the rear wheels only.
        wheels[REAR_LEFT].mMaxHandBrakeTorque = vehicleProps.maxHandBrakeTorque
        wheels[REAR_RIGHT].mMaxHandBrakeTorque = vehicleProps.maxHandBrakeTorque
        // Enable steering for the front wheels only.
        wheels[FRONT_LEFT].mMaxSteer = vehicleProps.maxSteerAngle.toRad()
        wheels[FRONT_RIGHT].mMaxSteer = vehicleProps.maxSteerAngle.toRad()

        // Set up the tires.
        val tires = List(numWheels) {
            val tire = PxVehicleTireData()
            tire.mType = FrictionPairs.TIRE_TYPE_NORMAL
            tire
        }

        // Set up the suspensions
        // Compute the mass supported by each suspension spring.
        val suspSprungMasses = Vector_PxReal(numWheels)
        Physics.PxVehicle.PxVehicleComputeSprungMasses(numWheels, pxWheelCenterActorOffsets.data(), centerOfMass, vehicleProps.chassisMass, 1, suspSprungMasses.data())
        // Set the suspension data.
        val suspensions = List(numWheels) { i ->
            val susp = PxVehicleSuspensionData()
            susp.mMaxCompression = vehicleProps.maxCompression
            susp.mMaxDroop = vehicleProps.maxDroop
            susp.mSpringStrength = vehicleProps.springStrength
            susp.mSpringDamperRate = vehicleProps.springDamperRate
            susp.mSprungMass = suspSprungMasses.at(i)
            susp
        }
        // Set the camber angles.
        for (i in 0 until numWheels step 2) {
            suspensions[i + 0].mCamberAtRest =  vehicleProps.camberAngleAtRest
            suspensions[i + 1].mCamberAtRest =  -vehicleProps.camberAngleAtRest
            suspensions[i + 0].mCamberAtMaxDroop = vehicleProps.camberAngleAtMaxDroop
            suspensions[i + 1].mCamberAtMaxDroop = -vehicleProps.camberAngleAtMaxDroop
            suspensions[i + 0].mCamberAtMaxCompression = vehicleProps.camberAngleAtMaxCompression
            suspensions[i + 1].mCamberAtMaxCompression = -vehicleProps.camberAngleAtMaxCompression
        }

        // Set up the wheel geometry.
        val suspTravelDirections = mutableListOf<Vec3f>()
        val wheelCentreCMOffsets = mutableListOf<Vec3f>()
        val suspForceAppCMOffsets = mutableListOf<Vec3f>()
        val tireForceAppCMOffsets = mutableListOf<Vec3f>()
        // Set the geometry data.
        for (i in 0 until numWheels) {
            // Vertical suspension travel.
            suspTravelDirections += MutableVec3f(Vec3f.NEG_Y_AXIS)
            // Wheel center offset is offset from rigid body center of mass.
            val cmOffset = wheelCenterActorOffsets[i].subtract(vehicleProps.chassisCMOffset, MutableVec3f())
            wheelCentreCMOffsets += cmOffset
            // Suspension force application point 0.3 metres below rigid body center of mass.
            suspForceAppCMOffsets += MutableVec3f(cmOffset.x, -0.3f, cmOffset.z)
            // Tire force application point 0.3 metres below rigid body center of mass.
            tireForceAppCMOffsets += MutableVec3f(cmOffset.x, -0.3f, cmOffset.z)
        }

        // Set up the filter data of the raycast that will be issued by each suspension.
        val qryFilterData = PxFilterData()
        qryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE

        // Set the wheel, tire and suspension data.
        // Set the geometry data.
        // Set the query filter data
        val tmpVec = PxVec3()
        for (i in 0 until numWheels) {
            wheelsSimData.setWheelData(i, wheels[i])
            wheelsSimData.setTireData(i, tires[i])
            wheelsSimData.setSuspensionData(i, suspensions[i])
            wheelsSimData.setSuspTravelDirection(i, suspTravelDirections[i].toPxVec3(tmpVec))
            wheelsSimData.setWheelCentreOffset(i, wheelCentreCMOffsets[i].toPxVec3(tmpVec))
            wheelsSimData.setSuspForceAppPointOffset(i, suspForceAppCMOffsets[i].toPxVec3(tmpVec))
            wheelsSimData.setTireForceAppPointOffset(i, tireForceAppCMOffsets[i].toPxVec3(tmpVec))
            wheelsSimData.setSceneQueryFilterData(i, qryFilterData)
            wheelsSimData.setWheelShapeMapping(i, i)
        }

        // Add a front and rear anti-roll bar
        if (vehicleProps.frontAntiRollBarStiffness > 0f) {
            val barFront = PxVehicleAntiRollBarData()
            barFront.mWheel0 = FRONT_LEFT
            barFront.mWheel1 = FRONT_RIGHT
            barFront.mStiffness = vehicleProps.frontAntiRollBarStiffness
            wheelsSimData.addAntiRollBarData(barFront)
        }
        if (vehicleProps.rearAntiRollBarStiffness > 0f) {
            val barRear = PxVehicleAntiRollBarData()
            barRear.mWheel0 = REAR_LEFT
            barRear.mWheel1 = REAR_RIGHT
            barRear.mStiffness = vehicleProps.rearAntiRollBarStiffness
            wheelsSimData.addAntiRollBarData(barRear)
        }

        wheels.forEach { PhysxJsLoader.destroy(it) }
        tires.forEach { PhysxJsLoader.destroy(it) }
        suspensions.forEach { PhysxJsLoader.destroy(it) }
        PhysxJsLoader.destroy(suspSprungMasses)
        PhysxJsLoader.destroy(qryFilterData)
        PhysxJsLoader.destroy(tmpVec)
        PhysxJsLoader.destroy(centerOfMass)
        PhysxJsLoader.destroy(pxWheelCenterActorOffsets)
    }

    private fun createVehicle4w(vehicleProps: VehicleProperties): PxVehicleDrive4W {
        // Construct a physx actor with shapes for the chassis and wheels.
        // Set the rigid body mass, moment of inertia, and center of mass offset.

        val chassisMaterial = vehicleProps.chassisMaterial.pxMaterial
        val wheelMaterial = vehicleProps.wheelMaterial.pxMaterial
        val chassisSimFilterData = PxFilterData(vehicleProps.chassisSimFilterData)
        val wheelSimFilterData = PxFilterData(vehicleProps.wheelSimFilterData)

        // Construct a convex mesh for a cylindrical wheel.
        val wheelMesh = CylinderGeometry(vehicleProps.wheelWidth, vehicleProps.wheelRadius)
        // Assume all wheels are identical for simplicity.
        val wheelConvexMeshes = List(4) { wheelMesh.pxMesh }

        // Chassis just has a single convex shape for simplicity.
        //val chassisConvexMeshes = listOf(createChassisConvexMesh(vehicleProps.chassisDims))
        val chassisShapes = if (vehicleProps.chassisShapes.isEmpty()) {
            listOf(BoxGeometry(vehicleProps.chassisDims) to Mat4f())
        } else {
            vehicleProps.chassisShapes
        }

        val tmpVec = PxVec3()
        val rigidBodyData = PxVehicleChassisData()
        rigidBodyData.mMOI = vehicleProps.chassisMOI.toPxVec3(tmpVec)
        rigidBodyData.mMass = vehicleProps.chassisMass
        rigidBodyData.mCMOffset = vehicleProps.chassisCMOffset.toPxVec3(tmpVec)
        PhysxJsLoader.destroy(tmpVec)

        val veh4WActor = createVehicleActor(rigidBodyData,
            wheelMaterial, wheelConvexMeshes, wheelSimFilterData,
            chassisMaterial, chassisShapes, chassisSimFilterData)

        // Set up the sim data for the wheels.
        val wheelsSimData = PxVehicleWheelsSimData_allocate(vehicleProps.numWheels)
        // Compute the wheel center offsets from the origin.
        val wheelOffsets = computeWheelCenterActorOffsets(vehicleProps)

        // Set up the simulation data for all wheels.
        setupWheelsSimulationData(vehicleProps, wheelOffsets, wheelsSimData)

        // Set up the sim data for the vehicle drive model.
        val driveSimData = PxVehicleDriveSimData4W()
        // Diff
        driveSimData.getDiffData().apply {
            mType = PxVehicleDifferential4WDataEnum.eDIFF_TYPE_LS_4WD
        }
        // Engine
        driveSimData.getEngineData().apply {
            mPeakTorque = vehicleProps.peakEngineTorque
            mMaxOmega = (vehicleProps.peakEngineRpm / 60f) * 2f * PI.toFloat()
        }
        // Gears
        driveSimData.getGearsData().apply {
            mSwitchTime = vehicleProps.gearSwitchTime
        }
        // Clutch
        driveSimData.getClutchData().apply {
            mStrength = vehicleProps.clutchStrength
        }
        // Ackermann steer accuracy
        driveSimData.getAckermannGeometryData().apply {
            mAccuracy = 1f
            mAxleSeparation = wheelsSimData.getWheelCentreOffset(FRONT_LEFT).z -
                    wheelsSimData.getWheelCentreOffset(REAR_LEFT).z
            mFrontWidth = wheelsSimData.getWheelCentreOffset(FRONT_RIGHT).x -
                    wheelsSimData.getWheelCentreOffset(FRONT_LEFT).x
            mRearWidth = wheelsSimData.getWheelCentreOffset(REAR_RIGHT).x -
                    wheelsSimData.getWheelCentreOffset(REAR_LEFT).x
        }

        // Create a vehicle from the wheels and drive sim data.
        val vehDrive4W = PxVehicleDrive4W_allocate(vehicleProps.numWheels)
        vehDrive4W.setup(Physics.physics, veh4WActor, wheelsSimData, driveSimData, 0)

        // Configure the userdata
        //configureUserData(vehDrive4W, vehicle4WDesc.actorUserData, vehicle4WDesc.shapeUserDatas);

        // Free the sim data because we don't need that any more.
        wheelsSimData.free()
        PhysxJsLoader.destroy(chassisSimFilterData)
        PhysxJsLoader.destroy(wheelSimFilterData)

        return vehDrive4W
    }

    private fun createVehicleActor(chassisData: PxVehicleChassisData,
                                   wheelMaterial: PxMaterial, wheelConvexMeshes: List<PxConvexMesh>, wheelSimFilterData: PxFilterData,
                                   chassisMaterial: PxMaterial, chassisShapes: List<Pair<CollisionGeometry, Mat4f>>, chassisSimFilterData: PxFilterData
    ): PxRigidDynamic {

        // We need a rigid body actor for the vehicle.
        // Don't forget to add the actor to the scene after setting up the associated vehicle.
        val vehActor = Physics.physics.createRigidDynamic(PxTransform())

        // Wheel and chassis query filter data.
        // Optional: cars don't drive on other cars.
        val wheelQryFilterData = PxFilterData()
        wheelQryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE
        val chassisQryFilterData = PxFilterData()
        chassisQryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE

        // Add all the wheel shapes to the actor.
        wheelConvexMeshes.forEach { mesh ->
            val geom = PxConvexMeshGeometry(mesh)
            val wheelShape = Physics.physics.createShape(geom, wheelMaterial, true, Physics.defaultBodyFlags)
            wheelShape.setQueryFilterData(wheelQryFilterData)
            wheelShape.setSimulationFilterData(wheelSimFilterData)
            vehActor.attachShape(wheelShape)
        }

        // Add the chassis shapes to the actor.
        chassisShapes.forEach { (geom, pose) ->
            val chassisShape = Physics.physics.createShape(geom.pxGeometry, chassisMaterial, true)
            chassisShape.setLocalPose(pose.toPxTransform(chassisShape.getLocalPose()))
            chassisShape.setQueryFilterData(chassisQryFilterData)
            chassisShape.setSimulationFilterData(chassisSimFilterData)
            vehActor.attachShape(chassisShape)
        }

        PhysxJsLoader.destroy(wheelQryFilterData)
        PhysxJsLoader.destroy(chassisQryFilterData)

        vehActor.setMass(chassisData.mMass)
        vehActor.setMassSpaceInertiaTensor(chassisData.mMOI)
        vehActor.setCMassLocalPose(PxTransform().apply { p = chassisData.mCMOffset })

        return vehActor
    }
}