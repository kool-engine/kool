package de.fabmax.kool.util

import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized

class ConcurrentBuffer<T> {
    @PublishedApi
    internal val inputBuffer = mutableListOf<T>()
    @PublishedApi
    internal val outputBuffer = mutableListOf<T>()
    @PublishedApi
    internal val lock = SynchronizedObject()

    fun isEmpty(): Boolean = inputBuffer.isEmpty()
    fun isNotEmpty(): Boolean = inputBuffer.isNotEmpty()

    inline fun consumeAll(block: (List<T>) -> Unit) {
        synchronized(lock) {
            outputBuffer.addAll(inputBuffer)
            inputBuffer.clear()
        }
        block(outputBuffer)
        outputBuffer.clear()
    }

    fun add(element: T) {
        synchronized(lock) {
            inputBuffer.add(element)
        }
    }
}