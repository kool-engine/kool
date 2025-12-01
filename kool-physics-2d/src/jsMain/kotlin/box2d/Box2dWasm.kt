/*
 * Generated from WebIDL by webidl-util
 */
@file:Suppress("UnsafeCastFromDynamic", "ClassName", "FunctionName", "UNUSED_PARAMETER", "unused", "INLINE_CLASS_IN_EXTERNAL_DECLARATION_WARNING", "NOTHING_TO_INLINE")

package box2d

external interface B2_Shape {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param result WebIDL type: [b2ShapeDef] (Ref)
     */
    fun defaultShapeDef(result: b2ShapeDef)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param def    WebIDL type: [b2ShapeDef] (Const)
     * @param circle WebIDL type: [b2Circle] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createCircleShape(bodyId: Long, def: b2ShapeDef, circle: b2Circle): Long

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2ShapeDef] (Const)
     * @param segment WebIDL type: [b2Segment] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createSegmentShape(bodyId: Long, def: b2ShapeDef, segment: b2Segment): Long

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2ShapeDef] (Const)
     * @param capsule WebIDL type: [b2Capsule] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createCapsuleShape(bodyId: Long, def: b2ShapeDef, capsule: b2Capsule): Long

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2ShapeDef] (Const)
     * @param polygon WebIDL type: [b2Polygon] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createPolygonShape(bodyId: Long, def: b2ShapeDef, polygon: b2Polygon): Long

    /**
     * @param shapeId        WebIDL type: unsigned long long
     * @param updateBodyMass WebIDL type: boolean
     */
    fun destroyShape(shapeId: Long, updateBodyMass: Boolean)

    /**
     * @param id WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isValid(id: Long): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2ShapeType] (enum)
     */
    fun getType(shapeId: Long): b2ShapeType

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: unsigned long long
     */
    fun getBody(shapeId: Long): Long

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2WorldId] (Value)
     */
    fun getWorld(shapeId: Long): b2WorldId

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSensor(shapeId: Long): Boolean

    /**
     * @param shapeId  WebIDL type: unsigned long long
     * @param userData WebIDL type: VoidPtr
     */
    fun setUserData(shapeId: Long, userData: Any)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: VoidPtr
     */
    fun getUserData(shapeId: Long): Any

    /**
     * @param shapeId        WebIDL type: unsigned long long
     * @param density        WebIDL type: float
     * @param updateBodyMass WebIDL type: boolean
     */
    fun setDensity(shapeId: Long, density: Float, updateBodyMass: Boolean)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getDensity(shapeId: Long): Float

    /**
     * @param shapeId  WebIDL type: unsigned long long
     * @param friction WebIDL type: float
     */
    fun setFriction(shapeId: Long, friction: Float)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getFriction(shapeId: Long): Float

    /**
     * @param shapeId     WebIDL type: unsigned long long
     * @param restitution WebIDL type: float
     */
    fun setRestitution(shapeId: Long, restitution: Float)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getRestitution(shapeId: Long): Float

    /**
     * @param shapeId  WebIDL type: unsigned long long
     * @param material WebIDL type: long
     */
    fun setMaterial(shapeId: Long, material: Int)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getMaterial(shapeId: Long): Int

    /**
     * @param shapeId         WebIDL type: unsigned long long
     * @param surfaceMaterial WebIDL type: [b2SurfaceMaterial] (Ref)
     */
    fun setSurfaceMaterial(shapeId: Long, surfaceMaterial: b2SurfaceMaterial)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2SurfaceMaterial] (Value)
     */
    fun getSurfaceMaterial(shapeId: Long): b2SurfaceMaterial

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Filter] (Value)
     */
    fun getFilter(shapeId: Long): b2Filter

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param filter  WebIDL type: [b2Filter] (Ref)
     */
    fun setFilter(shapeId: Long, filter: b2Filter)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableSensorEvents(shapeId: Long, flag: Boolean)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun areSensorEventsEnabled(shapeId: Long): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableContactEvents(shapeId: Long, flag: Boolean)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun areContactEventsEnabled(shapeId: Long): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enablePreSolveEvents(shapeId: Long, flag: Boolean)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun arePreSolveEventsEnabled(shapeId: Long): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableHitEvents(shapeId: Long, flag: Boolean)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun areHitEventsEnabled(shapeId: Long): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param point   WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: boolean
     */
    fun testPoint(shapeId: Long, point: b2Vec2): Boolean

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param input   WebIDL type: [b2RayCastInput] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun rayCast(shapeId: Long, input: b2RayCastInput): b2CastOutput

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Circle] (Value)
     */
    fun getCircle(shapeId: Long): b2Circle

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Segment] (Value)
     */
    fun getSegment(shapeId: Long): b2Segment

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2ChainSegment] (Value)
     */
    fun getChainSegment(shapeId: Long): b2ChainSegment

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Capsule] (Value)
     */
    fun getCapsule(shapeId: Long): b2Capsule

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Polygon] (Value)
     */
    fun getPolygon(shapeId: Long): b2Polygon

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param circle  WebIDL type: [b2Circle] (Const)
     */
    fun setCircle(shapeId: Long, circle: b2Circle)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param capsule WebIDL type: [b2Capsule] (Const)
     */
    fun setCapsule(shapeId: Long, capsule: b2Capsule)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param segment WebIDL type: [b2Segment] (Const)
     */
    fun setSegment(shapeId: Long, segment: b2Segment)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param polygon WebIDL type: [b2Polygon] (Const)
     */
    fun setPolygon(shapeId: Long, polygon: b2Polygon)

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: unsigned long long
     */
    fun getParentChain(shapeId: Long): Long

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getContactCapacity(shapeId: Long): Int

    /**
     * @param shapeId     WebIDL type: unsigned long long
     * @param contactData WebIDL type: [b2ContactDataArray]
     * @return WebIDL type: long
     */
    fun getContactData(shapeId: Long, contactData: b2ContactDataArray): Int

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getSensorCapacity(shapeId: Long): Int

    /**
     * @param shapeId  WebIDL type: unsigned long long
     * @param overlaps WebIDL type: [b2ShapeIdArray]
     * @return WebIDL type: long
     */
    fun getSensorOverlaps(shapeId: Long, overlaps: b2ShapeIdArray): Int

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun getAABB(shapeId: Long): b2AABB

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @return WebIDL type: [b2MassData] (Value)
     */
    fun getMassData(shapeId: Long): b2MassData

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param target  WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getClosestPoint(shapeId: Long, target: b2Vec2): b2Vec2

}

fun B2_ShapeFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Shape = js("_module.wrapPointer(ptr, _module.B2_Shape)")

external interface B2_Chain {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param result WebIDL type: [b2ChainDef] (Ref)
     */
    fun defaultChainDef(result: b2ChainDef)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param def    WebIDL type: [b2ChainDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createChain(bodyId: Long, def: b2ChainDef): Long

    /**
     * @param chainId WebIDL type: unsigned long long
     */
    fun destroyChain(chainId: Long)

    /**
     * @param id WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isValid(id: Long): Boolean

    /**
     * @param chainId WebIDL type: unsigned long long
     * @return WebIDL type: [b2WorldId] (Value)
     */
    fun getWorld(chainId: Long): b2WorldId

    /**
     * @param chainId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getSegmentCount(chainId: Long): Int

    /**
     * @param chainId      WebIDL type: unsigned long long
     * @param segmentArray WebIDL type: [b2ShapeIdArray]
     * @return WebIDL type: long
     */
    fun getSegments(chainId: Long, segmentArray: b2ShapeIdArray): Int

    /**
     * @param chainId  WebIDL type: unsigned long long
     * @param friction WebIDL type: float
     */
    fun setFriction(chainId: Long, friction: Float)

    /**
     * @param chainId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getFriction(chainId: Long): Float

    /**
     * @param chainId     WebIDL type: unsigned long long
     * @param restitution WebIDL type: float
     */
    fun setRestitution(chainId: Long, restitution: Float)

    /**
     * @param chainId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getRestitution(chainId: Long): Float

    /**
     * @param chainId  WebIDL type: unsigned long long
     * @param material WebIDL type: long
     */
    fun setMaterial(chainId: Long, material: Int)

    /**
     * @param chainId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getMaterial(chainId: Long): Int

}

fun B2_ChainFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Chain = js("_module.wrapPointer(ptr, _module.B2_Chain)")

external interface b2ShapeDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
    /**
     * WebIDL type: [b2SurfaceMaterial] (Value)
     */
    var material: b2SurfaceMaterial
    /**
     * WebIDL type: float
     */
    var density: Float
    /**
     * WebIDL type: [b2Filter] (Value)
     */
    var filter: b2Filter
    /**
     * WebIDL type: boolean
     */
    var isSensor: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSensorEvents: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableContactEvents: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableHitEvents: Boolean
    /**
     * WebIDL type: boolean
     */
    var enablePreSolveEvents: Boolean
    /**
     * WebIDL type: boolean
     */
    var invokeContactCreation: Boolean
    /**
     * WebIDL type: boolean
     */
    var updateBodyMass: Boolean
}

fun b2ShapeDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeDef = js("new _module.b2ShapeDef()")

fun b2ShapeDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeDef = js("_module.wrapPointer(ptr, _module.b2ShapeDef)")

fun b2ShapeDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ChainDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
    /**
     * WebIDL type: [b2Vec2] (Const)
     */
    var points: b2Vec2
    /**
     * WebIDL type: long
     */
    var count: Int
    /**
     * WebIDL type: [b2SurfaceMaterial] (Const)
     */
    var materials: b2SurfaceMaterial
    /**
     * WebIDL type: long
     */
    var materialCount: Int
    /**
     * WebIDL type: [b2Filter] (Value)
     */
    var filter: b2Filter
    /**
     * WebIDL type: boolean
     */
    var isLoop: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSensorEvents: Boolean
    /**
     * WebIDL type: long
     */
    var internalValue: Int
}

fun b2ChainDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainDef = js("new _module.b2ChainDef()")

fun b2ChainDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainDef = js("_module.wrapPointer(ptr, _module.b2ChainDef)")

fun b2ChainDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Filter {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long long
     */
    var categoryBits: Long
    /**
     * WebIDL type: long
     */
    var groupIndex: Int
    /**
     * WebIDL type: unsigned long long
     */
    var maskBits: Long
}

fun b2Filter(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Filter = js("new _module.b2Filter()")

fun b2FilterFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Filter = js("_module.wrapPointer(ptr, _module.b2Filter)")

fun b2Filter.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2QueryFilter {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long long
     */
    var categoryBits: Long
    /**
     * WebIDL type: unsigned long long
     */
    var maskBits: Long
}

fun b2QueryFilter(_module: dynamic = Box2dWasmLoader.box2dWasm): b2QueryFilter = js("new _module.b2QueryFilter()")

fun b2QueryFilterFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2QueryFilter = js("_module.wrapPointer(ptr, _module.b2QueryFilter)")

fun b2QueryFilter.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2SurfaceMaterial {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var customColor: Int
    /**
     * WebIDL type: float
     */
    var friction: Float
    /**
     * WebIDL type: float
     */
    var restitution: Float
    /**
     * WebIDL type: float
     */
    var rollingResistance: Float
    /**
     * WebIDL type: float
     */
    var tangentSpeed: Float
    /**
     * WebIDL type: long
     */
    var userMaterialId: Int
}

fun b2SurfaceMaterial(_module: dynamic = Box2dWasmLoader.box2dWasm): b2SurfaceMaterial = js("new _module.b2SurfaceMaterial()")

fun b2SurfaceMaterialFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2SurfaceMaterial = js("_module.wrapPointer(ptr, _module.b2SurfaceMaterial)")

fun b2SurfaceMaterial.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactData {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Manifold] (Value)
     */
    var manifold: b2Manifold
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdA: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdB: b2ShapeId
}

