package de.fabmax.kool.platform.webgl

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.PrimitiveType
import de.fabmax.kool.util.Usage
import org.khronos.webgl.WebGLProgram
import org.khronos.webgl.WebGLRenderingContext.Companion.ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.DYNAMIC_DRAW
import org.khronos.webgl.WebGLRenderingContext.Companion.ELEMENT_ARRAY_BUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.INT
import org.khronos.webgl.WebGLRenderingContext.Companion.LINES
import org.khronos.webgl.WebGLRenderingContext.Companion.POINTS
import org.khronos.webgl.WebGLRenderingContext.Companion.STATIC_DRAW
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE0
import org.khronos.webgl.WebGLRenderingContext.Companion.TRIANGLES
import org.khronos.webgl.WebGLRenderingContext.Companion.UNSIGNED_INT
import org.khronos.webgl.WebGLUniformLocation

class CompiledShader(val prog: WebGLProgram?, pipeline: Pipeline, val ctx: JsContext) {

    val attributeLocations = mutableMapOf<String, Int>()
    val uniformLocations = mutableMapOf<String, WebGLUniformLocation?>()
    val instances = mutableMapOf<Long, ShaderInstance>()

    private var firstUse = true

    init {
        pipeline.vertexLayout.bindings.forEach { bnd ->
            bnd.attributes.forEach { attr ->
                attributeLocations[attr.name] = attr.location
            }
        }
        pipeline.descriptorSetLayouts.forEach { set ->
            set.descriptors.forEach { desc ->
                if (desc is UniformBuffer) {
                    desc.uniforms.forEach { uniformLocations[it.name] = ctx.gl.getUniformLocation(prog, it.name) }
                } else {
                    // sampler (texture or cube map
                    uniformLocations[desc.name] = ctx.gl.getUniformLocation(prog, desc.name)
                }
            }
        }
        pipeline.pushConstantRanges.forEach { pcr ->
            pcr.pushConstants.forEach { pc ->
                // in WebGL push constants are mapped to regular uniforms
                uniformLocations[pc.name] = ctx.gl.getUniformLocation(prog, pc.name)
            }
        }
    }

    fun use() {
        ctx.gl.useProgram(prog)
        if (firstUse) {
            firstUse = false
            attributeLocations.values.forEach { loc -> ctx.gl.enableVertexAttribArray(loc) }
        }
    }

    fun bindInstance(cmd: DrawCommand): ShaderInstance {
        val pipelineInst = cmd.pipeline!!
        val inst = instances.getOrPut(pipelineInst.pipelineInstanceId) { ShaderInstance(cmd.mesh, pipelineInst) }
        inst.bindInstance(cmd)
        return inst
    }

