package com.viktormykhailiv.kmp.health.ui

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Creates a linear gradient with the provided colors
 * and angle.
 *
 * @param colors Colors of gradient
 * @param stops Offsets to determine how the colors are dispersed throughout
 * the vertical gradient
 * @param tileMode Determines the behavior for how the shader is to fill a region outside
 * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
 * @param angleInDegrees Angle of a gradient in degrees
 *
 * @see `https://github.com/BMukhtar/linear-gradient-any-angle/blob/main/app/src/main/java/kz/bmukhtar/lineargradient/LinearGradient.kt`
 */
@Immutable
private class LinearGradient(
    private val colors: List<Color>,
    private val stops: List<Float>? = null,
    private val tileMode: TileMode = TileMode.Clamp,
    angleInDegrees: Float = 0f,
) : ShaderBrush() {

    private val normalizedAngle: Float = (angleInDegrees % 360 + 360) % 360
    private val angleInRadians: Float = normalizedAngle / 180f * PI.toFloat()

    override fun createShader(size: Size): Shader {
        val (from, to) = getGradientCoordinates(size = size)

        return LinearGradientShader(
            colors = colors,
            colorStops = stops,
            from = from,
            to = to,
            tileMode = tileMode
        )
    }

    private fun getGradientCoordinates(size: Size): Pair<Offset, Offset> {
        val diagonal = sqrt(size.width.pow(2) + size.height.pow(2))
        val angleBetweenDiagonalAndWidth = acos(size.width / diagonal)
        val angleBetweenDiagonalAndGradientLine =
            if ((normalizedAngle > 90 && normalizedAngle < 180)
                || (normalizedAngle > 270 && normalizedAngle < 360)
            ) {
                PI.toFloat() - angleInRadians - angleBetweenDiagonalAndWidth
            } else {
                angleInRadians - angleBetweenDiagonalAndWidth
            }
        val halfGradientLine = abs(cos(angleBetweenDiagonalAndGradientLine) * diagonal) / 2

        val horizontalOffset = halfGradientLine * cos(angleInRadians)
        val verticalOffset = halfGradientLine * sin(angleInRadians)

        val start = size.center + Offset(-horizontalOffset, verticalOffset)
        val end = size.center + Offset(horizontalOffset, -verticalOffset)

        return start to end
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LinearGradient) return false

        if (colors != other.colors) return false
        if (stops != other.stops) return false
        if (normalizedAngle != other.normalizedAngle) return false
        if (tileMode != other.tileMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + (stops?.hashCode() ?: 0)
        result = 31 * result + normalizedAngle.hashCode()
        result = 31 * result + tileMode.hashCode()
        return result
    }

    override fun toString(): String {
        return "LinearGradient(colors=$colors, " +
                "stops=$stops, " +
                "angle=$normalizedAngle, " +
                "tileMode=$tileMode)"
    }
}

/**
 * Creates a linear gradient with the provided colors
 * and angle.
 *
 * @param colors Colors of gradient
 * @param stops Offsets to determine how the colors are dispersed throughout
 * the vertical gradient
 * @param tileMode Determines the behavior for how the shader is to fill a region outside
 * its bounds. Defaults to [TileMode.Clamp] to repeat the edge pixels
 * @param angleInDegrees Angle of a gradient in degrees
 *
 * @see `https://github.com/BMukhtar/linear-gradient-any-angle/blob/main/app/src/main/java/kz/bmukhtar/lineargradient/LinearGradient.kt`
 */
@Stable
fun Brush.Companion.linearGradient(
    colors: List<Color>,
    stops: List<Float>? = null,
    tileMode: TileMode = TileMode.Clamp,
    angleInDegrees: Float = 0f,
): Brush = LinearGradient(
    colors = colors,
    stops = stops,
    tileMode = tileMode,
    angleInDegrees = angleInDegrees,
)
