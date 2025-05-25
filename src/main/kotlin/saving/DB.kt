package saving

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import viewmodel.EdgeViewModel
import viewmodel.ForceAtlas2Layout
import viewmodel.GraphViewModel
import viewmodel.MainScreenViewModel
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.MainScreenViewModelForUndirectedGraph
import viewmodel.VertexViewModel
import java.io.Closeable
import java.sql.Connection
import java.sql.SQLException
import kotlin.collections.forEach
import kotlin.use

class GraphRepository(private val connection: Connection) : Closeable {
    init {
        connection.createStatement().use { stmt ->
            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS Graphs (
                graph_id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT UNIQUE,
                is_directed BOOLEAN
                );
                """.trimIndent(),
            )

            stmt.execute(
                """
                CREATE TABLE IF NOT EXISTS Vertices (
                vertex_id INTEGER PRIMARY KEY AUTOINCREMENT,
                graph_id INTEGER,
                value INTEGER,
                x REAL,
                y REAL,
                color INTEGER,
                radius REAL,
                UNIQUE (graph_id, value),
                FOREIGN KEY (graph_id) REFERENCES Graphs (graph_id)
                ON DELETE CASCADE ON UPDATE CASCADE
                );
                """.trimIndent(),
            )

            stmt.execute(
                """
                    CREATE TABLE IF NOT EXISTS Edges (
                    edge_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    graph_id INTEGER,
                    first_vertex INTEGER,
                    second_vertex INTEGER,
                    weight INTEGER,
                    color INTEGER,
                    width REAL,
                    FOREIGN KEY (graph_id) REFERENCES Graphs (graph_id)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (first_vertex) REFERENCES Vertices (vertex_id)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (second_vertex) REFERENCES Vertices (vertex_id)
                    ON DELETE CASCADE ON UPDATE CASCADE
                );
                """.trimIndent(),
            )

            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_vertices_graph_id ON Vertices(graph_id);",
            )

            stmt.execute(
                "CREATE INDEX IF NOT EXISTS idx_edges_graph_id ON Edges(graph_id);",
            )
        }
    }

    fun addGraph(
        graphViewModel: GraphViewModel,
        name: String,
        isDirected: Boolean
    ) {
        if (graphExists(name)) {
            throw IllegalStateException("graph with name $name already exists")
        }

        connection.autoCommit = false
        try {
            val sqlGraphs = "INSERT INTO Graphs (name, is_directed) VALUES (?, ?)"
            connection.prepareStatement(sqlGraphs).use { pstmt ->
                pstmt.setString(1, name)
                pstmt.setBoolean(2, isDirected)
                pstmt.executeUpdate()
            }

            val graphID = getGraphID(name)
                ?: throw IllegalStateException("Graph ID should not be null after insert")

            val sqlInsertVertex = """
            INSERT INTO Vertices (graph_id, value, x, y, color, radius)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
            connection.prepareStatement(sqlInsertVertex).use { pstmt ->
                for (vertex in graphViewModel.vertices) {
                    pstmt.setInt(1, graphID)
                    pstmt.setLong(2, vertex.value)
                    pstmt.setFloat(3, vertex.x.value)
                    pstmt.setFloat(4, vertex.y.value)
                    pstmt.setInt(5, vertex.color.toArgb())
                    pstmt.setFloat(6, vertex.radius.value)
                    pstmt.addBatch()
                }
                pstmt.executeBatch()
            }

            val vertexToID = getVerticesToID(graphID)

            val sqlInsertEdge = """
            INSERT INTO Edges (graph_id, first_vertex, second_vertex, weight, color, width)
            VALUES (?, ?, ?, ?, ?, ?)
        """.trimIndent()
            connection.prepareStatement(sqlInsertEdge).use { pstmt ->
                for (edge in graphViewModel.edges) {
                    pstmt.setInt(1, graphID)
                    pstmt.setLong(
                        2,
                        vertexToID[edge.firstVertex.value]
                            ?: throw NoSuchElementException("Vertex not found in DB")
                    )
                    pstmt.setLong(
                        3,
                        vertexToID[edge.secondVertex.value]
                            ?: throw NoSuchElementException("Vertex not found in DB")
                    )
                    pstmt.setLong(4, edge.weight.toLong())
                    pstmt.setInt(5, edge.color.toArgb())
                    pstmt.setFloat(6, edge.width)
                    pstmt.addBatch()
                }
                pstmt.executeBatch()
            }
            connection.commit()
        } catch (e: SQLException) {
            connection.rollback()
            throw e
        } finally {
            connection.autoCommit = true
        }
    }

