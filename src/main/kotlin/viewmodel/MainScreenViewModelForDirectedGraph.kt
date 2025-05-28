package viewmodel

import algos.SCC
import algos.checkGraphForNegativeWeight
import algos.dijkstra
import algos.leaderRank
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import model.DirectedGraph
import model.fordBellman

class MainScreenViewModelForDirectedGraph(
    private val graph: DirectedGraph,
    private val representationStrategy: RepresentationStrategy,
) : MainScreenViewModel {
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

    override val graphViewModel =
        GraphViewModel(
            graph,
            _showVerticesElements,
            _showEdgesWeights,
            representationStrategy.defaultVertexRadius,
            representationStrategy.defaultEdgesWidth
        )

    init {
        representationStrategy.place(
            1050.0,
            1050.0,
            graphViewModel.vertices,
            graphViewModel.edges
        )
    }

    fun checkForNegativeWeights(): Boolean = checkGraphForNegativeWeight(graph)

    fun resetGraphView() {
        representationStrategy.place(
            1050.0,
            1050.0,
            graphViewModel.vertices,
            graphViewModel.edges
        )
        graphViewModel.reset()
    }

    fun defaultVertices() {
        representationStrategy.resetVertices(graphViewModel.vertices)
    }

    fun defaultEdges(){
        representationStrategy.resetEdges(graphViewModel.edges)
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
                graphViewModel.defaultEdgesWidth * 3,
            )
        }
    }

    fun findPathFordBellman(
        firstVertex: Long,
        secondVertex: Long) {
        val path = fordBellman(graph, firstVertex, secondVertex) ?: return
        for (i in 0..path.size - 2) {
            graphViewModel.setEdgeColor(
                path[i],
                path[i + 1],
                Color(0xFF1E88E5),
            )

            graphViewModel.setEdgeWidth(
                path[i],
                path[i + 1],
                graphViewModel.defaultEdgesWidth * 3,
            )
        }
    }

    fun findStronglyConnectedComponents() {
        val scc = SCC(graph)
        val cnt = scc.size
        val colors = generateDistinctColors(cnt)
        for (i in 0..cnt - 1) {
            for (v in scc[i])
                graphViewModel.setVertexColor(v, colors[i])
        }
    }

    fun highlightKeyVertices() {
        val verticesRanks: Map<Long, Double> = leaderRank(graph)
        val defaultRadius: Dp = graphViewModel.defaultVertexRadius

        val minRank = verticesRanks.values.minOrNull() ?: return
        val maxRank = verticesRanks.values.maxOrNull() ?: return

        val range = maxRank - minRank
        if (range == 0.0) return

        for ((vertexId, rank) in verticesRanks) {
            // t ∈ [0.0, 1.0]
            val t: Double = (rank - minRank) / range
            // scale ∈ [1.0, 4.0]
            val scale: Double = 1.0 + 3.0 * t
            val newRadius: Dp = defaultRadius * scale.toFloat()
            graphViewModel.setVertexSize(vertexId, newRadius)
        }
    }

    private fun generateDistinctColors(n: Int): List<Color> {
        return List(n) { i ->
            val hue = (i * 360f / n) % 360f
            val color = Color.hsl(hue, 0.6f, 0.5f)
            color
        }
    }
}
