package intagrationTests

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.mockk.*
import model.DirectedGraph
import model.Edge
import org.junit.jupiter.api.BeforeEach
import viewmodel.EdgeViewModel
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.RepresentationStrategy
import viewmodel.VertexViewModel
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainScreenViewModelForDirectedGraphTest {
    private lateinit var graph: DirectedGraph
    private lateinit var representationStrategy: RepresentationStrategy
    private lateinit var viewModel: MainScreenViewModelForDirectedGraph

    @BeforeEach
    fun setUp() {
        representationStrategy = mockk<RepresentationStrategy> {
            every { defaultVertexRadius } returns 16.dp
            every { defaultEdgesWidth } returns 2f

            every { place(any(), any(), any(), any()) } just Runs
            every { resetVertices(any()) } just Runs
            every { resetEdges(any()) } just Runs
        }
    }

    @Test
    fun `init main screen viewModel for directed graph`(){
        graph = DirectedGraph().apply { addEdge(1, 2, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        verify {
            representationStrategy.place(
                width = 800.0,
                height = 600.0,
                vertices = viewModel.graphViewModel.vertices,
                edges = viewModel.graphViewModel.edges
            )
        }
    }

    @Test
    fun `reset graph view test`(){
        graph = DirectedGraph().apply { addEdge(1, 2, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.resetGraphView()
        verify(exactly = 2) {
            representationStrategy.place(
                width = 800.0,
                height = 600.0,
                vertices = viewModel.graphViewModel.vertices,
                edges = viewModel.graphViewModel.edges
            )
        }
    }

    @Test
    fun `default vertices test`(){
        graph = DirectedGraph().apply { addEdge(1, 2, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.defaultVertices()
        verify {
            representationStrategy.resetVertices(viewModel.graphViewModel.vertices)
        }
    }

    @Test
    fun `default edges test`(){
        graph = DirectedGraph().apply { addEdge(1, 2, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.defaultEdges()
        verify {
            representationStrategy.resetEdges(viewModel.graphViewModel.edges)
        }
    }

    @Test
    fun `check for negative weights test`(){
        graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 4)
            addEdge(3, 1, -1)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        assertTrue(viewModel.checkForNegativeWeights())
    }

    @Test
    fun `dijkstra test`(){
        graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(1, 3, 3)
            addEdge(3, 4, 4)
            addEdge(2, 4, 2)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.findPathDijkstra(1, 4)
        val edges = viewModel.graphViewModel.edges.toList().sortedBy { it.weight }

        val firstEdge = edges[0]
        val secondEdge = edges[1]
        val thirdEdge = edges[2]
        val fourthEdge = edges[3]

        val newEdgeColor = Color(0xFF1E88E5)
        val newEdgeWidth = representationStrategy.defaultEdgesWidth * 3

        assertEquals(firstEdge.color, newEdgeColor)
        assertEquals(firstEdge.width, newEdgeWidth)

        assertEquals(secondEdge.color, newEdgeColor)
        assertEquals(secondEdge.width, newEdgeWidth)

        assertEquals(thirdEdge.color, Color.Black)
        assertEquals(thirdEdge.width, representationStrategy.defaultEdgesWidth)

        assertEquals(fourthEdge.color, Color.Black)
        assertEquals(fourthEdge.width, representationStrategy.defaultEdgesWidth)
    }

    @Test
    fun `find a non-existent path`(){
        graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(3, 4, 1)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.findPathDijkstra(1, 4)

        viewModel.graphViewModel.edges.forEach {
            it.width = viewModel.graphViewModel.defaultEdgesWidth
            it.color = Color.Black
        }
    }

    @Test
    fun `find path between non-existent vertices`(){
        graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(3, 4, 1)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.findPathDijkstra(5, 6)

        viewModel.graphViewModel.edges.forEach {
            it.width = viewModel.graphViewModel.defaultEdgesWidth
            it.color = Color.Black
        }
    }

    @Test
    fun `find strongly connected components`(){
        graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 1, 0)

            addEdge(5, 6, 0)
            addEdge(6, 7, 0)
            addEdge(7, 8, 0)
            addEdge(8, 5, 0)

            addEdge(3, 5, 0)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.findStronglyConnectedComponents()

        val vertices = viewModel.graphViewModel.vertices

        var firstColor: Color? = null
        var secondColor: Color? = null

        for (vertex in vertices){
            when (vertex.value){
                1L -> firstColor = vertex.color
                5L -> secondColor = vertex.color
            }
        }

        for (vertex in vertices){
            if (vertex.value in 1..4)
                assertEquals(firstColor, vertex.color)
            else
                assertEquals(secondColor, vertex.color)
        }
    }

    @Test
    fun `find strongly connected components in empty graph`(){
        graph = DirectedGraph()
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.findStronglyConnectedComponents()
    }

    @Test
    fun `highlight key vertices test`(){
        graph = DirectedGraph().apply {
            addEdge(2, 1, 0)
            addEdge(3, 1, 0)
            addEdge(4, 1, 0)

            addEdge(6, 5, 0)
            addEdge(7, 5, 0)

            addEdge(9, 8, 0)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.highlightKeyVertices()
        val vertices = viewModel.graphViewModel.vertices.toList().sortedBy { it.value }

        for (v in vertices){
            when (v.value){
                1L -> assertEquals(v.radius, 64.dp)
                5L -> assertEquals(v.radius, 48.dp)
                8L -> assertEquals(v.radius, 32.dp)
                else -> assertEquals(v.radius, 16.dp)
            }
        }
    }

    @Test
    fun `highlight key vertices in empty graph`(){
        graph = DirectedGraph()
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.highlightKeyVertices()
    }

    @Test
    fun `highlight key vertices in graph with one vertex`(){
        graph = DirectedGraph().apply { addVertex(1) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.highlightKeyVertices()

        assertEquals(
            viewModel.graphViewModel.defaultVertexRadius,
            viewModel.graphViewModel.vertices.first().radius
        )
    }

    @Test
    fun `highlight key vertices in graph with two vertices`(){
        graph = DirectedGraph().apply { addEdge(1, 2, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.highlightKeyVertices()
        val vertices = viewModel.graphViewModel.vertices.toList().sortedBy { it.value }

        val firstVertexRadius = vertices[0].radius
        val secondVertexRadius = vertices[1].radius

        assertEquals(
            viewModel.graphViewModel.defaultVertexRadius * 4,
            secondVertexRadius
        )
        assertEquals(
            viewModel.graphViewModel.defaultVertexRadius,
            firstVertexRadius
        )
    }

    @Test
    fun `highlight key vertices in graph without edges`(){
        graph = DirectedGraph().apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)
            addVertex(4)
            addVertex(5)
        }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.highlightKeyVertices()
        val vertices = viewModel.graphViewModel.vertices.toList()
        for (v in vertices){
            assertEquals(viewModel.graphViewModel.defaultVertexRadius, v.radius)
        }
    }

    @Test
    fun `vertex values and edge weights are hidden`(){
        graph = DirectedGraph().apply { addEdge(2, 1, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)

        assertFalse(viewModel.showVerticesElements)
        assertFalse(viewModel.showEdgesWeights)
    }

    @Test
    fun `set vertex values and edge weights `(){
        graph = DirectedGraph().apply { addEdge(2, 1, 0) }
        viewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        viewModel.showVerticesElements = true
        viewModel.showEdgesWeights = true

        assertTrue(viewModel.showVerticesElements)
        assertTrue(viewModel.showEdgesWeights)
    }

}