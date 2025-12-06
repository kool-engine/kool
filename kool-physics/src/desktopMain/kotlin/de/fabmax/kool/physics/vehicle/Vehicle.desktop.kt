package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.*
import de.fabmax.kool.physics.*
import de.fabmax.kool.physics.vehicle.Vehicle.Companion.FRONT_LEFT
import de.fabmax.kool.physics.vehicle.Vehicle.Companion.FRONT_RIGHT
import de.fabmax.kool.physics.vehicle.Vehicle.Companion.OMEGA_TO_RPM
import de.fabmax.kool.physics.vehicle.Vehicle.Companion.REAR_LEFT
import de.fabmax.kool.physics.vehicle.Vehicle.Companion.REAR_RIGHT
import de.fabmax.kool.util.scopedMem
import physx.common.PxVec3
import physx.geometry.PxBoxGeometry
import physx.physics.PxPairFlagEnum
import physx.physics.PxQueryFlagEnum
import physx.physics.PxShapeFlagEnum
import physx.support.PxArray_PxReal
import physx.vehicle2.*
import kotlin.math.abs
import kotlin.math.max

actual fun Vehicle(vehicleProps: VehicleProperties, world: PhysicsWorld, pose: Mat4f): Vehicle {
    return VehicleImpl(vehicleProps, world, pose)
}

