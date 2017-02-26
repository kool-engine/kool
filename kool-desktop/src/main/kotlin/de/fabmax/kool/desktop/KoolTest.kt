package de.fabmax.kool.desktop

import de.fabmax.kool.demo.modelDemo
import de.fabmax.kool.demo.simpleShapesDemo
import de.fabmax.kool.demo.uiDemo
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.lwjgl3.Lwjgl3Context
import java.awt.BorderLayout
import java.awt.Font
import javax.swing.JFrame
import javax.swing.JLabel

/**
 * @author fabmax
 */
fun main(args: Array<String>) {
    PlatformImpl.init()
    val ctx = Platform.createContext(Lwjgl3Context.InitProps())

//    val frame = JFrame()
//    val label = JLabel("Hello World")
//    label.font = Font("Segoe UI", Font.PLAIN, 31)
//    frame.contentPane.add(BorderLayout.CENTER, label)
//    frame.setSize(300, 300)
//    frame.isVisible = true
//    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE


    simpleShapesDemo(ctx)
    //modelDemo(ctx)
    //uiDemo(ctx)
}
