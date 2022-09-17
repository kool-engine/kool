package de.fabmax.kool.modules.ui2

interface Clickable {
    fun onClick(ev: PointerEvent) { }
}

interface Hoverable {
    fun onEnter(ev: PointerEvent) { }
    fun onHover(ev: PointerEvent) { }
    fun onExit(ev: PointerEvent) { }
}

interface Draggable {
    fun onDragStart(ev: PointerEvent) { }
    fun onDrag(ev: PointerEvent) { }
    fun onDragEnd(ev: PointerEvent) { }
}
