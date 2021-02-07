/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxVehicleTopLevelFunctions {
    fun InitVehicleSDK(physics: PxPhysics): Boolean
    fun PxVehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxVec3, centreOfMass: PxVec3, totalMass: Float, gravityDirection: Int, sprungMasses: PxRealPtr)
    fun PxVehicleSuspensionRaycasts(batchQuery: PxBatchQuery, vehicles: Vector_PxVehicleWheels, nbSceneQueryResults: Int, sceneQueryResults: PxRaycastQueryResult)
    fun PxVehicleUpdates(timestep: Float, gravity: PxVec3, vehicleDrivableSurfaceToTireFrictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs, vehicles: Vector_PxVehicleWheels, vehicleWheelQueryResults: PxVehicleWheelQueryResult)
    fun VehicleSetBasisVectors(up: PxVec3, forward: PxVec3)
    fun VehicleSetUpdateMode(vehicleUpdateMode: Int)
    fun PxVehicleTireData_getFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int): Float
    fun PxVehicleTireData_setFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int, value: Float)
}

external interface PxVehicleAckermannGeometryData {
    var mAccuracy: Float
    var mFrontWidth: Float
    var mRearWidth: Float
    var mAxleSeparation: Float
}
fun PxVehicleAckermannGeometryData(): PxVehicleAckermannGeometryData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleAckermannGeometryData()")
}

external interface PxVehicleAntiRollBarData {
    var mWheel0: Int
    var mWheel1: Int
    var mStiffness: Float
}
fun PxVehicleAntiRollBarData(): PxVehicleAntiRollBarData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleAntiRollBarData()")
}

external interface PxVehicleAutoBoxData {
    var mUpRatios: Array<Float>
    var mDownRatios: Array<Float>

    fun setLatency(latency: Float)
    fun getLatency(): Float
    fun getUpRatios(a: Int): Float
    fun setUpRatios(a: Int, ratio: Float)
    fun getDownRatios(a: Int): Float
    fun setDownRatios(a: Int, ratio: Float)
}
fun PxVehicleAutoBoxData(): PxVehicleAutoBoxData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleAutoBoxData()")
}

external interface PxVehicleChassisData {
    var mMOI: PxVec3
    var mMass: Float
    var mCMOffset: PxVec3
}
fun PxVehicleChassisData(): PxVehicleChassisData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleChassisData()")
}

external interface PxVehicleClutchData {
    var mStrength: Float
    var mAccuracyMode: Int
    var mEstimateIterations: Int
}
fun PxVehicleClutchData(): PxVehicleClutchData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleClutchData()")
}

external interface PxVehicleDifferential4WData {
    var mFrontRearSplit: Float
    var mFrontLeftRightSplit: Float
    var mRearLeftRightSplit: Float
    var mCentreBias: Float
    var mFrontBias: Float
    var mRearBias: Float
    var mType: Int
}
fun PxVehicleDifferential4WData(): PxVehicleDifferential4WData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleDifferential4WData()")
}

external interface PxVehicleDrivableSurfaceToTireFrictionPairs {
    fun allocate(maxNbTireTypes: Int, maxNbSurfaceTypes: Int): PxVehicleDrivableSurfaceToTireFrictionPairs
    fun setup(nbTireTypes: Int, nbSurfaceTypes: Int, drivableSurfaceMaterials: PxMaterialPtr, drivableSurfaceTypes: PxVehicleDrivableSurfaceType)
    fun release()
    fun setTypePairFriction(surfaceType: Int, tireType: Int, value: Float)
    fun getTypePairFriction(surfaceType: Int, tireType: Int): Float
    fun getMaxNbSurfaceTypes(): Int
    fun getMaxNbTireTypes(): Int
}

external interface PxVehicleDrivableSurfaceType {
    var mType: Int
}
fun PxVehicleDrivableSurfaceType(): PxVehicleDrivableSurfaceType {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleDrivableSurfaceType()")
}

external interface PxVehicleDrive : PxVehicleWheels {
    var mDriveDynData: PxVehicleDriveDynData
}

