package de.fabmax.kool.physics

import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import physx.PxTopLevelFunctions
import physx.common.PxDefaultErrorCallback
import physx.common.PxFoundation
import physx.common.PxTolerancesScale
import physx.cooking.PxCooking
import physx.cooking.PxCookingParams
import physx.extensions.PxDefaultAllocator
import physx.physics.PxPhysics
import physx.physics.PxShapeFlagEnum
import physx.physics.PxShapeFlags
import kotlin.coroutines.CoroutineContext

actual object Physics : CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    actual val isLoaded = true

    // default PhysX facilities
    val foundation: PxFoundation
    val physics: PxPhysics
    val cooking: PxCooking

    val defaultBodyFlags: PxShapeFlags

    init {
        logD { "Loading physx-js..." }

        val version = PxTopLevelFunctions.getPHYSICS_VERSION()
        val errorCallback = PxDefaultErrorCallback()
        val allocator = PxDefaultAllocator()
        foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCallback)

        val scale = PxTolerancesScale()
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, scale)

        PxTopLevelFunctions.InitExtensions(physics)

        val cookingParams = PxCookingParams(scale)
        cooking = PxTopLevelFunctions.CreateCooking(version, foundation, cookingParams)

        defaultBodyFlags = PxShapeFlags((PxShapeFlagEnum.eSCENE_QUERY_SHAPE or PxShapeFlagEnum.eSIMULATION_SHAPE).toByte())

        logI { "PhysX loaded, version: ${pxVersionToString(version)}" }
    }

    actual fun loadPhysics() { }

    actual suspend fun awaitLoaded() { }

    private fun pxVersionToString(pxVersion: Int): String {
        val major = pxVersion shr 24
        val minor = (pxVersion shr 16) and 0xff
        val bugfix = (pxVersion shr 8) and 0xff
        return "$major.$minor.$bugfix"
    }
}