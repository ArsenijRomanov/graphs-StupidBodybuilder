package algos

import model.Graph

fun vertexWithMinDistance(
    distances: Map<Long, Long>,
    used: Set<Long>,
): Long? {
    var minVertex: Long? = null
    var minDistance = Long.MAX_VALUE

    for ((vertex, distance) in distances) {
        if (!used.contains(vertex) && distance < minDistance) {
            minDistance = distance
            minVertex = vertex
        }
    }

    return minVertex
}

fun checkGraphForNegativeWeight(graph: Graph) = graph.edges.any { it.weight < 0 }

fun dijkstra(
    graph: Graph,
    start: Long,
    end: Long,
): List<Long>? {
    if (graph.findVertex(start) == null || graph.findVertex(end) == null) {
        return null
    }
    if (checkGraphForNegativeWeight(graph)) {
        return null
    }
    if (start == end) return listOf(start)

    val adjacencyList = getAdjacencyList(graph)

    val infinity = Long.MAX_VALUE
    val distances = mutableMapOf<Long, Long>()
    for (i in graph.vertices) distances[i] = infinity
    distances[start] = 0

    val used = mutableSetOf<Long>()
    val prev = mutableMapOf<Long, Long>()
    prev[start] = start

    for (i in 0..graph.vertices.size - 1) {
        val curVertex = vertexWithMinDistance(distances, used) ?: return emptyList()
        used.add(curVertex)
        if (curVertex == end) break

        val neighbours = adjacencyList[curVertex] ?: emptyList()
        for (neighbour in neighbours) {
            val weight = graph.findEdge(curVertex, neighbour)?.weight
            val curDistance = distances[neighbour] ?: infinity
            val from = distances[curVertex]

            val newDistance = if (weight == null || from == null) infinity else weight + from
            if (newDistance < curDistance) {
                distances[neighbour] = newDistance
                prev[neighbour] = curVertex
            }
        }
    }

    if (!prev.containsKey(end)) return emptyList()

    val path = mutableListOf<Long>()
    var cur: Long? = end
    while (cur != null && cur != start) {
        path.add(cur)
        cur = prev[cur]
    }
    if (cur == null) return emptyList()
    path.add(start)
    path.reverse()
    return path
}