external interface PxVehicleDrive4W : PxVehicleDrive {
    var mDriveSimData: PxVehicleDriveSimData4W

    fun allocate(nbWheels: Int): PxVehicleDrive4W
    fun free()
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData, driveData: PxVehicleDriveSimData4W, nbNonDrivenWheels: Int)
    fun setToRestState()
}

external interface PxVehicleDriveDynData {
    var mControlAnalogVals: Array<Float>
    var mUseAutoGears: Boolean
    var mGearUpPressed: Boolean
    var mGearDownPressed: Boolean
    var mCurrentGear: Int
    var mTargetGear: Int
    var mEnginespeed: Float
    var mGearSwitchTime: Float
    var mAutoBoxSwitchTime: Float

    fun setToRestState()
    fun setAnalogInput(type: Int, analogVal: Float)
    fun getAnalogInput(type: Int): Float
    fun setGearUp(digitalVal: Boolean)
    fun setGearDown(digitalVal: Boolean)
    fun getGearUp(): Boolean
    fun getGearDown(): Boolean
    fun setUseAutoGears(useAutoGears: Boolean)
    fun getUseAutoGears(): Boolean
    fun toggleAutoGears()
    fun setCurrentGear(currentGear: Int)
    fun getCurrentGear(): Int
    fun setTargetGear(targetGear: Int)
    fun getTargetGear(): Int
    fun startGearChange(targetGear: Int)
    fun forceGearChange(targetGear: Int)
    fun setEngineRotationSpeed(speed: Float)
    fun getEngineRotationSpeed(): Float
    fun getGearSwitchTime(): Float
    fun getAutoBoxSwitchTime(): Float
    fun getNbAnalogInput(): Int
    fun setGearChange(gearChange: Int)
    fun getGearChange(): Int
    fun setGearSwitchTime(switchTime: Float)
    fun setAutoBoxSwitchTime(autoBoxSwitchTime: Float)
}

external interface PxVehicleDriveSimData {
    fun getEngineData(): PxVehicleEngineData
    fun setEngineData(engine: PxVehicleEngineData)
    fun getGearsData(): PxVehicleGearsData
    fun setGearsData(gears: PxVehicleGearsData)
    fun getClutchData(): PxVehicleClutchData
    fun setClutchData(clutch: PxVehicleClutchData)
    fun getAutoBoxData(): PxVehicleAutoBoxData
    fun setAutoBoxData(clutch: PxVehicleAutoBoxData)
}
fun PxVehicleDriveSimData(): PxVehicleDriveSimData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleDriveSimData()")
}

external interface PxVehicleDriveSimData4W : PxVehicleDriveSimData {
    fun getDiffData(): PxVehicleDifferential4WData
    fun getAckermannGeometryData(): PxVehicleAckermannGeometryData
    fun setDiffData(diff: PxVehicleDifferential4WData)
    fun setAckermannGeometryData(ackermannData: PxVehicleAckermannGeometryData)
}
fun PxVehicleDriveSimData4W(): PxVehicleDriveSimData4W {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleDriveSimData4W()")
}

external interface PxVehicleEngineData {
    var mTorqueCurve: PxEngineTorqueLookupTable
    var mMOI: Float
    var mPeakTorque: Float
    var mMaxOmega: Float
    var mDampingRateFullThrottle: Float
    var mDampingRateZeroThrottleClutchEngaged: Float
    var mDampingRateZeroThrottleClutchDisengaged: Float
}
fun PxVehicleEngineData(): PxVehicleEngineData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleEngineData()")
}

external interface PxEngineTorqueLookupTable {
    var mDataPairs: Array<Float>
    var mNbDataPairs: Int

    fun addPair(x: Float, y: Float)
    fun getYVal(x: Float): Float
    fun getNbDataPairs(): Int
    fun clear()
    fun getX(i: Int): Float
    fun getY(i: Int): Float
}
fun PxEngineTorqueLookupTable(): PxEngineTorqueLookupTable {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxEngineTorqueLookupTable()")
}

