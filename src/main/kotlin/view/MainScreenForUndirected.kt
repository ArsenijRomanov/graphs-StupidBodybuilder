package view

import ZoomableBox
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.MainScreenViewModelForUndirectedGraph

@Composable
fun MainScreenForUndirected(viewModel: MainScreenViewModelForUndirectedGraph) {
    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.graphViewModel.verticesToFindPath }
            .collect { pathList ->
                if (pathList.size == 2) {
                    viewModel.findPathDijkstra(pathList[0], pathList[1])
                    viewModel.graphViewModel.clearVerticesToFindPath()
                    viewModel.graphViewModel.findPathState = false
                }
            }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Column(
            modifier =
                Modifier
                    .background(Color(red = 224, green = 224, blue = 224)) // Серый фон
                    .width(370.dp)
                    .fillMaxHeight() // Занимает всю высоту родителя
                    .padding(16.dp), // Добавим немного отступов
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.showVerticesElements,
                    onCheckedChange = { viewModel.showVerticesElements = it },
                )
                Text(
                    "Show vertices elements",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(4.dp),
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.showEdgesWeights,
                    onCheckedChange = { viewModel.showEdgesWeights = it },
                )
                Text(
                    "Show edges weights",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(4.dp),
                )
            }

            Button(
                onClick = viewModel::resetGraphView,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1976D2), // тот же цвет, что и обычный для Dijkstra
                        contentColor = Color.White,
                    ),
            ) {
                Text("Reset default settings")
            }

            Button(
                onClick = viewModel::defaultVertices,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1976D2), // тот же цвет, что и обычный для Dijkstra
                        contentColor = Color.White,
                    ),
            ) {
                Text("Reset vertices")
            }

            Button(
                onClick = viewModel::defaultEdges,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1976D2), // тот же цвет, что и обычный для Dijkstra
                        contentColor = Color.White,
                    ),
            ) {
                Text("Reset edges")
            }

            Button(
                onClick = viewModel::highlightKeyVertices,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF1976D2), // тот же цвет, что и обычный для Dijkstra
                        contentColor = Color.White,
                    ),
            ) {
                Text("get leaders")
            }

            if (!viewModel.checkForNegativeWeights()) {
                Button(
                    onClick = {
                        viewModel.graphViewModel.clearVerticesToFindPath()
                        viewModel.graphViewModel.findPathState = !viewModel.graphViewModel.findPathState
                    },
                    enabled = true,
                    modifier = Modifier.padding(top = 8.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            backgroundColor =
                                if (viewModel.graphViewModel.findPathState) {
                                    Color(0xFF1565C0)
                                } else {
                                    Color(0xFF1976D2)
                                },
                            contentColor = Color.White,
                        ),
                ) {
                    Text("Dijkstra")
                }
            }
        }

        Surface(
            modifier = Modifier.weight(1f)
        ) {
            ZoomableBox {
                undirectedGraphView(viewModel.graphViewModel)
            }
        }
    }
}
