package de.fabmax.kool.pipeline

import org.junit.Test
import kotlin.test.assertEquals

class BufferLayoutTest {
    @Test
    fun testStd140Layout() {
        val uniforms = listOf(
            uniform1f("floatVal"),
            uniform3f("vec3Val"),
            uniformMat4("mat4Val"),
            uniform1fv("floatArrayVal", 3),
            uniform1i("intVal")
        )

        val std140Layout = Std140BufferLayout(uniforms)
        assertEquals(0, std140Layout.uniformPositions["floatVal"]!!.byteIndex)
        assertEquals(16, std140Layout.uniformPositions["vec3Val"]!!.byteIndex)
        assertEquals(32, std140Layout.uniformPositions["mat4Val"]!!.byteIndex)
        assertEquals(96, std140Layout.uniformPositions["floatArrayVal"]!!.byteIndex)
        assertEquals(144, std140Layout.uniformPositions["intVal"]!!.byteIndex)
    }
}