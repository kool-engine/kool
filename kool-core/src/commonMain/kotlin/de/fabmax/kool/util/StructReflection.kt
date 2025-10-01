@file:Suppress("UNCHECKED_CAST")

package de.fabmax.kool.util

import de.fabmax.kool.math.*

fun <S: Struct> S.indexOf(memberName: String): Int = members.indexOfFirst { it.name == memberName }

fun <S: Struct> S.getFloat1(memberIndex: Int): Float1Member<S> = members[memberIndex] as Float1Member<S>
fun <S: Struct> S.getFloat2(memberIndex: Int): Float2Member<S> = members[memberIndex] as Float2Member<S>
fun <S: Struct> S.getFloat3(memberIndex: Int): Float3Member<S> = members[memberIndex] as Float3Member<S>
fun <S: Struct> S.getFloat4(memberIndex: Int): Float4Member<S> = members[memberIndex] as Float4Member<S>

fun <S: Struct> S.getInt1(memberIndex: Int): Int1Member<S> = members[memberIndex] as Int1Member<S>
fun <S: Struct> S.getInt2(memberIndex: Int): Int2Member<S> = members[memberIndex] as Int2Member<S>
fun <S: Struct> S.getInt3(memberIndex: Int): Int3Member<S> = members[memberIndex] as Int3Member<S>
fun <S: Struct> S.getInt4(memberIndex: Int): Int4Member<S> = members[memberIndex] as Int4Member<S>

fun <S: Struct> S.getMat2(memberIndex: Int): Mat2Member<S> = members[memberIndex] as Mat2Member<S>
fun <S: Struct> S.getMat3(memberIndex: Int): Mat3Member<S> = members[memberIndex] as Mat3Member<S>
fun <S: Struct> S.getMat4(memberIndex: Int): Mat4Member<S> = members[memberIndex] as Mat4Member<S>

fun <S: Struct> S.getFloat1Array(memberIndex: Int): Float1ArrayMember<S> = members[memberIndex] as Float1ArrayMember<S>
fun <S: Struct> S.getFloat2Array(memberIndex: Int): Float2ArrayMember<S> = members[memberIndex] as Float2ArrayMember<S>
fun <S: Struct> S.getFloat3Array(memberIndex: Int): Float3ArrayMember<S> = members[memberIndex] as Float3ArrayMember<S>
fun <S: Struct> S.getFloat4Array(memberIndex: Int): Float4ArrayMember<S> = members[memberIndex] as Float4ArrayMember<S>

fun <S: Struct> S.getInt1Array(memberIndex: Int): Int1ArrayMember<S> = members[memberIndex] as Int1ArrayMember<S>
fun <S: Struct> S.getInt2Array(memberIndex: Int): Int2ArrayMember<S> = members[memberIndex] as Int2ArrayMember<S>
fun <S: Struct> S.getInt3Array(memberIndex: Int): Int3ArrayMember<S> = members[memberIndex] as Int3ArrayMember<S>
fun <S: Struct> S.getInt4Array(memberIndex: Int): Int4ArrayMember<S> = members[memberIndex] as Int4ArrayMember<S>

fun <S: Struct> S.getMat2Array(memberIndex: Int): Mat2ArrayMember<S> = members[memberIndex] as Mat2ArrayMember<S>
fun <S: Struct> S.getMat3Array(memberIndex: Int): Mat3ArrayMember<S> = members[memberIndex] as Mat3ArrayMember<S>
fun <S: Struct> S.getMat4Array(memberIndex: Int): Mat4ArrayMember<S> = members[memberIndex] as Mat4ArrayMember<S>

fun <S: Struct, N: Struct> S.getStruct(memberIndex: Int): NestedStructMember<S, N> = members[memberIndex] as NestedStructMember<S, N>
fun <S: Struct, N: Struct> S.getStructArray(memberIndex: Int): NestedStructArrayMember<S, N> = members[memberIndex] as NestedStructArrayMember<S, N>


fun <S: Struct> S.getFloat1(member: String): Float1Member<S> = getFloat1(indexOf(member))
fun <S: Struct> S.getFloat2(member: String): Float2Member<S> = getFloat2(indexOf(member))
fun <S: Struct> S.getFloat3(member: String): Float3Member<S> = getFloat3(indexOf(member))
fun <S: Struct> S.getFloat4(member: String): Float4Member<S> = getFloat4(indexOf(member))

