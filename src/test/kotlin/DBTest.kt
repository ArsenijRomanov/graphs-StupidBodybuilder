import io.kotest.assertions.throwables.shouldThrow
import model.DirectedGraph
import model.Edge
import model.Graph
import model.UndirectedGraph
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import saving.GraphRepository
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DBTest {
    private lateinit var connection: Connection
    private lateinit var repository: GraphRepository

    @BeforeEach
    fun setUp() {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:")
        repository = GraphRepository(connection)
    }

    @AfterEach
    fun tearDown() {
        repository.close()
    }

    fun verticesAreEqual(firstVertices: Collection<Long>, secondVertices: Collection<Long>): Boolean{
        return firstVertices.groupingBy { it }.eachCount() ==
                secondVertices.groupingBy { it }.eachCount()
    }

    fun directedEdgesAreEqual(firstEdges: Collection<Edge>, secondEdges: Collection<Edge>): Boolean{
        val firstEdgesMap = firstEdges.associate { e ->
            (e.vertices.first to e.vertices.second) to e.weight
        }

        val secondEdgesMap = secondEdges.associate { e ->
            (e.vertices.first to e.vertices.second) to e.weight
        }

        return firstEdgesMap == secondEdgesMap
    }

    fun undirectedEdgesAreEqual(firstEdges: Collection<Edge>, secondEdges: Collection<Edge>): Boolean{
        val firstEdgesMap = mutableMapOf<Pair<Long, Long>, Long>()

        for (e in firstEdges){
            val firstVertex = e.vertices.first
            val secondVertex = e.vertices.second
            val weight = e.weight
            if (firstVertex <= secondVertex)
                firstEdgesMap[firstVertex to secondVertex] = weight
            else
                firstEdgesMap[secondVertex to firstVertex] = weight
        }

        val secondEdgesMap = mutableMapOf<Pair<Long, Long>, Long>()

        for (e in secondEdges){
            val firstVertex = e.vertices.first
            val secondVertex = e.vertices.second
            val weight = e.weight
            if (firstVertex <= secondVertex)
                secondEdgesMap[firstVertex to secondVertex] = weight
            else
                secondEdgesMap[secondVertex to firstVertex] = weight
        }

        return firstEdgesMap == secondEdgesMap
    }

    fun assertGraphsAreEqual(firstGraph: Graph, secondGraph: Graph){
        assertTrue(firstGraph is DirectedGraph == secondGraph is DirectedGraph)
        assertTrue(verticesAreEqual(firstGraph.vertices, secondGraph.vertices))
        if (firstGraph is DirectedGraph)
            assertTrue(directedEdgesAreEqual(firstGraph.edges, secondGraph.edges))
        else if (firstGraph is UndirectedGraph && secondGraph is UndirectedGraph)
            assertTrue(undirectedEdgesAreEqual(firstGraph.edges, secondGraph.edges))
    }

    @Test
    fun `save directed graph`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")
        assertTrue(repository.graphExists("sampleGraph"))
    }

    @Test
    fun `save undirected graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")
        assertTrue(repository.graphExists("sampleGraph"))
    }

    @Test
    fun `should throw exception if graph exists`() {
        val firstGraph = DirectedGraph().apply { addEdge(1, 2, 10) }
        val secondGraph = UndirectedGraph().apply { addEdge(2, 2, 10) }
        repository.addGraph(firstGraph, "sampleGraph")
        shouldThrow<IllegalStateException> { repository.addGraph(secondGraph, "sampleGraph") }
    }

    @Test
    fun `load directed graph`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")

        val loaded = repository.loadDirectedGraph("sampleGraph")
        assertGraphsAreEqual(graph, loaded)
    }

    @Test
    fun `load undirected graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")

        val loaded = repository.loadUndirectedGraph("sampleGraph")
        assertGraphsAreEqual(graph, loaded)
    }

    @Test
    fun `load directed graph as undirected`(){
        val graph = DirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")
        shouldThrow<IllegalStateException> { repository.loadUndirectedGraph("sampleGraph") }
    }

    @Test
    fun `load undirected graph as directed`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")
        shouldThrow<IllegalStateException> { repository.loadDirectedGraph("sampleGraph") }
    }

    @Test
    fun `delete graph`(){
        val graph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.addGraph(graph, "sampleGraph")
        repository.deleteGraph("sampleGraph")

        var graphID = 0

        val sqlGetGraphID = "SELECT graphID FROM Graphs WHERE Name = ?"
        connection.prepareStatement(sqlGetGraphID).use { pstmt ->
            pstmt.setString(1, "sampleGraph")
            val result = pstmt.executeQuery()
            if (result.next()) {
                graphID = result.getInt("graphID")
            }
        }

        val sqlGetVertices = "SELECT value FROM Vertices WHERE graphID = ?"
        connection.prepareStatement(sqlGetVertices).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            assertFalse(result.next())
        }

        val sqlGetEdges = "SELECT edgeID FROM Edges WHERE graphID = ?"
        connection.prepareStatement(sqlGetVertices).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            assertFalse(result.next())
        }
    }

    @Test
    fun `list graph names`(){
        val graph = DirectedGraph().apply { addEdge(1, 2, 10) }
        repository.addGraph(graph, "first")
        repository.addGraph(graph, "second")
        repository.addGraph(graph, "third")
        val names = repository.listGraphNames()
        assertTrue(names.contains("first"))
        assertTrue(names.contains("second"))
        assertTrue(names.contains("third"))
    }

    @Test
    fun `upsert graph`(){
        val firstGraph = DirectedGraph().apply { addEdge(1, 2, 10) }
        repository.addGraph(firstGraph, "sampleGraphs")

        val secondGraph = UndirectedGraph().apply {
            addEdge(1, 2, 10)
            addEdge(2, 3, 20)
            addEdge(3, 4, 30)
            addEdge(4, 1, 40)
        }
        repository.upsertGraph(secondGraph, "sampleGraphs")

        val loaded = repository.loadUndirectedGraph("sampleGraphs")
        assertGraphsAreEqual(loaded, secondGraph)
    }

    @Test
    fun `upsert non-existent graph`(){
        val graph = DirectedGraph().apply { addEdge(1, 2, 10) }
        repository.upsertGraph(graph, "sampleGraphs")

        val loaded = repository.loadDirectedGraph("sampleGraphs")
        assertGraphsAreEqual(loaded, graph)
    }
}
