package de.fabmax.kool.math

import kotlin.math.*


/**
 * Partitions items with the given comparator. After partitioning, all elements left of k are smaller
 * than all elements right of k with respect to the given comparator function.
 *
 * This method implements the Floyd-Rivest selection algorithm:
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Rivest_algorithm
 */
fun <T> MutableList<T>.partition(k: Int, cmp: (T, T) -> Int) = partition(indices, k, cmp)

fun <T> MutableList<T>.partition(rng: IntRange, k: Int, cmp: (T, T) -> Int) {
    var left = rng.first
    var right = rng.last
    while (right > left) {
        if (right - left > 600) {
            val n = right - left + 1
            val i = k - left + 1
            val z = ln(n.toDouble())
            val s = 0.5 * exp(2.0 * z / 3.0)
            val sd = 0.5 * sqrt(z * s * (n - s) / n) * sign(i - n / 2.0)
            val newLeft = max(left, (k - i * s / n + sd).toInt())
            val newRight = min(right, (k + (n - i) * s / n + sd).toInt())
            partition(newLeft..newRight, k, cmp)
        }
        val t = get(k)
        var i = left
        var j = right
        swap(left, k)
        if (cmp(get(right), t) > 0) {
            swap(right, left)
        }
        while (i < j) {
            swap( i, j)
            i++
            j--
            while (cmp(get(i), t) < 0) {
                i++
            }
            while (cmp(get(j), t) > 0) {
                j--
            }
        }
        if (cmp(get(left), t) == 0) {
            swap(left, j)
        } else {
            j++
            swap(j, right)
        }
        if (j <= k) {
            left = j + 1
        }
        if (k <= j) {
            right = j - 1
        }
    }
}

private fun <T> MutableList<T>.swap(a: Int, b: Int) {
    this[a] = this[b].also { this[b] = this[a] }
}