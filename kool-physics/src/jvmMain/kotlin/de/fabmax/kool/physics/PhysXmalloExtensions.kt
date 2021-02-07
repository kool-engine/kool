package de.fabmax.kool.physics

import org.lwjgl.system.MemoryStack
import physx.common.*
import physx.cooking.PxConvexFlags
import physx.extensions.PxRevoluteJointFlags
import physx.geomutils.PxConvexMeshGeometryFlags
import physx.physics.*

fun MemoryStack.mallocPxFilterData() = PxFilterData.malloc(this, MemoryStack::nmalloc)
fun MemoryStack.mallocPxFilterData(w0: Int, w1: Int, w2: Int, w3: Int) =
    PxFilterData.malloc(this, MemoryStack::nmalloc, w0, w1, w2, w3)

fun MemoryStack.mallocPxVec3() = PxVec3.malloc(this, MemoryStack::nmalloc)
fun MemoryStack.mallocPxVec3(x: Float, y: Float, z: Float) = PxVec3.malloc(this, MemoryStack::nmalloc, x, y, z)

fun MemoryStack.mallocPxQuat() = PxQuat.malloc(this, MemoryStack::nmalloc)
fun MemoryStack.mallocPxQuat(x: Float, y: Float, z: Float, w: Float) =
    PxQuat.malloc(this, MemoryStack::nmalloc, x, y, z, w)

fun MemoryStack.mallocPxTransform() = PxTransform.malloc(this, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity)
fun MemoryStack.mallocPxTransform(p: PxVec3, q: PxQuat) =
    PxTransform.malloc(this, MemoryStack::nmalloc, p, q)

fun MemoryStack.mallocPxActorFlags(flags: Int) = PxActorFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.mallocPxBaseFlags(flags: Int) = PxBaseFlags.malloc(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.mallocPxConvexFlags(flags: Int) = PxConvexFlags.malloc(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.mallocPxConvexMeshGeometryFlags(flags: Int) = PxConvexMeshGeometryFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.mallocPxHitFlags(flags: Int) = PxHitFlags.malloc(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.mallocPxRevoluteJointFlags(flags: Int) = PxRevoluteJointFlags.malloc(this, MemoryStack::nmalloc, flags.toShort())
fun MemoryStack.mallocPxRigidBodyFlags(flags: Int) = PxRigidBodyFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.mallocPxRigidDynamicLockFlags(flags: Int) = PxRigidDynamicLockFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
fun MemoryStack.mallocPxSceneFlags(flags: Int) = PxSceneFlags.malloc(this, MemoryStack::nmalloc, flags)
fun MemoryStack.mallocPxShapeFlags(flags: Int) = PxShapeFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
//fun MemoryStack.mallocPxVehicleWheelsSimFlags(flags: Int) = PxVehicleWheelsSimFlags.malloc(this, MemoryStack::nmalloc, flags.toByte())
