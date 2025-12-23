@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "unused")

package physx.prototypes

import physx.*
import kotlin.js.JsAny
import kotlin.js.js

val NativeArrayHelpers: NativeArrayHelpers = NativeArrayHelpers(PhysXJsLoader.physXJs)
private fun NativeArrayHelpers(module: JsAny): NativeArrayHelpers = js("module.NativeArrayHelpers.prototype")

val PxCollectionExt: PxCollectionExt = PxCollectionExt(PhysXJsLoader.physXJs)
private fun PxCollectionExt(module: JsAny): PxCollectionExt = js("module.PxCollectionExt.prototype")

val PxConvexCoreGeometryFactory: PxConvexCoreGeometryFactory = PxConvexCoreGeometryFactory(PhysXJsLoader.physXJs)
private fun PxConvexCoreGeometryFactory(module: JsAny): PxConvexCoreGeometryFactory = js("module.PxConvexCoreGeometryFactory.prototype")

val PxExtensionTopLevelFunctions: PxExtensionTopLevelFunctions = PxExtensionTopLevelFunctions(PhysXJsLoader.physXJs)
private fun PxExtensionTopLevelFunctions(module: JsAny): PxExtensionTopLevelFunctions = js("module.PxExtensionTopLevelFunctions.prototype")

val PxGeometryQuery: PxGeometryQuery = PxGeometryQuery(PhysXJsLoader.physXJs)
private fun PxGeometryQuery(module: JsAny): PxGeometryQuery = js("module.PxGeometryQuery.prototype")

val PxGjkQuery: PxGjkQuery = PxGjkQuery(PhysXJsLoader.physXJs)
private fun PxGjkQuery(module: JsAny): PxGjkQuery = js("module.PxGjkQuery.prototype")

val PxGjkQueryExt: PxGjkQueryExt = PxGjkQueryExt(PhysXJsLoader.physXJs)
private fun PxGjkQueryExt(module: JsAny): PxGjkQueryExt = js("module.PxGjkQueryExt.prototype")

val PxRigidActorExt: PxRigidActorExt = PxRigidActorExt(PhysXJsLoader.physXJs)
private fun PxRigidActorExt(module: JsAny): PxRigidActorExt = js("module.PxRigidActorExt.prototype")

val PxRigidBodyExt: PxRigidBodyExt = PxRigidBodyExt(PhysXJsLoader.physXJs)
private fun PxRigidBodyExt(module: JsAny): PxRigidBodyExt = js("module.PxRigidBodyExt.prototype")

val PxSerialization: PxSerialization = PxSerialization(PhysXJsLoader.physXJs)
private fun PxSerialization(module: JsAny): PxSerialization = js("module.PxSerialization.prototype")

val PxShapeExt: PxShapeExt = PxShapeExt(PhysXJsLoader.physXJs)
private fun PxShapeExt(module: JsAny): PxShapeExt = js("module.PxShapeExt.prototype")

val PxTetMaker: PxTetMaker = PxTetMaker(PhysXJsLoader.physXJs)
private fun PxTetMaker(module: JsAny): PxTetMaker = js("module.PxTetMaker.prototype")

val PxTetrahedronMeshExt: PxTetrahedronMeshExt = PxTetrahedronMeshExt(PhysXJsLoader.physXJs)
private fun PxTetrahedronMeshExt(module: JsAny): PxTetrahedronMeshExt = js("module.PxTetrahedronMeshExt.prototype")

val PxTopLevelFunctions: PxTopLevelFunctions = PxTopLevelFunctions(PhysXJsLoader.physXJs)
private fun PxTopLevelFunctions(module: JsAny): PxTopLevelFunctions = js("module.PxTopLevelFunctions.prototype")

val PxVehicleTireForceParamsExt: PxVehicleTireForceParamsExt = PxVehicleTireForceParamsExt(PhysXJsLoader.physXJs)
private fun PxVehicleTireForceParamsExt(module: JsAny): PxVehicleTireForceParamsExt = js("module.PxVehicleTireForceParamsExt.prototype")

val PxVehicleTopLevelFunctions: PxVehicleTopLevelFunctions = PxVehicleTopLevelFunctions(PhysXJsLoader.physXJs)
private fun PxVehicleTopLevelFunctions(module: JsAny): PxVehicleTopLevelFunctions = js("module.PxVehicleTopLevelFunctions.prototype")

val SupportFunctions: SupportFunctions = SupportFunctions(PhysXJsLoader.physXJs)
private fun SupportFunctions(module: JsAny): SupportFunctions = js("module.SupportFunctions.prototype")

