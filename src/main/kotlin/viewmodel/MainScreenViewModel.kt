package viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.Graph

class MainScreenViewModel(graph: Graph, private val representationStrategy: RepresentationStrategy) {
    private var _showVerticesElements = mutableStateOf(false)
    var showVerticesElements: Boolean
        get() = _showVerticesElements.value
        set(value) {
            _showVerticesElements.value = value
        }

    private var _showEdgesWeights = mutableStateOf(false)
    var showEdgesWeights: Boolean
        get() = _showEdgesWeights.value
        set(value) {
            _showEdgesWeights.value = value
        }

    val graphViewModel = GraphViewModel(graph, _showVerticesElements, _showEdgesWeights)

    init {
        representationStrategy.place(800.0, 600.0, graphViewModel.vertices)
    }

    fun resetGraphView() {
        representationStrategy.place(800.0, 600.0, graphViewModel.vertices)
        graphViewModel.vertices.forEach { v -> v.color = Color.Gray }
    }

    fun setVerticesColor() {
        representationStrategy.highlight(graphViewModel.vertices)
    }
}