    fun loadGraph(name: String): MainScreenViewModel {
        if (!graphExists(name))
            throw NoSuchElementException("Graph with name '$name' not found")
        val graphID = getGraphID(name) ?: throw IllegalStateException()

        val isDir = queryIsDirected(name)
        val graph = if(isDir) DirectedGraph() else UndirectedGraph()

        val valueToVertex = getVertices(graphID, graph)
        val verticesToEdge = getEdges(graphID, graph)
        val viewModel = if(graph is DirectedGraph)
            MainScreenViewModelForDirectedGraph(
                graph, ForceAtlas2Layout()
            )
        else MainScreenViewModelForUndirectedGraph(
            graph as UndirectedGraph, ForceAtlas2Layout()
        )

        viewModel.graphViewModel.vertices.forEach { vertex ->
            val parameters = valueToVertex[vertex.value] ?: throw NoSuchElementException()
            vertex.radius = parameters.radius
            vertex.color = parameters.color
            vertex.x = parameters.x
            vertex.y = parameters.y
        }

        viewModel.graphViewModel.edges.forEach { edge ->
            val firstVertex = edge.firstVertex.value
            val secondVertex = edge.secondVertex.value
            val parameters =
                verticesToEdge[firstVertex to secondVertex] ?: throw NoSuchElementException()
            edge.color = parameters.color
            edge.width = parameters.width
        }

        return viewModel
    }

