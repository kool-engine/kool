package de.fabmax.kool.util

import de.fabmax.kool.math.*

fun Struct.indexOf(memberName: String) = members.indexOfFirst { it.name == memberName }

fun Struct.getFloat1(memberIndex: Int): Float1Member = members[memberIndex] as Float1Member
fun Struct.getFloat2(memberIndex: Int): Float2Member = members[memberIndex] as Float2Member
fun Struct.getFloat3(memberIndex: Int): Float3Member = members[memberIndex] as Float3Member
fun Struct.getFloat4(memberIndex: Int): Float4Member = members[memberIndex] as Float4Member

fun Struct.getInt1(memberIndex: Int): Int1Member = members[memberIndex] as Int1Member
fun Struct.getInt2(memberIndex: Int): Int2Member = members[memberIndex] as Int2Member
fun Struct.getInt3(memberIndex: Int): Int3Member = members[memberIndex] as Int3Member
fun Struct.getInt4(memberIndex: Int): Int4Member = members[memberIndex] as Int4Member

fun Struct.getMat2(memberIndex: Int): Mat2Member = members[memberIndex] as Mat2Member
fun Struct.getMat3(memberIndex: Int): Mat3Member = members[memberIndex] as Mat3Member
fun Struct.getMat4(memberIndex: Int): Mat4Member = members[memberIndex] as Mat4Member

fun Struct.getFloat1Array(memberIndex: Int): Float1ArrayMember = members[memberIndex] as Float1ArrayMember
fun Struct.getFloat2Array(memberIndex: Int): Float2ArrayMember = members[memberIndex] as Float2ArrayMember
fun Struct.getFloat3Array(memberIndex: Int): Float3ArrayMember = members[memberIndex] as Float3ArrayMember
fun Struct.getFloat4Array(memberIndex: Int): Float4ArrayMember = members[memberIndex] as Float4ArrayMember

fun Struct.getInt1Array(memberIndex: Int): Int1ArrayMember = members[memberIndex] as Int1ArrayMember
fun Struct.getInt2Array(memberIndex: Int): Int2ArrayMember = members[memberIndex] as Int2ArrayMember
fun Struct.getInt3Array(memberIndex: Int): Int3ArrayMember = members[memberIndex] as Int3ArrayMember
fun Struct.getInt4Array(memberIndex: Int): Int4ArrayMember = members[memberIndex] as Int4ArrayMember

fun Struct.getMat2Array(memberIndex: Int): Mat2ArrayMember = members[memberIndex] as Mat2ArrayMember
fun Struct.getMat3Array(memberIndex: Int): Mat3ArrayMember = members[memberIndex] as Mat3ArrayMember
fun Struct.getMat4Array(memberIndex: Int): Mat4ArrayMember = members[memberIndex] as Mat4ArrayMember

fun Struct.getStruct(memberIndex: Int): Struct = members[memberIndex] as Struct
fun Struct.getStructArray(memberIndex: Int): NestedStructArrayMember<*> = members[memberIndex] as NestedStructArrayMember<*>


fun Struct.getFloat1(member: String): Float1Member = getFloat1(indexOf(member))
fun Struct.getFloat2(member: String): Float2Member = getFloat2(indexOf(member))
fun Struct.getFloat3(member: String): Float3Member = getFloat3(indexOf(member))
fun Struct.getFloat4(member: String): Float4Member = getFloat4(indexOf(member))

fun Struct.getInt1(member: String): Int1Member = getInt1(indexOf(member))
fun Struct.getInt2(member: String): Int2Member = getInt2(indexOf(member))
fun Struct.getInt3(member: String): Int3Member = getInt3(indexOf(member))
fun Struct.getInt4(member: String): Int4Member = getInt4(indexOf(member))

fun Struct.getMat2(member: String): Mat2Member = getMat2(indexOf(member))
fun Struct.getMat3(member: String): Mat3Member = getMat3(indexOf(member))
fun Struct.getMat4(member: String): Mat4Member = getMat4(indexOf(member))

fun Struct.getFloat1Array(member: String): Float1ArrayMember = getFloat1Array(indexOf(member))
fun Struct.getFloat2Array(member: String): Float2ArrayMember = getFloat2Array(indexOf(member))
fun Struct.getFloat3Array(member: String): Float3ArrayMember = getFloat3Array(indexOf(member))
fun Struct.getFloat4Array(member: String): Float4ArrayMember = getFloat4Array(indexOf(member))

fun Struct.getInt1Array(member: String): Int1ArrayMember = getInt1Array(indexOf(member))
fun Struct.getInt2Array(member: String): Int2ArrayMember = getInt2Array(indexOf(member))
fun Struct.getInt3Array(member: String): Int3ArrayMember = getInt3Array(indexOf(member))
fun Struct.getInt4Array(member: String): Int4ArrayMember = getInt4Array(indexOf(member))

fun Struct.getMat2Array(member: String): Mat2ArrayMember = getMat2Array(indexOf(member))
fun Struct.getMat3Array(member: String): Mat3ArrayMember = getMat3Array(indexOf(member))
fun Struct.getMat4Array(member: String): Mat4ArrayMember = getMat4Array(indexOf(member))

fun Struct.getStruct(member: String): Struct = getStruct(indexOf(member))
fun Struct.getStructArray(member: String): NestedStructArrayMember<*> = getStructArray(indexOf(member))


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
