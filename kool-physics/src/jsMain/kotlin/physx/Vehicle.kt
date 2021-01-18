@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName")

package physx

external interface PxVehicleTopLevelFunctions {
    fun InitVehicleSDK(physics: PxPhysics): Boolean
    fun PxVehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxVec3, centreOfMass: PxVec3, totalMass: Float, gravityDirection: Int, sprungMasses: PxRealPtr)
    fun PxVehicleSuspensionRaycasts(batchQuery: PxBatchQuery, vehicles: Vector_PxVehicleWheels, nbSceneQueryResults: Int, sceneQueryResults: PxRaycastQueryResult)
    fun PxVehicleUpdates(timestep: Float, gravity: PxVec3, vehicleDrivableSurfaceToTireFrictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs,
                         vehicles: Vector_PxVehicleWheels, vehicleWheelQueryResults: PxVehicleWheelQueryResult)
    fun VehicleSetBasisVectors(up: PxVec3, forward: PxVec3)
    fun VehicleSetUpdateMode(vehicleUpdateMode: Int) // physx_PxVehicleUpdateMode

    fun PxVehicleTireData_getFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int): Float
    fun PxVehicleTireData_setFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int, value: Float)
}

external interface PxVehicleAckermannGeometryData {
    var mAccuracy: Float
    var mFrontWidth: Float
    var mRearWidth: Float
    var mAxleSeparation: Float
}

external interface PxVehicleAntiRollBarData {
    var mWheel0: Int
    var mWheel1: Int
    var mStiffness: Float
}

external interface PxVehicleAutoBoxData {
    fun setLatency(latency: Float)
    fun getLatency(): Float
    fun getUpRatios(a: Int): Float          // physx_PxVehicleGear
    fun setUpRatios(a: Int, ratio: Float)   // physx_PxVehicleGear
    fun getDownRatios(a: Int): Float        // physx_PxVehicleGear
    fun setDownRatios(a: Int, ratio: Float) // physx_PxVehicleGear

    fun get_mUpRatios(index: Int): Float
    fun set_mUpRatios(index: Int, value: Float): Float

    fun get_mDownRatios(index: Int): Float
    fun set_mDownRatios(index: Int, value: Float): Float
}

external interface PxVehicleChassisData {
    var mMOI: PxVec3
    var mMass: Float
    var mCMOffset: PxVec3
}

object physx_PxVehicleClutchAccuracyMode {
    val eESTIMATE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleClutchAccuracyMode_eESTIMATE()
    val eBEST_POSSIBLE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleClutchAccuracyMode_eBEST_POSSIBLE()
}

external interface PxVehicleClutchData {
    var mStrength: Float
    var mAccuracyMode: Int
    var mEstimateIterations: Int
}

external interface PxVehicleDifferential4WData {
    var mFrontRearSplit: Float
    var mFrontLeftRightSplit: Float
    var mRearLeftRightSplit: Float
    var mCentreBias: Float
    var mFrontBias: Float
    var mRearBias: Float
    var mType: Int // physx_PxVehicleDifferential4WData
}

object physx_PxVehicleDifferential4WData {
    val eDIFF_TYPE_LS_4WD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_LS_4WD()
    val eDIFF_TYPE_LS_FRONTWD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_LS_FRONTWD()
    val eDIFF_TYPE_LS_REARWD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_LS_REARWD()
    val eDIFF_TYPE_OPEN_4WD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_OPEN_4WD()
    val eDIFF_TYPE_OPEN_FRONTWD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_OPEN_FRONTWD()
    val eDIFF_TYPE_OPEN_REARWD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eDIFF_TYPE_OPEN_REARWD()
    val eMAX_NB_DIFF_TYPES: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDifferential4WData_eMAX_NB_DIFF_TYPES()
}

external interface PxVehicleDrivableSurfaceToTireFrictionPairs {
    fun setup(nbTireTypes: Int, nbSurfaceTypes: Int, drivableSurfaceMaterials: Array<PxMaterial>, drivableSurfaceTypes: PxVehicleDrivableSurfaceType)
    fun release()
    fun setTypePairFriction(surfaceType: Int, tireType: Int, value: Float)
    fun getTypePairFriction(surfaceType: Int, tireType: Int): Float
    fun getMaxNbSurfaceTypes(): Int
    fun getMaxNbTireTypes(): Int
}

