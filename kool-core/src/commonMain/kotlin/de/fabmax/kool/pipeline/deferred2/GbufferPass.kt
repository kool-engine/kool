package de.fabmax.kool.pipeline.deferred2

import de.fabmax.kool.math.MutableMat4f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.InstanceLayouts
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.*
import kotlin.math.max

class GbufferPass(
    initialSize: Vec2i,
    name: String,
    val pipeline: Deferred2Pipeline,
) : OffscreenPass2d(
    drawNode = pipeline.content,
    attachmentConfig = AttachmentConfig {
        // albedo, a * 64 = emission strength
        addColor(TexFormat.RGBA, filterMethod = FilterMethod.NEAREST)
        // metal, roughness, ao, [empty: a, flags maybe?]
        addColor(TexFormat.RGBA, filterMethod = FilterMethod.NEAREST)
        // encoded normals (view space)
        addColor(TexFormat.R_I32, filterMethod = FilterMethod.NEAREST, clearColor = ClearColorFill(Color.ZERO))
        // object-ids, meta
        addColor(TexFormat.R_I32, filterMethod = FilterMethod.NEAREST)
        defaultDepth()
    },
    initialSize = initialSize,
    name = name
) {
    val albedoEmission get() = colorTextures[0]
    val metalRoughnessAo get() = colorTextures[1]
    val normals get() = colorTextures[2]
    val objectIds get() = colorTextures[3]
    val depth get() = depthTexture!!

    internal val lightingPassMeshes = mutableListOf<Mesh<*>>()

    init {
        camera = pipeline.camera
        onAfterCollectDrawCommands += { viewData ->
            val upload = pipeline.reprojectMatrixComputePass.uploadData.newVal
            upload.viewProjMat.set(pipeline.viewProjNoTsaa)
            lightingPassMeshes.clear()

            // This is a bit of a hack to prevent modifying data while it is uploaded to oldModelMats binding during
            // the first frame. Later frames do not upload data to oldModelMats because of buffer swapping. Therefore,
            // it is then safe to modify the buffer.
            val canUpload = pipeline.reprojectMatrixComputePass.isWarmedUp
            if (canUpload) {
                upload.modelMats.limit = 0
            }

            val drawQueue = viewData.drawQueue
            val it = drawQueue.iterator()
            while (it.hasNext()) {
                val cmd = it.next()
                val mesh = cmd.mesh
                when (val shader = mesh.shader) {
                    is DeferredObjectShader -> {
                        val idRange = pipeline.idAllocator.getIdRange(cmd.mesh)
                        val bufferPos = idRange.from * 16
                        shader.objectId = idRange.from

                        if (canUpload) {
                            upload.modelMats.limit = max(upload.modelMats.limit, bufferPos + idRange.size * 16)
                            val instances = mesh.instances
                            if (instances != null) {
                                val matrixExtractor = shader.instanceModelMatExtractor ?: DefaultInstanceModelMatrixExtractor
                                if (instances.numInstances > idRange.size) {
                                    logE { "Mesh ${mesh.name} number of instances exceeds ID range: ${instances.numInstances} > ${idRange.size}" }
                                }
                                for (i in 0 until instances.numInstances.coerceAtMost(idRange.size)) {
                                    upload.modelMats.position = bufferPos + i * 16
                                    matrixExtractor.getModelMatrix(i, mesh, upload.modelMats)
                                }
                            } else {
                                upload.modelMats.position = bufferPos
                                cmd.modelMatF.putTo(upload.modelMats)
                            }
                        }
                    }
                    else -> {
                        lightingPassMeshes += cmd.mesh
                        it.remove()
                        drawQueue.recycleDrawCommand(cmd)
                    }
                }
            }
        }
    }
}

private object DefaultInstanceModelMatrixExtractor : InstanceModelMatrixExtractor {
    private val insModelMatBuf = MutableMat4f()
    private val modelMatBuf = MutableMat4f()

    @Suppress("UNCHECKED_CAST")
    override fun getModelMatrix(instanceIndex: Int, mesh: Mesh<*>, target: Float32Buffer) {
        val insts = requireNotNull(mesh.instances)
        val modelMat = insts.layout.members.first { it.name == InstanceLayouts.ModelMat.modelMat.name } as Mat4Member<Struct>
        insts.instanceData.get(instanceIndex) {
            this as StructBufferView<Struct>
            get(modelMat, insModelMatBuf)
            modelMatBuf.set(mesh.modelMatF).mul(insModelMatBuf)
            modelMatBuf.putTo(target)
        }
    }
}