fun b2ContactData(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactData = js("new _module.b2ContactData()")

fun b2ContactDataFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactData = js("_module.wrapPointer(ptr, _module.b2ContactData)")

fun b2ContactData.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_Vec2 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param a WebIDL type: [b2Vec2] (Ref)
     * @param b WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun dot(a: b2Vec2, b: b2Vec2): Float

    /**
     * @param a WebIDL type: [b2Vec2] (Ref)
     * @param b WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun cross(a: b2Vec2, b: b2Vec2): Float

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param s      WebIDL type: float
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun crossVS(v: b2Vec2, s: Float, result: b2Vec2)

    /**
     * @param s      WebIDL type: float
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun crossSV(s: Float, v: b2Vec2, result: b2Vec2)

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun leftPerp(v: b2Vec2, result: b2Vec2)

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun rightPerp(v: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun add(a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun sub(a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun neg(a: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param t      WebIDL type: float
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun lerp(a: b2Vec2, b: b2Vec2, t: Float, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun mul(a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param s      WebIDL type: float
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun mulSV(s: Float, v: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param s      WebIDL type: float
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun mulAdd(a: b2Vec2, s: Float, b: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param s      WebIDL type: float
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun mulSub(a: b2Vec2, s: Float, b: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun abs(a: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun min(a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun max(a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param a      WebIDL type: [b2Vec2] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun clamp(v: b2Vec2, a: b2Vec2, b: b2Vec2, result: b2Vec2)

    /**
     * @param v WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun length(v: b2Vec2): Float

    /**
     * @param a WebIDL type: [b2Vec2] (Ref)
     * @param b WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun distance(a: b2Vec2, b: b2Vec2): Float

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun normalize(v: b2Vec2, result: b2Vec2)

    /**
     * @param a WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: boolean
     */
    fun isNormalized(a: b2Vec2): Boolean

    /**
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun getLengthAndNormalize(v: b2Vec2, result: b2Vec2): Float

    /**
     * @param v WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun lengthSquared(v: b2Vec2): Float

    /**
     * @param v WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(v: b2Vec2): Boolean

}

fun B2_Vec2FromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Vec2 = js("_module.wrapPointer(ptr, _module.B2_Vec2)")

external interface B2_Rot {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param q WebIDL type: [b2Rot] (Ref)
     * @return WebIDL type: float
     */
    fun getAngle(q: b2Rot): Float

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun getXAxis(q: b2Rot, result: b2Vec2)

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun getYAxis(q: b2Rot, result: b2Vec2)

    /**
     * @param radians WebIDL type: float
     * @param result  WebIDL type: [b2Rot] (Ref)
     */
    fun makeRot(radians: Float, result: b2Rot)

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param result WebIDL type: [b2Rot] (Ref)
     */
    fun normalize(q: b2Rot, result: b2Rot)

    /**
     * @param q WebIDL type: [b2Rot] (Ref)
     * @return WebIDL type: boolean
     */
    fun isNormalized(q: b2Rot): Boolean

    /**
     * @param q1         WebIDL type: [b2Rot] (Ref)
     * @param deltaAngle WebIDL type: float
     * @param result     WebIDL type: [b2Rot] (Ref)
     */
    fun integrateRotation(q1: b2Rot, deltaAngle: Float, result: b2Rot)

    /**
     * @param q1     WebIDL type: [b2Rot] (Ref)
     * @param q2     WebIDL type: [b2Rot] (Ref)
     * @param t      WebIDL type: float
     * @param result WebIDL type: [b2Rot] (Ref)
     */
    fun nLerp(q1: b2Rot, q2: b2Rot, t: Float, result: b2Rot)

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param r      WebIDL type: [b2Rot] (Ref)
     * @param result WebIDL type: [b2Rot] (Ref)
     */
    fun mulRot(q: b2Rot, r: b2Rot, result: b2Rot)

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param r      WebIDL type: [b2Rot] (Ref)
     * @param result WebIDL type: [b2Rot] (Ref)
     */
    fun invMulRot(q: b2Rot, r: b2Rot, result: b2Rot)

    /**
     * @param b WebIDL type: [b2Rot] (Ref)
     * @param a WebIDL type: [b2Rot] (Ref)
     * @return WebIDL type: float
     */
    fun relativeAngle(b: b2Rot, a: b2Rot): Float

    /**
     * @param radians WebIDL type: float
     * @return WebIDL type: float
     */
    fun unwindAngle(radians: Float): Float

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun rotateVector(q: b2Rot, v: b2Vec2, result: b2Vec2)

    /**
     * @param q      WebIDL type: [b2Rot] (Ref)
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun invRotateVector(q: b2Rot, v: b2Vec2, result: b2Vec2)

    /**
     * @param q1    WebIDL type: [b2Rot] (Ref)
     * @param q2    WebIDL type: [b2Rot] (Ref)
     * @param inv_h WebIDL type: float
     * @return WebIDL type: float
     */
    fun computeAngularVelocity(q1: b2Rot, q2: b2Rot, inv_h: Float): Float

    /**
     * @param v1     WebIDL type: [b2Vec2] (Ref)
     * @param v2     WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Rot] (Ref)
     */
    fun computeRotationBetweenUnitVectors(v1: b2Vec2, v2: b2Vec2, result: b2Rot)

    /**
     * @param q WebIDL type: [b2Rot] (Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(q: b2Rot): Boolean

}

fun B2_RotFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Rot = js("_module.wrapPointer(ptr, _module.B2_Rot)")

external interface B2_Transform {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param t      WebIDL type: [b2Transform] (Ref)
     * @param p      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun transformPoint(t: b2Transform, p: b2Vec2, result: b2Vec2)

    /**
     * @param t      WebIDL type: [b2Transform] (Ref)
     * @param p      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun invTransformPoint(t: b2Transform, p: b2Vec2, result: b2Vec2)

    /**
     * @param A      WebIDL type: [b2Transform] (Ref)
     * @param B      WebIDL type: [b2Transform] (Ref)
     * @param result WebIDL type: [b2Transform] (Ref)
     */
    fun mulTransforms(A: b2Transform, B: b2Transform, result: b2Transform)

    /**
     * @param A      WebIDL type: [b2Transform] (Ref)
     * @param B      WebIDL type: [b2Transform] (Ref)
     * @param result WebIDL type: [b2Transform] (Ref)
     */
    fun invMulTransforms(A: b2Transform, B: b2Transform, result: b2Transform)

}

fun B2_TransformFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Transform = js("_module.wrapPointer(ptr, _module.B2_Transform)")

external interface B2_Mat22 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param A      WebIDL type: [b2Mat22] (Ref)
     * @param v      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun mulMV(A: b2Mat22, v: b2Vec2, result: b2Vec2)

    /**
     * @param A      WebIDL type: [b2Mat22] (Ref)
     * @param result WebIDL type: [b2Mat22] (Ref)
     */
    fun getInverse22(A: b2Mat22, result: b2Mat22)

    /**
     * @param A      WebIDL type: [b2Mat22] (Ref)
     * @param b      WebIDL type: [b2Vec2] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun solve22(A: b2Mat22, b: b2Vec2, result: b2Vec2)

}

fun B2_Mat22FromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Mat22 = js("_module.wrapPointer(ptr, _module.B2_Mat22)")

external interface B2_AABB {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param a WebIDL type: [b2AABB] (Ref)
     * @param b WebIDL type: [b2AABB] (Ref)
     * @return WebIDL type: boolean
     */
    fun contains(a: b2AABB, b: b2AABB): Boolean

    /**
     * @param a      WebIDL type: [b2AABB] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun center(a: b2AABB, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2AABB] (Ref)
     * @param result WebIDL type: [b2Vec2] (Ref)
     */
    fun extents(a: b2AABB, result: b2Vec2)

    /**
     * @param a      WebIDL type: [b2AABB] (Ref)
     * @param b      WebIDL type: [b2AABB] (Ref)
     * @param result WebIDL type: [b2AABB] (Ref)
     */
    fun unionAABB(a: b2AABB, b: b2AABB, result: b2AABB)

    /**
     * @param a WebIDL type: [b2AABB] (Ref)
     * @param b WebIDL type: [b2AABB] (Ref)
     * @return WebIDL type: boolean
     */
    fun overlaps(a: b2AABB, b: b2AABB): Boolean

    /**
     * @param aabb WebIDL type: [b2AABB] (Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(aabb: b2AABB): Boolean

}

fun B2_AABBFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_AABB = js("_module.wrapPointer(ptr, _module.B2_AABB)")

external interface B2_Plane {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param plane WebIDL type: [b2Plane] (Ref)
     * @param point WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: float
     */
    fun planeSeparation(plane: b2Plane, point: b2Vec2): Float

    /**
     * @param plane WebIDL type: [b2Plane] (Ref)
     * @return WebIDL type: boolean
     */
    fun isValid(plane: b2Plane): Boolean

}

fun B2_PlaneFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Plane = js("_module.wrapPointer(ptr, _module.B2_Plane)")

external interface B2_Math {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param radians WebIDL type: float
     * @param result  WebIDL type: [b2CosSin] (Ref)
     */
    fun computeCosSin(radians: Float, result: b2CosSin)

    /**
     * @param hertz        WebIDL type: float
     * @param dampingRatio WebIDL type: float
     * @param position     WebIDL type: float
     * @param velocity     WebIDL type: float
     * @param timeStep     WebIDL type: float
     * @return WebIDL type: float
     */
    fun springDamper(hertz: Float, dampingRatio: Float, position: Float, velocity: Float, timeStep: Float): Float

    /**
     * @param lengthUnits WebIDL type: float
     */
    fun setLengthUnitsPerMeter(lengthUnits: Float)

    /**
     * @return WebIDL type: float
     */
    fun getLengthUnitsPerMeter(): Float

}

fun B2_MathFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Math = js("_module.wrapPointer(ptr, _module.B2_Math)")

var B2_Math.lengthUnitsPerMeter
    get() = getLengthUnitsPerMeter()
    set(value) { setLengthUnitsPerMeter(value) }

external interface b2Vec2 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var x: Float
    /**
     * WebIDL type: float
     */
    var y: Float
}