external interface PxVehicleGearsData {
    var mRatios: Array<Float>
    var mFinalRatio: Float
    var mNbRatios: Int
    var mSwitchTime: Float

    fun getGearRatio(a: Int): Float
    fun setGearRatio(a: Int, ratio: Float)
}
fun PxVehicleGearsData(): PxVehicleGearsData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleGearsData()")
}

external interface PxVehicleSuspensionData {
    var mSpringStrength: Float
    var mSpringDamperRate: Float
    var mMaxCompression: Float
    var mMaxDroop: Float
    var mSprungMass: Float
    var mCamberAtRest: Float
    var mCamberAtMaxCompression: Float
    var mCamberAtMaxDroop: Float

    fun setMassAndPreserveNaturalFrequency(newSprungMass: Float)
}
fun PxVehicleSuspensionData(): PxVehicleSuspensionData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleSuspensionData()")
}

external interface PxVehicleTireData {
    var mLatStiffX: Float
    var mLatStiffY: Float
    var mLongitudinalStiffnessPerUnitGravity: Float
    var mCamberStiffnessPerUnitGravity: Float
    var mType: Int
}
fun PxVehicleTireData(): PxVehicleTireData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleTireData()")
}

external interface PxVehicleTireLoadFilterData {
    var mMinNormalisedLoad: Float
    var mMinFilteredNormalisedLoad: Float
    var mMaxNormalisedLoad: Float
    var mMaxFilteredNormalisedLoad: Float

    fun getDenominator(): Float
}
fun PxVehicleTireLoadFilterData(): PxVehicleTireLoadFilterData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleTireLoadFilterData()")
}

external interface PxVehicleWheelData {
    var mRadius: Float
    var mWidth: Float
    var mMass: Float
    var mMOI: Float
    var mDampingRate: Float
    var mMaxBrakeTorque: Float
    var mMaxHandBrakeTorque: Float
    var mMaxSteer: Float
    var mToeAngle: Float
}
fun PxVehicleWheelData(): PxVehicleWheelData {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleWheelData()")
}

external interface PxVehicleWheelQueryResult {
    var wheelQueryResults: PxWheelQueryResult
    var nbWheelQueryResults: Int
}
fun PxVehicleWheelQueryResult(): PxVehicleWheelQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleWheelQueryResult()")
}

external interface PxVehicleWheels : PxBase {
    var mWheelsSimData: PxVehicleWheelsSimData
    var mWheelsDynData: PxVehicleWheelsDynData

    fun getVehicleType(): Int
    fun getRigidDynamicActor(): PxRigidDynamic
    fun computeForwardSpeed(): Float
    fun computeSidewaysSpeed(): Float
    fun getNbNonDrivenWheels(): Int
}

external interface PxVehicleWheelsDynData {
    fun setToRestState()
    fun setWheelRotationSpeed(wheelIdx: Int, speed: Float)
    fun getWheelRotationSpeed(wheelIdx: Int): Float
    fun setWheelRotationAngle(wheelIdx: Int, angle: Float)
    fun getWheelRotationAngle(wheelIdx: Int): Float
    fun copy(src: PxVehicleWheelsDynData, srcWheel: Int, trgWheel: Int)
    fun getNbWheelRotationSpeed(): Int
    fun getNbWheelRotationAngle(): Int
}

