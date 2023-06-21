package de.fabmax.kool.demo.bees

import de.fabmax.kool.modules.ksl.KslBlinnPhongShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.util.Color

class BeeShader(
    aliveColor: Color,
    deadColor: Color,
    cfg: Config = Config().apply {
        pipeline { cullMethod = CullMethod.NO_CULLING }
        color { uniformColor(aliveColor.toLinear()) }
        shininess(5f)
        specularStrength(0.25f)
        ambientColor = AmbientColor.Uniform(BeeDemo.bgColor.toLinear())

        modelCustomizer = {
            val aliveness = interStageFloat1()
            vertexStage {
                main {
                    val rotQuat = instanceAttribFloat4(BeeDemo.ATTR_ROTATION.name)
                    val r = rotQuat.w
                    val i = rotQuat.x
                    val j = rotQuat.y
                    val k = rotQuat.z

                    val s = float1Var(sqrt(r*r + i*i + j*j + k*k))
                    s set 1f.const / (s * s)

                    val rotMat = mat3Var()
                    rotMat[0] set float3Value(
                        1f.const - 2f.const * s * (j*j + k*k),
                        2f.const * s * (i*j + k*r),
                        2f.const * s * (i*k - j*r)
                    )
                    rotMat[1] set float3Value(
                        2f.const * s * (i*j - k*r),
                        1f.const - 2f.const * s * (i*i + k*k),
                        2f.const * s * (j*k + i*r)
                    )
                    rotMat[2] set float3Value(
                        2f.const * s * (i*k + j*r),
                        2f.const * s * (j*k - i*r),
                        1f.const - 2f.const * s * (i*i + j*j)
                    )

                    val globalPos = instanceAttribFloat4(BeeDemo.ATTR_POSITION.name)
                    val vertexNormal = vertexAttribFloat3(Attribute.NORMALS.name)
                    val vertexPos = float3Var(vertexAttribFloat3(Attribute.POSITIONS.name))

                    val scale = float1Var(1f.const - clamp(globalPos.w - (BeeConfig.decayTime - 1f).const, 0f.const, 1f.const))

                    getFloat3Port("worldPos").input(rotMat * vertexPos * scale + globalPos.xyz)
                    getFloat3Port("worldNormal").input(rotMat * vertexNormal)

                    aliveness.input set globalPos.w
                }
            }
            fragmentStage {
                main {
                    val colorPort = getFloat4Port("baseColor")
                    val color = float4Var(colorPort.input.input)
                    `if`(aliveness.output gt 0.01f.const) {
                        color set deadColor.toLinear().const
                    }
                    colorPort.input(color)
                }
            }
        }
    }
) : KslBlinnPhongShader(cfg)