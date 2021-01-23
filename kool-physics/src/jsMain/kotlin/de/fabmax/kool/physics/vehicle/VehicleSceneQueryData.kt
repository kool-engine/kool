package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Physics
import physx.*

class VehicleSceneQueryData(maxNumVehicles: Int, numWheelsPerVehicle: Int,
                            maxNumHitPointsPerWheel: Int, numVehiclesInBatch: Int = 1,
                            val preFilterShader: PxBatchQueryPreFilterShader? = Physics.Px.DefaultWheelSceneQueryPreFilterBlocking(),
                            val postFilterShader: PxBatchQueryPostFilterShader? = null) {

    val numQueriesPerBatch = numVehiclesInBatch * numWheelsPerVehicle
    val numHitResultsPerQuery = maxNumHitPointsPerWheel

    val raycastResults: Vector_PxRaycastQueryResult
    val raycastHitBuffer: Vector_PxRaycastHit

    val sweepResults: Vector_PxSweepQueryResult
    val sweepHitBuffer: Vector_PxSweepHit

    init {
        Physics.checkIsLoaded()

        val maxNumWheels = maxNumVehicles * numWheelsPerVehicle
        val maxNumHitPoints = maxNumWheels * maxNumHitPointsPerWheel

        raycastResults = Vector_PxRaycastQueryResult(maxNumWheels)
        raycastHitBuffer = Vector_PxRaycastHit(maxNumHitPoints)
        sweepResults = Vector_PxSweepQueryResult(maxNumWheels)
        sweepHitBuffer = Vector_PxSweepHit(maxNumHitPoints)
    }

    fun setupBatchedSceneQuery(scene: PxScene): PxBatchQuery {
        val maxNumHitResultsInBatch = numQueriesPerBatch * numHitResultsPerQuery
        val sqDesc = PxBatchQueryDesc(numQueriesPerBatch, numHitResultsPerQuery, 0)

        sqDesc.queryMemory.userRaycastResultBuffer = raycastResults.data()
        sqDesc.queryMemory.userRaycastTouchBuffer = raycastHitBuffer.data()
        sqDesc.queryMemory.raycastTouchBufferSize = maxNumHitResultsInBatch

        sqDesc.queryMemory.userSweepResultBuffer = sweepResults.data()
        sqDesc.queryMemory.userSweepTouchBuffer = sweepHitBuffer.data()
        sqDesc.queryMemory.sweepTouchBufferSize = maxNumHitResultsInBatch

        preFilterShader?.let { sqDesc.preFilterShader = it }
        postFilterShader?.let { sqDesc.postFilterShader = it }

        return scene.createBatchQuery(sqDesc)
    }
}