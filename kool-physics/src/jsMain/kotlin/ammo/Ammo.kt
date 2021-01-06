@file:Suppress("ClassName", "FunctionName", "unused", "UNUSED_PARAMETER", "UnsafeCastFromDynamic", "PropertyName")

package ammo

import de.fabmax.kool.math.*
import de.fabmax.kool.util.logI
import kotlin.js.Promise

external interface btActionInterface {
    fun updateAction(collisionWorld: btCollisionWorld, deltaTimeStep: Float)
}

external interface btBoxShape : btCollisionShape

external interface btBroadphaseInterface {
    fun getOverlappingPairCache(): btOverlappingPairCache
}

external interface btBroadphaseProxy {
    var m_collisionFilterGroup: Int
    var m_collisionFilterMask: Int
}

external interface btBvhTriangleMeshShape : btConcaveShape

external interface btCapsuleShape : btCollisionShape {
    fun getUpAxis(): Int
    fun getRadius(): Float
    fun getHalfHeight(): Float
}

external interface btCollisionConfiguration

external interface btCollisionDispatcher : btDispatcher

external interface btCollisionObject {
    fun setAnisotropicFriction(anisotropicFriction: btVector3, frictionMode: Int)
    fun getCollisionShape(): btCollisionShape
    fun setContactProcessingThreshold(contactProcessingThreshold: Float)
    fun setActivationState(newState: Int)
    fun forceActivationState(newState: Int)
    fun activate()
    fun activate(forceActivation: Boolean)
    fun isActive(): Boolean
    fun isKinematicObject(): Boolean
    fun isStaticObject(): Boolean
    fun isStaticOrKinematicObject(): Boolean
    fun getRestitution(): Float
    fun getFriction(): Float
    fun getRollingFriction(): Float
    fun setRestitution(rest: Float)
    fun setFriction(frict: Float)
    fun setRollingFriction(frict: Float)
    fun getWorldTransform(): btTransform
    fun getCollisionFlags(): Int
    fun setCollisionFlags(flags: Int)
    fun setWorldTransform(worldTrans: btTransform)
    fun setCollisionShape(collisionShape: btCollisionShape)
    fun setCcdMotionThreshold(ccdMotionThreshold: Float)
    fun setCcdSweptSphereRadius(radius: Float)
    fun getUserIndex(): Int
    fun setUserIndex(index: Int)
    fun getUserPointer(): Any?      // todo: VoidPtr
    fun setUserPointer(userPointer: Any?)   // todo: VoidPtr
    fun getBroadphaseHandle(): btBroadphaseProxy
}

external interface btCollisionShape {
    fun setLocalScaling(scaling: btVector3)
    fun getLocalScaling(): btVector3
    fun calculateLocalInertia(mass: Float, inertia: btVector3)
    fun setMargin(margin: Float)
    fun getMargin(): Float
    fun getAabb(t: btTransform, aabbMin: btVector3, aabbMax: btVector3)
    fun getBoundingSphereAndRadius(center: btVector3): Float
}

external interface btCollisionWorld {
    fun getDispatcher(): btDispatcher
    //fun rayTest(rayFromWorld: btVector3, rayToWorld: btVector3, resultCallback: RayResultCallback)
    fun getPairCache(): btOverlappingPairCache
    //fun getDispatchInfo(): btDispatcherInfo
    fun addCollisionObject(collisionObject: btCollisionObject)
    fun addCollisionObject(collisionObject: btCollisionObject, collisionFilterGroup: Short, collisionFilterMask: Short)
    fun removeCollisionObject(collisionObject: btCollisionObject)
    fun getBroadphase(): btBroadphaseInterface
    //fun convexSweepTest(castShape: btConvexShape, from: btTransform, to: btTransform, resultCallback: ConvexResultCallback, allowedCcdPenetration: Float)
    //fun contactPairTest(colObjA: btCollisionObject, colObjB: btCollisionObject, resultCallback: ContactResultCallback)
    //fun contactTest(colObj: btCollisionObject, resultCallback: ContactResultCallback)
    fun updateSingleAabb(colObj: btCollisionObject)
    //fun setDebugDrawer(debugDrawer: btIDebugDraw)
    //fun getDebugDrawer(): btIDebugDraw
    //fun debugDrawWorld()
    //fun debugDrawObject(worldTransform: btTransform, shape: btCollisionShape, color: btVector3)
}

