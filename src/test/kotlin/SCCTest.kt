import algos.SCC
import model.DirectedGraph
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SCCTest {
    fun assertSCCListContainsAllVertices(graph: DirectedGraph, scc: List<List<Long>>){
        val vertices: MutableSet<Long> = graph.vertices.toMutableSet()
        for (component in scc){
            for (vertex in component){
                assert(vertices.contains(vertex))
                vertices.remove(vertex)
            }
        }
        assertTrue(vertices.isEmpty())
    }

    fun assertCheckForCorrectComponents(expected: List<List<Long>>, actual: List<List<Long>>){
        val componentsCount = expected.size
        assertTrue(actual.size == componentsCount)

        val vertexToComponent = mutableMapOf<Long, Long>()

        for (i in 0..<componentsCount){
            for (vertex in expected[i])
                vertexToComponent[vertex] = i.toLong()
        }

        for (i in 0..<componentsCount){
            assertTrue(actual[i].isNotEmpty())
            val componentNumber = vertexToComponent[actual[i][0]]
            assertNotNull(componentNumber)

            for (vertex in actual[i])
                assertTrue(vertexToComponent[vertex] == componentNumber)
        }
    }

    @Test
    fun `empty graph returns no components`() {
        val graph = DirectedGraph()
        val scc = SCC(graph)
        assertTrue(scc.isEmpty())
    }

    @Test
    fun `single vertex is one SCC`() {
        val graph = DirectedGraph().apply { addVertex(1) }
        val scc = SCC(graph)
        assertEquals(1, scc.size)
        assertEquals(listOf(1L), scc[0])
    }

    @Test
    fun `disconnected vertices form separate SCCs`() {
        val graph = DirectedGraph().apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L), listOf(2L), listOf(3L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `two vertices in cycle`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 1, 1)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L, 2L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `three vertices in chain`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 3, 1)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L), listOf(2L), listOf(3L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `three vertices in cycle`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 3, 1)
            addEdge(3, 1, 1)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L, 2L, 3L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `graph with cycle and isolated node`() {
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 3, 1)
            addEdge(3, 1, 1)
            addVertex(4)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L, 2L, 3L), listOf(4L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `two separate cycles`() {
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 1, 1)
            addEdge(3, 4, 1)
            addEdge(4, 3, 1)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L, 2L), listOf(3L, 4L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `two connected cycles in two scc`() {
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 1)
            addEdge(2, 3, 1)
            addEdge(3, 1, 1)

            addEdge(4, 5, 1)
            addEdge(5, 6, 1)
            addEdge(6, 4, 1)

            addEdge(1, 4, 1)
        }
        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = listOf(listOf(1L, 2L, 3L), listOf(4L, 5L, 6L))
        assertCheckForCorrectComponents(expected, scc)
    }

    @Test
    fun `long chain with N SCCs`() {
        val graph = DirectedGraph()
        for (i in 1L..999L)
            graph.addEdge(i, i + 1, 1)

        val scc = SCC(graph)
        assertSCCListContainsAllVertices(graph, scc)

        val expected = mutableListOf<List<Long>>()
        for (i in 1L..1000L)
            expected.add(listOf(i))

        assertCheckForCorrectComponents(expected, scc)
    }
}
