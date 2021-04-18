package de.fabmax.kool.physics.vehicle

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.physics.*
import physx.*

actual class SingleVehicleUpdater actual constructor(vehicle: Vehicle, private val world: PhysicsWorld) : VehicleUpdater {

    private val vehicleAsVector: Vector_PxVehicleWheels
    private val wheelQueryResults: Vector_PxWheelQueryResult
    private val vehicleWheelQueryResult: PxVehicleWheelQueryResult

    private val queryData: VehicleQueryData
    private val query: PxBatchQuery
    private var frictionPairs = Physics.defaultSurfaceFrictions

    actual var vehicleGravity: Vec3f? = null
    private val pxGravity = PxVec3()

    init {
        Physics.checkIsLoaded()

        queryData = VehicleQueryData(4)
        query = queryData.setupSceneQuery(world.scene)

        vehicleAsVector = Vector_PxVehicleWheels()
        vehicleAsVector.push_back(vehicle.pxVehicle)

        wheelQueryResults = Vector_PxWheelQueryResult(vehicle.vehicleProps.numWheels)
        vehicleWheelQueryResult = PxVehicleWheelQueryResult()
        vehicleWheelQueryResult.nbWheelQueryResults = wheelQueryResults.size()
        vehicleWheelQueryResult.wheelQueryResults = wheelQueryResults.data()
    }

    override fun updateVehicle(vehicle: Vehicle, timeStep: Float) {
        val grav = vehicleGravity?.toPxVec3(pxGravity) ?: world.scene.gravity
        Physics.PxVehicle.PxVehicleSuspensionRaycasts(query, vehicleAsVector, queryData.numQueriesPerBatch, queryData.raycastResults.data())
        Physics.PxVehicle.PxVehicleUpdates(timeStep, grav, frictionPairs.frictionPairs, vehicleAsVector, vehicleWheelQueryResult)
        for (i in 0 until 4) {
            val wheelInfo = vehicle.wheelInfos[i]
            wheelQueryResults.at(i).apply {
                localPose.toMat4f(wheelInfo.transform)
                wheelInfo.lateralSlip = lateralSlip
                wheelInfo.longitudinalSlip = longitudinalSlip
            }
        }
    }

    override fun setSurfaceFrictions(frictionPairs: Map<Material, Float>) {
        this.frictionPairs = FrictionPairs(frictionPairs)
    }

    override fun release() {
        vehicleAsVector.destroy()
        wheelQueryResults.destroy()
        vehicleWheelQueryResult.destroy()
        pxGravity.destroy()

        queryData.release()
        query.release()
        if (frictionPairs != Physics.defaultSurfaceFrictions) {
            frictionPairs.release()
        }
    }
}