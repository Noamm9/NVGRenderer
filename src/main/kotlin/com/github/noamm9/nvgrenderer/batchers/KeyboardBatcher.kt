package com.github.noamm9.nvgrenderer.batchers

object KeyboardBatcher {
    private val keyCallbacks = HashSet<(KeyEvent) -> Boolean>()
    private val charCallbacks = HashSet<(CharEvent) -> Boolean>()

    fun addCallbackKey(cb: (KeyEvent) -> Boolean) = keyCallbacks.add(cb)
    fun removeCallbackKey(cb: (KeyEvent) -> Boolean) = keyCallbacks.remove(cb)

    fun addCallbackChar(cb: (CharEvent) -> Boolean) = charCallbacks.add(cb)
    fun removeCallbackChar(cb: (CharEvent) -> Boolean) = charCallbacks.remove(cb)

    @JvmStatic
    fun keyboardHookKey(event: KeyEvent): Boolean {
        keyCallbacks.forEach {
            if (it.invoke(event)) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun keyboardHookChar(event: CharEvent): Boolean {
        charCallbacks.forEach {
            if (it.invoke(event)) {
                return true
            }
        }
        return false
    }

    data class KeyEvent(val button: Int, val modifiers: Int, val scanCode: Int, val state: KeyState)
    data class CharEvent(val charCode: Int, val modifiers: Int) {
        val char = charCode.toChar()
    }

    enum class KeyState { RELEASE, PRESS, REPEAT }
}

