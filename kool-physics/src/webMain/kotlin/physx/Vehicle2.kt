/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "NOTHING_TO_INLINE")

package physx

import kotlin.js.JsAny
import kotlin.js.js

external interface PxVehicleTopLevelFunctions : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var MAX_NB_ENGINE_TORQUE_CURVE_ENTRIES: Int

    /**
     * @param foundation WebIDL type: [PxFoundation] (Ref)
     * @return WebIDL type: boolean
     */
    fun InitVehicleExtension(foundation: PxFoundation): Boolean

    fun CloseVehicleExtension()

    /**
     * @param nbSprungMasses        WebIDL type: unsigned long
     * @param sprungMassCoordinates WebIDL type: [PxArray_PxVec3] (Ref)
     * @param totalMass             WebIDL type: float
     * @param gravityDirection      WebIDL type: [PxVehicleAxesEnum] (enum)
     * @param sprungMasses          WebIDL type: [PxArray_PxReal] (Ref)
     * @return WebIDL type: boolean
     */
    fun VehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxArray_PxVec3, totalMass: Float, gravityDirection: Int, sprungMasses: PxArray_PxReal): Boolean

    /**
     * @param vehicleFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param physics      WebIDL type: [PxPhysics] (Ref)
     * @param params       WebIDL type: [PxCookingParams] (Const, Ref)
     * @return WebIDL type: [PxConvexMesh]
     */
    fun VehicleUnitCylinderSweepMeshCreate(vehicleFrame: PxVehicleFrame, physics: PxPhysics, params: PxCookingParams): PxConvexMesh

    /**
     * @param mesh WebIDL type: [PxConvexMesh]
     */
    fun VehicleUnitCylinderSweepMeshDestroy(mesh: PxConvexMesh)

}

fun PxVehicleTopLevelFunctionsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTopLevelFunctions = js("_module.wrapPointer(ptr, _module.PxVehicleTopLevelFunctions)")

fun PxVehicleTopLevelFunctions.VehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxArray_PxVec3, totalMass: Float, gravityDirection: PxVehicleAxesEnum, sprungMasses: PxArray_PxReal) = VehicleComputeSprungMasses(nbSprungMasses, sprungMassCoordinates, totalMass, gravityDirection.value, sprungMasses)

external interface PxVehicleAckermannParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    fun get_wheelIds(index: Int): Int
    fun set_wheelIds(index: Int, value: Int)
    /**
     * WebIDL type: float
     */
    var wheelBase: Float
    /**
     * WebIDL type: float
     */
    var trackWidth: Float
    /**
     * WebIDL type: float
     */
    var strength: Float

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleAckermannParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleAckermannParams

}

fun PxVehicleAckermannParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAckermannParams = js("new _module.PxVehicleAckermannParams()")

fun PxVehicleAckermannParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAckermannParams = js("_module.wrapPointer(ptr, _module.PxVehicleAckermannParams)")

inline fun PxVehicleAckermannParams.getWheelIds(index: Int) = get_wheelIds(index)
inline fun PxVehicleAckermannParams.setWheelIds(index: Int, value: Int) = set_wheelIds(index, value)

external interface PxVehicleAntiRollForceParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var wheel0: Int
    /**
     * WebIDL type: unsigned long
     */
    var wheel1: Int
    /**
     * WebIDL type: float
     */
    var stiffness: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleAntiRollForceParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleAntiRollForceParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun PxVehicleAntiRollForceParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAntiRollForceParams = js("new _module.PxVehicleAntiRollForceParams()")

fun PxVehicleAntiRollForceParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAntiRollForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleAntiRollForceParams)")

external interface PxVehicleAntiRollTorque : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var antiRollTorque: PxVec3

    fun setToDefault()

}

fun PxVehicleAntiRollTorque(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAntiRollTorque = js("new _module.PxVehicleAntiRollTorque()")

fun PxVehicleAntiRollTorqueFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAntiRollTorque = js("_module.wrapPointer(ptr, _module.PxVehicleAntiRollTorque)")

external interface PxVehicleAutoboxParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_upRatios(index: Int): Float
    fun set_upRatios(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    fun get_downRatios(index: Int): Float
    fun set_downRatios(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    var latency: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleAutoboxParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleAutoboxParams

    /**
     * @param gearboxParams WebIDL type: [PxVehicleGearboxParams] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(gearboxParams: PxVehicleGearboxParams): Boolean

}

fun PxVehicleAutoboxParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAutoboxParams = js("new _module.PxVehicleAutoboxParams()")

fun PxVehicleAutoboxParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAutoboxParams = js("_module.wrapPointer(ptr, _module.PxVehicleAutoboxParams)")

inline fun PxVehicleAutoboxParams.getUpRatios(index: Int) = get_upRatios(index)
inline fun PxVehicleAutoboxParams.setUpRatios(index: Int, value: Float) = set_upRatios(index, value)
inline fun PxVehicleAutoboxParams.getDownRatios(index: Int) = get_downRatios(index)
inline fun PxVehicleAutoboxParams.setDownRatios(index: Int, value: Float) = set_downRatios(index, value)

external interface PxVehicleAutoboxState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var timeSinceLastShift: Float
    /**
     * WebIDL type: boolean
     */
    var activeAutoboxGearShift: Boolean

    fun setToDefault()

}

fun PxVehicleAutoboxState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAutoboxState = js("new _module.PxVehicleAutoboxState()")

fun PxVehicleAutoboxStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAutoboxState = js("_module.wrapPointer(ptr, _module.PxVehicleAutoboxState)")

external interface PxVehicleAxleDescription : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var nbAxles: Int
    /**
     * WebIDL type: unsigned long
     */
    fun get_nbWheelsPerAxle(index: Int): Int
    fun set_nbWheelsPerAxle(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_axleToWheelIds(index: Int): Int
    fun set_axleToWheelIds(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_wheelIdsInAxleOrder(index: Int): Int
    fun set_wheelIdsInAxleOrder(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    var nbWheels: Int

    fun setToDefault()

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelsOnAxle(i: Int): Int

    /**
     * @param j WebIDL type: unsigned long
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getWheelOnAxle(j: Int, i: Int): Int

    /**
     * @param wheelId WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getAxle(wheelId: Int): Int

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleAxleDescription(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleAxleDescription = js("new _module.PxVehicleAxleDescription()")

fun PxVehicleAxleDescriptionFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleAxleDescription = js("_module.wrapPointer(ptr, _module.PxVehicleAxleDescription)")

inline fun PxVehicleAxleDescription.getNbWheelsPerAxle(index: Int) = get_nbWheelsPerAxle(index)
inline fun PxVehicleAxleDescription.setNbWheelsPerAxle(index: Int, value: Int) = set_nbWheelsPerAxle(index, value)
inline fun PxVehicleAxleDescription.getAxleToWheelIds(index: Int) = get_axleToWheelIds(index)
inline fun PxVehicleAxleDescription.setAxleToWheelIds(index: Int, value: Int) = set_axleToWheelIds(index, value)
inline fun PxVehicleAxleDescription.getWheelIdsInAxleOrder(index: Int) = get_wheelIdsInAxleOrder(index)
inline fun PxVehicleAxleDescription.setWheelIdsInAxleOrder(index: Int, value: Int) = set_wheelIdsInAxleOrder(index, value)

external interface PxVehicleBrakeCommandResponseParams : JsAny, DestroyableNative, PxVehicleCommandResponseParams {
    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleBrakeCommandResponseParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleBrakeCommandResponseParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun PxVehicleBrakeCommandResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleBrakeCommandResponseParams = js("new _module.PxVehicleBrakeCommandResponseParams()")

fun PxVehicleBrakeCommandResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleBrakeCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleBrakeCommandResponseParams)")

external interface PxVehicleClutchCommandResponseParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var maxResponse: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleClutchCommandResponseParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleClutchCommandResponseParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleClutchCommandResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseParams = js("new _module.PxVehicleClutchCommandResponseParams()")

fun PxVehicleClutchCommandResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleClutchCommandResponseParams)")

external interface PxVehicleClutchCommandResponseState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var normalisedCommandResponse: Float
    /**
     * WebIDL type: float
     */
    var commandResponse: Float

    fun setToDefault()

}

fun PxVehicleClutchCommandResponseState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseState = js("new _module.PxVehicleClutchCommandResponseState()")

fun PxVehicleClutchCommandResponseStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseState = js("_module.wrapPointer(ptr, _module.PxVehicleClutchCommandResponseState)")

external interface PxVehicleClutchParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleClutchAccuracyModeEnum] (enum)
     */
    var accuracyMode: Int
    /**
     * WebIDL type: unsigned long
     */
    var estimateIterations: Int

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleClutchParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleClutchParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleClutchParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchParams = js("new _module.PxVehicleClutchParams()")

fun PxVehicleClutchParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchParams = js("_module.wrapPointer(ptr, _module.PxVehicleClutchParams)")

var PxVehicleClutchParams.accuracyModeEnum: PxVehicleClutchAccuracyModeEnum
    get() = PxVehicleClutchAccuracyModeEnum.forValue(accuracyMode)
    set(value) { accuracyMode = value.value }

external interface PxVehicleClutchSlipState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var clutchSlip: Float

    fun setToDefault()

}

fun PxVehicleClutchSlipState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchSlipState = js("new _module.PxVehicleClutchSlipState()")

fun PxVehicleClutchSlipStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleClutchSlipState = js("_module.wrapPointer(ptr, _module.PxVehicleClutchSlipState)")

external interface PxVehicleCommandNonLinearResponseParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_speedResponses(index: Int): Float
    fun set_speedResponses(index: Int, value: Float)
    /**
     * WebIDL type: short
     */
    var nbSpeedResponses: Short
    /**
     * WebIDL type: short
     */
    fun get_speedResponsesPerCommandValue(index: Int): Short
    fun set_speedResponsesPerCommandValue(index: Int, value: Short)
    /**
     * WebIDL type: short
     */
    fun get_nbSpeedResponsesPerCommandValue(index: Int): Short
    fun set_nbSpeedResponsesPerCommandValue(index: Int, value: Short)
    /**
     * WebIDL type: float
     */
    fun get_commandValues(index: Int): Float
    fun set_commandValues(index: Int, value: Float)
    /**
     * WebIDL type: short
     */
    var nbCommandValues: Short

    fun clear()

    /**
     * @param commandValueSpeedResponses WebIDL type: [PxVehicleCommandValueResponseTable] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun addResponse(commandValueSpeedResponses: PxVehicleCommandValueResponseTable): Boolean

}

fun PxVehicleCommandNonLinearResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandNonLinearResponseParams = js("new _module.PxVehicleCommandNonLinearResponseParams()")

fun PxVehicleCommandNonLinearResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandNonLinearResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleCommandNonLinearResponseParams)")

inline fun PxVehicleCommandNonLinearResponseParams.getSpeedResponses(index: Int) = get_speedResponses(index)
inline fun PxVehicleCommandNonLinearResponseParams.setSpeedResponses(index: Int, value: Float) = set_speedResponses(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getSpeedResponsesPerCommandValue(index: Int) = get_speedResponsesPerCommandValue(index)
inline fun PxVehicleCommandNonLinearResponseParams.setSpeedResponsesPerCommandValue(index: Int, value: Short) = set_speedResponsesPerCommandValue(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getNbSpeedResponsesPerCommandValue(index: Int) = get_nbSpeedResponsesPerCommandValue(index)
inline fun PxVehicleCommandNonLinearResponseParams.setNbSpeedResponsesPerCommandValue(index: Int, value: Short) = set_nbSpeedResponsesPerCommandValue(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getCommandValues(index: Int) = get_commandValues(index)
inline fun PxVehicleCommandNonLinearResponseParams.setCommandValues(index: Int, value: Float) = set_commandValues(index, value)

external interface PxVehicleCommandResponseParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleCommandNonLinearResponseParams] (Value)
     */
    var nonlinearResponse: PxVehicleCommandNonLinearResponseParams
    /**
     * WebIDL type: float
     */
    fun get_wheelResponseMultipliers(index: Int): Float
    fun set_wheelResponseMultipliers(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    var maxResponse: Float
}

fun PxVehicleCommandResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandResponseParams = js("new _module.PxVehicleCommandResponseParams()")

fun PxVehicleCommandResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleCommandResponseParams)")

inline fun PxVehicleCommandResponseParams.getWheelResponseMultipliers(index: Int) = get_wheelResponseMultipliers(index)
inline fun PxVehicleCommandResponseParams.setWheelResponseMultipliers(index: Int, value: Float) = set_wheelResponseMultipliers(index, value)

external interface PxVehicleCommandState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_brakes(index: Int): Float
    fun set_brakes(index: Int, value: Float)
    /**
     * WebIDL type: unsigned long
     */
    var nbBrakes: Int
    /**
     * WebIDL type: float
     */
    var throttle: Float
    /**
     * WebIDL type: float
     */
    var steer: Float

    fun setToDefault()

}

fun PxVehicleCommandState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandState = js("new _module.PxVehicleCommandState()")

fun PxVehicleCommandStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleCommandState)")

inline fun PxVehicleCommandState.getBrakes(index: Int) = get_brakes(index)
inline fun PxVehicleCommandState.setBrakes(index: Int, value: Float) = set_brakes(index, value)

external interface PxVehicleCommandValueResponseTable : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var commandValue: Float
}

