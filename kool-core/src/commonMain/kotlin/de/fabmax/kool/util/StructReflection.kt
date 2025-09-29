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


fun MutableStructBufferView.getFloat1(struct: Struct, memberIndex: Int) = get(struct.getFloat1(memberIndex))
fun MutableStructBufferView.getFloat2(struct: Struct, memberIndex: Int) = get(struct.getFloat2(memberIndex))
fun MutableStructBufferView.getFloat3(struct: Struct, memberIndex: Int) = get(struct.getFloat3(memberIndex))
fun MutableStructBufferView.getFloat4(struct: Struct, memberIndex: Int) = get(struct.getFloat4(memberIndex))

fun MutableStructBufferView.getInt1(struct: Struct, memberIndex: Int) = get(struct.getInt1(memberIndex))
fun MutableStructBufferView.getInt2(struct: Struct, memberIndex: Int) = get(struct.getInt2(memberIndex))
fun MutableStructBufferView.getInt3(struct: Struct, memberIndex: Int) = get(struct.getInt3(memberIndex))
fun MutableStructBufferView.getInt4(struct: Struct, memberIndex: Int) = get(struct.getInt4(memberIndex))

fun MutableStructBufferView.getMat2(struct: Struct, memberIndex: Int) = get(struct.getMat2(memberIndex))
fun MutableStructBufferView.getMat3(struct: Struct, memberIndex: Int) = get(struct.getMat3(memberIndex))
fun MutableStructBufferView.getMat4(struct: Struct, memberIndex: Int) = get(struct.getMat4(memberIndex))

fun MutableStructBufferView.getFloat1Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat1Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getFloat2Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat2Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getFloat3Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat3Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getFloat4Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat4Array(memberIndex), arrayIndex)

fun MutableStructBufferView.getInt1Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getInt1Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getInt2Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getInt2Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getInt3Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getInt3Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getInt4Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getInt4Array(memberIndex), arrayIndex)

fun MutableStructBufferView.getMat2Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getMat2Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getMat3Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getMat3Array(memberIndex), arrayIndex)
fun MutableStructBufferView.getMat4Array(struct: Struct, memberIndex: Int, arrayIndex: Int) = get(struct.getMat4Array(memberIndex), arrayIndex)


fun MutableStructBufferView.setFloat1(struct: Struct, memberIndex: Int, value: Float) = set(struct.getFloat1(memberIndex), value)
fun MutableStructBufferView.setFloat2(struct: Struct, memberIndex: Int, value: Vec2f) = set(struct.getFloat2(memberIndex), value)
fun MutableStructBufferView.setFloat3(struct: Struct, memberIndex: Int, value: Vec3f) = set(struct.getFloat3(memberIndex), value)
fun MutableStructBufferView.setFloat4(struct: Struct, memberIndex: Int, value: Vec4f) = set(struct.getFloat4(memberIndex), value)

fun MutableStructBufferView.setInt1(struct: Struct, memberIndex: Int, value: Int) = set(struct.getInt1(memberIndex), value)
fun MutableStructBufferView.setInt2(struct: Struct, memberIndex: Int, value: Vec2i) = set(struct.getInt2(memberIndex), value)
fun MutableStructBufferView.setInt3(struct: Struct, memberIndex: Int, value: Vec3i) = set(struct.getInt3(memberIndex), value)
fun MutableStructBufferView.setInt4(struct: Struct, memberIndex: Int, value: Vec4i) = set(struct.getInt4(memberIndex), value)

fun MutableStructBufferView.setMat2(struct: Struct, memberIndex: Int, value: Mat2f) = set(struct.getMat2(memberIndex), value)
fun MutableStructBufferView.setMat3(struct: Struct, memberIndex: Int, value: Mat3f) = set(struct.getMat3(memberIndex), value)
fun MutableStructBufferView.setMat4(struct: Struct, memberIndex: Int, value: Mat4f) = set(struct.getMat4(memberIndex), value)

fun MutableStructBufferView.setFloat1Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Float) = set(struct.getFloat1Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setFloat2Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec2f) = set(struct.getFloat2Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setFloat3Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec3f) = set(struct.getFloat3Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setFloat4Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec4f) = set(struct.getFloat4Array(memberIndex), arrayIdnex, value)

fun MutableStructBufferView.setInt1Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Int) = set(struct.getInt1Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setInt2Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec2i) = set(struct.getInt2Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setInt3Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec3i) = set(struct.getInt3Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setInt4Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Vec4i) = set(struct.getInt4Array(memberIndex), arrayIdnex, value)

fun MutableStructBufferView.setMat2Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Mat2f) = set(struct.getMat2Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setMat3Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Mat3f) = set(struct.getMat3Array(memberIndex), arrayIdnex, value)
fun MutableStructBufferView.setMat4Array(struct: Struct, memberIndex: Int, arrayIdnex: Int, value: Mat4f) = set(struct.getMat4Array(memberIndex), arrayIdnex, value)


fun MutableStructBufferView.setFloat1(struct: Struct, memberName: String, value: Float) = set(struct.getFloat1(memberName), value)
fun MutableStructBufferView.setFloat2(struct: Struct, memberName: String, value: Vec2f) = set(struct.getFloat2(memberName), value)
fun MutableStructBufferView.setFloat3(struct: Struct, memberName: String, value: Vec3f) = set(struct.getFloat3(memberName), value)
fun MutableStructBufferView.setFloat4(struct: Struct, memberName: String, value: Vec4f) = set(struct.getFloat4(memberName), value)

fun MutableStructBufferView.setInt1(struct: Struct, memberName: String, value: Int) = set(struct.getInt1(memberName), value)
fun MutableStructBufferView.setInt2(struct: Struct, memberName: String, value: Vec2i) = set(struct.getInt2(memberName), value)
fun MutableStructBufferView.setInt3(struct: Struct, memberName: String, value: Vec3i) = set(struct.getInt3(memberName), value)
fun MutableStructBufferView.setInt4(struct: Struct, memberName: String, value: Vec4i) = set(struct.getInt4(memberName), value)

fun MutableStructBufferView.setMat2(struct: Struct, memberName: String, value: Mat2f) = set(struct.getMat2(memberName), value)
fun MutableStructBufferView.setMat3(struct: Struct, memberName: String, value: Mat3f) = set(struct.getMat3(memberName), value)
fun MutableStructBufferView.setMat4(struct: Struct, memberName: String, value: Mat4f) = set(struct.getMat4(memberName), value)

fun MutableStructBufferView.setFloat1Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Float) = set(struct.getFloat1Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setFloat2Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec2f) = set(struct.getFloat2Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setFloat3Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec3f) = set(struct.getFloat3Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setFloat4Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec4f) = set(struct.getFloat4Array(memberName), arrayIdnex, value)

fun MutableStructBufferView.setInt1Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Int) = set(struct.getInt1Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setInt2Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec2i) = set(struct.getInt2Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setInt3Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec3i) = set(struct.getInt3Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setInt4Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Vec4i) = set(struct.getInt4Array(memberName), arrayIdnex, value)

fun MutableStructBufferView.setMat2Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Mat2f) = set(struct.getMat2Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setMat3Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Mat3f) = set(struct.getMat3Array(memberName), arrayIdnex, value)
fun MutableStructBufferView.setMat4Array(struct: Struct, memberName: String, arrayIdnex: Int, value: Mat4f) = set(struct.getMat4Array(memberName), arrayIdnex, value)
