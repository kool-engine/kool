package de.fabmax.kool.app

import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.ColorMesh
import de.fabmax.kool.util.MdColor

class SampleProceduralMesh : ColorMesh() {

    init {
        generate {
            color = MdColor.LIGHT_GREEN.toLinear()
            cube {
                centered()
            }
        }
        shader = KslPbrShader {
            color { vertexColor() }
        }
    }

}