fun <S: Struct> S.getInt1(member: String): Int1Member<S> = getInt1(indexOf(member))
fun <S: Struct> S.getInt2(member: String): Int2Member<S> = getInt2(indexOf(member))
fun <S: Struct> S.getInt3(member: String): Int3Member<S> = getInt3(indexOf(member))
fun <S: Struct> S.getInt4(member: String): Int4Member<S> = getInt4(indexOf(member))

fun <S: Struct> S.getMat2(member: String): Mat2Member<S> = getMat2(indexOf(member))
fun <S: Struct> S.getMat3(member: String): Mat3Member<S> = getMat3(indexOf(member))
fun <S: Struct> S.getMat4(member: String): Mat4Member<S> = getMat4(indexOf(member))

fun <S: Struct> S.getFloat1Array(member: String): Float1ArrayMember<S> = getFloat1Array(indexOf(member))
fun <S: Struct> S.getFloat2Array(member: String): Float2ArrayMember<S> = getFloat2Array(indexOf(member))
fun <S: Struct> S.getFloat3Array(member: String): Float3ArrayMember<S> = getFloat3Array(indexOf(member))
fun <S: Struct> S.getFloat4Array(member: String): Float4ArrayMember<S> = getFloat4Array(indexOf(member))

fun <S: Struct> S.getInt1Array(member: String): Int1ArrayMember<S> = getInt1Array(indexOf(member))
fun <S: Struct> S.getInt2Array(member: String): Int2ArrayMember<S> = getInt2Array(indexOf(member))
fun <S: Struct> S.getInt3Array(member: String): Int3ArrayMember<S> = getInt3Array(indexOf(member))
fun <S: Struct> S.getInt4Array(member: String): Int4ArrayMember<S> = getInt4Array(indexOf(member))

fun <S: Struct> S.getMat2Array(member: String): Mat2ArrayMember<S> = getMat2Array(indexOf(member))
fun <S: Struct> S.getMat3Array(member: String): Mat3ArrayMember<S> = getMat3Array(indexOf(member))
fun <S: Struct> S.getMat4Array(member: String): Mat4ArrayMember<S> = getMat4Array(indexOf(member))

fun <S: Struct, N: Struct> S.getStruct(member: String): NestedStructMember<S, N> = getStruct(indexOf(member))
fun <S: Struct, N: Struct> S.getStructArray(member: String): NestedStructArrayMember<S, N> = getStructArray(indexOf(member))