fun b2Vec2(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Vec2 = js("new _module.b2Vec2()")

fun b2Vec2FromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Vec2 = js("_module.wrapPointer(ptr, _module.b2Vec2)")

fun b2Vec2.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CosSin {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var cosine: Float
    /**
     * WebIDL type: float
     */
    var sine: Float
}

fun b2CosSin(_module: dynamic = Box2dWasmLoader.box2dWasm): b2CosSin = js("new _module.b2CosSin()")

fun b2CosSinFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CosSin = js("_module.wrapPointer(ptr, _module.b2CosSin)")

fun b2CosSin.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Rot {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var c: Float
    /**
     * WebIDL type: float
     */
    var s: Float
}

fun b2Rot(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Rot = js("new _module.b2Rot()")

fun b2RotFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Rot = js("_module.wrapPointer(ptr, _module.b2Rot)")

fun b2Rot.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Transform {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var p: b2Vec2
    /**
     * WebIDL type: [b2Rot] (Value)
     */
    var q: b2Rot
}

fun b2Transform(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Transform = js("new _module.b2Transform()")

fun b2TransformFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Transform = js("_module.wrapPointer(ptr, _module.b2Transform)")

fun b2Transform.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Mat22 {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var cx: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var cy: b2Vec2
}

fun b2Mat22(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Mat22 = js("new _module.b2Mat22()")

fun b2Mat22FromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Mat22 = js("_module.wrapPointer(ptr, _module.b2Mat22)")

fun b2Mat22.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2AABB {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var lowerBound: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var upperBound: b2Vec2
}

fun b2AABB(_module: dynamic = Box2dWasmLoader.box2dWasm): b2AABB = js("new _module.b2AABB()")

fun b2AABBFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2AABB = js("_module.wrapPointer(ptr, _module.b2AABB)")

fun b2AABB.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Plane {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var normal: b2Vec2
    /**
     * WebIDL type: float
     */
    var offset: Float
}

fun b2Plane(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Plane = js("new _module.b2Plane()")

fun b2PlaneFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Plane = js("_module.wrapPointer(ptr, _module.b2Plane)")

fun b2Plane.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_World {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param result WebIDL type: [b2WorldDef] (Ref)
     */
    fun defaultWorldDef(result: b2WorldDef)

    /**
     * @param def WebIDL type: [b2WorldDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createWorld(def: b2WorldDef): Long

    /**
     * @param worldId WebIDL type: unsigned long long
     */
    fun destroyWorld(worldId: Long)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isValid(worldId: Long): Boolean

    /**
     * @param worldId      WebIDL type: unsigned long long
     * @param timeStep     WebIDL type: float
     * @param subStepCount WebIDL type: long
     */
    fun step(worldId: Long, timeStep: Float, subStepCount: Int)

    /**
     * @param worldId   WebIDL type: unsigned long long
     * @param draw      WebIDL type: [b2DebugDraw]
     * @param callbacks WebIDL type: [b2DebugDrawCallbacks]
     */
    fun draw(worldId: Long, draw: b2DebugDraw, callbacks: b2DebugDrawCallbacks)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2BodyEvents] (Value)
     */
    fun getBodyEvents(worldId: Long): b2BodyEvents

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2SensorEvents] (Value)
     */
    fun getSensorEvents(worldId: Long): b2SensorEvents

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2ContactEvents] (Value)
     */
    fun getContactEvents(worldId: Long): b2ContactEvents

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param aabb    WebIDL type: [b2AABB] (Ref)
     * @param filter  WebIDL type: [b2QueryFilter] (Ref)
     * @param fcn     WebIDL type: [b2OverlapResultFcnI]
     * @return WebIDL type: [b2TreeStats] (Value)
     */
    fun overlapAABB(worldId: Long, aabb: b2AABB, filter: b2QueryFilter, fcn: b2OverlapResultFcnI): b2TreeStats

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param proxy   WebIDL type: [b2ShapeProxy] (Const)
     * @param filter  WebIDL type: [b2QueryFilter] (Ref)
     * @param fcn     WebIDL type: [b2OverlapResultFcnI]
     * @return WebIDL type: [b2TreeStats] (Value)
     */
    fun overlapShape(worldId: Long, proxy: b2ShapeProxy, filter: b2QueryFilter, fcn: b2OverlapResultFcnI): b2TreeStats

    /**
     * @param worldId     WebIDL type: unsigned long long
     * @param origin      WebIDL type: [b2Vec2] (Ref)
     * @param translation WebIDL type: [b2Vec2] (Ref)
     * @param filter      WebIDL type: [b2QueryFilter] (Ref)
     * @param fcn         WebIDL type: [b2CastResultFcnI]
     * @return WebIDL type: [b2TreeStats] (Value)
     */
    fun castRay(worldId: Long, origin: b2Vec2, translation: b2Vec2, filter: b2QueryFilter, fcn: b2CastResultFcnI): b2TreeStats

    /**
     * @param worldId     WebIDL type: unsigned long long
     * @param origin      WebIDL type: [b2Vec2] (Ref)
     * @param translation WebIDL type: [b2Vec2] (Ref)
     * @param filter      WebIDL type: [b2QueryFilter] (Ref)
     * @return WebIDL type: [b2RayResult] (Value)
     */
    fun castRayClosest(worldId: Long, origin: b2Vec2, translation: b2Vec2, filter: b2QueryFilter): b2RayResult

    /**
     * @param worldId     WebIDL type: unsigned long long
     * @param proxy       WebIDL type: [b2ShapeProxy] (Const)
     * @param translation WebIDL type: [b2Vec2] (Ref)
     * @param filter      WebIDL type: [b2QueryFilter] (Ref)
     * @param fcn         WebIDL type: [b2CastResultFcnI]
     * @return WebIDL type: [b2TreeStats] (Value)
     */
    fun castShape(worldId: Long, proxy: b2ShapeProxy, translation: b2Vec2, filter: b2QueryFilter, fcn: b2CastResultFcnI): b2TreeStats

    /**
     * @param worldId     WebIDL type: unsigned long long
     * @param mover       WebIDL type: [b2Capsule] (Const)
     * @param translation WebIDL type: [b2Vec2] (Ref)
     * @param filter      WebIDL type: [b2QueryFilter] (Ref)
     * @return WebIDL type: float
     */
    fun castMover(worldId: Long, mover: b2Capsule, translation: b2Vec2, filter: b2QueryFilter): Float

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param mover   WebIDL type: [b2Capsule] (Const)
     * @param filter  WebIDL type: [b2QueryFilter] (Ref)
     * @param fcn     WebIDL type: [b2PlaneResultFcnI]
     */
    fun collideMover(worldId: Long, mover: b2Capsule, filter: b2QueryFilter, fcn: b2PlaneResultFcnI)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableSleeping(worldId: Long, flag: Boolean)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSleepingEnabled(worldId: Long): Boolean

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableContinuous(worldId: Long, flag: Boolean)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isContinuousEnabled(worldId: Long): Boolean

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param value   WebIDL type: float
     */
    fun setRestitutionThreshold(worldId: Long, value: Float)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getRestitutionThreshold(worldId: Long): Float

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param value   WebIDL type: float
     */
    fun setHitEventThreshold(worldId: Long, value: Float)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getHitEventThreshold(worldId: Long): Float

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param gravity WebIDL type: [b2Vec2] (Ref)
     */
    fun setGravity(worldId: Long, gravity: b2Vec2)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getGravity(worldId: Long): b2Vec2

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param fcn     WebIDL type: [b2CustomFilterFcnI]
     */
    fun setCustomFilterCallback(worldId: Long, fcn: b2CustomFilterFcnI)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param fcn     WebIDL type: [b2PreSolveFcnI]
     */
    fun setPreSolveCallback(worldId: Long, fcn: b2PreSolveFcnI)

    /**
     * @param worldId  WebIDL type: unsigned long long
     * @param callback WebIDL type: [b2FrictionCallbackI]
     */
    fun setFrictionCallback(worldId: Long, callback: b2FrictionCallbackI)

    /**
     * @param worldId  WebIDL type: unsigned long long
     * @param callback WebIDL type: [b2RestitutionCallbackI]
     */
    fun setRestitutionCallback(worldId: Long, callback: b2RestitutionCallbackI)

    /**
     * @param worldId      WebIDL type: unsigned long long
     * @param explosionDef WebIDL type: [b2ExplosionDef] (Const)
     */
    fun explode(worldId: Long, explosionDef: b2ExplosionDef)

    /**
     * @param worldId      WebIDL type: unsigned long long
     * @param hertz        WebIDL type: float
     * @param dampingRatio WebIDL type: float
     * @param pushSpeed    WebIDL type: float
     */
    fun setContactTuning(worldId: Long, hertz: Float, dampingRatio: Float, pushSpeed: Float)

    /**
     * @param worldId            WebIDL type: unsigned long long
     * @param maximumLinearSpeed WebIDL type: float
     */
    fun setMaximumLinearSpeed(worldId: Long, maximumLinearSpeed: Float)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaximumLinearSpeed(worldId: Long): Float

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableWarmStarting(worldId: Long, flag: Boolean)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isWarmStartingEnabled(worldId: Long): Boolean

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getAwakeBodyCount(worldId: Long): Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Profile] (Value)
     */
    fun getProfile(worldId: Long): b2Profile

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Counters] (Value)
     */
    fun getCounters(worldId: Long): b2Counters

    /**
     * @param worldId WebIDL type: unsigned long long
     */
    fun dumpMemoryStats(worldId: Long)

    /**
     * @param worldId  WebIDL type: unsigned long long
     * @param userData WebIDL type: VoidPtr
     */
    fun setUserData(worldId: Long, userData: Any)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @return WebIDL type: VoidPtr
     */
    fun getUserData(worldId: Long): Any

    /**
     * @param worldId WebIDL type: unsigned long long
     */
    fun rebuildStaticTree(worldId: Long)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param flag    WebIDL type: boolean
     */
    fun enableSpeculative(worldId: Long, flag: Boolean)

}

fun B2_WorldFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_World = js("_module.wrapPointer(ptr, _module.B2_World)")

external interface TaskManager {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldDef    WebIDL type: [b2WorldDef] (Ref)
     * @param threadCount WebIDL type: long
     */
    fun install(worldDef: b2WorldDef, threadCount: Int)

    /**
     * @param start        WebIDL type: unsigned long
     * @param end          WebIDL type: unsigned long
     * @param threadIndex  WebIDL type: unsigned long
     * @param box2dTask    WebIDL type: unsigned long long
     * @param box2dContext WebIDL type: unsigned long long
     */
    fun executeTask(start: Int, end: Int, threadIndex: Int, box2dTask: Long, box2dContext: Long)

}

fun TaskManagerFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): TaskManager = js("_module.wrapPointer(ptr, _module.TaskManager)")

fun TaskManager.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface TaskManagerImpl : TaskManager {
    /**
     * param box2dTask    WebIDL type: unsigned long long
     * param itemCount    WebIDL type: long
     * param minRange     WebIDL type: long
     * param box2dContext WebIDL type: unsigned long long
     * return WebIDL type: unsigned long long
     */
    var enqueueTask: (box2dTask: Long, itemCount: Int, minRange: Int, box2dContext: Long) -> Long

    /**
     * param userTask WebIDL type: unsigned long long
     */
    var finishTask: (userTask: Long) -> Unit

}

fun TaskManagerImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): TaskManagerImpl = js("new _module.TaskManagerImpl()")

external interface b2BodyEvents {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var moveCount: Int
    /**
     * WebIDL type: [b2BodyMoveEvent]
     */
    var moveEvents: b2BodyMoveEvent
}

fun b2BodyEvents(_module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyEvents = js("new _module.b2BodyEvents()")

fun b2BodyEventsFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyEvents = js("_module.wrapPointer(ptr, _module.b2BodyEvents)")

fun b2BodyEvents.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2BodyMoveEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyId: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var fellAsleep: Boolean
    /**
     * WebIDL type: [b2Transform] (Value)
     */
    var transform: b2Transform
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2BodyMoveEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyMoveEvent = js("new _module.b2BodyMoveEvent()")

fun b2BodyMoveEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyMoveEvent = js("_module.wrapPointer(ptr, _module.b2BodyMoveEvent)")

fun b2BodyMoveEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2SensorEvents {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var beginCount: Int
    /**
     * WebIDL type: [b2SensorBeginTouchEvent]
     */
    var beginEvents: b2SensorBeginTouchEvent
    /**
     * WebIDL type: long
     */
    var endCount: Int
    /**
     * WebIDL type: [b2SensorEndTouchEvent]
     */
    var endEvents: b2SensorEndTouchEvent
}

fun b2SensorEvents(_module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorEvents = js("new _module.b2SensorEvents()")

fun b2SensorEventsFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorEvents = js("_module.wrapPointer(ptr, _module.b2SensorEvents)")

fun b2SensorEvents.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2SensorBeginTouchEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var sensorShapeId: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var visitorShapeId: b2ShapeId
}

fun b2SensorBeginTouchEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorBeginTouchEvent = js("new _module.b2SensorBeginTouchEvent()")

fun b2SensorBeginTouchEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorBeginTouchEvent = js("_module.wrapPointer(ptr, _module.b2SensorBeginTouchEvent)")

fun b2SensorBeginTouchEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2SensorEndTouchEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var sensorShapeId: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var visitorShapeId: b2ShapeId
}

fun b2SensorEndTouchEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorEndTouchEvent = js("new _module.b2SensorEndTouchEvent()")

fun b2SensorEndTouchEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2SensorEndTouchEvent = js("_module.wrapPointer(ptr, _module.b2SensorEndTouchEvent)")

fun b2SensorEndTouchEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactEvents {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var beginCount: Int
    /**
     * WebIDL type: [b2ContactBeginTouchEvent]
     */
    var beginEvents: b2ContactBeginTouchEvent
    /**
     * WebIDL type: long
     */
    var endCount: Int
    /**
     * WebIDL type: [b2ContactEndTouchEvent]
     */
    var endEvents: b2ContactEndTouchEvent
    /**
     * WebIDL type: long
     */
    var hitCount: Int
    /**
     * WebIDL type: [b2ContactHitEvent]
     */
    var hitEvents: b2ContactHitEvent
}

fun b2ContactEvents(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactEvents = js("new _module.b2ContactEvents()")

fun b2ContactEventsFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactEvents = js("_module.wrapPointer(ptr, _module.b2ContactEvents)")

fun b2ContactEvents.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactBeginTouchEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Manifold] (Value)
     */
    var manifold: b2Manifold
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdA: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdB: b2ShapeId
}

fun b2ContactBeginTouchEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactBeginTouchEvent = js("new _module.b2ContactBeginTouchEvent()")

fun b2ContactBeginTouchEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactBeginTouchEvent = js("_module.wrapPointer(ptr, _module.b2ContactBeginTouchEvent)")

fun b2ContactBeginTouchEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactEndTouchEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdA: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdB: b2ShapeId
}

fun b2ContactEndTouchEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactEndTouchEvent = js("new _module.b2ContactEndTouchEvent()")

fun b2ContactEndTouchEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactEndTouchEvent = js("_module.wrapPointer(ptr, _module.b2ContactEndTouchEvent)")

fun b2ContactEndTouchEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactHitEvent {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var approachSpeed: Float
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var normal: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point: b2Vec2
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdA: b2ShapeId
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeIdB: b2ShapeId
}

fun b2ContactHitEvent(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactHitEvent = js("new _module.b2ContactHitEvent()")

fun b2ContactHitEventFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactHitEvent = js("_module.wrapPointer(ptr, _module.b2ContactHitEvent)")

fun b2ContactHitEvent.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Manifold {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var normal: b2Vec2
    /**
     * WebIDL type: long
     */
    var pointCount: Int
    /**
     * WebIDL type: [b2ManifoldPoint] (Value)
     */
    fun get_points(index: Int): b2ManifoldPoint
    fun set_points(index: Int, value: b2ManifoldPoint)
    /**
     * WebIDL type: float
     */
    var rollingImpulse: Float
}

fun b2Manifold(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Manifold = js("new _module.b2Manifold()")

fun b2ManifoldFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Manifold = js("_module.wrapPointer(ptr, _module.b2Manifold)")

fun b2Manifold.destroy() {
    Box2dWasmLoader.destroy(this)
}

inline fun b2Manifold.getPoints(index: Int) = get_points(index)
inline fun b2Manifold.setPoints(index: Int, value: b2ManifoldPoint) = set_points(index, value)

external interface b2ManifoldPoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var anchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var anchorB: b2Vec2
    /**
     * WebIDL type: unsigned short
     */
    var id: Short
    /**
     * WebIDL type: float
     */
    var normalImpulse: Float
    /**
     * WebIDL type: float
     */
    var normalVelocity: Float
    /**
     * WebIDL type: boolean
     */
    var persisted: Boolean
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point: b2Vec2
    /**
     * WebIDL type: float
     */
    var separation: Float
    /**
     * WebIDL type: float
     */
    var tangentImpulse: Float
    /**
     * WebIDL type: float
     */
    var totalNormalImpulse: Float
}

fun b2ManifoldPoint(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ManifoldPoint = js("new _module.b2ManifoldPoint()")

fun b2ManifoldPointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ManifoldPoint = js("_module.wrapPointer(ptr, _module.b2ManifoldPoint)")

fun b2ManifoldPoint.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2RayResult {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var fraction: Float
    /**
     * WebIDL type: boolean
     */
    var hit: Boolean
    /**
     * WebIDL type: long
     */
    var leafVisits: Int
    /**
     * WebIDL type: long
     */
    var nodeVisits: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var normal: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point: b2Vec2
    /**
     * WebIDL type: [b2ShapeId] (Value)
     */
    var shapeId: b2ShapeId
}

fun b2RayResult(_module: dynamic = Box2dWasmLoader.box2dWasm): b2RayResult = js("new _module.b2RayResult()")

fun b2RayResultFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2RayResult = js("_module.wrapPointer(ptr, _module.b2RayResult)")

fun b2RayResult.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2TreeStats {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var leafVisits: Int
    /**
     * WebIDL type: long
     */
    var nodeVisits: Int
}

fun b2TreeStats(_module: dynamic = Box2dWasmLoader.box2dWasm): b2TreeStats = js("new _module.b2TreeStats()")

fun b2TreeStatsFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2TreeStats = js("_module.wrapPointer(ptr, _module.b2TreeStats)")

fun b2TreeStats.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ExplosionDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var falloff: Float
    /**
     * WebIDL type: float
     */
    var impulsePerLength: Float
    /**
     * WebIDL type: unsigned long long
     */
    var maskBits: Long
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var position: b2Vec2
    /**
     * WebIDL type: float
     */
    var radius: Float
}

fun b2ExplosionDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ExplosionDef = js("new _module.b2ExplosionDef()")

fun b2ExplosionDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ExplosionDef = js("_module.wrapPointer(ptr, _module.b2ExplosionDef)")

fun b2ExplosionDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2WorldDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var gravity: b2Vec2
    /**
     * WebIDL type: float
     */
    var restitutionThreshold: Float
    /**
     * WebIDL type: float
     */
    var hitEventThreshold: Float
    /**
     * WebIDL type: float
     */
    var contactHertz: Float
    /**
     * WebIDL type: float
     */
    var contactDampingRatio: Float
    /**
     * WebIDL type: float
     */
    var maxContactPushSpeed: Float
    /**
     * WebIDL type: float
     */
    var maximumLinearSpeed: Float
    /**
     * WebIDL type: boolean
     */
    var enableSleep: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableContinuous: Boolean
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2WorldDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2WorldDef = js("new _module.b2WorldDef()")

fun b2WorldDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2WorldDef = js("_module.wrapPointer(ptr, _module.b2WorldDef)")

fun b2WorldDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2DebugDraw {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2AABB] (Value)
     */
    var drawingBounds: b2AABB
    /**
     * WebIDL type: boolean
     */
    var useDrawingBounds: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawShapes: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawJoints: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawJointExtras: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawBounds: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawMass: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawBodyNames: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawContacts: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawGraphColors: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawContactNormals: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawContactImpulses: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawContactFeatures: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawFrictionImpulses: Boolean
    /**
     * WebIDL type: boolean
     */
    var drawIslands: Boolean
}

