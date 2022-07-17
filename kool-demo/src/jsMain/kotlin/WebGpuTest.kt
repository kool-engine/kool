import de.fabmax.kool.platform.webgpu.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Float32Array
import org.khronos.webgl.Uint32Array
import org.w3c.dom.HTMLCanvasElement
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.math.roundToInt

val vertexCode = """
    struct VSOut {
        @builtin(position) Position: vec4<f32>,
        @location(0) color: vec3<f32>
    };
    
    struct UBO {
        modelViewProj: mat4x4<f32>,
        primaryColor: vec4<f32>,
        accentColor: vec4<f32>
    };

    @group(0) @binding(0)
    var<uniform> uniforms: UBO;
    
    @stage(vertex)
    fn main(@location(0) inPos: vec3<f32>,
            @location(1) inColor: vec3<f32>) -> VSOut {
        var vsOut: VSOut;
        vsOut.Position = vec4<f32>(inPos, 1.0);
        vsOut.color = inColor * uniforms.accentColor.rgb + uniforms.primaryColor.rgb;
        return vsOut;
    }
""".trimIndent()

val fragmentCode = """
    @stage(fragment)
    fn main(@location(0) inColor: vec3<f32>) -> @location(0) vec4<f32> {
        return vec4<f32>(inColor, 1.0);
    }
""".trimIndent()

val vertexData = arrayOf(1f, -1f, 0f, -1f, -1f, 0f, 0f, 1f, 0f)
val colorData = arrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
val indexData = arrayOf(0, 1, 2)

val uniformData = arrayOf(
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f,

    0.9f, 0.1f, 0.3f, 1f,
    0.8f, 0.2f, 0.8f, 1f
)

lateinit var canvas: HTMLCanvasElement
lateinit var device: GPUDevice
lateinit var context: GPUCanvasContext
lateinit var pipeline: GPURenderPipeline
lateinit var view: GPUTextureView

lateinit var vertexBuffer: GPUBuffer
lateinit var colorBuffer: GPUBuffer
lateinit var indexBuffer: GPUBuffer
lateinit var uniformBuffer: GPUBuffer
lateinit var uniformBindGroup: GPUBindGroup

/**
 * WebGPU hello world test / demo, for now this is not integrated into kool at all...
 *
 * Only works on Chrome Canary with --enable-unsafe-webgpu
 */
