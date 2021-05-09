package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Physics
import de.fabmax.kool.physics.Releasable
import de.fabmax.kool.physics.createPxBatchQueryDesc
import org.lwjgl.system.MemoryStack
import physx.physics.PxBatchQuery
import physx.physics.PxBatchQueryPostFilterShader
import physx.physics.PxBatchQueryPreFilterShader
import physx.physics.PxScene
import physx.support.Vector_PxRaycastHit
import physx.support.Vector_PxRaycastQueryResult
import physx.support.Vector_PxSweepHit
import physx.support.Vector_PxSweepQueryResult
import physx.vehicle.PxVehicleTopLevelFunctions

class VehicleQueryData(numWheels: Int, maxNumHitPointsPerWheel: Int = 1,
                       val preFilterShader: PxBatchQueryPreFilterShader? = PxVehicleTopLevelFunctions.DefaultWheelSceneQueryPreFilterBlocking(),
                       val postFilterShader: PxBatchQueryPostFilterShader? = PxVehicleTopLevelFunctions.DefaultWheelSceneQueryPostFilterBlocking()) : Releasable {

    val numQueriesPerBatch = numWheels
    val numHitResultsPerQuery = maxNumHitPointsPerWheel

    val raycastResults: Vector_PxRaycastQueryResult
    val raycastHitBuffer: Vector_PxRaycastHit

    val sweepResults: Vector_PxSweepQueryResult
    val sweepHitBuffer: Vector_PxSweepHit

    init {
        Physics.checkIsLoaded()

        raycastResults = Vector_PxRaycastQueryResult(numWheels)
        raycastHitBuffer = Vector_PxRaycastHit(maxNumHitPointsPerWheel)
        sweepResults = Vector_PxSweepQueryResult(numWheels)
        sweepHitBuffer = Vector_PxSweepHit(maxNumHitPointsPerWheel)
    }

    fun setupSceneQuery(scene: PxScene): PxBatchQuery {
        return MemoryStack.stackPush().use { mem ->
            val maxNumHitResultsInBatch = numQueriesPerBatch * numHitResultsPerQuery
            val sqDesc = mem.createPxBatchQueryDesc(numQueriesPerBatch, numHitResultsPerQuery, 0)

            sqDesc.queryMemory.userRaycastResultBuffer = raycastResults.data()
            sqDesc.queryMemory.userRaycastTouchBuffer = raycastHitBuffer.data()
            sqDesc.queryMemory.raycastTouchBufferSize = maxNumHitResultsInBatch

            sqDesc.queryMemory.userSweepResultBuffer = sweepResults.data()
            sqDesc.queryMemory.userSweepTouchBuffer = sweepHitBuffer.data()
            sqDesc.queryMemory.sweepTouchBufferSize = maxNumHitResultsInBatch

            preFilterShader?.let { sqDesc.preFilterShader = it }
            postFilterShader?.let { sqDesc.postFilterShader = it }

            scene.createBatchQuery(sqDesc)
        }
    }

    override fun release() {
        raycastResults.destroy()
        raycastHitBuffer.destroy()
        sweepResults.destroy()
        sweepHitBuffer.destroy()
    }
}