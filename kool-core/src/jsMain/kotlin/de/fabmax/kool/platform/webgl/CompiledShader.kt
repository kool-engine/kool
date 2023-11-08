package de.fabmax.kool.platform.webgl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.INVALID_INDEX
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.UNIFORM_BLOCK_DATA_SIZE
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.UNIFORM_BUFFER
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.UNIFORM_OFFSET
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.scene.geometry.Usage
import de.fabmax.kool.util.logE
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

    private val pipelineId = pipeline.pipelineHash.toLong()

    private val attributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val instanceAttributes = mutableMapOf<String, VertexLayout.VertexAttribute>()
    private val uniformLocations = mutableMapOf<String, List<WebGLUniformLocation?>>()
    private val uboLayouts = mutableMapOf<String, BufferLayout>()
    private val instances = mutableMapOf<Long, ShaderInstance>()

    init {
        pipeline.layout.vertices.bindings.forEach { bnd ->
            bnd.vertexAttributes.forEach { attr ->
                when (bnd.inputRate) {
                    InputRate.VERTEX -> attributes[attr.attribute.name] = attr
                    InputRate.INSTANCE -> instanceAttributes[attr.attribute.name] = attr
                }
            }
        }
        pipeline.layout.descriptorSets.forEach { set ->
            set.descriptors.forEach { desc ->
                when (desc) {
                    is UniformBuffer -> {
                        val blockIndex = ctx.gl.getUniformBlockIndex(prog, desc.name)
                        if (blockIndex == INVALID_INDEX) {
                            // descriptor does not describe an actual UBO but plain old uniforms...
                            desc.uniforms.forEach { uniformLocations[it.name] = listOf(ctx.gl.getUniformLocation(prog, it.name)) }
                        } else {
                            setupUboLayout(desc, blockIndex)
                        }
                    }
                    is TextureSampler1d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSampler2d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSampler3d -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                    is TextureSamplerCube -> {
                        uniformLocations[desc.name] = getUniformLocations(desc.name, desc.arraySize)
                    }
                }
            }
        }
        pipeline.layout.pushConstantRanges.forEach { pcr ->
            pcr.pushConstants.forEach { pc ->
                // in WebGL push constants are mapped to regular uniforms
                uniformLocations[pc.name] = listOf(ctx.gl.getUniformLocation(prog, pc.name))
            }
        }
    }

    private fun setupUboLayout(desc: UniformBuffer, blockIndex: Int) {
        val bufferSize = ctx.gl.getActiveUniformBlockParameter(prog, blockIndex, UNIFORM_BLOCK_DATA_SIZE)
        val uniformNames = desc.uniforms.map {
            if (it.size > 1) "${it.name}[0]" else it.name
        }.toTypedArray()

        val indices = ctx.gl.getUniformIndices(prog, uniformNames)
        val offsets = ctx.gl.getActiveUniforms(prog, indices, UNIFORM_OFFSET)

        val sortedOffsets = offsets.sorted()
        val bufferPositions = Array(desc.uniforms.size) { i ->
            val off = offsets[i]
            val nextOffI = sortedOffsets.indexOf(off) + 1
            val nextOff = if (nextOffI < sortedOffsets.size) sortedOffsets[nextOffI] else bufferSize
            BufferPosition(off, nextOff - off)
        }

        ctx.gl.uniformBlockBinding(prog, blockIndex, desc.binding)
        uboLayouts[desc.name] = ExternalBufferLayout(desc.uniforms, bufferPositions, bufferSize)
    }

    private fun getUniformLocations(name: String, arraySize: Int): List<WebGLUniformLocation?> {
        val locations = mutableListOf<WebGLUniformLocation?>()
        if (arraySize > 1) {
            for (i in 0 until arraySize) {
                locations += ctx.gl.getUniformLocation(prog, "$name[$i]")
            }
        } else {
            locations += ctx.gl.getUniformLocation(prog, name)
        }
        return locations
    }

    fun use() {
        ctx.gl.useProgram(prog)
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                val location = attr.location + i
                ctx.gl.enableVertexAttribArray(location)
                ctx.gl.vertexAttribDivisor(location, 0)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                val location = attr.location + i
                ctx.gl.enableVertexAttribArray(location)
                ctx.gl.vertexAttribDivisor(location, 1)
            }
        }
    }

    fun unUse() {
        attributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                ctx.gl.disableVertexAttribArray(attr.location + i)
            }
        }
        instanceAttributes.values.forEach { attr ->
            for (i in 0 until attr.attribute.props.nSlots) {
                ctx.gl.disableVertexAttribArray(attr.location + i)
            }
        }
    }

    fun bindInstance(cmd: DrawCommand): ShaderInstance? {
        val pipelineInst = cmd.pipeline!!
        val inst = instances.getOrPut(pipelineInst.pipelineInstanceId) {
            ShaderInstance(cmd.geometry, cmd.mesh.instances, pipelineInst)
        }
        return if (inst.bindInstance(cmd)) { inst } else { null }
    }

    fun destroyInstance(pipeline: Pipeline) {
        instances.remove(pipeline.pipelineInstanceId)?.let {
            it.destroyInstance()
            ctx.engineStats.pipelineInstanceDestroyed(pipelineId)
        }
    }

    fun isEmpty(): Boolean = instances.isEmpty()

    fun destroy() {
        ctx.engineStats.pipelineDestroyed(pipelineId)
        ctx.gl.deleteProgram(prog)
    }

    inner class ShaderInstance(var geometry: IndexedVertexList, val instances: MeshInstanceList?, val pipeline: Pipeline) {
        private val pushConstants = mutableListOf<PushConstantRange>()
        private val ubos = mutableListOf<UniformBuffer>()
        private val textures1d = mutableListOf<TextureSampler1d>()
        private val textures2d = mutableListOf<TextureSampler2d>()
        private val textures3d = mutableListOf<TextureSampler3d>()
        private val texturesCube = mutableListOf<TextureSamplerCube>()
        private val mappings = mutableListOf<MappedUniform>()
        private val attributeBinders = mutableListOf<AttributeOnLocation>()
        private val instanceAttribBinders = mutableListOf<AttributeOnLocation>()

        private var dataBufferF: BufferResource? = null
        private var dataBufferI: BufferResource? = null
        private var indexBuffer: BufferResource? = null
        private var instanceBuffer: BufferResource? = null
        private var uboBuffers = mutableListOf<BufferResource>()
        private var buffersSet = false

        private var nextTexUnit = TEXTURE0

        var numIndices = 0
        var indexType = 0
        var primitiveType = 0

        init {
            pipeline.layout.descriptorSets.forEach { set ->
                set.descriptors.forEach { desc ->
                    when (desc) {
                        is UniformBuffer -> mapUbo(desc)
                        is TextureSampler1d -> mapTexture1d(desc)
                        is TextureSampler2d -> mapTexture2d(desc)
                        is TextureSampler3d -> mapTexture3d(desc)
                        is TextureSamplerCube -> mapTextureCube(desc)
                    }
                }
            }
            pipeline.layout.pushConstantRanges.forEach { pc ->
                mapPushConstants(pc)
            }
            ctx.engineStats.pipelineInstanceCreated(pipelineId)
        }

        private fun mapPushConstants(pc: PushConstantRange) {
            pushConstants.add(pc)
            pc.pushConstants.forEach { mappings += MappedUniform.mappedUniform(it, uniformLocations[it.name]?.get(0)) }
        }

        private fun mapUbo(ubo: UniformBuffer) {
            ubos.add(ubo)
            val uboLayout = uboLayouts[ubo.name]
            if (uboLayout != null) {
                mappings += MappedUbo(ubo, uboLayout)

            } else {
                ubo.uniforms.forEach {
                    val location = uniformLocations[it.name]
                    if (location != null) {
                        mappings += MappedUniform.mappedUniform(it, location[0])
                    } else {
                        logE { "Uniform location not present for uniform ${ubo.name}.${it.name}" }
                    }
                }
            }
        }

        private fun mapTexture1d(tex: TextureSampler1d) {
            textures1d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex1d(tex, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture2d(tex: TextureSampler2d) {
            textures2d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex2d(tex, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        private fun mapTexture3d(tex: TextureSampler3d) {
            textures3d.add(tex)
            uniformLocations[tex.name]?.let { locs ->
                mappings += MappedUniformTex3d(tex, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        private fun mapTextureCube(cubeMap: TextureSamplerCube) {
            texturesCube.add(cubeMap)
            uniformLocations[cubeMap.name]?.let { locs ->
                mappings += MappedUniformTexCube(cubeMap, nextTexUnit, locs)
                nextTexUnit += locs.size
            }
        }

        fun bindInstance(drawCmd: DrawCommand): Boolean {
            if (geometry !== drawCmd.geometry) {
                geometry = drawCmd.geometry
                destroyBuffers()
            }

            // call onUpdate callbacks
            for (i in pipeline.onUpdate.indices) {
                pipeline.onUpdate[i].invoke(drawCmd)
            }
            for (i in pushConstants.indices) {
                pushConstants[i].onUpdate?.invoke(pushConstants[i], drawCmd)
            }
            for (i in ubos.indices) {
                ubos[i].onUpdate?.invoke(ubos[i], drawCmd)
            }
            for (i in textures1d.indices) {
                textures1d[i].onUpdate?.invoke(textures1d[i], drawCmd)
            }
            for (i in textures2d.indices) {
                textures2d[i].onUpdate?.invoke(textures2d[i], drawCmd)
            }
            for (i in textures3d.indices) {
                textures3d[i].onUpdate?.invoke(textures3d[i], drawCmd)
            }
            for (i in texturesCube.indices) {
                texturesCube[i].onUpdate?.invoke(texturesCube[i], drawCmd)
            }

            // update buffers (vertex data, instance data, UBOs)
            checkBuffers()

            // bind uniform values
            var uniformsValid = true
            for (i in mappings.indices) {
                uniformsValid = uniformsValid && mappings[i].setUniform(ctx)
            }

            if (uniformsValid) {
                // bind vertex data
                indexBuffer?.bind(ctx)
                attributeBinders.forEach { it.vbo.bindAttribute(it.loc, ctx) }
                instanceAttribBinders.forEach { it.vbo.bindAttribute(it.loc, ctx) }
            }
            return uniformsValid
        }

        private fun destroyBuffers() {
            attributeBinders.clear()
            instanceAttribBinders.clear()
            dataBufferF?.delete(ctx)
            dataBufferI?.delete(ctx)
            indexBuffer?.delete(ctx)
            instanceBuffer?.delete(ctx)
            uboBuffers.forEach { it.delete(ctx) }
            dataBufferF = null
            dataBufferI = null
            indexBuffer = null
            instanceBuffer = null
            buffersSet = false
            uboBuffers.clear()
        }

        fun destroyInstance() {
            destroyBuffers()

            pushConstants.clear()
            ubos.clear()
            textures1d.clear()
            textures2d.clear()
            textures3d.clear()
            texturesCube.clear()
            mappings.clear()
        }

        private fun checkBuffers() {
            val md = geometry
            if (indexBuffer == null) {
                indexBuffer = BufferResource(ELEMENT_ARRAY_BUFFER, ctx)

                mappings.filterIsInstance<MappedUbo>().forEach { mappedUbo ->
                    val uboBuffer = BufferResource(UNIFORM_BUFFER, ctx)
                    uboBuffers += uboBuffer
                    mappedUbo.uboBuffer = uboBuffer
                }
            }
            var hasIntData = false
            if (dataBufferF == null) {
                dataBufferF = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        hasIntData = true
                    } else {
                        val stride = md.byteStrideF
                        val offset = md.attributeByteOffsets[vertexAttrib]!! / 4
                        attributeBinders += attributes.makeAttribBinders(vertexAttrib, dataBufferF!!, stride, offset)
                    }
                }
            }
            if (hasIntData && dataBufferI == null) {
                dataBufferI = BufferResource(ARRAY_BUFFER, ctx)
                for (vertexAttrib in md.vertexAttributes) {
                    if (vertexAttrib.type.isInt) {
                        attributes[vertexAttrib.name]?.let { attr ->
                            val vbo = VboBinder(dataBufferI!!, vertexAttrib.type.byteSize / 4,
                                    md.byteStrideI, md.attributeByteOffsets[vertexAttrib]!! / 4, INT)
                            attributeBinders += AttributeOnLocation(vbo, attr.location)
                        }
                    }
                }
            }

            val instanceList = instances
            if (instanceList != null) {
                var instBuf = instanceBuffer
                var isNewlyCreated = false
                if (instBuf == null) {
                    instBuf = BufferResource(ARRAY_BUFFER, ctx)
                    instanceBuffer = instBuf
                    isNewlyCreated = true
                    for (instanceAttrib in instanceList.instanceAttributes) {
                        val stride = instanceList.strideBytesF
                        val offset = instanceList.attributeOffsets[instanceAttrib]!! / 4
                        instanceAttribBinders += instanceAttributes.makeAttribBinders(instanceAttrib, instanceBuffer!!, stride, offset)
                    }
                }
                if (instanceList.hasChanged || isNewlyCreated) {
                    instBuf.setData(instanceList.dataF, instanceList.usage.glUsage(), ctx)
                    ctx.afterRenderActions += { instanceList.hasChanged = false }
                }
            }

            if (!md.isBatchUpdate && (md.hasChanged || !buffersSet)) {
                val usage = md.usage.glUsage()

                indexType = UNSIGNED_INT
                indexBuffer?.setData(md.indices, usage, ctx)

                primitiveType = pipeline.layout.vertices.primitiveType.glElemType()
                numIndices = md.numIndices
                dataBufferF?.setData(md.dataF, usage, ctx)
                dataBufferI?.setData(md.dataI, usage, ctx)

                // fixme: data buffers should be bound to mesh, not to shader instance
                // if mesh is rendered multiple times (e.g. by additional shadow passes), clearing
                // hasChanged flag early results in buffers not being updated
                ctx.afterRenderActions += { md.hasChanged = false }
                buffersSet = true
            }
        }
    }

    private fun Map<String, VertexLayout.VertexAttribute>.makeAttribBinders(attr: Attribute, buffer: BufferResource, stride: Int, offset: Int): List<AttributeOnLocation> {
        val binders = mutableListOf<AttributeOnLocation>()
        get(attr.name)?.let { vertAttr ->
            for (i in 0 until attr.props.nSlots) {
                val off = offset + attr.props.attribSize * i
                val vbo = VboBinder(buffer, attr.props.attribSize, stride, off, FLOAT)
                binders += AttributeOnLocation(vbo, vertAttr.location + i)
            }
        }
        return binders
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
}