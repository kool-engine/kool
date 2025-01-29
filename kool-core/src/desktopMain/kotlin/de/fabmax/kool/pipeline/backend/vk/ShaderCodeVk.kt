package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputeShaderCode
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10.*

class ShaderCodeVk(val stages: List<ShaderStageVk>): ShaderCode, ComputeShaderCode {

    val vertexStage: ShaderStageVk? = stages.find { it.stage == VK_SHADER_STAGE_VERTEX_BIT }
    val fragmentStage: ShaderStageVk? = stages.find { it.stage == VK_SHADER_STAGE_FRAGMENT_BIT }
    val computeStage: ShaderStageVk? = stages.find { it.stage == VK_SHADER_STAGE_COMPUTE_BIT }

    override val hash: LongHash = LongHash {
        stages.forEach { this += it.hash }
    }

    companion object {
        private val shaderCache = mutableMapOf<ShaderKey, ShaderStageVk>()

        fun drawShaderCode(vertShaderSrc: String, fragShaderSrc: String): ShaderCodeVk {
            try {
                val vertexStage = shaderCache.getOrPut(ShaderKey(vertShaderSrc, VK_SHADER_STAGE_VERTEX_BIT)) {
                    ShaderStageVk.fromSource("vertShader", vertShaderSrc, VK_SHADER_STAGE_VERTEX_BIT)
                }
                val fragmentStage = shaderCache.getOrPut(ShaderKey(fragShaderSrc, VK_SHADER_STAGE_FRAGMENT_BIT)) {
                    ShaderStageVk.fromSource("fragShader", fragShaderSrc, VK_SHADER_STAGE_FRAGMENT_BIT)
                }
                return ShaderCodeVk(listOf(vertexStage, fragmentStage))
            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                dumpCode(listOf("Vertex shader" to vertShaderSrc, "Fragment shader" to fragShaderSrc))
                throw RuntimeException(e)
            }
        }

        fun computeShaderCode(computeShaderSrc: String): ShaderCodeVk {
            try {
                val computeStage = shaderCache.getOrPut(ShaderKey(computeShaderSrc, VK_SHADER_STAGE_COMPUTE_BIT)) {
                    ShaderStageVk.fromSource("computeShader", computeShaderSrc, VK_SHADER_STAGE_COMPUTE_BIT)
                }
                return ShaderCodeVk(listOf(computeStage))
            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                dumpCode(listOf("Compute shader" to computeShaderSrc))
                throw RuntimeException(e)
            }
        }

        private fun dumpCode(sources: List<Pair<String, String>>) {
            sources.forEach { (name, source) ->
                println("$name:\n\n")
                source.lines().forEachIndexed { i, l ->
                    println(String.format("%3d: %s", i+1, l))
                }
            }
        }
    }

    private data class ShaderKey(val src: String, val stageBit: Int)
}
