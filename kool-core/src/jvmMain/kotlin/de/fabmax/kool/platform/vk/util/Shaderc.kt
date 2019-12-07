package de.fabmax.kool.platform.vk.util

import de.fabmax.kool.util.PerfTimer
import de.fabmax.kool.util.logE
import org.lwjgl.util.shaderc.Shaderc
import java.io.FileReader
import java.nio.ByteBuffer

fun main() {
    val compiler = Shaderc.shaderc_compiler_initialize()

    val srcTxt = FileReader("shaders/shader.vert").readText()

    val perf = PerfTimer()
    val result = Shaderc.shaderc_compile_into_spv(compiler, srcTxt, Shaderc.shaderc_vertex_shader, "shader.vert", "main", 0L)
    val status = Shaderc.shaderc_result_get_compilation_status(result)
    println("compilation finished in ${perf.takeSecs()} s, status: $status")

    val buf = Shaderc.shaderc_result_get_bytes(result)!!
    println("result size: ${buf.remaining()}")

    Shaderc.shaderc_compiler_release(compiler)
}

object Shaderc {
    private val compiler = Shaderc.shaderc_compiler_initialize()

    fun compileVertexShader(src: String, name: String = "shader.vert", entryPoint: String = "main") =
        compileShader(src, Shaderc.shaderc_vertex_shader, name, entryPoint)

    fun compileFragmentShader(src: String, name: String = "shader.frag", entryPoint: String = "main") =
        compileShader(src, Shaderc.shaderc_fragment_shader, name, entryPoint)

    private fun compileShader(src: String, shaderKind: Int, fName: String, entryPoint: String): CompileResult {
        val result = Shaderc.shaderc_compile_into_spv(compiler, src, shaderKind, fName, entryPoint, 0L)

        val status = Shaderc.shaderc_result_get_compilation_status(result)
        if (status != 0) {
            logE { "Failed to compile shader $fName ($status):\n${Shaderc.shaderc_result_get_error_message(result)}" }
        }

        return CompileResult(result)
    }

    class CompileResult(private val compilationResult: Long) {
        val compilationStatus = Shaderc.shaderc_result_get_compilation_status(compilationResult)
        val spirvData: ByteBuffer?

        init {
            spirvData = if (compilationStatus == 0) {
                Shaderc.shaderc_result_get_bytes(compilationResult)!!
            } else {
                null
            }
        }

        fun free() {
            Shaderc.shaderc_result_release(compilationResult)
        }
    }
}
