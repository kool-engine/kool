package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.geometry.BoxGeometry
import de.fabmax.kool.physics.geometry.CylinderGeometry
import physx.common.PxVec3
import physx.physics.PxBatchQuery
import physx.physics.PxFilterData
import physx.support.TypeHelpers
import physx.support.Vector_PxReal
import physx.support.Vector_PxVehicleWheels
import physx.support.Vector_PxWheelQueryResult
import physx.vehicle.*
import kotlin.math.max

actual class Vehicle actual constructor(vehicleProps: VehicleProperties, private val world: PhysicsWorld, pose: Mat4f) : CommonVehicle(vehicleProps, pose) {

    private val vehicle: PxVehicleDrive4W

    private val vehicleAsVector: Vector_PxVehicleWheels
    private val wheelQueryResults: Vector_PxWheelQueryResult
    private val vehicleWheelQueryResult: PxVehicleWheelQueryResult

    private val queryData: VehicleSceneQueryData
    private val query: PxBatchQuery
    private val frictionPairs: FrictionPairs

    actual val wheelTransforms = List(4) { Mat4f() }

    override var steerInput = 0f
        set(value) {
            field = value
            if (value < 0) {
                vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, 0f)
                vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, -value)
            } else {
                vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, 0f)
                vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, value)
            }
        }
    override var throttleInput = 0f
        set(value) {
            field = value
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_ACCEL, value)
        }
    override var brakeInput = 0f
        set(value) {
            field = value
            vehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_BRAKE, value)
        }

    private val peakTorque = vehicleProps.peakEngineTorque

    private val linearSpeed = MutableVec3f()
    private val prevLinearSpeed = MutableVec3f()
    private val linearAccel = MutableVec3f()
    private var engineSpd = 0f
    private var engineTq = 0f
    private var engineP = 0f
    private var curGear = 0

    actual val forwardSpeed: Float
        get() = linearSpeed.z
    actual val sidewaysSpeed: Float
        get() = linearSpeed.x
    actual val longitudinalAcceleration: Float
        get() = linearAccel.z
    actual val lateralAcceleration: Float
        get() = linearAccel.x
    actual val engineSpeedRpm: Float
        get() = engineSpd
    actual val engineTorqueNm: Float
        get() = engineTq
    actual val enginePowerW: Float
        get() = engineP
    actual val currentGear: Int
        get() = curGear

    actual var isReverse = false

    init {
        queryData = VehicleSceneQueryData(1, 4, 1, 1)
        query = queryData.setupBatchedSceneQuery(world.scene)

        val gndMaterials = if (vehicleProps.groundMaterialFrictions.isEmpty()) {
            listOf(Material(0.5f).pxMaterial)
        } else {
            vehicleProps.groundMaterialFrictions.keys.map { it.pxMaterial }.toList()
        }
        frictionPairs = FrictionPairs(1, gndMaterials)
        vehicleProps.groundMaterialFrictions.forEach { (mat, friction) ->
            frictionPairs.setTypePairFriction(mat.pxMaterial, 0, friction)
        }

        setupVehicleActor(vehicleProps)
        vehicle = createVehicle4w(vehicleProps)
        vehicleAsVector = Vector_PxVehicleWheels()
        vehicleAsVector.push_back(vehicle)

        wheelQueryResults = Vector_PxWheelQueryResult(vehicleProps.numWheels)
        vehicleWheelQueryResult = PxVehicleWheelQueryResult()
        vehicleWheelQueryResult.nbWheelQueryResults = wheelQueryResults.size()
        vehicleWheelQueryResult.wheelQueryResults = wheelQueryResults.data()

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

        PxVehicleTopLevelFunctions.PxVehicleSuspensionRaycasts(query, vehicleAsVector, queryData.numQueriesPerBatch, queryData.raycastResults.data())
        PxVehicleTopLevelFunctions.PxVehicleUpdates(timeStep, world.scene.gravity, frictionPairs.frictionPairs, vehicleAsVector, vehicleWheelQueryResult)

        for (i in 0 until 4) {
            wheelQueryResults.at(i).apply {
                localPose.toMat4f(wheelTransforms[i])
            }
        }

        val engioneSpdOmega = vehicle.mDriveDynData.engineRotationSpeed
        engineSpd = max(750f, engioneSpdOmega * OMEGA_TO_RPM)
        engineTq = vehicle.mDriveSimData.engineData.mTorqueCurve.getYVal(engioneSpdOmega) * peakTorque * throttleInput
        engineP = engineTq * engioneSpdOmega
        curGear = vehicle.mDriveDynData.currentGear - PxVehicleGearEnum.eNEUTRAL

        prevLinearSpeed.set(linearSpeed)
        linearSpeed.z = vehicle.computeForwardSpeed()
        linearSpeed.x = vehicle.computeSidewaysSpeed()
        linearAccel.z = linearAccel.z * 0.5f + (linearSpeed.z - prevLinearSpeed.z) / timeStep * 0.5f
        linearAccel.x = linearAccel.x * 0.5f + (linearSpeed.x - prevLinearSpeed.x) / timeStep * 0.5f

        super.fixedUpdate(timeStep)
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

    private fun setupVehicleActor(vehicleProps: VehicleProperties) {
        // Chassis just has a single convex shape for simplicity.
        val chassisShapes = if (vehicleProps.chassisShapes.isEmpty()) {
            listOf(BoxGeometry(vehicleProps.chassisDims) to Mat4f())
        } else {
            vehicleProps.chassisShapes
        }

        val tmpVec = PxVec3()
        val chassisData = PxVehicleChassisData()
        chassisData.mmoi = vehicleProps.chassisMOI.toPxVec3(tmpVec)
        chassisData.mMass = vehicleProps.chassisMass
        chassisData.mcmOffset = vehicleProps.chassisCMOffset.toPxVec3(tmpVec)
        tmpVec.destroy()

        val chassisQryFilterData = FilterData()
        VehicleUtils.setupNonDrivableSurface(chassisQryFilterData)
        val wheelQryFilterData = FilterData()
        VehicleUtils.setupNonDrivableSurface(wheelQryFilterData)

        inertia = chassisData.mmoi.toVec3f()
        val cMassTransform = PxTransform().apply { p = chassisData.mcmOffset }
        pxRigidDynamic.cMassLocalPose = cMassTransform
        cMassTransform.destroy()

        // Add wheel shapes to the actor. Must happen before chassis shapes because first
        // four shapes are treated as wheels
        val wheelMesh = CylinderGeometry(vehicleProps.wheelWidth, vehicleProps.wheelRadius)
        for(i in 0..3) {
            val shape = Shape(wheelMesh, vehicleProps.wheelMaterial,
                simFilterData = vehicleProps.wheelSimFilterData, queryFilterData = wheelQryFilterData)
            attachShape(shape)
        }

        // add chassis shapes to the actor
        chassisShapes.forEach { (geom, pose) ->
            val shape = Shape(geom, vehicleProps.chassisMaterial, pose,
                vehicleProps.chassisSimFilterData, chassisQryFilterData)
            attachShape(shape)
        }
    }

    private fun setupWheelsSimulationData(vehicleProps: VehicleProperties, wheelCenterActorOffsets: List<Vec3f>, wheelsSimData: PxVehicleWheelsSimData) {
        val numWheels = vehicleProps.numWheels
        val centerOfMass = vehicleProps.chassisCMOffset.toPxVec3(PxVec3())
        val pxWheelCenterActorOffsets = wheelCenterActorOffsets.toVector_PxVec3()

        // Set up the wheels.
        val wheels = List(numWheels) {
            val wheel = PxVehicleWheelData()
            wheel.mMass = vehicleProps.wheelMass
            wheel.mmoi = vehicleProps.wheelMOI
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
        val suspSprungMassesRealPtr = TypeHelpers.voidToRealPtr(suspSprungMasses.data())
        PxVehicleTopLevelFunctions.PxVehicleComputeSprungMasses(numWheels, pxWheelCenterActorOffsets.data(), centerOfMass, vehicleProps.chassisMass, 1, suspSprungMassesRealPtr)
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
            barFront.destroy()
        }
        if (vehicleProps.rearAntiRollBarStiffness > 0f) {
            val barRear = PxVehicleAntiRollBarData()
            barRear.mWheel0 = REAR_LEFT
            barRear.mWheel1 = REAR_RIGHT
            barRear.mStiffness = vehicleProps.rearAntiRollBarStiffness
            wheelsSimData.addAntiRollBarData(barRear)
            barRear.destroy()
        }

        wheels.forEach { it.destroy() }
        tires.forEach { it.destroy() }
        suspensions.forEach { it.destroy() }
        suspSprungMasses.destroy()
        qryFilterData.destroy()
        tmpVec.destroy()
        centerOfMass.destroy()
        pxWheelCenterActorOffsets.destroy()
    }

    private fun createVehicle4w(vehicleProps: VehicleProperties): PxVehicleDrive4W {
        // Set up the sim data for the wheels.
        val wheelsSimData = PxVehicleWheelsSimData.allocate(vehicleProps.numWheels)
        // Compute the wheel center offsets from the origin.
        val wheelOffsets = computeWheelCenterActorOffsets(vehicleProps)

        // Set up the simulation data for all wheels.
        setupWheelsSimulationData(vehicleProps, wheelOffsets, wheelsSimData)

        // Set up the sim data for the vehicle drive model.
        val driveSimData = PxVehicleDriveSimData4W()
        // Diff
        driveSimData.diffData.apply {
            mType = PxVehicleDifferential4WDataEnum.eDIFF_TYPE_LS_4WD
        }
        // Engine
        driveSimData.engineData.apply {
            mPeakTorque = vehicleProps.peakEngineTorque
            mMaxOmega = vehicleProps.peakEngineRpm / OMEGA_TO_RPM
        }
        // Gears
        driveSimData.gearsData.apply {
            mSwitchTime = vehicleProps.gearSwitchTime
        }
        // Clutch
        driveSimData.clutchData.apply {
            mStrength = vehicleProps.clutchStrength
        }
        // Ackermann steer accuracy
        driveSimData.ackermannGeometryData.apply {
            mAccuracy = 1f
            mAxleSeparation = wheelsSimData.getWheelCentreOffset(FRONT_LEFT).z -
                    wheelsSimData.getWheelCentreOffset(REAR_LEFT).z
            mFrontWidth = wheelsSimData.getWheelCentreOffset(FRONT_RIGHT).x -
                    wheelsSimData.getWheelCentreOffset(FRONT_LEFT).x
            mRearWidth = wheelsSimData.getWheelCentreOffset(REAR_RIGHT).x -
                    wheelsSimData.getWheelCentreOffset(REAR_LEFT).x
        }

        // Create a vehicle from the wheels and drive sim data.
        val vehDrive4W = PxVehicleDrive4W.allocate(vehicleProps.numWheels)
        vehDrive4W.setup(Physics.physics, pxRigidDynamic, wheelsSimData, driveSimData, 0)

        // Free the sim data because we don't need that any more.
        wheelsSimData.free()

        return vehDrive4W
    }
}