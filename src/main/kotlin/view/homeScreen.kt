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
import model.DirectedGraph
import model.Graph
import model.UndirectedGraph
import saving.loadMainScreenViewModelFromJson
import viewmodel.CircularPlacementStrategy
import viewmodel.ForceAtlas2Layout
import viewmodel.MainScreenViewModel
import viewmodel.MainScreenViewModelForDirectedGraph
import viewmodel.MainScreenViewModelForUndirectedGraph
import java.awt.Dimension
import javax.swing.JFileChooser

@Composable
fun homeScreen(){
    var filePath by remember { mutableStateOf("Файл не выбран") }
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

                            dialogTitle = "Выберите файл"
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