external interface btCompoundShape : btCollisionShape {
    fun addChildShape(localTransform: btTransform, shape: btCollisionShape)
    fun removeChildShape(shape: btCollisionShape)
    fun removeChildShapeByIndex(childShapeIndex: Int)
    fun getNumChildShapes(): Int
    fun getChildShape(index: Int): btCollisionShape
    fun updateChildTransform(childIndex: Int, newChildTransform: btTransform)
}

external interface btTypedConstraint {
    fun enableFeedback(needsFeedback: Boolean)
    fun getBreakingImpulseThreshold(): Float
    fun setBreakingImpulseThreshold(threshold: Float)
    fun getParam(num: Int, axis: Int): Float
    fun setParam(num: Int, value: Float, axis: Int)
}

external interface btConeShape : btCollisionShape

external interface btConstraintSolver

external interface btConcaveShape : btCollisionShape

external interface btConvexShape : btCollisionShape

external interface btConvexHullShape : btConvexShape {
    fun addPoint(point: btVector3)
    fun addPoint(point: btVector3, recalculateLocalAABB: Boolean)
    fun getNumVertices(): Int
    fun initializePolyhedralFeatures(shiftVerticesByMargin: Int)
    fun recalcLocalAabb()
    fun getConvexPolyhedron(): btConvexPolyhedron
}

external interface btConvexPolyhedron {
    val m_vertices: btVector3Array
    val m_faces: btFaceArray
}

external interface btCylinderShape : btCollisionShape

external interface btDbvtBroadphase : btBroadphaseInterface {
    override fun getOverlappingPairCache(): btOverlappingPairCache
}

external interface btDefaultCollisionConfiguration : btCollisionConfiguration

external interface btDefaultMotionState : btMotionState {
    var m_graphicsWorldTrans: btTransform
}

external interface btDiscreteDynamicsWorld : btDynamicsWorld {
    fun setGravity(gravity: btVector3)
    fun getGravity(): btVector3

    fun addRigidBody(body: btRigidBody)
    fun addRigidBody(body: btRigidBody, group: Short, mask: Short)
    fun removeRigidBody(body: btRigidBody)

    fun addConstraint(constraint: btTypedConstraint)
    fun addConstraint(constraint: btTypedConstraint, disableCollisionsBetweenLinkedBodies: Boolean)
    fun removeConstraint(constraint: btTypedConstraint)

    fun stepSimulation(timeStep: Float): Int
    fun stepSimulation(timeStep: Float, maxSubSteps: Int, fixedTimeStep: Float): Int

    fun setContactAddedCallback(funcpointer: Int)
    fun setContactProcessedCallback(funcpointer: Int)
    fun setContactDestroyedCallback(funcpointer: Int)
}

external interface btDispatcher {
    fun getNumManifolds(): Int
    fun getManifoldByIndexInternal(index: Int): btPersistentManifold
}

external interface btDynamicsWorld : btCollisionWorld {
    fun addAction(action: btActionInterface)
    fun removeAction(action: btActionInterface)
//    fun getSolverInfo(): btContactSolverInfo
//    fun setInternalTickCallback(cb: VoidPtr, worldUserInfo: VoidPtr)
//    fun setInternalTickCallback(cb: VoidPtr, worldUserInfo: VoidPtr, isPreTick: Boolean)
}

external interface btEmptyShape : btConcaveShape

external interface btFace {
    val m_indices: btIntArray
    val m_plane: FloatArray
}

external interface btFaceArray {
    fun size(): Int
    fun at(n: Int): btFace
}

external interface btHingeConstraint : btTypedConstraint {
    fun setLimit(low: Float, high: Float, softness: Float, biasFactor: Float)
    fun setLimit(low: Float, high: Float, softness: Float, biasFactor: Float, relaxationFactor: Float)
    fun enableAngularMotor(enableMotor: Boolean, targetVelocity: Float, maxMotorImpulse: Float)
    fun setAngularOnly(angularOnly: Boolean)

    fun enableMotor(enableMotor: Boolean)
    fun setMaxMotorImpulse(maxMotorImpulse: Float)
    fun setMotorTarget(targetAngle: Float, dt: Float)
}

external interface btIntArray {
    fun size(): Int
    fun at(n: Int): Int
}

