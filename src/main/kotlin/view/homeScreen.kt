package view

import GraphScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
import javax.swing.JOptionPane

@Composable
fun homeScreen(){
    var filePath by remember { mutableStateOf("File not selected") }
    val navigator = LocalNavigator.currentOrThrow
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Start with",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
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
                    navigator.push(GraphScreen(viewModel))
                }) {
                    Text("SQLite")
                }

                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = {
                    }
                ) { Text("Neo4j") }
                Spacer(Modifier.width(4.dp))
                Button(
                    onClick = {
                        JFileChooser().apply {
                            preferredSize = Dimension(800, 600)

                            dialogTitle = "Select file"
                            fileSelectionMode = JFileChooser.FILES_ONLY
                            isMultiSelectionEnabled = false

                            val result = showOpenDialog(null)
                            if(result == JFileChooser.APPROVE_OPTION){
                                filePath = selectedFile.absolutePath
                            } else return@Button
                        }
                        val mainScreenViewModel = loadMainScreenViewModelFromJson(
                            filePath,
                            ForceAtlas2Layout()
                        )
                        if(mainScreenViewModel is MainScreenViewModelForDirectedGraph){
                            navigator.push(GraphScreen(mainScreenViewModel))
                        }
                        if(mainScreenViewModel is MainScreenViewModelForUndirectedGraph){
                            navigator.push(GraphScreen(mainScreenViewModel))
                        }

                    }
                ) { Text("JSON") }
            }
        }
    }}



