package de.fabmax.kool.pipeline

import de.fabmax.kool.platform.vk.pipeline.ShaderStage
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10

class ShaderCodeImpl private constructor(private val vkCode: VkCode?, private val glCode: GlCode?): ShaderCode {

    constructor(vararg stages: ShaderStage): this(VkCode(stages.asList()), null)
    constructor(vkCode: VkCode): this(vkCode, null)
    constructor(glCode: GlCode): this(null, glCode)

    override val longHash: Long = vkCode?.longHash?.toLong() ?: glCode!!.longHash.toLong()

    val vkStages: List<ShaderStage>
        get() = vkCode!!.stages

    val glVertexSrc: String
        get() = glCode!!.vertexSrc
    val glFragmentSrc: String
        get() = glCode!!.fragmentSrc

    class GlCode(val vertexSrc: String, val fragmentSrc: String) {
        val longHash = (vertexSrc.hashCode().toULong() shl 32) + fragmentSrc.hashCode().toULong()
    }

    class VkCode(val stages: List<ShaderStage>) {
        val longHash : ULong
        init {
            var hash = 0UL
            stages.forEach { hash = (hash * 71023UL) xor it.longHash }
            longHash = hash
        }
    }

    companion object {
        fun vkCodeFromSource(vertShaderSrc: String, fragShaderSrc: String): ShaderCode {
            try {
                val vertShaderCode = ShaderStage.fromSource("vertShader", vertShaderSrc, VK10.VK_SHADER_STAGE_VERTEX_BIT)
                val fragShaderCode = ShaderStage.fromSource("fragShader", fragShaderSrc, VK10.VK_SHADER_STAGE_FRAGMENT_BIT)
                logD("ShaderCode") { "Successfully compiled shader: vertShader: ${vertShaderCode.code.size} bytes, fragShader: ${fragShaderCode.code.size} bytes" }
                return ShaderCodeImpl(vertShaderCode, fragShaderCode)
            } catch (e: Exception) {
                logE { "Compilation failed: $e" }
                dumpCode(vertShaderSrc, fragShaderSrc)
                throw RuntimeException(e)
            }
        }

        fun glCodeFromSource(vertexShaderSource: String, fraqmentShaderSource: String): ShaderCode {
            return ShaderCodeImpl(GlCode(vertexShaderSource, fraqmentShaderSource))
        }

        fun dumpCode(vertShader: String, fragShader: String) {
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
