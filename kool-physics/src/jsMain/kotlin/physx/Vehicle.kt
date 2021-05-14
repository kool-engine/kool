/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxVehicleTopLevelFunctions {
    /**
     * @param physics WebIDL type: [PxPhysics] (Ref)
     * @return WebIDL type: boolean
     */
    fun InitVehicleSDK(physics: PxPhysics): Boolean

    /**
     * @param nbSprungMasses        WebIDL type: unsigned long
     * @param sprungMassCoordinates WebIDL type: [PxVec3] (Const)
     * @param centreOfMass          WebIDL type: [PxVec3] (Const, Ref)
     * @param totalMass             WebIDL type: float
     * @param gravityDirection      WebIDL type: unsigned long
     * @param sprungMasses          WebIDL type: [PxRealPtr] (Ref)
     */
    fun PxVehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxVec3, centreOfMass: PxVec3, totalMass: Float, gravityDirection: Int, sprungMasses: PxRealPtr)

    /**
     * @param batchQuery          WebIDL type: [PxBatchQuery]
     * @param vehicles            WebIDL type: [Vector_PxVehicleWheels] (Ref)
     * @param nbSceneQueryResults WebIDL type: unsigned long
     * @param sceneQueryResults   WebIDL type: [PxRaycastQueryResult]
     */
    fun PxVehicleSuspensionRaycasts(batchQuery: PxBatchQuery, vehicles: Vector_PxVehicleWheels, nbSceneQueryResults: Int, sceneQueryResults: PxRaycastQueryResult)

    /**
     * @param timestep                                  WebIDL type: float
     * @param gravity                                   WebIDL type: [PxVec3] (Const, Ref)
     * @param vehicleDrivableSurfaceToTireFrictionPairs WebIDL type: [PxVehicleDrivableSurfaceToTireFrictionPairs] (Const, Ref)
     * @param vehicles                                  WebIDL type: [Vector_PxVehicleWheels] (Ref)
     * @param vehicleWheelQueryResults                  WebIDL type: [PxVehicleWheelQueryResult]
     */
    fun PxVehicleUpdates(timestep: Float, gravity: PxVec3, vehicleDrivableSurfaceToTireFrictionPairs: PxVehicleDrivableSurfaceToTireFrictionPairs, vehicles: Vector_PxVehicleWheels, vehicleWheelQueryResults: PxVehicleWheelQueryResult)

    /**
     * @param up      WebIDL type: [PxVec3] (Const, Ref)
     * @param forward WebIDL type: [PxVec3] (Const, Ref)
     */
    fun VehicleSetBasisVectors(up: PxVec3, forward: PxVec3)

    /**
     * @param vehicleUpdateMode WebIDL type: [PxVehicleUpdateModeEnum] (enum)
     */
    fun VehicleSetUpdateMode(vehicleUpdateMode: Int)

    /**
     * @param tireData WebIDL type: [PxVehicleTireData]
     * @param m        WebIDL type: unsigned long
     * @param n        WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun PxVehicleTireData_getFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int): Float

    /**
     * @param tireData WebIDL type: [PxVehicleTireData]
     * @param m        WebIDL type: unsigned long
     * @param n        WebIDL type: unsigned long
     * @param value    WebIDL type: float
     */
    fun PxVehicleTireData_setFrictionVsSlipGraph(tireData: PxVehicleTireData, m: Int, n: Int, value: Float)

    /**
     * @return WebIDL type: [PxBatchQueryPreFilterShader] (Value)
     */
    fun DefaultWheelSceneQueryPreFilterBlocking(): PxBatchQueryPreFilterShader

    /**
     * @return WebIDL type: [PxBatchQueryPostFilterShader] (Value)
     */
    fun DefaultWheelSceneQueryPostFilterBlocking(): PxBatchQueryPostFilterShader

}

fun PxVehicleTopLevelFunctions.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAckermannGeometryData {
    /**
     * WebIDL type: float
     */
    var mAccuracy: Float
    /**
     * WebIDL type: float
     */
    var mFrontWidth: Float
    /**
     * WebIDL type: float
     */
    var mRearWidth: Float
    /**
     * WebIDL type: float
     */
    var mAxleSeparation: Float
}

fun PxVehicleAckermannGeometryData(): PxVehicleAckermannGeometryData {
    fun _PxVehicleAckermannGeometryData(_module: dynamic) = js("new _module.PxVehicleAckermannGeometryData()")
    return _PxVehicleAckermannGeometryData(PhysXJsLoader.physXJs)
}

fun PxVehicleAckermannGeometryData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAntiRollBarData {
    /**
     * WebIDL type: unsigned long
     */
    var mWheel0: Int
    /**
     * WebIDL type: unsigned long
     */
    var mWheel1: Int
    /**
     * WebIDL type: float
     */
    var mStiffness: Float
}

fun PxVehicleAntiRollBarData(): PxVehicleAntiRollBarData {
    fun _PxVehicleAntiRollBarData(_module: dynamic) = js("new _module.PxVehicleAntiRollBarData()")
    return _PxVehicleAntiRollBarData(PhysXJsLoader.physXJs)
}

fun PxVehicleAntiRollBarData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAutoBoxData {
    /**
     * WebIDL type: float
     */
    var mUpRatios: Array<Float>
    /**
     * WebIDL type: float
     */
    var mDownRatios: Array<Float>

    /**
     * @param latency WebIDL type: float
     */
    fun setLatency(latency: Float)

    /**
     * @return WebIDL type: float
     */
    fun getLatency(): Float

    /**
     * @param a WebIDL type: [PxVehicleGearEnum] (enum)
     * @return WebIDL type: float
     */
    fun getUpRatios(a: Int): Float

    /**
     * @param a     WebIDL type: [PxVehicleGearEnum] (enum)
     * @param ratio WebIDL type: float
     */
    fun setUpRatios(a: Int, ratio: Float)

    /**
     * @param a WebIDL type: [PxVehicleGearEnum] (enum)
     * @return WebIDL type: float
     */
    fun getDownRatios(a: Int): Float

    /**
     * @param a     WebIDL type: [PxVehicleGearEnum] (enum)
     * @param ratio WebIDL type: float
     */
    fun setDownRatios(a: Int, ratio: Float)

}

