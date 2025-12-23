@file:Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE", "unused")

package box2d.prototypes

import box2d.*
import kotlin.js.JsAny
import kotlin.js.js

val B2_AABB: B2_AABB = B2_AABB(Box2dWasmLoader.box2dWasm)
private fun B2_AABB(module: JsAny): B2_AABB = js("module.B2_AABB.prototype")

val B2_Base: B2_Base = B2_Base(Box2dWasmLoader.box2dWasm)
private fun B2_Base(module: JsAny): B2_Base = js("module.B2_Base.prototype")

val B2_Body: B2_Body = B2_Body(Box2dWasmLoader.box2dWasm)
private fun B2_Body(module: JsAny): B2_Body = js("module.B2_Body.prototype")

val B2_Chain: B2_Chain = B2_Chain(Box2dWasmLoader.box2dWasm)
private fun B2_Chain(module: JsAny): B2_Chain = js("module.B2_Chain.prototype")

val B2_CharacterMover: B2_CharacterMover = B2_CharacterMover(Box2dWasmLoader.box2dWasm)
private fun B2_CharacterMover(module: JsAny): B2_CharacterMover = js("module.B2_CharacterMover.prototype")

val B2_Collision: B2_Collision = B2_Collision(Box2dWasmLoader.box2dWasm)
private fun B2_Collision(module: JsAny): B2_Collision = js("module.B2_Collision.prototype")

val B2_Distance: B2_Distance = B2_Distance(Box2dWasmLoader.box2dWasm)
private fun B2_Distance(module: JsAny): B2_Distance = js("module.B2_Distance.prototype")

val B2_DistanceJoint: B2_DistanceJoint = B2_DistanceJoint(Box2dWasmLoader.box2dWasm)
private fun B2_DistanceJoint(module: JsAny): B2_DistanceJoint = js("module.B2_DistanceJoint.prototype")

val B2_FilterJoint: B2_FilterJoint = B2_FilterJoint(Box2dWasmLoader.box2dWasm)
private fun B2_FilterJoint(module: JsAny): B2_FilterJoint = js("module.B2_FilterJoint.prototype")

val B2_Geometry: B2_Geometry = B2_Geometry(Box2dWasmLoader.box2dWasm)
private fun B2_Geometry(module: JsAny): B2_Geometry = js("module.B2_Geometry.prototype")

val B2_Joint: B2_Joint = B2_Joint(Box2dWasmLoader.box2dWasm)
private fun B2_Joint(module: JsAny): B2_Joint = js("module.B2_Joint.prototype")

val B2_Mat22: B2_Mat22 = B2_Mat22(Box2dWasmLoader.box2dWasm)
private fun B2_Mat22(module: JsAny): B2_Mat22 = js("module.B2_Mat22.prototype")

val B2_Math: B2_Math = B2_Math(Box2dWasmLoader.box2dWasm)
private fun B2_Math(module: JsAny): B2_Math = js("module.B2_Math.prototype")

val B2_MotorJoint: B2_MotorJoint = B2_MotorJoint(Box2dWasmLoader.box2dWasm)
private fun B2_MotorJoint(module: JsAny): B2_MotorJoint = js("module.B2_MotorJoint.prototype")

val B2_MouseJoint: B2_MouseJoint = B2_MouseJoint(Box2dWasmLoader.box2dWasm)
private fun B2_MouseJoint(module: JsAny): B2_MouseJoint = js("module.B2_MouseJoint.prototype")

val B2_Plane: B2_Plane = B2_Plane(Box2dWasmLoader.box2dWasm)
private fun B2_Plane(module: JsAny): B2_Plane = js("module.B2_Plane.prototype")

val B2_PrismaticJoint: B2_PrismaticJoint = B2_PrismaticJoint(Box2dWasmLoader.box2dWasm)
private fun B2_PrismaticJoint(module: JsAny): B2_PrismaticJoint = js("module.B2_PrismaticJoint.prototype")

val B2_RevoluteJoint: B2_RevoluteJoint = B2_RevoluteJoint(Box2dWasmLoader.box2dWasm)
private fun B2_RevoluteJoint(module: JsAny): B2_RevoluteJoint = js("module.B2_RevoluteJoint.prototype")

val B2_Rot: B2_Rot = B2_Rot(Box2dWasmLoader.box2dWasm)
private fun B2_Rot(module: JsAny): B2_Rot = js("module.B2_Rot.prototype")

val B2_Shape: B2_Shape = B2_Shape(Box2dWasmLoader.box2dWasm)
private fun B2_Shape(module: JsAny): B2_Shape = js("module.B2_Shape.prototype")

val B2_Transform: B2_Transform = B2_Transform(Box2dWasmLoader.box2dWasm)
private fun B2_Transform(module: JsAny): B2_Transform = js("module.B2_Transform.prototype")

val B2_Vec2: B2_Vec2 = B2_Vec2(Box2dWasmLoader.box2dWasm)
private fun B2_Vec2(module: JsAny): B2_Vec2 = js("module.B2_Vec2.prototype")

val B2_WeldJoint: B2_WeldJoint = B2_WeldJoint(Box2dWasmLoader.box2dWasm)
private fun B2_WeldJoint(module: JsAny): B2_WeldJoint = js("module.B2_WeldJoint.prototype")

val B2_WheelJoint: B2_WheelJoint = B2_WheelJoint(Box2dWasmLoader.box2dWasm)
private fun B2_WheelJoint(module: JsAny): B2_WheelJoint = js("module.B2_WheelJoint.prototype")

val B2_World: B2_World = B2_World(Box2dWasmLoader.box2dWasm)
private fun B2_World(module: JsAny): B2_World = js("module.B2_World.prototype")

