package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.Shader
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */

class Model(name: String): TransformGroup(name) {

    private val subModels: MutableList<Model> = mutableListOf()
    private val geometries: MutableList<Geometry> = mutableListOf()

    private var initGeometries = true

    var shaderFab: (() -> Shader)? = null
    var shader: Shader? = null

    fun copyInstance(): Model {
        val model = Model(name!!)
        model.shaderFab = shaderFab
        for (child in subModels) {
            model.addSubModel(child.copyInstance())
        }
        for (geom in geometries) {
            model.geometries += geom
        }
        return model
    }

    fun addGeometry(meshData: MeshData, block: Geometry.() -> Unit) {
        geometries += Geometry(meshData).apply {
            block()
            meshData.generateGeometry()
        }
    }

    fun addColorGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(true, true, false)
        addGeometry(meshData, block)
    }

    fun addTextGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(true, true, true)
        addGeometry(meshData, block)
    }

    fun addTextureGeometry(block: Geometry.() -> Unit) {
        val meshData = MeshData(true, false, true)
        addGeometry(meshData, block)
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
                val mesh = Mesh(geom.meshData, geom.name)
                val fab = geom.shaderFab
                if (fab != null) {
                    mesh.shader = fab()
                } else {
                    mesh.shader = shader
                }
                addNode(mesh)
            }
        }
        super.render(ctx)
    }

    class Geometry internal constructor(val meshData: MeshData) {
        var name: String? = null
        var shaderFab: (() -> Shader)? = null
    }
}