fun PxVehicleAutoBoxData(): PxVehicleAutoBoxData {
    fun _PxVehicleAutoBoxData(_module: dynamic) = js("new _module.PxVehicleAutoBoxData()")
    return _PxVehicleAutoBoxData(PhysXJsLoader.physXJs)
}

fun PxVehicleAutoBoxData.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxVehicleAutoBoxData.latency
    get() = getLatency()
    set(value) { setLatency(value) }

external interface PxVehicleChassisData {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var mMOI: PxVec3
    /**
     * WebIDL type: float
     */
    var mMass: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var mCMOffset: PxVec3
}

fun PxVehicleChassisData(): PxVehicleChassisData {
    fun _PxVehicleChassisData(_module: dynamic) = js("new _module.PxVehicleChassisData()")
    return _PxVehicleChassisData(PhysXJsLoader.physXJs)
}

fun PxVehicleChassisData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleClutchData {
    /**
     * WebIDL type: float
     */
    var mStrength: Float
    /**
     * WebIDL type: [PxVehicleClutchAccuracyModeEnum] (enum)
     */
    var mAccuracyMode: Int
    /**
     * WebIDL type: unsigned long
     */
    var mEstimateIterations: Int
}

fun PxVehicleClutchData(): PxVehicleClutchData {
    fun _PxVehicleClutchData(_module: dynamic) = js("new _module.PxVehicleClutchData()")
    return _PxVehicleClutchData(PhysXJsLoader.physXJs)
}

fun PxVehicleClutchData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleDifferential4WData {
    /**
     * WebIDL type: float
     */
    var mFrontRearSplit: Float
    /**
     * WebIDL type: float
     */
    var mFrontLeftRightSplit: Float
    /**
     * WebIDL type: float
     */
    var mRearLeftRightSplit: Float
    /**
     * WebIDL type: float
     */
    var mCentreBias: Float
    /**
     * WebIDL type: float
     */
    var mFrontBias: Float
    /**
     * WebIDL type: float
     */
    var mRearBias: Float
    /**
     * WebIDL type: [PxVehicleDifferential4WDataEnum] (enum)
     */
    var mType: Int
}

fun PxVehicleDifferential4WData(): PxVehicleDifferential4WData {
    fun _PxVehicleDifferential4WData(_module: dynamic) = js("new _module.PxVehicleDifferential4WData()")
    return _PxVehicleDifferential4WData(PhysXJsLoader.physXJs)
}

fun PxVehicleDifferential4WData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleDifferentialNWData {
    /**
     * @param wheelId     WebIDL type: unsigned long
     * @param drivenState WebIDL type: boolean
     */
    fun setDrivenWheel(wheelId: Int, drivenState: Boolean)

    /**
     * @param wheelId WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun getIsDrivenWheel(wheelId: Int): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getDrivenWheelStatus(): Int

    /**
     * @param status WebIDL type: unsigned long
     */
    fun setDrivenWheelStatus(status: Int)

}

fun PxVehicleDifferentialNWData(): PxVehicleDifferentialNWData {
    fun _PxVehicleDifferentialNWData(_module: dynamic) = js("new _module.PxVehicleDifferentialNWData()")
    return _PxVehicleDifferentialNWData(PhysXJsLoader.physXJs)
}

fun PxVehicleDifferentialNWData.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxVehicleDifferentialNWData.drivenWheelStatus
    get() = getDrivenWheelStatus()
    set(value) { setDrivenWheelStatus(value) }

external interface PxVehicleDrivableSurfaceToTireFrictionPairs {
    /**
     * @param maxNbTireTypes    WebIDL type: unsigned long
     * @param maxNbSurfaceTypes WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleDrivableSurfaceToTireFrictionPairs]
     */
    fun allocate(maxNbTireTypes: Int, maxNbSurfaceTypes: Int): PxVehicleDrivableSurfaceToTireFrictionPairs

    /**
     * @param nbTireTypes              WebIDL type: unsigned long
     * @param nbSurfaceTypes           WebIDL type: unsigned long
     * @param drivableSurfaceMaterials WebIDL type: [PxMaterialConstPtr]
     * @param drivableSurfaceTypes     WebIDL type: [PxVehicleDrivableSurfaceType] (Const)
     */
    fun setup(nbTireTypes: Int, nbSurfaceTypes: Int, drivableSurfaceMaterials: PxMaterialConstPtr, drivableSurfaceTypes: PxVehicleDrivableSurfaceType)

    fun release()

    /**
     * @param surfaceType WebIDL type: unsigned long
     * @param tireType    WebIDL type: unsigned long
     * @param value       WebIDL type: float
     */
    fun setTypePairFriction(surfaceType: Int, tireType: Int, value: Float)

    /**
     * @param surfaceType WebIDL type: unsigned long
     * @param tireType    WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getTypePairFriction(surfaceType: Int, tireType: Int): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbSurfaceTypes(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getMaxNbTireTypes(): Int

}

val PxVehicleDrivableSurfaceToTireFrictionPairs.maxNbSurfaceTypes
    get() = getMaxNbSurfaceTypes()
val PxVehicleDrivableSurfaceToTireFrictionPairs.maxNbTireTypes
    get() = getMaxNbTireTypes()

external interface PxVehicleDrivableSurfaceType {
    /**
     * WebIDL type: unsigned long
     */
    var mType: Int
}

fun PxVehicleDrivableSurfaceType(): PxVehicleDrivableSurfaceType {
    fun _PxVehicleDrivableSurfaceType(_module: dynamic) = js("new _module.PxVehicleDrivableSurfaceType()")
    return _PxVehicleDrivableSurfaceType(PhysXJsLoader.physXJs)
}

fun PxVehicleDrivableSurfaceType.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleDrive : PxVehicleWheels {
    /**
     * WebIDL type: [PxVehicleDriveDynData] (Value)
     */
    var mDriveDynData: PxVehicleDriveDynData
}

