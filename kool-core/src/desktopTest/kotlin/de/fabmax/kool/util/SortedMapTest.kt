package de.fabmax.kool.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SortedMapTest {

    @Test
    fun `test insertion`() {
        val map = SortedMap<Int, Int>()

        map[17] = 17
        assertEquals(17, map[17])
        map[17] = 18
        assertEquals(18, map[17])
        assertEquals(1, map.size)

        map.putAll(mapOf(
            3 to 3,
            9 to 9,
            17 to 19,
            5 to 5
        ))
        assertEquals(4, map.size)
        assertTrue(listOf(3, 9, 17, 5).all { map.containsKey(it) })
    }

    @Test
    fun `test sorted`() {
        val map = makeMap(17, 3, 8, 29, 12, 14, 8)
        val keys = map.keys.toList().sorted()
        var prev = keys[0]
        for (i in 1 until keys.size) {
            assertTrue(prev < keys[i])
            prev = keys[i]
        }
    }

    @Test
    fun `test key set removal`() {
        val map = makeMap(17, 3, 8, 29, 12, 14)

        val keys = map.keys
        assertTrue(keys.all { map.containsKey(it) })
        assertEquals(keys.size, map.size)

        keys.remove(17)
        assertEquals(false, map.containsKey(17))
        assertEquals(5, map.size)

        keys.removeAll(listOf(29, 12))
        assertEquals(false, map.containsKey(29))
        assertEquals(false, map.containsKey(12))
        assertEquals(3, map.size)

        keys.retainAll(listOf(3, 8))
        assertEquals(true, map.containsKey(3))
        assertEquals(true, map.containsKey(8))
        assertEquals(2, map.size)

        keys.clear()
        assertEquals(0, map.size)
    }

    @Test
    fun `test key set iterator`() {
        val map = makeMap(17, 3, 8, 29, 12, 14)
        val keys = map.keys
        val it = keys.iterator()
        while (it.hasNext()) {
            val key = it.next()
            if (key % 2 == 0) {
                it.remove()
            }
        }
        assertEquals(3, map.size)
        assertTrue(map.containsKey(17))
        assertTrue(map.containsKey(3))
        assertTrue(map.containsKey(29))
    }

    @Test
    fun `test value iterator`() {
        val map = makeMap(17, 3, 8, 29, 12, 14)
        val values = map.values
        val it = values.iterator()
        while (it.hasNext()) {
            val value = it.next()
            if (value % 2 == 0) {
                it.remove()
            }
        }
        assertEquals(3, map.size)
        assertTrue(map.containsKey(17))
        assertTrue(map.containsKey(3))
        assertTrue(map.containsKey(29))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun `test key set fails on add`() {
        val map = makeMap(17, 3, 8, 29, 12, 14)
        map.keys += 12
    }

    @Test
    fun `test first and last entries`() {
        val map = makeMap(1 to 4, 2 to 5, 3 to 6)

        assertEquals(1, map.firstKey())
        assertEquals(4, map.firstValue())
        assertEquals(3, map.lastKey())
        assertEquals(6, map.lastValue())
    }

    @Test
    fun `test lower entry`() {
        val map = makeMap(1 to 2, 3 to 6, 10 to 20)

        assertEquals(10, map.lowerKey(100))
        assertEquals(3, map.lowerKey(10))
        assertEquals(1, map.lowerKey(2))
        assertEquals(null, map.lowerKey(1))
        assertEquals(null, map.lowerKey(0))

        assertEquals(20, map.lowerValue(100))
        assertEquals(6, map.lowerValue(10))
        assertEquals(2, map.lowerValue(2))
        assertEquals(null, map.lowerValue(1))
        assertEquals(null, map.lowerValue(0))
    }

    @Test
    fun `test higher entry`() {
        val map = makeMap(1 to 2, 3 to 6, 10 to 20)

        assertEquals(null, map.higherKey(100))
        assertEquals(null, map.higherKey(10))
        assertEquals(10, map.higherKey(3))
        assertEquals(3, map.higherKey(2))
        assertEquals(1, map.higherKey(0))

        assertEquals(null, map.higherValue(100))
        assertEquals(null, map.higherValue(10))
        assertEquals(20, map.higherValue(3))
        assertEquals(6, map.higherValue(2))
        assertEquals(2, map.higherValue(0))
    }

    @Test
    fun `test floor entry`() {
        val map = makeMap(1 to 2, 3 to 6, 10 to 20)

        assertEquals(10, map.floorKey(100))
        assertEquals(10, map.floorKey(10))
        assertEquals(1, map.floorKey(2))
        assertEquals(1, map.floorKey(1))
        assertEquals(null, map.floorKey(0))

        assertEquals(20, map.floorValue(100))
        assertEquals(20, map.floorValue(10))
        assertEquals(2, map.floorValue(2))
        assertEquals(2, map.floorValue(1))
        assertEquals(null, map.floorValue(0))
    }

    @Test
    fun `test ceiling entry`() {
        val map = makeMap(1 to 2, 3 to 6, 10 to 20)

        assertEquals(null, map.ceilingKey(100))
        assertEquals(10, map.ceilingKey(10))
        assertEquals(3, map.ceilingKey(3))
        assertEquals(3, map.ceilingKey(2))
        assertEquals(1, map.ceilingKey(0))

        assertEquals(null, map.ceilingValue(100))
        assertEquals(20, map.ceilingValue(10))
        assertEquals(6, map.ceilingValue(3))
        assertEquals(6, map.ceilingValue(2))
        assertEquals(2, map.ceilingValue(0))
    }

    private fun makeMap(vararg ints: Int): SortedMap<Int, Int> {
        val map = SortedMap<Int, Int>()
        ints.forEach { map[it] = it }
        return map
    }

    private fun makeMap(vararg ints: Pair<Int, Int>): SortedMap<Int, Int> {
        val map = SortedMap<Int, Int>()
        ints.forEach { (k, v) -> map[k] = v }
        return map
    }
}