external interface PxVehicleDrivableSurfaceType {
    var mType: Int
}

external interface PxVehicleDrive : PxVehicleWheels {
    val mDriveDynData: PxVehicleDriveDynData
}

external interface PxVehicleDrive4W : PxVehicleDrive {
    val mDriveSimData: PxVehicleDriveSimData4W

    fun free()
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData, driveData: PxVehicleDriveSimData4W, nbNonDrivenWheels: Int)
    fun setToRestState()
}

object physx_PxVehicleDrive4WControl {
    val eANALOG_INPUT_ACCEL: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eANALOG_INPUT_ACCEL()
    val eANALOG_INPUT_BRAKE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eANALOG_INPUT_BRAKE()
    val eANALOG_INPUT_HANDBRAKE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eANALOG_INPUT_HANDBRAKE()
    val eANALOG_INPUT_STEER_LEFT: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eANALOG_INPUT_STEER_LEFT()
    val eANALOG_INPUT_STEER_RIGHT: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eANALOG_INPUT_STEER_RIGHT()
    val eMAX_NB_DRIVE4W_ANALOG_INPUTS: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleDrive4WControl_eMAX_NB_DRIVE4W_ANALOG_INPUTS()
}

external interface PxVehicleDriveDynData {
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
    fun toggleAutoGears()
    fun startGearChange(targetGear: Int)
    fun forceGearChange(targetGear: Int)
    fun getNbAnalogInput(): Int
    fun setGearChange(gearChange: Int)
    fun getGearChange(): Int
}

external interface PxVehicleDriveSimData {
    fun getEngineData(): PxVehicleEngineData
    fun setEngineData(engine: PxVehicleEngineData)
    fun getGearsData(): PxVehicleGearsData
    fun setGearsData(gears: PxVehicleGearsData)
    fun getClutchData(): PxVehicleClutchData
    fun setClutchData(clutch:  PxVehicleClutchData)
    fun getAutoBoxData(): PxVehicleAutoBoxData
    fun setAutoBoxData(clutch:  PxVehicleAutoBoxData)
}

