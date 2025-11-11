/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING", "NOTHING_TO_INLINE")

package physx

external interface PxVehicleTopLevelFunctions {
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
    fun VehicleComputeSprungMasses(nbSprungMasses: Int, sprungMassCoordinates: PxArray_PxVec3, totalMass: Float, gravityDirection: PxVehicleAxesEnum, sprungMasses: PxArray_PxReal): Boolean

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

fun PxVehicleTopLevelFunctionsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTopLevelFunctions = js("_module.wrapPointer(ptr, _module.PxVehicleTopLevelFunctions)")

external interface PxVehicleAckermannParams {
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

fun PxVehicleAckermannParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAckermannParams = js("new _module.PxVehicleAckermannParams()")

fun PxVehicleAckermannParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAckermannParams = js("_module.wrapPointer(ptr, _module.PxVehicleAckermannParams)")

fun PxVehicleAckermannParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleAckermannParams.getWheelIds(index: Int) = get_wheelIds(index)
inline fun PxVehicleAckermannParams.setWheelIds(index: Int, value: Int) = set_wheelIds(index, value)

external interface PxVehicleAntiRollForceParams {
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

fun PxVehicleAntiRollForceParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAntiRollForceParams = js("new _module.PxVehicleAntiRollForceParams()")

fun PxVehicleAntiRollForceParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAntiRollForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleAntiRollForceParams)")

fun PxVehicleAntiRollForceParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAntiRollTorque {
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

fun PxVehicleAntiRollTorque(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAntiRollTorque = js("new _module.PxVehicleAntiRollTorque()")

fun PxVehicleAntiRollTorqueFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAntiRollTorque = js("_module.wrapPointer(ptr, _module.PxVehicleAntiRollTorque)")

fun PxVehicleAntiRollTorque.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAutoboxParams {
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

fun PxVehicleAutoboxParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAutoboxParams = js("new _module.PxVehicleAutoboxParams()")

fun PxVehicleAutoboxParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAutoboxParams = js("_module.wrapPointer(ptr, _module.PxVehicleAutoboxParams)")

fun PxVehicleAutoboxParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleAutoboxParams.getUpRatios(index: Int) = get_upRatios(index)
inline fun PxVehicleAutoboxParams.setUpRatios(index: Int, value: Float) = set_upRatios(index, value)
inline fun PxVehicleAutoboxParams.getDownRatios(index: Int) = get_downRatios(index)
inline fun PxVehicleAutoboxParams.setDownRatios(index: Int, value: Float) = set_downRatios(index, value)

external interface PxVehicleAutoboxState {
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

fun PxVehicleAutoboxState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAutoboxState = js("new _module.PxVehicleAutoboxState()")

fun PxVehicleAutoboxStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAutoboxState = js("_module.wrapPointer(ptr, _module.PxVehicleAutoboxState)")

fun PxVehicleAutoboxState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleAxleDescription {
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

fun PxVehicleAxleDescription(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleAxleDescription = js("new _module.PxVehicleAxleDescription()")

fun PxVehicleAxleDescriptionFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleAxleDescription = js("_module.wrapPointer(ptr, _module.PxVehicleAxleDescription)")

fun PxVehicleAxleDescription.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleAxleDescription.getNbWheelsPerAxle(index: Int) = get_nbWheelsPerAxle(index)
inline fun PxVehicleAxleDescription.setNbWheelsPerAxle(index: Int, value: Int) = set_nbWheelsPerAxle(index, value)
inline fun PxVehicleAxleDescription.getAxleToWheelIds(index: Int) = get_axleToWheelIds(index)
inline fun PxVehicleAxleDescription.setAxleToWheelIds(index: Int, value: Int) = set_axleToWheelIds(index, value)
inline fun PxVehicleAxleDescription.getWheelIdsInAxleOrder(index: Int) = get_wheelIdsInAxleOrder(index)
inline fun PxVehicleAxleDescription.setWheelIdsInAxleOrder(index: Int, value: Int) = set_wheelIdsInAxleOrder(index, value)

external interface PxVehicleBrakeCommandResponseParams : PxVehicleCommandResponseParams {
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

fun PxVehicleBrakeCommandResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleBrakeCommandResponseParams = js("new _module.PxVehicleBrakeCommandResponseParams()")

fun PxVehicleBrakeCommandResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleBrakeCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleBrakeCommandResponseParams)")

fun PxVehicleBrakeCommandResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleClutchCommandResponseParams {
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

fun PxVehicleClutchCommandResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseParams = js("new _module.PxVehicleClutchCommandResponseParams()")

fun PxVehicleClutchCommandResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleClutchCommandResponseParams)")

fun PxVehicleClutchCommandResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleClutchCommandResponseState {
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

fun PxVehicleClutchCommandResponseState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseState = js("new _module.PxVehicleClutchCommandResponseState()")

fun PxVehicleClutchCommandResponseStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchCommandResponseState = js("_module.wrapPointer(ptr, _module.PxVehicleClutchCommandResponseState)")

fun PxVehicleClutchCommandResponseState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleClutchParams {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleClutchAccuracyModeEnum] (enum)
     */
    var accuracyMode: PxVehicleClutchAccuracyModeEnum
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

fun PxVehicleClutchParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchParams = js("new _module.PxVehicleClutchParams()")

fun PxVehicleClutchParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchParams = js("_module.wrapPointer(ptr, _module.PxVehicleClutchParams)")

fun PxVehicleClutchParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleClutchSlipState {
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

fun PxVehicleClutchSlipState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchSlipState = js("new _module.PxVehicleClutchSlipState()")

fun PxVehicleClutchSlipStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleClutchSlipState = js("_module.wrapPointer(ptr, _module.PxVehicleClutchSlipState)")

fun PxVehicleClutchSlipState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleCommandNonLinearResponseParams {
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

fun PxVehicleCommandNonLinearResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandNonLinearResponseParams = js("new _module.PxVehicleCommandNonLinearResponseParams()")

fun PxVehicleCommandNonLinearResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandNonLinearResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleCommandNonLinearResponseParams)")

fun PxVehicleCommandNonLinearResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleCommandNonLinearResponseParams.getSpeedResponses(index: Int) = get_speedResponses(index)
inline fun PxVehicleCommandNonLinearResponseParams.setSpeedResponses(index: Int, value: Float) = set_speedResponses(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getSpeedResponsesPerCommandValue(index: Int) = get_speedResponsesPerCommandValue(index)
inline fun PxVehicleCommandNonLinearResponseParams.setSpeedResponsesPerCommandValue(index: Int, value: Short) = set_speedResponsesPerCommandValue(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getNbSpeedResponsesPerCommandValue(index: Int) = get_nbSpeedResponsesPerCommandValue(index)
inline fun PxVehicleCommandNonLinearResponseParams.setNbSpeedResponsesPerCommandValue(index: Int, value: Short) = set_nbSpeedResponsesPerCommandValue(index, value)
inline fun PxVehicleCommandNonLinearResponseParams.getCommandValues(index: Int) = get_commandValues(index)
inline fun PxVehicleCommandNonLinearResponseParams.setCommandValues(index: Int, value: Float) = set_commandValues(index, value)

external interface PxVehicleCommandResponseParams {
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

fun PxVehicleCommandResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandResponseParams = js("new _module.PxVehicleCommandResponseParams()")

fun PxVehicleCommandResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleCommandResponseParams)")

fun PxVehicleCommandResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleCommandResponseParams.getWheelResponseMultipliers(index: Int) = get_wheelResponseMultipliers(index)
inline fun PxVehicleCommandResponseParams.setWheelResponseMultipliers(index: Int, value: Float) = set_wheelResponseMultipliers(index, value)

external interface PxVehicleCommandState {
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

fun PxVehicleCommandState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandState = js("new _module.PxVehicleCommandState()")

fun PxVehicleCommandStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleCommandState)")

fun PxVehicleCommandState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleCommandState.getBrakes(index: Int) = get_brakes(index)
inline fun PxVehicleCommandState.setBrakes(index: Int, value: Float) = set_brakes(index, value)

external interface PxVehicleCommandValueResponseTable {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var commandValue: Float
}

fun PxVehicleCommandValueResponseTable(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandValueResponseTable = js("new _module.PxVehicleCommandValueResponseTable()")

fun PxVehicleCommandValueResponseTableFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleCommandValueResponseTable = js("_module.wrapPointer(ptr, _module.PxVehicleCommandValueResponseTable)")

fun PxVehicleCommandValueResponseTable.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleComponent

fun PxVehicleComponentFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleComponent = js("_module.wrapPointer(ptr, _module.PxVehicleComponent)")

fun PxVehicleComponent.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleComponentSequence {
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

fun PxVehicleComponentSequence(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleComponentSequence = js("new _module.PxVehicleComponentSequence()")

fun PxVehicleComponentSequenceFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleComponentSequence = js("_module.wrapPointer(ptr, _module.PxVehicleComponentSequence)")

fun PxVehicleComponentSequence.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleConstraintConnector : PxConstraintConnector {
    /**
     * @param constraintState WebIDL type: [PxVehiclePhysXConstraintState]
     */
    fun setConstraintState(constraintState: PxVehiclePhysXConstraintState)

    override fun getConstantBlock()

}

fun PxVehicleConstraintConnector(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("new _module.PxVehicleConstraintConnector()")

/**
 * @param vehicleConstraintState WebIDL type: [PxVehiclePhysXConstraintState]
 */
fun PxVehicleConstraintConnector(vehicleConstraintState: PxVehiclePhysXConstraintState, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("new _module.PxVehicleConstraintConnector(vehicleConstraintState)")

fun PxVehicleConstraintConnectorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleConstraintConnector = js("_module.wrapPointer(ptr, _module.PxVehicleConstraintConnector)")

fun PxVehicleConstraintConnector.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleConstraintConnector.constantBlock
    get() = getConstantBlock()

external interface PxVehicleDifferentialState {
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

fun PxVehicleDifferentialState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleDifferentialState = js("new _module.PxVehicleDifferentialState()")

fun PxVehicleDifferentialStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleDifferentialState = js("_module.wrapPointer(ptr, _module.PxVehicleDifferentialState)")

fun PxVehicleDifferentialState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleDifferentialState.getConnectedWheels(index: Int) = get_connectedWheels(index)
inline fun PxVehicleDifferentialState.setConnectedWheels(index: Int, value: Int) = set_connectedWheels(index, value)
inline fun PxVehicleDifferentialState.getTorqueRatiosAllWheels(index: Int) = get_torqueRatiosAllWheels(index)
inline fun PxVehicleDifferentialState.setTorqueRatiosAllWheels(index: Int, value: Float) = set_torqueRatiosAllWheels(index, value)
inline fun PxVehicleDifferentialState.getAveWheelSpeedContributionAllWheels(index: Int) = get_aveWheelSpeedContributionAllWheels(index)
inline fun PxVehicleDifferentialState.setAveWheelSpeedContributionAllWheels(index: Int, value: Float) = set_aveWheelSpeedContributionAllWheels(index, value)

external interface PxVehicleDirectDriveThrottleCommandResponseParams : PxVehicleCommandResponseParams {
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

fun PxVehicleDirectDriveThrottleCommandResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleDirectDriveThrottleCommandResponseParams = js("new _module.PxVehicleDirectDriveThrottleCommandResponseParams()")

fun PxVehicleDirectDriveThrottleCommandResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleDirectDriveThrottleCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleDirectDriveThrottleCommandResponseParams)")

fun PxVehicleDirectDriveThrottleCommandResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleDirectDriveTransmissionCommandState {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleDirectDriveTransmissionCommandStateEnum] (enum)
     */
    var gear: PxVehicleDirectDriveTransmissionCommandStateEnum

    fun setToDefault()

}

fun PxVehicleDirectDriveTransmissionCommandState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleDirectDriveTransmissionCommandState = js("new _module.PxVehicleDirectDriveTransmissionCommandState()")

fun PxVehicleDirectDriveTransmissionCommandStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleDirectDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleDirectDriveTransmissionCommandState)")

fun PxVehicleDirectDriveTransmissionCommandState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleEngineDriveThrottleCommandResponseState {
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

fun PxVehicleEngineDriveThrottleCommandResponseState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineDriveThrottleCommandResponseState = js("new _module.PxVehicleEngineDriveThrottleCommandResponseState()")

fun PxVehicleEngineDriveThrottleCommandResponseStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineDriveThrottleCommandResponseState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineDriveThrottleCommandResponseState)")

fun PxVehicleEngineDriveThrottleCommandResponseState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleEngineDriveTransmissionCommandState {
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

fun PxVehicleEngineDriveTransmissionCommandState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineDriveTransmissionCommandState = js("new _module.PxVehicleEngineDriveTransmissionCommandState()")

fun PxVehicleEngineDriveTransmissionCommandStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineDriveTransmissionCommandState)")

fun PxVehicleEngineDriveTransmissionCommandState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleEngineParams {
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

fun PxVehicleEngineParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineParams = js("new _module.PxVehicleEngineParams()")

fun PxVehicleEngineParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineParams = js("_module.wrapPointer(ptr, _module.PxVehicleEngineParams)")

fun PxVehicleEngineParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleEngineState {
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

fun PxVehicleEngineState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineState = js("new _module.PxVehicleEngineState()")

fun PxVehicleEngineStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleEngineState = js("_module.wrapPointer(ptr, _module.PxVehicleEngineState)")

fun PxVehicleEngineState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleFixedSizeLookupTableFloat_3 {
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

fun PxVehicleFixedSizeLookupTableFloat_3(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableFloat_3 = js("new _module.PxVehicleFixedSizeLookupTableFloat_3()")

fun PxVehicleFixedSizeLookupTableFloat_3FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableFloat_3 = js("_module.wrapPointer(ptr, _module.PxVehicleFixedSizeLookupTableFloat_3)")

fun PxVehicleFixedSizeLookupTableFloat_3.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleFixedSizeLookupTableVec3_3 {
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

fun PxVehicleFixedSizeLookupTableVec3_3(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableVec3_3 = js("new _module.PxVehicleFixedSizeLookupTableVec3_3()")

fun PxVehicleFixedSizeLookupTableVec3_3FromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleFixedSizeLookupTableVec3_3 = js("_module.wrapPointer(ptr, _module.PxVehicleFixedSizeLookupTableVec3_3)")

fun PxVehicleFixedSizeLookupTableVec3_3.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleFourWheelDriveDifferentialParams : PxVehicleMultiWheelDriveDifferentialParams {
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

fun PxVehicleFourWheelDriveDifferentialParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleFourWheelDriveDifferentialParams = js("new _module.PxVehicleFourWheelDriveDifferentialParams()")

fun PxVehicleFourWheelDriveDifferentialParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleFourWheelDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleFourWheelDriveDifferentialParams)")

fun PxVehicleFourWheelDriveDifferentialParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleFourWheelDriveDifferentialParams.getFrontWheelIds(index: Int) = get_frontWheelIds(index)
inline fun PxVehicleFourWheelDriveDifferentialParams.setFrontWheelIds(index: Int, value: Int) = set_frontWheelIds(index, value)
inline fun PxVehicleFourWheelDriveDifferentialParams.getRearWheelIds(index: Int) = get_rearWheelIds(index)
inline fun PxVehicleFourWheelDriveDifferentialParams.setRearWheelIds(index: Int, value: Int) = set_rearWheelIds(index, value)

external interface PxVehicleFrame {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var lngAxis: PxVehicleAxesEnum
    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var latAxis: PxVehicleAxesEnum
    /**
     * WebIDL type: [PxVehicleAxesEnum] (enum)
     */
    var vrtAxis: PxVehicleAxesEnum

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

fun PxVehicleFrame(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleFrame = js("new _module.PxVehicleFrame()")

fun PxVehicleFrameFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleFrame = js("_module.wrapPointer(ptr, _module.PxVehicleFrame)")

fun PxVehicleFrame.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleFrame.frame
    get() = getFrame()

external interface PxVehicleGearboxParams {
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

fun PxVehicleGearboxParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleGearboxParams = js("new _module.PxVehicleGearboxParams()")

fun PxVehicleGearboxParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleGearboxParams = js("_module.wrapPointer(ptr, _module.PxVehicleGearboxParams)")

fun PxVehicleGearboxParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleGearboxParams.getRatios(index: Int) = get_ratios(index)
inline fun PxVehicleGearboxParams.setRatios(index: Int, value: Float) = set_ratios(index, value)

external interface PxVehicleGearboxState {
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

fun PxVehicleGearboxState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleGearboxState = js("new _module.PxVehicleGearboxState()")

fun PxVehicleGearboxStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleGearboxState = js("_module.wrapPointer(ptr, _module.PxVehicleGearboxState)")

fun PxVehicleGearboxState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleMultiWheelDriveDifferentialParams {
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

fun PxVehicleMultiWheelDriveDifferentialParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleMultiWheelDriveDifferentialParams = js("new _module.PxVehicleMultiWheelDriveDifferentialParams()")

fun PxVehicleMultiWheelDriveDifferentialParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleMultiWheelDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleMultiWheelDriveDifferentialParams)")

fun PxVehicleMultiWheelDriveDifferentialParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleMultiWheelDriveDifferentialParams.getTorqueRatios(index: Int) = get_torqueRatios(index)
inline fun PxVehicleMultiWheelDriveDifferentialParams.setTorqueRatios(index: Int, value: Float) = set_torqueRatios(index, value)
inline fun PxVehicleMultiWheelDriveDifferentialParams.getAveWheelSpeedRatios(index: Int) = get_aveWheelSpeedRatios(index)
inline fun PxVehicleMultiWheelDriveDifferentialParams.setAveWheelSpeedRatios(index: Int, value: Float) = set_aveWheelSpeedRatios(index, value)

external interface PxVehiclePhysXActor {
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

fun PxVehiclePhysXActorFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXActor = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXActor)")

fun PxVehiclePhysXActor.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehiclePhysXActor.getWheelShapes(index: Int) = get_wheelShapes(index)
inline fun PxVehiclePhysXActor.setWheelShapes(index: Int, value: PxShape) = set_wheelShapes(index, value)

external interface PxVehiclePhysXConstraints {
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

fun PxVehiclePhysXConstraintsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXConstraints = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXConstraints)")

fun PxVehiclePhysXConstraints.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehiclePhysXConstraints.getConstraintStates(index: Int) = get_constraintStates(index)
inline fun PxVehiclePhysXConstraints.setConstraintStates(index: Int, value: PxVehiclePhysXConstraintState) = set_constraintStates(index, value)
inline fun PxVehiclePhysXConstraints.getConstraints(index: Int) = get_constraints(index)
inline fun PxVehiclePhysXConstraints.setConstraints(index: Int, value: PxConstraint) = set_constraints(index, value)
inline fun PxVehiclePhysXConstraints.getConstraintConnectors(index: Int) = get_constraintConnectors(index)
inline fun PxVehiclePhysXConstraints.setConstraintConnectors(index: Int, value: PxVehicleConstraintConnector) = set_constraintConnectors(index, value)

external interface PxVehiclePhysXConstraintState {
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

fun PxVehiclePhysXConstraintState(_module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXConstraintState = js("new _module.PxVehiclePhysXConstraintState()")

fun PxVehiclePhysXConstraintStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXConstraintState = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXConstraintState)")

fun PxVehiclePhysXConstraintState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehiclePhysXConstraintState.getTireActiveStatus(index: Int) = get_tireActiveStatus(index)
inline fun PxVehiclePhysXConstraintState.setTireActiveStatus(index: Int, value: Boolean) = set_tireActiveStatus(index, value)
inline fun PxVehiclePhysXConstraintState.getTireLinears(index: Int) = get_tireLinears(index)
inline fun PxVehiclePhysXConstraintState.setTireLinears(index: Int, value: PxVec3) = set_tireLinears(index, value)
inline fun PxVehiclePhysXConstraintState.getTireAngulars(index: Int) = get_tireAngulars(index)
inline fun PxVehiclePhysXConstraintState.setTireAngulars(index: Int, value: PxVec3) = set_tireAngulars(index, value)
inline fun PxVehiclePhysXConstraintState.getTireDamping(index: Int) = get_tireDamping(index)
inline fun PxVehiclePhysXConstraintState.setTireDamping(index: Int, value: Float) = set_tireDamping(index, value)

external interface PxVehiclePhysXMaterialFriction {
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

fun PxVehiclePhysXMaterialFriction(_module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFriction = js("new _module.PxVehiclePhysXMaterialFriction()")

fun PxVehiclePhysXMaterialFrictionFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFriction = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXMaterialFriction)")

fun PxVehiclePhysXMaterialFriction.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehiclePhysXMaterialFrictionParams {
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

fun PxVehiclePhysXMaterialFrictionParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXMaterialFrictionParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXMaterialFrictionParams)")

fun PxVehiclePhysXMaterialFrictionParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehiclePhysXRoadGeometryQueryParams {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehiclePhysXRoadGeometryQueryTypeEnum] (enum)
     */
    var roadGeometryQueryType: PxVehiclePhysXRoadGeometryQueryTypeEnum
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

fun PxVehiclePhysXRoadGeometryQueryParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXRoadGeometryQueryParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXRoadGeometryQueryParams)")

fun PxVehiclePhysXRoadGeometryQueryParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehiclePhysXSimulationContext : PxVehicleSimulationContext {
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
    var physxActorUpdateMode: PxVehiclePhysXActorUpdateModeEnum
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

fun PxVehiclePhysXSimulationContext(_module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXSimulationContext = js("new _module.PxVehiclePhysXSimulationContext()")

fun PxVehiclePhysXSimulationContextFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXSimulationContext = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSimulationContext)")

external interface PxVehiclePhysXSteerState {
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

fun PxVehiclePhysXSteerStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXSteerState = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSteerState)")

fun PxVehiclePhysXSteerState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehiclePhysXSuspensionLimitConstraintParams {
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
    var directionForSuspensionLimitConstraint: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum

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

fun PxVehiclePhysXSuspensionLimitConstraintParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePhysXSuspensionLimitConstraintParams = js("_module.wrapPointer(ptr, _module.PxVehiclePhysXSuspensionLimitConstraintParams)")

fun PxVehiclePhysXSuspensionLimitConstraintParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehiclePvdContext

fun PxVehiclePvdContextFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehiclePvdContext = js("_module.wrapPointer(ptr, _module.PxVehiclePvdContext)")

external interface PxVehicleRigidBodyParams {
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

fun PxVehicleRigidBodyParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleRigidBodyParams = js("new _module.PxVehicleRigidBodyParams()")

fun PxVehicleRigidBodyParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleRigidBodyParams = js("_module.wrapPointer(ptr, _module.PxVehicleRigidBodyParams)")

fun PxVehicleRigidBodyParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleRigidBodyState {
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

fun PxVehicleRigidBodyState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleRigidBodyState = js("new _module.PxVehicleRigidBodyState()")

fun PxVehicleRigidBodyStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleRigidBodyState = js("_module.wrapPointer(ptr, _module.PxVehicleRigidBodyState)")

fun PxVehicleRigidBodyState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleRoadGeometryState {
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

fun PxVehicleRoadGeometryState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleRoadGeometryState = js("new _module.PxVehicleRoadGeometryState()")

fun PxVehicleRoadGeometryStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleRoadGeometryState = js("_module.wrapPointer(ptr, _module.PxVehicleRoadGeometryState)")

fun PxVehicleRoadGeometryState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleScale {
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

fun PxVehicleScale(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleScale = js("new _module.PxVehicleScale()")

fun PxVehicleScaleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleScale = js("_module.wrapPointer(ptr, _module.PxVehicleScale)")

fun PxVehicleScale.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSimulationContext {
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
    fun getType(): PxVehicleSimulationContextTypeEnum

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

fun PxVehicleSimulationContext(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSimulationContext = js("new _module.PxVehicleSimulationContext()")

fun PxVehicleSimulationContextFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSimulationContext = js("_module.wrapPointer(ptr, _module.PxVehicleSimulationContext)")

fun PxVehicleSimulationContext.destroy() {
    PhysXJsLoader.destroy(this)
}

val PxVehicleSimulationContext.type
    get() = getType()

external interface PxVehicleSteerCommandResponseParams : PxVehicleCommandResponseParams {
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

fun PxVehicleSteerCommandResponseParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSteerCommandResponseParams = js("new _module.PxVehicleSteerCommandResponseParams()")

fun PxVehicleSteerCommandResponseParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSteerCommandResponseParams = js("_module.wrapPointer(ptr, _module.PxVehicleSteerCommandResponseParams)")

fun PxVehicleSteerCommandResponseParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionComplianceParams {
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

fun PxVehicleSuspensionComplianceParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceParams = js("new _module.PxVehicleSuspensionComplianceParams()")

fun PxVehicleSuspensionComplianceParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionComplianceParams)")

fun PxVehicleSuspensionComplianceParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionComplianceState {
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

fun PxVehicleSuspensionComplianceState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceState = js("new _module.PxVehicleSuspensionComplianceState()")

fun PxVehicleSuspensionComplianceStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionComplianceState = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionComplianceState)")

fun PxVehicleSuspensionComplianceState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionForce {
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

fun PxVehicleSuspensionForce(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionForce = js("new _module.PxVehicleSuspensionForce()")

fun PxVehicleSuspensionForceFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionForce = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionForce)")

fun PxVehicleSuspensionForce.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionForceParams {
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

fun PxVehicleSuspensionForceParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionForceParams = js("new _module.PxVehicleSuspensionForceParams()")

fun PxVehicleSuspensionForceParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionForceParams)")

fun PxVehicleSuspensionForceParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionParams {
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

fun PxVehicleSuspensionParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionParams = js("new _module.PxVehicleSuspensionParams()")

fun PxVehicleSuspensionParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionParams)")

fun PxVehicleSuspensionParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionState {
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

fun PxVehicleSuspensionState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionState = js("new _module.PxVehicleSuspensionState()")

fun PxVehicleSuspensionStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionState = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionState)")

fun PxVehicleSuspensionState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleSuspensionStateCalculationParams {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [PxVehicleSuspensionJounceCalculationTypeEnum] (enum)
     */
    var suspensionJounceCalculationType: PxVehicleSuspensionJounceCalculationTypeEnum
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

fun PxVehicleSuspensionStateCalculationParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionStateCalculationParams = js("new _module.PxVehicleSuspensionStateCalculationParams()")

fun PxVehicleSuspensionStateCalculationParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleSuspensionStateCalculationParams = js("_module.wrapPointer(ptr, _module.PxVehicleSuspensionStateCalculationParams)")

fun PxVehicleSuspensionStateCalculationParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTankDriveDifferentialParams : PxVehicleMultiWheelDriveDifferentialParams {
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

fun PxVehicleTankDriveDifferentialParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTankDriveDifferentialParams = js("new _module.PxVehicleTankDriveDifferentialParams()")

fun PxVehicleTankDriveDifferentialParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTankDriveDifferentialParams = js("_module.wrapPointer(ptr, _module.PxVehicleTankDriveDifferentialParams)")

fun PxVehicleTankDriveDifferentialParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTankDriveDifferentialParams.getThrustIdPerTrack(index: Int) = get_thrustIdPerTrack(index)
inline fun PxVehicleTankDriveDifferentialParams.setThrustIdPerTrack(index: Int, value: Int) = set_thrustIdPerTrack(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getNbWheelsPerTrack(index: Int) = get_nbWheelsPerTrack(index)
inline fun PxVehicleTankDriveDifferentialParams.setNbWheelsPerTrack(index: Int, value: Int) = set_nbWheelsPerTrack(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getTrackToWheelIds(index: Int) = get_trackToWheelIds(index)
inline fun PxVehicleTankDriveDifferentialParams.setTrackToWheelIds(index: Int, value: Int) = set_trackToWheelIds(index, value)
inline fun PxVehicleTankDriveDifferentialParams.getWheelIdsInTrackOrder(index: Int) = get_wheelIdsInTrackOrder(index)
inline fun PxVehicleTankDriveDifferentialParams.setWheelIdsInTrackOrder(index: Int, value: Int) = set_wheelIdsInTrackOrder(index, value)

external interface PxVehicleTankDriveTransmissionCommandState : PxVehicleEngineDriveTransmissionCommandState {
    /**
     * WebIDL type: float
     */
    fun get_thrusts(index: Int): Float
    fun set_thrusts(index: Int, value: Float)

    override fun setToDefault()

}

fun PxVehicleTankDriveTransmissionCommandState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTankDriveTransmissionCommandState = js("new _module.PxVehicleTankDriveTransmissionCommandState()")

fun PxVehicleTankDriveTransmissionCommandStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTankDriveTransmissionCommandState = js("_module.wrapPointer(ptr, _module.PxVehicleTankDriveTransmissionCommandState)")

fun PxVehicleTankDriveTransmissionCommandState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTankDriveTransmissionCommandState.getThrusts(index: Int) = get_thrusts(index)
inline fun PxVehicleTankDriveTransmissionCommandState.setThrusts(index: Int, value: Float) = set_thrusts(index, value)

external interface PxVehicleTireAxisStickyParams {
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

fun PxVehicleTireAxisStickyParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireAxisStickyParams = js("new _module.PxVehicleTireAxisStickyParams()")

fun PxVehicleTireAxisStickyParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireAxisStickyParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireAxisStickyParams)")

fun PxVehicleTireAxisStickyParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireCamberAngleState {
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

fun PxVehicleTireCamberAngleState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireCamberAngleState = js("new _module.PxVehicleTireCamberAngleState()")

fun PxVehicleTireCamberAngleStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireCamberAngleState = js("_module.wrapPointer(ptr, _module.PxVehicleTireCamberAngleState)")

fun PxVehicleTireCamberAngleState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireDirectionState {
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

fun PxVehicleTireDirectionState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireDirectionState = js("new _module.PxVehicleTireDirectionState()")

fun PxVehicleTireDirectionStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireDirectionState = js("_module.wrapPointer(ptr, _module.PxVehicleTireDirectionState)")

fun PxVehicleTireDirectionState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireDirectionState.getDirections(index: Int) = get_directions(index)
inline fun PxVehicleTireDirectionState.setDirections(index: Int, value: PxVec3) = set_directions(index, value)

external interface PxVehicleTireForce {
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

fun PxVehicleTireForce(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireForce = js("new _module.PxVehicleTireForce()")

fun PxVehicleTireForceFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireForce = js("_module.wrapPointer(ptr, _module.PxVehicleTireForce)")

fun PxVehicleTireForce.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireForce.getForces(index: Int) = get_forces(index)
inline fun PxVehicleTireForce.setForces(index: Int, value: PxVec3) = set_forces(index, value)
inline fun PxVehicleTireForce.getTorques(index: Int) = get_torques(index)
inline fun PxVehicleTireForce.setTorques(index: Int, value: PxVec3) = set_torques(index, value)

external interface PxVehicleTireForceParams {
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

fun PxVehicleTireForceParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireForceParams = js("new _module.PxVehicleTireForceParams()")

fun PxVehicleTireForceParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireForceParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireForceParams)")

fun PxVehicleTireForceParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireForceParamsExt {
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

fun PxVehicleTireForceParamsExtFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireForceParamsExt = js("_module.wrapPointer(ptr, _module.PxVehicleTireForceParamsExt)")

external interface PxVehicleTireGripState {
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

fun PxVehicleTireGripStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireGripState = js("_module.wrapPointer(ptr, _module.PxVehicleTireGripState)")

fun PxVehicleTireGripState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireSlipParams {
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

fun PxVehicleTireSlipParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSlipParams = js("new _module.PxVehicleTireSlipParams()")

fun PxVehicleTireSlipParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSlipParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireSlipParams)")

fun PxVehicleTireSlipParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleTireSlipState {
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

fun PxVehicleTireSlipState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSlipState = js("new _module.PxVehicleTireSlipState()")

fun PxVehicleTireSlipStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSlipState = js("_module.wrapPointer(ptr, _module.PxVehicleTireSlipState)")

fun PxVehicleTireSlipState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireSlipState.getSlips(index: Int) = get_slips(index)
inline fun PxVehicleTireSlipState.setSlips(index: Int, value: Float) = set_slips(index, value)

external interface PxVehicleTireSpeedState {
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

fun PxVehicleTireSpeedState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSpeedState = js("new _module.PxVehicleTireSpeedState()")

fun PxVehicleTireSpeedStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireSpeedState = js("_module.wrapPointer(ptr, _module.PxVehicleTireSpeedState)")

fun PxVehicleTireSpeedState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireSpeedState.getSpeedStates(index: Int) = get_speedStates(index)
inline fun PxVehicleTireSpeedState.setSpeedStates(index: Int, value: Float) = set_speedStates(index, value)

external interface PxVehicleTireStickyParams {
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

fun PxVehicleTireStickyParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireStickyParams = js("new _module.PxVehicleTireStickyParams()")

fun PxVehicleTireStickyParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireStickyParams = js("_module.wrapPointer(ptr, _module.PxVehicleTireStickyParams)")

fun PxVehicleTireStickyParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireStickyParams.getStickyParams(index: Int) = get_stickyParams(index)
inline fun PxVehicleTireStickyParams.setStickyParams(index: Int, value: PxVehicleTireAxisStickyParams) = set_stickyParams(index, value)

external interface PxVehicleTireStickyState {
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

fun PxVehicleTireStickyState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireStickyState = js("new _module.PxVehicleTireStickyState()")

fun PxVehicleTireStickyStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTireStickyState = js("_module.wrapPointer(ptr, _module.PxVehicleTireStickyState)")

fun PxVehicleTireStickyState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PxVehicleTireStickyState.getLowSpeedTime(index: Int) = get_lowSpeedTime(index)
inline fun PxVehicleTireStickyState.setLowSpeedTime(index: Int, value: Float) = set_lowSpeedTime(index, value)
inline fun PxVehicleTireStickyState.getActiveStatus(index: Int) = get_activeStatus(index)
inline fun PxVehicleTireStickyState.setActiveStatus(index: Int, value: Boolean) = set_activeStatus(index, value)

external interface PxVehicleTorqueCurveLookupTable {
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

fun PxVehicleTorqueCurveLookupTable(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleTorqueCurveLookupTable = js("new _module.PxVehicleTorqueCurveLookupTable()")

fun PxVehicleTorqueCurveLookupTableFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleTorqueCurveLookupTable = js("_module.wrapPointer(ptr, _module.PxVehicleTorqueCurveLookupTable)")

fun PxVehicleTorqueCurveLookupTable.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelActuationState {
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

fun PxVehicleWheelActuationState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelActuationState = js("new _module.PxVehicleWheelActuationState()")

fun PxVehicleWheelActuationStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelActuationState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelActuationState)")

fun PxVehicleWheelActuationState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelConstraintGroupState {
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

fun PxVehicleWheelConstraintGroupState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelConstraintGroupState = js("new _module.PxVehicleWheelConstraintGroupState()")

fun PxVehicleWheelConstraintGroupStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelConstraintGroupState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelConstraintGroupState)")

fun PxVehicleWheelConstraintGroupState.destroy() {
    PhysXJsLoader.destroy(this)
}

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

external interface PxVehicleWheelLocalPose {
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

fun PxVehicleWheelLocalPose(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelLocalPose = js("new _module.PxVehicleWheelLocalPose()")

fun PxVehicleWheelLocalPoseFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelLocalPose = js("_module.wrapPointer(ptr, _module.PxVehicleWheelLocalPose)")

fun PxVehicleWheelLocalPose.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelParams {
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

fun PxVehicleWheelParams(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelParams = js("new _module.PxVehicleWheelParams()")

fun PxVehicleWheelParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelParams = js("_module.wrapPointer(ptr, _module.PxVehicleWheelParams)")

fun PxVehicleWheelParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PxVehicleWheelRigidBody1dState {
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

fun PxVehicleWheelRigidBody1dState(_module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelRigidBody1dState = js("new _module.PxVehicleWheelRigidBody1dState()")

fun PxVehicleWheelRigidBody1dStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PxVehicleWheelRigidBody1dState = js("_module.wrapPointer(ptr, _module.PxVehicleWheelRigidBody1dState)")

fun PxVehicleWheelRigidBody1dState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface BaseVehicleParams {
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

fun BaseVehicleParams(_module: dynamic = PhysXJsLoader.physXJs): BaseVehicleParams = js("new _module.BaseVehicleParams()")

fun BaseVehicleParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): BaseVehicleParams = js("_module.wrapPointer(ptr, _module.BaseVehicleParams)")

fun BaseVehicleParams.destroy() {
    PhysXJsLoader.destroy(this)
}

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

external interface BaseVehicleState {
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

fun BaseVehicleState(_module: dynamic = PhysXJsLoader.physXJs): BaseVehicleState = js("new _module.BaseVehicleState()")

fun BaseVehicleStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): BaseVehicleState = js("_module.wrapPointer(ptr, _module.BaseVehicleState)")

fun BaseVehicleState.destroy() {
    PhysXJsLoader.destroy(this)
}

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

external interface PhysXIntegrationParams {
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
    fun create(axleDesc: PxVehicleAxleDescription, roadQueryFilterData: PxQueryFilterData, roadQueryFilterCallback: PxQueryFilterCallback?, materialFrictions: PxVehiclePhysXMaterialFriction, nbMaterialFrictions: Int, defaultFriction: Float, physxActorCMassLocalPose: PxTransform, actorGeometry: PxGeometry, physxActorBoxShapeLocalPose: PxTransform, roadGeometryQueryType: PxVehiclePhysXRoadGeometryQueryTypeEnum)

}

fun PhysXIntegrationParams(_module: dynamic = PhysXJsLoader.physXJs): PhysXIntegrationParams = js("new _module.PhysXIntegrationParams()")

fun PhysXIntegrationParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PhysXIntegrationParams = js("_module.wrapPointer(ptr, _module.PhysXIntegrationParams)")

fun PhysXIntegrationParams.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun PhysXIntegrationParams.getPhysxMaterialFrictionParams(index: Int) = get_physxMaterialFrictionParams(index)
inline fun PhysXIntegrationParams.setPhysxMaterialFrictionParams(index: Int, value: PxVehiclePhysXMaterialFrictionParams) = set_physxMaterialFrictionParams(index, value)
inline fun PhysXIntegrationParams.getPhysxSuspensionLimitConstraintParams(index: Int) = get_physxSuspensionLimitConstraintParams(index)
inline fun PhysXIntegrationParams.setPhysxSuspensionLimitConstraintParams(index: Int, value: PxVehiclePhysXSuspensionLimitConstraintParams) = set_physxSuspensionLimitConstraintParams(index, value)
inline fun PhysXIntegrationParams.getPhysxWheelShapeLocalPoses(index: Int) = get_physxWheelShapeLocalPoses(index)
inline fun PhysXIntegrationParams.setPhysxWheelShapeLocalPoses(index: Int, value: PxTransform) = set_physxWheelShapeLocalPoses(index, value)

external interface PhysXIntegrationState {
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

fun PhysXIntegrationState(_module: dynamic = PhysXJsLoader.physXJs): PhysXIntegrationState = js("new _module.PhysXIntegrationState()")

fun PhysXIntegrationStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PhysXIntegrationState = js("_module.wrapPointer(ptr, _module.PhysXIntegrationState)")

fun PhysXIntegrationState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface DirectDrivetrainParams {
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

fun DirectDrivetrainParams(_module: dynamic = PhysXJsLoader.physXJs): DirectDrivetrainParams = js("new _module.DirectDrivetrainParams()")

fun DirectDrivetrainParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): DirectDrivetrainParams = js("_module.wrapPointer(ptr, _module.DirectDrivetrainParams)")

fun DirectDrivetrainParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface DirectDrivetrainState {
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

fun DirectDrivetrainState(_module: dynamic = PhysXJsLoader.physXJs): DirectDrivetrainState = js("new _module.DirectDrivetrainState()")

fun DirectDrivetrainStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): DirectDrivetrainState = js("_module.wrapPointer(ptr, _module.DirectDrivetrainState)")

fun DirectDrivetrainState.destroy() {
    PhysXJsLoader.destroy(this)
}

inline fun DirectDrivetrainState.getDirectDriveThrottleResponseStates(index: Int) = get_directDriveThrottleResponseStates(index)
inline fun DirectDrivetrainState.setDirectDriveThrottleResponseStates(index: Int, value: Float) = set_directDriveThrottleResponseStates(index, value)

external interface EngineDrivetrainParams {
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

fun EngineDrivetrainParams(_module: dynamic = PhysXJsLoader.physXJs): EngineDrivetrainParams = js("new _module.EngineDrivetrainParams()")

fun EngineDrivetrainParamsFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): EngineDrivetrainParams = js("_module.wrapPointer(ptr, _module.EngineDrivetrainParams)")

fun EngineDrivetrainParams.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface EngineDrivetrainState {
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

fun EngineDrivetrainState(_module: dynamic = PhysXJsLoader.physXJs): EngineDrivetrainState = js("new _module.EngineDrivetrainState()")

fun EngineDrivetrainStateFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): EngineDrivetrainState = js("_module.wrapPointer(ptr, _module.EngineDrivetrainState)")

fun EngineDrivetrainState.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface BaseVehicle {
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

fun BaseVehicleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): BaseVehicle = js("_module.wrapPointer(ptr, _module.BaseVehicle)")

fun BaseVehicle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface PhysXActorVehicle : BaseVehicle {
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

fun PhysXActorVehicleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): PhysXActorVehicle = js("_module.wrapPointer(ptr, _module.PhysXActorVehicle)")

fun PhysXActorVehicle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface DirectDriveVehicle : PhysXActorVehicle {
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

fun DirectDriveVehicle(_module: dynamic = PhysXJsLoader.physXJs): DirectDriveVehicle = js("new _module.DirectDriveVehicle()")

fun DirectDriveVehicleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): DirectDriveVehicle = js("_module.wrapPointer(ptr, _module.DirectDriveVehicle)")

fun DirectDriveVehicle.destroy() {
    PhysXJsLoader.destroy(this)
}

external interface EngineDriveVehicle : PhysXActorVehicle {
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
    var differentialType: EngineDriveVehicleEnum

    /**
     * @param physics          WebIDL type: [PxPhysics] (Ref)
     * @param params           WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial  WebIDL type: [PxMaterial] (Ref)
     * @param differentialType WebIDL type: [EngineDriveVehicleEnum] (enum)
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: EngineDriveVehicleEnum): Boolean

    /**
     * @param physics                    WebIDL type: [PxPhysics] (Ref)
     * @param params                     WebIDL type: [PxCookingParams] (Const, Ref)
     * @param defaultMaterial            WebIDL type: [PxMaterial] (Ref)
     * @param differentialType           WebIDL type: [EngineDriveVehicleEnum] (enum)
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     * @return WebIDL type: boolean
     */
    fun initialize(physics: PxPhysics, params: PxCookingParams, defaultMaterial: PxMaterial, differentialType: EngineDriveVehicleEnum, addPhysXBeginEndComponents: Boolean): Boolean

    /**
     * @param addPhysXBeginEndComponents WebIDL type: boolean
     */
    override fun initComponentSequence(addPhysXBeginEndComponents: Boolean)

}

fun EngineDriveVehicle(_module: dynamic = PhysXJsLoader.physXJs): EngineDriveVehicle = js("new _module.EngineDriveVehicle()")

fun EngineDriveVehicleFromPointer(ptr: Int, _module: dynamic = PhysXJsLoader.physXJs): EngineDriveVehicle = js("_module.wrapPointer(ptr, _module.EngineDriveVehicle)")

fun EngineDriveVehicle.destroy() {
    PhysXJsLoader.destroy(this)
}

value class PxVehicleAxesEnum private constructor(val value: Int) {
    companion object {
        val ePosX: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_ePosX())
        val eNegX: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_eNegX())
        val ePosY: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_ePosY())
        val eNegY: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_eNegY())
        val ePosZ: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_ePosZ())
        val eNegZ: PxVehicleAxesEnum = PxVehicleAxesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleAxesEnum_eNegZ())
    }
}

value class PxVehicleClutchAccuracyModeEnum private constructor(val value: Int) {
    companion object {
        val eESTIMATE: PxVehicleClutchAccuracyModeEnum = PxVehicleClutchAccuracyModeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eESTIMATE())
        val eBEST_POSSIBLE: PxVehicleClutchAccuracyModeEnum = PxVehicleClutchAccuracyModeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleClutchAccuracyModeEnum_eBEST_POSSIBLE())
    }
}

value class PxVehicleCommandNonLinearResponseParamsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_COMMAND_VALUES: PxVehicleCommandNonLinearResponseParamsEnum = PxVehicleCommandNonLinearResponseParamsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleCommandNonLinearResponseParamsEnum_eMAX_NB_COMMAND_VALUES())
    }
}

value class PxVehicleCommandValueResponseTableEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_SPEED_RESPONSES: PxVehicleCommandValueResponseTableEnum = PxVehicleCommandValueResponseTableEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleCommandValueResponseTableEnum_eMAX_NB_SPEED_RESPONSES())
    }
}

value class PxVehicleDirectDriveTransmissionCommandStateEnum private constructor(val value: Int) {
    companion object {
        val eREVERSE: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eREVERSE())
        val eNEUTRAL: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eNEUTRAL())
        val eFORWARD: PxVehicleDirectDriveTransmissionCommandStateEnum = PxVehicleDirectDriveTransmissionCommandStateEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleDirectDriveTransmissionCommandStateEnum_eFORWARD())
    }
}

value class PxVehicleEngineDriveTransmissionCommandStateEnum private constructor(val value: Int) {
    companion object {
        val eAUTOMATIC_GEAR: PxVehicleEngineDriveTransmissionCommandStateEnum = PxVehicleEngineDriveTransmissionCommandStateEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleEngineDriveTransmissionCommandStateEnum_eAUTOMATIC_GEAR())
    }
}

value class PxVehicleGearboxParamsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_GEARS: PxVehicleGearboxParamsEnum = PxVehicleGearboxParamsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleGearboxParamsEnum_eMAX_NB_GEARS())
    }
}

value class PxVehicleLimitsEnum private constructor(val value: Int) {
    companion object {
        val eMAX_NB_WHEELS: PxVehicleLimitsEnum = PxVehicleLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleLimitsEnum_eMAX_NB_WHEELS())
        val eMAX_NB_AXLES: PxVehicleLimitsEnum = PxVehicleLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleLimitsEnum_eMAX_NB_AXLES())
    }
}

value class PxVehiclePhysXActorUpdateModeEnum private constructor(val value: Int) {
    companion object {
        val eAPPLY_VELOCITY: PxVehiclePhysXActorUpdateModeEnum = PxVehiclePhysXActorUpdateModeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXActorUpdateModeEnum_eAPPLY_VELOCITY())
        val eAPPLY_ACCELERATION: PxVehiclePhysXActorUpdateModeEnum = PxVehiclePhysXActorUpdateModeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXActorUpdateModeEnum_eAPPLY_ACCELERATION())
    }
}

value class PxVehiclePhysXConstraintLimitsEnum private constructor(val value: Int) {
    companion object {
        val eNB_DOFS_PER_PXCONSTRAINT: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_PXCONSTRAINT())
        val eNB_DOFS_PER_WHEEL: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_DOFS_PER_WHEEL())
        val eNB_WHEELS_PER_PXCONSTRAINT: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_WHEELS_PER_PXCONSTRAINT())
        val eNB_CONSTRAINTS_PER_VEHICLE: PxVehiclePhysXConstraintLimitsEnum = PxVehiclePhysXConstraintLimitsEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXConstraintLimitsEnum_eNB_CONSTRAINTS_PER_VEHICLE())
    }
}

value class PxVehiclePhysXRoadGeometryQueryTypeEnum private constructor(val value: Int) {
    companion object {
        val eNONE: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eNONE())
        val eRAYCAST: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eRAYCAST())
        val eSWEEP: PxVehiclePhysXRoadGeometryQueryTypeEnum = PxVehiclePhysXRoadGeometryQueryTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXRoadGeometryQueryTypeEnum_eSWEEP())
    }
}

value class PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum private constructor(val value: Int) {
    companion object {
        val eSUSPENSION: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eSUSPENSION())
        val eROAD_GEOMETRY_NORMAL: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eROAD_GEOMETRY_NORMAL())
        val eNONE: PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum = PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehiclePhysXSuspensionLimitConstraintParamsDirectionSpecifierEnum_eNONE())
    }
}

value class PxVehicleSimulationContextTypeEnum private constructor(val value: Int) {
    companion object {
        val eDEFAULT: PxVehicleSimulationContextTypeEnum = PxVehicleSimulationContextTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleSimulationContextTypeEnum_eDEFAULT())
        val ePHYSX: PxVehicleSimulationContextTypeEnum = PxVehicleSimulationContextTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleSimulationContextTypeEnum_ePHYSX())
    }
}

value class PxVehicleSuspensionJounceCalculationTypeEnum private constructor(val value: Int) {
    companion object {
        val eRAYCAST: PxVehicleSuspensionJounceCalculationTypeEnum = PxVehicleSuspensionJounceCalculationTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleSuspensionJounceCalculationTypeEnum_eRAYCAST())
        val eSWEEP: PxVehicleSuspensionJounceCalculationTypeEnum = PxVehicleSuspensionJounceCalculationTypeEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleSuspensionJounceCalculationTypeEnum_eSWEEP())
    }
}

value class PxVehicleTireDirectionModesEnum private constructor(val value: Int) {
    companion object {
        val eLONGITUDINAL: PxVehicleTireDirectionModesEnum = PxVehicleTireDirectionModesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleTireDirectionModesEnum_eLONGITUDINAL())
        val eLATERAL: PxVehicleTireDirectionModesEnum = PxVehicleTireDirectionModesEnum(PhysXJsLoader.physXJs._emscripten_enum_PxVehicleTireDirectionModesEnum_eLATERAL())
    }
}

value class EngineDriveVehicleEnum private constructor(val value: Int) {
    companion object {
        val eDIFFTYPE_FOURWHEELDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(PhysXJsLoader.physXJs._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_FOURWHEELDRIVE())
        val eDIFFTYPE_MULTIWHEELDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(PhysXJsLoader.physXJs._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_MULTIWHEELDRIVE())
        val eDIFFTYPE_TANKDRIVE: EngineDriveVehicleEnum = EngineDriveVehicleEnum(PhysXJsLoader.physXJs._emscripten_enum_EngineDriveVehicleEnum_eDIFFTYPE_TANKDRIVE())
    }
}

