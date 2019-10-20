package de.fabmax.kool.drawqueue

import de.fabmax.kool.scene.Mesh

class DrawCommandMesh(val mesh: Mesh) : DrawCommand() {

    var indexBuffer = 0L
    var vertexBuffer = 0L

}