import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.ui.unit.IntOffset  // Добавьте этот импорт
import androidx.compose.ui.zIndex
import model.DirectedGraph
import model.UndirectedGraph
import model.Vertex
import kotlin.math.roundToInt

fun main(){
    val graph = DirectedGraph<Int, Int>()
    graph.addVertex(1)
    graph.addVertex(2)
    graph.addVertex(3)
    graph.addVertex(4)
    graph.addVertex(5)
    graph.addVertex(6)
    graph.addVertex(7)
    graph.addVertex(8)
    graph.addVertex(9)

    graph.addEdge(1, 2, 5)
    graph.addEdge(1, 3, 5)
    graph.addEdge(1, 4, 5)
    graph.addEdge(1, 5, 5)
    graph.addEdge(1, 6, 5)
    graph.addEdge(1, 7, 5)
    graph.addEdge(1, 8, 5)
    graph.addEdge(1, 9, 5)

    graph.addEdge(2, 1, 5)
    graph.addEdge(3, 1, 5)
    graph.addEdge(4, 1, 5)
    graph.addEdge(5, 1, 5)
    graph.addEdge(6, 1, 5)
    graph.addEdge(7, 1, 5)
    graph.addEdge(8, 1, 5)
    graph.addEdge(9, 1, 5)

    graph.addEdge(2, 3, 5)
    graph.addEdge(2, 4, 5)
    graph.addEdge(2, 5, 5)
    graph.addEdge(2, 6, 5)
    graph.addEdge(2, 7, 5)
    graph.addEdge(2, 8, 5)
    graph.addEdge(2, 9, 5)

    graph.addEdge(3, 2, 5)
    graph.addEdge(4, 2, 5)
    graph.addEdge(5, 2, 5)
    graph.addEdge(6, 2, 5)
    graph.addEdge(7, 2, 5)
    graph.addEdge(8, 2, 5)
    graph.addEdge(9, 2, 5)

    graph.deleteEdge(2, 1)
    graph.deleteVertex(1)
    graph.deleteVertex(2)
}
