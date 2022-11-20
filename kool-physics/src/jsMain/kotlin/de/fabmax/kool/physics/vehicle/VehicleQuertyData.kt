package de.fabmax.kool.physics.vehicle

//class VehicleQueryData(numWheels: Int, maxNumHitPointsPerWheel: Int = 1,
//                            val preFilterShader: PxBatchQueryPreFilterShader? = Physics.PxVehicle.DefaultWheelSceneQueryPreFilterBlocking(),
//                            val postFilterShader: PxBatchQueryPostFilterShader? = Physics.PxVehicle.DefaultWheelSceneQueryPostFilterBlocking()) : Releasable {
//
//    val numQueriesPerBatch = numWheels
//    val numHitResultsPerQuery = maxNumHitPointsPerWheel
//
//    val raycastResults: Vector_PxRaycastQueryResult
//    val raycastHitBuffer: Vector_PxRaycastHit
//
//    val sweepResults: Vector_PxSweepQueryResult
//    val sweepHitBuffer: Vector_PxSweepHit
//
//    init {
//        Physics.checkIsLoaded()
//
//        raycastResults = Vector_PxRaycastQueryResult(numWheels)
//        raycastHitBuffer = Vector_PxRaycastHit(maxNumHitPointsPerWheel)
//        sweepResults = Vector_PxSweepQueryResult(numWheels)
//        sweepHitBuffer = Vector_PxSweepHit(maxNumHitPointsPerWheel)
//    }
//
//    fun setupSceneQuery(scene: PxScene): PxBatchQuery {
//        return MemoryStack.stackPush().use { mem ->
//            val maxNumHitResultsInBatch = numQueriesPerBatch * numHitResultsPerQuery
//            val sqDesc = mem.createPxBatchQueryDesc(numQueriesPerBatch, numHitResultsPerQuery, 0)
//
//            sqDesc.queryMemory.userRaycastResultBuffer = raycastResults.data()
//            sqDesc.queryMemory.userRaycastTouchBuffer = raycastHitBuffer.data()
//            sqDesc.queryMemory.raycastTouchBufferSize = maxNumHitResultsInBatch
//
//            sqDesc.queryMemory.userSweepResultBuffer = sweepResults.data()
//            sqDesc.queryMemory.userSweepTouchBuffer = sweepHitBuffer.data()
//            sqDesc.queryMemory.sweepTouchBufferSize = maxNumHitResultsInBatch
//
//            preFilterShader?.let { sqDesc.preFilterShader = it }
//            postFilterShader?.let { sqDesc.postFilterShader = it }
//
//            scene.createBatchQuery(sqDesc)
//        }
//    }
//
//    override fun release() {
//        raycastResults.destroy()
//        raycastHitBuffer.destroy()
//        sweepResults.destroy()
//        sweepHitBuffer.destroy()
//    }
//}