    fun deleteGraph(name: String) {
        val sql = "DELETE FROM Graphs WHERE name = ?"
        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            pstmt.executeUpdate()
        }
    }

    fun upsertGraph(
        graph: GraphViewModel,
        name: String,
        isDirected: Boolean
    ) {
        if (graphExists(name)) {
            deleteGraph(name)
        }
        addGraph(graph, name, isDirected)
    }

    fun getGraphsNames(): List<String> {
        val names = mutableListOf<String>()
        val sql = "SELECT name FROM Graphs"
        connection.createStatement().use { stmt ->
            val rs = stmt.executeQuery(sql)
            while (rs.next()) {
                names.add(rs.getString("name"))
            }
        }
        return names
    }

    fun graphExists(name: String): Boolean {
        val sql = "SELECT 1 FROM Graphs WHERE name = ? LIMIT 1"
        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            val resultSet = pstmt.executeQuery()
            return resultSet.next()
        }
    }

    fun queryIsDirected(name: String): Boolean {
        val sql = "SELECT is_directed FROM Graphs WHERE name = ?"
        connection.prepareStatement(sql).use { pstmt ->
            pstmt.setString(1, name)
            val result = pstmt.executeQuery()
            if (result.next()) {
                return result.getBoolean("is_directed")
            } else
                throw NoSuchElementException()
        }
    }

    private fun getGraphID(name: String): Int? {
        val sqlGetGraphID = "SELECT graph_id FROM Graphs WHERE name = ?"
        connection.prepareStatement(sqlGetGraphID).use { pstmt ->
            pstmt.setString(1, name)
            val result = pstmt.executeQuery()
            if (result.next()) {
                return result.getInt("graph_id")
            }
        }
        return null
    }

    private fun addVertices(
        vertices: Collection<VertexViewModel>,
        graphID: Int,
    ) {
        val sqlInsertVertex =
            """
            INSERT INTO Vertices (graph_id, value, x, y, color, radius) 
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        connection.prepareStatement(sqlInsertVertex).use { pstmt ->
            for (vertex in vertices) {
                pstmt.setInt(1, graphID)
                pstmt.setLong(2, vertex.value)
                pstmt.setFloat(3, vertex.x.value)
                pstmt.setFloat(4, vertex.y.value)
                pstmt.setInt(5, vertex.color.toArgb())
                pstmt.setFloat(6, vertex.radius.value)
                pstmt.executeUpdate()
            }
        }
    }

    private fun addEdges(
        edges: Collection<EdgeViewModel>,
        graphID: Int,
    ) {
        val vertexToID = getVerticesToID(graphID)

        val sqlInsertEdge =
            """
            INSERT INTO Edges (graph_id, first_vertex, second_vertex, weight, color, width)
            VALUES (?, ?, ?, ?, ?, ?)
            """.trimIndent()
        connection.prepareStatement(sqlInsertEdge).use { pstmt ->
            for (edge in edges) {
                pstmt.setInt(1, graphID)
                pstmt.setLong(
                    2,
                    vertexToID[edge.firstVertex.value]
                        ?: throw NoSuchElementException("Vertex not found in DB"),
                )
                pstmt.setLong(
                    3,
                    vertexToID[edge.secondVertex.value]
                        ?: throw NoSuchElementException("Vertex not found in DB"),
                )
                pstmt.setLong(4, edge.weight.toLong())
                pstmt.setInt(5, edge.color.toArgb())
                pstmt.setFloat(6, edge.width)
                pstmt.executeUpdate()
            }
        }
    }

    private fun getVertices(graphID: Int, graph: Graph): Map<Long, VerticesTableElement> {
        val valueToVertex = mutableMapOf<Long, VerticesTableElement>()

        val sqlGetVertexToID = """
            SELECT vertex_id, value, x, y, color, radius FROM Vertices WHERE graph_id = ?
            """.trimIndent()
        connection.prepareStatement(sqlGetVertexToID).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            while (result.next()) {
                result.getLong("vertex_id")
                val value = result.getLong("value")
                val x = result.getFloat("x").dp
                val y = result.getFloat("y").dp
                val color = Color(result.getInt("color"))
                val radius = result.getFloat("radius").dp
                valueToVertex[value] = VerticesTableElement(value, x, y, color, radius)
                graph.addVertex(value)
            }
        }
        return valueToVertex
    }

    private fun getEdges(graphID: Int, graph: Graph): Map<Pair<Long, Long>, EdgesTableElement> {
        val verticesToEdge =  mutableMapOf<Pair<Long, Long>, EdgesTableElement>()

        val sqlGetEdges = """
            SELECT Edges.*, FirstVertices.value AS first_vertex_value, SecondVertices.value AS second_vertex_value
            FROM Edges 
            JOIN Vertices AS FirstVertices
              ON Edges.first_vertex = FirstVertices.vertex_id
            JOIN Vertices AS SecondVertices
              ON Edges.second_vertex = SecondVertices.vertex_id
            WHERE Edges.graph_id = ?
        """.trimIndent()

        connection.prepareStatement(sqlGetEdges).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            while (result.next()) {
                val firstVertex = result.getLong("first_vertex_value")
                val secondVertex = result.getLong("second_vertex_value")
                val weight = result.getLong("weight")
                val color = Color(result.getInt("color"))
                val width = result.getFloat("width")
                verticesToEdge[firstVertex to secondVertex] = EdgesTableElement(
                    firstVertex,
                    secondVertex,
                    weight,
                    color,
                    width
                )
                graph.addEdge(firstVertex, secondVertex, weight)
            }
        }
        return verticesToEdge
    }

    private fun getVerticesToID(graphID: Int): Map<Long, Long> {
        val vertexToID = mutableMapOf<Long, Long>()

        val sqlGetVertexToID = "SELECT vertex_id, value FROM Vertices WHERE graph_id = ?"
        connection.prepareStatement(sqlGetVertexToID).use { pstmt ->
            pstmt.setInt(1, graphID)
            val result = pstmt.executeQuery()
            while (result.next()) {
                val value = result.getLong("value")
                val elementID = result.getLong("vertex_id")
                vertexToID[value] = elementID
            }
        }
        return vertexToID
    }

    override fun close() {
        connection.close()
    }

    private class VerticesTableElement(
        val value: Long,
        val x: Dp,
        val y: Dp,
        val color: Color,
        val radius: Dp
    )

    private class EdgesTableElement(
        val firstVertex: Long,
        val secondVertex: Long,
        val weight: Long,
        val color: Color,
        val width: Float
    )
}

