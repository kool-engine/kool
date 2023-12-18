package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputeShaderCode
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.backend.vk.pipeline.ShaderStage
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10

class ShaderCodeImplVk(val vkCode: VkCode): ShaderCode, ComputeShaderCode {

    constructor(vararg stages: ShaderStage): this(VkCode(stages.asList()))

    override val hash: LongHash = LongHash().apply { this += vkCode.hash }

    val vkStages: List<ShaderStage>
        get() = vkCode.stages

    class VkCode(val stages: List<ShaderStage>) {
        val hash = LongHash()
        init {
            stages.forEach {
                hash += it.hash
            }
        }
    }

    companion object {
        fun vkCodeFromSource(vertShaderSrc: String, fragShaderSrc: String): ShaderCodeImplVk {
            try {
                val vertShaderCode = ShaderStage.fromSource("vertShader", vertShaderSrc, VK10.VK_SHADER_STAGE_VERTEX_BIT)
                val fragShaderCode = ShaderStage.fromSource("fragShader", fragShaderSrc, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
                logD("ShaderCode") { "Successfully compiled shader: vertShader: ${vertShaderCode.code.size} bytes, fragShader: ${fragShaderCode.code.size} bytes" }
                return ShaderCodeImplVk(vertShaderCode, fragShaderCode)
            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                dumpCode(listOf(
                    "Vertex shader" to vertShaderSrc,
                    "Fragment shader" to fragShaderSrc
                ))
                throw RuntimeException(e)
            }
        }

        fun vkComputeCodeFromSource(computeShaderSrc: String): ShaderCodeImplVk {
            try {
                val computeShaderCode = ShaderStage.fromSource("computeShader", computeShaderSrc, VK10.VK_SHADER_STAGE_COMPUTE_BIT)
                logD("ShaderCode") { "Successfully compiled compute shader: ${computeShaderCode.code.size} bytes" }
                return ShaderCodeImplVk(computeShaderCode)
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
}