fun webGpuTest() {
    launch {
        canvas = document.getElementById("glCanvas") as HTMLCanvasElement
        val adapter = navigator.gpu.requestAdapter().await()
        device = adapter.requestDevice().await()

        val ctx = canvas.getContext("webgpu") as? GPUCanvasContext
        if (ctx == null) {
            js("alert(\"Unable to initialize WebGL2 context. Your browser may not support it.\")")
            throw RuntimeException("Unable to obtain WebGPU context from canvas")
        }
        context = ctx

        val screenScale = window.devicePixelRatio
        val presentationSize = intArrayOf((canvas.clientWidth * screenScale).roundToInt(), (canvas.clientHeight * screenScale).roundToInt())
        val presentationFormat = navigator.gpu.getPreferredCanvasFormat()
        val sampleCount = 4

        canvas.width = presentationSize[0]
        canvas.height = presentationSize[1]
        context.configure(
            GPUCanvasConfiguration(
                device = device,
                format = presentationFormat
            )
        )
        println("WebGPU context configured!")

        vertexBuffer = createF32Buffer(vertexData, GPUBufferUsage.VERTEX)
        colorBuffer = createF32Buffer(colorData, GPUBufferUsage.VERTEX)
        indexBuffer = createU32Buffer(indexData, GPUBufferUsage.INDEX)
        uniformBuffer = createF32Buffer(uniformData, GPUBufferUsage.UNIFORM or GPUBufferUsage.COPY_DST)

        val positionBufferDesc = GPUVertexBufferLayout(
            attributes = arrayOf(
                GPUVertexAttribute(
                    shaderLocation = 0,
                    offset = 0,
                    format = GPUVertexFormat.float32x3
                )
            ),
            arrayStride = 4 * 3
        )
        val colorBufferDesc = GPUVertexBufferLayout(
            attributes = arrayOf(
                GPUVertexAttribute(
                    shaderLocation = 1,
                    offset = 0,
                    format = GPUVertexFormat.float32x3
                )
            ),
            arrayStride = 4 * 3
        )

        val uniformBindGroupLayout = device.createBindGroupLayout(
            GPUBindGroupLayoutDescriptor(
                entries = arrayOf(
                    GPUBindGroupLayoutEntry(
                        binding = 0,
                        visibility = GPUShaderStage.VERTEX,
                        buffer = GPUBufferBindingLayout(GPUBufferBindingType.uniform)
                    )
                )
            )
        )
        uniformBindGroup = device.createBindGroup(
            GPUBindGroupDescriptor(
                layout = uniformBindGroupLayout,
                entries = arrayOf(
                    GPUBindGroupEntry(
                        binding = 0,
                        resource = GPUBufferBinding(uniformBuffer)
                    )
                )
            )
        )

        val layout = device.createPipelineLayout(
            GPUPipelineLayoutDescriptor(
                bindGroupLayouts = arrayOf(uniformBindGroupLayout)
            )
        )

        pipeline = device.createRenderPipeline(
            GPURenderPipelineDescriptor(
                layout = layout,
                vertex = GPUVertexState(
                    module = device.createShaderModule(GPUShaderModuleDescriptor(vertexCode)),
                    entryPoint = "main",
                    buffers = arrayOf(positionBufferDesc, colorBufferDesc)
                ),
                fragment = GPUFragmentState(
                    module = device.createShaderModule(GPUShaderModuleDescriptor(fragmentCode)),
                    entryPoint = "main",
                    targets = arrayOf(GPUColorTargetState(presentationFormat))
                ),
                primitive = GPUPrimitiveState(
                    topology = GPUPrimitiveTopology.triangleList
                ),
                multisample = GPUMultisampleState(
                    count = sampleCount
                )
            )
        )
        println("created pipeline!")

        val texture = device.createTexture(
            GPUTextureDescriptor(
                size = presentationSize,
                sampleCount = sampleCount,
                format = presentationFormat,
                usage = GPUTextureUsage.RENDER_ATTACHMENT
            )
        )
        view = texture.createView()
        println("created view!")

        window.requestAnimationFrame { frame() }
        println("started animation loop")
    }
}

fun frame() {
    val commandEncoder = device.createCommandEncoder()
    val passEncoder = commandEncoder.beginRenderPass(
        GPURenderPassDescriptor(arrayOf(
            GPURenderPassColorAttachment(
                view = view,
                resolveTarget = context.getCurrentTexture().createView(),
                clearValue = GPUColorDict(0.0, 0.0, 0.0, 1.0),
                loadOp = GPULoadOp.clear,
                storeOp = GPUStoreOp.store
            )
        ))
    )


    passEncoder.setPipeline(pipeline)

    passEncoder.setViewport(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), 0f, 1f)
    passEncoder.setVertexBuffer(0, vertexBuffer)
    passEncoder.setVertexBuffer(1, colorBuffer)
    passEncoder.setIndexBuffer(indexBuffer, GPUIndexFormat.uint32)
    passEncoder.setBindGroup(0, uniformBindGroup)
    passEncoder.drawIndexed(3)

    //passEncoder.draw(3, 1, 0, 0)
    passEncoder.end()

    device.queue.submit(arrayOf(commandEncoder.finish()))

    window.requestAnimationFrame { frame() }
}

fun createF32Buffer(data: Array<Float>, usage: Int): GPUBuffer {
    val buffer = device.createBuffer(
        GPUBufferDescriptor(
            size = data.size * 4L,
            usage = usage,
            mappedAtCreation = true
        )
    )
    val array = Float32Array(buffer.getMappedRange())
    array.set(data)
    buffer.unmap()
    return buffer
}

fun createU32Buffer(data: Array<Int>, usage: Int): GPUBuffer {
    val buffer = device.createBuffer(
        GPUBufferDescriptor(
            size = data.size * 4L,
            usage = usage,
            mappedAtCreation = true
        )
    )
    val array = Uint32Array(buffer.getMappedRange())
    array.set(data)
    buffer.unmap()
    return buffer
}

fun launch(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext get() = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) {
            if (result.isFailure) {
                println("resume with failure")
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    })
}