package de.fabmax.kool.util

import de.fabmax.kool.math.*

fun Struct.indexOf(memberName: String) = members.indexOfFirst { it.memberName == memberName }


fun Struct.getFloat1(memberIndex: Int): Struct.Float1Member = members[memberIndex] as Struct.Float1Member
fun Struct.getFloat2(memberIndex: Int): Struct.Float2Member = members[memberIndex] as Struct.Float2Member
fun Struct.getFloat3(memberIndex: Int): Struct.Float3Member = members[memberIndex] as Struct.Float3Member
fun Struct.getFloat4(memberIndex: Int): Struct.Float4Member = members[memberIndex] as Struct.Float4Member

fun Struct.getInt1(memberIndex: Int): Struct.Int1Member = members[memberIndex] as Struct.Int1Member
fun Struct.getInt2(memberIndex: Int): Struct.Int2Member = members[memberIndex] as Struct.Int2Member
fun Struct.getInt3(memberIndex: Int): Struct.Int3Member = members[memberIndex] as Struct.Int3Member
fun Struct.getInt4(memberIndex: Int): Struct.Int4Member = members[memberIndex] as Struct.Int4Member

fun Struct.getMat2(memberIndex: Int): Struct.Mat2Member = members[memberIndex] as Struct.Mat2Member
fun Struct.getMat3(memberIndex: Int): Struct.Mat3Member = members[memberIndex] as Struct.Mat3Member
fun Struct.getMat4(memberIndex: Int): Struct.Mat4Member = members[memberIndex] as Struct.Mat4Member

fun Struct.getFloat1Array(memberIndex: Int): Struct.Float1ArrayMember = members[memberIndex] as Struct.Float1ArrayMember
fun Struct.getFloat2Array(memberIndex: Int): Struct.Float2ArrayMember = members[memberIndex] as Struct.Float2ArrayMember
fun Struct.getFloat3Array(memberIndex: Int): Struct.Float3ArrayMember = members[memberIndex] as Struct.Float3ArrayMember
fun Struct.getFloat4Array(memberIndex: Int): Struct.Float4ArrayMember = members[memberIndex] as Struct.Float4ArrayMember

fun Struct.getInt1Array(memberIndex: Int): Struct.Int1ArrayMember = members[memberIndex] as Struct.Int1ArrayMember
fun Struct.getInt2Array(memberIndex: Int): Struct.Int2ArrayMember = members[memberIndex] as Struct.Int2ArrayMember
fun Struct.getInt3Array(memberIndex: Int): Struct.Int3ArrayMember = members[memberIndex] as Struct.Int3ArrayMember
fun Struct.getInt4Array(memberIndex: Int): Struct.Int4ArrayMember = members[memberIndex] as Struct.Int4ArrayMember

fun Struct.getMat2Array(memberIndex: Int): Struct.Mat2ArrayMember = members[memberIndex] as Struct.Mat2ArrayMember
fun Struct.getMat3Array(memberIndex: Int): Struct.Mat3ArrayMember = members[memberIndex] as Struct.Mat3ArrayMember
fun Struct.getMat4Array(memberIndex: Int): Struct.Mat4ArrayMember = members[memberIndex] as Struct.Mat4ArrayMember

fun Struct.getStruct(memberIndex: Int): Struct = members[memberIndex] as Struct
fun Struct.getStructArray(memberIndex: Int): Struct.NestedStructArrayMember<*> = members[memberIndex] as Struct.NestedStructArrayMember<*>


fun Struct.setFloat1(memberIndex: Int, value: Float) = getFloat1(memberIndex).set(value)
fun Struct.setFloat2(memberIndex: Int, value: Vec2f) = getFloat2(memberIndex).set(value)
fun Struct.setFloat3(memberIndex: Int, value: Vec3f) = getFloat3(memberIndex).set(value)
fun Struct.setFloat4(memberIndex: Int, value: Vec4f) = getFloat4(memberIndex).set(value)

fun Struct.setInt1(memberIndex: Int, value: Int) = getInt1(memberIndex).set(value)
fun Struct.setInt2(memberIndex: Int, value: Vec2i) = getInt2(memberIndex).set(value)
fun Struct.setInt3(memberIndex: Int, value: Vec3i) = getInt3(memberIndex).set(value)
fun Struct.setInt4(memberIndex: Int, value: Vec4i) = getInt4(memberIndex).set(value)

fun Struct.setMat2(memberIndex: Int, value: Mat2f) = getMat2(memberIndex).set(value)
fun Struct.setMat3(memberIndex: Int, value: Mat3f) = getMat3(memberIndex).set(value)
fun Struct.setMat4(memberIndex: Int, value: Mat4f) = getMat4(memberIndex).set(value)

fun Struct.setFloat1Array(memberIndex: Int, index: Int, value: Float) = getFloat1Array(memberIndex).set(index, value)
fun Struct.setFloat2Array(memberIndex: Int, index: Int, value: Vec2f) = getFloat2Array(memberIndex).set(index, value)
fun Struct.setFloat3Array(memberIndex: Int, index: Int, value: Vec3f) = getFloat3Array(memberIndex).set(index, value)
fun Struct.setFloat4Array(memberIndex: Int, index: Int, value: Vec4f) = getFloat4Array(memberIndex).set(index, value)

fun Struct.setInt1Array(memberIndex: Int, index: Int, value: Int) = getInt1Array(memberIndex).set(index, value)
fun Struct.setInt2Array(memberIndex: Int, index: Int, value: Vec2i) = getInt2Array(memberIndex).set(index, value)
fun Struct.setInt3Array(memberIndex: Int, index: Int, value: Vec3i) = getInt3Array(memberIndex).set(index, value)
fun Struct.setInt4Array(memberIndex: Int, index: Int, value: Vec4i) = getInt4Array(memberIndex).set(index, value)

