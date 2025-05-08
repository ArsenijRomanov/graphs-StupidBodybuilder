package saving

import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import java.io.Closeable
import java.sql.Connection
import kotlin.use

class GraphRepository(private val connection: Connection) : Closeable {
    init {
        connection.createStatement().use { stmt ->
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS Graphs (
                graphID INTEGER PRIMARY KEY AUTOINCREMENT,
                Name TEXT UNIQUE,
                isDirected BOOLEAN
                );
                """.trimIndent(),
            )

            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS Vertices (
                elementID INTEGER PRIMARY KEY AUTOINCREMENT,
                graphID INTEGER,
                value INTEGER,
                UNIQUE (graphID, value),
                FOREIGN KEY (graphID) REFERENCES Graphs (graphID)
                ON DELETE CASCADE ON UPDATE CASCADE
                );
                """.trimIndent(),
            )

            stmt.execute(
                """
                    CREATE TABLE IF NOT EXISTS Edges (
                    edgeID INTEGER PRIMARY KEY AUTOINCREMENT,
                    graphID INTEGER,
                    firstVertex INTEGER,
                    secondVertex INTEGER,
                    weight INTEGER,
                    FOREIGN KEY (graphID) REFERENCES Graphs (graphID)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (firstVertex) REFERENCES Vertices (elementID)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (secondVertex) REFERENCES Vertices (elementID)
                    ON DELETE CASCADE ON UPDATE CASCADE
                );
                """.trimIndent(),
            )

            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_vertices_graphID ON Vertices(graphID);",
            )

            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_edges_graphID ON Edges(graphID);",
            )
        }
    }

    fun addGraph(
        graph: Graph,
        name: String,
    ) {
        if (graphExists(name))
            throw IllegalStateException("graph with name $name already exists")


        val sqlGraphs = "INSERT INTO Graphs (Name, isDirected) VALUES (?, ?)"
        connection.prepareStatement(sqlGraphs).use { pstmt ->
            pstmt.setString(1, name)
            pstmt.setBoolean(2, graph is DirectedGraph)
            pstmt.executeUpdate()
        }

        val graphID = getGraphID(name) ?: return
        addVertices(graph, graphID)
        addEdges(graph, graphID)
    }

    fun loadDirectedGraph(name: String): DirectedGraph {
        val graph = loadGraph(name)
        return graph as? DirectedGraph
            ?: throw IllegalStateException("Graph $name is not a DirectedGraph")
    }

    fun loadUndirectedGraph(name: String): UndirectedGraph {
        val graph = loadGraph(name)
        return graph as? UndirectedGraph
            ?: throw IllegalStateException("Graph $name is not a UndirectedGraph")
    }

    fun deleteGraph(name: String) {
        val sql = "DELETE FROM Graphs WHERE Name = ?"
        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            pstmt.executeUpdate()
        }
    }

    fun upsertGraph(
        graph: Graph,
        name: String,
    ) {
        if (graphExists(name)) {
            deleteGraph(name)
        }
        addGraph(graph, name)
    }

    fun listGraphNames(): List<String> {
        val names = mutableListOf<String>()
        val sql = "SELECT Name FROM Graphs"
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                names.add(rs.getString("Name"))
            }
        }
        return names
    }

    fun graphExists(name: String): Boolean {
        val sql = "SELECT 1 FROM Graphs WHERE Name = ? LIMIT 1"
        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            val resultSet = pstmt.executeQuery()
            return resultSet.next()
        }
    }

    private fun getGraphID(name: String): Int? {
        val sqlGetGraphID = "SELECT graphID FROM Graphs WHERE Name = ?"
        connection.prepareStatement(sqlGetGraphID).use { pstmt ->
            pstmt.setString(1, name)
            val result = pstmt.executeQuery()
            if (result.next()) {
                return result.getInt("graphID")
            }
        }
        return null
    }

    private fun addVertices(
        graph: Graph,
        graphID: Int,
    ) {
        val sqlInsertVertex =
            """
            INSERT INTO Vertices (graphId, value) VALUES (?, ?)
            """.trimIndent()
        connection.prepareStatement(sqlInsertVertex).use { pstmt ->
            for (vertex in graph.vertices) {
                pstmt.setInt(1, graphID)
                pstmt.setLong(2, vertex)
                pstmt.executeUpdate()
            }
        }
    }

    private fun addEdges(
        graph: Graph,
        graphID: Int,
    ) {
        val vertexToID = getVertices(graphID)

        val sqlInsertEdge =
            """
            INSERT INTO Edges (graphId, firstVertex, secondVertex, weight) VALUES (?, ?, ?, ?)
            """.trimIndent()
        connection.prepareStatement(sqlInsertEdge).use { pstmt ->
            for (edge in graph.edges) {
                pstmt.setInt(1, graphID)
                pstmt.setLong(
                    2,
                    vertexToID[edge.vertices.first]
                        ?: throw NoSuchElementException("Vertex ${edge.vertices.first} not found in DB"),
                )
                pstmt.setLong(
                    3,
                    vertexToID[edge.vertices.second]
                        ?: throw NoSuchElementException("Vertex ${edge.vertices.first} not found in DB"),
                )
                pstmt.setLong(4, edge.weight)
                pstmt.executeUpdate()
            }
        }
    }

    private fun getVertices(graphID: Int): Map<Long, Long> {
        val vertexToID = mutableMapOf<Long, Long>()

        val sqlGetVertexToID = "SELECT elementID, value FROM Vertices WHERE graphID = ?"
        connection.prepareStatement(sqlGetVertexToID).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            while (result.next()) {
                val value = result.getLong("value")
                val elementID = result.getLong("elementID")
                vertexToID[value] = elementID
            }
        }
        return vertexToID
    }

    private fun getEdges(
        graph: Graph,
        graphID: Int,
        vertexToID: Map<Long, Long>,
    ) {
        val idToVertex = mutableMapOf<Long, Long>()
        vertexToID.forEach { v, id -> idToVertex[id] = v }

        val sqlGetEdges = "SELECT firstVertex, secondVertex, weight FROM Edges WHERE graphID = ?"
        connection.prepareStatement(sqlGetEdges).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            while (result.next()) {
                val firstVertexID = result.getLong("firstVertex")
                val secondVertexID = result.getLong("secondVertex")
                val firstVertex =
                    idToVertex[firstVertexID]
                        ?: throw NoSuchElementException("Vertex with ID $firstVertexID not found in DB")
                val secondVertex =
                    idToVertex[secondVertexID]
                        ?: throw NoSuchElementException("Vertex with ID $secondVertexID not found in DB")
                val weight = result.getLong("weight")
                graph.addEdge(firstVertex, secondVertex, weight)
            }
        }
    }

    private fun loadGraph(name: String): Graph {
        if (!graphExists(name)) {
            throw NoSuchElementException("Graph with id '$name' not found")
        }

        val graphID = getGraphID(name) ?: throw IllegalStateException()

        val graph = if (queryIsDirected(name)) DirectedGraph() else UndirectedGraph()

        val vertexToID = getVertices(graphID)
        vertexToID.forEach { v, _ -> graph.addVertex(v) }

        getEdges(graph, graphID, vertexToID)

        return graph
    }

    private fun queryIsDirected(name: String): Boolean {
        if (!graphExists(name)) {
            throw NoSuchElementException("Graph with name '$name' not found")
        }

        var isDirected = true
        val sqlGetGraphType = "SELECT isDirected FROM Graphs WHERE Name = ?"

        connection.prepareStatement(sqlGetGraphType).use { pstmt ->
            pstmt.setString(1, name)
            val result = pstmt.executeQuery()
            if (result.next()) {
                isDirected = result.getBoolean("isDirected")
            }
        }

        return isDirected
    }

    override fun close() {
        connection.close()
    }
}
