package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.physics.Physics
import physx.*

class VehicleSceneQueryData(maxNumVehicles: Int, numWheelsPerVehicle: Int,
                            maxNumHitPointsPerWheel: Int, numVehiclesInBatch: Int = 1,
                            val preFilterShader: PxBatchQueryPreFilterShader? = PhysX.Px.DefaultWheelSceneQueryPreFilterBlocking(),
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

        raycastResults = PhysX.Vector_PxRaycastQueryResult(maxNumWheels)
        raycastHitBuffer = PhysX.Vector_PxRaycastHit(maxNumHitPoints)
        sweepResults = PhysX.Vector_PxSweepQueryResult(maxNumWheels)
        sweepHitBuffer = PhysX.Vector_PxSweepHit(maxNumHitPoints)
    }

    fun setupBatchedSceneQuery(scene: PxScene): PxBatchQuery {
        val maxNumHitResultsInBatch = numQueriesPerBatch * numHitResultsPerQuery
        val sqDesc = PhysX.PxBatchQueryDesc(numQueriesPerBatch, numHitResultsPerQuery, 0)

        sqDesc.queryMemory.userRaycastResultBuffer = raycastResults.data()
        sqDesc.queryMemory.userRaycastTouchBuffer = raycastHitBuffer.data()
        sqDesc.queryMemory.raycastTouchBufferSize = maxNumHitResultsInBatch

        sqDesc.queryMemory.userSweepResultBuffer = sweepResults.data()
        sqDesc.queryMemory.userSweepTouchBuffer = sweepHitBuffer.data()
        sqDesc.queryMemory.sweepTouchBufferSize = maxNumHitResultsInBatch

        sqDesc.preFilterShader = preFilterShader
        sqDesc.postFilterShader = postFilterShader

        return scene.createBatchQuery(sqDesc)
    }
}