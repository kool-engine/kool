package de.fabmax.kool.util

import de.fabmax.kool.CubeMapTexture
import de.fabmax.kool.CubeMapTextureData
import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureProps
import de.fabmax.kool.gl.*
import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.PerspectiveCamera

class EnvironmentMapRenderer(mapSize: Int = 512) {
    private val fboFt = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_POSITIVE_Y).withDepth()
    private val fboBk = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_NEGATIVE_Y).withDepth()
    private val fboLt = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_NEGATIVE_X).withDepth()
    private val fboRt = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_POSITIVE_X).withDepth()
    private val fboUp = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_POSITIVE_Z).withDepth()
    private val fboDn = Framebuffer(mapSize, mapSize, GL_TEXTURE_CUBE_MAP_NEGATIVE_Z).withDepth()

    private val envCam = PerspectiveCamera().apply {
        fovy = 90.0f
    }

    val origin: MutableVec3f
        get() = envCam.position
    val environmentMap: CubeMapTexture

    init {
        val props = TextureProps("env-map", GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0, GL_TEXTURE_CUBE_MAP)
        environmentMap = CubeMapTexture(props) {
            CubeMapTextureData(
                    FbColorTexData(mapSize, mapSize),
                    FbColorTexData(mapSize, mapSize),
                    FbColorTexData(mapSize, mapSize),
                    FbColorTexData(mapSize, mapSize),
                    FbColorTexData(mapSize, mapSize),
                    FbColorTexData(mapSize, mapSize)
            )
        }
        fboFt.colorAttachment = environmentMap
        fboBk.colorAttachment = environmentMap
        fboLt.colorAttachment = environmentMap
        fboRt.colorAttachment = environmentMap
        fboUp.colorAttachment = environmentMap
        fboDn.colorAttachment = environmentMap
    }

    fun update(nodesToRender: List<Node>, ctx: KoolContext) {
        val scene = nodesToRender[0].scene ?: return
        val camera = scene.camera
        scene.camera = envCam

        renderView(nodesToRender, fboFt, Vec3f.Y_AXIS, Vec3f.Z_AXIS, ctx)
        renderView(nodesToRender, fboBk, Vec3f.NEG_Y_AXIS, Vec3f.NEG_Z_AXIS, ctx)
        renderView(nodesToRender, fboLt, Vec3f.NEG_X_AXIS, Vec3f.NEG_Y_AXIS, ctx)
        renderView(nodesToRender, fboRt, Vec3f.X_AXIS, Vec3f.NEG_Y_AXIS, ctx)
        renderView(nodesToRender, fboUp, Vec3f.Z_AXIS, Vec3f.NEG_Y_AXIS, ctx)
        renderView(nodesToRender, fboDn, Vec3f.NEG_Z_AXIS, Vec3f.NEG_Y_AXIS, ctx)

        scene.camera = camera
    }

    private fun renderView(nodesToRender: List<Node>, target: Framebuffer, dir: Vec3f, up: Vec3f, ctx: KoolContext) {
        target.bind(ctx)
        glClear(GL_DEPTH_BUFFER_BIT)

        ctx.mvpState.pushMatrices()
        ctx.mvpState.modelMatrix.setIdentity()
        envCam.lookAt.set(envCam.position).add(dir)
        envCam.up.set(up)
        envCam.updateCamera(ctx)

        for (i in nodesToRender.indices) {
            nodesToRender[i].render(ctx)
        }

        ctx.mvpState.popMatrices()
        ctx.mvpState.update(ctx)
        target.unbind(ctx)
    }
}