fun Struct.setMat2Array(memberIndex: Int, index: Int, value: Mat2f) = getMat2Array(memberIndex).set(index, value)
fun Struct.setMat3Array(memberIndex: Int, index: Int, value: Mat3f) = getMat3Array(memberIndex).set(index, value)
fun Struct.setMat4Array(memberIndex: Int, index: Int, value: Mat4f) = getMat4Array(memberIndex).set(index, value)


fun Struct.getFloat1(member: String): Struct.Float1Member = getFloat1(indexOf(member))
fun Struct.getFloat2(member: String): Struct.Float2Member = getFloat2(indexOf(member))
fun Struct.getFloat3(member: String): Struct.Float3Member = getFloat3(indexOf(member))
fun Struct.getFloat4(member: String): Struct.Float4Member = getFloat4(indexOf(member))

fun Struct.getInt1(member: String): Struct.Int1Member = getInt1(indexOf(member))
fun Struct.getInt2(member: String): Struct.Int2Member = getInt2(indexOf(member))
fun Struct.getInt3(member: String): Struct.Int3Member = getInt3(indexOf(member))
fun Struct.getInt4(member: String): Struct.Int4Member = getInt4(indexOf(member))

fun Struct.getMat2(member: String): Struct.Mat2Member = getMat2(indexOf(member))
fun Struct.getMat3(member: String): Struct.Mat3Member = getMat3(indexOf(member))
fun Struct.getMat4(member: String): Struct.Mat4Member = getMat4(indexOf(member))

fun Struct.getFloat1Array(member: String): Struct.Float1ArrayMember = getFloat1Array(indexOf(member))
fun Struct.getFloat2Array(member: String): Struct.Float2ArrayMember = getFloat2Array(indexOf(member))
fun Struct.getFloat3Array(member: String): Struct.Float3ArrayMember = getFloat3Array(indexOf(member))
fun Struct.getFloat4Array(member: String): Struct.Float4ArrayMember = getFloat4Array(indexOf(member))

fun Struct.getInt1Array(member: String): Struct.Int1ArrayMember = getInt1Array(indexOf(member))
fun Struct.getInt2Array(member: String): Struct.Int2ArrayMember = getInt2Array(indexOf(member))
fun Struct.getInt3Array(member: String): Struct.Int3ArrayMember = getInt3Array(indexOf(member))
fun Struct.getInt4Array(member: String): Struct.Int4ArrayMember = getInt4Array(indexOf(member))

fun Struct.getMat2Array(member: String): Struct.Mat2ArrayMember = getMat2Array(indexOf(member))
fun Struct.getMat3Array(member: String): Struct.Mat3ArrayMember = getMat3Array(indexOf(member))
fun Struct.getMat4Array(member: String): Struct.Mat4ArrayMember = getMat4Array(indexOf(member))

fun Struct.getStruct(member: String): Struct = getStruct(indexOf(member))
fun Struct.getStructArray(member: String): Struct.NestedStructArrayMember<*> = getStructArray(indexOf(member))


fun Struct.setFloat1(member: String, value: Float) = getFloat1(indexOf(member)).set(value)
fun Struct.setFloat2(member: String, value: Vec2f) = getFloat2(indexOf(member)).set(value)
fun Struct.setFloat3(member: String, value: Vec3f) = getFloat3(indexOf(member)).set(value)
fun Struct.setFloat4(member: String, value: Vec4f) = getFloat4(indexOf(member)).set(value)

fun Struct.setInt1(member: String, value: Int) = getInt1(indexOf(member)).set(value)
fun Struct.setInt2(member: String, value: Vec2i) = getInt2(indexOf(member)).set(value)
fun Struct.setInt3(member: String, value: Vec3i) = getInt3(indexOf(member)).set(value)
fun Struct.setInt4(member: String, value: Vec4i) = getInt4(indexOf(member)).set(value)

fun Struct.setMat2(member: String, value: Mat2f) = getMat2(indexOf(member)).set(value)
fun Struct.setMat3(member: String, value: Mat3f) = getMat3(indexOf(member)).set(value)
fun Struct.setMat4(member: String, value: Mat4f) = getMat4(indexOf(member)).set(value)

fun Struct.setFloat1Array(member: String, index: Int, value: Float) = getFloat1Array(indexOf(member)).set(index, value)
fun Struct.setFloat2Array(member: String, index: Int, value: Vec2f) = getFloat2Array(indexOf(member)).set(index, value)
fun Struct.setFloat3Array(member: String, index: Int, value: Vec3f) = getFloat3Array(indexOf(member)).set(index, value)
fun Struct.setFloat4Array(member: String, index: Int, value: Vec4f) = getFloat4Array(indexOf(member)).set(index, value)

fun Struct.setInt1Array(member: String, index: Int, value: Int) = getInt1Array(indexOf(member)).set(index, value)
fun Struct.setInt2Array(member: String, index: Int, value: Vec2i) = getInt2Array(indexOf(member)).set(index, value)
fun Struct.setInt3Array(member: String, index: Int, value: Vec3i) = getInt3Array(indexOf(member)).set(index, value)
fun Struct.setInt4Array(member: String, index: Int, value: Vec4i) = getInt4Array(indexOf(member)).set(index, value)

fun Struct.setMat2Array(member: String, index: Int, value: Mat2f) = getMat2Array(indexOf(member)).set(index, value)
fun Struct.setMat3Array(member: String, index: Int, value: Mat3f) = getMat3Array(indexOf(member)).set(index, value)
fun Struct.setMat4Array(member: String, index: Int, value: Mat4f) = getMat4Array(indexOf(member)).set(index, value)