fun PxVehicleCommandValueResponseTable(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandValueResponseTable = js("new _module.PxVehicleCommandValueResponseTable()")

fun PxVehicleCommandValueResponseTableFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleCommandValueResponseTable = js("_module.wrapPointer(ptr, _module.PxVehicleCommandValueResponseTable)")

external interface PxVehicleComponent : JsAny, DestroyableNative

fun PxVehicleComponentFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleComponent = js("_module.wrapPointer(ptr, _module.PxVehicleComponent)")

external interface PxVehicleComponentSequence : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param component WebIDL type: [PxVehicleComponent]
     * @return WebIDL type: boolean
     */
    fun add(component: PxVehicleComponent): Boolean

    /**
     * @return WebIDL type: octet
     */
    fun beginSubstepGroup(): Byte

    /**
     * @param nbSubSteps WebIDL type: octet
     * @return WebIDL type: octet
     */
    fun beginSubstepGroup(nbSubSteps: Byte): Byte

    fun endSubstepGroup()

    /**
     * @param subGroupHandle WebIDL type: octet
     * @param nbSteps        WebIDL type: octet
     */
    fun setSubsteps(subGroupHandle: Byte, nbSteps: Byte)

    /**
     * @param dt      WebIDL type: float
     * @param context WebIDL type: [PxVehicleSimulationContext] (Const, Ref)
     */
    fun update(dt: Float, context: PxVehicleSimulationContext)

}

fun PxVehicleComponentSequence(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleComponentSequence = js("new _module.PxVehicleComponentSequence()")

fun PxVehicleComponentSequenceFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleComponentSequence = js("_module.wrapPointer(ptr, _module.PxVehicleComponentSequence)")

external interface PxVehicleConstraintConnector : JsAny, DestroyableNative, PxConstraintConnector {
    /**
     * @param constraintState WebIDL type: [PxVehiclePhysXConstraintState]
     */
    fun setConstraintState(constraintState: PxVehiclePhysXConstraintState)

    override fun getConstantBlock()

}

fun PxVehicleConstraintConnector(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("new _module.PxVehicleConstraintConnector()")

/**
 * @param vehicleConstraintState WebIDL type: [PxVehiclePhysXConstraintState]
 */
fun PxVehicleConstraintConnector(vehicleConstraintState: PxVehiclePhysXConstraintState, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("new _module.PxVehicleConstraintConnector(vehicleConstraintState)")

fun PxVehicleConstraintConnectorFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("_module.wrapPointer(ptr, _module.PxVehicleConstraintConnector)")

val PxVehicleConstraintConnector.constantBlock
    get() = getConstantBlock()

external interface PxVehicleDifferentialState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    fun get_connectedWheels(index: Int): Int
    fun set_connectedWheels(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    var nbConnectedWheels: Int
    /**
     * WebIDL type: float
     */
    fun get_torqueRatiosAllWheels(index: Int): Float
    fun set_torqueRatiosAllWheels(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    fun get_aveWheelSpeedContributionAllWheels(index: Int): Float
    fun set_aveWheelSpeedContributionAllWheels(index: Int, value: Float)

    fun setToDefault()

}

fun PxVehicleDifferentialState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleDifferentialState = js("new _module.PxVehicleDifferentialState()")

fun PxVehicleDifferentialStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleDifferentialState = js("_module.wrapPointer(ptr, _module.PxVehicleDifferentialState)")

inline fun PxVehicleDifferentialState.getConnectedWheels(index: Int) = get_connectedWheels(index)
inline fun PxVehicleDifferentialState.setConnectedWheels(index: Int, value: Int) = set_connectedWheels(index, value)
inline fun PxVehicleDifferentialState.getTorqueRatiosAllWheels(index: Int) = get_torqueRatiosAllWheels(index)
inline fun PxVehicleDifferentialState.setTorqueRatiosAllWheels(index: Int, value: Float) = set_torqueRatiosAllWheels(index, value)
inline fun PxVehicleDifferentialState.getAveWheelSpeedContributionAllWheels(index: Int) = get_aveWheelSpeedContributionAllWheels(index)
inline fun PxVehicleDifferentialState.setAveWheelSpeedContributionAllWheels(index: Int, value: Float) = set_aveWheelSpeedContributionAllWheels(index, value)

external interface PxVehicleDirectDriveThrottleCommandResponseParams : JsAny, DestroyableNative, PxVehicleCommandResponseParams {
    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleDirectDriveThrottleCommandResponseParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleDirectDriveThrottleCommandResponseParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun PxVehicleDirectDriveThrottleCommandResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleDirectDriveThrottleCommandResponseParams = js("new _module.PxVehicleDirectDriveThrottleCommandResponseParams()")

fun PxVehicleDirectDriveThrottleCommandResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleDirectDriveThrottleCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleDirectDriveThrottleCommandResponseParams)")

external interface PxVehicleDirectDriveTransmissionCommandState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleDirectDriveTransmissionCommandStateEnum] (enum)
     */
    var gear: Int

    fun setToDefault()

}

fun PxVehicleDirectDriveTransmissionCommandState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleDirectDriveTransmissionCommandState = js("new _module.PxVehicleDirectDriveTransmissionCommandState()")

fun PxVehicleDirectDriveTransmissionCommandStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleDirectDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleDirectDriveTransmissionCommandState)")

var PxVehicleDirectDriveTransmissionCommandState.gearEnum: PxVehicleDirectDriveTransmissionCommandStateEnum
    get() = PxVehicleDirectDriveTransmissionCommandStateEnum.forValue(gear)
    set(value) { gear = value.value }

external interface PxVehicleEngineDriveThrottleCommandResponseState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var commandResponse: Float

    fun setToDefault()

}

fun PxVehicleEngineDriveThrottleCommandResponseState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineDriveThrottleCommandResponseState = js("new _module.PxVehicleEngineDriveThrottleCommandResponseState()")

fun PxVehicleEngineDriveThrottleCommandResponseStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineDriveThrottleCommandResponseState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineDriveThrottleCommandResponseState)")

external interface PxVehicleEngineDriveTransmissionCommandState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var clutch: Float
    /**
     * WebIDL type: unsigned long
     */
    var targetGear: Int

    fun setToDefault()

}

fun PxVehicleEngineDriveTransmissionCommandState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineDriveTransmissionCommandState = js("new _module.PxVehicleEngineDriveTransmissionCommandState()")

fun PxVehicleEngineDriveTransmissionCommandStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineDriveTransmissionCommandState)")

external interface PxVehicleEngineParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleTorqueCurveLookupTable] (Value)
     */
    var torqueCurve: PxVehicleTorqueCurveLookupTable
    /**
     * WebIDL type: float
     */
    var moi: Float
    /**
     * WebIDL type: float
     */
    var peakTorque: Float
    /**
     * WebIDL type: float
     */
    var idleOmega: Float
    /**
     * WebIDL type: float
     */
    var maxOmega: Float
    /**
     * WebIDL type: float
     */
    var dampingRateFullThrottle: Float
    /**
     * WebIDL type: float
     */
    var dampingRateZeroThrottleClutchEngaged: Float
    /**
     * WebIDL type: float
     */
    var dampingRateZeroThrottleClutchDisengaged: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleEngineParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleEngineParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleEngineParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineParams = js("new _module.PxVehicleEngineParams()")

fun PxVehicleEngineParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineParams = js("_module.wrapPointer(ptr, _module.PxVehicleEngineParams)")

external interface PxVehicleEngineState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var rotationSpeed: Float

    fun setToDefault()

}

fun PxVehicleEngineState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineState = js("new _module.PxVehicleEngineState()")

fun PxVehicleEngineStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleEngineState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineState)")

external interface PxVehicleFixedSizeLookupTableFloat_3 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param x WebIDL type: float
     * @param y WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun addPair(x: Float, y: Float): Boolean

    /**
     * @param x WebIDL type: float
     * @return WebIDL type: float
     */
    fun interpolate(x: Float): Float

    fun clear()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleFixedSizeLookupTableFloat_3(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableFloat_3 = js("new _module.PxVehicleFixedSizeLookupTableFloat_3()")

fun PxVehicleFixedSizeLookupTableFloat_3FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableFloat_3 = js("_module.wrapPointer(ptr, _module.PxVehicleFixedSizeLookupTableFloat_3)")

external interface PxVehicleFixedSizeLookupTableVec3_3 : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param x WebIDL type: float
     * @param y WebIDL type: [PxVec3] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun addPair(x: Float, y: PxVec3): Boolean

    /**
     * @param x WebIDL type: float
     * @return WebIDL type: [PxVec3] (Value)
     */
    fun interpolate(x: Float): PxVec3

    fun clear()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleFixedSizeLookupTableVec3_3(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableVec3_3 = js("new _module.PxVehicleFixedSizeLookupTableVec3_3()")

fun PxVehicleFixedSizeLookupTableVec3_3FromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableVec3_3 = js("_module.wrapPointer(ptr, _module.PxVehicleFixedSizeLookupTableVec3_3)")

external interface PxVehicleFourWheelDriveDifferentialParams : JsAny, DestroyableNative, PxVehicleMultiWheelDriveDifferentialParams {
    /**
     * WebIDL type: unsigned long
     */
    fun get_frontWheelIds(index: Int): Int
    fun set_frontWheelIds(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_rearWheelIds(index: Int): Int
    fun set_rearWheelIds(index: Int, value: Int)
    /**
     * WebIDL type: float
     */
    var frontBias: Float
    /**
     * WebIDL type: float
     */
    var frontTarget: Float
    /**
     * WebIDL type: float
     */
    var rearBias: Float
    /**
     * WebIDL type: float
     */
    var rearTarget: Float
    /**
     * WebIDL type: float
     */
    var centerBias: Float
    /**
     * WebIDL type: float
     */
    var centerTarget: Float
    /**
     * WebIDL type: float
     */
    var rate: Float

    override fun setToDefault()

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleFourWheelDriveDifferentialParams] (Value)
     */
    override fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleFourWheelDriveDifferentialParams

}

fun PxVehicleFourWheelDriveDifferentialParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleFourWheelDriveDifferentialParams = js("new _module.PxVehicleFourWheelDriveDifferentialParams()")

fun PxVehicleFourWheelDriveDifferentialParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleFourWheelDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleFourWheelDriveDifferentialParams)")

inline fun PxVehicleFourWheelDriveDifferentialParams.getFrontWheelIds(index: Int) = get_frontWheelIds(index)
inline fun PxVehicleFourWheelDriveDifferentialParams.setFrontWheelIds(index: Int, value: Int) = set_frontWheelIds(index, value)
inline fun PxVehicleFourWheelDriveDifferentialParams.getRearWheelIds(index: Int) = get_rearWheelIds(index)
inline fun PxVehicleFourWheelDriveDifferentialParams.setRearWheelIds(index: Int, value: Int) = set_rearWheelIds(index, value)

external interface PxVehicleFrame : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var lngAxis: Int
    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var latAxis: Int
    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var vrtAxis: Int

    fun setToDefault()

    /**
     * @return WebIDL type: [PxMat33] (Value)
     */
    fun getFrame(): PxMat33

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleFrame(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleFrame = js("new _module.PxVehicleFrame()")

fun PxVehicleFrameFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleFrame = js("_module.wrapPointer(ptr, _module.PxVehicleFrame)")

val PxVehicleFrame.frame
    get() = getFrame()

var PxVehicleFrame.lngAxisEnum: PxVehicleAxesEnum
    get() = PxVehicleAxesEnum.forValue(lngAxis)
    set(value) { lngAxis = value.value }
var PxVehicleFrame.latAxisEnum: PxVehicleAxesEnum
    get() = PxVehicleAxesEnum.forValue(latAxis)
    set(value) { latAxis = value.value }
var PxVehicleFrame.vrtAxisEnum: PxVehicleAxesEnum
    get() = PxVehicleAxesEnum.forValue(vrtAxis)
    set(value) { vrtAxis = value.value }

external interface PxVehicleGearboxParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var neutralGear: Int
    /**
     * WebIDL type: float
     */
    fun get_ratios(index: Int): Float
    fun set_ratios(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    var finalRatio: Float
    /**
     * WebIDL type: unsigned long
     */
    var nbRatios: Int
    /**
     * WebIDL type: float
     */
    var switchTime: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleGearboxParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleGearboxParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleGearboxParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleGearboxParams = js("new _module.PxVehicleGearboxParams()")

fun PxVehicleGearboxParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleGearboxParams = js("_module.wrapPointer(ptr, _module.PxVehicleGearboxParams)")

inline fun PxVehicleGearboxParams.getRatios(index: Int) = get_ratios(index)
inline fun PxVehicleGearboxParams.setRatios(index: Int, value: Float) = set_ratios(index, value)

external interface PxVehicleGearboxState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var currentGear: Int
    /**
     * WebIDL type: unsigned long
     */
    var targetGear: Int
    /**
     * WebIDL type: float
     */
    var gearSwitchTime: Float

    fun setToDefault()

}

fun PxVehicleGearboxState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleGearboxState = js("new _module.PxVehicleGearboxState()")

fun PxVehicleGearboxStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleGearboxState = js("_module.wrapPointer(ptr, _module.PxVehicleGearboxState)")

external interface PxVehicleMultiWheelDriveDifferentialParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_torqueRatios(index: Int): Float
    fun set_torqueRatios(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    fun get_aveWheelSpeedRatios(index: Int): Float
    fun set_aveWheelSpeedRatios(index: Int, value: Float)

    fun setToDefault()

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleMultiWheelDriveDifferentialParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleMultiWheelDriveDifferentialParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun PxVehicleMultiWheelDriveDifferentialParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleMultiWheelDriveDifferentialParams = js("new _module.PxVehicleMultiWheelDriveDifferentialParams()")

fun PxVehicleMultiWheelDriveDifferentialParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleMultiWheelDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleMultiWheelDriveDifferentialParams)")

inline fun PxVehicleMultiWheelDriveDifferentialParams.getTorqueRatios(index: Int) = get_torqueRatios(index)
inline fun PxVehicleMultiWheelDriveDifferentialParams.setTorqueRatios(index: Int, value: Float) = set_torqueRatios(index, value)
inline fun PxVehicleMultiWheelDriveDifferentialParams.getAveWheelSpeedRatios(index: Int) = get_aveWheelSpeedRatios(index)
inline fun PxVehicleMultiWheelDriveDifferentialParams.setAveWheelSpeedRatios(index: Int, value: Float) = set_aveWheelSpeedRatios(index, value)

external interface PxVehiclePhysXActor : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxRigidBody]
     */
    var rigidBody: PxRigidBody
    /**
     * WebIDL type: [PxShape]
     */
    fun get_wheelShapes(index: Int): PxShape
    fun set_wheelShapes(index: Int, value: PxShape)

    fun setToDefault()

}

fun PxVehiclePhysXActorFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXActor = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXActor)")

inline fun PxVehiclePhysXActor.getWheelShapes(index: Int) = get_wheelShapes(index)
inline fun PxVehiclePhysXActor.setWheelShapes(index: Int, value: PxShape) = set_wheelShapes(index, value)

external interface PxVehiclePhysXConstraints : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXConstraintState] (Value)
     */
    fun get_constraintStates(index: Int): PxVehiclePhysXConstraintState
    fun set_constraintStates(index: Int, value: PxVehiclePhysXConstraintState)
    /**
     * WebIDL type: [PxConstraint]
     */
    fun get_constraints(index: Int): PxConstraint
    fun set_constraints(index: Int, value: PxConstraint)
    /**
     * WebIDL type: [PxVehicleConstraintConnector]
     */
    fun get_constraintConnectors(index: Int): PxVehicleConstraintConnector
    fun set_constraintConnectors(index: Int, value: PxVehicleConstraintConnector)

    fun setToDefault()

}

fun PxVehiclePhysXConstraintsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXConstraints = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXConstraints)")

inline fun PxVehiclePhysXConstraints.getConstraintStates(index: Int) = get_constraintStates(index)
inline fun PxVehiclePhysXConstraints.setConstraintStates(index: Int, value: PxVehiclePhysXConstraintState) = set_constraintStates(index, value)
inline fun PxVehiclePhysXConstraints.getConstraints(index: Int) = get_constraints(index)
inline fun PxVehiclePhysXConstraints.setConstraints(index: Int, value: PxConstraint) = set_constraints(index, value)
inline fun PxVehiclePhysXConstraints.getConstraintConnectors(index: Int) = get_constraintConnectors(index)
inline fun PxVehiclePhysXConstraints.setConstraintConnectors(index: Int, value: PxVehicleConstraintConnector) = set_constraintConnectors(index, value)

external interface PxVehiclePhysXConstraintState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    fun get_tireActiveStatus(index: Int): Boolean
    fun set_tireActiveStatus(index: Int, value: Boolean)
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    fun get_tireLinears(index: Int): PxVec3
    fun set_tireLinears(index: Int, value: PxVec3)
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    fun get_tireAngulars(index: Int): PxVec3
    fun set_tireAngulars(index: Int, value: PxVec3)
    /**
     * WebIDL type: float
     */
    fun get_tireDamping(index: Int): Float
    fun set_tireDamping(index: Int, value: Float)
    /**
     * WebIDL type: boolean
     */
    var suspActiveStatus: Boolean
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspLinear: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspAngular: PxVec3
    /**
     * WebIDL type: float
     */
    var suspGeometricError: Float
    /**
     * WebIDL type: float
     */
    var restitution: Float

    fun setToDefault()

}

fun PxVehiclePhysXConstraintState(_module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXConstraintState = js("new _module.PxVehiclePhysXConstraintState()")

fun PxVehiclePhysXConstraintStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXConstraintState = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXConstraintState)")

inline fun PxVehiclePhysXConstraintState.getTireActiveStatus(index: Int) = get_tireActiveStatus(index)
inline fun PxVehiclePhysXConstraintState.setTireActiveStatus(index: Int, value: Boolean) = set_tireActiveStatus(index, value)
inline fun PxVehiclePhysXConstraintState.getTireLinears(index: Int) = get_tireLinears(index)
inline fun PxVehiclePhysXConstraintState.setTireLinears(index: Int, value: PxVec3) = set_tireLinears(index, value)
inline fun PxVehiclePhysXConstraintState.getTireAngulars(index: Int) = get_tireAngulars(index)
inline fun PxVehiclePhysXConstraintState.setTireAngulars(index: Int, value: PxVec3) = set_tireAngulars(index, value)
inline fun PxVehiclePhysXConstraintState.getTireDamping(index: Int) = get_tireDamping(index)
inline fun PxVehiclePhysXConstraintState.setTireDamping(index: Int, value: Float) = set_tireDamping(index, value)

external interface PxVehiclePhysXMaterialFriction : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxMaterial] (Const)
     */
    var material: PxMaterial
    /**
     * WebIDL type: float
     */
    var friction: Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehiclePhysXMaterialFriction(_module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFriction = js("new _module.PxVehiclePhysXMaterialFriction()")

fun PxVehiclePhysXMaterialFrictionFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFriction = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXMaterialFriction)")

external interface PxVehiclePhysXMaterialFrictionParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXMaterialFriction]
     */
    var materialFrictions: PxVehiclePhysXMaterialFriction
    /**
     * WebIDL type: unsigned long
     */
    var nbMaterialFrictions: Int
    /**
     * WebIDL type: float
     */
    var defaultFriction: Float

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehiclePhysXMaterialFrictionParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFrictionParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXMaterialFrictionParams)")

external interface PxVehiclePhysXRoadGeometryQueryParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXRoadGeometryQueryTypeEnum] (enum)
     */
    var roadGeometryQueryType: Int
    /**
     * WebIDL type: [PxQueryFilterData] (Value)
     */
    var defaultFilterData: PxQueryFilterData
    /**
     * WebIDL type: [PxQueryFilterData] (Nullable)
     */
    var filterDataEntries: PxQueryFilterData?
    /**
     * WebIDL type: [PxQueryFilterCallback]
     */
    var filterCallback: PxQueryFilterCallback

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehiclePhysXRoadGeometryQueryParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehiclePhysXRoadGeometryQueryParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehiclePhysXRoadGeometryQueryParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXRoadGeometryQueryParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXRoadGeometryQueryParams)")

var PxVehiclePhysXRoadGeometryQueryParams.roadGeometryQueryTypeEnum: PxVehiclePhysXRoadGeometryQueryTypeEnum
    get() = PxVehiclePhysXRoadGeometryQueryTypeEnum.forValue(roadGeometryQueryType)
    set(value) { roadGeometryQueryType = value.value }

external interface PxVehiclePhysXSimulationContext : JsAny, PxVehicleSimulationContext {
    /**
     * WebIDL type: [PxConvexMesh] (Const)
     */
    var physxUnitCylinderSweepMesh: PxConvexMesh
    /**
     * WebIDL type: [PxScene] (Const)
     */
    var physxScene: PxScene
    /**
     * WebIDL type: [PxVehiclePhysXActorUpdateModeEnum] (enum)
     */
    var physxActorUpdateMode: Int
    /**
     * WebIDL type: float
     */
    var physxActorWakeCounterResetValue: Float
    /**
     * WebIDL type: float
     */
    var physxActorWakeCounterThreshold: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehiclePhysXSimulationContext] (Value)
     */
    override fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehiclePhysXSimulationContext

}

fun PxVehiclePhysXSimulationContext(_module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXSimulationContext = js("new _module.PxVehiclePhysXSimulationContext()")

fun PxVehiclePhysXSimulationContextFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXSimulationContext = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSimulationContext)")

var PxVehiclePhysXSimulationContext.physxActorUpdateModeEnum: PxVehiclePhysXActorUpdateModeEnum
    get() = PxVehiclePhysXActorUpdateModeEnum.forValue(physxActorUpdateMode)
    set(value) { physxActorUpdateMode = value.value }

external interface PxVehiclePhysXSteerState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var previousSteerCommand: Float

    fun setToDefault()

}

fun PxVehiclePhysXSteerStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXSteerState = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSteerState)")

external interface PxVehiclePhysXSuspensionLimitConstraintParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var restitution: Float
    /**
     * WebIDL type: [PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum] (enum)
     */
    var directionForSuspensionLimitConstraint: Int

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehiclePhysXSuspensionLimitConstraintParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehiclePhysXSuspensionLimitConstraintParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehiclePhysXSuspensionLimitConstraintParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePhysXSuspensionLimitConstraintParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSuspensionLimitConstraintParams)")

var PxVehiclePhysXSuspensionLimitConstraintParams.directionForSuspensionLimitConstraintEnum: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum
    get() = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum.forValue(directionForSuspensionLimitConstraint)
    set(value) { directionForSuspensionLimitConstraint = value.value }

external interface PxVehiclePvdContext : JsAny

fun PxVehiclePvdContextFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehiclePvdContext = js("_module.wrapPointer(ptr, _module.PxVehiclePvdContext)")

external interface PxVehicleRigidBodyParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var mass: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var moi: PxVec3

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleRigidBodyParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleRigidBodyParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleRigidBodyParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleRigidBodyParams = js("new _module.PxVehicleRigidBodyParams()")

fun PxVehicleRigidBodyParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleRigidBodyParams = js("_module.wrapPointer(ptr, _module.PxVehicleRigidBodyParams)")

external interface PxVehicleRigidBodyState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var pose: PxTransform
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var linearVelocity: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var angularVelocity: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var previousLinearVelocity: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var previousAngularVelocity: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var externalForce: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var externalTorque: PxVec3

    fun setToDefault()

    /**
     * @param frame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @return WebIDL type: float
     */
    fun getVerticalSpeed(frame: PxVehicleFrame): Float

    /**
     * @param frame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @return WebIDL type: float
     */
    fun getLateralSpeed(frame: PxVehicleFrame): Float

    /**
     * @param frame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @return WebIDL type: float
     */
    fun getLongitudinalSpeed(frame: PxVehicleFrame): Float

}

fun PxVehicleRigidBodyState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleRigidBodyState = js("new _module.PxVehicleRigidBodyState()")

fun PxVehicleRigidBodyStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleRigidBodyState = js("_module.wrapPointer(ptr, _module.PxVehicleRigidBodyState)")

external interface PxVehicleRoadGeometryState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxPlane] (Value)
     */
    var plane: PxPlane
    /**
     * WebIDL type: float
     */
    var friction: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var velocity: PxVec3
    /**
     * WebIDL type: boolean
     */
    var hitState: Boolean

    fun setToDefault()

}

fun PxVehicleRoadGeometryState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleRoadGeometryState = js("new _module.PxVehicleRoadGeometryState()")

fun PxVehicleRoadGeometryStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleRoadGeometryState = js("_module.wrapPointer(ptr, _module.PxVehicleRoadGeometryState)")

external interface PxVehicleScale : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var scale: Float

    fun setToDefault()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleScale(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleScale = js("new _module.PxVehicleScale()")

fun PxVehicleScaleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleScale = js("_module.wrapPointer(ptr, _module.PxVehicleScale)")

external interface PxVehicleSimulationContext : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var gravity: PxVec3
    /**
     * WebIDL type: [PxVehicleFrame] (Value)
     */
    var frame: PxVehicleFrame
    /**
     * WebIDL type: [PxVehicleScale] (Value)
     */
    var scale: PxVehicleScale
    /**
     * WebIDL type: [PxVehicleTireSlipParams] (Value)
     */
    var tireSlipParams: PxVehicleTireSlipParams
    /**
     * WebIDL type: [PxVehicleTireStickyParams] (Value)
     */
    var tireStickyParams: PxVehicleTireStickyParams
    /**
     * WebIDL type: float
     */
    var thresholdForwardSpeedForWheelAngleIntegration: Float
    /**
     * WebIDL type: [PxVehiclePvdContext] (Value)
     */
    var pvdContext: PxVehiclePvdContext

    /**
     * @return WebIDL type: [PxVehicleSimulationContextTypeEnum] (enum)
     */
    fun getType(): Int

    fun setToDefault()

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSimulationContext] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSimulationContext

}

fun PxVehicleSimulationContext(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSimulationContext = js("new _module.PxVehicleSimulationContext()")

fun PxVehicleSimulationContextFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSimulationContext = js("_module.wrapPointer(ptr, _module.PxVehicleSimulationContext)")

val PxVehicleSimulationContext.type: PxVehicleSimulationContextTypeEnum
    get() = PxVehicleSimulationContextTypeEnum.forValue(getType())

external interface PxVehicleSteerCommandResponseParams : JsAny, DestroyableNative, PxVehicleCommandResponseParams {
    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSteerCommandResponseParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSteerCommandResponseParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun PxVehicleSteerCommandResponseParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSteerCommandResponseParams = js("new _module.PxVehicleSteerCommandResponseParams()")

fun PxVehicleSteerCommandResponseParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSteerCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleSteerCommandResponseParams)")

external interface PxVehicleSuspensionComplianceParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleFixedSizeLookupTableFloat_3] (Value)
     */
    var wheelToeAngle: PxVehicleFixedSizeLookupTableFloat_3
    /**
     * WebIDL type: [PxVehicleFixedSizeLookupTableFloat_3] (Value)
     */
    var wheelCamberAngle: PxVehicleFixedSizeLookupTableFloat_3
    /**
     * WebIDL type: [PxVehicleFixedSizeLookupTableVec3_3] (Value)
     */
    var suspForceAppPoint: PxVehicleFixedSizeLookupTableVec3_3
    /**
     * WebIDL type: [PxVehicleFixedSizeLookupTableVec3_3] (Value)
     */
    var tireForceAppPoint: PxVehicleFixedSizeLookupTableVec3_3

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSuspensionComplianceParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSuspensionComplianceParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleSuspensionComplianceParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceParams = js("new _module.PxVehicleSuspensionComplianceParams()")

fun PxVehicleSuspensionComplianceParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionComplianceParams)")

