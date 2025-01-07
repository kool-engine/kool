package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.logE
import org.lwjgl.util.shaderc.Shaderc
import java.nio.ByteBuffer

object Shaderc {
    private val compiler = Shaderc.shaderc_compiler_initialize()

    fun compileVertexShader(src: String, name: String, entryPoint: String) =
        compileShader(src, Shaderc.shaderc_vertex_shader, name, entryPoint)

    fun compileFragmentShader(src: String, name: String, entryPoint: String) =
        compileShader(src, Shaderc.shaderc_fragment_shader, name, entryPoint)

    fun compileComputeShader(src: String, name: String, entryPoint: String) =
        compileShader(src, Shaderc.shaderc_compute_shader, name, entryPoint)

    private fun compileShader(src: String, shaderKind: Int, fName: String, entryPoint: String): CompileResult {
        val options = Shaderc.shaderc_compile_options_initialize()
        Shaderc.shaderc_compile_options_set_optimization_level(options, Shaderc.shaderc_optimization_level_performance)
        Shaderc.shaderc_compile_options_set_target_env(options, Shaderc.shaderc_target_env_vulkan, 0)

        val result = Shaderc.shaderc_compile_into_spv(compiler, src, shaderKind, fName, entryPoint, options)
        val status = Shaderc.shaderc_result_get_compilation_status(result)
        if (status != 0) {
            logE { "Failed to compile shader $fName ($status):\n${Shaderc.shaderc_result_get_error_message(result)}" }
        }

        Shaderc.shaderc_compile_options_release(options)
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