external interface PxVehicleDriveSimData4W : PxVehicleDriveSimData {
    fun getDiffData(): PxVehicleDifferential4WData
    fun getAckermannGeometryData(): PxVehicleAckermannGeometryData
    fun setDiffData(diff:  PxVehicleDifferential4WData)
    fun setAckermannGeometryData(ackermannData: PxVehicleAckermannGeometryData)
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

external interface PxEngineTorqueLookupTable {
    val mNbDataPairs: Int
    fun get_mDataPairs(index: Int): Float
    fun set_mDataPairs(index: Int, value: Float): Float

    fun addPair(x: Float, y: Float)
    fun getYVal(x: Float): Float
    fun getNbDataPairs(): Int
    fun clear()
    fun getX(i: Int): Float
    fun getY(i: Int): Float
}

external interface PxVehicleGearsData {
    var mFinalRatio: Float
    var mNbRatios: Int
    var mSwitchTime: Float

    fun getGearRatio(a: Int): Float         // physx_PxVehicleGear
    fun setGearRatio(a: Int, ratio: Float)  // physx_PxVehicleGear
}

object physx_PxVehicleGear {
    val eREVERSE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eREVERSE()
    val eNEUTRAL: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eNEUTRAL()
    val eFIRST: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eFIRST()
    val eSECOND: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eSECOND()
    val eTHIRD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTHIRD()
    val eFOURTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eFOURTH()
    val eFIFTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eFIFTH()
    val eSIXTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eSIXTH()
    val eSEVENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eSEVENTH()
    val eEIGHTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eEIGHTH()
    val eNINTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eNINTH()
    val eTENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTENTH()
    val eELEVENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eELEVENTH()
    val eTWELFTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWELFTH()
    val eTHIRTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTHIRTEENTH()
    val eFOURTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eFOURTEENTH()
    val eFIFTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eFIFTEENTH()
    val eSIXTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eSIXTEENTH()
    val eSEVENTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eSEVENTEENTH()
    val eEIGHTEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eEIGHTEENTH()
    val eNINETEENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eNINETEENTH()
    val eTWENTIETH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTIETH()
    val eTWENTYFIRST: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYFIRST()
    val eTWENTYSECOND: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYSECOND()
    val eTWENTYTHIRD: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYTHIRD()
    val eTWENTYFOURTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYFOURTH()
    val eTWENTYFIFTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYFIFTH()
    val eTWENTYSIXTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYSIXTH()
    val eTWENTYSEVENTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYSEVENTH()
    val eTWENTYEIGHTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYEIGHTH()
    val eTWENTYNINTH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTWENTYNINTH()
    val eTHIRTIETH: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eTHIRTIETH()
    val eGEARSRATIO_COUNT: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleGear_eGEARSRATIO_COUNT()
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

external interface PxVehicleTireData {
    var mLatStiffX: Float
    var mLatStiffY: Float
    var mLongitudinalStiffnessPerUnitGravity: Float
    var mCamberStiffnessPerUnitGravity: Float
    //attribute float[][] mFrictionVsSlipGraph;     // 2-dimensional array is not supported by WebIDL -> use top-level getter / setter functions
    var mType: Int
}

external interface PxVehicleTireLoadFilterData {
    var mMinNormalisedLoad: Float
    var mMinFilteredNormalisedLoad: Float
    var mMaxNormalisedLoad: Float
    var mMaxFilteredNormalisedLoad: Float
    fun getDenominator(): Float
}

object physx_PxVehicleUpdateMode {
    val eVELOCITY_CHANGE: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleUpdateMode_eVELOCITY_CHANGE()
    val eACCELERATION: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleUpdateMode_eACCELERATION()
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

external interface PxVehicleWheelQueryResult {
    var wheelQueryResults: PxWheelQueryResult
    var nbWheelQueryResults: Int
}

external interface PxVehicleWheels : PxBase {
    fun getVehicleType(): Int
    fun getRigidDynamicActor(): PxRigidDynamic
    fun computeForwardSpeed(): Float
    fun computeSidewaysSpeed(): Float
    fun getNbNonDrivenWheels(): Int
}

external interface PxVehicleWheelsPtr

external interface PxVehicleWheelsDynData {
    fun setToRestState()
    fun setWheelRotationSpeed(wheelIdx: Int, speed: Float)
    fun getWheelRotationSpeed(wheelIdx: Int): Float
    fun setWheelRotationAngle(wheelIdx: Int, angle: Float)
    fun getWheelRotationAngle(wheelIdx: Int)
    fun copy(src: PxVehicleWheelsDynData, srcWheel: Int, trgWheel: Int)
    fun getNbWheelRotationSpeed(): Int
    fun getNbWheelRotationAngle(): Int
}

external interface PxVehicleWheelsSimData {
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

object physx_PxVehicleWheelsSimFlag {
    val eLIMIT_SUSPENSION_EXPANSION_VELOCITY: Int get() = PhysX.physx._emscripten_enum_physx_PxVehicleWheelsSimFlag_eLIMIT_SUSPENSION_EXPANSION_VELOCITY()
}

external interface PxVehicleWheelsSimFlags : PxFlags

external interface PxWheelQueryResult {
    val suspLineStart: PxVec3
    val suspLineDir: PxVec3
    val suspLineLength: Float
    val isInAir: Boolean
    val tireContactActor: PxActor
    val tireContactShape: PxShape
    val tireSurfaceMaterial: PxMaterial
    val tireSurfaceType: Int
    val tireContactPoint: PxVec3
    val tireContactNormal: PxVec3
    val tireFriction: Float
    val suspJounce: Float
    val suspSpringForce: Float
    val tireLongitudinalDir: PxVec3
    val tireLateralDir: PxVec3
    val longitudinalSlip: Float
    val lateralSlip: Float
    val steerAngle: Float
    val localPose: PxTransform
}

object VehicleSurfaceTypeMask {
    val DRIVABLE_SURFACE: Int get() = PhysX.physx._emscripten_enum_VehicleSurfaceTypeMask_DRIVABLE_SURFACE()
    val UNDRIVABLE_SURFACE: Int get() = PhysX.physx._emscripten_enum_VehicleSurfaceTypeMask_UNDRIVABLE_SURFACE()
}

external interface Vector_PxVehicleDrivableSurfaceType : StdVector<PxVehicleDrivableSurfaceType>

external interface Vector_PxWheelQueryResult : StdVector<PxWheelQueryResult>

external interface Vector_PxVehicleWheels : StdVector<PxVehicleWheels>