external interface btMatrix3x3 {
    fun setEulerZYX(ex: Float, ey: Float, ez: Float)
    fun getRotation(q: btQuaternion)
    fun getRow(y: Int): btVector3
}

external interface btMotionState {
    fun getWorldTransform(worldTrans: btTransform)
    fun setWorldTransform(worldTrans: btTransform)
}

external interface btOverlappingPairCache {
//    fun setInternalGhostPairCallback(ghostPairCallback: btOverlappingPairCallback);
    fun getNumOverlappingPairs(): Float
}

external interface btPersistentManifold {
    fun getBody0(): btCollisionObject
    fun getBody1(): btCollisionObject
}

external interface btQuaternion {
    fun x(): Float
    fun y(): Float
    fun z(): Float
    fun w(): Float

    fun setX(x: Float)
    fun setY(y: Float)
    fun setZ(z: Float)
    fun setW(w: Float)

    fun setValue(x: Float, y: Float, z: Float, w: Float)
    fun setEulerZYX(z: Float, y: Float, x: Float)
    fun setRotation(axis: btVector3, angle: Float)
    fun normalize()
    fun length2(): Float
    fun length(): Float
    fun dot(q: btQuaternion): Float
    fun normalized(): btQuaternion
    fun getAxis(): btVector3
    fun inverse(): btQuaternion
    fun getAngle(): Float
    fun getAngleShortestPath(): Float
    fun angle(q: btQuaternion): Float
    fun angleShortestPath(q: btQuaternion): Float
    fun op_add(q: btQuaternion): btQuaternion
    fun op_sub(q: btQuaternion): btQuaternion
    fun op_mul(s: Float): btQuaternion
    fun op_mulq(q: btQuaternion): btQuaternion
    fun op_div(s: Float): btQuaternion
}

external interface btRaycastVehicle : btActionInterface {
    fun applyEngineForce(force: Float, wheel: Int)
    fun setSteeringValue(steering: Float, wheel: Int)
    fun getWheelTransformWS(wheelIndex: Int): btTransform
    fun updateWheelTransform(wheelIndex: Int, interpolatedTransform: Boolean)
    fun addWheel(connectionPointCS0: btVector3, wheelDirectionCS0: btVector3, wheelAxleCS: btVector3,
                 suspensionRestLength: Float, wheelRadius: Float, tuning: btVehicleTuning, isFrontWheel: Boolean):  btWheelInfo
    fun getNumWheels(): Int
    fun getRigidBody(): btRigidBody
    fun getWheelInfo(index: Int): btWheelInfo
    fun setBrake(brake: Float, wheelIndex: Int)
    fun setCoordinateSystem(rightIndex: Int, upIndex: Int, forwardIndex: Int)
//    float getCurrentSpeedKmHour();
//    [Const, Ref] btTransform getChassisWorldTransform();
//    float rayCast([Ref] btWheelInfo wheel);
//    void updateVehicle(float step);
//    void resetSuspension();
//    float getSteeringValue(long wheel);
//    fun updateWheelTransformsWS(wheel: btWheelInfo)
//    fun updateWheelTransformsWS(wheel: btWheelInfo, interpolatedTransform: Boolean)
//    void setPitchControl(float pitch);
//    void updateSuspension(float deltaTime);
//    void updateFriction(float timeStep);
//    long getRightAxis();
//    long getUpAxis();
//    long getForwardAxis();
//    [Value] btVector3 getForwardVector();
//    long getUserConstraintType();
//    void setUserConstraintType(long userConstraintType);
//    void setUserConstraintId(long uid);
//    long getUserConstraintId();
}

