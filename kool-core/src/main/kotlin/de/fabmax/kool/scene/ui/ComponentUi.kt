package de.fabmax.kool.scene.ui

import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.*
import de.fabmax.kool.util.Attribute
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MeshBuilder

interface ComponentUi {

    fun updateComponentAlpha() { }

    fun createUi(ctx: RenderContext) { }

    fun updateUi(ctx: RenderContext) { }

    fun disposeUi(ctx: RenderContext) { }

    fun onRender(ctx: RenderContext) { }
}

open class BlankComponentUi : ComponentUi

open class SimpleComponentUi(val component: UiComponent) : ComponentUi {

    protected var shader: BasicShader? = null
    protected val meshData = MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.COLORS, Attribute.TEXTURE_COORDS)
    protected val meshBuilder = MeshBuilder(meshData)
    protected val mesh = Mesh(meshData)

    val color: ThemeOrCustomProp<Color> = ThemeOrCustomProp(Color.BLACK.withAlpha(0.5f))

    override fun updateComponentAlpha() {
        shader?.alpha = component.alpha
    }

    override fun createUi(ctx: RenderContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader = createShader(ctx)
        shader?.staticColor?.set(color.prop)
        mesh.shader = shader
        component.addNode(mesh, 0)
    }

    override fun disposeUi(ctx: RenderContext) {
        component -= mesh
        mesh.dispose(ctx)
    }

    override fun updateUi(ctx: RenderContext) {
        color.setTheme(component.root.theme.backgroundColor).apply()
        shader?.staticColor?.set(color.prop)

        component.setupBuilder(meshBuilder)
        meshBuilder.color = color.prop
        meshBuilder.rect {
            size.set(component.width, component.height)
            fullTexCoords()
        }
    }

    protected open fun createShader(ctx: RenderContext): BasicShader {
        return basicShader {
            lightModel = component.root.shaderLightModel
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }
    }
}

open class BlurredComponentUi(component: UiComponent) : SimpleComponentUi(component) {
    override fun createShader(ctx: RenderContext): BasicShader {
        return blurShader {
            lightModel = component.root.shaderLightModel
            colorModel = ColorModel.STATIC_COLOR
            isAlpha = true
        }.apply {
            blurHelper = component.root.createBlurHelper()
        }
    }

    override fun updateUi(ctx: RenderContext) {
        super.updateUi(ctx)
        val bs = shader
        if (bs is BlurShader) {
            bs.colorMix = bs.staticColor.w
            bs.staticColor.w = 1f
        }
    }
}