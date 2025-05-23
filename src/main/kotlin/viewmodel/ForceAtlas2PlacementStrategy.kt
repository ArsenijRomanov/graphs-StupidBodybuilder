package viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.gephi.graph.api.GraphController
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2
import org.gephi.project.api.ProjectController
import org.openide.util.Lookup
import kotlin.math.*

class ForceAtlas2Layout(
    override val defaultVertexRadius: Dp = 25.dp,
    override val defaultEdgesWidth: Float = 1f
) : RepresentationStrategy {

    private companion object {
        const val PADDING_FACTOR = 1.5
        const val OVERLAP_PREVENTION = 1.5
        const val MAX_ITERATIONS = 1000
        const val MIN_DISTANCE_BETWEEN_NODES = 10.0
    }

    override fun place(
        width: Double,
        height: Double,
        vertices: Collection<VertexViewModel>,
        edges: Collection<EdgeViewModel>
    ) {
        if (vertices.isEmpty()) return

        val pc = Lookup.getDefault().lookup(ProjectController::class.java)
        pc.newProject()
        val graphModel = Lookup.getDefault().lookup(GraphController::class.java).graphModel
        val graph = graphModel.undirectedGraph

        val centerX = 0.0f
        val centerY = 0.0f
        val radius = 100.0f
        val angleStep = 2 * PI / vertices.size

        vertices.forEachIndexed { index, vertex ->
            val node = graphModel.factory().newNode(vertex.value.toString())
            val angle = angleStep * index
            node.setX(centerX + radius * cos(angle).toFloat())
            node.setY(centerY + radius * sin(angle).toFloat())
            graph.addNode(node)
        }

        edges.forEach { edge ->
            val sourceNode = graph.getNode(edge.firstVertex.value.toString()) ?: return@forEach
            val targetNode = graph.getNode(edge.secondVertex.value.toString()) ?: return@forEach
            graph.addEdge(graphModel.factory().newEdge(sourceNode, targetNode, 1, false))
        }

        val layout = ForceAtlas2(null).apply {
            setGraphModel(graphModel)
            initAlgo()
            resetPropertiesValues()
            isAdjustSizes = true
            isBarnesHutOptimize = true
            scalingRatio = calculateScalingRatio(vertices.size)
            gravity = 0.01
            jitterTolerance = 0.5
            barnesHutTheta = 1.2
            edgeWeightInfluence = 0.5
        }

        var lastImprovement = 0
        var lastOverlap = Double.MAX_VALUE

        repeat(MAX_ITERATIONS) {
            if (layout.canAlgo()) {
                layout.goAlgo()

                // Проверка на улучшение (уменьшение наложения)
                val currentOverlap = calculateOverlap(graph)
                if (currentOverlap < lastOverlap) {
                    lastOverlap = currentOverlap
                    lastImprovement = it
                } else if (it - lastImprovement > 50) {
                    return@repeat
                }
            }
        }
        layout.endAlgo()

        scaleAndCenterGraph(width, height, vertices, graph)
    }

    private fun calculateOverlap(graph: org.gephi.graph.api.Graph): Double {
        var overlap = 0.0
        val nodes = graph.nodes.toList()

        for (i in nodes.indices) {
            for (j in i+1 until nodes.size) {
                val node1 = nodes[i]
                val node2 = nodes[j]
                val distance = sqrt(
                    (node1.x() - node2.x()).pow(2) +
                            (node1.y() - node2.y()).pow(2))
                            overlap += max(0.0, MIN_DISTANCE_BETWEEN_NODES - distance)
            }
        }
        return overlap
    }

    private fun calculateScalingRatio(vertexCount: Int): Double {
        return when {
            vertexCount < 10 -> 10.0
            vertexCount < 50 -> 5.0
            vertexCount < 100 -> 2.0
            else -> 1.0
        }
    }

    private fun scaleAndCenterGraph(
        width: Double,
        height: Double,
        vertices: Collection<VertexViewModel>,
        graph: org.gephi.graph.api.Graph
    ) {
        if (vertices.isEmpty()) return

        // Вычисляем границы графа
        val (minX, maxX, minY, maxY) = calculateGraphBounds(graph)
        val graphWidth = maxX - minX
        val graphHeight = maxY - minY

        val vertexDiameter = defaultVertexRadius.value * 2 * OVERLAP_PREVENTION
        val padding = vertexDiameter * PADDING_FACTOR

        val effectiveWidth = maxOf(graphWidth, MIN_DISTANCE_BETWEEN_NODES * vertices.size / 2)
        val effectiveHeight = maxOf(graphHeight, MIN_DISTANCE_BETWEEN_NODES * vertices.size / 2)

        val scaleX = (width - padding * 2) / effectiveWidth
        val scaleY = (height - padding * 2) / effectiveHeight
        val scale = minOf(scaleX, scaleY)

        val centerX = width / 2
        val centerY = height / 2
        val graphCenterX = (minX + maxX) / 2
        val graphCenterY = (minY + maxY) / 2

        vertices.forEach { vertex ->
            val node = graph.getNode(vertex.value.toString())
            val scaledX = centerX + (node.x() - graphCenterX) * scale
            val scaledY = centerY + (node.y() - graphCenterY) * scale

            vertex.x = scaledX.coerceIn(padding, width - padding).dp
            vertex.y = scaledY.coerceIn(padding, height - padding).dp
        }
    }

    private fun calculateGraphBounds(graph: org.gephi.graph.api.Graph): Quadruple<Double, Double, Double, Double> {
        if (graph.nodeCount == 0) return Quadruple(0.0, 0.0, 0.0, 0.0)

        var minX = Double.MAX_VALUE
        var maxX = -Double.MAX_VALUE
        var minY = Double.MAX_VALUE
        var maxY = -Double.MAX_VALUE

        graph.nodes.forEach { node ->
            minX = min(minX, node.x().toDouble())
            maxX = max(maxX, node.x().toDouble())
            minY = min(minY, node.y().toDouble())
            maxY = max(maxY, node.y().toDouble())
        }

        return Quadruple(minX, maxX, minY, maxY)
    }

    override fun resetVertices(vertices: Collection<VertexViewModel>) {
        vertices.forEach {
            it.color = Color.Gray
            it.radius = defaultVertexRadius
        }
    }

    override fun resetEdges(edges: Collection<EdgeViewModel>) {
        edges.forEach {
            it.color = Color.Black
            it.width = defaultEdgesWidth
        }
    }
}

private data class Quadruple<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)