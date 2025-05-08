package algos

import model.Graph
import model.UndirectedGraph

fun getAdjacencyList(graph: Graph): Map<Long, List<Long>> {
    val adjacencyList: Map<Long, MutableList<Long>> =
        graph.vertices.associateWith { mutableListOf() }
    for (i in graph.edges) {
        val first = i.vertices.first
        val second = i.vertices.second
        adjacencyList[first]?.add(second)
        if (graph is UndirectedGraph && first != second) {
            adjacencyList[second]?.add(first)
        }
    }
    return adjacencyList
}
