package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import viewmodel.EdgeViewModel

@Composable
fun loopEdgeView(
    viewModel: EdgeViewModel,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { viewModel.firstVertex.radius.toPx() }

    val center =
        Offset(
            x = with(density) { viewModel.firstVertex.x.toPx() },
            y = with(density) { viewModel.firstVertex.y.toPx() + radiusPx },
        )

    val loopRadius = radiusPx

    Canvas(
        modifier =
            modifier
                .fillMaxSize()
                .zIndex(-2f),
    ) {
        drawCircle(
            color = Color.Black,
            radius = loopRadius,
            center = center,
            style = Stroke(width = viewModel.width),
        )
    }

    if (viewModel.weightVisible) {
        val labelY = center.y - loopRadius - 20f
        Text(
            modifier =
                Modifier
                    .zIndex(1f)
                    .offset(
                        x = with(density) { center.x.toDp() },
                        y = with(density) { labelY.toDp() },
                    ),
            text = viewModel.weight,
            fontSize = 14.sp,
            color = Color.Black,
        )
    }
}