fun b2DebugDraw(_module: dynamic = Box2dWasmLoader.box2dWasm): b2DebugDraw = js("new _module.b2DebugDraw()")

fun b2DebugDrawFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2DebugDraw = js("_module.wrapPointer(ptr, _module.b2DebugDraw)")

fun b2DebugDraw.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2DebugDrawCallbacks

fun b2DebugDrawCallbacksFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2DebugDrawCallbacks = js("_module.wrapPointer(ptr, _module.b2DebugDrawCallbacks)")

fun b2DebugDrawCallbacks.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CustomFilterFcnI

fun b2CustomFilterFcnIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CustomFilterFcnI = js("_module.wrapPointer(ptr, _module.b2CustomFilterFcnI)")

fun b2CustomFilterFcnI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2PreSolveFcnI

fun b2PreSolveFcnIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2PreSolveFcnI = js("_module.wrapPointer(ptr, _module.b2PreSolveFcnI)")

fun b2PreSolveFcnI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2FrictionCallbackI

fun b2FrictionCallbackIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2FrictionCallbackI = js("_module.wrapPointer(ptr, _module.b2FrictionCallbackI)")

fun b2FrictionCallbackI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2RestitutionCallbackI

fun b2RestitutionCallbackIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2RestitutionCallbackI = js("_module.wrapPointer(ptr, _module.b2RestitutionCallbackI)")

fun b2RestitutionCallbackI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2OverlapResultFcnI

fun b2OverlapResultFcnIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2OverlapResultFcnI = js("_module.wrapPointer(ptr, _module.b2OverlapResultFcnI)")

fun b2OverlapResultFcnI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CastResultFcnI

fun b2CastResultFcnIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CastResultFcnI = js("_module.wrapPointer(ptr, _module.b2CastResultFcnI)")

fun b2CastResultFcnI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2PlaneResultFcnI

fun b2PlaneResultFcnIFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneResultFcnI = js("_module.wrapPointer(ptr, _module.b2PlaneResultFcnI)")

fun b2PlaneResultFcnI.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2DebugDrawCallbacksImpl : b2DebugDrawCallbacks {
    /**
     * param vertices    WebIDL type: [b2Vec2] (Const)
     * param vertexCount WebIDL type: long
     * param color       WebIDL type: long
     */
    var drawPolygon: (vertices: Int, vertexCount: Int, color: Int) -> Unit

    /**
     * param transform   WebIDL type: [b2Transform] (Ref)
     * param vertices    WebIDL type: [b2Vec2] (Const)
     * param vertexCount WebIDL type: long
     * param radius      WebIDL type: float
     * param color       WebIDL type: long
     */
    var drawSolidPolygon: (transform: Int, vertices: Int, vertexCount: Int, radius: Float, color: Int) -> Unit

    /**
     * param center WebIDL type: [b2Vec2] (Ref)
     * param radius WebIDL type: float
     * param color  WebIDL type: long
     */
    var drawCircle: (center: Int, radius: Float, color: Int) -> Unit

    /**
     * param transform WebIDL type: [b2Transform] (Ref)
     * param radius    WebIDL type: float
     * param color     WebIDL type: long
     */
    var drawSolidCircle: (transform: Int, radius: Float, color: Int) -> Unit

    /**
     * param p1     WebIDL type: [b2Vec2] (Ref)
     * param p2     WebIDL type: [b2Vec2] (Ref)
     * param radius WebIDL type: float
     * param color  WebIDL type: long
     */
    var drawSolidCapsule: (p1: Int, p2: Int, radius: Float, color: Int) -> Unit

    /**
     * param p1    WebIDL type: [b2Vec2] (Ref)
     * param p2    WebIDL type: [b2Vec2] (Ref)
     * param color WebIDL type: long
     */
    var drawSegment: (p1: Int, p2: Int, color: Int) -> Unit

    /**
     * param transform WebIDL type: [b2Transform] (Ref)
     */
    var drawTransform: (transform: Int) -> Unit

    /**
     * param p     WebIDL type: [b2Vec2] (Ref)
     * param size  WebIDL type: float
     * param color WebIDL type: long
     */
    var drawPoint: (p: Int, size: Float, color: Int) -> Unit

    /**
     * param p     WebIDL type: [b2Vec2] (Ref)
     * param s     WebIDL type: DOMString (Const)
     * param color WebIDL type: long
     */
    var drawString: (p: Int, s: String, color: Int) -> Unit

}

fun b2DebugDrawCallbacksImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2DebugDrawCallbacksImpl = js("new _module.b2DebugDrawCallbacksImpl()")

external interface b2CustomFilterFcnImpl : b2CustomFilterFcnI {
    /**
     * param shapeIdA WebIDL type: unsigned long long
     * param shapeIdB WebIDL type: unsigned long long
     * return WebIDL type: boolean
     */
    var customFilterFcn: (shapeIdA: Long, shapeIdB: Long) -> Boolean

}

fun b2CustomFilterFcnImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2CustomFilterFcnImpl = js("new _module.b2CustomFilterFcnImpl()")

external interface b2PreSolveFcnImpl : b2PreSolveFcnI {
    /**
     * param shapeIdA WebIDL type: unsigned long long
     * param shapeIdB WebIDL type: unsigned long long
     * param manifold WebIDL type: [b2Manifold]
     * return WebIDL type: boolean
     */
    var preSolveFcn: (shapeIdA: Long, shapeIdB: Long, manifold: Int) -> Boolean

}

fun b2PreSolveFcnImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2PreSolveFcnImpl = js("new _module.b2PreSolveFcnImpl()")

external interface b2FrictionCallbackImpl : b2FrictionCallbackI {
    /**
     * param frictionA       WebIDL type: float
     * param userMaterialIdA WebIDL type: long
     * param frictionB       WebIDL type: float
     * param userMaterialIdB WebIDL type: long
     * return WebIDL type: float
     */
    var frictionCallback: (frictionA: Float, userMaterialIdA: Int, frictionB: Float, userMaterialIdB: Int) -> Float

}

fun b2FrictionCallbackImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2FrictionCallbackImpl = js("new _module.b2FrictionCallbackImpl()")

external interface b2RestitutionCallbackImpl : b2RestitutionCallbackI {
    /**
     * param restitutionA    WebIDL type: float
     * param userMaterialIdA WebIDL type: long
     * param restitutionB    WebIDL type: float
     * param userMaterialIdB WebIDL type: long
     * return WebIDL type: float
     */
    var restitutionCallback: (restitutionA: Float, userMaterialIdA: Int, restitutionB: Float, userMaterialIdB: Int) -> Float

}

fun b2RestitutionCallbackImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2RestitutionCallbackImpl = js("new _module.b2RestitutionCallbackImpl()")

external interface b2OverlapResultFcnImpl : b2OverlapResultFcnI {
    /**
     * param shapeId WebIDL type: unsigned long long
     * return WebIDL type: boolean
     */
    var overlapResultFcn: (shapeId: Long) -> Boolean

}

fun b2OverlapResultFcnImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2OverlapResultFcnImpl = js("new _module.b2OverlapResultFcnImpl()")

external interface b2CastResultFcnImpl : b2CastResultFcnI {
    /**
     * param shapeId  WebIDL type: unsigned long long
     * param point    WebIDL type: [b2Vec2] (Ref)
     * param normal   WebIDL type: [b2Vec2] (Ref)
     * param fraction WebIDL type: float
     * return WebIDL type: boolean
     */
    var castResultFcn: (shapeId: Long, point: Int, normal: Int, fraction: Float) -> Boolean

}

fun b2CastResultFcnImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2CastResultFcnImpl = js("new _module.b2CastResultFcnImpl()")

external interface b2PlaneResultFcnImpl : b2PlaneResultFcnI {
    /**
     * param shapeId WebIDL type: unsigned long long
     * param plane   WebIDL type: [b2PlaneResult] (Const)
     * return WebIDL type: boolean
     */
    var planeResultFcn: (shapeId: Long, plane: Int) -> Boolean

}

