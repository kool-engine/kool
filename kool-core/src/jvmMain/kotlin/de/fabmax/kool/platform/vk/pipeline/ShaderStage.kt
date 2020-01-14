package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.KoolException
import de.fabmax.kool.platform.vk.util.Shaderc
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_FRAGMENT_BIT
import org.lwjgl.vulkan.VK10.VK_SHADER_STAGE_VERTEX_BIT
import java.io.InputStream

class ShaderStage(val name: String, val code: ByteArray, val stage: Int, val entryPoint: String = "main") {

    val longHash: ULong

    init {
        var hash = stage.toULong()
        hash = (hash * 31UL) xor entryPoint.hashCode().toULong()
        for (b in code) {
            hash = (hash * 31UL) xor b.toULong()
        }
        longHash = hash
    }

    companion object {
        fun fromSource(name: String, srcCode: InputStream, stage: Int, entryPoint: String = "main"): ShaderStage {
            return fromSource(name, String(srcCode.readBytes()), stage, entryPoint)
        }

        fun fromSource(name: String, srcCode: String, stage: Int, entryPoint: String = "main"): ShaderStage {
            val compileResult = when (stage) {
                VK_SHADER_STAGE_VERTEX_BIT -> Shaderc.compileVertexShader(srcCode, name, entryPoint)
                VK_SHADER_STAGE_FRAGMENT_BIT -> Shaderc.compileFragmentShader(srcCode, name, entryPoint)
                else -> throw IllegalArgumentException("Invalid shader stage: $stage")
            }
            val data = compileResult.spirvData ?: throw KoolException("Shader compilation failed")
            val codeArray = ByteArray(data.remaining())
            data.get(codeArray)
            compileResult.free()
            return ShaderStage(name, codeArray, stage, entryPoint)
        }
    }
}