class VehicleImpl(
    override val vehicleProps: VehicleProperties,
    val world: PhysicsWorld,
    pose: Mat4f
) : RigidBodyImpl(), Vehicle, PhysicsStepListener {
    val vehicleSimulationContext: PxVehiclePhysXSimulationContext
    val pxVehicle: EngineDriveVehicle

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

    private val peakTorque = vehicleProps.peakEngineTorque

    private val tmpVec = MutableVec3f()
    private val tmpVecPx = PxVec3()
    private val linearSpeed = MutableVec3f()
    private val prevLinearSpeed = MutableVec3f()
    private val linearAccel = MutableVec3f()
    private var engineSpd = 0f
    private var engineTq = 0f
    private var engineP = 0f
    private var curGear = 0

    override val wheelInfos: List<WheelInfo> = List(4) { WheelInfo() }

    override val forwardSpeed: Float
        get() = linearSpeed.z
    override val sidewaysSpeed: Float
        get() = linearSpeed.x
    override val longitudinalAcceleration: Float
        get() = linearAccel.z
    override val lateralAcceleration: Float
        get() = linearAccel.x
    override val engineSpeedRpm: Float
        get() = engineSpd
    override val engineTorqueNm: Float
        get() = engineTq
    override val enginePowerW: Float
        get() = engineP
    override val currentGear: Int
        get() = curGear

    override var isReverse = false

    override val holder: RigidActorHolder

    init {
        vehicleSimulationContext = PxVehiclePhysXSimulationContext().apply {
            setToDefault()
            frame.lngAxis = PxVehicleAxesEnum.ePosZ
            frame.latAxis = PxVehicleAxesEnum.ePosX
            frame.vrtAxis = PxVehicleAxesEnum.ePosY
            scale.scale = 1f
            world.gravity.toPxVec3(gravity)
            physxScene = (world as PhysicsWorldImpl).pxScene
            physxActorUpdateMode = PxVehiclePhysXActorUpdateModeEnum.eAPPLY_VELOCITY
            physxUnitCylinderSweepMesh = PhysicsImpl.unitCylinder
        }
        pxVehicle = createVehicle(vehicleProps)

        holder = RigidActorHolder(pxVehicle.physXState.physxActor.rigidBody)
        transform.setMatrix(pose)
        world.physicsStepListeners += this
    }

    override fun setToRestState() {
        pxVehicle.commandState.setToDefault()
        pxVehicle.transmissionCommandState.setToDefault()
        pxVehicle.baseState.setToDefault()
        pxVehicle.engineDriveState.setToDefault()

        scopedMem {
            val pxVecZero = createPxVec3(0f, 0f, 0f)
            val actor = WrapPointer.PxRigidDynamic(pxVehicle.physXState.physxActor.rigidBody.ptr)
            actor.linearVelocity = pxVecZero
            actor.angularVelocity = pxVecZero
        }
    }

    override fun doRelease() {
        super.doRelease()
        pxVehicle.destroy()
        tmpVecPx.destroy()
        world.physicsStepListeners -= this
    }

    override fun onPhysicsUpdate(timeStep: Float) {
        val targetGear = pxVehicle.engineDriveState.gearboxState.targetGear
        val neutralGear = pxVehicle.engineDriveParams.gearBoxParams.neutralGear
        if (isReverse && targetGear != neutralGear - 1) {
            pxVehicle.transmissionCommandState.targetGear = 0
            pxVehicle.engineDriveState.gearboxState.currentGear = neutralGear - 1
            pxVehicle.engineDriveState.gearboxState.targetGear = neutralGear - 1
        } else if (!isReverse && targetGear == neutralGear - 1) {
            pxVehicle.transmissionCommandState.targetGear =
                PxVehicleEngineDriveTransmissionCommandStateEnum.eAUTOMATIC_GEAR.value
            pxVehicle.engineDriveState.gearboxState.currentGear = neutralGear + 1
            pxVehicle.engineDriveState.gearboxState.targetGear = neutralGear + 1
        }

        pxVehicle.step(timeStep, vehicleSimulationContext)

        tmpVec.set(vehicleProps.chassisCMOffset).mul(-2f)
        for (i in 0 until 4) {
            val wheelInfo = wheelInfos[i]
            pxVehicle.baseState.getWheelLocalPoses(i).localPose.toTrsTransform(wheelInfo.transform)
            wheelInfo.transform.translate(tmpVec)

            val isHit = pxVehicle.baseState.getRoadGeomStates(i).hitState
            if (isHit) {
                val slipState = pxVehicle.baseState.getTireSlipStates(i)
                wheelInfo.lateralSlip = slipState.getSlips(PxVehicleTireDirectionModesEnum.eLATERAL.value)
                wheelInfo.longitudinalSlip = slipState.getSlips(PxVehicleTireDirectionModesEnum.eLONGITUDINAL.value)
            } else {
                wheelInfo.lateralSlip = 0f
                wheelInfo.longitudinalSlip = 0f
            }
        }

        val engineSpdOmega = pxVehicle.engineDriveState.engineState.rotationSpeed
        engineSpd = max(750f, engineSpdOmega * OMEGA_TO_RPM)
        engineTq =
            pxVehicle.engineDriveParams.engineParams.torqueCurve.interpolate(engineSpdOmega / pxVehicle.engineDriveParams.engineParams.maxOmega) * peakTorque * throttleInput
        engineP = engineTq * engineSpdOmega
        curGear =
            pxVehicle.engineDriveState.gearboxState.currentGear - pxVehicle.engineDriveParams.gearBoxParams.neutralGear

        val vehicleActor = pxVehicle.physXState.physxActor.rigidBody
        val linearVelocity = vehicleActor.linearVelocity
        val forwardDir = vehicleActor.globalPose.q.basisVector2
        val sideDir = vehicleActor.globalPose.q.basisVector0

        linearVelocity.toVec3f(tmpVec)
        val cW = 0.22f
        val p = 1f
        val a = 1.25f
        val v = tmpVec.length()
        val fw = tmpVec.norm().mul(-cW * p * a * v * v)
        if (fw.length() > 10f) {
            vehicleActor.addForce(fw.toPxVec3(tmpVecPx))
        }

        prevLinearSpeed.set(linearSpeed)
        linearSpeed.z = linearVelocity.dot(forwardDir)
        linearSpeed.x = linearVelocity.dot(sideDir)
        linearAccel.z = linearAccel.z * 0.5f + (linearSpeed.z - prevLinearSpeed.z) / timeStep * 0.5f
        linearAccel.x = linearAccel.x * 0.5f + (linearSpeed.x - prevLinearSpeed.x) / timeStep * 0.5f

        val nbSubsteps = if (linearSpeed.z < 5f) 3 else 1
        pxVehicle.componentSequence.setSubsteps(pxVehicle.componentSequenceSubstepGroupHandle, nbSubsteps.toByte())
    }

    private fun computeWheelCenterActorOffsets(vehicleProps: VehicleProperties): List<MutableVec3f> {
        val twF = vehicleProps.trackWidthFront * 0.5f
        val twR = vehicleProps.trackWidthRear * 0.5f
        val offsets = List(4) { MutableVec3f() }
        offsets[FRONT_LEFT].set(twF, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosFront)
        offsets[FRONT_RIGHT].set(-twF, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosFront)
        offsets[REAR_LEFT].set(twR, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosRear)
        offsets[REAR_RIGHT].set(-twR, vehicleProps.wheelCenterHeightOffset, vehicleProps.wheelPosRear)
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
        vehicle.initialize(
            PhysicsImpl.physics,
            PhysicsImpl.cookingParams,
            Physics.defaultMaterial.pxMaterial,
            EngineDriveVehicleEnum.eDIFFTYPE_FOURWHEELDRIVE
        )

        scopedMem {
            // Apply a start pose to the physx actor and add it to the physx scene.
            val vehiclePose = createPxTransform(Vec3f(0f, 1f, 0f), QuatF.IDENTITY)
            vehicle.physXState.physxActor.rigidBody.globalPose = vehiclePose

            // Set the vehicle in 1st gear.
            vehicle.engineDriveState.gearboxState.currentGear = vehicle.engineDriveParams.gearBoxParams.neutralGear + 1
            vehicle.engineDriveState.gearboxState.targetGear = vehicle.engineDriveParams.gearBoxParams.neutralGear + 1

            // Set the vehicle to use the automatic gearbox.
            vehicle.transmissionCommandState.targetGear =
                PxVehicleEngineDriveTransmissionCommandStateEnum.eAUTOMATIC_GEAR.value
        }

        vehicle.physXState.physxActor.rigidBody.mass = vehicleProps.chassisMass

        return vehicle
    }

    private fun EngineDriveVehicle.setupBaseParams(
        vehicleProps: VehicleProperties,
        wheelCenterActorOffsets: List<Vec3f>
    ) {
        scopedMem {
            val numWheels = vehicleProps.numWheels
            val wheelOffsets = wheelCenterActorOffsets.map { MutableVec3f(it).add(vehicleProps.chassisCMOffset) }
            val pxWheelCenterActorOffsets = wheelOffsets.toPxArray_PxVec3()

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
            val handBrake = baseParams.getBrakeResponseParams(1)
            normalBrake.nonlinearResponse.nbSpeedResponses = 0
            normalBrake.nonlinearResponse.nbCommandValues = 0
            normalBrake.maxResponse = vehicleProps.maxBrakeTorque
            handBrake.nonlinearResponse.nbSpeedResponses = 0
            handBrake.nonlinearResponse.nbCommandValues = 0
            handBrake.maxResponse = vehicleProps.maxHandBrakeTorque
            for (i in 0..3) {
                val isFront = i < 2
                normalBrake.setWheelResponseMultipliers(
                    i,
                    if (isFront) vehicleProps.brakeTorqueFrontFactor else vehicleProps.brakeTorqueRearFactor
                )
                handBrake.setWheelResponseMultipliers(
                    i,
                    if (isFront) vehicleProps.handBrakeTorqueFrontFactor else vehicleProps.handBrakeTorqueRearFactor
                )
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
                trackWidth = vehicleProps.trackWidthFront
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
            }

            // Set up the tires.
            for (i in 0 until numWheels) {
                val tire = baseParams.getTireForceParams(i)
                tire.longStiff = 25000f
                tire.latStiffX = 0.007f
                tire.latStiffY = 180000f
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
            baseParams.suspensionStateCalculationParams.suspensionJounceCalculationType =
                PxVehicleSuspensionJounceCalculationTypeEnum.eSWEEP
            baseParams.suspensionStateCalculationParams.limitSuspensionExpansionVelocity = false

            val forceAppPoint = createPxVec3(0f, 0f, -0.2f)
            val suspSprungMasses = PxArray_PxReal(numWheels)
            PxVehicleTopLevelFunctions.VehicleComputeSprungMasses(
                numWheels, pxWheelCenterActorOffsets,
                vehicleProps.chassisMass, PxVehicleAxesEnum.eNegY, suspSprungMasses
            )

            for (i in 0 until numWheels) {
                val susp = baseParams.getSuspensionParams(i)
                val suspForce = baseParams.getSuspensionForceParams(i)
                val suspComp = baseParams.getSuspensionComplianceParams(i)

                susp.suspensionAttachment.p = wheelOffsets[i].toPxVec3(createPxVec3())
                susp.suspensionAttachment.q.setIdentity()
                susp.suspensionTravelDir.set(Vec3f.NEG_Y_AXIS)
                susp.suspensionTravelDist = vehicleProps.maxCompression + vehicleProps.maxDroop
                susp.wheelAttachment.p.set(Vec3f.ZERO)
                susp.wheelAttachment.q.setIdentity()

                suspForce.damping = vehicleProps.springDamperRate
                suspForce.stiffness = vehicleProps.springStrength
                suspForce.sprungMass = suspSprungMasses.get(i)

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
        scopedMem {
            val roadFilterData = createPxFilterData(0, 0, 0, 0)
            val roadQueryFlags = createPxQueryFlags(PxQueryFlagEnum.eSTATIC)
            val roadQueryFilterData = createPxQueryFilterData(roadFilterData, roadQueryFlags)
            val actorCMassLocalPose = createPxTransform(vehicleProps.chassisCMOffset * -1f, QuatF.IDENTITY)
            val actorShapeLocalPose = createPxTransform(Vec3f(0f, 0.83f, 0f), QuatF.IDENTITY)

            // chassis filter data
            physXParams.physxActorShapeFlags.raise(PxShapeFlagEnum.eSCENE_QUERY_SHAPE)
            physXParams.physxActorShapeFlags.raise(PxShapeFlagEnum.eSIMULATION_SHAPE)

            val reportContactFlags = PxPairFlagEnum.eNOTIFY_TOUCH_FOUND.value or PxPairFlagEnum.eNOTIFY_TOUCH_LOST.value or PxPairFlagEnum.eNOTIFY_CONTACT_POINTS.value
            FilterData(
                VehicleUtils.COLLISION_FLAG_CHASSIS,
                VehicleUtils.COLLISION_FLAG_CHASSIS_AGAINST,
                reportContactFlags
            ).toPxFilterData(physXParams.physxActorSimulationFilterData)
            FilterData { VehicleUtils.setupNonDrivableSurface(this) }.toPxFilterData(physXParams.physxActorQueryFilterData)

            // wheel filter data
            //physXParams.physxActorWheelShapeFlags.raise(PxShapeFlagEnum.eSCENE_QUERY_SHAPE)
            //physXParams.physxActorWheelShapeFlags.raise(PxShapeFlagEnum.eSIMULATION_SHAPE)
            FilterData(VehicleUtils.COLLISION_FLAG_WHEEL, VehicleUtils.COLLISION_FLAG_WHEEL_AGAINST)
                .toPxFilterData(physXParams.physxActorWheelSimulationFilterData)
            FilterData { VehicleUtils.setupNonDrivableSurface(this) }
                .toPxFilterData(physXParams.physxActorWheelQueryFilterData)

            val geometry = vehicleProps.chassisGeometry?.holder?.px
                ?: PxBoxGeometry(
                    vehicleProps.chassisDims.x * 0.5f,
                    vehicleProps.chassisDims.y * 0.5f,
                    vehicleProps.chassisDims.z * 0.5f
                )

            val materialFriction = PxVehiclePhysXMaterialFriction()
            materialFriction.friction = 1.5f
            materialFriction.material = Physics.defaultMaterial.pxMaterial
            physXParams.create(
                baseParams.axleDescription,
                roadQueryFilterData,
                null,
                materialFriction,
                1,
                1.5f,
                actorCMassLocalPose,
                geometry,
                actorShapeLocalPose,
                PxVehiclePhysXRoadGeometryQueryTypeEnum.eSWEEP
            )
        }
    }

    private fun EngineDriveVehicle.setupEngineParams(vehicleProps: VehicleProperties) {
        engineDriveParams.autoboxParams.apply {
            // set engine speed ratios to trigger shift up / shift down
            for (i in 0..7) {
                setUpRatios(i, 0.9f)
                setDownRatios(i, 0.5f)
            }
            // set lower ratio for gear 1 (neutral gear)
            setUpRatios(1, 0.15f)
            latency = 0.7f
        }

        engineDriveParams.clutchCommandResponseParams.maxResponse = vehicleProps.clutchStrength
        engineDriveParams.clutchParams.apply {
            accuracyMode = PxVehicleClutchAccuracyModeEnum.eBEST_POSSIBLE
            estimateIterations = 5
        }

        engineDriveParams.engineParams.apply {
            torqueCurve.addPair(0f, 0.3f)
            torqueCurve.addPair(0.33f, 0.85f)
            torqueCurve.addPair(0.8f, 1f)
            torqueCurve.addPair(0.9f, 0.8f)
            torqueCurve.addPair(1f, 0.5f)
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
            setRatios(6, 0.95f)
            setRatios(7, 0.85f)
            nbRatios = 8
            finalRatio = vehicleProps.gearFinalRatio
            switchTime = 0.35f
        }

        engineDriveParams.fourWheelDifferentialParams.apply {
            for (i in 0..3) {
                val isFront = i < 2
                setTorqueRatios(i, if (isFront) 0.15f else 0.35f)
                setAveWheelSpeedRatios(i, 0.25f)
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

        for (i in 0..3) {
            engineDriveParams.multiWheelDifferentialParams.setTorqueRatios(i, 0.25f)
            engineDriveParams.multiWheelDifferentialParams.setAveWheelSpeedRatios(i, 0.25f)
            engineDriveParams.tankDifferentialParams.setTorqueRatios(i, 0.25f)
            engineDriveParams.tankDifferentialParams.setAveWheelSpeedRatios(i, 0.25f)
        }
    }
}