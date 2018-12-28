package de.fabmax.kool.math

import kotlin.math.*


fun <T> MutableList<T>.partition(k: Int, cmp: (T, T) -> Int) = partition(indices, k, cmp)

fun <T> MutableList<T>.partition(rng: IntRange, k: Int, cmp: (T, T) -> Int) {
    partition(this, rng.first, rng.last, k, { get(it) }, cmp, { a, b -> this[a] = this[b].also { this[b] = this[a] } })
}

fun <T> Array<T>.partition(k: Int, cmp: (T, T) -> Int) = partition(indices, k, cmp)

fun <T> Array<T>.partition(rng: IntRange, k: Int, cmp: (T, T) -> Int) {
    partition(this, rng.first, rng.last, k, { get(it) }, cmp, { a, b -> this[a] = this[b].also { this[b] = this[a] } })
}

/**
 * Partitions items with the given comparator. After partitioning, all elements left of k are smaller
 * than all elements right of k with respect to the given comparator function.
 *
 * This method implements the Floyd-Rivest selection algorithm:
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Rivest_algorithm
 */
fun <L,T> partition(elems: L, lt: Int, rt: Int, k: Int, get: L.(Int) -> T, cmp: (T, T) -> Int, swap: L.(Int, Int) -> Unit) {
    var left = lt
    var right = rt
    while (right > left) {
        if (right - left > 600) {
            val n = right - left + 1
            val i = k - left + 1
            val z = ln(n.toDouble())
            val s = 0.5 * exp(2.0 * z / 3.0)
            val sd = 0.5 * sqrt(z * s * (n - s) / n) * sign(i - n / 2.0)
            val newLeft = max(left, (k - i * s / n + sd).toInt())
            val newRight = min(right, (k + (n - i) * s / n + sd).toInt())
            partition(elems, newLeft, newRight, k, get, cmp, swap)
        }
        val t = elems.get(k)
        var i = left
        var j = right
        elems.swap(left, k)
        if (cmp(elems.get(right), t) > 0) {
            elems.swap(right, left)
        }
        while (i < j) {
            elems.swap(i, j)
            i++
            j--
            while (cmp(elems.get(i), t) < 0) {
                i++
            }
            while (j >= 0 && cmp(elems.get(j), t) > 0) {
                j--
            }
        }
        if (cmp(elems.get(left), t) == 0) {
            elems.swap(left, j)
        } else {
            j++
            elems.swap(j, right)
        }
        if (j <= k) {
            left = j + 1
        }
        if (k <= j) {
            right = j - 1
        }
    }
}