external interface PxVehicleWheelsSimData {
    fun allocate(nbWheels: Int): PxVehicleWheelsSimData
    fun setChassisMass(chassisMass: Float)
    fun free()
    fun copy(src: PxVehicleWheelsSimData, srcWheel: Int, trgWheel: Int)
    fun getNbWheels(): Int
    fun getSuspensionData(id: Int): PxVehicleSuspensionData
    fun getWheelData(id: Int): PxVehicleWheelData
    fun getTireData(id: Int): PxVehicleTireData
    fun getSuspTravelDirection(id: Int): PxVec3
    fun getSuspForceAppPointOffset(id: Int): PxVec3
    fun getTireForceAppPointOffset(id: Int): PxVec3
    fun getWheelCentreOffset(id: Int): PxVec3
    fun getWheelShapeMapping(wheelId: Int): Int
    fun getSceneQueryFilterData(suspId: Int): PxFilterData
    fun getNbAntiRollBars(): Int
    fun getAntiRollBarData(antiRollId: Int): PxVehicleAntiRollBarData
    fun getTireLoadFilterData(): PxVehicleTireLoadFilterData
    fun setSuspensionData(id: Int, susp: PxVehicleSuspensionData)
    fun setWheelData(id: Int, wheel: PxVehicleWheelData)
    fun setTireData(id: Int, tire: PxVehicleTireData)
    fun setSuspTravelDirection(id: Int, dir: PxVec3)
    fun setSuspForceAppPointOffset(id: Int, offset: PxVec3)
    fun setTireForceAppPointOffset(id: Int, offset: PxVec3)
    fun setWheelCentreOffset(id: Int, offset: PxVec3)
    fun setWheelShapeMapping(wheelId: Int, shapeId: Int)
    fun setSceneQueryFilterData(suspId: Int, sqFilterData: PxFilterData)
    fun setTireLoadFilterData(tireLoadFilter: PxVehicleTireLoadFilterData)
    fun addAntiRollBarData(antiRoll: PxVehicleAntiRollBarData): Int
    fun disableWheel(wheel: Int)
    fun enableWheel(wheel: Int)
    fun getIsWheelDisabled(wheel: Int): Boolean
    fun setSubStepCount(thresholdLongitudinalSpeed: Float, lowForwardSpeedSubStepCount: Int, highForwardSpeedSubStepCount: Int)
    fun setMinLongSlipDenominator(minLongSlipDenominator: Float)
    fun setFlags(flags: PxVehicleWheelsSimFlags)
    fun getFlags(): PxVehicleWheelsSimFlags
    fun getNbWheels4(): Int
    fun getNbSuspensionData(): Int
    fun getNbWheelData(): Int
    fun getNbSuspTravelDirection(): Int
    fun getNbTireData(): Int
    fun getNbSuspForceAppPointOffset(): Int
    fun getNbTireForceAppPointOffset(): Int
    fun getNbWheelCentreOffset(): Int
    fun getNbWheelShapeMapping(): Int
    fun getNbSceneQueryFilterData(): Int
    fun getMinLongSlipDenominator(): Float
    fun setThresholdLongSpeed(f: Float)
    fun getThresholdLongSpeed(): Float
    fun setLowForwardSpeedSubStepCount(f: Int)
    fun getLowForwardSpeedSubStepCount(): Int
    fun setHighForwardSpeedSubStepCount(f: Int)
    fun getHighForwardSpeedSubStepCount(): Int
    fun setWheelEnabledState(wheel: Int, state: Boolean)
    fun getWheelEnabledState(wheel: Int): Boolean
    fun getNbWheelEnabledState(): Int
    fun getNbAntiRollBars4(): Int
    fun getNbAntiRollBarData(): Int
    fun setAntiRollBarData(id: Int, antiRoll: PxVehicleAntiRollBarData)
}

external interface PxVehicleWheelsSimFlags {
    fun isSet(flag: Int): Boolean
    fun set(flag: Int)
    fun clear(flag: Int)
}
fun PxVehicleWheelsSimFlags(flags: Int): PxVehicleWheelsSimFlags {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxVehicleWheelsSimFlags(flags)")
}

external interface PxWheelQueryResult {
    var suspLineStart: PxVec3
    var suspLineDir: PxVec3
    var suspLineLength: Float
    var isInAir: Boolean
    var tireContactActor: PxActor
    var tireContactShape: PxShape
    var tireSurfaceMaterial: PxMaterial
    var tireSurfaceType: Int
    var tireContactPoint: PxVec3
    var tireContactNormal: PxVec3
    var tireFriction: Float
    var suspJounce: Float
    var suspSpringForce: Float
    var tireLongitudinalDir: PxVec3
    var tireLateralDir: PxVec3
    var longitudinalSlip: Float
    var lateralSlip: Float
    var steerAngle: Float
    var localPose: PxTransform
}
fun PxWheelQueryResult(): PxWheelQueryResult {
    val module = PhysXJsLoader.physXJs
    return js("new module.PxWheelQueryResult()")
}

