package com.viktormykhailiv.kmp.health.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Set background as app gradient.
 *
 * Important: background color and elevation of the Component
 * itself must be set `Color.Transparent` and `0.dp` respectively.
 */
fun Modifier.appGradient(): Modifier {
    return drawWithCache {
        val gradient = Brush.linearGradient(
            colors = listOf(
                Color(0xFF55B8FF),
                Color(0xFF52FABD),
                Color(0xFFC9FF8F),
            ),
            angleInDegrees = 45f,
        )
        onDrawBehind {
            drawRect(gradient)
        }
    }
}
