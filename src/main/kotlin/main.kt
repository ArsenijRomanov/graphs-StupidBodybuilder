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



//fun main(){
//    val viewModel1 = loadMainScreenViewModelFromJson(
//        "/home/ars/graphs-StupidBodybuilder/examples/bigGraph.json",
//        ForceAtlas2Layout()
//    ) ?: return
//
//    val viewModel2 = loadMainScreenViewModelFromJson(
//        "/home/ars/graphs-StupidBodybuilder/examples/graph.json",
//        ForceAtlas2Layout()
//    ) ?: return
//
//    val viewModel3 = loadMainScreenViewModelFromJson(
//        "/home/ars/graphs-StupidBodybuilder/examples/people.json",
//        ForceAtlas2Layout()
//    ) ?: return
//    val connection = DriverManager.getConnection("jdbc:sqlite:sampleDB.db")
//    val repo = GraphRepository(connection)
//
//    repo.addGraph(
//        viewModel1.graphViewModel,
//        "big graph",
//        viewModel1 is MainScreenViewModelForDirectedGraph
//        )
//
//    repo.addGraph(
//        viewModel2.graphViewModel,
//        "common graph",
//        viewModel2 is MainScreenViewModelForDirectedGraph
//    )
//
//    repo.addGraph(
//        viewModel3.graphViewModel,
//        "people",
//        viewModel3 is MainScreenViewModelForDirectedGraph
//    )
//
//    repo.close()
//}



