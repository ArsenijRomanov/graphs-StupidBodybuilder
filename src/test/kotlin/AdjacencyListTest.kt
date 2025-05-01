import model.DirectedGraph
import model.UndirectedGraph
import model.getAdjacencyList
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AdjacencyListTest {
    @Test
    fun `empty graph`() {
        val graph = DirectedGraph()
        assert(getAdjacencyList(graph).isEmpty())
    }

    @Test
    fun `one vertex`() {
        val graph = UndirectedGraph()
        graph.addVertex(1)
        val adjacencyList = getAdjacencyList(graph)
        assertEquals(adjacencyList.size, 1)
        assertEquals(adjacencyList[1]?.size, 0)
    }

    @Test
    fun `adjacency list for undirected graph`() {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 0)
                this.addEdge(2, 3, 0)
                this.addEdge(1, 3, 0)
                this.addEdge(1, 4, 0)
                this.addEdge(3, 3, 0)
                this.addVertex(5)
            }
        val adjacencyList = getAdjacencyList(graph)

        assertEquals(listOf<Long>(2, 3, 4), adjacencyList[1])

        assertEquals(listOf<Long>(1, 3), adjacencyList[2])

        assertEquals(listOf<Long>(3, 2, 1), adjacencyList[3])

        assertEquals(listOf<Long>(1), adjacencyList[4])

        assertEquals(emptyList(), adjacencyList[5])
    }

    @Test
    fun `adjacency list for directed graph`() {
        val graph =
            DirectedGraph().apply {
                this.addEdge(1, 2, 0)
                this.addEdge(2, 3, 0)
                this.addEdge(3, 2, 0)
                this.addEdge(3, 4, 0)
                this.addEdge(4, 1, 0)
                this.addEdge(1, 4, 0)
                this.addEdge(1, 1, 0)
            }
        val adjacencyList = getAdjacencyList(graph)

        assertEquals(listOf<Long>(1, 2, 4), adjacencyList[1])
        assertEquals(listOf<Long>(3), adjacencyList[2])
        assertEquals(listOf<Long>(4, 2), adjacencyList[3])
        assertEquals(listOf<Long>(1), adjacencyList[4])
    }

    @Test
    fun `graph without edges`() {
        val graph =
            UndirectedGraph().apply {
                this.addVertex(1)
                this.addVertex(2)
                this.addVertex(3)
                this.addVertex(4)
                this.addVertex(5)
            }
        val adjacencyList = getAdjacencyList(graph)

        assertEquals(emptyList(), adjacencyList[1])
        assertEquals(emptyList(), adjacencyList[1])
        assertEquals(emptyList(), adjacencyList[1])
        assertEquals(emptyList(), adjacencyList[1])
        assertEquals(emptyList(), adjacencyList[1])
    }
}