external interface btRigidBody : btCollisionObject {
    fun getCenterOfMassTransform(): btTransform
    fun setCenterOfMassTransform(xform: btTransform)
    fun setSleepingThresholds(linear: Float, angular: Float)
    fun getLinearDamping(): Float
    fun getAngularDamping(): Float
    fun setDamping(lin_damping: Float, ang_damping: Float)
    fun setMassProps(mass: Float, inertia: btVector3)
    fun getLinearFactor(): btVector3
    fun setLinearFactor(linearFactor: btVector3)
    fun applyTorque(torque: btVector3)
    fun applyLocalTorque(torque: btVector3)
    fun applyForce(force: btVector3, rel_pos: btVector3)
    fun applyCentralForce(force: btVector3)
    fun applyCentralLocalForce(force: btVector3)
    fun applyTorqueImpulse(torque: btVector3)
    fun applyImpulse(impulse: btVector3, rel_pos: btVector3)
    fun applyCentralImpulse(impulse: btVector3)
    fun updateInertiaTensor()
    fun getLinearVelocity(): btVector3
    fun getAngularVelocity(): btVector3
    fun setLinearVelocity(lin_vel: btVector3)
    fun setAngularVelocity(ang_vel: btVector3)
    fun getMotionState(): btMotionState
    fun setMotionState(motionState: btMotionState)
    fun getAngularFactor(): btVector3
    fun setAngularFactor(angularFactor: btVector3)
    fun upcast(colObj: btCollisionObject): btRigidBody
    fun getAabb(aabbMin: btVector3, aabbMax: btVector3)
    fun applyGravity()
    fun getGravity(): btVector3
    fun setGravity(acceleration: btVector3)
    fun getBroadphaseProxy(): btBroadphaseProxy
    fun clearForces()
}

external interface btRigidBodyConstructionInfo {
    var m_linearDamping: Float
    var m_angularDamping: Float
    var m_friction: Float
    var m_rollingFriction: Float
    var m_restitution: Float
    var m_linearSleepingThreshold: Float
    var m_angularSleepingThreshold: Float
    var m_additionalDamping: Boolean
    var m_additionalDampingFactor: Float
    var m_additionalLinearDampingThresholdSqr: Float
    var m_additionalAngularDampingThresholdSqr: Float
    var m_additionalAngularDampingFactor: Float
}

external interface btSequentialImpulseConstraintSolver: btConstraintSolver

external interface btShapeHull {
    fun buildHull(margin: Float): Boolean
    fun numIndices(): Int
    fun numVertices(): Int
    fun getIndexAt(n: Int): Int
    fun getVertexAt(n: Int): btVector3
}

external interface btSphereShape : btCollisionShape

external interface btStaticPlaneShape : btConcaveShape

external interface btStridingMeshInterface {
    fun setScaling(scaling: btVector3)
}

external interface btTransform {
    fun setIdentity()
    fun setOrigin(origin: btVector3)
    fun setRotation(rotation: btQuaternion)
    fun getOrigin(): btVector3
    fun getRotation(): btQuaternion
    fun getBasis(): btMatrix3x3
    fun setFromOpenGLMatrix(m: FloatArray)
    fun inverse(): btTransform
    fun op_mul(t: btTransform): btTransform
}

external interface btTriangleIndexVertexArray : btStridingMeshInterface

external interface btVector3 {
    fun x(): Float
    fun y(): Float
    fun z(): Float

    fun setX(x: Float)
    fun setY(y: Float)
    fun setZ(z: Float)
    fun setValue(x: Float, y: Float, z: Float)

    fun length(): Float
    fun normalize()
    fun rotate(wAxis: btVector3, angle: Float): btVector3
    fun dot(v: btVector3): Float

    fun op_mul(x: Float): btVector3
    fun op_add(v: btVector3): btVector3
    fun op_sub(v: btVector3): btVector3
}

external interface btVector3Array {
    fun size(): Int
    fun at(n: Int): btVector3
}

external interface btVehicleRaycaster {
    fun castRay(from: btVector3, to: btVector3, result: btVehicleRaycasterResult)
}

external interface btVehicleRaycasterResult {
    val m_hitPointInWorld: btVector3
    val m_hitNormalInWorld: btVector3
    val m_distFraction: Float
}

external interface btVehicleTuning {
    var m_suspensionStiffness: Float
    var m_suspensionCompression: Float
    var m_suspensionDamping: Float
    var m_maxSuspensionTravelCm: Float
    var m_frictionSlip: Float
    var m_maxSuspensionForce: Float
}

external interface btWheelInfo {
    var m_suspensionStiffness: Float
    var m_frictionSlip: Float
    var m_engineForce: Float
    var m_rollInfluence: Float
    var m_suspensionRestLength1: Float
    var m_wheelsRadius: Float
    var m_wheelsDampingCompression: Float
    var m_wheelsDampingRelaxation: Float
    var m_steering: Float
    var m_maxSuspensionForce: Float
    var m_maxSuspensionTravelCm: Float
    var m_wheelsSuspensionForce: Float
    var m_bIsFrontWheel: Boolean

    //val m_raycastInfo: RaycastInfo
    val m_chassisConnectionPointCS: btVector3
    val m_worldTransform: btTransform
    val m_wheelDirectionCS: btVector3
    val m_wheelAxleCS: btVector3

    var m_rotation: Float
    var m_deltaRotation: Float
    var m_brake: Float
    var m_clippedInvContactDotSuspension: Float
    var m_suspensionRelativeVelocity: Float
    var m_skidInfo: Float

    fun getSuspensionRestLength(): Float
    //fun updateWheel(chassis: btRigidBody, raycastInfo: RaycastInfo)
}


