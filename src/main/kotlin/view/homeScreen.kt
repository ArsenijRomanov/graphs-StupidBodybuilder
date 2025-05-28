package view

import GraphScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import saving.GraphRepository
import saving.loadMainScreenViewModelFromJson
import viewmodel.ForceAtlas2Layout
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.MainScreenViewModelForUndirectedGraph
import java.awt.Dimension
import java.sql.DriverManager
import javax.swing.JFileChooser
import androidx.compose.ui.graphics.Color.Companion
import javax.swing.JOptionPane

@Composable
fun homeScreen(){
    var filePath by remember { mutableStateOf("File not selected") }
    val navigator = LocalNavigator.currentOrThrow

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 60.dp)
            ) {
                Text(
                    text = "Welcome to Graphs App",
                    style = MaterialTheme.typography.h3.copy(fontSize = 42.sp),
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "To continue choose download method",
                    style = MaterialTheme.typography.h5.copy(fontSize = 24.sp),
                    color = MaterialTheme.colors.onBackground.copy(alpha = 0.8f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(40.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp)
            ) {
                Button(
                    onClick = {JFileChooser().apply {
                        preferredSize = Dimension(800, 600)
                        dialogTitle = "Select file"
                        fileSelectionMode = JFileChooser.FILES_ONLY
                        isMultiSelectionEnabled = false

                        val result = showOpenDialog(null)
                        if (result == JFileChooser.APPROVE_OPTION) {
                            filePath = selectedFile.absolutePath
                        } else return@Button
                    }

                    val url = "jdbc:sqlite:$filePath"
                    val connection = DriverManager.getConnection(url)
                    val repository = GraphRepository(connection)
                    val graphs: List<String> = repository.getGraphsNames()

                    if (graphs.isEmpty()) {
                        repository.close()
                        JOptionPane.showMessageDialog(
                            null,
                            "No graphs found in this database",
                            "Error",
                            JOptionPane.WARNING_MESSAGE
                        )
                        return@Button
                    }

                    val graphArray = graphs.toTypedArray()
                    val selectedGraph = JOptionPane.showInputDialog(
                        null,
                        "Select graph:",
                        "Graph Selection",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        graphArray,
                        graphArray.first()
                    ) as? String ?: throw NoSuchElementException()

                    val viewModel = repository.loadGraph(selectedGraph)
                    navigator.push(GraphScreen(viewModel))},
                    modifier = Modifier
                        .height(80.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("SQLite", fontSize = 22.sp)
                }

                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .height(80.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Neo4j", fontSize = 22.sp)
                }

                Button(
                    onClick = {
                        JFileChooser().apply {
                            preferredSize = Dimension(800, 600)

                            dialogTitle = "Select file"
                            fileSelectionMode = JFileChooser.FILES_ONLY
                            isMultiSelectionEnabled = false

                            val result = showOpenDialog(null)
                            if (result == JFileChooser.APPROVE_OPTION) {
                                filePath = selectedFile.absolutePath
                            } else return@Button
                        }

                        val mainScreenViewModel = loadMainScreenViewModelFromJson(
                            filePath,
                            ForceAtlas2Layout()
                        )

                        when (mainScreenViewModel) {
                            is MainScreenViewModelForDirectedGraph ->
                                navigator.push(GraphScreen(mainScreenViewModel))
                            is MainScreenViewModelForUndirectedGraph ->
                                navigator.push(GraphScreen(mainScreenViewModel))
                        }
                    },
                    modifier = Modifier
                        .height(80.dp)
                        .width(200.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 6.dp,
                        pressedElevation = 12.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("JSON", fontSize = 22.sp)
                }
            }
        }
    }
}



