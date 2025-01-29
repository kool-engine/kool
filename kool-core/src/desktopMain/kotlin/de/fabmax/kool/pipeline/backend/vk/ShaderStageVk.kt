package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.LongHash
import org.lwjgl.vulkan.VK10.*
import java.io.InputStream

class ShaderStageVk(val name: String, val code: ByteArray, val stage: Int, val entryPoint: String = "main") {

    val hash = LongHash {
        this += stage
        this += entryPoint
        for (b in code) {
            this += b.toInt()
        }
    }

    companion object {
        fun fromSource(name: String, srcCode: InputStream, stage: Int, entryPoint: String = "main"): ShaderStageVk {
            return fromSource(name, String(srcCode.readBytes()), stage, entryPoint)
        }

        fun fromSource(name: String, srcCode: String, stage: Int, entryPoint: String = "main"): ShaderStageVk {
            val compileResult = when (stage) {
                VK_SHADER_STAGE_VERTEX_BIT -> Shaderc.compileVertexShader(srcCode, name, entryPoint)
                VK_SHADER_STAGE_FRAGMENT_BIT -> Shaderc.compileFragmentShader(srcCode, name, entryPoint)
                VK_SHADER_STAGE_COMPUTE_BIT -> Shaderc.compileComputeShader(srcCode, name, entryPoint)
                else -> error("Invalid shader stage: $stage")
            }
            val data = checkNotNull(compileResult.spirvData)  { "Shader compilation failed" }
            val codeArray = ByteArray(data.remaining())
            data.get(codeArray)
            compileResult.free()
            return ShaderStageVk(name, codeArray, stage, entryPoint)
        }
    }
}
