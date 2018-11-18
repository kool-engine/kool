package de.fabmax.kool.scene

import de.fabmax.kool.CubeMapTexture
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.shading.*

class Skybox(camera: Camera, var environmentMap: CubeMapTexture) : Scene() {

    init {
        this.camera = SkyboxCamera(camera)
        clearMask = 0

        +mesh(setOf(Attribute.POSITIONS), "skybox") {
            isFrustumChecked = false
            generator = {
                cube {
                    size.set(1f, 1f, 1f)
                    centerOrigin()
                }
            }
            shader = SkyboxShader(environmentMap)
            cullMethod = CullMethod.CULL_FRONT_FACES
        }
    }

    private class SkyboxCamera(val mainCam: Camera) : Camera() {
        override fun updateViewMatrix() {
            view.set(mainCam.view)
        }

        override fun updateProjectionMatrix() {
            proj.set(mainCam.proj)
        }

        override fun computeFrustumPlane(z: Float, result: FrustumPlane) { }

        override fun isInFrustum(globalCenter: Vec3f, globalRadius: Float): Boolean = true
    }

    private class SkyboxShader(environmentMap: CubeMapTexture) : BasicShader(ShaderProps().apply {
        colorModel = ColorModel.CUSTOM_COLOR
        lightModel = LightModel.NO_LIGHTING
        this.environmentMap = environmentMap
    }) {
        private val projMatrix = addUniform(UniformMatrix4("uProjMatrix"))

        init {
            generator.customUniforms += projMatrix
            generator.injectors += object : GlslGenerator.GlslInjector {
                override fun vsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                    text.append("${generator.vsOut} vec3 texCoord;\n")
                }

                override fun vsAfterProj(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                    text.append("texCoord = ${Attribute.POSITIONS};\n")
                    text.append("vec4 pos = uProjMatrix * mat4(mat3(${GlslGenerator.U_VIEW_MATRIX})) * vec4(${Attribute.POSITIONS}, 1.0);\n")
                    text.append("gl_Position = pos.xyww;\n")
                }

                override fun fsAfterInput(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                    text.append("${generator.fsIn} vec3 texCoord;\n")
                    text.append("uniform samplerCube ${GlslGenerator.U_ENVIRONMENT_MAP};\n")
                }

                override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
                    text.append("${generator.fsOutBody} = texture(${GlslGenerator.U_ENVIRONMENT_MAP}, texCoord);\n")
                }
            }
        }

        override fun onMatrixUpdate(ctx: KoolContext) {
            projMatrix.value = ctx.mvpState.projMatrixBuffer
            projMatrix.bind(ctx)
            super.onMatrixUpdate(ctx)
        }
    }
}
