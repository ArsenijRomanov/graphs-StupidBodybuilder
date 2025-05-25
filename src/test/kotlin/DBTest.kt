import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import model.DirectedGraph
import model.UndirectedGraph
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import saving.GraphRepository
import viewmodel.*
import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DBTest {
    private lateinit var connection: Connection
    private lateinit var repository: GraphRepository
    private lateinit var representationStrategy: RepresentationStrategy

    @BeforeEach
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        repository = GraphRepository(connection)
        representationStrategy = mockk<RepresentationStrategy> {
            every { defaultVertexRadius } returns 16.dp
            every { defaultEdgesWidth } returns 2f

            every { place(any(), any(), any(), any()) } just Runs
            every { resetVertices(any()) } just Runs
            every { resetEdges(any()) } just Runs
        }
    }

    @AfterEach
    fun tearDown() {
        repository.close()
    }

    fun assertVerticesAreEqual(
        firstVertices: Collection<VertexViewModel>,
        secondVertices: Collection<VertexViewModel>
    ) {
        assertEquals(secondVertices.size, firstVertices.size)
        val sortedFirstVertices = firstVertices.toList().sortedBy { it.value }
        val sortedSecondVertices = secondVertices.toList().sortedBy { it.value }

        for (i in 0..<sortedFirstVertices.size) {
            val first = sortedFirstVertices[i]
            val second = sortedSecondVertices[i]
            assertEquals(first.value, second.value)
            assertEquals(first.color, second.color)
            assertEquals(first.radius, second.radius)
            assertEquals(first.x, second.x)
            assertEquals(first.y, second.y)
        }
    }

    fun assertDirectedEdgesAreEqual(firstEdges: Collection<EdgeViewModel>, secondEdges: Collection<EdgeViewModel>) {
        assertEquals(firstEdges.size, secondEdges.size)
        val sortedFirstEdges = firstEdges
            .toList()
            .sortedWith(
                compareBy<EdgeViewModel> { it.firstVertex.value }
                    .thenBy { it.secondVertex.value }
            )
        val sortedSecondEdges = secondEdges
            .toList()
            .sortedWith(
                compareBy<EdgeViewModel> { it.firstVertex.value }
                    .thenBy { it.secondVertex.value }
            )

        for (i in 0..<sortedFirstEdges.size) {
            val first = sortedFirstEdges[i]
            val second = sortedSecondEdges[i]
            assertEquals(first.firstVertex.value, second.firstVertex.value)
            assertEquals(first.secondVertex.value, second.secondVertex.value)
            assertEquals(first.weight, second.weight)
            assertEquals(first.color, second.color)
            assertEquals(first.width, second.width)
        }
    }

    private data class NormalizedEdge(
        val vertexA: Long,
        val vertexB: Long,
        val color: Int,
        val width: Float,
        val weight: String
    )

    private fun EdgeViewModel.toNormalizedEdge(): NormalizedEdge {
        val v1 = firstVertex.value
        val v2 = secondVertex.value
        val (a, b) = if (v1 <= v2) v1 to v2 else v2 to v1
        return NormalizedEdge(
            vertexA = a,
            vertexB = b,
            color = color.toArgb(),
            width = width,
            weight = weight
        )
    }

    fun assertUndirectedEdgesAreEqual(
        expected: Collection<EdgeViewModel>,
        actual: Collection<EdgeViewModel>
    ) {
        val expSet = expected.map { it.toNormalizedEdge() }.toSet()
        val actSet = actual.map { it.toNormalizedEdge() }.toSet()

        assertEquals(expSet, actSet)
    }

    fun assertGraphsAreEqual(firstGraph: MainScreenViewModel, secondGraph: MainScreenViewModel) {
        assertEquals(
            firstGraph is MainScreenViewModelForDirectedGraph,
            secondGraph is MainScreenViewModelForDirectedGraph
        )
        assertVerticesAreEqual(firstGraph.graphViewModel.vertices, secondGraph.graphViewModel.vertices)
        if (firstGraph is MainScreenViewModelForDirectedGraph)
            assertDirectedEdgesAreEqual(firstGraph.graphViewModel.edges, secondGraph.graphViewModel.edges)
        else
            assertUndirectedEdgesAreEqual(firstGraph.graphViewModel.edges, secondGraph.graphViewModel.edges)
    }

    @Test
    fun `save directed graph`() {
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val mainScreen = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        repository.addGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            true
        )
        assertTrue(repository.graphExists("sampleGraph"))
    }

    @Test
    fun `save undirected graph`() {
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val mainScreen = MainScreenViewModelForUndirectedGraph(graph, representationStrategy)
        repository.addGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            false
        )
        assertTrue(repository.graphExists("sampleGraph"))
    }

    @Test
    fun `should throw exception if graph exists`() {
        val firstGraph = DirectedGraph().apply { addEdge(1, 2, 10) }
        val secondGraph = UndirectedGraph().apply { addEdge(2, 2, 10) }
        val firstMainScreen =
            MainScreenViewModelForDirectedGraph(firstGraph, representationStrategy)
        val secondMainScreen =
            MainScreenViewModelForUndirectedGraph(secondGraph, representationStrategy)
        repository.addGraph(
            firstMainScreen.graphViewModel,
            "sampleGraph",
            true
        )
        shouldThrow<IllegalStateException> {
            repository.addGraph(
                secondMainScreen.graphViewModel,
                "sampleGraph",
                false
            )
        }
    }

    @Test
    fun `load directed graph`() {
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val mainScreen = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        mainScreen.graphViewModel.vertices.forEach {
            it.color = Color.Red
            it.radius = representationStrategy.defaultVertexRadius * 4
            it.y = 100.dp
            it.x = 100.dp
        }
        mainScreen.graphViewModel.edges.forEach {
            it.color = Color.Blue
            it.width = 10f
        }

        repository.addGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            true
        )

        val loaded = repository.loadGraph("sampleGraph")
        assertGraphsAreEqual(mainScreen, loaded)
    }

    @Test
    fun `load undirected graph`() {
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val mainScreen = MainScreenViewModelForUndirectedGraph(graph, representationStrategy)
        mainScreen.graphViewModel.vertices.forEach {
            it.color = Color.Red
            it.radius = representationStrategy.defaultVertexRadius * 4
            it.y = 100.dp
            it.x = 100.dp
        }
        mainScreen.graphViewModel.edges.forEach {
            it.color = Color.Blue
            it.width = 10f
        }

        repository.addGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            false
        )

        val loaded = repository.loadGraph("sampleGraph")
        assertGraphsAreEqual(mainScreen, loaded)
    }

    @Test
    fun `delete graph`() {
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val mainScreen = MainScreenViewModelForUndirectedGraph(graph, representationStrategy)
        repository.addGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            false
        )
        repository.deleteGraph("sampleGraph")

        var graphID = 0

        val sqlGetGraphID = "SELECT graph_id FROM Graphs WHERE name = ?"
        connection.prepareStatement(sqlGetGraphID).use { pstmt ->
            pstmt.setString(1, "sampleGraph")
            val result = pstmt.executeQuery()
            if (result.next()) {
                graphID = result.getInt("graphID")
            }
        }

        val sqlGetVertices = "SELECT value FROM Vertices WHERE graph_id = ?"
        connection.prepareStatement(sqlGetVertices).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            assertFalse(result.next())
        }

        val sqlGetEdges = "SELECT edge_id FROM Edges WHERE graph_id = ?"
        connection.prepareStatement(sqlGetEdges).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            assertFalse(result.next())
        }
    }

    @Test
    fun `list graph names`() {
        val graph = DirectedGraph().apply { addEdge(1, 2, 10) }
        val mainScreen = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        repository.addGraph(
            mainScreen.graphViewModel,
            "first",
            true
        )
        repository.addGraph(
            mainScreen.graphViewModel,
            "second",
            true
        )
        repository.addGraph(
            mainScreen.graphViewModel,
            "third",
            true
        )
        val names = repository.getGraphsNames()
        assertTrue(names.contains("first"))
        assertTrue(names.contains("second"))
        assertTrue(names.contains("third"))
    }

    @Test
    fun `upsert graph`() {
        val firstGraph = DirectedGraph().apply { addEdge(1, 2, 10) }
        val firstMainScreen =
            MainScreenViewModelForDirectedGraph(firstGraph, representationStrategy)
        repository.addGraph(
            firstMainScreen.graphViewModel,
            "sampleGraph",
            true
        )

        val secondGraph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        val secondMainScreen =
            MainScreenViewModelForUndirectedGraph(secondGraph, representationStrategy)
        repository.upsertGraph(
            secondMainScreen.graphViewModel,
            "sampleGraph",
            false
        )

        val loaded = repository.loadGraph("sampleGraph")
        assertGraphsAreEqual(secondMainScreen, loaded)
    }

    @Test
    fun `upsert non-existent graph`() {
        val graph = DirectedGraph().apply { addEdge(1, 2, 10) }
        val mainScreen =
            MainScreenViewModelForDirectedGraph(graph, representationStrategy)
        repository.upsertGraph(
            mainScreen.graphViewModel,
            "sampleGraph",
            true
        )

        val loaded = repository.loadGraph("sampleGraph")
        assertGraphsAreEqual(loaded, mainScreen)
    }
}
