package view

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import viewmodel.VertexViewModel

@Composable
fun vertexView(
    viewModel: VertexViewModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(viewModel.radius * 2, viewModel.radius * 2)
                .offset(viewModel.x, viewModel.y)
                .zIndex(-1f)
                .background(
                    color = viewModel.color,
                    shape = CircleShape,
                )
                .pointerInput(viewModel) {
                    detectTapGestures(
                        onTap = {
                            viewModel.onClick()
                        },
                    )
                }
                .pointerInput(viewModel) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        viewModel.onDrag(dragAmount)
                    }
                },
    ) {
        if (viewModel.valueVisible) {
            Text(
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .offset(0.dp, -viewModel.radius - 10.dp),
                text = viewModel.value.toString(),
            )
        }
    }
}
