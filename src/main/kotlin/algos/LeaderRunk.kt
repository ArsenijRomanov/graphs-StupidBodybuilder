package algos

import model.Graph
import kotlin.math.abs

fun leaderRank(graph: Graph): Map<Long, Double> {
    if (graph.isEmpty()) return emptyMap()
    if (graph.edges.isEmpty()) return graph.vertices.associateWith { 1.0 }

    val adjacencyList = getAdjacencyList(graph)
    val epsilon = 0.0001
    val vertices = graph.vertices
    val verticesSize = vertices.size
    var virtualVertexRank = 0.0
    var ranks = vertices.associateWith { 1.0 }.toMutableMap()
    val vertexDegree =
        vertices.associateWith {
            adjacencyList[it]?.size ?: 0
        }
    val neighboursOfVertex = vertices.associateWith { adjacencyList[it] ?: emptyList() }

    var maxDiff = 1.0
    while (maxDiff > epsilon) {
        val oldVirtualVertexRank = virtualVertexRank
        virtualVertexRank = 0.0
        val newRanks = vertices.associateWith { 0.0 }.toMutableMap()

        for ((curVertex, rank) in ranks) {
            val share: Double = rank / ((vertexDegree[curVertex] ?: 0) + 1)
            for (neighbour in neighboursOfVertex[curVertex] ?: emptyList()) {
                newRanks[neighbour] = (newRanks[neighbour] ?: 0.0) + share
            }
            virtualVertexRank += share
        }

        val virtualShare: Double = oldVirtualVertexRank / verticesSize
        for (curVertex in vertices)
            newRanks[curVertex] = (newRanks[curVertex] ?: 0.0) + virtualShare

        maxDiff =
            vertices.maxOf { vertex ->
                abs((ranks[vertex] ?: 0.0) - (newRanks[vertex] ?: 0.0))
            }
        ranks = newRanks
    }

    val virtualShare: Double = virtualVertexRank / verticesSize
    for (curVertex in vertices)
        ranks[curVertex] = (ranks[curVertex] ?: 0.0) + virtualShare

    return ranks
}