external interface PxVehicleSuspensionComplianceState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var toe: Float
    /**
     * WebIDL type: float
     */
    var camber: Float
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var tireForceAppPoint: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspForceAppPoint: PxVec3

    fun setToDefault()

}

fun PxVehicleSuspensionComplianceState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceState = js("new _module.PxVehicleSuspensionComplianceState()")

fun PxVehicleSuspensionComplianceStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceState = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionComplianceState)")

external interface PxVehicleSuspensionForce : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var force: PxVec3
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var torque: PxVec3
    /**
     * WebIDL type: float
     */
    var normalForce: Float

    fun setToDefault()

}

fun PxVehicleSuspensionForce(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionForce = js("new _module.PxVehicleSuspensionForce()")

fun PxVehicleSuspensionForceFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionForce = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionForce)")

external interface PxVehicleSuspensionForceParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var stiffness: Float
    /**
     * WebIDL type: float
     */
    var damping: Float
    /**
     * WebIDL type: float
     */
    var sprungMass: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSuspensionForceParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSuspensionForceParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleSuspensionForceParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionForceParams = js("new _module.PxVehicleSuspensionForceParams()")

fun PxVehicleSuspensionForceParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionForceParams)")

external interface PxVehicleSuspensionParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var suspensionAttachment: PxTransform
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    var suspensionTravelDir: PxVec3
    /**
     * WebIDL type: float
     */
    var suspensionTravelDist: Float
    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var wheelAttachment: PxTransform

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSuspensionParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSuspensionParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleSuspensionParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionParams = js("new _module.PxVehicleSuspensionParams()")

fun PxVehicleSuspensionParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionParams)")

external interface PxVehicleSuspensionState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var jounce: Float
    /**
     * WebIDL type: float
     */
    var jounceSpeed: Float
    /**
     * WebIDL type: float
     */
    var separation: Float

    /**
     * @param _jounce     WebIDL type: float
     * @param _separation WebIDL type: float
     */
    fun setToDefault(_jounce: Float, _separation: Float)

}

fun PxVehicleSuspensionState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionState = js("new _module.PxVehicleSuspensionState()")

fun PxVehicleSuspensionStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionState = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionState)")

external interface PxVehicleSuspensionStateCalculationParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleSuspensionJounceCalculationTypeEnum] (enum)
     */
    var suspensionJounceCalculationType: Int
    /**
     * WebIDL type: boolean
     */
    var limitSuspensionExpansionVelocity: Boolean

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleSuspensionStateCalculationParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleSuspensionStateCalculationParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleSuspensionStateCalculationParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionStateCalculationParams = js("new _module.PxVehicleSuspensionStateCalculationParams()")

fun PxVehicleSuspensionStateCalculationParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleSuspensionStateCalculationParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionStateCalculationParams)")

var PxVehicleSuspensionStateCalculationParams.suspensionJounceCalculationTypeEnum: PxVehicleSuspensionJounceCalculationTypeEnum
    get() = PxVehicleSuspensionJounceCalculationTypeEnum.forValue(suspensionJounceCalculationType)
    set(value) { suspensionJounceCalculationType = value.value }

external interface PxVehicleTankDriveDifferentialParams : JsAny, DestroyableNative, PxVehicleMultiWheelDriveDifferentialParams {
    /**
     * WebIDL type: unsigned long
     */
    var nbTracks: Int
    /**
     * WebIDL type: unsigned long
     */
    fun get_thrustIdPerTrack(index: Int): Int
    fun set_thrustIdPerTrack(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_nbWheelsPerTrack(index: Int): Int
    fun set_nbWheelsPerTrack(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_trackToWheelIds(index: Int): Int
    fun set_trackToWheelIds(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_wheelIdsInTrackOrder(index: Int): Int
    fun set_wheelIdsInTrackOrder(index: Int, value: Int)

    override fun setToDefault()

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelsInTrack(i: Int): Int

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: [PxU32ConstPtr] (Value)
     */
    fun getWheelsInTrack(i: Int): PxU32ConstPtr

    /**
     * @param j WebIDL type: unsigned long
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getWheelInTrack(j: Int, i: Int): Int

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getThrustControllerIndex(i: Int): Int

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleTankDriveDifferentialParams] (Value)
     */
    override fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleTankDriveDifferentialParams

}

fun PxVehicleTankDriveDifferentialParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTankDriveDifferentialParams = js("new _module.PxVehicleTankDriveDifferentialParams()")

fun PxVehicleTankDriveDifferentialParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTankDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleTankDriveDifferentialParams)")

inline fun PxVehicleTankDriveDifferentialParams.getThrustIdPerTrack(index: Int) = get_thrustIdPerTrack(index)
inline fun PxVehicleTankDriveDifferentialParams.setThrustIdPerTrack(index: Int, value: Int) = set_thrustIdPerTrack(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getNbWheelsPerTrack(index: Int) = get_nbWheelsPerTrack(index)
inline fun PxVehicleTankDriveDifferentialParams.setNbWheelsPerTrack(index: Int, value: Int) = set_nbWheelsPerTrack(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getTrackToWheelIds(index: Int) = get_trackToWheelIds(index)
inline fun PxVehicleTankDriveDifferentialParams.setTrackToWheelIds(index: Int, value: Int) = set_trackToWheelIds(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getWheelIdsInTrackOrder(index: Int) = get_wheelIdsInTrackOrder(index)
inline fun PxVehicleTankDriveDifferentialParams.setWheelIdsInTrackOrder(index: Int, value: Int) = set_wheelIdsInTrackOrder(index, value)

external interface PxVehicleTankDriveTransmissionCommandState : JsAny, DestroyableNative, PxVehicleEngineDriveTransmissionCommandState {
    /**
     * WebIDL type: float
     */
    fun get_thrusts(index: Int): Float
    fun set_thrusts(index: Int, value: Float)

    override fun setToDefault()

}

fun PxVehicleTankDriveTransmissionCommandState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTankDriveTransmissionCommandState = js("new _module.PxVehicleTankDriveTransmissionCommandState()")

fun PxVehicleTankDriveTransmissionCommandStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTankDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleTankDriveTransmissionCommandState)")

inline fun PxVehicleTankDriveTransmissionCommandState.getThrusts(index: Int) = get_thrusts(index)
inline fun PxVehicleTankDriveTransmissionCommandState.setThrusts(index: Int, value: Float) = set_thrusts(index, value)

external interface PxVehicleTireAxisStickyParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var thresholdSpeed: Float
    /**
     * WebIDL type: float
     */
    var thresholdTime: Float
    /**
     * WebIDL type: float
     */
    var damping: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleTireAxisStickyParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleTireAxisStickyParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleTireAxisStickyParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireAxisStickyParams = js("new _module.PxVehicleTireAxisStickyParams()")

fun PxVehicleTireAxisStickyParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireAxisStickyParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireAxisStickyParams)")

external interface PxVehicleTireCamberAngleState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var camberAngle: Float

    fun setToDefault()

}

fun PxVehicleTireCamberAngleState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireCamberAngleState = js("new _module.PxVehicleTireCamberAngleState()")

fun PxVehicleTireCamberAngleStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireCamberAngleState = js("_module.wrapPointer(ptr, _module.PxVehicleTireCamberAngleState)")

external interface PxVehicleTireDirectionState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    fun get_directions(index: Int): PxVec3
    fun set_directions(index: Int, value: PxVec3)

    fun setToDefault()

}

fun PxVehicleTireDirectionState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireDirectionState = js("new _module.PxVehicleTireDirectionState()")

fun PxVehicleTireDirectionStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireDirectionState = js("_module.wrapPointer(ptr, _module.PxVehicleTireDirectionState)")

inline fun PxVehicleTireDirectionState.getDirections(index: Int) = get_directions(index)
inline fun PxVehicleTireDirectionState.setDirections(index: Int, value: PxVec3) = set_directions(index, value)

external interface PxVehicleTireForce : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVec3] (Value)
     */
    fun get_forces(index: Int): PxVec3
    fun set_forces(index: Int, value: PxVec3)
    /**
     * WebIDL type: [PxVec3] (Value)
     */
    fun get_torques(index: Int): PxVec3
    fun set_torques(index: Int, value: PxVec3)
    /**
     * WebIDL type: float
     */
    var aligningMoment: Float
    /**
     * WebIDL type: float
     */
    var wheelTorque: Float

    fun setToDefault()

}

fun PxVehicleTireForce(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireForce = js("new _module.PxVehicleTireForce()")

fun PxVehicleTireForceFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireForce = js("_module.wrapPointer(ptr, _module.PxVehicleTireForce)")

inline fun PxVehicleTireForce.getForces(index: Int) = get_forces(index)
inline fun PxVehicleTireForce.setForces(index: Int, value: PxVec3) = set_forces(index, value)
inline fun PxVehicleTireForce.getTorques(index: Int) = get_torques(index)
inline fun PxVehicleTireForce.setTorques(index: Int, value: PxVec3) = set_torques(index, value)

external interface PxVehicleTireForceParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var latStiffX: Float
    /**
     * WebIDL type: float
     */
    var latStiffY: Float
    /**
     * WebIDL type: float
     */
    var longStiff: Float
    /**
     * WebIDL type: float
     */
    var camberStiff: Float
    /**
     * WebIDL type: float
     */
    var restLoad: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleTireForceParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleTireForceParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleTireForceParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireForceParams = js("new _module.PxVehicleTireForceParams()")

fun PxVehicleTireForceParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireForceParams)")

external interface PxVehicleTireForceParamsExt : JsAny {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param tireForceParams WebIDL type: [PxVehicleTireForceParams]
     * @param i               WebIDL type: unsigned long
     * @param j               WebIDL type: unsigned long
     * @param value           WebIDL type: float
     */
    fun setFrictionVsSlip(tireForceParams: PxVehicleTireForceParams, i: Int, j: Int, value: Float)

    /**
     * @param tireForceParams WebIDL type: [PxVehicleTireForceParams]
     * @param i               WebIDL type: unsigned long
     * @param j               WebIDL type: unsigned long
     * @param value           WebIDL type: float
     */
    fun setLoadFilter(tireForceParams: PxVehicleTireForceParams, i: Int, j: Int, value: Float)

}

fun PxVehicleTireForceParamsExtFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireForceParamsExt = js("_module.wrapPointer(ptr, _module.PxVehicleTireForceParamsExt)")

external interface PxVehicleTireGripState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var load: Float
    /**
     * WebIDL type: float
     */
    var friction: Float

    fun setToDefault()

}

fun PxVehicleTireGripStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireGripState = js("_module.wrapPointer(ptr, _module.PxVehicleTireGripState)")

external interface PxVehicleTireSlipParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var minLatSlipDenominator: Float
    /**
     * WebIDL type: float
     */
    var minPassiveLongSlipDenominator: Float
    /**
     * WebIDL type: float
     */
    var minActiveLongSlipDenominator: Float

    fun setToDefault()

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleTireSlipParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleTireSlipParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleTireSlipParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSlipParams = js("new _module.PxVehicleTireSlipParams()")

fun PxVehicleTireSlipParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSlipParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireSlipParams)")

external interface PxVehicleTireSlipState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_slips(index: Int): Float
    fun set_slips(index: Int, value: Float)

    fun setToDefault()

}

fun PxVehicleTireSlipState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSlipState = js("new _module.PxVehicleTireSlipState()")

fun PxVehicleTireSlipStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSlipState = js("_module.wrapPointer(ptr, _module.PxVehicleTireSlipState)")

inline fun PxVehicleTireSlipState.getSlips(index: Int) = get_slips(index)
inline fun PxVehicleTireSlipState.setSlips(index: Int, value: Float) = set_slips(index, value)

external interface PxVehicleTireSpeedState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_speedStates(index: Int): Float
    fun set_speedStates(index: Int, value: Float)

    fun setToDefault()

}

fun PxVehicleTireSpeedState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSpeedState = js("new _module.PxVehicleTireSpeedState()")

fun PxVehicleTireSpeedStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireSpeedState = js("_module.wrapPointer(ptr, _module.PxVehicleTireSpeedState)")

inline fun PxVehicleTireSpeedState.getSpeedStates(index: Int) = get_speedStates(index)
inline fun PxVehicleTireSpeedState.setSpeedStates(index: Int, value: Float) = set_speedStates(index, value)

external interface PxVehicleTireStickyParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleTireAxisStickyParams] (Value)
     */
    fun get_stickyParams(index: Int): PxVehicleTireAxisStickyParams
    fun set_stickyParams(index: Int, value: PxVehicleTireAxisStickyParams)

    fun setToDefault()

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleTireStickyParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleTireStickyParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleTireStickyParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireStickyParams = js("new _module.PxVehicleTireStickyParams()")

fun PxVehicleTireStickyParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireStickyParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireStickyParams)")

inline fun PxVehicleTireStickyParams.getStickyParams(index: Int) = get_stickyParams(index)
inline fun PxVehicleTireStickyParams.setStickyParams(index: Int, value: PxVehicleTireAxisStickyParams) = set_stickyParams(index, value)

external interface PxVehicleTireStickyState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_lowSpeedTime(index: Int): Float
    fun set_lowSpeedTime(index: Int, value: Float)
    /**
     * WebIDL type: boolean
     */
    fun get_activeStatus(index: Int): Boolean
    fun set_activeStatus(index: Int, value: Boolean)

    fun setToDefault()

}

fun PxVehicleTireStickyState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireStickyState = js("new _module.PxVehicleTireStickyState()")

fun PxVehicleTireStickyStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTireStickyState = js("_module.wrapPointer(ptr, _module.PxVehicleTireStickyState)")

