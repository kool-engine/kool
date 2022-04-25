package de.fabmax.kool.platform.vk

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.vk.pipeline.PipelineManager
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW.glfwGetFramebufferSize
import org.lwjgl.glfw.GLFW.glfwWaitEvents
import org.lwjgl.vulkan.VK10.vkDeviceWaitIdle

class VkSystem(val props: Lwjgl3Context.InitProps, val setup: VkSetup = VkSetup(), val scene: VkScene, val ctx: Lwjgl3Context) : VkResource() {

    val window: GlfwVkWindow

    val instance: Instance
    val physicalDevice: PhysicalDevice
    val device: Device
    val memManager: MemoryManager
    val pipelineManager = PipelineManager(this)

    val commandPool: CommandPool
    val transferCommandPool: CommandPool

    val renderLoop: RenderLoop

    var swapChain: SwapChain? = null

    init {
        val fsMonitor = if (props.monitor < 0) DesktopImpl.primaryMonitor else DesktopImpl.monitors[props.monitor]
        window = GlfwVkWindow(this, props.width, props.height, props.title, fsMonitor, ctx)
        window.isFullscreen = props.isFullscreen
        instance = Instance(this, props.title)
        window.createSurface()

        physicalDevice = PhysicalDevice(this)
        device = Device(this)
        memManager = MemoryManager(this)
        commandPool = CommandPool(this, device.graphicsQueue)
        transferCommandPool = CommandPool(this, device.transferQueue)

        scene.onLoad(this)

        renderLoop = RenderLoop(this)
        recreateSwapChain()
    }

    fun run() {
        renderLoop.run()
        destroy()
    }

    fun recreateSwapChain() {
        memStack {
            val width = mallocInt(1)
            val height = mallocInt(1)
            while (width[0] == 0 || height[0] == 0) {
                // wait while window is minimized
                glfwGetFramebufferSize(window.glfwWindow, width, height)
                glfwWaitEvents()
            }
        }

        swapChain?.let {
            pipelineManager.onSwapchainDestroyed()
            vkDeviceWaitIdle(device.vkDevice)
            it.destroy()
        }
        swapChain = SwapChain(this@VkSystem).also {
            pipelineManager.onSwapchainCreated(it)
            scene.onSwapChainCreated(it)
        }
    }

    override fun freeResources() {
        logD { "Destroyed VkSystem" }
    }
}