import model.UndirectedGraph
import algos.dijkstra
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DijkstraTests {
    @Test
    fun `negative weights`() {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 1, 3))
    }

    @Test
    fun `begin is missing`() {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 4, 1))
    }

    @Test
    fun `end is missing`() {
        val graph =
            UndirectedGraph().apply {
                this.addEdge(1, 2, 1)
                this.addEdge(2, 3, 2)
                this.addEdge(1, 3, -10)
            }
        assertNull(dijkstra(graph, 1, 4))
    }

    @Test
    fun `the beginning is equal to the end`() {
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

    @Test
    fun `prefer shorter alternative path`() {
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
    fun `disconnected graph returns empty path`() {
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(3, 4, 1)
        }
        val path = dijkstra(graph, 1, 4)
        assertEquals(emptyList<Long>(), path)
    }

    @Test
    fun `path through multiple intermediate vertices`() {
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 3, 1)
            addEdge(3, 4, 1)
            addEdge(4, 5, 1)
        }
        val path = dijkstra(graph, 1, 5)
        assertEquals(listOf(1L, 2L, 3L, 4L, 5L), path)
    }

    @Test
    fun `large graph`(){
        val graph = UndirectedGraph()
        val expected = mutableListOf<Long>()
        for (i in 0..1000) {
            graph.addEdge(i.toLong(), (i + 1).toLong(), 1)
            expected.add(i.toLong())
        }
        val path = dijkstra(graph, 0, 1000)
        assertEquals(expected, path)
    }
}
