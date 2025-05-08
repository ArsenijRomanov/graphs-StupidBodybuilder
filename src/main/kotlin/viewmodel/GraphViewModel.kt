package viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.Graph

class GraphViewModel(
    private val graph: Graph,
    showVerticesElements: State<Boolean>,
    showEdgesWeights: State<Boolean>,
    val defaultVertexRadius: Dp = 25.dp,
) {
    private var _findPathState = mutableStateOf(false)
    var findPathState: Boolean
        get() = _findPathState.value
        set(value) {
            _findPathState.value = value
        }

    private val _verticesToFindPath = mutableStateListOf<Long>()
    val verticesToFindPath: List<Long>
        get() = _verticesToFindPath.toList()

    fun addVertexToFindPath(vertex: Long) = _verticesToFindPath.add(vertex)

    fun clearVerticesToFindPath() = _verticesToFindPath.clear()

    private val _vertices =
        graph.vertices.associateWith { v ->
            VertexViewModel(
                0.dp,
                0.dp,
                Color.Gray,
                this,
                v,
                showVerticesElements,
                defaultVertexRadius,
            )
        }

    private val _edges =
        graph.edges.associateWith { e ->
            val fst =
                _vertices[e.vertices.first]
                    ?: throw IllegalStateException("VertexView for ${e.vertices.first} not found")
            val snd =
                _vertices[e.vertices.second]
                    ?: throw IllegalStateException("VertexView for ${e.vertices.second} not found")
            EdgeViewModel(
                Color.Black,
                1f,
                fst,
                snd,
                e,
                showEdgesWeights,
            )
        }

    val vertices: Collection<VertexViewModel>
        get() = _vertices.values

    val edges: Collection<EdgeViewModel>
        get() = _edges.values

    fun setVertexColor(
        v: Long,
        color: Color,
    )  {
        _vertices[v]?.color = color
    }

    fun setVertexSize(
        v: Long,
        radius: Dp,
    ) = _vertices[v]?.radius = radius

    fun setEdgeColor(
        firstVertex: Long,
        secondVertex: Long,
        color: Color,
    )  {
        val edge = graph.findEdge(firstVertex, secondVertex)
        _edges[edge]?.color = color
    }

    fun setEdgeWidth(
        firstVertex: Long,
        secondVertex: Long,
        width: Float,
    )  {
        val edge = graph.findEdge(firstVertex, secondVertex)
        _edges[edge]?.width = width
    }

    fun reset() {
        this.vertices.forEach { v ->
            v.color = Color.Gray
            v.radius = defaultVertexRadius
        }
        this.edges.forEach { e ->
            e.color = Color.Black
            e.width = 1f
        }
    }
}
