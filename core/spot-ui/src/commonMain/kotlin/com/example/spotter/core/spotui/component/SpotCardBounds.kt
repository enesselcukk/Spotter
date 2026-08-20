package com.example.spotter.core.spotui.component

data class SpotCardBounds(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
) {
    val centerX: Float get() = left + width / 2f
    val centerY: Float get() = top + height / 2f

    companion object {
        val Zero = SpotCardBounds(0f, 0f, 0f, 0f)
    }
}
