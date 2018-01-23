package de.fabmax.kool.scene

import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.shading.Shader
import de.fabmax.kool.util.Attribute

/**
 * @author fabmax
 */

class Model(name: String?): TransformGroup(name) {

    val armatures = mutableListOf<Armature>()
    private val geometries = mutableListOf<Geometry>()
    private val subModels = mutableListOf<Model>()

    private var initGeometries = true

    var shaderFab: (() -> Shader)? = null
    var shader: Shader? = null

    fun copyInstance(): Model {
        val copy = Model(name)
        copy.initGeometries = initGeometries
        copy.shaderFab = shaderFab

        copy.geometries.addAll(geometries)
        copy.armatures.addAll(armatures)

        for (child in subModels) {
            copy.addSubModel(child.copyInstance())
        }

        return copy
    }

    fun addGeometry(meshData: MeshData, armature: Armature?, block: (Geometry.() -> Unit)?) {
        geometries += Geometry(meshData).apply {
            block?.invoke(this)
            meshData.generateGeometry()
        }
        if (armature != null) {
            armatures.add(armature)
        }
    }

    fun addColorGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS)
        addGeometry(meshData, null, block)
    }

    fun addTextGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)
        addGeometry(meshData, null, block)
    }

    fun addTextureGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS)
        addGeometry(meshData, null, block)
    }

    fun addSubModel(child: Model) {
        subModels += child
        addNode(child)
    }

    override fun render(ctx: RenderContext) {
        if (initGeometries) {
            initGeometries = false

            if (shader == null) {
                val p = parent
                val fab = shaderFab
                if (fab != null) {
                    shader = fab()
                } else if (p is Model) {
                    shader = p.shader
                }
            }

            for (geom in geometries) {
                val mesh = geom.makeMesh()
                if (mesh.shader == null) {
                    mesh.shader = shader
                }
                addNode(mesh)
            }
        }

        for (i in armatures.indices) {
            armatures[i].applyAnimation(ctx.deltaT)
        }

        super.render(ctx)
    }

    class Geometry internal constructor(val meshData: MeshData) {
        var name: String? = null
        var meshFab: ((MeshData) -> Mesh) = { meshData -> Mesh(meshData, name) }
        var shaderFab: (() -> Shader)? = null

        fun makeMesh(): Mesh {
            val mesh = meshFab(meshData)
            mesh.shader = shaderFab?.invoke()
            return mesh
        }
    }
}