fun <T: Struct> MutableStructBufferView<T>.getFloat1(struct: T, memberIndex: Int) = get(struct.getFloat1(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getFloat2(struct: T, memberIndex: Int) = get(struct.getFloat2(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getFloat3(struct: T, memberIndex: Int) = get(struct.getFloat3(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getFloat4(struct: T, memberIndex: Int) = get(struct.getFloat4(memberIndex))

fun <T: Struct> MutableStructBufferView<T>.getInt1(struct: T, memberIndex: Int) = get(struct.getInt1(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getInt2(struct: T, memberIndex: Int) = get(struct.getInt2(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getInt3(struct: T, memberIndex: Int) = get(struct.getInt3(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getInt4(struct: T, memberIndex: Int) = get(struct.getInt4(memberIndex))

fun <T: Struct> MutableStructBufferView<T>.getMat2(struct: T, memberIndex: Int) = get(struct.getMat2(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getMat3(struct: T, memberIndex: Int) = get(struct.getMat3(memberIndex))
fun <T: Struct> MutableStructBufferView<T>.getMat4(struct: T, memberIndex: Int) = get(struct.getMat4(memberIndex))

fun <T: Struct> MutableStructBufferView<T>.getFloat1Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat1Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getFloat2Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat2Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getFloat3Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat3Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getFloat4Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getFloat4Array(memberIndex), arrayIndex)

fun <T: Struct> MutableStructBufferView<T>.getInt1Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getInt1Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getInt2Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getInt2Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getInt3Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getInt3Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getInt4Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getInt4Array(memberIndex), arrayIndex)

fun <T: Struct> MutableStructBufferView<T>.getMat2Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getMat2Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getMat3Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getMat3Array(memberIndex), arrayIndex)
fun <T: Struct> MutableStructBufferView<T>.getMat4Array(struct: T, memberIndex: Int, arrayIndex: Int) = get(struct.getMat4Array(memberIndex), arrayIndex)


fun <T: Struct> MutableStructBufferView<T>.setFloat1(struct: T, memberIndex: Int, value: Float) = set(struct.getFloat1(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat2(struct: T, memberIndex: Int, value: Vec2f) = set(struct.getFloat2(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat3(struct: T, memberIndex: Int, value: Vec3f) = set(struct.getFloat3(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat4(struct: T, memberIndex: Int, value: Vec4f) = set(struct.getFloat4(memberIndex), value)

fun <T: Struct> MutableStructBufferView<T>.setInt1(struct: T, memberIndex: Int, value: Int) = set(struct.getInt1(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setInt2(struct: T, memberIndex: Int, value: Vec2i) = set(struct.getInt2(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setInt3(struct: T, memberIndex: Int, value: Vec3i) = set(struct.getInt3(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setInt4(struct: T, memberIndex: Int, value: Vec4i) = set(struct.getInt4(memberIndex), value)

fun <T: Struct> MutableStructBufferView<T>.setMat2(struct: T, memberIndex: Int, value: Mat2f) = set(struct.getMat2(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setMat3(struct: T, memberIndex: Int, value: Mat3f) = set(struct.getMat3(memberIndex), value)
fun <T: Struct> MutableStructBufferView<T>.setMat4(struct: T, memberIndex: Int, value: Mat4f) = set(struct.getMat4(memberIndex), value)

fun <T: Struct> MutableStructBufferView<T>.setFloat1Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Float) = set(struct.getFloat1Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat2Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec2f) = set(struct.getFloat2Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat3Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec3f) = set(struct.getFloat3Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat4Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec4f) = set(struct.getFloat4Array(memberIndex), arrayIdnex, value)

fun <T: Struct> MutableStructBufferView<T>.setInt1Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Int) = set(struct.getInt1Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt2Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec2i) = set(struct.getInt2Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt3Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec3i) = set(struct.getInt3Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt4Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Vec4i) = set(struct.getInt4Array(memberIndex), arrayIdnex, value)

fun <T: Struct> MutableStructBufferView<T>.setMat2Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Mat2f) = set(struct.getMat2Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setMat3Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Mat3f) = set(struct.getMat3Array(memberIndex), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setMat4Array(struct: T, memberIndex: Int, arrayIdnex: Int, value: Mat4f) = set(struct.getMat4Array(memberIndex), arrayIdnex, value)


fun <T: Struct> MutableStructBufferView<T>.setFloat1(struct: T, memberName: String, value: Float) = set(struct.getFloat1(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat2(struct: T, memberName: String, value: Vec2f) = set(struct.getFloat2(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat3(struct: T, memberName: String, value: Vec3f) = set(struct.getFloat3(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setFloat4(struct: T, memberName: String, value: Vec4f) = set(struct.getFloat4(memberName), value)

fun <T: Struct> MutableStructBufferView<T>.setInt1(struct: T, memberName: String, value: Int) = set(struct.getInt1(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setInt2(struct: T, memberName: String, value: Vec2i) = set(struct.getInt2(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setInt3(struct: T, memberName: String, value: Vec3i) = set(struct.getInt3(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setInt4(struct: T, memberName: String, value: Vec4i) = set(struct.getInt4(memberName), value)

fun <T: Struct> MutableStructBufferView<T>.setMat2(struct: T, memberName: String, value: Mat2f) = set(struct.getMat2(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setMat3(struct: T, memberName: String, value: Mat3f) = set(struct.getMat3(memberName), value)
fun <T: Struct> MutableStructBufferView<T>.setMat4(struct: T, memberName: String, value: Mat4f) = set(struct.getMat4(memberName), value)

fun <T: Struct> MutableStructBufferView<T>.setFloat1Array(struct: T, memberName: String, arrayIdnex: Int, value: Float) = set(struct.getFloat1Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat2Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec2f) = set(struct.getFloat2Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat3Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec3f) = set(struct.getFloat3Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setFloat4Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec4f) = set(struct.getFloat4Array(memberName), arrayIdnex, value)

fun <T: Struct> MutableStructBufferView<T>.setInt1Array(struct: T, memberName: String, arrayIdnex: Int, value: Int) = set(struct.getInt1Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt2Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec2i) = set(struct.getInt2Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt3Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec3i) = set(struct.getInt3Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setInt4Array(struct: T, memberName: String, arrayIdnex: Int, value: Vec4i) = set(struct.getInt4Array(memberName), arrayIdnex, value)

fun <T: Struct> MutableStructBufferView<T>.setMat2Array(struct: T, memberName: String, arrayIdnex: Int, value: Mat2f) = set(struct.getMat2Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setMat3Array(struct: T, memberName: String, arrayIdnex: Int, value: Mat3f) = set(struct.getMat3Array(memberName), arrayIdnex, value)
fun <T: Struct> MutableStructBufferView<T>.setMat4Array(struct: T, memberName: String, arrayIdnex: Int, value: Mat4f) = set(struct.getMat4Array(memberName), arrayIdnex, value)
