package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.vk.pipeline.PipelineManager
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logD

class VkSystem(val setup: VkSetup = VkSetup(), val scene: VkScene, val ctx: Lwjgl3Context) : VkResource() {

    val window: GlfwVkWindow = TODO()

    val instance: Instance
    val physicalDevice: PhysicalDevice
    val logicalDevice: LogicalDevice
    val memManager: MemoryManager
    val pipelineManager = PipelineManager(this)

    val commandPool: CommandPool
    val transferCommandPool: CommandPool

    val renderLoop: RenderLoop

    var swapChain: SwapChain? = null

    val backend: RenderBackendVk get() = TODO()

//    init {
//        check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }
//        // tell GLFW to not initialize default OpenGL API before we create the window
//        GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, GLFW.GLFW_NO_API)
//
//        window = GlfwVkWindow(this, ctx)
//        window.isFullscreen = KoolSystem.configJvm.isFullscreen
//        instance = Instance(this, KoolSystem.configJvm.windowTitle)
//        window.createSurface()
//
//        physicalDevice = PhysicalDevice(this)
//        device = Device(this)
//        memManager = MemoryManager(this)
//        commandPool = CommandPool(this, device.graphicsQueue)
//        transferCommandPool = CommandPool(this, device.transferQueue)
//
//        scene.onLoad(this)
//
//        renderLoop = RenderLoop(this)
//        recreateSwapChain()
//    }

    fun run() {
        renderLoop.run()
        destroy()
    }

    fun recreateSwapChain() {
//        memStack {
//            val width = mallocInt(1)
//            val height = mallocInt(1)
//            while (width[0] == 0 || height[0] == 0) {
//                // wait while window is minimized
//                glfwGetFramebufferSize(window.windowPtr, width, height)
//                glfwWaitEvents()
//            }
//        }
//
//        swapChain?.let {
//            pipelineManager.onSwapchainDestroyed()
//            vkDeviceWaitIdle(logicalDevice.vkDevice)
//            it.destroy()
//        }
//        swapChain = SwapChain(this@VkSystem).also {
//            pipelineManager.onSwapchainCreated(it)
//            scene.onSwapChainCreated(it)
//        }
    }

    override fun freeResources() {
        logD { "Destroyed VkSystem" }
    }
}