package com.lmw.watermonitorandroid.domain.sensor.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class RingConfig(
    val label: String,
    val value: Float,
    val maxValue: Float,
    val color: Color,
    val unit: String = ""
)

@Composable
fun RainbowGauge(
    rings: List<RingConfig>,
    centerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    gap: Dp = 6.dp,
    strokeWidths: List<Dp>? = null,
    backgroundArc: Boolean = true,
    roundCap: Boolean = true,
    animEnabled: Boolean = true,
    animDurationMs: Int = 800
) {
    val density = LocalDensity.current
    val gapPx = with(density) { gap.toPx() }

    val defaultStrokeWidths = listOf(22, 18, 16, 14, 12).map { it.dp }
    val resolvedStrokeWidths = if (strokeWidths != null) {
        strokeWidths.take(rings.size) + defaultStrokeWidths.drop(strokeWidths.size)
    } else {
        defaultStrokeWidths
    }
    val actualStrokeWidths = resolvedStrokeWidths.take(rings.size)
    val strokeWidthsPx = actualStrokeWidths.map { with(density) { it.toPx() } }

    var touchedRingIndex by remember { mutableIntStateOf(-1) }
    var bubbleOffset by remember { mutableStateOf<Offset?>(null) }

    val animatedValues = rings.mapIndexed { index, ring ->
        val target = (ring.value / ring.maxValue.coerceAtLeast(1f)).coerceIn(0f, 1f)
        if (animEnabled) {
            val animated by animateFloatAsState(
                targetValue = target,
                animationSpec = tween(durationMillis = animDurationMs),
                label = "ring_$index"
            )
            animated
        } else {
            target
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val canvasWidthPx = with(density) { maxWidth.toPx() }
        val availableHeightPx = with(density) { maxHeight.toPx() }
        val maxRadius = canvasWidthPx / 2f
        val totalArcPx = strokeWidthsPx.sum() + (rings.size - 1) * gapPx
        val gaugeRadius = maxRadius - with(density) { 8.dp.toPx() }
        val centerYOffset = with(density) { 8.dp.toPx() }
        val idealHeightPx = gaugeRadius + centerYOffset
        val gaugeHeightPx = if (availableHeightPx > 0f && availableHeightPx < idealHeightPx) availableHeightPx else idealHeightPx
        val scaleY = if (idealHeightPx > 0f) gaugeHeightPx / idealHeightPx else 1f
        val gaugeHeightDp = with(density) { gaugeHeightPx.toDp() }

        val innerCumOffset = strokeWidthsPx.sum() + (rings.size - 1) * gapPx
        val innerR = gaugeRadius - innerCumOffset
        val centroidFromBaselineDp = with(density) { (innerR * 0.4f * scaleY).toDp() }
        val centerContentOffsetDp = gaugeHeightDp / 2f - centroidFromBaselineDp

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(gaugeHeightDp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(gaugeHeightDp)
                    .graphicsLayer(scaleY = scaleY)
                    .pointerInput(rings.size) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                val cw = size.width.toFloat()
                                val idx = findRingAt(
                                    offset, rings.size, gaugeRadius,
                                    strokeWidthsPx, gapPx, cw
                                )
                                touchedRingIndex = idx
                                if (idx >= 0) {
                                    bubbleOffset = calcBubblePos(
                                        idx, rings[idx], gaugeRadius,
                                        strokeWidthsPx, gapPx, cw
                                    )
                                } else {
                                    bubbleOffset = null
                                }
                            },
                            onDrag = { change, _ ->
                                val cw = size.width.toFloat()
                                val idx = findRingAt(
                                    change.position, rings.size, gaugeRadius,
                                    strokeWidthsPx, gapPx, cw
                                )
                                touchedRingIndex = idx
                                if (idx >= 0) {
                                    bubbleOffset = calcBubblePos(
                                        idx, rings[idx], gaugeRadius,
                                        strokeWidthsPx, gapPx, cw
                                    )
                                } else {
                                    bubbleOffset = null
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                touchedRingIndex = -1
                                bubbleOffset = null
                            },
                            onDragCancel = {
                                touchedRingIndex = -1
                                bubbleOffset = null
                            }
                        )
                    }
            ) {
                val centerX = size.width / 2f
                val centerY = gaugeRadius + centerYOffset
                val cap = if (roundCap) StrokeCap.Round else StrokeCap.Butt

                var cumulativeOffset = 0f
                rings.forEachIndexed { index, _ ->
                    val strokeW = strokeWidthsPx[index]
                    val outerR = gaugeRadius - cumulativeOffset
                    val midR = outerR - strokeW / 2f
                    cumulativeOffset += strokeW + gapPx

                    if (backgroundArc) {
                        drawArc(
                            color = rings[index].color.copy(alpha = 0.15f),
                            startAngle = 180f,
                            sweepAngle = 180f,
                            useCenter = false,
                            topLeft = Offset(centerX - midR, centerY - midR),
                            size = Size(midR * 2f, midR * 2f),
                            style = Stroke(width = strokeW, cap = cap)
                        )
                    }

                    val sweep = animatedValues[index] * 180f
                    if (sweep > 0.3f) {
                        drawArc(
                            color = rings[index].color,
                            startAngle = 180f,
                            sweepAngle = sweep,
                            useCenter = false,
                            topLeft = Offset(centerX - midR, centerY - midR),
                            size = Size(midR * 2f, midR * 2f),
                            style = Stroke(width = strokeW, cap = cap)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = centerContentOffsetDp),
                contentAlignment = Alignment.Center
            ) {
                centerContent()
            }

            if (touchedRingIndex >= 0 && touchedRingIndex < rings.size && bubbleOffset != null) {
                val ring = rings[touchedRingIndex]
                val pos = bubbleOffset!!
                Text(
                    text = "${ring.label}: ${String.format("%.1f", ring.value)} ${ring.unit}",
                    color = ring.color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .offset { IntOffset(pos.x.toInt(), pos.y.toInt()) }
                        .background(
                            color = Color.White.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun findRingAt(
    point: Offset,
    ringCount: Int,
    radius: Float,
    widths: List<Float>,
    gap: Float,
    canvasWidth: Float
): Int {
    val centerX = canvasWidth / 2f
    val dx = point.x - centerX
    val dy = point.y - (radius + 8f)
    val dist = sqrt(dx * dx + dy * dy)

    var cumOffset = 0f
    for (i in 0 until ringCount) {
        val outerR = radius - cumOffset
        val innerR = outerR - widths[i]
        if (dist in innerR..outerR) return i
        cumOffset += widths[i] + gap
    }
    return -1
}

private fun calcBubblePos(
    index: Int,
    ring: RingConfig,
    radius: Float,
    widths: List<Float>,
    gap: Float,
    canvasWidth: Float
): Offset {
    var cumOffset = 0f
    for (i in 0 until index) {
        cumOffset += widths[i] + gap
    }
    val midR = radius - cumOffset - widths[index] / 2f
    val progress = (ring.value / ring.maxValue.coerceAtLeast(1f)).coerceIn(0f, 1f)
    val angleRad = Math.toRadians((180.0 - progress * 180.0))
    val centerX = canvasWidth / 2f
    val centerY = radius + 8f
    val x = centerX + midR * cos(angleRad).toFloat()
    val y = centerY - midR * sin(angleRad).toFloat()
    return Offset(x - 40f, y - 40f)
}