    inner class ShaderInstance(val mesh: Mesh, pipeline: Pipeline) {
        private val pushConstants = mutableListOf<PushConstantRange>()
        private val ubos = mutableListOf<UniformBuffer>()
        private val textures = mutableListOf<TextureSampler>()
        private val cubeMaps = mutableListOf<CubeMapSampler>()
        private val mappings = mutableListOf<MappedUniform>()
        private val attributeBinders = mutableMapOf<String, AttributeOnLocation>()

        private var dataBufferF: BufferResource? = null
        private var dataBufferI: BufferResource? = null
        private var indexBuffer: BufferResource? = null

        private var nextTexUnit = TEXTURE0

        var numIndices = 0
        var indexType = 0
        var primitiveType = 0

        init {
            pipeline.descriptorSetLayouts.forEach { set ->
                set.descriptors.forEach { desc ->
                    when (desc) {
                        is UniformBuffer -> mapUbo(desc)
                        is TextureSampler -> mapTexture(desc)
                        is CubeMapSampler -> mapCubeMap(desc)
                        else -> TODO("$desc")
                    }
                }
            }
            pipeline.pushConstantRanges.forEach { pc ->
                mapPushConstants(pc)
            }
            ctx.engineStats.pipelineInstanceCreated(pipeline.pipelineHash.toLong())
        }

        private fun mapPushConstants(pc: PushConstantRange) {
            pushConstants.add(pc)
            pc.pushConstants.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]) }
        }

        private fun mapUbo(ubo: UniformBuffer) {
            ubos.add(ubo)
            ubo.uniforms.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]) }
        }

        private fun mapTexture(tex: TextureSampler) {
            textures.add(tex)
            mappings += MappedUniformTex2d(tex, nextTexUnit++, uniformLocations[tex.name])
        }

        private fun mapCubeMap(cubeMap: CubeMapSampler) {
            cubeMaps.add(cubeMap)
            mappings += MappedUniformCubeMap(cubeMap, nextTexUnit++, uniformLocations[cubeMap.name])
        }

        fun bindInstance(drawCmd: DrawCommand) {
            // call onUpdate callbacks
            for (i in pushConstants.indices) {
                pushConstants[i].onUpdate?.invoke(pushConstants[i], drawCmd)
            }
            for (i in ubos.indices) {
                ubos[i].onUpdate?.invoke(ubos[i], drawCmd)
            }
            for (i in textures.indices) {
                textures[i].onUpdate?.invoke(textures[i], drawCmd)
            }
            for (i in cubeMaps.indices) {
                cubeMaps[i].onUpdate?.invoke(cubeMaps[i], drawCmd)
            }

            // update unitform values
            for (i in mappings.indices) {
                mappings[i].setUniform(ctx)
            }

            // update vertex data
            checkBuffers()

            // bind vertex data
            indexBuffer?.bind(ctx)
            attributeBinders.values.forEach { it.vbo.bindAttribute(it.loc, ctx) }
        }

        private fun checkBuffers() {
            val md = mesh.geometry
            if (indexBuffer == null) {
                indexBuffer = BufferResource(ELEMENT_ARRAY_BUFFER, ctx)
            }
            var hasIntData = false
            if (dataBufferF == null) {
                dataBufferF = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        hasIntData = true
                    } else {
                        attributeLocations[vertexAttrib.glslSrcName]?.let { location ->
                            val vbo = VboBinder(dataBufferF!!, vertexAttrib.type.size / 4,
                                    md.strideBytesF, md.attributeOffsets[vertexAttrib]!! / 4, FLOAT)
                            attributeBinders[vertexAttrib.glslSrcName] = AttributeOnLocation(vbo, location)
                        }
                    }
                }
            }
            if (hasIntData && dataBufferI == null) {
                dataBufferI = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        attributeLocations[vertexAttrib.glslSrcName]?.let { location ->
                            val vbo = VboBinder(dataBufferI!!, vertexAttrib.type.size / 4,
                                    md.strideBytesI, md.attributeOffsets[vertexAttrib]!! / 4, INT)
                            attributeBinders[vertexAttrib.glslSrcName] = AttributeOnLocation(vbo, location)
                        }
                    }
                }
            }

            if (md.isSyncRequired && !md.isBatchUpdate) {
                if (md.isRebuildBoundsOnSync) {
                    md.rebuildBounds()
                }
                val usage = md.usage.glUsage()

                indexType = UNSIGNED_INT
                indexBuffer?.setData(md.indices, usage, ctx)

                primitiveType = md.primitiveType.glElemType()
                numIndices = md.numIndices
                dataBufferF?.setData(md.dataF, usage, ctx)
                dataBufferI?.setData(md.dataI, usage, ctx)
                md.isSyncRequired = false
            }
        }
    }

    private fun PrimitiveType.glElemType(): Int {
        return when (this) {
            PrimitiveType.LINES -> LINES
            PrimitiveType.POINTS -> POINTS
            PrimitiveType.TRIANGLES -> TRIANGLES
        }
    }

    private fun Usage.glUsage(): Int {
        return when (this) {
            Usage.DYNAMIC -> DYNAMIC_DRAW
            Usage.STATIC -> STATIC_DRAW
        }
    }

    private data class AttributeOnLocation(val vbo: VboBinder, val loc: Int)

    companion object {
        private var nextPipelineId = 1L
    }
}