package view

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import viewmodel.EdgeViewModel

@Composable
fun undirectedEdgeView(
    viewModel: EdgeViewModel,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawLine(
            start =
                Offset(
                    viewModel.firstVertex.x.toPx() + viewModel.firstVertex.radius.toPx(),
                    viewModel.firstVertex.y.toPx() + viewModel.firstVertex.radius.toPx(),
                ),
            end =
                Offset(
                    viewModel.secondVertex.x.toPx() + viewModel.secondVertex.radius.toPx(),
                    viewModel.secondVertex.y.toPx() + viewModel.secondVertex.radius.toPx(),
                ),
            color = viewModel.color,
            strokeWidth = viewModel.width,
        )
    }
    if (viewModel.weightVisible) {
        Text(
            modifier =
                Modifier
                    .offset(
                        viewModel.firstVertex.x + (viewModel.secondVertex.x - viewModel.firstVertex.x) / 2,
                        viewModel.firstVertex.y + (viewModel.secondVertex.y - viewModel.firstVertex.y) / 2,
                    ),
            text = viewModel.weight,
        )
    }
}