inline fun PxVehicleTireStickyState.getLowSpeedTime(index: Int) = get_lowSpeedTime(index)
inline fun PxVehicleTireStickyState.setLowSpeedTime(index: Int, value: Float) = set_lowSpeedTime(index, value)
inline fun PxVehicleTireStickyState.getActiveStatus(index: Int) = get_activeStatus(index)
inline fun PxVehicleTireStickyState.setActiveStatus(index: Int, value: Boolean) = set_activeStatus(index, value)

external interface PxVehicleTorqueCurveLookupTable : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param x WebIDL type: float
     * @param y WebIDL type: float
     * @return WebIDL type: boolean
     */
    fun addPair(x: Float, y: Float): Boolean

    /**
     * @param x WebIDL type: float
     * @return WebIDL type: float
     */
    fun interpolate(x: Float): Float

    fun clear()

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleTorqueCurveLookupTable(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleTorqueCurveLookupTable = js("new _module.PxVehicleTorqueCurveLookupTable()")

fun PxVehicleTorqueCurveLookupTableFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleTorqueCurveLookupTable = js("_module.wrapPointer(ptr, _module.PxVehicleTorqueCurveLookupTable)")

external interface PxVehicleWheelActuationState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var isBrakeApplied: Boolean
    /**
     * WebIDL type: boolean
     */
    var isDriveApplied: Boolean

    fun setToDefault()

}

fun PxVehicleWheelActuationState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelActuationState = js("new _module.PxVehicleWheelActuationState()")

fun PxVehicleWheelActuationStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelActuationState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelActuationState)")

external interface PxVehicleWheelConstraintGroupState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var nbGroups: Int
    /**
     * WebIDL type: unsigned long
     */
    fun get_nbWheelsPerGroup(index: Int): Int
    fun set_nbWheelsPerGroup(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_groupToWheelIds(index: Int): Int
    fun set_groupToWheelIds(index: Int, value: Int)
    /**
     * WebIDL type: unsigned long
     */
    fun get_wheelIdsInGroupOrder(index: Int): Int
    fun set_wheelIdsInGroupOrder(index: Int, value: Int)
    /**
     * WebIDL type: float
     */
    fun get_wheelMultipliersInGroupOrder(index: Int): Float
    fun set_wheelMultipliersInGroupOrder(index: Int, value: Float)
    /**
     * WebIDL type: unsigned long
     */
    var nbWheelsInGroups: Int

    fun setToDefault()

    /**
     * @return WebIDL type: unsigned long
     */
    fun getNbConstraintGroups(): Int

    /**
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getNbWheelsInConstraintGroup(i: Int): Int

    /**
     * @param j WebIDL type: unsigned long
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: unsigned long
     */
    fun getWheelInConstraintGroup(j: Int, i: Int): Int

    /**
     * @param j WebIDL type: unsigned long
     * @param i WebIDL type: unsigned long
     * @return WebIDL type: float
     */
    fun getMultiplierInConstraintGroup(j: Int, i: Int): Float

}

fun PxVehicleWheelConstraintGroupState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelConstraintGroupState = js("new _module.PxVehicleWheelConstraintGroupState()")

fun PxVehicleWheelConstraintGroupStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelConstraintGroupState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelConstraintGroupState)")

val PxVehicleWheelConstraintGroupState.nbConstraintGroups
    get() = getNbConstraintGroups()

inline fun PxVehicleWheelConstraintGroupState.getNbWheelsPerGroup(index: Int) = get_nbWheelsPerGroup(index)
inline fun PxVehicleWheelConstraintGroupState.setNbWheelsPerGroup(index: Int, value: Int) = set_nbWheelsPerGroup(index, value)
inline fun PxVehicleWheelConstraintGroupState.getGroupToWheelIds(index: Int) = get_groupToWheelIds(index)
inline fun PxVehicleWheelConstraintGroupState.setGroupToWheelIds(index: Int, value: Int) = set_groupToWheelIds(index, value)
inline fun PxVehicleWheelConstraintGroupState.getWheelIdsInGroupOrder(index: Int) = get_wheelIdsInGroupOrder(index)
inline fun PxVehicleWheelConstraintGroupState.setWheelIdsInGroupOrder(index: Int, value: Int) = set_wheelIdsInGroupOrder(index, value)
inline fun PxVehicleWheelConstraintGroupState.getWheelMultipliersInGroupOrder(index: Int) = get_wheelMultipliersInGroupOrder(index)
inline fun PxVehicleWheelConstraintGroupState.setWheelMultipliersInGroupOrder(index: Int, value: Float) = set_wheelMultipliersInGroupOrder(index, value)

external interface PxVehicleWheelLocalPose : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var localPose: PxTransform

    fun setToDefault()

}

fun PxVehicleWheelLocalPose(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelLocalPose = js("new _module.PxVehicleWheelLocalPose()")

fun PxVehicleWheelLocalPoseFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelLocalPose = js("_module.wrapPointer(ptr, _module.PxVehicleWheelLocalPose)")

external interface PxVehicleWheelParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var radius: Float
    /**
     * WebIDL type: float
     */
    var halfWidth: Float
    /**
     * WebIDL type: float
     */
    var mass: Float
    /**
     * WebIDL type: float
     */
    var moi: Float
    /**
     * WebIDL type: float
     */
    var dampingRate: Float

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PxVehicleWheelParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PxVehicleWheelParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun PxVehicleWheelParams(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelParams = js("new _module.PxVehicleWheelParams()")

fun PxVehicleWheelParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelParams = js("_module.wrapPointer(ptr, _module.PxVehicleWheelParams)")

external interface PxVehicleWheelRigidBody1dState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var rotationSpeed: Float
    /**
     * WebIDL type: float
     */
    var correctedRotationSpeed: Float
    /**
     * WebIDL type: float
     */
    var rotationAngle: Float

    fun setToDefault()

}

fun PxVehicleWheelRigidBody1dState(_module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelRigidBody1dState = js("new _module.PxVehicleWheelRigidBody1dState()")

fun PxVehicleWheelRigidBody1dStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PxVehicleWheelRigidBody1dState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelRigidBody1dState)")

external interface BaseVehicleParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleAxleDescription] (Value)
     */
    var axleDescription: PxVehicleAxleDescription
    /**
     * WebIDL type: [PxVehicleFrame] (Value)
     */
    var frame: PxVehicleFrame
    /**
     * WebIDL type: [PxVehicleScale] (Value)
     */
    var scale: PxVehicleScale
    /**
     * WebIDL type: [PxVehicleSuspensionStateCalculationParams] (Value)
     */
    var suspensionStateCalculationParams: PxVehicleSuspensionStateCalculationParams
    /**
     * WebIDL type: [PxVehicleBrakeCommandResponseParams] (Value)
     */
    fun get_brakeResponseParams(index: Int): PxVehicleBrakeCommandResponseParams
    fun set_brakeResponseParams(index: Int, value: PxVehicleBrakeCommandResponseParams)
    /**
     * WebIDL type: [PxVehicleSteerCommandResponseParams] (Value)
     */
    var steerResponseParams: PxVehicleSteerCommandResponseParams
    /**
     * WebIDL type: [PxVehicleAckermannParams] (Value)
     */
    fun get_ackermannParams(index: Int): PxVehicleAckermannParams
    fun set_ackermannParams(index: Int, value: PxVehicleAckermannParams)
    /**
     * WebIDL type: [PxVehicleSuspensionParams] (Value)
     */
    fun get_suspensionParams(index: Int): PxVehicleSuspensionParams
    fun set_suspensionParams(index: Int, value: PxVehicleSuspensionParams)
    /**
     * WebIDL type: [PxVehicleSuspensionComplianceParams] (Value)
     */
    fun get_suspensionComplianceParams(index: Int): PxVehicleSuspensionComplianceParams
    fun set_suspensionComplianceParams(index: Int, value: PxVehicleSuspensionComplianceParams)
    /**
     * WebIDL type: [PxVehicleSuspensionForceParams] (Value)
     */
    fun get_suspensionForceParams(index: Int): PxVehicleSuspensionForceParams
    fun set_suspensionForceParams(index: Int, value: PxVehicleSuspensionForceParams)
    /**
     * WebIDL type: [PxVehicleAntiRollForceParams] (Value)
     */
    fun get_antiRollForceParams(index: Int): PxVehicleAntiRollForceParams
    fun set_antiRollForceParams(index: Int, value: PxVehicleAntiRollForceParams)
    /**
     * WebIDL type: unsigned long
     */
    var nbAntiRollForceParams: Int
    /**
     * WebIDL type: [PxVehicleTireForceParams] (Value)
     */
    fun get_tireForceParams(index: Int): PxVehicleTireForceParams
    fun set_tireForceParams(index: Int, value: PxVehicleTireForceParams)
    /**
     * WebIDL type: [PxVehicleWheelParams] (Value)
     */
    fun get_wheelParams(index: Int): PxVehicleWheelParams
    fun set_wheelParams(index: Int, value: PxVehicleWheelParams)
    /**
     * WebIDL type: [PxVehicleRigidBodyParams] (Value)
     */
    var rigidBodyParams: PxVehicleRigidBodyParams

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [BaseVehicleParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): BaseVehicleParams

    /**
     * @return WebIDL type: boolean
     */
    fun isValid(): Boolean

}

fun BaseVehicleParams(_module: JsAny = PhysXJsLoader.physXJs): BaseVehicleParams = js("new _module.BaseVehicleParams()")

fun BaseVehicleParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): BaseVehicleParams = js("_module.wrapPointer(ptr, _module.BaseVehicleParams)")

inline fun BaseVehicleParams.getBrakeResponseParams(index: Int) = get_brakeResponseParams(index)
inline fun BaseVehicleParams.setBrakeResponseParams(index: Int, value: PxVehicleBrakeCommandResponseParams) = set_brakeResponseParams(index, value)
inline fun BaseVehicleParams.getAckermannParams(index: Int) = get_ackermannParams(index)
inline fun BaseVehicleParams.setAckermannParams(index: Int, value: PxVehicleAckermannParams) = set_ackermannParams(index, value)
inline fun BaseVehicleParams.getSuspensionParams(index: Int) = get_suspensionParams(index)
inline fun BaseVehicleParams.setSuspensionParams(index: Int, value: PxVehicleSuspensionParams) = set_suspensionParams(index, value)
inline fun BaseVehicleParams.getSuspensionComplianceParams(index: Int) = get_suspensionComplianceParams(index)
inline fun BaseVehicleParams.setSuspensionComplianceParams(index: Int, value: PxVehicleSuspensionComplianceParams) = set_suspensionComplianceParams(index, value)
inline fun BaseVehicleParams.getSuspensionForceParams(index: Int) = get_suspensionForceParams(index)
inline fun BaseVehicleParams.setSuspensionForceParams(index: Int, value: PxVehicleSuspensionForceParams) = set_suspensionForceParams(index, value)
inline fun BaseVehicleParams.getAntiRollForceParams(index: Int) = get_antiRollForceParams(index)
inline fun BaseVehicleParams.setAntiRollForceParams(index: Int, value: PxVehicleAntiRollForceParams) = set_antiRollForceParams(index, value)
inline fun BaseVehicleParams.getTireForceParams(index: Int) = get_tireForceParams(index)
inline fun BaseVehicleParams.setTireForceParams(index: Int, value: PxVehicleTireForceParams) = set_tireForceParams(index, value)
inline fun BaseVehicleParams.getWheelParams(index: Int) = get_wheelParams(index)
inline fun BaseVehicleParams.setWheelParams(index: Int, value: PxVehicleWheelParams) = set_wheelParams(index, value)

external interface BaseVehicleState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_brakeCommandResponseStates(index: Int): Float
    fun set_brakeCommandResponseStates(index: Int, value: Float)
    /**
     * WebIDL type: float
     */
    fun get_steerCommandResponseStates(index: Int): Float
    fun set_steerCommandResponseStates(index: Int, value: Float)
    /**
     * WebIDL type: [PxVehicleWheelActuationState] (Value)
     */
    fun get_actuationStates(index: Int): PxVehicleWheelActuationState
    fun set_actuationStates(index: Int, value: PxVehicleWheelActuationState)
    /**
     * WebIDL type: [PxVehicleRoadGeometryState] (Value)
     */
    fun get_roadGeomStates(index: Int): PxVehicleRoadGeometryState
    fun set_roadGeomStates(index: Int, value: PxVehicleRoadGeometryState)
    /**
     * WebIDL type: [PxVehicleSuspensionState] (Value)
     */
    fun get_suspensionStates(index: Int): PxVehicleSuspensionState
    fun set_suspensionStates(index: Int, value: PxVehicleSuspensionState)
    /**
     * WebIDL type: [PxVehicleSuspensionComplianceState] (Value)
     */
    fun get_suspensionComplianceStates(index: Int): PxVehicleSuspensionComplianceState
    fun set_suspensionComplianceStates(index: Int, value: PxVehicleSuspensionComplianceState)
    /**
     * WebIDL type: [PxVehicleSuspensionForce] (Value)
     */
    fun get_suspensionForces(index: Int): PxVehicleSuspensionForce
    fun set_suspensionForces(index: Int, value: PxVehicleSuspensionForce)
    /**
     * WebIDL type: [PxVehicleAntiRollTorque] (Value)
     */
    var antiRollTorque: PxVehicleAntiRollTorque
    /**
     * WebIDL type: [PxVehicleTireGripState] (Value)
     */
    fun get_tireGripStates(index: Int): PxVehicleTireGripState
    fun set_tireGripStates(index: Int, value: PxVehicleTireGripState)
    /**
     * WebIDL type: [PxVehicleTireDirectionState] (Value)
     */
    fun get_tireDirectionStates(index: Int): PxVehicleTireDirectionState
    fun set_tireDirectionStates(index: Int, value: PxVehicleTireDirectionState)
    /**
     * WebIDL type: [PxVehicleTireSpeedState] (Value)
     */
    fun get_tireSpeedStates(index: Int): PxVehicleTireSpeedState
    fun set_tireSpeedStates(index: Int, value: PxVehicleTireSpeedState)
    /**
     * WebIDL type: [PxVehicleTireSlipState] (Value)
     */
    fun get_tireSlipStates(index: Int): PxVehicleTireSlipState
    fun set_tireSlipStates(index: Int, value: PxVehicleTireSlipState)
    /**
     * WebIDL type: [PxVehicleTireCamberAngleState] (Value)
     */
    fun get_tireCamberAngleStates(index: Int): PxVehicleTireCamberAngleState
    fun set_tireCamberAngleStates(index: Int, value: PxVehicleTireCamberAngleState)
    /**
     * WebIDL type: [PxVehicleTireStickyState] (Value)
     */
    fun get_tireStickyStates(index: Int): PxVehicleTireStickyState
    fun set_tireStickyStates(index: Int, value: PxVehicleTireStickyState)
    /**
     * WebIDL type: [PxVehicleTireForce] (Value)
     */
    fun get_tireForces(index: Int): PxVehicleTireForce
    fun set_tireForces(index: Int, value: PxVehicleTireForce)
    /**
     * WebIDL type: [PxVehicleWheelRigidBody1dState] (Value)
     */
    fun get_wheelRigidBody1dStates(index: Int): PxVehicleWheelRigidBody1dState
    fun set_wheelRigidBody1dStates(index: Int, value: PxVehicleWheelRigidBody1dState)
    /**
     * WebIDL type: [PxVehicleWheelLocalPose] (Value)
     */
    fun get_wheelLocalPoses(index: Int): PxVehicleWheelLocalPose
    fun set_wheelLocalPoses(index: Int, value: PxVehicleWheelLocalPose)
    /**
     * WebIDL type: [PxVehicleRigidBodyState] (Value)
     */
    var rigidBodyState: PxVehicleRigidBodyState

    fun setToDefault()

}