fun b2PlaneResultFcnImpl(_module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneResultFcnImpl = js("new _module.b2PlaneResultFcnImpl()")

external interface b2Counters {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var bodyCount: Int
    /**
     * WebIDL type: long
     */
    var shapeCount: Int
    /**
     * WebIDL type: long
     */
    var contactCount: Int
    /**
     * WebIDL type: long
     */
    var jointCount: Int
    /**
     * WebIDL type: long
     */
    var islandCount: Int
    /**
     * WebIDL type: long
     */
    var stackUsed: Int
    /**
     * WebIDL type: long
     */
    var staticTreeHeight: Int
    /**
     * WebIDL type: long
     */
    var treeHeight: Int
    /**
     * WebIDL type: long
     */
    var byteCount: Int
    /**
     * WebIDL type: long
     */
    var taskCount: Int
    /**
     * WebIDL type: long
     */
    fun get_colorCounts(index: Int): Int
    fun set_colorCounts(index: Int, value: Int)
}

fun b2Counters(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Counters = js("new _module.b2Counters()")

fun b2CountersFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Counters = js("_module.wrapPointer(ptr, _module.b2Counters)")

fun b2Counters.destroy() {
    Box2dWasmLoader.destroy(this)
}

inline fun b2Counters.getColorCounts(index: Int) = get_colorCounts(index)
inline fun b2Counters.setColorCounts(index: Int, value: Int) = set_colorCounts(index, value)

external interface b2Profile {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var step: Float
    /**
     * WebIDL type: float
     */
    var pairs: Float
    /**
     * WebIDL type: float
     */
    var collide: Float
    /**
     * WebIDL type: float
     */
    var solve: Float
    /**
     * WebIDL type: float
     */
    var mergeIslands: Float
    /**
     * WebIDL type: float
     */
    var prepareStages: Float
    /**
     * WebIDL type: float
     */
    var solveConstraints: Float
    /**
     * WebIDL type: float
     */
    var prepareConstraints: Float
    /**
     * WebIDL type: float
     */
    var integrateVelocities: Float
    /**
     * WebIDL type: float
     */
    var warmStart: Float
    /**
     * WebIDL type: float
     */
    var solveImpulses: Float
    /**
     * WebIDL type: float
     */
    var integratePositions: Float
    /**
     * WebIDL type: float
     */
    var relaxImpulses: Float
    /**
     * WebIDL type: float
     */
    var applyRestitution: Float
    /**
     * WebIDL type: float
     */
    var storeImpulses: Float
    /**
     * WebIDL type: float
     */
    var splitIslands: Float
    /**
     * WebIDL type: float
     */
    var transforms: Float
    /**
     * WebIDL type: float
     */
    var hitEvents: Float
    /**
     * WebIDL type: float
     */
    var refit: Float
    /**
     * WebIDL type: float
     */
    var bullets: Float
    /**
     * WebIDL type: float
     */
    var sleepIslands: Float
    /**
     * WebIDL type: float
     */
    var sensors: Float
}

fun b2Profile(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Profile = js("new _module.b2Profile()")

fun b2ProfileFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Profile = js("_module.wrapPointer(ptr, _module.b2Profile)")

fun b2Profile.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_Base {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @return WebIDL type: [b2Version] (Value)
     */
    fun getVersion(): b2Version

    /**
     * @return WebIDL type: long
     */
    fun getByteCount(): Int

    /**
     * @param worldId WebIDL type: [b2WorldId] (Ref)
     * @return WebIDL type: unsigned long long
     */
    fun storeWorldId(worldId: b2WorldId): Long

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param result  WebIDL type: [b2WorldId] (Ref)
     */
    fun loadWorldId(worldId: Long, result: b2WorldId)

    /**
     * @param bodyId WebIDL type: [b2BodyId] (Ref)
     * @return WebIDL type: unsigned long long
     */
    fun storeBodyId(bodyId: b2BodyId): Long

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param result WebIDL type: [b2BodyId] (Ref)
     */
    fun loadBodyId(bodyId: Long, result: b2BodyId)

    /**
     * @param shapeId WebIDL type: [b2ShapeId] (Ref)
     * @return WebIDL type: unsigned long long
     */
    fun storeShapeId(shapeId: b2ShapeId): Long

    /**
     * @param shapeId WebIDL type: unsigned long long
     * @param result  WebIDL type: [b2ShapeId] (Ref)
     */
    fun loadShapeId(shapeId: Long, result: b2ShapeId)

    /**
     * @param chainId WebIDL type: [b2ChainId] (Ref)
     * @return WebIDL type: unsigned long long
     */
    fun storeChainId(chainId: b2ChainId): Long

    /**
     * @param chainId WebIDL type: unsigned long long
     * @param result  WebIDL type: [b2ChainId] (Ref)
     */
    fun loadChainId(chainId: Long, result: b2ChainId)

    /**
     * @param jointId WebIDL type: [b2JointId] (Ref)
     * @return WebIDL type: unsigned long long
     */
    fun storeJointId(jointId: b2JointId): Long

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param result  WebIDL type: [b2JointId] (Ref)
     */
    fun loadJointId(jointId: Long, result: b2JointId)

}

fun B2_BaseFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Base = js("_module.wrapPointer(ptr, _module.B2_Base)")

val B2_Base.version
    get() = getVersion()
val B2_Base.byteCount
    get() = getByteCount()

external interface b2Version {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var major: Int
    /**
     * WebIDL type: long
     */
    var minor: Int
    /**
     * WebIDL type: long
     */
    var revision: Int
}

fun b2VersionFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Version = js("_module.wrapPointer(ptr, _module.b2Version)")

fun b2Version.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2WorldId {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var generation: Short
    /**
     * WebIDL type: unsigned short
     */
    var index1: Short
}

fun b2WorldId(_module: dynamic = Box2dWasmLoader.box2dWasm): b2WorldId = js("new _module.b2WorldId()")

fun b2WorldIdFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2WorldId = js("_module.wrapPointer(ptr, _module.b2WorldId)")

fun b2WorldId.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2BodyId {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var generation: Short
    /**
     * WebIDL type: long
     */
    var index1: Int
    /**
     * WebIDL type: unsigned short
     */
    var world0: Short
}

fun b2BodyId(_module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyId = js("new _module.b2BodyId()")

fun b2BodyIdFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyId = js("_module.wrapPointer(ptr, _module.b2BodyId)")

fun b2BodyId.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ShapeId {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var generation: Short
    /**
     * WebIDL type: long
     */
    var index1: Int
    /**
     * WebIDL type: unsigned short
     */
    var world0: Short
}

fun b2ShapeId(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeId = js("new _module.b2ShapeId()")

fun b2ShapeIdFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeId = js("_module.wrapPointer(ptr, _module.b2ShapeId)")

fun b2ShapeId.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ChainId {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var generation: Short
    /**
     * WebIDL type: long
     */
    var index1: Int
    /**
     * WebIDL type: unsigned short
     */
    var world0: Short
}

fun b2ChainId(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainId = js("new _module.b2ChainId()")

fun b2ChainIdFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainId = js("_module.wrapPointer(ptr, _module.b2ChainId)")

fun b2ChainId.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2JointId {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned short
     */
    var generation: Short
    /**
     * WebIDL type: long
     */
    var index1: Int
    /**
     * WebIDL type: unsigned short
     */
    var world0: Short
}

fun b2JointId(_module: dynamic = Box2dWasmLoader.box2dWasm): b2JointId = js("new _module.b2JointId()")

fun b2JointIdFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2JointId = js("_module.wrapPointer(ptr, _module.b2JointId)")

fun b2JointId.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ShapeIdArray {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var length: Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [b2ShapeId] (Ref)
     */
    fun set(index: Int, value: b2ShapeId)

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [b2ShapeId]
     */
    fun get(index: Int): b2ShapeId

}

/**
 * @param length WebIDL type: unsigned long
 */
fun b2ShapeIdArray(length: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeIdArray = js("new _module.b2ShapeIdArray(length)")

fun b2ShapeIdArrayFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeIdArray = js("_module.wrapPointer(ptr, _module.b2ShapeIdArray)")

fun b2ShapeIdArray.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2JointIdArray {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var length: Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [b2JointId] (Ref)
     */
    fun set(index: Int, value: b2JointId)

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [b2JointId]
     */
    fun get(index: Int): b2JointId

}

/**
 * @param length WebIDL type: unsigned long
 */
fun b2JointIdArray(length: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2JointIdArray = js("new _module.b2JointIdArray(length)")

fun b2JointIdArrayFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2JointIdArray = js("_module.wrapPointer(ptr, _module.b2JointIdArray)")

fun b2JointIdArray.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ContactDataArray {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var length: Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [b2ContactData] (Ref)
     */
    fun set(index: Int, value: b2ContactData)

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [b2ContactData]
     */
    fun get(index: Int): b2ContactData

}

/**
 * @param length WebIDL type: unsigned long
 */
fun b2ContactDataArray(length: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactDataArray = js("new _module.b2ContactDataArray(length)")

fun b2ContactDataArrayFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ContactDataArray = js("_module.wrapPointer(ptr, _module.b2ContactDataArray)")

fun b2ContactDataArray.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CollisionPlaneArray {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var length: Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [b2CollisionPlane] (Ref)
     */
    fun set(index: Int, value: b2CollisionPlane)

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [b2CollisionPlane]
     */
    fun get(index: Int): b2CollisionPlane

}

/**
 * @param length WebIDL type: unsigned long
 */
fun b2CollisionPlaneArray(length: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CollisionPlaneArray = js("new _module.b2CollisionPlaneArray(length)")

fun b2CollisionPlaneArrayFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CollisionPlaneArray = js("_module.wrapPointer(ptr, _module.b2CollisionPlaneArray)")

fun b2CollisionPlaneArray.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Vec2Array {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var length: Int

    /**
     * @param index WebIDL type: unsigned long
     * @param value WebIDL type: [b2Vec2] (Ref)
     */
    fun set(index: Int, value: b2Vec2)

    /**
     * @param index WebIDL type: unsigned long
     * @return WebIDL type: [b2Vec2]
     */
    fun get(index: Int): b2Vec2

}

/**
 * @param length WebIDL type: unsigned long
 */
fun b2Vec2Array(length: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Vec2Array = js("new _module.b2Vec2Array(length)")

fun b2Vec2ArrayFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Vec2Array = js("_module.wrapPointer(ptr, _module.b2Vec2Array)")

fun b2Vec2Array.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_Body {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param result WebIDL type: [b2BodyDef] (Ref)
     */
    fun defaultBodyDef(result: b2BodyDef)

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2BodyDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createBody(worldId: Long, def: b2BodyDef): Long

    /**
     * @param bodyId WebIDL type: unsigned long long
     */
    fun destroyBody(bodyId: Long)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isValid(bodyId: Long): Boolean

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2BodyType] (enum)
     */
    fun getType(bodyId: Long): b2BodyType

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param type   WebIDL type: [b2BodyType] (enum)
     */
    fun setType(bodyId: Long, type: b2BodyType)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getPosition(bodyId: Long): b2Vec2

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Rot] (Value)
     */
    fun getRotation(bodyId: Long): b2Rot

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Transform] (Value)
     */
    fun getTransform(bodyId: Long): b2Transform

    /**
     * @param bodyId   WebIDL type: unsigned long long
     * @param position WebIDL type: [b2Vec2] (Ref)
     * @param rotation WebIDL type: [b2Rot] (Ref)
     */
    fun setTransform(bodyId: Long, position: b2Vec2, rotation: b2Rot)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun computeAABB(bodyId: Long): b2AABB

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param worldPoint WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalPoint(bodyId: Long, worldPoint: b2Vec2): b2Vec2

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param localPoint WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getWorldPoint(bodyId: Long, localPoint: b2Vec2): b2Vec2

    /**
     * @param bodyId      WebIDL type: unsigned long long
     * @param worldVector WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalVector(bodyId: Long, worldVector: b2Vec2): b2Vec2

    /**
     * @param bodyId      WebIDL type: unsigned long long
     * @param localVector WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getWorldVector(bodyId: Long, localVector: b2Vec2): b2Vec2

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLinearVelocity(bodyId: Long): b2Vec2

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularVelocity(bodyId: Long): Float

    /**
     * @param bodyId         WebIDL type: unsigned long long
     * @param linearVelocity WebIDL type: [b2Vec2] (Ref)
     */
    fun setLinearVelocity(bodyId: Long, linearVelocity: b2Vec2)

    /**
     * @param bodyId          WebIDL type: unsigned long long
     * @param angularVelocity WebIDL type: float
     */
    fun setAngularVelocity(bodyId: Long, angularVelocity: Float)

    /**
     * @param bodyId   WebIDL type: unsigned long long
     * @param target   WebIDL type: [b2Transform] (Ref)
     * @param timeStep WebIDL type: float
     */
    fun setTargetTransform(bodyId: Long, target: b2Transform, timeStep: Float)

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param localPoint WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalPointVelocity(bodyId: Long, localPoint: b2Vec2): b2Vec2

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param worldPoint WebIDL type: [b2Vec2] (Ref)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getWorldPointVelocity(bodyId: Long, worldPoint: b2Vec2): b2Vec2

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param force  WebIDL type: [b2Vec2] (Ref)
     * @param point  WebIDL type: [b2Vec2] (Ref)
     * @param wake   WebIDL type: boolean
     */
    fun applyForce(bodyId: Long, force: b2Vec2, point: b2Vec2, wake: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param force  WebIDL type: [b2Vec2] (Ref)
     * @param wake   WebIDL type: boolean
     */
    fun applyForceToCenter(bodyId: Long, force: b2Vec2, wake: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param torque WebIDL type: float
     * @param wake   WebIDL type: boolean
     */
    fun applyTorque(bodyId: Long, torque: Float, wake: Boolean)

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param impulse WebIDL type: [b2Vec2] (Ref)
     * @param point   WebIDL type: [b2Vec2] (Ref)
     * @param wake    WebIDL type: boolean
     */
    fun applyLinearImpulse(bodyId: Long, impulse: b2Vec2, point: b2Vec2, wake: Boolean)

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param impulse WebIDL type: [b2Vec2] (Ref)
     * @param wake    WebIDL type: boolean
     */
    fun applyLinearImpulseToCenter(bodyId: Long, impulse: b2Vec2, wake: Boolean)

    /**
     * @param bodyId  WebIDL type: unsigned long long
     * @param impulse WebIDL type: float
     * @param wake    WebIDL type: boolean
     */
    fun applyAngularImpulse(bodyId: Long, impulse: Float, wake: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMass(bodyId: Long): Float

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getRotationalInertia(bodyId: Long): Float

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalCenterOfMass(bodyId: Long): b2Vec2

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getWorldCenterOfMass(bodyId: Long): b2Vec2

    /**
     * @param bodyId   WebIDL type: unsigned long long
     * @param massData WebIDL type: [b2MassData] (Ref)
     */
    fun setMassData(bodyId: Long, massData: b2MassData)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2MassData] (Value)
     */
    fun getMassData(bodyId: Long): b2MassData

    /**
     * @param bodyId WebIDL type: unsigned long long
     */
    fun applyMassFromShapes(bodyId: Long)

    /**
     * @param bodyId        WebIDL type: unsigned long long
     * @param linearDamping WebIDL type: float
     */
    fun setLinearDamping(bodyId: Long, linearDamping: Float)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLinearDamping(bodyId: Long): Float

    /**
     * @param bodyId         WebIDL type: unsigned long long
     * @param angularDamping WebIDL type: float
     */
    fun setAngularDamping(bodyId: Long, angularDamping: Float)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularDamping(bodyId: Long): Float

    /**
     * @param bodyId       WebIDL type: unsigned long long
     * @param gravityScale WebIDL type: float
     */
    fun setGravityScale(bodyId: Long, gravityScale: Float)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getGravityScale(bodyId: Long): Float

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isAwake(bodyId: Long): Boolean

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param awake  WebIDL type: boolean
     */
    fun setAwake(bodyId: Long, awake: Boolean)

    /**
     * @param bodyId      WebIDL type: unsigned long long
     * @param enableSleep WebIDL type: boolean
     */
    fun enableSleep(bodyId: Long, enableSleep: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSleepEnabled(bodyId: Long): Boolean

    /**
     * @param bodyId         WebIDL type: unsigned long long
     * @param sleepThreshold WebIDL type: float
     */
    fun setSleepThreshold(bodyId: Long, sleepThreshold: Float)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSleepThreshold(bodyId: Long): Float

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isEnabled(bodyId: Long): Boolean

    /**
     * @param bodyId WebIDL type: unsigned long long
     */
    fun disable(bodyId: Long)

    /**
     * @param bodyId WebIDL type: unsigned long long
     */
    fun enable(bodyId: Long)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param flag   WebIDL type: boolean
     */
    fun setFixedRotation(bodyId: Long, flag: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isFixedRotation(bodyId: Long): Boolean

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param flag   WebIDL type: boolean
     */
    fun setBullet(bodyId: Long, flag: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isBullet(bodyId: Long): Boolean

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: [b2WorldId] (Value)
     */
    fun getWorld(bodyId: Long): b2WorldId

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getShapeCount(bodyId: Long): Int

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param shapeArray WebIDL type: [b2ShapeIdArray]
     * @return WebIDL type: long
     */
    fun getShapes(bodyId: Long, shapeArray: b2ShapeIdArray): Int

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getJointCount(bodyId: Long): Int

    /**
     * @param bodyId     WebIDL type: unsigned long long
     * @param jointArray WebIDL type: [b2JointIdArray]
     * @return WebIDL type: long
     */
    fun getJoints(bodyId: Long, jointArray: b2JointIdArray): Int

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param flag   WebIDL type: boolean
     */
    fun enableContactEvents(bodyId: Long, flag: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param flag   WebIDL type: boolean
     */
    fun enableHitEvents(bodyId: Long, flag: Boolean)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: long
     */
    fun getContactCapacity(bodyId: Long): Int

    /**
     * @param bodyId      WebIDL type: unsigned long long
     * @param contactData WebIDL type: [b2ContactDataArray]
     * @return WebIDL type: long
     */
    fun getContactData(bodyId: Long, contactData: b2ContactDataArray): Int

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @param name   WebIDL type: DOMString
     */
    fun setName(bodyId: Long, name: String)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: DOMString (Const)
     */
    fun getName(bodyId: Long): String

    /**
     * @param bodyId   WebIDL type: unsigned long long
     * @param userData WebIDL type: VoidPtr
     */
    fun setUserData(bodyId: Long, userData: Any)

    /**
     * @param bodyId WebIDL type: unsigned long long
     * @return WebIDL type: VoidPtr
     */
    fun getUserData(bodyId: Long): Any

}

fun B2_BodyFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Body = js("_module.wrapPointer(ptr, _module.B2_Body)")

external interface b2BodyDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyType] (enum)
     */
    var type: b2BodyType
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var position: b2Vec2
    /**
     * WebIDL type: [b2Rot] (Value)
     */
    var rotation: b2Rot
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var linearVelocity: b2Vec2
    /**
     * WebIDL type: float
     */
    var angularVelocity: Float
    /**
     * WebIDL type: float
     */
    var linearDamping: Float
    /**
     * WebIDL type: float
     */
    var angularDamping: Float
    /**
     * WebIDL type: float
     */
    var gravityScale: Float
    /**
     * WebIDL type: float
     */
    var sleepThreshold: Float
    /**
     * WebIDL type: DOMString (Const)
     */
    var name: String
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
    /**
     * WebIDL type: boolean
     */
    var enableSleep: Boolean
    /**
     * WebIDL type: boolean
     */
    var isAwake: Boolean
    /**
     * WebIDL type: boolean
     */
    var fixedRotation: Boolean
    /**
     * WebIDL type: boolean
     */
    var isBullet: Boolean
    /**
     * WebIDL type: boolean
     */
    var isEnabled: Boolean
    /**
     * WebIDL type: boolean
     */
    var allowFastRotation: Boolean
}

fun b2BodyDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyDef = js("new _module.b2BodyDef()")

fun b2BodyDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2BodyDef = js("_module.wrapPointer(ptr, _module.b2BodyDef)")

fun b2BodyDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_CharacterMover {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param targetDelta WebIDL type: [b2Vec2] (Ref)
     * @param planes      WebIDL type: [b2CollisionPlaneArray]
     * @return WebIDL type: [b2PlaneSolverResult] (Value)
     */
    fun solvePlanes(targetDelta: b2Vec2, planes: b2CollisionPlaneArray): b2PlaneSolverResult

    /**
     * @param vector WebIDL type: [b2Vec2] (Ref)
     * @param planes WebIDL type: [b2CollisionPlaneArray] (Const)
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun clipVector(vector: b2Vec2, planes: b2CollisionPlaneArray): b2Vec2

}

fun B2_CharacterMoverFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_CharacterMover = js("_module.wrapPointer(ptr, _module.B2_CharacterMover)")

external interface b2PlaneResult {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var hit: Boolean
    /**
     * WebIDL type: [b2Plane] (Value)
     */
    var plane: b2Plane
}

fun b2PlaneResult(_module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneResult = js("new _module.b2PlaneResult()")

fun b2PlaneResultFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneResult = js("_module.wrapPointer(ptr, _module.b2PlaneResult)")

fun b2PlaneResult.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CollisionPlane {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var clipVelocity: Boolean
    /**
     * WebIDL type: [b2Plane] (Value)
     */
    var plane: b2Plane
    /**
     * WebIDL type: float
     */
    var push: Float
    /**
     * WebIDL type: float
     */
    var pushLimit: Float
}

fun b2CollisionPlane(_module: dynamic = Box2dWasmLoader.box2dWasm): b2CollisionPlane = js("new _module.b2CollisionPlane()")

fun b2CollisionPlaneFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CollisionPlane = js("_module.wrapPointer(ptr, _module.b2CollisionPlane)")

fun b2CollisionPlane.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2PlaneSolverResult {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var iterationCount: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var translation: b2Vec2
}

fun b2PlaneSolverResult(_module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneSolverResult = js("new _module.b2PlaneSolverResult()")

fun b2PlaneSolverResultFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2PlaneSolverResult = js("_module.wrapPointer(ptr, _module.b2PlaneSolverResult)")

fun b2PlaneSolverResult.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_Joint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param jointId WebIDL type: unsigned long long
     */
    fun destroyJoint(jointId: Long)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isValid(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2JointType] (enum)
     */
    fun getType(jointId: Long): b2JointType

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: unsigned long long
     */
    fun getBodyA(jointId: Long): Long

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: unsigned long long
     */
    fun getBodyB(jointId: Long): Long

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: unsigned long
     */
    fun getWorld(jointId: Long): Int

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalAnchorA(jointId: Long): b2Vec2

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalAnchorB(jointId: Long): b2Vec2

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param localAnchor WebIDL type: [b2Vec2] (Ref)
     */
    fun setLocalAnchorA(jointId: Long, localAnchor: b2Vec2)

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param localAnchor WebIDL type: [b2Vec2] (Ref)
     */
    fun setLocalAnchorB(jointId: Long, localAnchor: b2Vec2)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getReferenceAngle(jointId: Long): Float

    /**
     * @param jointId        WebIDL type: unsigned long long
     * @param angleInRadians WebIDL type: float
     */
    fun setReferenceAngle(jointId: Long, angleInRadians: Float)

    /**
     * @param jointId   WebIDL type: unsigned long long
     * @param localAxis WebIDL type: [b2Vec2] (Ref)
     */
    fun setLocalAxisA(jointId: Long, localAxis: b2Vec2)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLocalAxisA(jointId: Long): b2Vec2

    /**
     * @param jointId       WebIDL type: unsigned long long
     * @param shouldCollide WebIDL type: boolean
     */
    fun setCollideConnected(jointId: Long, shouldCollide: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun getCollideConnected(jointId: Long): Boolean

    /**
     * @param jointId  WebIDL type: unsigned long long
     * @param userData WebIDL type: any
     */
    fun setUserData(jointId: Long, userData: Int)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: any
     */
    fun getUserData(jointId: Long): Int

    /**
     * @param jointId WebIDL type: unsigned long long
     */
    fun wakeBodies(jointId: Long)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getConstraintForce(jointId: Long): b2Vec2

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getConstraintTorque(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLinearSeparation(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularSeparation(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getConstraintTuningHertz(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getConstraintTuningDampingRatio(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param hertz        WebIDL type: float
     * @param dampingRatio WebIDL type: float
     */
    fun setConstraintTuning(jointId: Long, hertz: Float, dampingRatio: Float)

}

fun B2_JointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Joint = js("_module.wrapPointer(ptr, _module.B2_Joint)")

external interface B2_DistanceJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2DistanceJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createDistanceJoint(worldId: Long, def: b2DistanceJointDef): Long

    /**
     * @param result WebIDL type: [b2DistanceJointDef] (Ref)
     */
    fun defaultDistanceJointDef(result: b2DistanceJointDef)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param length  WebIDL type: float
     */
    fun setLength(jointId: Long, length: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLength(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param enableSpring WebIDL type: boolean
     */
    fun enableSpring(jointId: Long, enableSpring: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSpringEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setSpringHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setSpringDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringHertz(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringDampingRatio(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableLimit WebIDL type: boolean
     */
    fun enableLimit(jointId: Long, enableLimit: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isLimitEnabled(jointId: Long): Boolean

    /**
     * @param jointId   WebIDL type: unsigned long long
     * @param minLength WebIDL type: float
     * @param maxLength WebIDL type: float
     */
    fun setLengthRange(jointId: Long, minLength: Float, maxLength: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMinLength(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxLength(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getCurrentLength(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableMotor WebIDL type: boolean
     */
    fun enableMotor(jointId: Long, enableMotor: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isMotorEnabled(jointId: Long): Boolean

    /**
     * @param jointId    WebIDL type: unsigned long long
     * @param motorSpeed WebIDL type: float
     */
    fun setMotorSpeed(jointId: Long, motorSpeed: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorSpeed(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param force   WebIDL type: float
     */
    fun setMaxMotorForce(jointId: Long, force: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxMotorForce(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorForce(jointId: Long): Float

}

fun B2_DistanceJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_DistanceJoint = js("_module.wrapPointer(ptr, _module.B2_DistanceJoint)")

external interface b2DistanceJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var dampingRatio: Float
    /**
     * WebIDL type: boolean
     */
    var enableLimit: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableMotor: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSpring: Boolean
    /**
     * WebIDL type: float
     */
    var hertz: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: float
     */
    var length: Float
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorB: b2Vec2
    /**
     * WebIDL type: float
     */
    var maxLength: Float
    /**
     * WebIDL type: float
     */
    var maxMotorForce: Float
    /**
     * WebIDL type: float
     */
    var minLength: Float
    /**
     * WebIDL type: float
     */
    var motorSpeed: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2DistanceJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2DistanceJointDef = js("new _module.b2DistanceJointDef()")

fun b2DistanceJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2DistanceJointDef = js("_module.wrapPointer(ptr, _module.b2DistanceJointDef)")

fun b2DistanceJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_FilterJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2FilterJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createFilterJoint(worldId: Long, def: b2FilterJointDef): Long

    /**
     * @param result WebIDL type: [b2FilterJointDef] (Ref)
     */
    fun defaultFilterJointDef(result: b2FilterJointDef)

}

fun B2_FilterJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_FilterJoint = js("_module.wrapPointer(ptr, _module.B2_FilterJoint)")

external interface b2FilterJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2FilterJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2FilterJointDef = js("new _module.b2FilterJointDef()")

fun b2FilterJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2FilterJointDef = js("_module.wrapPointer(ptr, _module.b2FilterJointDef)")

fun b2FilterJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_MotorJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2MotorJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createMotorJoint(worldId: Long, def: b2MotorJointDef): Long

    /**
     * @param result WebIDL type: [b2MotorJointDef] (Ref)
     */
    fun defaultMotorJointDef(result: b2MotorJointDef)

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param linearOffset WebIDL type: [b2Vec2] (Ref)
     */
    fun setLinearOffset(jointId: Long, linearOffset: b2Vec2)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getLinearOffset(jointId: Long): b2Vec2

    /**
     * @param jointId       WebIDL type: unsigned long long
     * @param angularOffset WebIDL type: float
     */
    fun setAngularOffset(jointId: Long, angularOffset: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularOffset(jointId: Long): Float

    /**
     * @param jointId  WebIDL type: unsigned long long
     * @param maxForce WebIDL type: float
     */
    fun setMaxForce(jointId: Long, maxForce: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxForce(jointId: Long): Float

    /**
     * @param jointId   WebIDL type: unsigned long long
     * @param maxTorque WebIDL type: float
     */
    fun setMaxTorque(jointId: Long, maxTorque: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxTorque(jointId: Long): Float

    /**
     * @param jointId          WebIDL type: unsigned long long
     * @param correctionFactor WebIDL type: float
     */
    fun setCorrectionFactor(jointId: Long, correctionFactor: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getCorrectionFactor(jointId: Long): Float

}

fun B2_MotorJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_MotorJoint = js("_module.wrapPointer(ptr, _module.B2_MotorJoint)")

external interface b2MotorJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var angularOffset: Float
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var correctionFactor: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var linearOffset: b2Vec2
    /**
     * WebIDL type: float
     */
    var maxForce: Float
    /**
     * WebIDL type: float
     */
    var maxTorque: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2MotorJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2MotorJointDef = js("new _module.b2MotorJointDef()")

fun b2MotorJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2MotorJointDef = js("_module.wrapPointer(ptr, _module.b2MotorJointDef)")

fun b2MotorJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_MouseJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2MouseJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createMouseJoint(worldId: Long, def: b2MouseJointDef): Long

    /**
     * @param result WebIDL type: [b2MouseJointDef] (Ref)
     */
    fun defaultMouseJointDef(result: b2MouseJointDef)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param target  WebIDL type: [b2Vec2] (Ref)
     */
    fun setTarget(jointId: Long, target: b2Vec2)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: [b2Vec2] (Value)
     */
    fun getTarget(jointId: Long): b2Vec2

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setSpringHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setSpringDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringDampingRatio(jointId: Long): Float

    /**
     * @param jointId  WebIDL type: unsigned long long
     * @param maxForce WebIDL type: float
     */
    fun setMaxForce(jointId: Long, maxForce: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxForce(jointId: Long): Float

}

fun B2_MouseJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_MouseJoint = js("_module.wrapPointer(ptr, _module.B2_MouseJoint)")

external interface b2MouseJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var dampingRatio: Float
    /**
     * WebIDL type: float
     */
    var hertz: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: float
     */
    var maxForce: Float
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var target: b2Vec2
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2MouseJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2MouseJointDef = js("new _module.b2MouseJointDef()")

fun b2MouseJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2MouseJointDef = js("_module.wrapPointer(ptr, _module.b2MouseJointDef)")

fun b2MouseJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_PrismaticJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2PrismaticJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createPrismaticJoint(worldId: Long, def: b2PrismaticJointDef): Long

    /**
     * @param result WebIDL type: [b2PrismaticJointDef] (Ref)
     */
    fun defaultPrismaticJointDef(result: b2PrismaticJointDef)

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param enableSpring WebIDL type: boolean
     */
    fun enableSpring(jointId: Long, enableSpring: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSpringEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setSpringHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setSpringDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringDampingRatio(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param translation WebIDL type: float
     */
    fun setTargetTranslation(jointId: Long, translation: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getTargetTranslation(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableLimit WebIDL type: boolean
     */
    fun enableLimit(jointId: Long, enableLimit: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isLimitEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLowerLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getUpperLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param lower   WebIDL type: float
     * @param upper   WebIDL type: float
     */
    fun setLimits(jointId: Long, lower: Float, upper: Float)

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableMotor WebIDL type: boolean
     */
    fun enableMotor(jointId: Long, enableMotor: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isMotorEnabled(jointId: Long): Boolean

    /**
     * @param jointId    WebIDL type: unsigned long long
     * @param motorSpeed WebIDL type: float
     */
    fun setMotorSpeed(jointId: Long, motorSpeed: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorSpeed(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param force   WebIDL type: float
     */
    fun setMaxMotorForce(jointId: Long, force: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxMotorForce(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorForce(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getTranslation(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpeed(jointId: Long): Float

}

fun B2_PrismaticJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_PrismaticJoint = js("_module.wrapPointer(ptr, _module.B2_PrismaticJoint)")

external interface b2PrismaticJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var dampingRatio: Float
    /**
     * WebIDL type: boolean
     */
    var enableLimit: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableMotor: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSpring: Boolean
    /**
     * WebIDL type: float
     */
    var hertz: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorB: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAxisA: b2Vec2
    /**
     * WebIDL type: float
     */
    var lowerTranslation: Float
    /**
     * WebIDL type: float
     */
    var maxMotorForce: Float
    /**
     * WebIDL type: float
     */
    var motorSpeed: Float
    /**
     * WebIDL type: float
     */
    var referenceAngle: Float
    /**
     * WebIDL type: float
     */
    var upperTranslation: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2PrismaticJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2PrismaticJointDef = js("new _module.b2PrismaticJointDef()")

fun b2PrismaticJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2PrismaticJointDef = js("_module.wrapPointer(ptr, _module.b2PrismaticJointDef)")

fun b2PrismaticJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_RevoluteJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2RevoluteJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createRevoluteJoint(worldId: Long, def: b2RevoluteJointDef): Long

    /**
     * @param result WebIDL type: [b2RevoluteJointDef] (Ref)
     */
    fun defaultRevoluteJointDef(result: b2RevoluteJointDef)

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param enableSpring WebIDL type: boolean
     */
    fun enableSpring(jointId: Long, enableSpring: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSpringEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setSpringHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setSpringDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringDampingRatio(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param angle   WebIDL type: float
     */
    fun setTargetAngle(jointId: Long, angle: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getTargetAngle(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngle(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableLimit WebIDL type: boolean
     */
    fun enableLimit(jointId: Long, enableLimit: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isLimitEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLowerLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getUpperLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param lower   WebIDL type: float
     * @param upper   WebIDL type: float
     */
    fun setLimits(jointId: Long, lower: Float, upper: Float)

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableMotor WebIDL type: boolean
     */
    fun enableMotor(jointId: Long, enableMotor: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isMotorEnabled(jointId: Long): Boolean

    /**
     * @param jointId    WebIDL type: unsigned long long
     * @param motorSpeed WebIDL type: float
     */
    fun setMotorSpeed(jointId: Long, motorSpeed: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorSpeed(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorTorque(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param torque  WebIDL type: float
     */
    fun setMaxMotorTorque(jointId: Long, torque: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxMotorTorque(jointId: Long): Float

}

fun B2_RevoluteJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_RevoluteJoint = js("_module.wrapPointer(ptr, _module.B2_RevoluteJoint)")

external interface b2RevoluteJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var dampingRatio: Float
    /**
     * WebIDL type: float
     */
    var drawSize: Float
    /**
     * WebIDL type: boolean
     */
    var enableLimit: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableMotor: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSpring: Boolean
    /**
     * WebIDL type: float
     */
    var hertz: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorB: b2Vec2
    /**
     * WebIDL type: float
     */
    var lowerAngle: Float
    /**
     * WebIDL type: float
     */
    var maxMotorTorque: Float
    /**
     * WebIDL type: float
     */
    var motorSpeed: Float
    /**
     * WebIDL type: float
     */
    var referenceAngle: Float
    /**
     * WebIDL type: float
     */
    var upperAngle: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2RevoluteJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2RevoluteJointDef = js("new _module.b2RevoluteJointDef()")

fun b2RevoluteJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2RevoluteJointDef = js("_module.wrapPointer(ptr, _module.b2RevoluteJointDef)")

fun b2RevoluteJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_WeldJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2WeldJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createWeldJoint(worldId: Long, def: b2WeldJointDef): Long

    /**
     * @param result WebIDL type: [b2WeldJointDef] (Ref)
     */
    fun defaultWeldJointDef(result: b2WeldJointDef)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setLinearHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLinearHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setLinearDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLinearDampingRatio(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setAngularHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setAngularDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getAngularDampingRatio(jointId: Long): Float

}

fun B2_WeldJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_WeldJoint = js("_module.wrapPointer(ptr, _module.B2_WeldJoint)")

external interface b2WeldJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var angularDampingRatio: Float
    /**
     * WebIDL type: float
     */
    var angularHertz: Float
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: float
     */
    var linearDampingRatio: Float
    /**
     * WebIDL type: float
     */
    var linearHertz: Float
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorB: b2Vec2
    /**
     * WebIDL type: float
     */
    var referenceAngle: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2WeldJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2WeldJointDef = js("new _module.b2WeldJointDef()")

fun b2WeldJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2WeldJointDef = js("_module.wrapPointer(ptr, _module.b2WeldJointDef)")

fun b2WeldJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_WheelJoint {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * @param worldId WebIDL type: unsigned long long
     * @param def     WebIDL type: [b2WheelJointDef] (Const)
     * @return WebIDL type: unsigned long long
     */
    fun createWheelJoint(worldId: Long, def: b2WheelJointDef): Long

    /**
     * @param result WebIDL type: [b2WheelJointDef] (Ref)
     */
    fun defaultWheelJointDef(result: b2WheelJointDef)

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param enableSpring WebIDL type: boolean
     */
    fun enableSpring(jointId: Long, enableSpring: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isSpringEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param hertz   WebIDL type: float
     */
    fun setSpringHertz(jointId: Long, hertz: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringHertz(jointId: Long): Float

    /**
     * @param jointId      WebIDL type: unsigned long long
     * @param dampingRatio WebIDL type: float
     */
    fun setSpringDampingRatio(jointId: Long, dampingRatio: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getSpringDampingRatio(jointId: Long): Float

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableLimit WebIDL type: boolean
     */
    fun enableLimit(jointId: Long, enableLimit: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isLimitEnabled(jointId: Long): Boolean

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getLowerLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getUpperLimit(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param lower   WebIDL type: float
     * @param upper   WebIDL type: float
     */
    fun setLimits(jointId: Long, lower: Float, upper: Float)

    /**
     * @param jointId     WebIDL type: unsigned long long
     * @param enableMotor WebIDL type: boolean
     */
    fun enableMotor(jointId: Long, enableMotor: Boolean)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: boolean
     */
    fun isMotorEnabled(jointId: Long): Boolean

    /**
     * @param jointId    WebIDL type: unsigned long long
     * @param motorSpeed WebIDL type: float
     */
    fun setMotorSpeed(jointId: Long, motorSpeed: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorSpeed(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @param torque  WebIDL type: float
     */
    fun setMaxMotorTorque(jointId: Long, torque: Float)

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMaxMotorTorque(jointId: Long): Float

    /**
     * @param jointId WebIDL type: unsigned long long
     * @return WebIDL type: float
     */
    fun getMotorTorque(jointId: Long): Float

}

fun B2_WheelJointFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_WheelJoint = js("_module.wrapPointer(ptr, _module.B2_WheelJoint)")

external interface b2WheelJointDef {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdA: b2BodyId
    /**
     * WebIDL type: [b2BodyId] (Value)
     */
    var bodyIdB: b2BodyId
    /**
     * WebIDL type: boolean
     */
    var collideConnected: Boolean
    /**
     * WebIDL type: float
     */
    var dampingRatio: Float
    /**
     * WebIDL type: boolean
     */
    var enableLimit: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableMotor: Boolean
    /**
     * WebIDL type: boolean
     */
    var enableSpring: Boolean
    /**
     * WebIDL type: float
     */
    var hertz: Float
    /**
     * WebIDL type: long
     */
    var internalValue: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorA: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAnchorB: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var localAxisA: b2Vec2
    /**
     * WebIDL type: float
     */
    var lowerTranslation: Float
    /**
     * WebIDL type: float
     */
    var maxMotorTorque: Float
    /**
     * WebIDL type: float
     */
    var motorSpeed: Float
    /**
     * WebIDL type: float
     */
    var upperTranslation: Float
    /**
     * WebIDL type: VoidPtr
     */
    var userData: Any
}

fun b2WheelJointDef(_module: dynamic = Box2dWasmLoader.box2dWasm): b2WheelJointDef = js("new _module.b2WheelJointDef()")

fun b2WheelJointDefFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2WheelJointDef = js("_module.wrapPointer(ptr, _module.b2WheelJointDef)")

fun b2WheelJointDef.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface B2_Geometry {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: unsigned long
     */
    var MAX_POLYGON_VERTICES: Int

    /**
     * @param input WebIDL type: [b2RayCastInput] (Const)
     * @return WebIDL type: boolean
     */
    fun isValidRay(input: b2RayCastInput): Boolean

    /**
     * @param hull   WebIDL type: [b2Hull] (Const)
     * @param radius WebIDL type: float
     * @param result WebIDL type: [b2Polygon] (Ref)
     */
    fun makePolygon(hull: b2Hull, radius: Float, result: b2Polygon)

    /**
     * @param hull     WebIDL type: [b2Hull] (Const)
     * @param position WebIDL type: [b2Vec2] (Ref)
     * @param rotation WebIDL type: [b2Rot] (Ref)
     * @param result   WebIDL type: [b2Polygon] (Ref)
     */
    fun makeOffsetPolygon(hull: b2Hull, position: b2Vec2, rotation: b2Rot, result: b2Polygon)

    /**
     * @param hull     WebIDL type: [b2Hull] (Const)
     * @param position WebIDL type: [b2Vec2] (Ref)
     * @param rotation WebIDL type: [b2Rot] (Ref)
     * @param radius   WebIDL type: float
     * @param result   WebIDL type: [b2Polygon] (Ref)
     */
    fun makeOffsetRoundedPolygon(hull: b2Hull, position: b2Vec2, rotation: b2Rot, radius: Float, result: b2Polygon)

    /**
     * @param halfWidth WebIDL type: float
     * @param result    WebIDL type: [b2Polygon] (Ref)
     */
    fun makeSquare(halfWidth: Float, result: b2Polygon)

    /**
     * @param halfWidth  WebIDL type: float
     * @param halfHeight WebIDL type: float
     * @param result     WebIDL type: [b2Polygon] (Ref)
     */
    fun makeBox(halfWidth: Float, halfHeight: Float, result: b2Polygon)

    /**
     * @param halfWidth  WebIDL type: float
     * @param halfHeight WebIDL type: float
     * @param radius     WebIDL type: float
     * @param result     WebIDL type: [b2Polygon] (Ref)
     */
    fun makeRoundedBox(halfWidth: Float, halfHeight: Float, radius: Float, result: b2Polygon)

    /**
     * @param halfWidth  WebIDL type: float
     * @param halfHeight WebIDL type: float
     * @param center     WebIDL type: [b2Vec2] (Ref)
     * @param rotation   WebIDL type: [b2Rot] (Ref)
     * @param result     WebIDL type: [b2Polygon] (Ref)
     */
    fun makeOffsetBox(halfWidth: Float, halfHeight: Float, center: b2Vec2, rotation: b2Rot, result: b2Polygon)

    /**
     * @param halfWidth  WebIDL type: float
     * @param halfHeight WebIDL type: float
     * @param center     WebIDL type: [b2Vec2] (Ref)
     * @param rotation   WebIDL type: [b2Rot] (Ref)
     * @param radius     WebIDL type: float
     * @param result     WebIDL type: [b2Polygon] (Ref)
     */
    fun makeOffsetRoundedBox(halfWidth: Float, halfHeight: Float, center: b2Vec2, rotation: b2Rot, radius: Float, result: b2Polygon)

    /**
     * @param transform WebIDL type: [b2Transform] (Ref)
     * @param polygon   WebIDL type: [b2Polygon] (Const)
     * @param result    WebIDL type: [b2Polygon] (Ref)
     */
    fun transformPolygon(transform: b2Transform, polygon: b2Polygon, result: b2Polygon)

    /**
     * @param shape   WebIDL type: [b2Circle] (Const)
     * @param density WebIDL type: float
     * @return WebIDL type: [b2MassData] (Value)
     */
    fun computeCircleMass(shape: b2Circle, density: Float): b2MassData

    /**
     * @param shape   WebIDL type: [b2Capsule] (Const)
     * @param density WebIDL type: float
     * @return WebIDL type: [b2MassData] (Value)
     */
    fun computeCapsuleMass(shape: b2Capsule, density: Float): b2MassData

    /**
     * @param shape   WebIDL type: [b2Polygon] (Const)
     * @param density WebIDL type: float
     * @return WebIDL type: [b2MassData] (Value)
     */
    fun computePolygonMass(shape: b2Polygon, density: Float): b2MassData

    /**
     * @param shape     WebIDL type: [b2Circle] (Const)
     * @param transform WebIDL type: [b2Transform] (Ref)
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun computeCircleAABB(shape: b2Circle, transform: b2Transform): b2AABB

    /**
     * @param shape     WebIDL type: [b2Capsule] (Const)
     * @param transform WebIDL type: [b2Transform] (Ref)
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun computeCapsuleAABB(shape: b2Capsule, transform: b2Transform): b2AABB

    /**
     * @param shape     WebIDL type: [b2Polygon] (Const)
     * @param transform WebIDL type: [b2Transform] (Ref)
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun computePolygonAABB(shape: b2Polygon, transform: b2Transform): b2AABB

    /**
     * @param shape     WebIDL type: [b2Segment] (Const)
     * @param transform WebIDL type: [b2Transform] (Ref)
     * @return WebIDL type: [b2AABB] (Value)
     */
    fun computeSegmentAABB(shape: b2Segment, transform: b2Transform): b2AABB

    /**
     * @param point WebIDL type: [b2Vec2] (Ref)
     * @param shape WebIDL type: [b2Circle] (Const)
     * @return WebIDL type: boolean
     */
    fun pointInCircle(point: b2Vec2, shape: b2Circle): Boolean

    /**
     * @param point WebIDL type: [b2Vec2] (Ref)
     * @param shape WebIDL type: [b2Capsule] (Const)
     * @return WebIDL type: boolean
     */
    fun pointInCapsule(point: b2Vec2, shape: b2Capsule): Boolean

    /**
     * @param point WebIDL type: [b2Vec2] (Ref)
     * @param shape WebIDL type: [b2Polygon] (Const)
     * @return WebIDL type: boolean
     */
    fun pointInPolygon(point: b2Vec2, shape: b2Polygon): Boolean

    /**
     * @param input WebIDL type: [b2RayCastInput] (Const)
     * @param shape WebIDL type: [b2Circle] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun rayCastCircle(input: b2RayCastInput, shape: b2Circle): b2CastOutput

    /**
     * @param input WebIDL type: [b2RayCastInput] (Const)
     * @param shape WebIDL type: [b2Capsule] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun rayCastCapsule(input: b2RayCastInput, shape: b2Capsule): b2CastOutput

    /**
     * @param input    WebIDL type: [b2RayCastInput] (Const)
     * @param shape    WebIDL type: [b2Segment] (Const)
     * @param oneSided WebIDL type: boolean
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun rayCastSegment(input: b2RayCastInput, shape: b2Segment, oneSided: Boolean): b2CastOutput

    /**
     * @param input WebIDL type: [b2RayCastInput] (Const)
     * @param shape WebIDL type: [b2Polygon] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun rayCastPolygon(input: b2RayCastInput, shape: b2Polygon): b2CastOutput

    /**
     * @param input WebIDL type: [b2ShapeCastInput] (Const)
     * @param shape WebIDL type: [b2Circle] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun shapeCastCircle(input: b2ShapeCastInput, shape: b2Circle): b2CastOutput

    /**
     * @param input WebIDL type: [b2ShapeCastInput] (Const)
     * @param shape WebIDL type: [b2Capsule] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun shapeCastCapsule(input: b2ShapeCastInput, shape: b2Capsule): b2CastOutput

    /**
     * @param input WebIDL type: [b2ShapeCastInput] (Const)
     * @param shape WebIDL type: [b2Segment] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun shapeCastSegment(input: b2ShapeCastInput, shape: b2Segment): b2CastOutput

    /**
     * @param input WebIDL type: [b2ShapeCastInput] (Const)
     * @param shape WebIDL type: [b2Polygon] (Const)
     * @return WebIDL type: [b2CastOutput] (Value)
     */
    fun shapeCastPolygon(input: b2ShapeCastInput, shape: b2Polygon): b2CastOutput

    /**
     * @param points WebIDL type: [b2Vec2Array]
     * @param result WebIDL type: [b2Hull] (Ref)
     */
    fun computeHull(points: b2Vec2Array, result: b2Hull)

    /**
     * @param hull WebIDL type: [b2Hull] (Const)
     * @return WebIDL type: boolean
     */
    fun validateHull(hull: b2Hull): Boolean

}

fun B2_GeometryFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): B2_Geometry = js("_module.wrapPointer(ptr, _module.B2_Geometry)")

external interface b2MassData {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var center: b2Vec2
    /**
     * WebIDL type: float
     */
    var mass: Float
    /**
     * WebIDL type: float
     */
    var rotationalInertia: Float
}

fun b2MassData(_module: dynamic = Box2dWasmLoader.box2dWasm): b2MassData = js("new _module.b2MassData()")

fun b2MassDataFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2MassData = js("_module.wrapPointer(ptr, _module.b2MassData)")

fun b2MassData.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Circle {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var center: b2Vec2
    /**
     * WebIDL type: float
     */
    var radius: Float
}

fun b2Circle(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Circle = js("new _module.b2Circle()")

fun b2CircleFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Circle = js("_module.wrapPointer(ptr, _module.b2Circle)")

fun b2Circle.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Capsule {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var center1: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var center2: b2Vec2
    /**
     * WebIDL type: float
     */
    var radius: Float
}

fun b2Capsule(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Capsule = js("new _module.b2Capsule()")

fun b2CapsuleFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Capsule = js("_module.wrapPointer(ptr, _module.b2Capsule)")

fun b2Capsule.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Polygon {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    fun get_vertices(index: Int): b2Vec2
    fun set_vertices(index: Int, value: b2Vec2)
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    fun get_normals(index: Int): b2Vec2
    fun set_normals(index: Int, value: b2Vec2)
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var centroid: b2Vec2
    /**
     * WebIDL type: float
     */
    var radius: Float
    /**
     * WebIDL type: long
     */
    var count: Int
}

fun b2Polygon(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Polygon = js("new _module.b2Polygon()")

fun b2PolygonFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Polygon = js("_module.wrapPointer(ptr, _module.b2Polygon)")

fun b2Polygon.destroy() {
    Box2dWasmLoader.destroy(this)
}

inline fun b2Polygon.getVertices(index: Int) = get_vertices(index)
inline fun b2Polygon.setVertices(index: Int, value: b2Vec2) = set_vertices(index, value)
inline fun b2Polygon.getNormals(index: Int) = get_normals(index)
inline fun b2Polygon.setNormals(index: Int, value: b2Vec2) = set_normals(index, value)

external interface b2Segment {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point1: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point2: b2Vec2
}

fun b2Segment(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Segment = js("new _module.b2Segment()")

fun b2SegmentFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Segment = js("_module.wrapPointer(ptr, _module.b2Segment)")

fun b2Segment.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ChainSegment {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var chainId: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var ghost1: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var ghost2: b2Vec2
    /**
     * WebIDL type: [b2Segment] (Value)
     */
    var segment: b2Segment
}

fun b2ChainSegment(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainSegment = js("new _module.b2ChainSegment()")

fun b2ChainSegmentFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ChainSegment = js("_module.wrapPointer(ptr, _module.b2ChainSegment)")

fun b2ChainSegment.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2Hull {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var count: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    fun get_points(index: Int): b2Vec2
    fun set_points(index: Int, value: b2Vec2)
}

fun b2Hull(_module: dynamic = Box2dWasmLoader.box2dWasm): b2Hull = js("new _module.b2Hull()")

fun b2HullFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2Hull = js("_module.wrapPointer(ptr, _module.b2Hull)")

fun b2Hull.destroy() {
    Box2dWasmLoader.destroy(this)
}

inline fun b2Hull.getPoints(index: Int) = get_points(index)
inline fun b2Hull.setPoints(index: Int, value: b2Vec2) = set_points(index, value)

external interface b2ShapeProxy {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: long
     */
    var count: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    fun get_points(index: Int): b2Vec2
    fun set_points(index: Int, value: b2Vec2)
    /**
     * WebIDL type: float
     */
    var radius: Float
}

fun b2ShapeProxy(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeProxy = js("new _module.b2ShapeProxy()")

fun b2ShapeProxyFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeProxy = js("_module.wrapPointer(ptr, _module.b2ShapeProxy)")

fun b2ShapeProxy.destroy() {
    Box2dWasmLoader.destroy(this)
}

inline fun b2ShapeProxy.getPoints(index: Int) = get_points(index)
inline fun b2ShapeProxy.setPoints(index: Int, value: b2Vec2) = set_points(index, value)

external interface b2RayCastInput {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var maxFraction: Float
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var origin: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var translation: b2Vec2
}

fun b2RayCastInput(_module: dynamic = Box2dWasmLoader.box2dWasm): b2RayCastInput = js("new _module.b2RayCastInput()")

fun b2RayCastInputFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2RayCastInput = js("_module.wrapPointer(ptr, _module.b2RayCastInput)")

fun b2RayCastInput.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2ShapeCastInput {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: boolean
     */
    var canEncroach: Boolean
    /**
     * WebIDL type: float
     */
    var maxFraction: Float
    /**
     * WebIDL type: [b2ShapeProxy] (Value)
     */
    var proxy: b2ShapeProxy
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var translation: b2Vec2
}

fun b2ShapeCastInput(_module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeCastInput = js("new _module.b2ShapeCastInput()")

fun b2ShapeCastInputFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2ShapeCastInput = js("_module.wrapPointer(ptr, _module.b2ShapeCastInput)")

fun b2ShapeCastInput.destroy() {
    Box2dWasmLoader.destroy(this)
}

external interface b2CastOutput {
    /**
     * Native object address.
     */
    val ptr: Int

    /**
     * WebIDL type: float
     */
    var fraction: Float
    /**
     * WebIDL type: boolean
     */
    var hit: Boolean
    /**
     * WebIDL type: long
     */
    var iterations: Int
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var normal: b2Vec2
    /**
     * WebIDL type: [b2Vec2] (Value)
     */
    var point: b2Vec2
}

fun b2CastOutput(_module: dynamic = Box2dWasmLoader.box2dWasm): b2CastOutput = js("new _module.b2CastOutput()")

fun b2CastOutputFromPointer(ptr: Int, _module: dynamic = Box2dWasmLoader.box2dWasm): b2CastOutput = js("_module.wrapPointer(ptr, _module.b2CastOutput)")

fun b2CastOutput.destroy() {
    Box2dWasmLoader.destroy(this)
}

value class b2ShapeType private constructor(val value: Int) {
    companion object {
        val b2_circleShape: b2ShapeType = b2ShapeType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2ShapeType_b2_circleShape())
        val b2_capsuleShape: b2ShapeType = b2ShapeType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2ShapeType_b2_capsuleShape())
        val b2_segmentShape: b2ShapeType = b2ShapeType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2ShapeType_b2_segmentShape())
        val b2_polygonShape: b2ShapeType = b2ShapeType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2ShapeType_b2_polygonShape())
        val b2_chainSegmentShape: b2ShapeType = b2ShapeType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2ShapeType_b2_chainSegmentShape())
    }
}

value class b2BodyType private constructor(val value: Int) {
    companion object {
        val b2_staticBody: b2BodyType = b2BodyType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2BodyType_b2_staticBody())
        val b2_kinematicBody: b2BodyType = b2BodyType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2BodyType_b2_kinematicBody())
        val b2_dynamicBody: b2BodyType = b2BodyType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2BodyType_b2_dynamicBody())
    }
}

value class b2JointType private constructor(val value: Int) {
    companion object {
        val b2_distanceJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_distanceJoint())
        val b2_filterJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_filterJoint())
        val b2_motorJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_motorJoint())
        val b2_mouseJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_mouseJoint())
        val b2_prismaticJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_prismaticJoint())
        val b2_revoluteJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_revoluteJoint())
        val b2_weldJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_weldJoint())
        val b2_wheelJoint: b2JointType = b2JointType(Box2dWasmLoader.box2dWasm._emscripten_enum_b2JointType_b2_wheelJoint())
    }
}

