package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.PhysicsWorld
import de.fabmax.kool.physics.shapes.ConvexHullShape
import de.fabmax.kool.physics.shapes.CylinderShape
import physx.*
import kotlin.math.PI

actual class Vehicle actual constructor(private val world: PhysicsWorld): CommonVehicle() {

    val vehicle4WDesc = VehicleDesc()
    val vehicle: PxVehicleDrive4W

    private val vehicleAsVector: Vector_PxVehicleWheels
    private val wheelQueryResults: Vector_PxWheelQueryResult
    private val vehicleWheelQueryResult: PxVehicleWheelQueryResult

    val queryData: VehicleSceneQueryData
    val query: PxBatchQuery
    val frictionPairs: FrictionPairs

    actual val chassisTransform = Mat4f()
    actual val wheelTransforms = List(4) { Mat4f() }

    init {
        Physics.checkIsLoaded()

        queryData = VehicleSceneQueryData(1, 4, 1, 1)
        query = queryData.setupBatchedSceneQuery(world.scene)
        frictionPairs = FrictionPairs(1, listOf(PhysX.physics.createMaterial(0.25f, 0.25f, 0.25f)))

        vehicle = createVehicle4w(vehicle4WDesc)
        vehicleAsVector = PhysX.Vector_PxVehicleWheels()
        vehicleAsVector.push_back(vehicle)

        wheelQueryResults = PhysX.Vector_PxWheelQueryResult(vehicle4WDesc.numWheels)
        vehicleWheelQueryResult = PhysX.PxVehicleWheelQueryResult()
        vehicleWheelQueryResult.nbWheelQueryResults = wheelQueryResults.size()
        vehicleWheelQueryResult.wheelQueryResults = wheelQueryResults.data()

        val vehActor = vehicle.getRigidDynamicActor()
        val startTransform = PhysX.PxTransform()
        startTransform.p.set(Vec3f(0f, 3f, 0f))
        startTransform.toMat4f(chassisTransform)
        vehActor.setGlobalPose(startTransform)

        vehicle.setToRestState()
        vehicle.mDriveDynData.forceGearChange(physx_PxVehicleGear.eFIRST)
        vehicle.mDriveDynData.mUseAutoGears = true
    }

    override fun fixedUpdate(timeStep: Float) {
        PhysX.PxVehicle.PxVehicleSuspensionRaycasts(query, vehicleAsVector, queryData.numQueriesPerBatch, queryData.raycastResults.data())
        PhysX.PxVehicle.PxVehicleUpdates(timeStep, world.scene.getGravity(), frictionPairs.frictionPairs, vehicleAsVector, vehicleWheelQueryResult)

        val globalPose = vehicle.getRigidDynamicActor().getGlobalPose()
        globalPose.toMat4f(chassisTransform)
        for (i in 0 until 4) {
            wheelQueryResults.at(i).apply {
                localPose.toMat4f(wheelTransforms[i])
            }
        }

        super.fixedUpdate(timeStep)
    }

    override fun setSteerAngle(wheelIndex: Int, value: Float) {
    }

    override fun setEngineForce(wheelIndex: Int, value: Float) {
    }

    override fun setBrake(wheelIndex: Int, value: Float) {
    }

    override fun setSteerAngle(value: Float) {
        if (value < 0) {
            vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_STEER_RIGHT, 0f)
            vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_STEER_LEFT, -value)
        } else {
            vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_STEER_LEFT, 0f)
            vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_STEER_RIGHT, value)
        }
    }

    override fun setEngineForce(value: Float) {
        vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_ACCEL, value)
    }

    override fun setBrake(value: Float) {
        vehicle.mDriveDynData.setAnalogInput(physx_PxVehicleDrive4WControl.eANALOG_INPUT_BRAKE, value)
    }

    actual fun updateWheelTransform(wheelIndex: Int, result: Mat4f): Mat4f {
        return result
    }

    private fun createChassisConvexMesh(dimension: Vec3f): PxConvexMesh {
        val hx = dimension.x * 0.5f
        val hy = dimension.y * 0.5f
        val hz = dimension.z * 0.5f
        return ConvexHullShape.toConvexMesh(listOf(
            Vec3f(-hx, -hy, -hz), Vec3f(hx, -hy, -hz),
            Vec3f(-hx, -hy,  hz), Vec3f(hx, -hy,  hz),
            Vec3f(-hx,  hy, -hz), Vec3f(hx,  hy, -hz),
            Vec3f(-hx,  hy,  hz), Vec3f(hx,  hy,  hz)
        ))
    }

    private fun computeWheelCenterActorOffsets(wheelFrontZ: Float, wheelRearZ: Float, vehicleDesc: VehicleDesc): List<MutableVec3f> {
        // chassisDims.z is the distance from the rear of the chassis to the front of the chassis.
        // The front has z = 0.5*chassisDims.z and the rear has z = -0.5*chassisDims.z.
        // Compute a position for the front wheel and the rear wheel along the z-axis.
        // Compute the separation between each wheel along the z-axis.

        val dimX = vehicleDesc.chassisDims.x
        val dimY = vehicleDesc.chassisDims.y
        //val wheelR = vehicleDesc.wheelRadius
        val wheelW = vehicleDesc.wheelWidth + 0.1f

        val offsets = MutableList(4) { MutableVec3f() }
        offsets[REAR_LEFT].set((-dimX - wheelW) * 0.5f, -dimY / 2, wheelRearZ)
        offsets[REAR_RIGHT].set((dimX + wheelW) * 0.5f, -dimY / 2, wheelRearZ)
        offsets[FRONT_LEFT].set((-dimX - wheelW) * 0.5f, -dimY / 2, wheelFrontZ)
        offsets[FRONT_RIGHT].set((dimX + wheelW) * 0.5f, -dimY / 2, wheelFrontZ)

        return offsets
    }

    private fun setupWheelsSimulationData(vehicleDesc: VehicleDesc, wheelCenterActorOffsets: List<Vec3f>, wheelsSimData: PxVehicleWheelsSimData) {
        val numWheels = vehicleDesc.numWheels
        val centerOfMass = vehicleDesc.chassisCMOffset.toPxVec3()
        val pxWheelCenterActorOffsets = wheelCenterActorOffsets.toVector_PxVec3()

        // Set up the wheels.
        val wheels = List(numWheels) {
            val wheel = PhysX.PxVehicleWheelData()
            wheel.mMass = vehicleDesc.wheelMass
            wheel.mMOI = vehicleDesc.wheelMOI
            wheel.mRadius = vehicleDesc.wheelRadius
            wheel.mWidth = vehicleDesc.wheelWidth
            wheel.mMaxBrakeTorque = 4000f
            wheel
        }
        // Enable the handbrake for the rear wheels only.
        wheels[REAR_LEFT].mMaxHandBrakeTorque = 4000f
        wheels[REAR_RIGHT].mMaxHandBrakeTorque = 4000f
        // Enable steering for the front wheels only.
        wheels[FRONT_LEFT].mMaxSteer = PI.toFloat() * 0.25f
        wheels[FRONT_RIGHT].mMaxSteer = PI.toFloat() * 0.25f

        // Set up the tires.
        val tires = List(numWheels) {
            val tire = PhysX.PxVehicleTireData()
            tire.mType = FrictionPairs.TIRE_TYPE_NORMAL
            tire
        }

        // Set up the suspensions
        // Compute the mass supported by each suspension spring.
        val suspSprungMasses = PhysX.Vector_PxReal(numWheels)
        PhysX.PxVehicle.PxVehicleComputeSprungMasses(numWheels, pxWheelCenterActorOffsets.data(), centerOfMass, vehicleDesc.chassisMass, 1, suspSprungMasses.data())
        // Set the suspension data.
        val suspensions = List(numWheels) { i ->
            val susp = PhysX.PxVehicleSuspensionData()
            susp.mMaxCompression = 0.3f
            susp.mMaxDroop = 0.1f
            susp.mSpringStrength = 35000f
            susp.mSpringDamperRate = 4500f
            susp.mSprungMass = suspSprungMasses.at(i)
            susp
        }
        // Set the camber angles.
        val camberAngleAtRest = 0.0f
        val camberAngleAtMaxDroop = 0.05f
        val camberAngleAtMaxCompression = -0.05f
        for (i in 0 until numWheels step 2) {
            suspensions[i + 0].mCamberAtRest =  camberAngleAtRest
            suspensions[i + 1].mCamberAtRest =  -camberAngleAtRest
            suspensions[i + 0].mCamberAtMaxDroop = camberAngleAtMaxDroop
            suspensions[i + 1].mCamberAtMaxDroop = -camberAngleAtMaxDroop
            suspensions[i + 0].mCamberAtMaxCompression = camberAngleAtMaxCompression
            suspensions[i + 1].mCamberAtMaxCompression = -camberAngleAtMaxCompression
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
            val cmOffset = wheelCenterActorOffsets[i].subtract(vehicleDesc.chassisCMOffset, MutableVec3f())
            wheelCentreCMOffsets += cmOffset
            // Suspension force application point 0.3 metres below rigid body center of mass.
            suspForceAppCMOffsets += MutableVec3f(cmOffset.x, -0.3f, cmOffset.z)
            // Tire force application point 0.3 metres below rigid body center of mass.
            tireForceAppCMOffsets += MutableVec3f(cmOffset.x, -0.3f, cmOffset.z)
        }

        // Set up the filter data of the raycast that will be issued by each suspension.
        val qryFilterData = PhysX.PxFilterData()
        qryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE

        // Set the wheel, tire and suspension data.
        // Set the geometry data.
        // Set the query filter data
        for (i in 0 until numWheels) {
            wheelsSimData.setWheelData(i, wheels[i])
            wheelsSimData.setTireData(i, tires[i])
            wheelsSimData.setSuspensionData(i, suspensions[i])
            wheelsSimData.setSuspTravelDirection(i, suspTravelDirections[i].toPxVec3())
            wheelsSimData.setWheelCentreOffset(i, wheelCentreCMOffsets[i].toPxVec3())
            wheelsSimData.setSuspForceAppPointOffset(i, suspForceAppCMOffsets[i].toPxVec3())
            wheelsSimData.setTireForceAppPointOffset(i, tireForceAppCMOffsets[i].toPxVec3())
            wheelsSimData.setSceneQueryFilterData(i, qryFilterData)
            wheelsSimData.setWheelShapeMapping(i, i)
        }

        // Add a front and rear anti-roll bar
        val barFront = PhysX.PxVehicleAntiRollBarData()
        barFront.mWheel0 = FRONT_LEFT
        barFront.mWheel1 = FRONT_RIGHT
        barFront.mStiffness = 10000.0f
        wheelsSimData.addAntiRollBarData(barFront)
        val barRear = PhysX.PxVehicleAntiRollBarData()
        barRear.mWheel0 = REAR_LEFT
        barRear.mWheel1 = REAR_RIGHT
        barRear.mStiffness = 10000.0f
        wheelsSimData.addAntiRollBarData(barRear)
    }

    private fun createVehicle4w(vehicle4WDesc: VehicleDesc): PxVehicleDrive4W {
        // Construct a physx actor with shapes for the chassis and wheels.
        // Set the rigid body mass, moment of inertia, and center of mass offset.

        // Construct a convex mesh for a cylindrical wheel.
        val wheelMesh = CylinderShape(vehicle4WDesc.wheelWidth, vehicle4WDesc.wheelRadius)
        // Assume all wheels are identical for simplicity.
        val wheelConvexMeshes = List(4) { wheelMesh.pxMesh }
        val wheelMaterials = List(4) { vehicle4WDesc.wheelMaterial }

        // Chassis just has a single convex shape for simplicity.
        val chassisConvexMeshes = listOf(createChassisConvexMesh(vehicle4WDesc.chassisDims))
        val chassisMaterials = listOf(vehicle4WDesc.chassisMaterial)

        val rigidBodyData = PhysX.PxVehicleChassisData()
        rigidBodyData.mMOI = vehicle4WDesc.chassisMOI.toPxVec3()
        rigidBodyData.mMass = vehicle4WDesc.chassisMass
        rigidBodyData.mCMOffset = vehicle4WDesc.chassisCMOffset.toPxVec3()

        val veh4WActor = createVehicleActor(rigidBodyData,
            wheelMaterials, wheelConvexMeshes, vehicle4WDesc.wheelSimFilterData,
            chassisMaterials, chassisConvexMeshes, vehicle4WDesc.chassisSimFilterData)

        // Set up the sim data for the wheels.
        val wheelsSimData = PhysX.PxVehicleWheelsSimData_allocate(vehicle4WDesc.numWheels)
        // Compute the wheel center offsets from the origin.
        val frontZ = vehicle4WDesc.chassisDims.z * 0.3f
        val rearZ = vehicle4WDesc.chassisDims.z * -0.3f
        val wheelOffsets = computeWheelCenterActorOffsets(frontZ, rearZ, vehicle4WDesc)

        // Set up the simulation data for all wheels.
        setupWheelsSimulationData(vehicle4WDesc, wheelOffsets, wheelsSimData)

        // Set up the sim data for the vehicle drive model.
        val driveSimData = PhysX.PxVehicleDriveSimData4W()
        // Diff
        driveSimData.getDiffData().apply {
            mType = physx_PxVehicleDifferential4WData.eDIFF_TYPE_LS_4WD
        }
        // Engine
        driveSimData.getEngineData().apply {
            mPeakTorque = 1000f
            mMaxOmega = 600f     // approx 6000 rpm
        }
        // Gears
        driveSimData.getGearsData().apply {
            mSwitchTime = 0.5f
        }
        // Clutch
        driveSimData.getClutchData().apply {
            mStrength = 10f
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
        val vehDrive4W = PhysX.PxVehicleDrive4W_allocate(vehicle4WDesc.numWheels)
        vehDrive4W.setup(PhysX.physics, veh4WActor, wheelsSimData, driveSimData, 0)

        // Configure the userdata
        //configureUserData(vehDrive4W, vehicle4WDesc.actorUserData, vehicle4WDesc.shapeUserDatas);

        // Free the sim data because we don't need that any more.
        wheelsSimData.free()

        return vehDrive4W
    }

    private fun createVehicleActor(chassisData: PxVehicleChassisData,
                                   wheelMaterials: List<PxMaterial>, wheelConvexMeshes: List<PxConvexMesh>, wheelSimFilterData: PxFilterData,
                                   chassisMaterials: List<PxMaterial>, chassisConvexMeshes: List<PxConvexMesh>, chassisSimFilterData: PxFilterData
    ): PxRigidDynamic {

        // We need a rigid body actor for the vehicle.
        // Don't forget to add the actor to the scene after setting up the associated vehicle.
        val vehActor = PhysX.physics.createRigidDynamic(PhysX.PxTransform())

        // Wheel and chassis query filter data.
        // Optional: cars don't drive on other cars.
        val wheelQryFilterData = PhysX.PxFilterData()
        wheelQryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE
        val chassisQryFilterData = PhysX.PxFilterData()
        chassisQryFilterData.word3 = VehicleUtils.SURFACE_FLAG_NON_DRIVABLE

        // Add all the wheel shapes to the actor.
        wheelConvexMeshes.forEachIndexed { i, mesh ->
            val geom = PhysX.PxConvexMeshGeometry(mesh)
            val wheelShape = PhysX.physics.createShape(geom, wheelMaterials[i], true, PhysX.defaultBodyFlags)
            wheelShape.setQueryFilterData(wheelQryFilterData)
            wheelShape.setSimulationFilterData(wheelSimFilterData)
            vehActor.attachShape(wheelShape)
        }

        // Add the chassis shapes to the actor.
        chassisConvexMeshes.forEachIndexed { i, mesh ->
            val geom = PhysX.PxConvexMeshGeometry(mesh)
            val chassisShape = PhysX.physics.createShape(geom, chassisMaterials[i], true, PhysX.defaultBodyFlags)
            chassisShape.setQueryFilterData(chassisQryFilterData)
            chassisShape.setSimulationFilterData(chassisSimFilterData)
            vehActor.attachShape(chassisShape)
        }

        PhysX.destroy(wheelQryFilterData)
        PhysX.destroy(chassisQryFilterData)

        vehActor.setMass(chassisData.mMass)
        vehActor.setMassSpaceInertiaTensor(chassisData.mMOI)
        vehActor.setCMassLocalPose(PhysX.PxTransform().apply { p = chassisData.mCMOffset })

        return vehActor
    }
}