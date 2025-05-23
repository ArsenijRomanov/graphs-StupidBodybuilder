package saving

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.File
import java.io.FileNotFoundException
import model.Graph
import model.DirectedGraph
import model.UndirectedGraph
import viewmodel.MainScreenViewModel
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.MainScreenViewModelForUndirectedGraph
import viewmodel.RepresentationStrategy
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlinx.serialization.encodeToString

@Serializable
private data class Vertex(
    val value: Long,
    val radius: Int,
    val color: String
)

@Serializable
private data class Edge(
    val from: Long,
    val to: Long,
    val width: Float,
    val color: String,
    val weight: Long
)

@Serializable
private data class GraphJson(
    val type : String,
    val vertices: List<Vertex>,
    val edges: List<Edge>
)

@Serializable
private data class GraphWrapper(
    val graph: GraphJson
)

fun Dp.toPx(): Int = (value * 1.0f).toInt()

private fun parseGraphFromJson(filePath: String): GraphJson {
    val jsonFile = File(filePath)
    if (!jsonFile.exists()) {
        throw FileNotFoundException("Файл '$filePath' не найден.")
    }

    return try {
        val jsonString = jsonFile.readText()
        val graphWrapper = Json.decodeFromString<GraphWrapper>(jsonString)
        graphWrapper.graph
    } catch (e: Exception) {
        throw IllegalArgumentException("Ошибка парсинга JSON: ${e.message}")
    }
}

fun String.hexToColor(): Color {
    val hex = this.removePrefix("#")
    val (a, r, g, b) = when (hex.length) {
        6 -> listOf(255, hex.substring(0, 2), hex.substring(2, 4), hex.substring(4, 6))
        8 -> listOf(hex.substring(0, 2), hex.substring(2, 4), hex.substring(4, 6), hex.substring(6, 8))
        else -> throw IllegalArgumentException("Некорректный HEX-формат")
    }
    return Color(
        alpha = a.toString().toInt(16),
        red = r.toString().toInt(16),
        green = g.toString().toInt(16),
        blue = b.toString().toInt(16)
    )
}

fun Color.toHexString(): String {
    val srgb = this.convert(ColorSpaces.Srgb)
    val alpha = (srgb.alpha * 255).toInt()
    val red = (srgb.red * 255).toInt()
    val green = (srgb.green * 255).toInt()
    val blue = (srgb.blue * 255).toInt()
    return String.format("#%02X%02X%02X%02X", alpha, red, green, blue)
}



fun loadMainScreenViewModelFromJson(filePath: String, representationStrategy: RepresentationStrategy) : MainScreenViewModel?{
    val graphJson = parseGraphFromJson(filePath)
    val graph : Graph
    val mainScreenViewModel: MainScreenViewModel
    when (graphJson.type){
        "directed" -> { //убрать потом дублирование кода
            graph = DirectedGraph()
            graphJson.vertices.forEach { vertex -> graph.addVertex(vertex.value) }
            graphJson.edges.forEach { edge -> graph.addEdge(edge.from, edge.to, edge.weight) }
            mainScreenViewModel = MainScreenViewModelForDirectedGraph(graph, representationStrategy)
            graphJson.vertices.forEach { vertex ->
                mainScreenViewModel.graphViewModel.setVertexColor(vertex.value, vertex.color.hexToColor())
                mainScreenViewModel.graphViewModel.setVertexSize(vertex.value, vertex.radius.dp)
            }
            graphJson.edges.forEach { edge ->
                mainScreenViewModel.graphViewModel.setEdgeColor(edge.from, edge.to, edge.color.hexToColor())
                mainScreenViewModel.graphViewModel.setEdgeWidth(edge.from, edge.to, edge.width)
            }
            return mainScreenViewModel
        }
        "undirected" -> {
            graph = UndirectedGraph()
            graphJson.vertices.forEach { vertex -> graph.addVertex(vertex.value) }
            graphJson.edges.forEach { edge -> graph.addEdge(edge.from, edge.to, edge.weight) }
            mainScreenViewModel = MainScreenViewModelForUndirectedGraph(graph, representationStrategy)
            graphJson.vertices.forEach { vertex ->
                mainScreenViewModel.graphViewModel.setVertexColor(vertex.value, vertex.color.hexToColor())
                mainScreenViewModel.graphViewModel.setVertexSize(vertex.value, vertex.radius.dp)
            }
            graphJson.edges.forEach { edge ->
                mainScreenViewModel.graphViewModel.setEdgeColor(edge.from, edge.to, edge.color.hexToColor())
                mainScreenViewModel.graphViewModel.setEdgeWidth(edge.from, edge.to, edge.width)
            }
            return mainScreenViewModel
        }
        else -> IllegalArgumentException()//потом тут подумаю чо кинуть
    }
    return null
}

fun saveToJson(mainScreenViewModel : MainScreenViewModel, filePath : String){
    val graphType = when (mainScreenViewModel) {
        is MainScreenViewModelForDirectedGraph -> "directed"
        is MainScreenViewModelForUndirectedGraph -> "undirected"
        else -> return
    }
    val vertices = mutableListOf<Vertex>()
    mainScreenViewModel.graphViewModel.vertices.forEach { vertex ->
        vertices.add(Vertex(vertex.value, vertex.radius.toPx(), vertex.color.toHexString()))
    }
    val edges = mutableListOf<Edge>()
    mainScreenViewModel.graphViewModel.edges.forEach { edge ->
        edges.add(Edge(edge.firstVertex.value, edge.secondVertex.value, edge.width, edge.color.toHexString(), edge.weight.toLong()))
    }

    val graphToSave = GraphJson(graphType, vertices, edges)
    val jsonString = """{
    "graph": ${Json { prettyPrint = true }.encodeToString(graphToSave)}
    }""".trimIndent()

    File(filePath).writeText(jsonString)
}

