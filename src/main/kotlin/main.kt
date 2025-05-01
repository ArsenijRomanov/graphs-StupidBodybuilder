import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.*
import view.MainScreen
import viewmodel.CircularPlacementStrategy
import viewmodel.MainScreenViewModel

val sampleGraph: Graph = UndirectedGraph().apply {
    addVertex(1)
    addVertex(2)
    addVertex(3)
    addVertex(4)
    addVertex(5)
    addVertex(6)
    addVertex(7)

    addEdge(1, 2, 1)
    addEdge(1, 3, 2)
    addEdge(1, 4, 3)
    addEdge(1, 5, 4)
    addEdge(1, 6, 5)
    addEdge(1, 7, 6)

    addVertex(8)
    addVertex(9)
    addVertex(10)
    addVertex(11)
    addVertex(12)
    addVertex(13)
    addVertex(14)

    addEdge(8, 9, 7)
    addEdge(8, 10, 8)
    addEdge(8, 11, 9)
    addEdge(8, 12, 10)
    addEdge(8, 13, 11)
    addEdge(8, 14, 12)

    addEdge(1, 8, 0)
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        MainScreen(MainScreenViewModel(sampleGraph, CircularPlacementStrategy()))
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
