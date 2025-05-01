package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import viewmodel.MainScreenViewModel

@Composable
fun MainScreen(viewModel: MainScreenViewModel) {
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
                Text("Show vertices elements", fontSize = 28.sp, modifier = Modifier.padding(4.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = viewModel.showEdgesWeights,
                    onCheckedChange = { viewModel.showEdgesWeights = it },
                )
                Text("Show edges weights", fontSize = 28.sp, modifier = Modifier.padding(4.dp))
            }

            Button(
                onClick = viewModel::resetGraphView,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Reset default settings")
            }

            Button(
                onClick = viewModel::setVerticesColor,
                enabled = true,
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Set colors")
            }
        }

        Surface(
            modifier = Modifier.weight(1f),
        ) {
            GraphView(viewModel.graphViewModel)
        }
    }
}
