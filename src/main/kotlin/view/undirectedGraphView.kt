package view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import viewmodel.GraphViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun undirectedGraphView(viewModel: GraphViewModel) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        viewModel.edges.forEach { e ->
            if (e.firstVertex.value != e.secondVertex.value) {
                undirectedEdgeView(e, Modifier)
            } else {
                loopEdgeView(e, Modifier)
            }
        }

        viewModel.vertices.forEach { v ->
            vertexView(v, Modifier)
        }
    }
}
