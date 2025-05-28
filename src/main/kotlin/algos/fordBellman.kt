package model

fun fordBellman(graph: Graph, start: Long, end: Long): List<Long>? {
    if (graph.findVertex(start) == null || graph.findVertex(end) == null) return null
    if (start == end) return listOf(start)

    val infinity = Long.MAX_VALUE
    val distances = graph.vertices.associateWith { infinity }.toMutableMap()
    val predecessors = mutableMapOf<Long, Long>()
    distances[start] = 0

    repeat(graph.vertices.size - 1) {
        var updated = false
        graph.edges.forEach { edge ->
            val u = edge.vertices.first
            val v = edge.vertices.second

            val uDist = distances[u] ?: infinity
            if (uDist == infinity) return@forEach

            val newDist = uDist + edge.weight
            val vDist = distances[v] ?: infinity
            if (newDist < vDist) {
                distances[v] = newDist
                predecessors[v] = u
                updated = true
            }

            if (graph is UndirectedGraph) {
                val vDist = distances[v] ?: infinity
                if (vDist == infinity) return@forEach

                val newDistReverse = vDist + edge.weight
                val uDistCurrent = distances[u] ?: infinity
                if (newDistReverse < uDistCurrent) {
                    distances[u] = newDistReverse
                    predecessors[u] = v
                    updated = true
                }
            }
        }
        if (!updated) return@repeat
    }

    graph.edges.forEach { edge ->
        val u = edge.vertices.first
        val v = edge.vertices.second

        val uDist = distances[u] ?: infinity
        if (uDist == infinity) return@forEach

        val vDist = distances[v] ?: infinity
        if (uDist + edge.weight < vDist) {
            return null
        }

        // Для ненаправленного графа
        if (graph is UndirectedGraph) {
            if (vDist + edge.weight < uDist) {
                return null
            }
        }
    }

    return reconstructPath(start, end, predecessors).takeIf { it.isNotEmpty() }
}

private fun reconstructPath(start: Long, end: Long, predecessors: Map<Long, Long>): List<Long> {
    if (start == end) return listOf(start)
    if (!predecessors.containsKey(end)) return emptyList()

    val path = mutableListOf<Long>()
    var current: Long? = end

    while (current != null && current != start) {
        path.add(current)
        current = predecessors[current]
    }

    if (current != start) return emptyList()
    path.add(start)

    return path.reversed()
}