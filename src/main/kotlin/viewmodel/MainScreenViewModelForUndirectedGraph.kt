package viewmodel

import algos.checkGraphForNegativeWeight
import algos.dijkstra
import algos.leaderRank
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import model.UndirectedGraph

class MainScreenViewModelForUndirectedGraph(
    private val graph: UndirectedGraph,
    private val representationStrategy: RepresentationStrategy,
) {
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
        representationStrategy.place(
            800.0,
            600.0,
            graphViewModel.vertices,
        )
    }

    fun checkForNegativeWeights(): Boolean = !checkGraphForNegativeWeight(graph)

    fun resetGraphView() {
        representationStrategy.place(
            800.0,
            600.0,
            graphViewModel.vertices,
        )
        graphViewModel.reset()
    }

    fun setVerticesColor() {
        representationStrategy.highlight(graphViewModel.vertices)
    }

    fun findPathDijkstra(
        firstVertex: Long,
        secondVertex: Long,
    )  {
        val path = dijkstra(graph, firstVertex, secondVertex) ?: return
        for (i in 0..path.size - 2) {
            graphViewModel.setEdgeColor(
                path[i],
                path[i + 1],
                Color(0xFF1E88E5),
            )

            graphViewModel.setEdgeWidth(
                path[i],
                path[i + 1],
                5f,
            )
        }
    }

    fun getLeaders() {
        val verticesRanks = leaderRank(graph)
        val first = maxBelow(verticesRanks, Double.POSITIVE_INFINITY) ?: return
        graphViewModel.setVertexSize(first.first, graphViewModel.defaultVertexRadius * 2)

        val second = maxBelow(verticesRanks, first.second) ?: return
        graphViewModel.setVertexSize(second.first, graphViewModel.defaultVertexRadius * 1.5f)

        val third = maxBelow(verticesRanks, second.second) ?: return
        graphViewModel.setVertexSize(third.first, graphViewModel.defaultVertexRadius * 1.25f)
    }

    private fun maxBelow(
        map: Map<Long, Double>,
        threshold: Double,
    ): Pair<Long, Double>?  {
        var vertexToRank: Pair<Long, Double>? = null
        for ((vertex, rank) in map) {
            if (rank < threshold && (vertexToRank == null || vertexToRank.second < rank)) {
                vertexToRank = vertex to rank
            }
        }

        return vertexToRank
    }
}