fun BaseVehicleState(_module: JsAny = PhysXJsLoader.physXJs): BaseVehicleState = js("new _module.BaseVehicleState()")

fun BaseVehicleStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): BaseVehicleState = js("_module.wrapPointer(ptr, _module.BaseVehicleState)")

inline fun BaseVehicleState.getBrakeCommandResponseStates(index: Int) = get_brakeCommandResponseStates(index)
inline fun BaseVehicleState.setBrakeCommandResponseStates(index: Int, value: Float) = set_brakeCommandResponseStates(index, value)
inline fun BaseVehicleState.getSteerCommandResponseStates(index: Int) = get_steerCommandResponseStates(index)
inline fun BaseVehicleState.setSteerCommandResponseStates(index: Int, value: Float) = set_steerCommandResponseStates(index, value)
inline fun BaseVehicleState.getActuationStates(index: Int) = get_actuationStates(index)
inline fun BaseVehicleState.setActuationStates(index: Int, value: PxVehicleWheelActuationState) = set_actuationStates(index, value)
inline fun BaseVehicleState.getRoadGeomStates(index: Int) = get_roadGeomStates(index)
inline fun BaseVehicleState.setRoadGeomStates(index: Int, value: PxVehicleRoadGeometryState) = set_roadGeomStates(index, value)
inline fun BaseVehicleState.getSuspensionStates(index: Int) = get_suspensionStates(index)
inline fun BaseVehicleState.setSuspensionStates(index: Int, value: PxVehicleSuspensionState) = set_suspensionStates(index, value)
inline fun BaseVehicleState.getSuspensionComplianceStates(index: Int) = get_suspensionComplianceStates(index)
inline fun BaseVehicleState.setSuspensionComplianceStates(index: Int, value: PxVehicleSuspensionComplianceState) = set_suspensionComplianceStates(index, value)
inline fun BaseVehicleState.getSuspensionForces(index: Int) = get_suspensionForces(index)
inline fun BaseVehicleState.setSuspensionForces(index: Int, value: PxVehicleSuspensionForce) = set_suspensionForces(index, value)
inline fun BaseVehicleState.getTireGripStates(index: Int) = get_tireGripStates(index)
inline fun BaseVehicleState.setTireGripStates(index: Int, value: PxVehicleTireGripState) = set_tireGripStates(index, value)
inline fun BaseVehicleState.getTireDirectionStates(index: Int) = get_tireDirectionStates(index)
inline fun BaseVehicleState.setTireDirectionStates(index: Int, value: PxVehicleTireDirectionState) = set_tireDirectionStates(index, value)
inline fun BaseVehicleState.getTireSpeedStates(index: Int) = get_tireSpeedStates(index)
inline fun BaseVehicleState.setTireSpeedStates(index: Int, value: PxVehicleTireSpeedState) = set_tireSpeedStates(index, value)
inline fun BaseVehicleState.getTireSlipStates(index: Int) = get_tireSlipStates(index)
inline fun BaseVehicleState.setTireSlipStates(index: Int, value: PxVehicleTireSlipState) = set_tireSlipStates(index, value)
inline fun BaseVehicleState.getTireCamberAngleStates(index: Int) = get_tireCamberAngleStates(index)
inline fun BaseVehicleState.setTireCamberAngleStates(index: Int, value: PxVehicleTireCamberAngleState) = set_tireCamberAngleStates(index, value)
inline fun BaseVehicleState.getTireStickyStates(index: Int) = get_tireStickyStates(index)
inline fun BaseVehicleState.setTireStickyStates(index: Int, value: PxVehicleTireStickyState) = set_tireStickyStates(index, value)
inline fun BaseVehicleState.getTireForces(index: Int) = get_tireForces(index)
inline fun BaseVehicleState.setTireForces(index: Int, value: PxVehicleTireForce) = set_tireForces(index, value)
inline fun BaseVehicleState.getWheelRigidBody1dStates(index: Int) = get_wheelRigidBody1dStates(index)
inline fun BaseVehicleState.setWheelRigidBody1dStates(index: Int, value: PxVehicleWheelRigidBody1dState) = set_wheelRigidBody1dStates(index, value)
inline fun BaseVehicleState.getWheelLocalPoses(index: Int) = get_wheelLocalPoses(index)
inline fun BaseVehicleState.setWheelLocalPoses(index: Int, value: PxVehicleWheelLocalPose) = set_wheelLocalPoses(index, value)

external interface PhysXIntegrationParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXRoadGeometryQueryParams] (Value)
     */
    var physxRoadGeometryQueryParams: PxVehiclePhysXRoadGeometryQueryParams
    /**
     * WebIDL type: [PxVehiclePhysXMaterialFrictionParams] (Value)
     */
    fun get_physxMaterialFrictionParams(index: Int): PxVehiclePhysXMaterialFrictionParams
    fun set_physxMaterialFrictionParams(index: Int, value: PxVehiclePhysXMaterialFrictionParams)
    /**
     * WebIDL type: [PxVehiclePhysXSuspensionLimitConstraintParams] (Value)
     */
    fun get_physxSuspensionLimitConstraintParams(index: Int): PxVehiclePhysXSuspensionLimitConstraintParams
    fun set_physxSuspensionLimitConstraintParams(index: Int, value: PxVehiclePhysXSuspensionLimitConstraintParams)
    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var physxActorCMassLocalPose: PxTransform
    /**
     * WebIDL type: [PxGeometry]
     */
    var physxActorGeometry: PxGeometry
    /**
     * WebIDL type: [PxTransform] (Value)
     */
    var physxActorBoxShapeLocalPose: PxTransform
    /**
     * WebIDL type: [PxTransform] (Value)
     */
    fun get_physxWheelShapeLocalPoses(index: Int): PxTransform
    fun set_physxWheelShapeLocalPoses(index: Int, value: PxTransform)
    /**
     * WebIDL type: [PxShapeFlags] (Value)
     */
    var physxActorShapeFlags: PxShapeFlags
    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var physxActorSimulationFilterData: PxFilterData
    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var physxActorQueryFilterData: PxFilterData
    /**
     * WebIDL type: [PxShapeFlags] (Value)
     */
    var physxActorWheelShapeFlags: PxShapeFlags
    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var physxActorWheelSimulationFilterData: PxFilterData
    /**
     * WebIDL type: [PxFilterData] (Value)
     */
    var physxActorWheelQueryFilterData: PxFilterData

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [PhysXIntegrationParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): PhysXIntegrationParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

    /**
     * @param axleDesc                    WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @param roadQueryFilterData         WebIDL type: [PxQueryFilterData] (Const, Ref)
     * @param roadQueryFilterCallback     WebIDL type: [PxQueryFilterCallback] (Nullable)
     * @param materialFrictions           WebIDL type: [PxVehiclePhysXMaterialFriction]
     * @param nbMaterialFrictions         WebIDL type: unsigned long
     * @param defaultFriction             WebIDL type: float
     * @param physxActorCMassLocalPose    WebIDL type: [PxTransform] (Const, Ref)
     * @param actorGeometry               WebIDL type: [PxGeometry] (Ref)
     * @param physxActorBoxShapeLocalPose WebIDL type: [PxTransform] (Const, Ref)
     * @param roadGeometryQueryType       WebIDL type: [PxVehiclePhysXRoadGeometryQueryTypeEnum] (enum)
     */
    fun create(axleDesc: PxVehicleAxleDescription, roadQueryFilterData: PxQueryFilterData, roadQueryFilterCallback: PxQueryFilterCallback?, materialFrictions: PxVehiclePhysXMaterialFriction, nbMaterialFrictions: Int, defaultFriction: Float, physxActorCMassLocalPose: PxTransform, actorGeometry: PxGeometry, physxActorBoxShapeLocalPose: PxTransform, roadGeometryQueryType: Int)

}

fun PhysXIntegrationParams(_module: JsAny = PhysXJsLoader.physXJs): PhysXIntegrationParams = js("new _module.PhysXIntegrationParams()")

fun PhysXIntegrationParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PhysXIntegrationParams = js("_module.wrapPointer(ptr, _module.PhysXIntegrationParams)")

inline fun PhysXIntegrationParams.getPhysxMaterialFrictionParams(index: Int) = get_physxMaterialFrictionParams(index)
inline fun PhysXIntegrationParams.setPhysxMaterialFrictionParams(index: Int, value: PxVehiclePhysXMaterialFrictionParams) = set_physxMaterialFrictionParams(index, value)
inline fun PhysXIntegrationParams.getPhysxSuspensionLimitConstraintParams(index: Int) = get_physxSuspensionLimitConstraintParams(index)
inline fun PhysXIntegrationParams.setPhysxSuspensionLimitConstraintParams(index: Int, value: PxVehiclePhysXSuspensionLimitConstraintParams) = set_physxSuspensionLimitConstraintParams(index, value)
inline fun PhysXIntegrationParams.getPhysxWheelShapeLocalPoses(index: Int) = get_physxWheelShapeLocalPoses(index)
inline fun PhysXIntegrationParams.setPhysxWheelShapeLocalPoses(index: Int, value: PxTransform) = set_physxWheelShapeLocalPoses(index, value)

fun PhysXIntegrationParams.create(axleDesc: PxVehicleAxleDescription, roadQueryFilterData: PxQueryFilterData, roadQueryFilterCallback: PxQueryFilterCallback?, materialFrictions: PxVehiclePhysXMaterialFriction, nbMaterialFrictions: Int, defaultFriction: Float, physxActorCMassLocalPose: PxTransform, actorGeometry: PxGeometry, physxActorBoxShapeLocalPose: PxTransform, roadGeometryQueryType: PxVehiclePhysXRoadGeometryQueryTypeEnum) = create(axleDesc, roadQueryFilterData, roadQueryFilterCallback, materialFrictions, nbMaterialFrictions, defaultFriction, physxActorCMassLocalPose, actorGeometry, physxActorBoxShapeLocalPose, roadGeometryQueryType.value)

external interface PhysXIntegrationState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXActor] (Value)
     */
    var physxActor: PxVehiclePhysXActor
    /**
     * WebIDL type: [PxVehiclePhysXSteerState] (Value)
     */
    var physxSteerState: PxVehiclePhysXSteerState
    /**
     * WebIDL type: [PxVehiclePhysXConstraints] (Value)
     */
    var physxConstraints: PxVehiclePhysXConstraints

    fun destroyState()

    fun setToDefault()

    /**
     * @param baseParams      WebIDL type: [BaseVehicleParams] (Const, Ref)
     * @param physxParams     WebIDL type: [PhysXIntegrationParams] (Const, Ref)
     * @param physics         WebIDL type: [PxPhysics] (Ref)
     * @param params          WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial WebIDL type: [PxMaterial] (Ref)
     */
    fun create(baseParams: BaseVehicleParams, physxParams: PhysXIntegrationParams, physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial)

}

fun PhysXIntegrationState(_module: JsAny = PhysXJsLoader.physXJs): PhysXIntegrationState = js("new _module.PhysXIntegrationState()")

fun PhysXIntegrationStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PhysXIntegrationState = js("_module.wrapPointer(ptr, _module.PhysXIntegrationState)")

external interface DirectDrivetrainParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleDirectDriveThrottleCommandResponseParams] (Value)
     */
    var directDriveThrottleResponseParams: PxVehicleDirectDriveThrottleCommandResponseParams

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [DirectDrivetrainParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): DirectDrivetrainParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun DirectDrivetrainParams(_module: JsAny = PhysXJsLoader.physXJs): DirectDrivetrainParams = js("new _module.DirectDrivetrainParams()")

fun DirectDrivetrainParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): DirectDrivetrainParams = js("_module.wrapPointer(ptr, _module.DirectDrivetrainParams)")

external interface DirectDrivetrainState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    fun get_directDriveThrottleResponseStates(index: Int): Float
    fun set_directDriveThrottleResponseStates(index: Int, value: Float)

    fun setToDefault()

}

fun DirectDrivetrainState(_module: JsAny = PhysXJsLoader.physXJs): DirectDrivetrainState = js("new _module.DirectDrivetrainState()")

