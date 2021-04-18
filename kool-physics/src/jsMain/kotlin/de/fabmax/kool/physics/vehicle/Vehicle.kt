package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Mat4f
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.toRad
import de.fabmax.kool.physics.*
import physx.*
import kotlin.math.max

actual class Vehicle actual constructor(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f/*,
                                        updater: (Vehicle, PhysicsWorld) -> VehicleUpdater*/)
    : CommonVehicle(vehicleProps, pose) {

    val pxVehicle: PxVehicleDrive4W

    private val vehicleAsVector: Vector_PxVehicleWheels

    override var steerInput = 0f
        set(value) {
            field = value
            if (value < 0) {
                pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, 0f)
                pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, -value)
            } else {
                pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_LEFT, 0f)
                pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_STEER_RIGHT, value)
            }
        }
    override var throttleInput = 0f
        set(value) {
            field = value
            pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_ACCEL, value)
        }
    override var brakeInput = 0f
        set(value) {
            field = value
            pxVehicle.mDriveDynData.setAnalogInput(PxVehicleDrive4WControlEnum.eANALOG_INPUT_BRAKE, value)
        }

    actual val updater: VehicleUpdater

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
        for (i in 0..3) {
            mutWheelInfos += WheelInfo()
        }

        setupVehicleActor(vehicleProps)
        pxVehicle = createVehicle4w(vehicleProps)
        vehicleAsVector = Vector_PxVehicleWheels()
        vehicleAsVector.push_back(pxVehicle)

        pxVehicle.setToRestState()
        pxVehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eFIRST)
        pxVehicle.mDriveDynData.mUseAutoGears = true

        updater = vehicleProps.updater(this, world)
    }

    override fun release() {
        pxVehicle.release()
        vehicleAsVector.destroy()
        super.release()
    }

    override fun fixedUpdate(timeStep: Float) {
        if (isReverse && pxVehicle.mDriveDynData.mTargetGear != PxVehicleGearEnum.eREVERSE) {
            pxVehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eREVERSE)
        } else if (!isReverse && pxVehicle.mDriveDynData.mTargetGear == PxVehicleGearEnum.eREVERSE) {
            pxVehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eFIRST)
        }

        updater.updateVehicle(this, timeStep)

        val engineSpdOmega = pxVehicle.mDriveDynData.engineRotationSpeed
        engineSpd = max(750f, engineSpdOmega * OMEGA_TO_RPM)
        engineTq = pxVehicle.mDriveSimData.engineData.mTorqueCurve.getYVal(engineSpdOmega) * peakTorque * throttleInput
        engineP = engineTq * engineSpdOmega
        curGear = pxVehicle.mDriveDynData.currentGear - PxVehicleGearEnum.eNEUTRAL

        prevLinearSpeed.set(linearSpeed)
        linearSpeed.z = pxVehicle.computeForwardSpeed()
        linearSpeed.x = pxVehicle.computeSidewaysSpeed()
        linearAccel.z = linearAccel.z * 0.5f + (linearSpeed.z - prevLinearSpeed.z) / timeStep * 0.5f
        linearAccel.x = linearAccel.x * 0.5f + (linearSpeed.x - prevLinearSpeed.x) / timeStep * 0.5f

        super.fixedUpdate(timeStep)
    }

    private fun computeWheelCenterActorOffsets(vehicleProps: VehicleProperties): List<MutableVec3f> {
        val tw = vehicleProps.trackWidth * 0.5f
        val offsets = List(4) { MutableVec3f() }
        offsets[FRONT_LEFT].set(tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosFront)
        offsets[FRONT_RIGHT].set(-tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosFront)
        offsets[REAR_LEFT].set(tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosRear)
        offsets[REAR_RIGHT].set(-tw, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosRear)
        return offsets
    }

    private fun setupVehicleActor(vehicleProps: VehicleProperties) {
        MemoryStack.stackPush().use { mem ->
            // Chassis just has a single convex shape for simplicity.
            val chassisShapes = if (vehicleProps.chassisShapes.isEmpty()) {
                listOf(VehicleUtils.defaultChassisShape(vehicleProps.chassisDims))
            } else {
                vehicleProps.chassisShapes
            }

            val wheelShapes = if (vehicleProps.wheelShapes.size != 4) {
                listOf(
                    VehicleUtils.defaultWheelShape(vehicleProps.wheelRadiusFront, vehicleProps.wheelWidthFront),
                    VehicleUtils.defaultWheelShape(vehicleProps.wheelRadiusFront, vehicleProps.wheelWidthFront),
                    VehicleUtils.defaultWheelShape(vehicleProps.wheelRadiusRear, vehicleProps.wheelWidthRear),
                    VehicleUtils.defaultWheelShape(vehicleProps.wheelRadiusRear, vehicleProps.wheelWidthRear)
                )
            } else {
                vehicleProps.wheelShapes
            }

            inertia = vehicleProps.chassisMOI
            val cMassTransform = mem.createPxTransform()
            cMassTransform.p = vehicleProps.chassisCMOffset.toPxVec3(mem.createPxVec3())
            pxRigidDynamic.cMassLocalPose = cMassTransform

            // Add shapes to the actor. Wheel shapes must added first because first
            // four shapes are treated as wheels (based on shape index)
            for (i in 0..3) {
                attachShape(wheelShapes[i])
            }
            chassisShapes.forEach { attachShape(it) }
        }
    }

    private fun createVehicle4w(vehicleProps: VehicleProperties): PxVehicleDrive4W {
        // Set up the sim data for the wheels.
        val wheelsSimData = PxVehicleWheelsSimData_allocate(vehicleProps.numWheels)
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
            mFinalRatio = vehicleProps.gearFinalRatio
        }
        // Clutch
        driveSimData.clutchData.apply {
            mStrength = vehicleProps.clutchStrength
        }
        // Ackermann steer accuracy
        driveSimData.ackermannGeometryData.apply {
            mAccuracy = 1f
            mAxleSeparation = wheelsSimData.getWheelCentreOffset(FRONT_LEFT).z - wheelsSimData.getWheelCentreOffset(REAR_LEFT).z
            mFrontWidth = wheelsSimData.getWheelCentreOffset(FRONT_RIGHT).x - wheelsSimData.getWheelCentreOffset(FRONT_LEFT).x
            mRearWidth = wheelsSimData.getWheelCentreOffset(REAR_RIGHT).x - wheelsSimData.getWheelCentreOffset(REAR_LEFT).x
        }

        // Create a vehicle from the wheels and drive sim data.
        val vehDrive4W = PxVehicleDrive4W_allocate(vehicleProps.numWheels)
        vehDrive4W.setup(Physics.physics, pxRigidDynamic, wheelsSimData, driveSimData, 0)

        // Free the sim data because we don't need that any more.
        wheelsSimData.free()
        driveSimData.destroy()

        return vehDrive4W
    }

    private fun setupWheelsSimulationData(vehicleProps: VehicleProperties, wheelCenterActorOffsets: List<Vec3f>, wheelsSimData: PxVehicleWheelsSimData) {
        MemoryStack.stackPush().use { mem ->
            val numWheels = vehicleProps.numWheels
            val centerOfMass = vehicleProps.chassisCMOffset.toPxVec3(mem.createPxVec3())
            val pxWheelCenterActorOffsets = wheelCenterActorOffsets.toVector_PxVec3()

            // Set up the wheels.
            val wheels = List(numWheels) { i ->
                val isFront = i < 2
                val wheel = mem.createPxVehicleWheelData()
                wheel.mMass = if (isFront) vehicleProps.wheelMassFront else vehicleProps.wheelMassRear
                wheel.mMOI = if (isFront) vehicleProps.wheelMoiFront else vehicleProps.wheelMoiRear
                wheel.mRadius = if (isFront) vehicleProps.wheelRadiusFront else vehicleProps.wheelRadiusRear
                wheel.mWidth = if (isFront) vehicleProps.wheelWidthFront else vehicleProps.wheelWidthRear
                wheel.mMaxBrakeTorque = if (i == FRONT_LEFT || i == FRONT_RIGHT) vehicleProps.maxBrakeTorqueFront else vehicleProps.maxBrakeTorqueRear
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
                val tire = mem.createPxVehicleTireData()
                tire.mType = FrictionPairs.TIRE_TYPE_NORMAL
                tire
            }

            // Set up the suspensions
            // Compute the mass supported by each suspension spring.
            val suspSprungMasses = Vector_PxReal(numWheels)
            val suspSprungMassesRealPtr = Physics.TypeHelpers.voidToRealPtr(suspSprungMasses.data())
            Physics.PxVehicle.PxVehicleComputeSprungMasses(numWheels, pxWheelCenterActorOffsets.data(),
                centerOfMass, vehicleProps.chassisMass, 1, suspSprungMassesRealPtr)
            // Set the suspension data.
            val suspensions = List(numWheels) { i ->
                mem.createPxVehicleSuspensionData().apply {
                    mMaxCompression = vehicleProps.maxCompression
                    mMaxDroop = vehicleProps.maxDroop
                    mSpringStrength = vehicleProps.springStrength
                    mSpringDamperRate = vehicleProps.springDamperRate
                    mSprungMass = suspSprungMasses.at(i)

                    val camberSign = if (i % 2 == 1) -1f else 1f
                    mCamberAtRest = vehicleProps.camberAngleAtRest * camberSign
                    mCamberAtMaxDroop = vehicleProps.camberAngleAtMaxDroop * camberSign
                    mCamberAtMaxCompression = vehicleProps.camberAngleAtMaxCompression * camberSign
                }
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
            val qryFilterData = mem.createPxFilterData(0, 0, 0, VehicleUtils.SURFACE_FLAG_NON_DRIVABLE)

            // Set the wheel, tire and suspension data, the geometry data and the query filter data
            val tmpVec = mem.createPxVec3()
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
                val barFront = mem.createPxVehicleAntiRollBarData()
                barFront.mWheel0 = FRONT_LEFT
                barFront.mWheel1 = FRONT_RIGHT
                barFront.mStiffness = vehicleProps.frontAntiRollBarStiffness
                wheelsSimData.addAntiRollBarData(barFront)
            }
            if (vehicleProps.rearAntiRollBarStiffness > 0f) {
                val barRear = mem.createPxVehicleAntiRollBarData()
                barRear.mWheel0 = REAR_LEFT
                barRear.mWheel1 = REAR_RIGHT
                barRear.mStiffness = vehicleProps.rearAntiRollBarStiffness
                wheelsSimData.addAntiRollBarData(barRear)
            }

            suspSprungMasses.destroy()
            pxWheelCenterActorOffsets.destroy()
        }
    }
}