object PxVehicleClutchAccuracyModeEnum {
    val eESTIMATE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE()
    val eBEST_POSSIBLE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE()
}

object PxVehicleDifferential4WDataEnum {
    val eDIFF_TYPE_LS_4WD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_4WD()
    val eDIFF_TYPE_LS_FRONTWD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_FRONTWD()
    val eDIFF_TYPE_LS_REARWD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_LS_REARWD()
    val eDIFF_TYPE_OPEN_4WD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_4WD()
    val eDIFF_TYPE_OPEN_FRONTWD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_FRONTWD()
    val eDIFF_TYPE_OPEN_REARWD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eDIFF_TYPE_OPEN_REARWD()
    val eMAX_NB_DIFF_TYPES: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDifferential4WDataEnum_eMAX_NB_DIFF_TYPES()
}

object PxVehicleDrive4WControlEnum {
    val eANALOG_INPUT_ACCEL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_ACCEL()
    val eANALOG_INPUT_BRAKE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_BRAKE()
    val eANALOG_INPUT_HANDBRAKE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_HANDBRAKE()
    val eANALOG_INPUT_STEER_LEFT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_LEFT()
    val eANALOG_INPUT_STEER_RIGHT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eANALOG_INPUT_STEER_RIGHT()
    val eMAX_NB_DRIVE4W_ANALOG_INPUTS: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDrive4WControlEnum_eMAX_NB_DRIVE4W_ANALOG_INPUTS()
}

object PxVehicleGearEnum {
    val eREVERSE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eREVERSE()
    val eNEUTRAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eNEUTRAL()
    val eFIRST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eFIRST()
    val eSECOND: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eSECOND()
    val eTHIRD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTHIRD()
    val eFOURTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eFOURTH()
    val eFIFTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eFIFTH()
    val eSIXTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eSIXTH()
    val eSEVENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eSEVENTH()
    val eEIGHTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eEIGHTH()
    val eNINTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eNINTH()
    val eTENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTENTH()
    val eELEVENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eELEVENTH()
    val eTWELFTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWELFTH()
    val eTHIRTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTHIRTEENTH()
    val eFOURTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eFOURTEENTH()
    val eFIFTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eFIFTEENTH()
    val eSIXTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eSIXTEENTH()
    val eSEVENTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eSEVENTEENTH()
    val eEIGHTEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eEIGHTEENTH()
    val eNINETEENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eNINETEENTH()
    val eTWENTIETH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTIETH()
    val eTWENTYFIRST: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYFIRST()
    val eTWENTYSECOND: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYSECOND()
    val eTWENTYTHIRD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYTHIRD()
    val eTWENTYFOURTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYFOURTH()
    val eTWENTYFIFTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYFIFTH()
    val eTWENTYSIXTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYSIXTH()
    val eTWENTYSEVENTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYSEVENTH()
    val eTWENTYEIGHTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYEIGHTH()
    val eTWENTYNINTH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTWENTYNINTH()
    val eTHIRTIETH: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eTHIRTIETH()
    val eGEARSRATIO_COUNT: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearEnum_eGEARSRATIO_COUNT()
}

object PxVehicleUpdateModeEnum {
    val eVELOCITY_CHANGE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleUpdateModeEnum_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleUpdateModeEnum_eACCELERATION()
}

object PxVehicleWheelsSimFlagEnum {
    val eLIMIT_SUSPENSION_EXPANSION_VELOCITY: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleWheelsSimFlagEnum_eLIMIT_SUSPENSION_EXPANSION_VELOCITY()
}

object VehicleSurfaceTypeMask {
    val DRIVABLE_SURFACE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE()
    val UNDRIVABLE_SURFACE: Int get() = PhysXJsLoader.physXJs._emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE()
}