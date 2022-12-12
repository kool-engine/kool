package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import org.lwjgl.system.MemoryStack
import physx.common.PxIDENTITYEnum
import physx.common.PxQuat
import physx.common.PxTransform
import physx.common.PxVec3
import physx.geometry.PxBoxGeometry
import physx.geometry.PxGeometry
import physx.physics.*
import physx.support.Vector_PxReal
import physx.vehicle2.*
import kotlin.math.abs

actual class Vehicle actual constructor(vehicleProps: VehicleProperties, val world: PhysicsWorld, pose: Mat4f)
    : CommonVehicle(vehicleProps) {

    //val vehicleTest = VehicleTest(world.pxScene)

    val pxVehicle: EngineDriveVehicle
//        get() = vehicleTest.vehicle
    private val actorGeometry: PxGeometry

    val vehicleSimulationContext: PxVehiclePhysXSimulationContext

    override var steerInput = 0f
        set(value) {
            field = value
            pxVehicle.commandState.steer = -value
        }
    override var throttleInput = 0f
        set(value) {
            field = value
            pxVehicle.commandState.throttle = value
        }
    override var brakeInput = 0f
        set(value) {
            field = value
            pxVehicle.commandState.setBrakes(0, value)
            pxVehicle.commandState.nbBrakes = 1
        }

    actual val updater: VehicleUpdater get() = TODO()

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

    override val pxRigidActor: PxRigidActor

    init {
        for (i in 0..3) {
            mutWheelInfos += WheelInfo()
        }

        actorGeometry = PxBoxGeometry(1f, 0.75f, 2.3f)
        pxVehicle = createVehicle(vehicleProps)

        pxRigidActor = pxVehicle.physXState.physxActor.rigidBody
        mass = vehicleProps.chassisMass
        transform.set(pose)

        vehicleSimulationContext = PxVehiclePhysXSimulationContext().apply {
            setToDefault()
            frame.lngAxis = PxVehicleAxesEnum.ePosZ
            frame.latAxis = PxVehicleAxesEnum.ePosX
            frame.vrtAxis = PxVehicleAxesEnum.ePosY
            scale.scale = 1f
            world.gravity.toPxVec3(gravity)
            physxScene = world.pxScene
            physxActorUpdateMode = PxVehiclePhysXActorUpdateModeEnum.eAPPLY_ACCELERATION
        }
    }

    actual fun setToRestState() {
//        pxVehicle.setToRestState()
    }

    override fun release() {
        pxVehicle.destroy()
//        vehicleAsVector.destroy()
        super.release()
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        // fixme: onPhysicsUpdate() is executed after physics step, vehicle update would probably be better
        //  before physics step

//        if (isReverse && pxVehicle.mDriveDynData.mTargetGear != PxVehicleGearEnum.eREVERSE) {
//            pxVehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eREVERSE)
//        } else if (!isReverse && pxVehicle.mDriveDynData.mTargetGear == PxVehicleGearEnum.eREVERSE) {
//            pxVehicle.mDriveDynData.forceGearChange(PxVehicleGearEnum.eFIRST)
//        }

        pxVehicle.step(timeStep, vehicleSimulationContext)
//        vehicleTest.stepSimulation()

        for (i in 0 until 4) {
            val wheelInfo = wheelInfos[i]
            pxVehicle.baseState.getWheelLocalPoses(i).localPose.toMat4f(wheelInfo.transform)
            wheelInfo.lateralSlip = 0f
            wheelInfo.longitudinalSlip = 0f
        }

//        updater.updateVehicle(this, timeStep)

        val engineSpdOmega = pxVehicle.engineDriveState.engineState.rotationSpeed
        engineSpd = max(750f, engineSpdOmega * OMEGA_TO_RPM)
        engineTq = pxVehicle.engineDriveParams.engineParams.torqueCurve.interpolate(engineSpdOmega / pxVehicle.engineDriveParams.engineParams.maxOmega) * peakTorque * throttleInput
        engineP = engineTq * engineSpdOmega
        curGear = pxVehicle.engineDriveState.gearboxState.currentGear - pxVehicle.engineDriveParams.gearBoxParams.neutralGear

//        prevLinearSpeed.set(linearSpeed)
//        linearSpeed.z = pxVehicle.computeForwardSpeed()
//        linearSpeed.x = pxVehicle.computeSidewaysSpeed()
//        linearAccel.z = linearAccel.z * 0.5f + (linearSpeed.z - prevLinearSpeed.z) / timeStep * 0.5f
//        linearAccel.x = linearAccel.x * 0.5f + (linearSpeed.x - prevLinearSpeed.x) / timeStep * 0.5f

        super.onPhysicsUpdate(timeStep)
    }

    private fun computeWheelCenterActorOffsets(vehicleProps: VehicleProperties): List<MutableVec3f> {
        val tw = vehicleProps.trackWidth * 0.5f
        val offsets = List(4) { MutableVec3f() }
        offsets[FRONT_LEFT].set(tw, vehicleProps.wheelCenterHeightOffset - 0.3f, vehicleProps.wheelPosFront)
        offsets[FRONT_RIGHT].set(-tw, vehicleProps.wheelCenterHeightOffset - 0.3f, vehicleProps.wheelPosFront)
        offsets[REAR_LEFT].set(tw, vehicleProps.wheelCenterHeightOffset - 0.3f, vehicleProps.wheelPosRear)
        offsets[REAR_RIGHT].set(-tw, vehicleProps.wheelCenterHeightOffset - 0.3f, vehicleProps.wheelPosRear)
        return offsets
    }

    private fun createVehicle(vehicleProps: VehicleProperties): EngineDriveVehicle {
        val vehicle = EngineDriveVehicle()

        // Compute the wheel center offsets from the origin.
        val wheelOffsets = computeWheelCenterActorOffsets(vehicleProps)

        // Set up rigid body, wheels, suspensions etc.
        vehicle.setupBaseParams(vehicleProps, wheelOffsets)
        // Set up actor
        vehicle.setupPhysxParams(vehicleProps)
        // Set up engine, gearbox, diff etc.
        vehicle.setupEngineParams(vehicleProps)



        // Initialize vehicle stuff
        vehicle.initialize(Physics.physics, Physics.cookingParams, Physics.defaultMaterial.pxMaterial, EngineDriveVehicleEnum.eDIFFTYPE_FOURWHEELDRIVE)

        MemoryStack.stackPush().use { stack ->
            // Apply a start pose to the physx actor and add it to the physx scene.
            val vehiclePose = PxTransform.createAt(stack, MemoryStack::nmalloc,
                PxVec3.createAt(stack, MemoryStack::nmalloc, 0f, 1f, 0f),
                PxQuat.createAt(stack, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity)
            )
            vehicle.physXState.physxActor.rigidBody.globalPose = vehiclePose
            //world.pxScene.addActor(vehicle.physXState.physxActor.rigidBody)

            // Set the vehicle in 1st gear.
            vehicle.engineDriveState.gearboxState.currentGear = vehicle.engineDriveParams.gearBoxParams.neutralGear + 1
            vehicle.engineDriveState.gearboxState.targetGear = vehicle.engineDriveParams.gearBoxParams.neutralGear + 1

            // Set the vehicle to use the automatic gearbox.
            vehicle.transmissionCommandState.targetGear =
                PxVehicleEngineDriveTransmissionCommandStateEnum.eAUTOMATIC_GEAR.value
        }

        return vehicle
    }

    private fun EngineDriveVehicle.setupBaseParams(vehicleProps: VehicleProperties, wheelCenterActorOffsets: List<Vec3f>) {
        MemoryStack.stackPush().use { mem ->
            val numWheels = vehicleProps.numWheels
            //val centerOfMass = vehicleProps.chassisCMOffset.toPxVec3(mem.createPxVec3())
            val pxWheelCenterActorOffsets = wheelCenterActorOffsets.toVector_PxVec3()

            if (numWheels != 4) {
                TODO("For now only 4 wheeled vehicles are suppoted")
            }

            baseParams.axleDescription.apply {
                nbAxles = 2
                nbWheels = 4
                setNbWheelsPerAxle(0, 2)
                setNbWheelsPerAxle(1, 2)
                setAxleToWheelIds(0, 0)
                setAxleToWheelIds(1, 2)
                for (i in 0..3) {
                    setWheelIdsInAxleOrder(i, i)
                }
            }

            baseParams.frame.apply {
                latAxis = PxVehicleAxesEnum.ePosX
                lngAxis = PxVehicleAxesEnum.ePosZ
                vrtAxis = PxVehicleAxesEnum.ePosY
            }
            baseParams.scale.scale = 1f

            baseParams.rigidBodyParams.apply {
                mass = vehicleProps.chassisMass
                vehicleProps.chassisMOI.toPxVec3(moi)
            }

            val normalBrake = baseParams.getBrakeResponseParams(0)
            val handBrake = baseParams.getBrakeResponseParams(0)
            normalBrake.nonlinearResponse.nbSpeedResponses = 0
            normalBrake.nonlinearResponse.nbCommandValues = 0
            normalBrake.maxResponse = (vehicleProps.maxBrakeTorqueFront + vehicleProps.maxBrakeTorqueRear) / 2f
            handBrake.nonlinearResponse.nbSpeedResponses = 0
            handBrake.nonlinearResponse.nbCommandValues = 0
            handBrake.maxResponse = vehicleProps.maxHandBrakeTorque
            for (i in 0..3) {
                val isFront = i < 2
                normalBrake.setWheelResponseMultipliers(i, if (isFront) 0.7f else 0.3f)
                handBrake.setWheelResponseMultipliers(i, if (isFront) 0f else 1f)
            }

            baseParams.steerResponseParams.apply {
                maxResponse = vehicleProps.maxSteerAngle.toRad()
                for (i in 0..3) {
                    val isFront = i < 2
                    setWheelResponseMultipliers(i, if (isFront) 1f else 0f)
                }
            }
            baseParams.getAckermannParams(0).apply {
                setWheelIds(0, 0)
                setWheelIds(1, 1)
                wheelBase = abs(vehicleProps.wheelPosFront) + abs(vehicleProps.wheelPosRear)
                trackWidth = vehicleProps.trackWidth
                strength = 1f
            }

            // Set up the wheels
            for (i in 0 until numWheels) {
                val isFront = i < 2
                val wheel = baseParams.getWheelParams(i)
                wheel.mass = if (isFront) vehicleProps.wheelMassFront else vehicleProps.wheelMassRear
                wheel.moi = if (isFront) vehicleProps.wheelMoiFront else vehicleProps.wheelMoiRear
                wheel.radius = if (isFront) vehicleProps.wheelRadiusFront else vehicleProps.wheelRadiusRear
                wheel.halfWidth = if (isFront) vehicleProps.wheelWidthFront / 2f else vehicleProps.wheelWidthRear / 2f
                wheel.dampingRate = 0.25f
                //wheel.mMaxBrakeTorque = if (i == FRONT_LEFT || i == FRONT_RIGHT) vehicleProps.maxBrakeTorqueFront else vehicleProps.maxBrakeTorqueRear
            }
            // Enable the handbrake for the rear wheels only.
//            wheels[REAR_LEFT].mMaxHandBrakeTorque = vehicleProps.maxHandBrakeTorque
//            wheels[REAR_RIGHT].mMaxHandBrakeTorque = vehicleProps.maxHandBrakeTorque
//            // Enable steering for the front wheels only.
//            wheels[FRONT_LEFT].mMaxSteer = vehicleProps.maxSteerAngle.toRad()
//            wheels[FRONT_RIGHT].mMaxSteer = vehicleProps.maxSteerAngle.toRad()

            // Set up the tires.
            for (i in 0 until numWheels) {
                val tire = baseParams.getTireForceParams(i)
                tire.longStiff = 25000f
                tire.latStiffX = 0.01f
                tire.latStiffY = 120000f
                tire.camberStiff = 0f
                tire.restLoad = 5500f

                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 0, 0, 0f)
                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 0, 1, 1f)
                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 1, 0, 0.1f)
                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 1, 1, 1f)
                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 2, 0, 1f)
                PxVehicleTireForceParamsExt.setFrictionVsSlip(tire, 2, 1, 1f)
                PxVehicleTireForceParamsExt.setLoadFilter(tire, 0, 0, 0f)
                PxVehicleTireForceParamsExt.setLoadFilter(tire, 0, 1, 0.23f)
                PxVehicleTireForceParamsExt.setLoadFilter(tire, 1, 0, 3f)
                PxVehicleTireForceParamsExt.setLoadFilter(tire, 1, 1, 3f)
            }

            // Set up the suspensions
            // Compute the mass supported by each suspension spring.
            baseParams.suspensionStateCalculationParams.suspensionJounceCalculationType = PxVehicleSuspensionJounceCalculationTypeEnum.eSWEEP
            baseParams.suspensionStateCalculationParams.limitSuspensionExpansionVelocity = false

            val forceAppPoint = mem.createPxVec3(0f, 0f, -0.1f)
            val suspSprungMasses = Vector_PxReal(numWheels)
            PxVehicleTopLevelFunctions.VehicleComputeSprungMasses(numWheels, pxWheelCenterActorOffsets,
                vehicleProps.chassisMass, PxVehicleAxesEnum.eNegY, suspSprungMasses)

            for (i in 0 until numWheels) {
                val susp = baseParams.getSuspensionParams(i)
                val suspForce = baseParams.getSuspensionForceParams(i)
                val suspComp = baseParams.getSuspensionComplianceParams(i)

                val cmOffset = wheelCenterActorOffsets[i].subtract(vehicleProps.chassisCMOffset, MutableVec3f())

                susp.suspensionAttachment.p = cmOffset.toPxVec3(mem.createPxVec3())
                susp.suspensionAttachment.q.setIdentity()
                susp.suspensionTravelDir.set(Vec3f.NEG_Y_AXIS)
                susp.suspensionTravelDist = vehicleProps.maxCompression + vehicleProps.maxDroop
                susp.wheelAttachment.p.set(Vec3f.ZERO)
                susp.wheelAttachment.q.setIdentity()

                suspForce.damping = vehicleProps.springDamperRate
                suspForce.stiffness = vehicleProps.springStrength
                suspForce.sprungMass = suspSprungMasses.at(i)

                suspComp.wheelToeAngle.addPair(0f, 0f)
                suspComp.wheelCamberAngle.addPair(0f, 0f)
                suspComp.suspForceAppPoint.addPair(0f, forceAppPoint)
                suspComp.tireForceAppPoint.addPair(0f, forceAppPoint)
            }

            // Add a front and rear anti-roll bar
            var antiRollIdx = 0
            if (vehicleProps.frontAntiRollBarStiffness > 0f) {
                val barFront = baseParams.getAntiRollForceParams(antiRollIdx++)
                barFront.wheel0 = FRONT_LEFT
                barFront.wheel1 = FRONT_RIGHT
                barFront.stiffness = vehicleProps.frontAntiRollBarStiffness
            }
            if (vehicleProps.rearAntiRollBarStiffness > 0f) {
                val barRear = baseParams.getAntiRollForceParams(antiRollIdx++)
                barRear.wheel0 = REAR_LEFT
                barRear.wheel1 = REAR_RIGHT
                barRear.stiffness = vehicleProps.rearAntiRollBarStiffness
            }
            baseParams.nbAntiRollForceParams = antiRollIdx

            suspSprungMasses.destroy()
            pxWheelCenterActorOffsets.destroy()
        }
    }

    private fun EngineDriveVehicle.setupPhysxParams(vehicleProps: VehicleProperties) {
        MemoryStack.stackPush().use { mem ->
            val filterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 0, 0, 0, 0)
            val queryFlags = PxQueryFlags.createAt(mem, MemoryStack::nmalloc, PxQueryFlagEnum.eSTATIC.value.toShort())
            val roadQueryFilterData = PxQueryFilterData.createAt(mem, MemoryStack::nmalloc, filterData, queryFlags)

            val com = MutableVec3f(vehicleProps.chassisCMOffset)
            //println(com)
            com.set(0f, 0f, 0f)

            val actorCMassLocalPose = PxTransform.createAt(mem, MemoryStack::nmalloc,
                com.toPxVec3(mem.createPxVec3()),
                PxQuat.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity)
            )
            //val actorBoxShapeHalfExtents = PxVec3.createAt(mem, MemoryStack::nmalloc, 1f, 0.75f, 2.3f)
            val actorShapeLocalPose = PxTransform.createAt(mem, MemoryStack::nmalloc,
                PxVec3.createAt(mem, MemoryStack::nmalloc, 0f, 0.83f, 0f),
                PxQuat.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity)
            )

            val materialFriction = PxVehiclePhysXMaterialFriction()
            materialFriction.friction = 1f
            materialFriction.material = Physics.defaultMaterial.pxMaterial
            physXParams.create(
                baseParams.axleDescription,
                roadQueryFilterData,
                null,
                materialFriction,
                1,
                1f,
                actorCMassLocalPose,
                actorGeometry,
                actorShapeLocalPose,
                PxVehiclePhysXRoadGeometryQueryTypeEnum.eRAYCAST
            )
            //physXParams.physxActorWheelQueryFilterData = mem.createPxFilterData(0, 0, 0, VehicleUtils.SURFACE_FLAG_NON_DRIVABLE)
        }
    }

    private fun EngineDriveVehicle.setupEngineParams(vehicleProps: VehicleProperties) {
        engineDriveParams.autoboxParams.apply {
            // set engine speed ratios to trigger shift up / shift down
            for (i in 0..6) {
                setUpRatios(i, 0.65f)
                setDownRatios(i, 0.5f)
            }
            // set lower ratio for gear 1 (neutral gear)
            setUpRatios(1, 0.15f)
            latency = 2f
        }

        engineDriveParams.clutchCommandResponseParams.maxResponse = vehicleProps.clutchStrength
        engineDriveParams.clutchParams.apply {
            accuracyMode = PxVehicleClutchAccuracyModeEnum.eBEST_POSSIBLE
            estimateIterations = 5
        }

        engineDriveParams.engineParams.apply {
            // todo: set a more realistic torque curve
            torqueCurve.addPair(0f, 1f)
            torqueCurve.addPair(0.33f, 1f)
            torqueCurve.addPair(1f, 1f)
            moi = 1f
            peakTorque = vehicleProps.peakEngineTorque
            idleOmega = 0f //750f / OMEGA_TO_RPM
            maxOmega = vehicleProps.peakEngineRpm / OMEGA_TO_RPM
            dampingRateFullThrottle = 0.15f
            dampingRateZeroThrottleClutchEngaged = 2f
            dampingRateZeroThrottleClutchDisengaged = 0.35f
        }

        engineDriveParams.gearBoxParams.apply {
            neutralGear = 1
            setRatios(0, -4f)
            setRatios(1, 0f)
            setRatios(2, 4f)
            setRatios(3, 2f)
            setRatios(4, 1.5f)
            setRatios(5, 1.1f)
            setRatios(6, 1f)
            nbRatios = 7
            finalRatio = 4f
            switchTime = 0.35f
        }

        engineDriveParams.fourWheelDifferentialParams.apply {
            for (i in 0..3) {
                val isFront = i < 2
                setTorqueRatios(i, if (isFront) 0.3f else 0.7f)
                setAveWheelSpeedRatios(i, 0.35f)
                setFrontWheelIds(0, 0)
                setFrontWheelIds(1, 1)
                setRearWheelIds(0, 2)
                setRearWheelIds(1, 3)
                centerBias = 1.3f
                centerTarget = 1.29f
                frontBias = 1.3f
                frontTarget = 1.29f
                rearBias = 1.3f
                rearTarget = 1.29f
                rate = 10f
            }
        }
    }
}