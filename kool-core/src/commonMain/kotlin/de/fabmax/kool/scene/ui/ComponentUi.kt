package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Disposable
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.MeshBuilder

interface ComponentUi : Disposable {

    fun updateComponentAlpha() { }

    fun createUi(ctx: KoolContext) { }

    fun updateUi(ctx: KoolContext) { }

    fun onRender(ctx: KoolContext) { }

    override fun dispose(ctx: KoolContext) { }
}

open class BlankComponentUi : ComponentUi

open class SimpleComponentUi(val component: UiComponent) : ComponentUi {

    protected val geometry = IndexedVertexList(UiShader.UI_MESH_ATTRIBS)
    protected val meshBuilder = MeshBuilder(geometry)
    protected val mesh = Mesh(geometry)
    protected val shader = UiShader()

    val color: ThemeOrCustomProp<Color> = ThemeOrCustomProp(Color.BLACK.withAlpha(0.5f))

    override fun updateComponentAlpha() {
        shader.alpha = component.alpha
    }

    override fun createUi(ctx: KoolContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        mesh.shader = shader
        component.addNode(mesh, 0)
    }

    override fun dispose(ctx: KoolContext) {
        component -= mesh
        mesh.dispose(ctx)
    }

    override fun updateUi(ctx: KoolContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        component.setupBuilder(meshBuilder)
        meshBuilder.color = color.prop
        meshBuilder.rect {
            size.set(component.width, component.height)
            zeroTexCoords()
        }
    }

    override fun onRender(ctx: KoolContext) {
//        shader?.setDrawBounds(component.drawBounds)
    }
}