fun btQuaternion.set(v: Vec4f) = setValue(v.x, v.y, v.z, v.w)
fun btQuaternion.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x(), y(), z(), w())

fun btVector3.set(v: Vec3f) = setValue(v.x, v.y, v.z)
fun btVector3.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x(), y(), z())

fun btTransform.toMat4f(result: Mat4f = Mat4f()): Mat4f {
    val basis = getBasis()
    val origin = getOrigin()

    result[0, 0] = basis.getRow(0).x()
    result[0, 1] = basis.getRow(0).y()
    result[0, 2] = basis.getRow(0).z()

    result[1, 0] = basis.getRow(1).x()
    result[1, 1] = basis.getRow(1).y()
    result[1, 2] = basis.getRow(1).z()

    result[2, 0] = basis.getRow(2).x()
    result[2, 1] = basis.getRow(2).y()
    result[2, 2] = basis.getRow(2).z()

    result[0, 3] = origin.x()
    result[1, 3] = origin.y()
    result[2, 3] = origin.z()

    result[3, 0] = 0f
    result[3, 1] = 0f
    result[3, 2] = 0f
    result[3, 3] = 1f

    return result
}

fun Vec3f.toBtVector3() = Ammo.btVector3(x, y, z)
fun Vec4f.toBtQuaternion() = Ammo.btQuaternion(x, y, z, w)
fun Mat4f.toBtTransform(result: btTransform = Ammo.btTransform()): btTransform {
    result.setFromOpenGLMatrix(matrix)
    return result
}

object Ammo {
    @JsName("ammo")
    private var ammo: dynamic = null
    private val ammoPromise: Promise<dynamic> = js("require('ammo.js')")()

    private val onLoadListeners = mutableListOf<() -> Unit>()

    val isInitialized: Boolean
        get() = ammo != null

    internal lateinit var IDENTITY: btTransform

    fun initAmmo() {
        if (ammo == null) {
            ammoPromise.then { ammo: dynamic ->
                logI { "loaded ammo.js" }
                this.ammo = ammo
                IDENTITY = btTransform().apply { setIdentity() }
                onLoadListeners.forEach { it() }
            }
        }
    }

    fun onLoad(l: () -> Unit) {
        onLoadListeners += l
        if (ammo != null) {
            l()
        }
    }

    fun btBoxShape(boxHalfExtents: btVector3): btBoxShape = js("new this.ammo.btBoxShape(boxHalfExtents)")

    fun btBvhTriangleMeshShape(meshInterface: btStridingMeshInterface, useQuantizedAabbCompression: Boolean): btBvhTriangleMeshShape =
        js("new this.ammo.btBvhTriangleMeshShape(meshInterface, useQuantizedAabbCompression)")

    fun btCapsuleShape(radius: Float, height: Float): btCapsuleShape =
        js("new this.ammo.btCapsuleShape(radius, height)")

    fun btCollisionDispatcher(conf: btDefaultCollisionConfiguration): btCollisionDispatcher =
        js("new this.ammo.btCollisionDispatcher(conf)")

    fun btCompoundShape(): btCompoundShape = js("new this.ammo.btCompoundShape()")

    fun btConeShape(radius: Float, height: Float): btConeShape = js("new this.ammo.btConeShape(radius, height)")

    fun btConvexHullShape(): btConvexHullShape = js("new this.ammo.btConvexHullShape()")

    fun btCylinderShape(halfExtents: btVector3): btCylinderShape = js("new this.ammo.btCylinderShape(halfExtents)")

    fun btDbvtBroadphase(): btDbvtBroadphase =
        js("new this.ammo.btDbvtBroadphase()")

    fun btDefaultCollisionConfiguration(): btDefaultCollisionConfiguration =
        js("new this.ammo.btDefaultCollisionConfiguration()")

