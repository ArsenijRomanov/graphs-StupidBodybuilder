package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import viewmodel.EdgeViewModel
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun directedEdgeView(
    viewModel: EdgeViewModel,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val vertex1Center = Offset(
            viewModel.firstVertex.x.toPx() + viewModel.firstVertex.radius.toPx(),
            viewModel.firstVertex.y.toPx() + viewModel.firstVertex.radius.toPx(),
        )
        val vertex2Center = Offset(
            viewModel.secondVertex.x.toPx() + viewModel.secondVertex.radius.toPx(),
            viewModel.secondVertex.y.toPx() + viewModel.secondVertex.radius.toPx(),
        )

        val direction = vertex2Center - vertex1Center
        val distance = sqrt(direction.x * direction.x + direction.y * direction.y)

        val normalizedDirection = if (distance > 0) direction / distance else direction

        val start = vertex1Center + normalizedDirection * viewModel.firstVertex.radius.toPx()
        val end = vertex2Center - normalizedDirection * viewModel.secondVertex.radius.toPx()

        drawLine(
            start = start,
            end = end,
            color = viewModel.color,
            strokeWidth = viewModel.width,
        )

        val angle = atan2(end.y - start.y, end.x - start.x)
        val arrowLength = 15f
        val arrowAngle = Math.toRadians(30.0).toFloat()

        val arrowPoint1 =
            Offset(
                x = end.x - arrowLength * cos(angle - arrowAngle),
                y = end.y - arrowLength * sin(angle - arrowAngle),
            )
        val arrowPoint2 =
            Offset(
                x = end.x - arrowLength * cos(angle + arrowAngle),
                y = end.y - arrowLength * sin(angle + arrowAngle),
            )

        drawLine(
            color = viewModel.color,
            start = end,
            end = arrowPoint1,
            strokeWidth = viewModel.width,
        )

        drawLine(
            color = viewModel.color,
            start = end,
            end = arrowPoint2,
            strokeWidth = viewModel.width,
        )
    }

    if (viewModel.weightVisible) {
        val density = LocalDensity.current
        with(density) {
            val x1 = viewModel.firstVertex.x.toPx() + viewModel.firstVertex.radius.toPx()
            val y1 = viewModel.firstVertex.y.toPx() + viewModel.firstVertex.radius.toPx()
            val x2 = viewModel.secondVertex.x.toPx() + viewModel.secondVertex.radius.toPx()
            val y2 = viewModel.secondVertex.y.toPx() + viewModel.secondVertex.radius.toPx()

            val midX = (x1 + x2) / 2
            val midY = (y1 + y2) / 2

            val dx = x2 - x1
            val dy = y2 - y1
            val length = sqrt(dx * dx + dy * dy)
            val offsetAmount = 20f

            val offsetX = -dy / length * offsetAmount
            val offsetY = dx / length * offsetAmount

            val finalX = midX + offsetX
            val finalY = midY + offsetY

            Text(
                modifier =
                    Modifier.offset(
                        x = finalX.toDp(),
                        y = finalY.toDp(),
                    ),
                text = viewModel.weight,
            )
        }
    }
}
