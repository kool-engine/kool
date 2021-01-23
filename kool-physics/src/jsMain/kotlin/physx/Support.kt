/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "unused")

package physx

external interface PxMaterialPtr

external interface Vector_PxMaterial {
    fun at(index: Int): PxMaterial
    fun data(): PxMaterialPtr
    fun size(): Int
    fun push_back(value: PxMaterial)
}
fun Vector_PxMaterial(): Vector_PxMaterial {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxMaterial()")
}
fun Vector_PxMaterial(size: Int): Vector_PxMaterial {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxMaterial(size)")
}

external interface Vector_PxReal {
    fun at(index: Int): Float
    fun data(): PxRealPtr
    fun size(): Int
    fun push_back(value: Float)
}
fun Vector_PxReal(): Vector_PxReal {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxReal()")
}
fun Vector_PxReal(size: Int): Vector_PxReal {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxReal(size)")
}

external interface Vector_PxVec3 {
    fun at(index: Int): PxVec3
    fun data(): PxVec3
    fun size(): Int
    fun push_back(value: PxVec3)
}
fun Vector_PxVec3(): Vector_PxVec3 {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVec3()")
}
fun Vector_PxVec3(size: Int): Vector_PxVec3 {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVec3(size)")
}

external interface Vector_PxRaycastQueryResult {
    fun at(index: Int): PxRaycastQueryResult
    fun data(): PxRaycastQueryResult
    fun size(): Int
    fun push_back(value: PxRaycastQueryResult)
}
fun Vector_PxRaycastQueryResult(): Vector_PxRaycastQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxRaycastQueryResult()")
}
fun Vector_PxRaycastQueryResult(size: Int): Vector_PxRaycastQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxRaycastQueryResult(size)")
}

external interface Vector_PxSweepQueryResult {
    fun at(index: Int): PxSweepQueryResult
    fun data(): PxSweepQueryResult
    fun size(): Int
    fun push_back(value: PxSweepQueryResult)
}
fun Vector_PxSweepQueryResult(): Vector_PxSweepQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxSweepQueryResult()")
}
fun Vector_PxSweepQueryResult(size: Int): Vector_PxSweepQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxSweepQueryResult(size)")
}

external interface Vector_PxRaycastHit {
    fun at(index: Int): PxRaycastHit
    fun data(): PxRaycastHit
    fun size(): Int
    fun push_back(value: PxRaycastHit)
}
fun Vector_PxRaycastHit(): Vector_PxRaycastHit {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxRaycastHit()")
}
fun Vector_PxRaycastHit(size: Int): Vector_PxRaycastHit {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxRaycastHit(size)")
}

external interface Vector_PxSweepHit {
    fun at(index: Int): PxSweepHit
    fun data(): PxSweepHit
    fun size(): Int
    fun push_back(value: PxSweepHit)
}
fun Vector_PxSweepHit(): Vector_PxSweepHit {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxSweepHit()")
}
fun Vector_PxSweepHit(size: Int): Vector_PxSweepHit {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxSweepHit(size)")
}

external interface Vector_PxVehicleDrivableSurfaceType {
    fun at(index: Int): PxVehicleDrivableSurfaceType
    fun data(): PxVehicleDrivableSurfaceType
    fun size(): Int
    fun push_back(value: PxVehicleDrivableSurfaceType)
}
fun Vector_PxVehicleDrivableSurfaceType(): Vector_PxVehicleDrivableSurfaceType {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVehicleDrivableSurfaceType()")
}
fun Vector_PxVehicleDrivableSurfaceType(size: Int): Vector_PxVehicleDrivableSurfaceType {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVehicleDrivableSurfaceType(size)")
}

external interface Vector_PxWheelQueryResult {
    fun at(index: Int): PxWheelQueryResult
    fun data(): PxWheelQueryResult
    fun size(): Int
    fun push_back(value: PxWheelQueryResult)
}
fun Vector_PxWheelQueryResult(): Vector_PxWheelQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxWheelQueryResult()")
}
fun Vector_PxWheelQueryResult(size: Int): Vector_PxWheelQueryResult {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxWheelQueryResult(size)")
}

external interface PxVehicleWheelsPtr

external interface Vector_PxVehicleWheels {
    fun at(index: Int): PxVehicleWheels
    fun data(): PxVehicleWheelsPtr
    fun size(): Int
    fun push_back(value: PxVehicleWheels)
}
fun Vector_PxVehicleWheels(): Vector_PxVehicleWheels {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVehicleWheels()")
}
fun Vector_PxVehicleWheels(size: Int): Vector_PxVehicleWheels {
    val module = PhysxJsLoader.physxJs
    return js("new module.Vector_PxVehicleWheels(size)")
}