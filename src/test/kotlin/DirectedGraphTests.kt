import model.DirectedGraph
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DirectedGraphTests {
    private lateinit var graph: DirectedGraph

    @BeforeEach
    fun setUp() {
        graph = DirectedGraph()
    }

    @Test
    fun `add vertex test`() {
        graph.addVertex(1)
        assertEquals(graph.vertices.size, 1)
        assert(graph.findVertex(1) != null)
    }

    @Test
    fun `add an existing vertex`() {
        graph.addVertex(1)
        graph.addVertex(1)
        assertEquals(graph.vertices.size, 1)
    }

    @Test
    fun `find vertex`() {
        graph.addVertex(1)
        val vertexElement = graph.findVertex(1)?.value
        assertEquals(vertexElement, 1)
    }

    @Test
    fun `find not existing vertex`() {
        assertEquals(graph.findVertex(1), null)
    }

    @Test
    fun `add edge with two existing vertex`() {
        graph.addVertex(1)
        graph.addVertex(2)
        graph.addEdge(1, 2, 10)
        assertEquals(graph.edges.size, 1)
        assertEquals(graph.findEdge(1, 2)?.weight, 10)
    }

    @Test
    fun `add edge without vertices`() {
        graph.addEdge(1, 2, 10)
        assertEquals(graph.edges.size, 1)
        assertEquals(graph.findEdge(1, 2)?.weight, 10)
    }

    @Test
    fun `add existing edge`() {
        graph.addEdge(1, 2, 10)
        graph.addEdge(1, 2, 5)
        assertEquals(graph.edges.size, 1)
        assertEquals(graph.findEdge(1, 2)?.weight, 10)
    }

    @Test
    fun `add reverse edge`() {
        graph.addEdge(1, 2, 10)
        graph.addEdge(2, 1, 5)
        assertEquals(graph.edges.size, 2)
        assertEquals(graph.findEdge(1, 2)?.weight, 10)
        assertEquals(graph.findEdge(2, 1)?.weight, 5)
    }

    @Test
    fun `add loop`() {
        graph.addEdge(1, 1, 10)
        assertEquals(graph.edges.size, 1)
        assertEquals(graph.findEdge(1, 1)?.weight, 10)
    }

    @Test
    fun `find edge`() {
        graph.addEdge(1, 2, 10)
        assertEquals(graph.findEdge(1, 2)?.weight, 10)
    }

    @Test
    fun `find not existing edge`() {
        assertEquals(graph.findEdge(1, 2), null)
    }

    @Test
    fun `find reverse edge`() {
        graph.addEdge(1, 2, 10)
        assertEquals(graph.findEdge(2, 1), null)
    }
}
