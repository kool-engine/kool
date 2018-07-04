package de.fabmax.kool.demo.globe

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.BasicShader

class TileMesh(val globe: Globe, val tileName: TileName, ctx: KoolContext) :
        Mesh(MeshData(Attribute.POSITIONS, Attribute.NORMALS, Attribute.TEXTURE_COORDS), "$tileName") {

    val key: Long get() = tileName.fusedKey
    val centerNormal = MutableVec3f(Vec3f.Z_AXIS)

    val isCurrentlyVisible get() = isRendered

    private var tileShader: BasicShader

    private var tmpVec = MutableVec3f()

    var isFadingOut = false
    var isLoaded = false
        private set
    var isTexLoaded = false
        private set

    init {
        tileShader = globe.tileShaderProvider.getShader(tileName, ctx)
        shader = tileShader
    }

    override fun preRender(ctx: KoolContext) {
        val targetAlpha = 1f
        if (isTexLoaded && !isFadingOut && tileShader.alpha < targetAlpha) {
            // increase alpha as soon as texture is available (but mesh doesn't have to be visible)
            tileShader.alpha += ctx.deltaT
            if (tileShader.alpha >= targetAlpha) {
                tileShader.alpha = targetAlpha
                isLoaded = true
                globe.tileLoaded(this)
            }

        } else if (isFadingOut && tileShader.alpha > 0f) {
            tileShader.alpha -= ctx.deltaT
            if (tileShader.alpha <= 0f) {
                tileShader.alpha = 0f
                globe.tileFadedOut(this)
            }
        }

        super.preRender(ctx)
    }

    override fun checkIsVisible(ctx: KoolContext): Boolean {
        val tex = tileShader.texture ?: return true

        isTexLoaded = tex.res?.isLoaded ?: false
        val visible = isTexLoaded && super.checkIsVisible(ctx)
        if (visible) {
            toGlobalCoords(tmpVec.set(centerNormal), 0f)
            val cos = scene?.camera?.globalLookDir?.dot(tmpVec) ?: 0f
            return cos < 0.1f

        } else if (!isTexLoaded) {
            // trigger / poll texture loading
            ctx.textureMgr.bindTexture(tex, ctx)
        }
        return false
    }
}