fun DirectDrivetrainStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): DirectDrivetrainState = js("_module.wrapPointer(ptr, _module.DirectDrivetrainState)")

inline fun DirectDrivetrainState.getDirectDriveThrottleResponseStates(index: Int) = get_directDriveThrottleResponseStates(index)
inline fun DirectDrivetrainState.setDirectDriveThrottleResponseStates(index: Int, value: Float) = set_directDriveThrottleResponseStates(index, value)

external interface EngineDrivetrainParams : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleAutoboxParams] (Value)
     */
    var autoboxParams: PxVehicleAutoboxParams
    /**
     * WebIDL type: [PxVehicleClutchCommandResponseParams] (Value)
     */
    var clutchCommandResponseParams: PxVehicleClutchCommandResponseParams
    /**
     * WebIDL type: [PxVehicleEngineParams] (Value)
     */
    var engineParams: PxVehicleEngineParams
    /**
     * WebIDL type: [PxVehicleGearboxParams] (Value)
     */
    var gearBoxParams: PxVehicleGearboxParams
    /**
     * WebIDL type: [PxVehicleMultiWheelDriveDifferentialParams] (Value)
     */
    var multiWheelDifferentialParams: PxVehicleMultiWheelDriveDifferentialParams
    /**
     * WebIDL type: [PxVehicleFourWheelDriveDifferentialParams] (Value)
     */
    var fourWheelDifferentialParams: PxVehicleFourWheelDriveDifferentialParams
    /**
     * WebIDL type: [PxVehicleTankDriveDifferentialParams] (Value)
     */
    var tankDifferentialParams: PxVehicleTankDriveDifferentialParams
    /**
     * WebIDL type: [PxVehicleClutchParams] (Value)
     */
    var clutchParams: PxVehicleClutchParams

    /**
     * @param srcFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param trgFrame WebIDL type: [PxVehicleFrame] (Const, Ref)
     * @param srcScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @param trgScale WebIDL type: [PxVehicleScale] (Const, Ref)
     * @return WebIDL type: [EngineDrivetrainParams] (Value)
     */
    fun transformAndScale(srcFrame: PxVehicleFrame, trgFrame: PxVehicleFrame, srcScale: PxVehicleScale, trgScale: PxVehicleScale): EngineDrivetrainParams

    /**
     * @param axleDesc WebIDL type: [PxVehicleAxleDescription] (Const, Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(axleDesc: PxVehicleAxleDescription): Boolean

}

fun EngineDrivetrainParams(_module: JsAny = PhysXJsLoader.physXJs): EngineDrivetrainParams = js("new _module.EngineDrivetrainParams()")

fun EngineDrivetrainParamsFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): EngineDrivetrainParams = js("_module.wrapPointer(ptr, _module.EngineDrivetrainParams)")

external interface EngineDrivetrainState : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleEngineDriveThrottleCommandResponseState] (Value)
     */
    var throttleCommandResponseState: PxVehicleEngineDriveThrottleCommandResponseState
    /**
     * WebIDL type: [PxVehicleAutoboxState] (Value)
     */
    var autoboxState: PxVehicleAutoboxState
    /**
     * WebIDL type: [PxVehicleClutchCommandResponseState] (Value)
     */
    var clutchCommandResponseState: PxVehicleClutchCommandResponseState
    /**
     * WebIDL type: [PxVehicleDifferentialState] (Value)
     */
    var differentialState: PxVehicleDifferentialState
    /**
     * WebIDL type: [PxVehicleWheelConstraintGroupState] (Value)
     */
    var wheelConstraintGroupState: PxVehicleWheelConstraintGroupState
    /**
     * WebIDL type: [PxVehicleEngineState] (Value)
     */
    var engineState: PxVehicleEngineState
    /**
     * WebIDL type: [PxVehicleGearboxState] (Value)
     */
    var gearboxState: PxVehicleGearboxState
    /**
     * WebIDL type: [PxVehicleClutchSlipState] (Value)
     */
    var clutchState: PxVehicleClutchSlipState

    fun setToDefault()

}

fun EngineDrivetrainState(_module: JsAny = PhysXJsLoader.physXJs): EngineDrivetrainState = js("new _module.EngineDrivetrainState()")

fun EngineDrivetrainStateFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): EngineDrivetrainState = js("_module.wrapPointer(ptr, _module.EngineDrivetrainState)")

external interface BaseVehicle : JsAny, DestroyableNative {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [BaseVehicleParams] (Value)
     */
    var baseParams: BaseVehicleParams
    /**
     * WebIDL type: [BaseVehicleState] (Value)
     */
    var baseState: BaseVehicleState
    /**
     * WebIDL type: [PxVehicleComponentSequence] (Value)
     */
    var componentSequence: PxVehicleComponentSequence
    /**
     * WebIDL type: octet
     */
    var componentSequenceSubstepGroupHandle: Byte

    /**
     * @return WebIDL type: boolean
     */
    fun initialize(): Boolean

    fun destroyState()

    /**
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     */
    fun initComponentSequence(addPhysXBeginEndComponents: Boolean)

    /**
     * @param dt      WebIDL type: float
     * @param context WebIDL type: [PxVehicleSimulationContext] (Const, Ref)
     */
    fun step(dt: Float, context: PxVehicleSimulationContext)

}

fun BaseVehicleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): BaseVehicle = js("_module.wrapPointer(ptr, _module.BaseVehicle)")

external interface PhysXActorVehicle : JsAny, DestroyableNative, BaseVehicle {
    /**
     * WebIDL type: [PhysXIntegrationParams] (Value)
     */
    var physXParams: PhysXIntegrationParams
    /**
     * WebIDL type: [PhysXIntegrationState] (Value)
     */
    var physXState: PhysXIntegrationState
    /**
     * WebIDL type: [PxVehicleCommandState] (Value)
     */
    var commandState: PxVehicleCommandState

    /**
     * @param physics         WebIDL type: [PxPhysics] (Ref)
     * @param params          WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial WebIDL type: [PxMaterial] (Ref)
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial): Boolean

}

fun PhysXActorVehicleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): PhysXActorVehicle = js("_module.wrapPointer(ptr, _module.PhysXActorVehicle)")

external interface DirectDriveVehicle : JsAny, DestroyableNative, PhysXActorVehicle {
    /**
     * WebIDL type: [DirectDrivetrainParams] (Value)
     */
    var directDriveParams: DirectDrivetrainParams
    /**
     * WebIDL type: [DirectDrivetrainState] (Value)
     */
    var directDriveState: DirectDrivetrainState
    /**
     * WebIDL type: [PxVehicleDirectDriveTransmissionCommandState] (Value)
     */
    var transmissionCommandState: PxVehicleDirectDriveTransmissionCommandState

    /**
     * @param physics         WebIDL type: [PxPhysics] (Ref)
     * @param params          WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial WebIDL type: [PxMaterial] (Ref)
     * @return WebIDL type: boolean
     */
    override fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial): Boolean

    /**
     * @param physics                    WebIDL type: [PxPhysics] (Ref)
     * @param params                     WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial            WebIDL type: [PxMaterial] (Ref)
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, addPhysXBeginEndComponents: Boolean): Boolean

    /**
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     */
    override fun initComponentSequence(addPhysXBeginEndComponents: Boolean)

}

fun DirectDriveVehicle(_module: JsAny = PhysXJsLoader.physXJs): DirectDriveVehicle = js("new _module.DirectDriveVehicle()")

fun DirectDriveVehicleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): DirectDriveVehicle = js("_module.wrapPointer(ptr, _module.DirectDriveVehicle)")

external interface EngineDriveVehicle : JsAny, DestroyableNative, PhysXActorVehicle {
    /**
     * WebIDL type: [EngineDrivetrainParams] (Value)
     */
    var engineDriveParams: EngineDrivetrainParams
    /**
     * WebIDL type: [EngineDrivetrainState] (Value)
     */
    var engineDriveState: EngineDrivetrainState
    /**
     * WebIDL type: [PxVehicleEngineDriveTransmissionCommandState] (Value)
     */
    var transmissionCommandState: PxVehicleEngineDriveTransmissionCommandState
    /**
     * WebIDL type: [PxVehicleTankDriveTransmissionCommandState] (Value)
     */
    var tankDriveTransmissionCommandState: PxVehicleTankDriveTransmissionCommandState
    /**
     * WebIDL type: [EngineDriveVehicleEnum] (enum)
     */
    var differentialType: Int

    /**
     * @param physics          WebIDL type: [PxPhysics] (Ref)
     * @param params           WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial  WebIDL type: [PxMaterial] (Ref)
     * @param differentialType WebIDL type: [EngineDriveVehicleEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: Int): Boolean

    /**
     * @param physics                    WebIDL type: [PxPhysics] (Ref)
     * @param params                     WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial            WebIDL type: [PxMaterial] (Ref)
     * @param differentialType           WebIDL type: [EngineDriveVehicleEnum] (enum)
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: Int, addPhysXBeginEndComponents: Boolean): Boolean

    /**
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     */
    override fun initComponentSequence(addPhysXBeginEndComponents: Boolean)

}

fun EngineDriveVehicle(_module: JsAny = PhysXJsLoader.physXJs): EngineDriveVehicle = js("new _module.EngineDriveVehicle()")

fun EngineDriveVehicleFromPointer(ptr: Int, _module: JsAny = PhysXJsLoader.physXJs): EngineDriveVehicle = js("_module.wrapPointer(ptr, _module.EngineDriveVehicle)")

var EngineDriveVehicle.differentialTypeEnum: EngineDriveVehicleEnum
    get() = EngineDriveVehicleEnum.forValue(differentialType)
    set(value) { differentialType = value.value }

fun EngineDriveVehicle.initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: EngineDriveVehicleEnum) = initialize(physics, params, defaultMaterial, differentialType.value)
fun EngineDriveVehicle.initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: EngineDriveVehicleEnum, addPhysXBeginEndComponents: Boolean) = initialize(physics, params, defaultMaterial, differentialType.value, addPhysXBeginEndComponents)

value class PxVehicleAxesEnum private constructor(val value: Int) {
    companion object {
        val ePosX: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_ePosX(PhysXJsLoader.physXJs))
        val eNegX: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_eNegX(PhysXJsLoader.physXJs))
        val ePosY: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_ePosY(PhysXJsLoader.physXJs))
        val eNegY: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_eNegY(PhysXJsLoader.physXJs))
        val ePosZ: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_ePosZ(PhysXJsLoader.physXJs))
        val eNegZ: PxVehicleAxesEnum = PxVehicleAxesEnum(PxVehicleAxesEnum_eNegZ(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            ePosX.value -> ePosX
            eNegX.value -> eNegX
            ePosY.value -> ePosY
            eNegY.value -> eNegY
            ePosZ.value -> ePosZ
            eNegZ.value -> eNegZ
            else -> error("Invalid enum value $value for enum PxVehicleAxesEnum")
        }
    }
}

private fun PxVehicleAxesEnum_ePosX(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_ePosX()")
private fun PxVehicleAxesEnum_eNegX(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_eNegX()")
private fun PxVehicleAxesEnum_ePosY(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_ePosY()")
private fun PxVehicleAxesEnum_eNegY(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_eNegY()")
private fun PxVehicleAxesEnum_ePosZ(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_ePosZ()")
private fun PxVehicleAxesEnum_eNegZ(module: JsAny): Int = js("module._emscripten_enum_PxVehicleAxesEnum_eNegZ()")

value class PxVehicleClutchAccuracyModeEnum private constructor(val value: Int) {
    companion object {
        val eESTIMATE: PxVehicleClutchAccuracyModeEnum = PxVehicleClutchAccuracyModeEnum(PxVehicleClutchAccuracyModeEnum_eESTIMATE(PhysXJsLoader.physXJs))
        val eBEST_POSSIBLE: PxVehicleClutchAccuracyModeEnum = PxVehicleClutchAccuracyModeEnum(PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eESTIMATE.value -> eESTIMATE
            eBEST_POSSIBLE.value -> eBEST_POSSIBLE
            else -> error("Invalid enum value $value for enum PxVehicleClutchAccuracyModeEnum")
        }
    }
}

private fun PxVehicleClutchAccuracyModeEnum_eESTIMATE(module: JsAny): Int = js("module._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE()")
private fun PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE(module: JsAny): Int = js("module._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE()")

value class PxVehicleCommandNonLinearResponseParamsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_COMMAND_VALUES: PxVehicleCommandNonLinearResponseParamsEnum = PxVehicleCommandNonLinearResponseParamsEnum(PxVehicleCommandNonLinearResponseParamsEnum_eMAX_NB_COMMAND_VALUES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eMAX_NB_COMMAND_VALUES.value -> eMAX_NB_COMMAND_VALUES
            else -> error("Invalid enum value $value for enum PxVehicleCommandNonLinearResponseParamsEnum")
        }
    }
}

private fun PxVehicleCommandNonLinearResponseParamsEnum_eMAX_NB_COMMAND_VALUES(module: JsAny): Int = js("module._emscripten_enum_PxVehicleCommandNonLinearResponseParamsEnum_eMAX_NB_COMMAND_VALUES()")

value class PxVehicleCommandValueResponseTableEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_SPEED_RESPONSES: PxVehicleCommandValueResponseTableEnum = PxVehicleCommandValueResponseTableEnum(PxVehicleCommandValueResponseTableEnum_eMAX_NB_SPEED_RESPONSES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eMAX_NB_SPEED_RESPONSES.value -> eMAX_NB_SPEED_RESPONSES
            else -> error("Invalid enum value $value for enum PxVehicleCommandValueResponseTableEnum")
        }
    }
}

private fun PxVehicleCommandValueResponseTableEnum_eMAX_NB_SPEED_RESPONSES(module: JsAny): Int = js("module._emscripten_enum_PxVehicleCommandValueResponseTableEnum_eMAX_NB_SPEED_RESPONSES()")

value class PxVehicleDirectDriveTransmissionCommandStateEnum private constructor(val value: Int) {
    companion object {
        val eREVERSE: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PxVehicleDirectDriveTransmissionCommandStateEnum_eREVERSE(PhysXJsLoader.physXJs))
        val eNEUTRAL: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PxVehicleDirectDriveTransmissionCommandStateEnum_eNEUTRAL(PhysXJsLoader.physXJs))
        val eFORWARD: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PxVehicleDirectDriveTransmissionCommandStateEnum_eFORWARD(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eREVERSE.value -> eREVERSE
            eNEUTRAL.value -> eNEUTRAL
            eFORWARD.value -> eFORWARD
            else -> error("Invalid enum value $value for enum PxVehicleDirectDriveTransmissionCommandStateEnum")
        }
    }
}