    fun btDefaultMotionState(): btDefaultMotionState =
        js("new this.ammo.btDefaultMotionState()")

    fun btDefaultMotionState(startTrans: btTransform, centerOfMassOffset: btTransform): btDefaultMotionState =
        js("new this.ammo.btDefaultMotionState(startTrans, centerOfMassOffset)")

    fun btDefaultVehicleRaycaster(world: btDynamicsWorld): btVehicleRaycaster =
        js("new this.ammo.btDefaultVehicleRaycaster(world)")

    fun btDiscreteDynamicsWorld(dispatcher: btDispatcher,
                                pairCache: btBroadphaseInterface,
                                constraintSolver: btConstraintSolver,
                                collisionConfiguration: btCollisionConfiguration): btDiscreteDynamicsWorld =
        js("new this.ammo.btDiscreteDynamicsWorld(dispatcher, pairCache, constraintSolver, collisionConfiguration)")

    fun btHingeConstraint(rbA: btRigidBody, rbB: btRigidBody,
                          rbAFrame: btTransform, rbBFrame: btTransform): btHingeConstraint =
        js("new this.ammo.btHingeConstraint(rbA, rbB, rbAFrame, rbBFrame)")

    fun btHingeConstraint(rbA: btRigidBody, rbB: btRigidBody,
                          pivotInA: btVector3, pivotInB: btVector3,
                          axisInA: btVector3, axisInB: btVector3): btHingeConstraint =
        js("new this.ammo.btHingeConstraint(rbA, rbB, pivotInA, pivotInB, axisInA, axisInB)")

    fun btQuaternion(x: Float, y: Float, z: Float, w: Float): btQuaternion =
        js("new this.ammo.btQuaternion(x, y, z, w)")

    fun btRaycastVehicle(tuning: btVehicleTuning, chassis: btRigidBody, raycaster: btVehicleRaycaster): btRaycastVehicle =
        js("new this.ammo.btRaycastVehicle(tuning, chassis, raycaster)")

    fun btRigidBody(constructionInfo: btRigidBodyConstructionInfo): btRigidBody =
        js("new this.ammo.btRigidBody(constructionInfo)")

    fun btRigidBodyConstructionInfo(mass: Float,
                                    motionState: btMotionState,
                                    collisionShape: btCollisionShape): btRigidBodyConstructionInfo =
        js("new this.ammo.btRigidBodyConstructionInfo(mass, motionState, collisionShape)")

    fun btRigidBodyConstructionInfo(mass: Float,
                                    motionState: btMotionState,
                                    collisionShape: btCollisionShape,
                                    localInertia: btVector3): btRigidBodyConstructionInfo =
        js("new this.ammo.btRigidBodyConstructionInfo(mass, motionState, collisionShape, localInertia)")

    fun btSequentialImpulseConstraintSolver(): btSequentialImpulseConstraintSolver =
        js("new this.ammo.btSequentialImpulseConstraintSolver()")

    fun btShapeHull(shape: btConvexShape): btShapeHull = js("new this.ammo.btShapeHull(shape)")

    fun btSphereShape(radius: Float): btSphereShape = js("new this.ammo.btSphereShape(radius)")

    fun btStaticPlaneShape(planeNormal: btVector3, planeConstant: Float): btStaticPlaneShape =
        js("new this.ammo.btStaticPlaneShape(planeNormal, planeConstant)")

    fun btTransform(): btTransform = js("new this.ammo.btTransform()")

    fun btTransform(q: btQuaternion, v: btVector3): btTransform = js("new this.ammo.btTransform(q, v)")

    fun btTriangleIndexVertexArray(numTriangles: Int,
                                   triangleIndexBase: IntArray,
                                   triangleIndexStride: Int,
                                   numVertices: Int,
                                   vertexBase: FloatArray,
                                   vertexStride: Int): btTriangleIndexVertexArray =
        js("new this.ammo.btTriangleIndexVertexArray(numTriangles, triangleIndexBase, triangleIndexStride, numVertices, vertexBase, vertexStride)")

    fun btVector3(x: Float, y: Float, z: Float): btVector3 = js("new this.ammo.btVector3(x, y, z)")

    fun btVehicleTuning(): btVehicleTuning = js("new this.ammo.btVehicleTuning()")

    val DISABLE_DEACTIVATION = 4
}
