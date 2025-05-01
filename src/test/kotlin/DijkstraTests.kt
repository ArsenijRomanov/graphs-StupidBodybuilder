import model.UndirectedGraph
import model.dijkstra
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DijkstraTests {
    @Test
    fun `find path in simple graph`()  {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, 10)
            }

        val path = dijkstra(graph, 1, 3)
        assertEquals(path?.elementAtOrNull(0), 1)
        assertEquals(path?.elementAtOrNull(1), 2)
        assertEquals(path?.elementAtOrNull(2), 3)
    }

    @Test
    fun `negative weights`()  {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 1, 3))
    }

    @Test
    fun `begin is missing`()  {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 4, 1))
    }

    @Test
    fun `end is missing`()  {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 1, 4))
    }

    @Test
    fun `the beginning is equal to the end`()  {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, 10)
            }
        val path = dijkstra(graph, 1, 1)
        assertEquals(path?.elementAtOrNull(0), 1)
        assertEquals(path?.size, 1)
    }
}
