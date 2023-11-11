package de.fabmax.kool.pipeline.backend.gl

class VboBinder(
    val vbo: BufferResource,
    val elemSize: Int,
    val strideBytes: Int,
    val offset: Int,
    val type: Int
) {

    private val gl = vbo.backend.gl

    fun bindAttribute(target: Int) {
        // fixme: remove entire VboBinder class and integrate this into BufferResource.bind()
        vbo.bind()
        if (type == gl.INT || type == gl.UNSIGNED_INT) {
            gl.vertexAttribIPointer(target, elemSize, type, strideBytes, offset * 4)
        } else {
            gl.vertexAttribPointer(target, elemSize, type, false, strideBytes, offset * 4)
        }
    }
}
