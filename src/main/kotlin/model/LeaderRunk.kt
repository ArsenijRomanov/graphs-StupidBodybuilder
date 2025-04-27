package model

import kotlin.math.abs

fun <V, E : Comparable<E>> leaderRank(graph: Graph<V, E>): Map<V, Double> {
    val epsilon = 0.0001
    val vertices = graph.vertices
    val verticesSize = vertices.size
    var virtualVertexRank = 0.0
    var ranks = vertices.associateWith { 1.0 }
    val vertexDegree = vertices.associateWith {
        graph.getEdgesByVertex(it).size
    }
    val edgesByVertex = vertices.associateWith { graph.getEdgesByVertex(it) }

    var maxDiff = 1.0
    while (maxDiff > epsilon) {
        val oldVirtualVertexRank = virtualVertexRank
        virtualVertexRank = 0.0
        val newRanks = vertices.associateWith { 0.0 }.toMutableMap()

        for ((curVertex, rank) in ranks) {
            val share: Double = rank / ((vertexDegree[curVertex] ?: 0) + 1)
            for (edge in edgesByVertex[curVertex] ?: emptyList()) {
                val to = edge.vertices.second
                newRanks[to] = (newRanks[to] ?: 0.0) + share
            }
            virtualVertexRank += share
        }

        val virtualShare: Double = oldVirtualVertexRank / verticesSize
        for (curVertex in vertices) newRanks[curVertex] = (newRanks[curVertex] ?: 0.0) + virtualShare

        maxDiff = vertices.maxOf { vertex ->
            abs((ranks[vertex] ?: 0.0) - (newRanks[vertex] ?: 0.0))
        }
        ranks = newRanks
    }

    return ranks
}






















