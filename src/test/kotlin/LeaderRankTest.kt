import model.UndirectedGraph
import algos.leaderRank
import model.DirectedGraph
import model.Graph
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LeaderRankTest {
    fun assertTotalRank(ranks: Map<Long, Double>){
        var sum = 0.0
        var verticesCount = 0
        for ((_, rank) in ranks) {
            sum += rank
            ++verticesCount
        }
        assertTrue(abs(sum - verticesCount) < 0.01)
    }

    fun assertCorrectCountOfVertices(graph: Graph, ranks: Map<Long, Double>){
        val vertices = graph.vertices
        assertEquals(ranks.size, vertices.size)

        for (i in vertices)
            assertNotNull(ranks[i])
    }

    fun maxRankVertex(ranks: Map<Long, Double>): Pair<Long, Double>? {
        return ranks.maxByOrNull { it.value }?.toPair()
    }

    fun minRankVertex(ranks: Map<Long, Double>): Pair<Long, Double>? {
        return ranks.minByOrNull { it.value }?.toPair()
    }

    @Test
    fun `empty graph`(){
        val graph = DirectedGraph()
        val ranks = leaderRank(graph)
        assertTrue(ranks.isEmpty())
    }

    @Test
    fun `graph with one vertex`(){
        val graph = DirectedGraph().apply { addVertex(1) }
        val ranks = leaderRank(graph)
        assertEquals(ranks.size, 1)
        assertTrue(abs((ranks[1] ?: 2.0) - 1.0) < 0.0001)
    }

    @Test
    fun `graph with two isolated vertex`(){
        val graph = DirectedGraph().apply {
            addVertex(1)
            addVertex(2)
        }
        val ranks = leaderRank(graph)
        assertEquals(ranks.size, 2)
        assertTrue(abs((ranks[1] ?: 2.0) - 1.0) < 0.0001)
        assertTrue(abs((ranks[2] ?: 2.0) - 1.0) < 0.0001)
    }

    @Test
    fun `graph without edges`(){
        val graph = DirectedGraph().apply {
            addVertex(1)
            addVertex(2)
            addVertex(3)
            addVertex(4)
            addVertex(5)
        }
        val ranks = leaderRank(graph)
        assertEquals(ranks.size, 5)
        for (i in 1L..5L)
            assertTrue((abs(ranks[i] ?: 2.0) - 1.0) < 0.0001)
    }

    @Test
    fun `two connected vertices in directed graph`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)

        assertTrue((ranks[2] ?: 0.0) > (ranks[1] ?: 0.0))
    }

    @Test
    fun `two connected vertices in undirected graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)

        assertTrue(abs((ranks[2] ?: 0.0) - (ranks[1] ?: 0.0)) < 0.0001)
    }

    @Test
    fun `ring in directed graph`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 5, 0)
            addEdge(5, 1, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)

        for (i in 1L..5L)
            assertTrue(abs((ranks[i] ?: 1.0) - 1.0) < 0.0001)
    }

    @Test
    fun `ring in undirected graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 5, 0)
            addEdge(5, 1, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)

        for (i in 1L..5L)
            assertTrue(abs((ranks[i] ?: 1.0) - 1.0) < 0.0001)
    }

    @Test
    fun `chain in directed graph`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 5, 0)
            addEdge(5, 6, 0)
            addEdge(6, 7, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)
        assertEquals(maxRankVertex(ranks)?.first, 7)
    }

    @Test
    fun `star graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(1, 3, 0)
            addEdge(1, 4, 0)
            addEdge(1, 5, 0)
            addEdge(1, 6, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)
        assertEquals(maxRankVertex(ranks)?.first, 1)
    }

    @Test
    fun `isolated vertex`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 5, 0)
            addVertex(6)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)
        assertEquals(minRankVertex(ranks)?.first, 6)
    }

    @Test
    fun `two strongly connected components`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(2, 3, 0)
            addEdge(3, 4, 0)
            addEdge(4, 1, 0)

            addEdge(5, 6, 0)
            addEdge(6, 7, 0)
            addEdge(7, 8, 0)
            addEdge(8, 5, 0)

            addEdge(8, 3, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)
        assertEquals(maxRankVertex(ranks)?.first, 3)

        val firstComponentRank = (ranks[1] ?: 5.0) + (ranks[2] ?: 5.0)
        + (ranks[3] ?: 5.0) + (ranks[4] ?: 5.0)

        val secondComponentRank = (ranks[5] ?: 5.0) + (ranks[6] ?: 5.0)
        + (ranks[7] ?: 5.0) + (ranks[8] ?: 5.0)

        assertTrue(firstComponentRank > secondComponentRank)
    }

    @Test
    fun `strongly connected graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 0)
            addEdge(1, 3, 0)
            addEdge(1, 4, 0)
            addEdge(1, 5, 0)
            addEdge(2, 3, 0)
            addEdge(2, 4, 0)
            addEdge(2, 5, 0)
            addEdge(3, 4, 0)
            addEdge(3, 5, 0)
            addEdge(4, 5, 0)
            addEdge(5, 4, 0)
        }
        val ranks = leaderRank(graph)
        assertCorrectCountOfVertices(graph, ranks)
        assertTotalRank(ranks)

        val middleRank = ranks[1] ?: 0.0
        for ((_, rank) in ranks)
            assertTrue(abs(middleRank - rank) < 0.0001)
    }
}
