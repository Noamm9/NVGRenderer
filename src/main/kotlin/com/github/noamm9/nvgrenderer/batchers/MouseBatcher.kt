package com.github.noamm9.nvgrenderer.batchers

object MouseBatcher {
    private val clickCallbacks = HashSet<(MouseClickEvent) -> Boolean>()
    private val scrollCallbacks = HashSet<(MouseScrolledEvent) -> Boolean>()

    fun addCallbackClick(cb: (MouseClickEvent) -> Boolean) = clickCallbacks.add(cb)
    fun removeCallbackClick(cb: (MouseClickEvent) -> Boolean) = clickCallbacks.remove(cb)

    fun addCallbackScroll(cb: (MouseScrolledEvent) -> Boolean) = scrollCallbacks.add(cb)
    fun removeCallbackScroll(cb: (MouseScrolledEvent) -> Boolean) = scrollCallbacks.remove(cb)

    @JvmStatic
    fun mouseHookClick(event: MouseClickEvent): Boolean {
        clickCallbacks.forEach {
            if (it.invoke(event)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun mouseHookScroll(event: MouseScrolledEvent): Boolean {
        scrollCallbacks.forEach {
            if (it.invoke(event)) {
                return true
            }
        }
        return false
    }

    data class MouseClickEvent(val button: Int, val modifiers: Int, val scanCode: Int)
    data class MouseScrolledEvent(val horizontal: Double, val vertical: Double)
}