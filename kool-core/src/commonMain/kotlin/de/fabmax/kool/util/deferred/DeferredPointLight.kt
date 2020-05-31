package de.fabmax.kool.util.deferred

import de.fabmax.kool.math.MutableVec3f
import de.fabmax.kool.scene.Light
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.util.Color
import kotlin.math.sqrt

class DeferredPointLight(mrtPass: DeferredMrtPass) {

    private val pointLight = Light()
    val position = MutableVec3f()
    var color = Color.WHITE
    var intensity = 1f

    val lightNode = TransformGroup()
    val lightMesh: Mesh = colorMesh {
        generate {
            icoSphere {
                steps = 1
                radius = 1.1f
            }
        }

        val cfg = PbrLightShader.Config().apply {
            sceneCamera = mrtPass.camera
            light = pointLight
            positionAo = mrtPass.positionAo
            normalRoughness = mrtPass.normalRoughness
            albedoMetal = mrtPass.albedoMetal
        }
        pipelineLoader = PbrLightShader(cfg)
    }

    init {
        lightNode += lightMesh

        lightNode.onUpdate += { _, ctx ->
            lightNode.setIdentity()
            lightNode.translate(position)
            val soi = sqrt(intensity)
            lightNode.scale(soi, soi, soi)

            pointLight.setPoint(position)
            pointLight.setColor(color, intensity)
        }
    }
}