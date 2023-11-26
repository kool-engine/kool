package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.backend.vk.pipeline.ShaderStage
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10

class ShaderCodeImplVk(val vkCode: VkCode): ShaderCode {

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
        fun vkCodeFromSource(vertShaderSrc: String, fragShaderSrc: String): ShaderCode {
            try {
                val vertShaderCode = ShaderStage.fromSource("vertShader", vertShaderSrc, VK10.VK_SHADER_STAGE_VERTEX_BIT)
                val fragShaderCode = ShaderStage.fromSource("fragShader", fragShaderSrc, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
                logD("ShaderCode") { "Successfully compiled shader: vertShader: ${vertShaderCode.code.size} bytes, fragShader: ${fragShaderCode.code.size} bytes" }
                return ShaderCodeImplVk(vertShaderCode, fragShaderCode)
            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                dumpCode(vertShaderSrc, fragShaderSrc)
                throw RuntimeException(e)
            }
        }

        private fun dumpCode(vertShader: String, fragShader: String) {
            println("Vertex shader:\n\n")
            vertShader.lines().forEachIndexed { i, l ->
                println(String.format("%3d: %s", i+1, l))
            }
            println("Fragment shader:\n\n")
            fragShader.lines().forEachIndexed { i, l ->
                println(String.format("%3d: %s", i+1, l))
            }
        }
    }
}