external interface PxVehicleDrive4W : PxVehicleDrive {
    /**
     * WebIDL type: [PxVehicleDriveSimData4W] (Value)
     */
    var mDriveSimData: PxVehicleDriveSimData4W

    /**
     * @param nbWheels WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleDrive4W]
     */
    fun allocate(nbWheels: Int): PxVehicleDrive4W

    fun free()

    /**
     * @param physics           WebIDL type: [PxPhysics]
     * @param vehActor          WebIDL type: [PxRigidDynamic]
     * @param wheelsData        WebIDL type: [PxVehicleWheelsSimData] (Const, Ref)
     * @param driveData         WebIDL type: [PxVehicleDriveSimData4W] (Const, Ref)
     * @param nbNonDrivenWheels WebIDL type: unsigned long
     */
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData, driveData: PxVehicleDriveSimData4W, nbNonDrivenWheels: Int)

    fun setToRestState()

}

external interface PxVehicleDriveDynData {
    /**
     * WebIDL type: float
     */
    var mControlAnalogVals: Array<Float>
    /**
     * WebIDL type: boolean
     */
    var mUseAutoGears: Boolean
    /**
     * WebIDL type: boolean
     */
    var mGearUpPressed: Boolean
    /**
     * WebIDL type: boolean
     */
    var mGearDownPressed: Boolean
    /**
     * WebIDL type: unsigned long
     */
    var mCurrentGear: Int
    /**
     * WebIDL type: unsigned long
     */
    var mTargetGear: Int
    /**
     * WebIDL type: float
     */
    var mEnginespeed: Float
    /**
     * WebIDL type: float
     */
    var mGearSwitchTime: Float
    /**
     * WebIDL type: float
     */
    var mAutoBoxSwitchTime: Float

    fun setToRestState()

    /**
     * @param type      WebIDL type: unsigned long
     * @param analogVal WebIDL type: float
     */
    fun setAnalogInput(type: Int, analogVal: Float)

    /**
     * @param type WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getAnalogInput(type: Int): Float

    /**
     * @param digitalVal WebIDL type: boolean
     */
    fun setGearUp(digitalVal: Boolean)

    /**
     * @param digitalVal WebIDL type: boolean
     */
    fun setGearDown(digitalVal: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun getGearUp(): Boolean

    /**
     * @return WebIDL type: boolean
     */
    fun getGearDown(): Boolean

    /**
     * @param useAutoGears WebIDL type: boolean
     */
    fun setUseAutoGears(useAutoGears: Boolean)

    /**
     * @return WebIDL type: boolean
     */
    fun getUseAutoGears(): Boolean

    fun toggleAutoGears()

    /**
     * @param currentGear WebIDL type: unsigned long
     */
    fun setCurrentGear(currentGear: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getCurrentGear(): Int

    /**
     * @param targetGear WebIDL type: unsigned long
     */
    fun setTargetGear(targetGear: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getTargetGear(): Int

    /**
     * @param targetGear WebIDL type: unsigned long
     */
    fun startGearChange(targetGear: Int)

    /**
     * @param targetGear WebIDL type: unsigned long
     */
    fun forceGearChange(targetGear: Int)

    /**
     * @param speed WebIDL type: float
     */
    fun setEngineRotationSpeed(speed: Float)

    /**
     * @return WebIDL type: float
     */
    fun getEngineRotationSpeed(): Float

    /**
     * @return WebIDL type: float
     */
    fun getGearSwitchTime(): Float

    /**
     * @return WebIDL type: float
     */
    fun getAutoBoxSwitchTime(): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAnalogInput(): Int

    /**
     * @param gearChange WebIDL type: unsigned long
     */
    fun setGearChange(gearChange: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getGearChange(): Int

    /**
     * @param switchTime WebIDL type: float
     */
    fun setGearSwitchTime(switchTime: Float)

    /**
     * @param autoBoxSwitchTime WebIDL type: float
     */
    fun setAutoBoxSwitchTime(autoBoxSwitchTime: Float)

}

fun PxVehicleDriveDynData.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleDriveDynData.targetGear
    get() = getTargetGear()
val PxVehicleDriveDynData.nbAnalogInput
    get() = getNbAnalogInput()

var PxVehicleDriveDynData.gearUp
    get() = getGearUp()
    set(value) { setGearUp(value) }
var PxVehicleDriveDynData.gearDown
    get() = getGearDown()
    set(value) { setGearDown(value) }
var PxVehicleDriveDynData.useAutoGears
    get() = getUseAutoGears()
    set(value) { setUseAutoGears(value) }
var PxVehicleDriveDynData.currentGear
    get() = getCurrentGear()
    set(value) { setCurrentGear(value) }
var PxVehicleDriveDynData.engineRotationSpeed
    get() = getEngineRotationSpeed()
    set(value) { setEngineRotationSpeed(value) }
var PxVehicleDriveDynData.gearSwitchTime
    get() = getGearSwitchTime()
    set(value) { setGearSwitchTime(value) }
var PxVehicleDriveDynData.autoBoxSwitchTime
    get() = getAutoBoxSwitchTime()
    set(value) { setAutoBoxSwitchTime(value) }
var PxVehicleDriveDynData.gearChange
    get() = getGearChange()
    set(value) { setGearChange(value) }

external interface PxVehicleDriveNW : PxVehicleDrive {
    /**
     * WebIDL type: [PxVehicleDriveSimDataNW] (Value)
     */
    var mDriveSimData: PxVehicleDriveSimDataNW

    /**
     * @param nbWheels WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleDriveNW]
     */
    fun allocate(nbWheels: Int): PxVehicleDriveNW

    fun free()

    /**
     * @param physics    WebIDL type: [PxPhysics]
     * @param vehActor   WebIDL type: [PxRigidDynamic]
     * @param wheelsData WebIDL type: [PxVehicleWheelsSimData] (Const, Ref)
     * @param driveData  WebIDL type: [PxVehicleDriveSimDataNW] (Const, Ref)
     * @param nbWheels   WebIDL type: unsigned long
     */
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData, driveData: PxVehicleDriveSimDataNW, nbWheels: Int)

    fun setToRestState()

}

external interface PxVehicleDriveSimData {
    /**
     * @return WebIDL type: [PxVehicleEngineData] (Const, Ref)
     */
    fun getEngineData(): PxVehicleEngineData

    /**
     * @param engine WebIDL type: [PxVehicleEngineData] (Const, Ref)
     */
    fun setEngineData(engine: PxVehicleEngineData)

    /**
     * @return WebIDL type: [PxVehicleGearsData] (Const, Ref)
     */
    fun getGearsData(): PxVehicleGearsData

    /**
     * @param gears WebIDL type: [PxVehicleGearsData] (Const, Ref)
     */
    fun setGearsData(gears: PxVehicleGearsData)

    /**
     * @return WebIDL type: [PxVehicleClutchData] (Const, Ref)
     */
    fun getClutchData(): PxVehicleClutchData

    /**
     * @param clutch WebIDL type: [PxVehicleClutchData] (Const, Ref)
     */
    fun setClutchData(clutch: PxVehicleClutchData)

    /**
     * @return WebIDL type: [PxVehicleAutoBoxData] (Const, Ref)
     */
    fun getAutoBoxData(): PxVehicleAutoBoxData

    /**
     * @param clutch WebIDL type: [PxVehicleAutoBoxData] (Const, Ref)
     */
    fun setAutoBoxData(clutch: PxVehicleAutoBoxData)

}

fun PxVehicleDriveSimData(): PxVehicleDriveSimData {
    fun _PxVehicleDriveSimData(_module: dynamic) = js("new _module.PxVehicleDriveSimData()")
    return _PxVehicleDriveSimData(PhysXJsLoader.physXJs)
}

fun PxVehicleDriveSimData.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxVehicleDriveSimData.engineData
    get() = getEngineData()
    set(value) { setEngineData(value) }
var PxVehicleDriveSimData.gearsData
    get() = getGearsData()
    set(value) { setGearsData(value) }
var PxVehicleDriveSimData.clutchData
    get() = getClutchData()
    set(value) { setClutchData(value) }
var PxVehicleDriveSimData.autoBoxData
    get() = getAutoBoxData()
    set(value) { setAutoBoxData(value) }

external interface PxVehicleDriveSimData4W : PxVehicleDriveSimData {
    /**
     * @return WebIDL type: [PxVehicleDifferential4WData] (Const, Ref)
     */
    fun getDiffData(): PxVehicleDifferential4WData

    /**
     * @return WebIDL type: [PxVehicleAckermannGeometryData] (Const, Ref)
     */
    fun getAckermannGeometryData(): PxVehicleAckermannGeometryData

    /**
     * @param diff WebIDL type: [PxVehicleDifferential4WData] (Const, Ref)
     */
    fun setDiffData(diff: PxVehicleDifferential4WData)

    /**
     * @param ackermannData WebIDL type: [PxVehicleAckermannGeometryData] (Const, Ref)
     */
    fun setAckermannGeometryData(ackermannData: PxVehicleAckermannGeometryData)

}

fun PxVehicleDriveSimData4W(): PxVehicleDriveSimData4W {
    fun _PxVehicleDriveSimData4W(_module: dynamic) = js("new _module.PxVehicleDriveSimData4W()")
    return _PxVehicleDriveSimData4W(PhysXJsLoader.physXJs)
}

fun PxVehicleDriveSimData4W.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxVehicleDriveSimData4W.diffData
    get() = getDiffData()
    set(value) { setDiffData(value) }
var PxVehicleDriveSimData4W.ackermannGeometryData
    get() = getAckermannGeometryData()
    set(value) { setAckermannGeometryData(value) }

external interface PxVehicleDriveSimDataNW : PxVehicleDriveSimData {
    /**
     * @return WebIDL type: [PxVehicleDifferentialNWData] (Const, Ref)
     */
    fun getDiffData(): PxVehicleDifferentialNWData

    /**
     * @param diff WebIDL type: [PxVehicleDifferentialNWData] (Const, Ref)
     */
    fun setDiffData(diff: PxVehicleDifferentialNWData)

}

fun PxVehicleDriveSimDataNW(): PxVehicleDriveSimDataNW {
    fun _PxVehicleDriveSimDataNW(_module: dynamic) = js("new _module.PxVehicleDriveSimDataNW()")
    return _PxVehicleDriveSimDataNW(PhysXJsLoader.physXJs)
}

fun PxVehicleDriveSimDataNW.destroy() {
    PhysXJsLoader.destroy(this)
}

var PxVehicleDriveSimDataNW.diffData
    get() = getDiffData()
    set(value) { setDiffData(value) }

external interface PxVehicleDriveTank : PxVehicleDrive {
    /**
     * WebIDL type: [PxVehicleDriveSimData] (Value)
     */
    var mDriveSimData: PxVehicleDriveSimData

    /**
     * @param nbWheels WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleDriveTank]
     */
    fun allocate(nbWheels: Int): PxVehicleDriveTank

    fun free()

    /**
     * @param physics        WebIDL type: [PxPhysics]
     * @param vehActor       WebIDL type: [PxRigidDynamic]
     * @param wheelsData     WebIDL type: [PxVehicleWheelsSimData] (Const, Ref)
     * @param driveData      WebIDL type: [PxVehicleDriveSimData] (Const, Ref)
     * @param nbDrivenWheels WebIDL type: unsigned long
     */
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData, driveData: PxVehicleDriveSimData, nbDrivenWheels: Int)

    /**
     * @param driveModel WebIDL type: [PxVehicleDriveTankControlModelEnum] (enum)
     */
    fun setDriveModel(driveModel: Int)

    /**
     * @return WebIDL type: [PxVehicleDriveTankControlModelEnum] (enum)
     */
    fun getDriveModel(): Int

    fun setToRestState()

}

var PxVehicleDriveTank.driveModel
    get() = getDriveModel()
    set(value) { setDriveModel(value) }

external interface PxVehicleEngineData {
    /**
     * WebIDL type: [PxEngineTorqueLookupTable] (Value)
     */
    var mTorqueCurve: PxEngineTorqueLookupTable
    /**
     * WebIDL type: float
     */
    var mMOI: Float
    /**
     * WebIDL type: float
     */
    var mPeakTorque: Float
    /**
     * WebIDL type: float
     */
    var mMaxOmega: Float
    /**
     * WebIDL type: float
     */
    var mDampingRateFullThrottle: Float
    /**
     * WebIDL type: float
     */
    var mDampingRateZeroThrottleClutchEngaged: Float
    /**
     * WebIDL type: float
     */
    var mDampingRateZeroThrottleClutchDisengaged: Float
}

fun PxVehicleEngineData(): PxVehicleEngineData {
    fun _PxVehicleEngineData(_module: dynamic) = js("new _module.PxVehicleEngineData()")
    return _PxVehicleEngineData(PhysXJsLoader.physXJs)
}

fun PxVehicleEngineData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxEngineTorqueLookupTable {
    /**
     * WebIDL type: float
     */
    var mDataPairs: Array<Float>
    /**
     * WebIDL type: unsigned long
     */
    var mNbDataPairs: Int

    /**
     * @param x WebIDL type: float
     * @param y WebIDL type: float
     */
    fun addPair(x: Float, y: Float)

    /**
     * @param x WebIDL type: float
     * @return WebIDL type: float
     */
    fun getYVal(x: Float): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbDataPairs(): Int

    fun clear()

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getX(i: Int): Float

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getY(i: Int): Float

}

fun PxEngineTorqueLookupTable(): PxEngineTorqueLookupTable {
    fun _PxEngineTorqueLookupTable(_module: dynamic) = js("new _module.PxEngineTorqueLookupTable()")
    return _PxEngineTorqueLookupTable(PhysXJsLoader.physXJs)
}

fun PxEngineTorqueLookupTable.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxEngineTorqueLookupTable.nbDataPairs
    get() = getNbDataPairs()

external interface PxVehicleGearsData {
    /**
     * WebIDL type: float
     */
    var mRatios: Array<Float>
    /**
     * WebIDL type: float
     */
    var mFinalRatio: Float
    /**
     * WebIDL type: unsigned long
     */
    var mNbRatios: Int
    /**
     * WebIDL type: float
     */
    var mSwitchTime: Float

    /**
     * @param a WebIDL type: [PxVehicleGearEnum] (enum)
     * @return WebIDL type: float
     */
    fun getGearRatio(a: Int): Float

    /**
     * @param a     WebIDL type: [PxVehicleGearEnum] (enum)
     * @param ratio WebIDL type: float
     */
    fun setGearRatio(a: Int, ratio: Float)

}

fun PxVehicleGearsData(): PxVehicleGearsData {
    fun _PxVehicleGearsData(_module: dynamic) = js("new _module.PxVehicleGearsData()")
    return _PxVehicleGearsData(PhysXJsLoader.physXJs)
}

fun PxVehicleGearsData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleNoDrive : PxVehicleWheels {
    /**
     * @param nbWheels WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleNoDrive]
     */
    fun allocate(nbWheels: Int): PxVehicleNoDrive

    fun free()

    /**
     * @param physics    WebIDL type: [PxPhysics]
     * @param vehActor   WebIDL type: [PxRigidDynamic]
     * @param wheelsData WebIDL type: [PxVehicleWheelsSimData] (Const, Ref)
     */
    fun setup(physics: PxPhysics, vehActor: PxRigidDynamic, wheelsData: PxVehicleWheelsSimData)

    fun setToRestState()

    /**
     * @param id          WebIDL type: unsigned long
     * @param brakeTorque WebIDL type: float
     */
    fun setBrakeTorque(id: Int, brakeTorque: Float)

    /**
     * @param id          WebIDL type: unsigned long
     * @param driveTorque WebIDL type: float
     */
    fun setDriveTorque(id: Int, driveTorque: Float)

    /**
     * @param id         WebIDL type: unsigned long
     * @param steerAngle WebIDL type: float
     */
    fun setSteerAngle(id: Int, steerAngle: Float)

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getBrakeTorque(id: Int): Float

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getDriveTorque(id: Int): Float

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getSteerAngle(id: Int): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSteerAngle(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbDriveTorque(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbBrakeTorque(): Int

}

val PxVehicleNoDrive.nbSteerAngle
    get() = getNbSteerAngle()
val PxVehicleNoDrive.nbDriveTorque
    get() = getNbDriveTorque()
val PxVehicleNoDrive.nbBrakeTorque
    get() = getNbBrakeTorque()

external interface PxVehicleSuspensionData {
    /**
     * WebIDL type: float
     */
    var mSpringStrength: Float
    /**
     * WebIDL type: float
     */
    var mSpringDamperRate: Float
    /**
     * WebIDL type: float
     */
    var mMaxCompression: Float
    /**
     * WebIDL type: float
     */
    var mMaxDroop: Float
    /**
     * WebIDL type: float
     */
    var mSprungMass: Float
    /**
     * WebIDL type: float
     */
    var mCamberAtRest: Float
    /**
     * WebIDL type: float
     */
    var mCamberAtMaxCompression: Float
    /**
     * WebIDL type: float
     */
    var mCamberAtMaxDroop: Float

    /**
     * @param newSprungMass WebIDL type: float
     */
    fun setMassAndPreserveNaturalFrequency(newSprungMass: Float)

}

fun PxVehicleSuspensionData(): PxVehicleSuspensionData {
    fun _PxVehicleSuspensionData(_module: dynamic) = js("new _module.PxVehicleSuspensionData()")
    return _PxVehicleSuspensionData(PhysXJsLoader.physXJs)
}

fun PxVehicleSuspensionData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireData {
    /**
     * WebIDL type: float
     */
    var mLatStiffX: Float
    /**
     * WebIDL type: float
     */
    var mLatStiffY: Float
    /**
     * WebIDL type: float
     */
    var mLongitudinalStiffnessPerUnitGravity: Float
    /**
     * WebIDL type: float
     */
    var mCamberStiffnessPerUnitGravity: Float
    /**
     * WebIDL type: unsigned long
     */
    var mType: Int
}

fun PxVehicleTireData(): PxVehicleTireData {
    fun _PxVehicleTireData(_module: dynamic) = js("new _module.PxVehicleTireData()")
    return _PxVehicleTireData(PhysXJsLoader.physXJs)
}

fun PxVehicleTireData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireLoadFilterData {
    /**
     * WebIDL type: float
     */
    var mMinNormalisedLoad: Float
    /**
     * WebIDL type: float
     */
    var mMinFilteredNormalisedLoad: Float
    /**
     * WebIDL type: float
     */
    var mMaxNormalisedLoad: Float
    /**
     * WebIDL type: float
     */
    var mMaxFilteredNormalisedLoad: Float

    /**
     * @return WebIDL type: float
     */
    fun getDenominator(): Float

}

fun PxVehicleTireLoadFilterData(): PxVehicleTireLoadFilterData {
    fun _PxVehicleTireLoadFilterData(_module: dynamic) = js("new _module.PxVehicleTireLoadFilterData()")
    return _PxVehicleTireLoadFilterData(PhysXJsLoader.physXJs)
}

fun PxVehicleTireLoadFilterData.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleTireLoadFilterData.denominator
    get() = getDenominator()

external interface PxVehicleWheelData {
    /**
     * WebIDL type: float
     */
    var mRadius: Float
    /**
     * WebIDL type: float
     */
    var mWidth: Float
    /**
     * WebIDL type: float
     */
    var mMass: Float
    /**
     * WebIDL type: float
     */
    var mMOI: Float
    /**
     * WebIDL type: float
     */
    var mDampingRate: Float
    /**
     * WebIDL type: float
     */
    var mMaxBrakeTorque: Float
    /**
     * WebIDL type: float
     */
    var mMaxHandBrakeTorque: Float
    /**
     * WebIDL type: float
     */
    var mMaxSteer: Float
    /**
     * WebIDL type: float
     */
    var mToeAngle: Float
}

fun PxVehicleWheelData(): PxVehicleWheelData {
    fun _PxVehicleWheelData(_module: dynamic) = js("new _module.PxVehicleWheelData()")
    return _PxVehicleWheelData(PhysXJsLoader.physXJs)
}

fun PxVehicleWheelData.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelQueryResult {
    /**
     * WebIDL type: [PxWheelQueryResult]
     */
    var wheelQueryResults: PxWheelQueryResult
    /**
     * WebIDL type: unsigned long
     */
    var nbWheelQueryResults: Int
}

fun PxVehicleWheelQueryResult(): PxVehicleWheelQueryResult {
    fun _PxVehicleWheelQueryResult(_module: dynamic) = js("new _module.PxVehicleWheelQueryResult()")
    return _PxVehicleWheelQueryResult(PhysXJsLoader.physXJs)
}

fun PxVehicleWheelQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheels : PxBase {
    /**
     * WebIDL type: [PxVehicleWheelsSimData] (Value)
     */
    var mWheelsSimData: PxVehicleWheelsSimData
    /**
     * WebIDL type: [PxVehicleWheelsDynData] (Value)
     */
    var mWheelsDynData: PxVehicleWheelsDynData

    /**
     * @return WebIDL type: unsigned long
     */
    fun getVehicleType(): Int

    /**
     * @return WebIDL type: [PxRigidDynamic]
     */
    fun getRigidDynamicActor(): PxRigidDynamic

    /**
     * @return WebIDL type: float
     */
    fun computeForwardSpeed(): Float

    /**
     * @return WebIDL type: float
     */
    fun computeSidewaysSpeed(): Float

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbNonDrivenWheels(): Int

}

val PxVehicleWheels.vehicleType
    get() = getVehicleType()
val PxVehicleWheels.rigidDynamicActor
    get() = getRigidDynamicActor()
val PxVehicleWheels.nbNonDrivenWheels
    get() = getNbNonDrivenWheels()

external interface PxVehicleWheelsDynData {
    fun setToRestState()

    /**
     * @param wheelIdx WebIDL type: unsigned long
     * @param speed    WebIDL type: float
     */
    fun setWheelRotationSpeed(wheelIdx: Int, speed: Float)

    /**
     * @param wheelIdx WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getWheelRotationSpeed(wheelIdx: Int): Float

    /**
     * @param wheelIdx WebIDL type: unsigned long
     * @param angle    WebIDL type: float
     */
    fun setWheelRotationAngle(wheelIdx: Int, angle: Float)

    /**
     * @param wheelIdx WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getWheelRotationAngle(wheelIdx: Int): Float

    /**
     * @param src      WebIDL type: [PxVehicleWheelsDynData] (Const, Ref)
     * @param srcWheel WebIDL type: unsigned long
     * @param trgWheel WebIDL type: unsigned long
     */
    fun copy(src: PxVehicleWheelsDynData, srcWheel: Int, trgWheel: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelRotationSpeed(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelRotationAngle(): Int

}

fun PxVehicleWheelsDynData.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleWheelsDynData.nbWheelRotationSpeed
    get() = getNbWheelRotationSpeed()
val PxVehicleWheelsDynData.nbWheelRotationAngle
    get() = getNbWheelRotationAngle()

external interface PxVehicleWheelsSimData {
    /**
     * @param nbWheels WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleWheelsSimData]
     */
    fun allocate(nbWheels: Int): PxVehicleWheelsSimData

    /**
     * @param chassisMass WebIDL type: float
     */
    fun setChassisMass(chassisMass: Float)

    fun free()

    /**
     * @param src      WebIDL type: [PxVehicleWheelsSimData] (Const, Ref)
     * @param srcWheel WebIDL type: unsigned long
     * @param trgWheel WebIDL type: unsigned long
     */
    fun copy(src: PxVehicleWheelsSimData, srcWheel: Int, trgWheel: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheels(): Int

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleSuspensionData] (Const, Ref)
     */
    fun getSuspensionData(id: Int): PxVehicleSuspensionData

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleWheelData] (Const, Ref)
     */
    fun getWheelData(id: Int): PxVehicleWheelData

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleTireData] (Const, Ref)
     */
    fun getTireData(id: Int): PxVehicleTireData

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Const, Ref)
     */
    fun getSuspTravelDirection(id: Int): PxVec3

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Const, Ref)
     */
    fun getSuspForceAppPointOffset(id: Int): PxVec3

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Const, Ref)
     */
    fun getTireForceAppPointOffset(id: Int): PxVec3

    /**
     * @param id WebIDL type: unsigned long
     * @return WebIDL type: [PxVec3] (Const, Ref)
     */
    fun getWheelCentreOffset(id: Int): PxVec3

    /**
     * @param wheelId WebIDL type: unsigned long
     * @return WebIDL type: long
     */
    fun getWheelShapeMapping(wheelId: Int): Int

    /**
     * @param suspId WebIDL type: unsigned long
     * @return WebIDL type: [PxFilterData] (Const, Ref)
     */
    fun getSceneQueryFilterData(suspId: Int): PxFilterData

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAntiRollBars(): Int

    /**
     * @param antiRollId WebIDL type: unsigned long
     * @return WebIDL type: [PxVehicleAntiRollBarData] (Const, Ref)
     */
    fun getAntiRollBarData(antiRollId: Int): PxVehicleAntiRollBarData

    /**
     * @return WebIDL type: [PxVehicleTireLoadFilterData] (Const, Ref)
     */
    fun getTireLoadFilterData(): PxVehicleTireLoadFilterData

    /**
     * @param id   WebIDL type: unsigned long
     * @param susp WebIDL type: [PxVehicleSuspensionData] (Const, Ref)
     */
    fun setSuspensionData(id: Int, susp: PxVehicleSuspensionData)

    /**
     * @param id    WebIDL type: unsigned long
     * @param wheel WebIDL type: [PxVehicleWheelData] (Const, Ref)
     */
    fun setWheelData(id: Int, wheel: PxVehicleWheelData)

    /**
     * @param id   WebIDL type: unsigned long
     * @param tire WebIDL type: [PxVehicleTireData] (Const, Ref)
     */
    fun setTireData(id: Int, tire: PxVehicleTireData)

    /**
     * @param id  WebIDL type: unsigned long
     * @param dir WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setSuspTravelDirection(id: Int, dir: PxVec3)

    /**
     * @param id     WebIDL type: unsigned long
     * @param offset WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setSuspForceAppPointOffset(id: Int, offset: PxVec3)

    /**
     * @param id     WebIDL type: unsigned long
     * @param offset WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setTireForceAppPointOffset(id: Int, offset: PxVec3)

    /**
     * @param id     WebIDL type: unsigned long
     * @param offset WebIDL type: [PxVec3] (Const, Ref)
     */
    fun setWheelCentreOffset(id: Int, offset: PxVec3)

    /**
     * @param wheelId WebIDL type: unsigned long
     * @param shapeId WebIDL type: long
     */
    fun setWheelShapeMapping(wheelId: Int, shapeId: Int)

    /**
     * @param suspId       WebIDL type: unsigned long
     * @param sqFilterData WebIDL type: [PxFilterData] (Const, Ref)
     */
    fun setSceneQueryFilterData(suspId: Int, sqFilterData: PxFilterData)

    /**
     * @param tireLoadFilter WebIDL type: [PxVehicleTireLoadFilterData] (Const, Ref)
     */
    fun setTireLoadFilterData(tireLoadFilter: PxVehicleTireLoadFilterData)

    /**
     * @param antiRoll WebIDL type: [PxVehicleAntiRollBarData] (Const, Ref)
     * @return WebIDL type: unsigned long
     */
    fun addAntiRollBarData(antiRoll: PxVehicleAntiRollBarData): Int

    /**
     * @param wheel WebIDL type: unsigned long
     */
    fun disableWheel(wheel: Int)

    /**
     * @param wheel WebIDL type: unsigned long
     */
    fun enableWheel(wheel: Int)

    /**
     * @param wheel WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun getIsWheelDisabled(wheel: Int): Boolean

    /**
     * @param thresholdLongitudinalSpeed   WebIDL type: float
     * @param lowForwardSpeedSubStepCount  WebIDL type: unsigned long
     * @param highForwardSpeedSubStepCount WebIDL type: unsigned long
     */
    fun setSubStepCount(thresholdLongitudinalSpeed: Float, lowForwardSpeedSubStepCount: Int, highForwardSpeedSubStepCount: Int)

    /**
     * @param minLongSlipDenominator WebIDL type: float
     */
    fun setMinLongSlipDenominator(minLongSlipDenominator: Float)

    /**
     * @param flags WebIDL type: [PxVehicleWheelsSimFlags] (Ref)
     */
    fun setFlags(flags: PxVehicleWheelsSimFlags)

    /**
     * @return WebIDL type: [PxVehicleWheelsSimFlags] (Value)
     */
    fun getFlags(): PxVehicleWheelsSimFlags

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheels4(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSuspensionData(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelData(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSuspTravelDirection(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTireData(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSuspForceAppPointOffset(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbTireForceAppPointOffset(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelCentreOffset(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelShapeMapping(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbSceneQueryFilterData(): Int

    /**
     * @return WebIDL type: float
     */
    fun getMinLongSlipDenominator(): Float

    /**
     * @param f WebIDL type: float
     */
    fun setThresholdLongSpeed(f: Float)

    /**
     * @return WebIDL type: float
     */
    fun getThresholdLongSpeed(): Float

    /**
     * @param f WebIDL type: unsigned long
     */
    fun setLowForwardSpeedSubStepCount(f: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getLowForwardSpeedSubStepCount(): Int

    /**
     * @param f WebIDL type: unsigned long
     */
    fun setHighForwardSpeedSubStepCount(f: Int)

    /**
     * @return WebIDL type: unsigned long
     */
    fun getHighForwardSpeedSubStepCount(): Int

    /**
     * @param wheel WebIDL type: unsigned long
     * @param state WebIDL type: boolean
     */
    fun setWheelEnabledState(wheel: Int, state: Boolean)

    /**
     * @param wheel WebIDL type: unsigned long
     * @return WebIDL type: boolean
     */
    fun getWheelEnabledState(wheel: Int): Boolean

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelEnabledState(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAntiRollBars4(): Int

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbAntiRollBarData(): Int

    /**
     * @param id       WebIDL type: unsigned long
     * @param antiRoll WebIDL type: [PxVehicleAntiRollBarData] (Const, Ref)
     */
    fun setAntiRollBarData(id: Int, antiRoll: PxVehicleAntiRollBarData)

}

fun PxVehicleWheelsSimData.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleWheelsSimData.nbWheels
    get() = getNbWheels()
val PxVehicleWheelsSimData.nbAntiRollBars
    get() = getNbAntiRollBars()
val PxVehicleWheelsSimData.nbWheels4
    get() = getNbWheels4()
val PxVehicleWheelsSimData.nbSuspensionData
    get() = getNbSuspensionData()
val PxVehicleWheelsSimData.nbWheelData
    get() = getNbWheelData()
val PxVehicleWheelsSimData.nbSuspTravelDirection
    get() = getNbSuspTravelDirection()
val PxVehicleWheelsSimData.nbTireData
    get() = getNbTireData()
val PxVehicleWheelsSimData.nbSuspForceAppPointOffset
    get() = getNbSuspForceAppPointOffset()
val PxVehicleWheelsSimData.nbTireForceAppPointOffset
    get() = getNbTireForceAppPointOffset()
val PxVehicleWheelsSimData.nbWheelCentreOffset
    get() = getNbWheelCentreOffset()
val PxVehicleWheelsSimData.nbWheelShapeMapping
    get() = getNbWheelShapeMapping()
val PxVehicleWheelsSimData.nbSceneQueryFilterData
    get() = getNbSceneQueryFilterData()
val PxVehicleWheelsSimData.nbWheelEnabledState
    get() = getNbWheelEnabledState()
val PxVehicleWheelsSimData.nbAntiRollBars4
    get() = getNbAntiRollBars4()
val PxVehicleWheelsSimData.nbAntiRollBarData
    get() = getNbAntiRollBarData()

var PxVehicleWheelsSimData.tireLoadFilterData
    get() = getTireLoadFilterData()
    set(value) { setTireLoadFilterData(value) }
var PxVehicleWheelsSimData.flags
    get() = getFlags()
    set(value) { setFlags(value) }
var PxVehicleWheelsSimData.minLongSlipDenominator
    get() = getMinLongSlipDenominator()
    set(value) { setMinLongSlipDenominator(value) }
var PxVehicleWheelsSimData.thresholdLongSpeed
    get() = getThresholdLongSpeed()
    set(value) { setThresholdLongSpeed(value) }
var PxVehicleWheelsSimData.lowForwardSpeedSubStepCount
    get() = getLowForwardSpeedSubStepCount()
    set(value) { setLowForwardSpeedSubStepCount(value) }
var PxVehicleWheelsSimData.highForwardSpeedSubStepCount
    get() = getHighForwardSpeedSubStepCount()
    set(value) { setHighForwardSpeedSubStepCount(value) }

external interface PxVehicleWheelsSimFlags {
    /**
     * @param flag WebIDL type: [PxVehicleWheelsSimFlagEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun isSet(flag: Int): Boolean

    /**
     * @param flag WebIDL type: [PxVehicleWheelsSimFlagEnum] (enum)
     */
    fun set(flag: Int)

    /**
     * @param flag WebIDL type: [PxVehicleWheelsSimFlagEnum] (enum)
     */
    fun clear(flag: Int)

}

/**
 * @param flags WebIDL type: unsigned long
 */
fun PxVehicleWheelsSimFlags(flags: Int): PxVehicleWheelsSimFlags {
    fun _PxVehicleWheelsSimFlags(_module: dynamic, flags: Int) = js("new _module.PxVehicleWheelsSimFlags(flags)")
    return _PxVehicleWheelsSimFlags(PhysXJsLoader.physXJs, flags)
}

fun PxVehicleWheelsSimFlags.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxWheelQueryResult {
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspLineStart: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspLineDir: PxVec3
    /**
     * WebIDL type: float
     */
    var suspLineLength: Float
    /**
     * WebIDL type: boolean
     */
    var isInAir: Boolean
    /**
     * WebIDL type: [PxActor]
     */
    var tireContactActor: PxActor
    /**
     * WebIDL type: [PxShape]
     */
    var tireContactShape: PxShape
    /**
     * WebIDL type: [PxMaterial] (Const)
     */
    var tireSurfaceMaterial: PxMaterial
    /**
     * WebIDL type: unsigned long
     */
    var tireSurfaceType: Int
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var tireContactPoint: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var tireContactNormal: PxVec3
    /**
     * WebIDL type: float
     */
    var tireFriction: Float
    /**
     * WebIDL type: float
     */
    var suspJounce: Float
    /**
     * WebIDL type: float
     */
    var suspSpringForce: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var tireLongitudinalDir: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var tireLateralDir: PxVec3
    /**
     * WebIDL type: float
     */
    var longitudinalSlip: Float
    /**
     * WebIDL type: float
     */
    var lateralSlip: Float
    /**
     * WebIDL type: float
     */
    var steerAngle: Float
    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var localPose: PxTransform
}

fun PxWheelQueryResult(): PxWheelQueryResult {
    fun _PxWheelQueryResult(_module: dynamic) = js("new _module.PxWheelQueryResult()")
    return _PxWheelQueryResult(PhysXJsLoader.physXJs)
}

fun PxWheelQueryResult.destroy() {
    PhysXJsLoader.destroy(this)
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

object PxVehicleDriveTankControlModelEnum {
    val eSTANDARD: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDriveTankControlModelEnum_eSTANDARD()
    val eSPECIAL: Int get() = PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDriveTankControlModelEnum_eSPECIAL()
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

