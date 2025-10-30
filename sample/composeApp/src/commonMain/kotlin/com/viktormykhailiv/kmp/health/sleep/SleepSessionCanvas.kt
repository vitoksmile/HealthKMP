package com.viktormykhailiv.kmp.health.sleep

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.records.SleepSessionRecord
import com.viktormykhailiv.kmp.health.records.SleepStageType
import kotlin.time.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.DateTimeFormat
import kotlin.time.Duration.Companion.minutes

/**
 * Source https://github.com/vitoksmile/Sleep-timeline-graph
 */
@Composable
fun SleepSessionCanvas(
    modifier: Modifier,
    record: SleepSessionRecord,
    stageHeight: Dp = 36.dp,
    stagesSpacing: Dp = 8.dp,
    textSpacing: Dp = 4.dp,
    typeTextStyle: TextStyle = LocalTextStyle.current,
    timeTextStyle: TextStyle = LocalTextStyle.current,
    timeFormatter: DateTimeFormat<DateTimeComponents> = remember {
        DateTimeComponents.Format {
            hour()
            chars(":")
            minute()
        }
    },
) {
    val colors = remember {
        mapOf(
            SleepStageType.Awake to Color(0xFFFF9800),
            SleepStageType.Light to Color(0xFF2196F3),
            SleepStageType.Deep to Color(0xFF673AB7),
            SleepStageType.REM to Color(0xFF795548),
        )
    }
    val textMeasurer = rememberTextMeasurer()

    val stageHeightPx = with(LocalDensity.current) { stageHeight.toPx() }
    val stagesSpacingPx = with(LocalDensity.current) { stagesSpacing.toPx() }
    val textSpacingPx = with(LocalDensity.current) { textSpacing.toPx() }

    val typeTextHeight = with(LocalDensity.current) {
        remember {
            textMeasurer.measure("", style = typeTextStyle).size.height
        }.toDp()
    }
    val timeTextHeight = with(LocalDensity.current) {
        remember {
            textMeasurer.measure("", style = timeTextStyle).size.height
        }.toDp()
    }

    Spacer(
        modifier = modifier
            .requiredHeight((stageHeight + typeTextHeight + textSpacing) * colors.size + stagesSpacing * (colors.size - 1) + textSpacing + timeTextHeight)
            .drawWithCache {
                val stages = listOf(
                    SleepStageType.Awake,
                    SleepStageType.REM,
                    SleepStageType.Light,
                    SleepStageType.Deep,
                ).map { type ->
                    val stages =
                        record.stages.filter { it.type == type }
                    val stageDuration =
                        stages.sumOf { (it.endTime - it.startTime).inWholeMinutes }.minutes

                    DrawStage(
                        title = textMeasurer.measure(
                            text = "$type • $stageDuration",
                            style = typeTextStyle,
                            maxLines = 1,
                        ),
                        type = type,
                        points = calculatePoints(
                            canvasSize = size.copy(height = stageHeightPx),
                            recordStartTime = record.startTime,
                            recordEndTime = record.endTime,
                            stages = stages,
                        ),
                    )
                }

                val hours = calculateHours(
                    textMeasurer = textMeasurer,
                    textStyle = timeTextStyle,
                    formatter = timeFormatter,
                    canvasSize = size,
                    recordStartTime = record.startTime,
                    recordEndTime = record.endTime,
                )

                onDrawWithContent {
                    var offset = 0f
                    stages.forEach { (title, type, points) ->
                        // Draw title (type and duration)
                        translate(top = offset) {
                            drawText(title)
                        }
                        offset += title.size.height + textSpacingPx

                        translate(top = offset) {
                            // Draw background
                            drawRoundRect(
                                color = Color.LightGray,
                                topLeft = Offset(x = 0f, y = stageHeightPx / 4),
                                size = size.copy(height = stageHeightPx / 2),
                                cornerRadius = CornerRadius(stageHeightPx / 2),
                            )

                            // Draw stage points
                            points.forEach { point ->
                                drawRect(
                                    topLeft = point.topLeft,
                                    size = point.size,
                                    color = colors.getValue(type),
                                )
                            }
                        }
                        offset += stageHeightPx + stagesSpacingPx
                    }

                    // Draw session hours
                    offset += textSpacingPx - stagesSpacingPx
                    hours.forEach { (topLeft, textLayoutResult) ->
                        translate(top = offset) {
                            drawText(
                                textLayoutResult = textLayoutResult,
                                topLeft = topLeft,
                            )
                        }
                    }
                }
            }
    )
}

private fun calculatePoints(
    canvasSize: Size,
    recordStartTime: Instant,
    recordEndTime: Instant,
    stages: List<SleepSessionRecord.Stage>,
): List<DrawPoint> {
    val totalDuration = (recordEndTime - recordStartTime).inWholeSeconds.toFloat()
        .coerceAtLeast(1f)

    return stages.map { stage ->
        val stageOffset =
            (stage.startTime - recordStartTime).inWholeSeconds / totalDuration
        val stageDuration =
            (stage.endTime - stage.startTime).inWholeSeconds.toFloat() / totalDuration

        DrawPoint(
            topLeft = Offset(x = canvasSize.width * stageOffset, y = 0f),
            size = canvasSize.copy(width = canvasSize.width * stageDuration),
        )
    }
}

private fun calculateHours(
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    formatter: DateTimeFormat<DateTimeComponents>,
    canvasSize: Size,
    recordStartTime: Instant,
    recordEndTime: Instant,
): List<Pair<Offset, TextLayoutResult>> {
    val startHour = textMeasurer.measure(
        text = recordStartTime.format(formatter),
        style = textStyle,
        maxLines = 1,
    )
    val startHourOffset = Offset.Zero

    val middleDate = recordStartTime + (recordEndTime - recordStartTime) / 2
    val middleHour = textMeasurer.measure(
        text = middleDate.format(formatter),
        style = textStyle,
        maxLines = 1,
    )
    val middleHourOffset = Offset(x = (canvasSize.width - middleHour.size.width) / 2, y = 0f)

    val endHour = textMeasurer.measure(
        text = recordEndTime.format(formatter),
        style = textStyle,
        maxLines = 1,
    )
    val endHourOffset = Offset(x = canvasSize.width - endHour.size.width, y = 0f)

    return listOf(
        startHourOffset to startHour,
        middleHourOffset to middleHour,
        endHourOffset to endHour,
    )
}

private data class DrawPoint(
    val topLeft: Offset,
    val size: Size,
)

private data class DrawStage(
    val title: TextLayoutResult,
    val type: SleepStageType,
    val points: List<DrawPoint>,
)