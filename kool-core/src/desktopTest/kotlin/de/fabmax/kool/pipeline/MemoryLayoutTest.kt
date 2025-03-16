package de.fabmax.kool.pipeline

import de.fabmax.kool.util.*
import org.junit.Test
import kotlin.test.assertEquals

class MemoryLayoutTest {
    @Test
    fun testStd140Layout() {
        val struct = DynamicStruct("test", MemoryLayout.Std140) {
            float1("floatVal")
            float3("vec3Val")
            mat4("mat4Val")
            float1Array("floatArrayVal", 3)
            int1("intVal")
        }

        assertEquals(0, struct.getFloat1("floatVal").byteOffset)
        assertEquals(16, struct.getFloat3("vec3Val").byteOffset)
        assertEquals(32, struct.getMat4("mat4Val").byteOffset)
        assertEquals(96, struct.getFloat1Array("floatArrayVal").byteOffset)
        assertEquals(144, struct.getInt1("intVal").byteOffset)
    }
}