@file:OptIn(ExperimentalAtomicApi::class)

package de.fabmax.kool.physics.util

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.reflect.KProperty

internal class SyncedFloat(initial: Float) {
    var writeBuffer = initial
    var readBuffer = initial
    val isDirty = AtomicBoolean(false)

    fun set(value: Float) {
        writeBuffer = value
        readBuffer = value
        isDirty.store(true)
    }

    inline fun writeIfDirty(block: (Float) -> Unit) {
        if (isDirty.exchange(false)) {
            block(writeBuffer)
        }
    }

    fun read(px: Float) {
        readBuffer = px
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Float = readBuffer
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) { set(value) }
}

internal class SyncedVec3(initial: Vec3f) {
    val writeBuffer = MutableVec3f(initial)
    val readBuffer = MutableVec3f(initial)
    val isDirty = AtomicBoolean(false)

    fun set(value: Vec3f) {
        writeBuffer.set(value)
        readBuffer.set(value)
        isDirty.store(true)
    }

    inline fun writeIfDirty(block: (Vec3f) -> Unit) {
        if (isDirty.exchange(false)) {
            block(writeBuffer)
        }
    }

    inline fun read(block: (MutableVec3f) -> Unit) = block(readBuffer)

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Vec3f = readBuffer
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Vec3f) { set(value) }
}