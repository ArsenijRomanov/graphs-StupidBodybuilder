package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import viewmodel.GraphViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GraphView(viewModel: GraphViewModel) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        viewModel.vertices.forEach { v ->
            vertexView(v, Modifier)
        }
        viewModel.edges.forEach { e ->
            edgeView(e, Modifier)
        }
    }
}