private fun PxVehicleDirectDriveTransmissionCommandStateEnum_eREVERSE(module: JsAny): Int = js("module._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eREVERSE()")
private fun PxVehicleDirectDriveTransmissionCommandStateEnum_eNEUTRAL(module: JsAny): Int = js("module._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eNEUTRAL()")
private fun PxVehicleDirectDriveTransmissionCommandStateEnum_eFORWARD(module: JsAny): Int = js("module._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eFORWARD()")

value class PxVehicleEngineDriveTransmissionCommandStateEnum private constructor(val value: Int) {
    companion object {
        val eAUTOMATIC_GEAR: PxVehicleEngineDriveTransmissionCommandStateEnum = PxVehicleEngineDriveTransmissionCommandStateEnum(PxVehicleEngineDriveTransmissionCommandStateEnum_eAUTOMATIC_GEAR(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eAUTOMATIC_GEAR.value -> eAUTOMATIC_GEAR
            else -> error("Invalid enum value $value for enum PxVehicleEngineDriveTransmissionCommandStateEnum")
        }
    }
}

private fun PxVehicleEngineDriveTransmissionCommandStateEnum_eAUTOMATIC_GEAR(module: JsAny): Int = js("module._emscripten_enum_PxVehicleEngineDriveTransmissionCommandStateEnum_eAUTOMATIC_GEAR()")

value class PxVehicleGearboxParamsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_GEARS: PxVehicleGearboxParamsEnum = PxVehicleGearboxParamsEnum(PxVehicleGearboxParamsEnum_eMAX_NB_GEARS(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eMAX_NB_GEARS.value -> eMAX_NB_GEARS
            else -> error("Invalid enum value $value for enum PxVehicleGearboxParamsEnum")
        }
    }
}

private fun PxVehicleGearboxParamsEnum_eMAX_NB_GEARS(module: JsAny): Int = js("module._emscripten_enum_PxVehicleGearboxParamsEnum_eMAX_NB_GEARS()")

value class PxVehicleLimitsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_WHEELS: PxVehicleLimitsEnum = PxVehicleLimitsEnum(PxVehicleLimitsEnum_eMAX_NB_WHEELS(PhysXJsLoader.physXJs))
        val eMAX_NB_AXLES: PxVehicleLimitsEnum = PxVehicleLimitsEnum(PxVehicleLimitsEnum_eMAX_NB_AXLES(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eMAX_NB_WHEELS.value -> eMAX_NB_WHEELS
            eMAX_NB_AXLES.value -> eMAX_NB_AXLES
            else -> error("Invalid enum value $value for enum PxVehicleLimitsEnum")
        }
    }
}

private fun PxVehicleLimitsEnum_eMAX_NB_WHEELS(module: JsAny): Int = js("module._emscripten_enum_PxVehicleLimitsEnum_eMAX_NB_WHEELS()")
private fun PxVehicleLimitsEnum_eMAX_NB_AXLES(module: JsAny): Int = js("module._emscripten_enum_PxVehicleLimitsEnum_eMAX_NB_AXLES()")

value class PxVehiclePhysXActorUpdateModeEnum private constructor(val value: Int) {
    companion object {
        val eAPPLY_VELOCITY: PxVehiclePhysXActorUpdateModeEnum = PxVehiclePhysXActorUpdateModeEnum(PxVehiclePhysXActorUpdateModeEnum_eAPPLY_VELOCITY(PhysXJsLoader.physXJs))
        val eAPPLY_ACCELERATION: PxVehiclePhysXActorUpdateModeEnum = PxVehiclePhysXActorUpdateModeEnum(PxVehiclePhysXActorUpdateModeEnum_eAPPLY_ACCELERATION(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eAPPLY_VELOCITY.value -> eAPPLY_VELOCITY
            eAPPLY_ACCELERATION.value -> eAPPLY_ACCELERATION
            else -> error("Invalid enum value $value for enum PxVehiclePhysXActorUpdateModeEnum")
        }
    }
}

private fun PxVehiclePhysXActorUpdateModeEnum_eAPPLY_VELOCITY(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXActorUpdateModeEnum_eAPPLY_VELOCITY()")
private fun PxVehiclePhysXActorUpdateModeEnum_eAPPLY_ACCELERATION(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXActorUpdateModeEnum_eAPPLY_ACCELERATION()")

value class PxVehiclePhysXConstraintLimitsEnum private constructor(val value: Int) {
    companion object {
        val eNB_DOFS_PER_PXCONSTRAINT: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_PXCONSTRAINT(PhysXJsLoader.physXJs))
        val eNB_DOFS_PER_WHEEL: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_WHEEL(PhysXJsLoader.physXJs))
        val eNB_WHEELS_PER_PXCONSTRAINT: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PxVehiclePhysXConstraintLimitsEnum_eNB_WHEELS_PER_PXCONSTRAINT(PhysXJsLoader.physXJs))
        val eNB_CONSTRAINTS_PER_VEHICLE: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PxVehiclePhysXConstraintLimitsEnum_eNB_CONSTRAINTS_PER_VEHICLE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNB_DOFS_PER_PXCONSTRAINT.value -> eNB_DOFS_PER_PXCONSTRAINT
            eNB_DOFS_PER_WHEEL.value -> eNB_DOFS_PER_WHEEL
            eNB_WHEELS_PER_PXCONSTRAINT.value -> eNB_WHEELS_PER_PXCONSTRAINT
            eNB_CONSTRAINTS_PER_VEHICLE.value -> eNB_CONSTRAINTS_PER_VEHICLE
            else -> error("Invalid enum value $value for enum PxVehiclePhysXConstraintLimitsEnum")
        }
    }
}

private fun PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_PXCONSTRAINT(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_PXCONSTRAINT()")
private fun PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_WHEEL(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_WHEEL()")
private fun PxVehiclePhysXConstraintLimitsEnum_eNB_WHEELS_PER_PXCONSTRAINT(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_WHEELS_PER_PXCONSTRAINT()")
private fun PxVehiclePhysXConstraintLimitsEnum_eNB_CONSTRAINTS_PER_VEHICLE(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_CONSTRAINTS_PER_VEHICLE()")

value class PxVehiclePhysXRoadGeometryQueryTypeEnum private constructor(val value: Int) {
    companion object {
        val eNONE: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PxVehiclePhysXRoadGeometryQueryTypeEnum_eNONE(PhysXJsLoader.physXJs))
        val eRAYCAST: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PxVehiclePhysXRoadGeometryQueryTypeEnum_eRAYCAST(PhysXJsLoader.physXJs))
        val eSWEEP: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PxVehiclePhysXRoadGeometryQueryTypeEnum_eSWEEP(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eNONE.value -> eNONE
            eRAYCAST.value -> eRAYCAST
            eSWEEP.value -> eSWEEP
            else -> error("Invalid enum value $value for enum PxVehiclePhysXRoadGeometryQueryTypeEnum")
        }
    }
}

private fun PxVehiclePhysXRoadGeometryQueryTypeEnum_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eNONE()")
private fun PxVehiclePhysXRoadGeometryQueryTypeEnum_eRAYCAST(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eRAYCAST()")
private fun PxVehiclePhysXRoadGeometryQueryTypeEnum_eSWEEP(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eSWEEP()")

value class PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum private constructor(val value: Int) {
    companion object {
        val eSUSPENSION: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eSUSPENSION(PhysXJsLoader.physXJs))
        val eROAD_GEOMETRY_NORMAL: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eROAD_GEOMETRY_NORMAL(PhysXJsLoader.physXJs))
        val eNONE: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eNONE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eSUSPENSION.value -> eSUSPENSION
            eROAD_GEOMETRY_NORMAL.value -> eROAD_GEOMETRY_NORMAL
            eNONE.value -> eNONE
            else -> error("Invalid enum value $value for enum PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum")
        }
    }
}

private fun PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eSUSPENSION(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eSUSPENSION()")
private fun PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eROAD_GEOMETRY_NORMAL(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eROAD_GEOMETRY_NORMAL()")
private fun PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eNONE(module: JsAny): Int = js("module._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eNONE()")

value class PxVehicleSimulationContextTypeEnum private constructor(val value: Int) {
    companion object {
        val eDEFAULT: PxVehicleSimulationContextTypeEnum = PxVehicleSimulationContextTypeEnum(PxVehicleSimulationContextTypeEnum_eDEFAULT(PhysXJsLoader.physXJs))
        val ePHYSX: PxVehicleSimulationContextTypeEnum = PxVehicleSimulationContextTypeEnum(PxVehicleSimulationContextTypeEnum_ePHYSX(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eDEFAULT.value -> eDEFAULT
            ePHYSX.value -> ePHYSX
            else -> error("Invalid enum value $value for enum PxVehicleSimulationContextTypeEnum")
        }
    }
}

private fun PxVehicleSimulationContextTypeEnum_eDEFAULT(module: JsAny): Int = js("module._emscripten_enum_PxVehicleSimulationContextTypeEnum_eDEFAULT()")
private fun PxVehicleSimulationContextTypeEnum_ePHYSX(module: JsAny): Int = js("module._emscripten_enum_PxVehicleSimulationContextTypeEnum_ePHYSX()")

value class PxVehicleSuspensionJounceCalculationTypeEnum private constructor(val value: Int) {
    companion object {
        val eRAYCAST: PxVehicleSuspensionJounceCalculationTypeEnum = PxVehicleSuspensionJounceCalculationTypeEnum(PxVehicleSuspensionJounceCalculationTypeEnum_eRAYCAST(PhysXJsLoader.physXJs))
        val eSWEEP: PxVehicleSuspensionJounceCalculationTypeEnum = PxVehicleSuspensionJounceCalculationTypeEnum(PxVehicleSuspensionJounceCalculationTypeEnum_eSWEEP(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eRAYCAST.value -> eRAYCAST
            eSWEEP.value -> eSWEEP
            else -> error("Invalid enum value $value for enum PxVehicleSuspensionJounceCalculationTypeEnum")
        }
    }
}

private fun PxVehicleSuspensionJounceCalculationTypeEnum_eRAYCAST(module: JsAny): Int = js("module._emscripten_enum_PxVehicleSuspensionJounceCalculationTypeEnum_eRAYCAST()")
private fun PxVehicleSuspensionJounceCalculationTypeEnum_eSWEEP(module: JsAny): Int = js("module._emscripten_enum_PxVehicleSuspensionJounceCalculationTypeEnum_eSWEEP()")

value class PxVehicleTireDirectionModesEnum private constructor(val value: Int) {
    companion object {
        val eLONGITUDINAL: PxVehicleTireDirectionModesEnum = PxVehicleTireDirectionModesEnum(PxVehicleTireDirectionModesEnum_eLONGITUDINAL(PhysXJsLoader.physXJs))
        val eLATERAL: PxVehicleTireDirectionModesEnum = PxVehicleTireDirectionModesEnum(PxVehicleTireDirectionModesEnum_eLATERAL(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eLONGITUDINAL.value -> eLONGITUDINAL
            eLATERAL.value -> eLATERAL
            else -> error("Invalid enum value $value for enum PxVehicleTireDirectionModesEnum")
        }
    }
}

private fun PxVehicleTireDirectionModesEnum_eLONGITUDINAL(module: JsAny): Int = js("module._emscripten_enum_PxVehicleTireDirectionModesEnum_eLONGITUDINAL()")
private fun PxVehicleTireDirectionModesEnum_eLATERAL(module: JsAny): Int = js("module._emscripten_enum_PxVehicleTireDirectionModesEnum_eLATERAL()")

value class EngineDriveVehicleEnum private constructor(val value: Int) {
    companion object {
        val eDIFFTYPE_FOURWHEELDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(EngineDriveVehicleEnum_eDIFFTYPE_FOURWHEELDRIVE(PhysXJsLoader.physXJs))
        val eDIFFTYPE_MULTIWHEELDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(EngineDriveVehicleEnum_eDIFFTYPE_MULTIWHEELDRIVE(PhysXJsLoader.physXJs))
        val eDIFFTYPE_TANKDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(EngineDriveVehicleEnum_eDIFFTYPE_TANKDRIVE(PhysXJsLoader.physXJs))
        fun forValue(value: Int) = when(value) {
            eDIFFTYPE_FOURWHEELDRIVE.value -> eDIFFTYPE_FOURWHEELDRIVE
            eDIFFTYPE_MULTIWHEELDRIVE.value -> eDIFFTYPE_MULTIWHEELDRIVE
            eDIFFTYPE_TANKDRIVE.value -> eDIFFTYPE_TANKDRIVE
            else -> error("Invalid enum value $value for enum EngineDriveVehicleEnum")
        }
    }
}

private fun EngineDriveVehicleEnum_eDIFFTYPE_FOURWHEELDRIVE(module: JsAny): Int = js("module._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_FOURWHEELDRIVE()")
private fun EngineDriveVehicleEnum_eDIFFTYPE_MULTIWHEELDRIVE(module: JsAny): Int = js("module._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_MULTIWHEELDRIVE()")
private fun EngineDriveVehicleEnum_eDIFFTYPE_TANKDRIVE(module: JsAny): Int = js("module._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_TANKDRIVE()")

