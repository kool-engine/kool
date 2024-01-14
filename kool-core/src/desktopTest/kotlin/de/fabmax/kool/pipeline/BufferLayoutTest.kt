package de.fabmax.kool.pipeline

import org.junit.Test
import kotlin.test.assertEquals

class BufferLayoutTest {
    @Test
    fun testStd140Layout() {
        val uniforms = listOf(
            Uniform.float1("floatVal"),
            Uniform.float3("vec3Val"),
            Uniform.mat4("mat4Val"),
            Uniform.float1Array("floatArrayVal", 3),
            Uniform.int1("intVal")
        )

        val std140Layout = Std140BufferLayout(uniforms)
        assertEquals(0, std140Layout.uniformPositions["floatVal"]!!.byteIndex)
        assertEquals(16, std140Layout.uniformPositions["vec3Val"]!!.byteIndex)
        assertEquals(32, std140Layout.uniformPositions["mat4Val"]!!.byteIndex)
        assertEquals(96, std140Layout.uniformPositions["floatArrayVal"]!!.byteIndex)
        assertEquals(144, std140Layout.uniformPositions["intVal"]!!.byteIndex)
    }
}