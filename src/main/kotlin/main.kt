import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import model.*
import view.MainScreenForDirected
import view.MainScreenForUndirected
import viewmodel.CircularPlacementStrategy
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.MainScreenViewModelForUndirectedGraph

val sampleGraph: Graph =
    DirectedGraph().apply {
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

val secondSampleGraph =
    DirectedGraph().apply {
        addEdge(1, 2, 5)
        addEdge(2, 1, 10)
    }

val thirdSampleGraph =
    UndirectedGraph().apply {
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

        addEdge(1, 1, 0)
        addEdge(2, 2, 0)
        addEdge(3, 3, 0)
        addEdge(4, 4, 0)
        addEdge(5, 5, 0)
        addEdge(6, 6, 0)
        addEdge(7, 7, 0)
    }

val fourthSampleGraph =
    DirectedGraph().apply {
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

val fifthSampleGraph =
    DirectedGraph().apply {
        addEdge(2, 1, 0)
        addEdge(3, 1, 0)
        addEdge(4, 1, 0)
        addEdge(5, 1, 0)
        addEdge(6, 1, 0)
    }

val sixthSampleGraph =
    UndirectedGraph().apply {
        addEdge(1, 2, 1)
        addEdge(2, 3, 2)
        addEdge(1, 3, 10)
    }

val seventhSampleGraph =
    UndirectedGraph().apply {
        addEdge(1, 2, 1)
        addEdge(2, 3, 1)
        addEdge(2, 4, 1)
        addEdge(2, 5, 1)
        addEdge(5, 6, 1)
        addEdge(5, 7, 1)

        addEdge(8, 9, 1)
        addEdge(9, 10, 1)
        addEdge(9, 11, 1)
        addEdge(9, 12, 1)
        addEdge(12, 13, 1)
        addEdge(12, 14, 1)

        addEdge(12, 5, 1)
    }

val star =
    UndirectedGraph().apply {
        addEdge(1, 3, 1)
        addEdge(1, 4, 1)
        addEdge(2, 5, 1)
        addEdge(2, 4, 2)
        addEdge(3, 5, 10)
    }

@Composable
@Preview
fun App(graph: Graph) {
    MaterialTheme {
        if (graph is DirectedGraph) {
            MainScreenForDirected(
                MainScreenViewModelForDirectedGraph(
                    graph,
                    CircularPlacementStrategy(),
                ),
            )
        }
        if (graph is UndirectedGraph) {
            MainScreenForUndirected(
                    MainScreenViewModelForUndirectedGraph(
                    graph,
                    CircularPlacementStrategy(),
                ),
            )
        }
    }
}

fun main() =
    application {
        Window(onCloseRequest = ::exitApplication) {
            App(star)
        }
    }
