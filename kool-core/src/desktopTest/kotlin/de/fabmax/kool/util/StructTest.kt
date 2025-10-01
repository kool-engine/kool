package de.fabmax.kool.util

import de.fabmax.kool.math.Vec2f
import kotlin.test.Test
import kotlin.test.assertEquals

class StructTest {
    object TopLevel : Struct("top", MemoryLayout.TightlyPacked) {
        val pad = float3()
        val nested = struct(NestedStruct)
        val nestedArray = structArray(NestedStruct, 4)
    }

    object NestedStruct : Struct("nested", MemoryLayout.TightlyPacked) {
        val foo = float2()
    }

    @Test
    fun `get nested struct`() {
        val buffer = StructBuffer(TopLevel, 2)

        var i = 0
        repeat(2) { bufi ->
            buffer.set(bufi) {
                set(it.nested) { nit ->
                    set(nit.foo, Vec2f(i++.toFloat(), i++.toFloat()))
                }
                repeat(4) { j ->
                    set(it.nestedArray, j) { nit ->
                        set(nit.foo, Vec2f(i++.toFloat(), i++.toFloat()))
                    }
                }
            }
        }
        i = 0
        repeat(2) { bufi ->
            buffer.get(bufi) {
                get(it.nested) { nit ->
                    val f = get(nit.foo)
                    assertEquals(i++, f.x.toInt())
                    assertEquals(i++, f.y.toInt())
                }
                repeat(4) { j ->
                    get(it.nestedArray, j) { nit ->
                        val f = get(nit.foo)
                        assertEquals(i++, f.x.toInt())
                        assertEquals(i++, f.y.toInt())
                    }
                }
            }
        }
    }
}