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
import saving.loadMainScreenViewModelFromJson
import view.homeScreen
import viewmodel.MainScreenViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import saving.GraphRepository
import saving.saveToJson
import viewmodel.ForceAtlas2Layout
import java.sql.DriverManager

class FirstScreen() : Screen {
    override val key: ScreenKey = "FirstScreen"

    @Composable
    override fun Content() {
        homeScreen()
    }
}

data class GraphScreen(val mainScreenViewModel: MainScreenViewModel) : Screen {
    @Composable
    override fun Content() {
        if(mainScreenViewModel is MainScreenViewModelForDirectedGraph){
            MainScreenForDirected(mainScreenViewModel)
        }
        if(mainScreenViewModel is MainScreenViewModelForUndirectedGraph){
            MainScreenForUndirected(mainScreenViewModel)
        }
    }
}

@Composable
@Preview
fun App() {
    MaterialTheme {
        Navigator(FirstScreen())
    }
}

fun main() =
    application {
        Window(onCloseRequest = ::exitApplication) {
            App()
        }
    }

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

val people =
    DirectedGraph().apply {
        //man
        addEdge(1, 2, 0)
        addEdge(2, 4, 0)
        addEdge(2, 6, 0)
        addEdge(2, 3, 0)
        addEdge(4, 5, 0)
        addEdge(6, 7, 0)
        addEdge(3, 8, 0)
        addEdge(3, 9, 0)
        addEdge(8, 10, 0)
        addEdge(9, 11, 0)

        addEdge(2, 1, 0)
        addEdge(4, 2, 0)
        addEdge(6, 2, 0)
        addEdge(3, 2, 0)
        addEdge(5, 4, 0)
        addEdge(7, 6, 0)
        addEdge(8, 3, 0)
        addEdge(9, 3, 0)
        addEdge(10, 8, 0)
        addEdge(11, 9, 0)

        //woman
        addEdge(1 + 11, 2 + 11, 0)
        addEdge(2 + 11, 4 + 11, 0)
        addEdge(2 + 11, 6 + 11, 0)
        addEdge(2 + 11, 3 + 11, 0)
        addEdge(4 + 11, 5 + 11, 0)
        addEdge(6 + 11, 7 + 11, 0)
        addEdge(3 + 11, 8 + 11, 0)
        addEdge(3 + 11, 9 + 11, 0)
        addEdge(8 + 11, 10 + 11, 0)
        addEdge(9 + 11, 11 + 11, 0)

        addEdge(2 + 11, 1 + 11, 0)
        addEdge(4 + 11, 2 + 11, 0)
        addEdge(6 + 11, 2 + 11, 0)
        addEdge(3 + 11, 2 + 11, 0)
        addEdge(5 + 11, 4 + 11, 0)
        addEdge(7 + 11, 6 + 11, 0)
        addEdge(8 + 11, 3 + 11, 0)
        addEdge(9 + 11, 3 + 11, 0)
        addEdge(10 + 11, 8 + 11, 0)
        addEdge(11 + 11, 9 + 11, 0)

        //amam
        addEdge(3, 3 + 11, 0)

        //tits
        addEdge(30, 40, 0)
        addEdge(40, 30, 0)
    }

val star =
    UndirectedGraph().apply {
        addEdge(1, 3, 1)
        addEdge(1, 4, 1)
        addEdge(2, 5, 1)
        addEdge(2, 4, 2)
        addEdge(3, 5, 10)
    }

//fun main(){
//    val connection = DriverManager.getConnection("jdbc:sqlite:sampleDB.db")
//    val repo = GraphRepository(connection)
//    repo.addGraph(sampleGraph, "sampleGraph")
//    repo.addGraph(people, "people")
//    repo.addGraph(star, "star")
//    repo.close()
//}



