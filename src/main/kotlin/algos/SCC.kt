package algos

import model.DirectedGraph

// adjacency list for transposed graph
private fun transposedGraph(graph: DirectedGraph): Map<Long, List<Long>> {
    val tg: Map<Long, MutableList<Long>> =
        graph.vertices.associateWith { mutableListOf() }
    for (i in graph.edges) {
        val first = i.vertices.first
        val second = i.vertices.second
        tg[second]?.add(first)
    }
    return tg
}

private fun dfs1(
    vertex: Long,
    adjacencyList: Map<Long, List<Long>>,
    used: HashMap<Long, Boolean>,
    order: ArrayList<Long>,
) {
    used[vertex] = true
    for (to in adjacencyList[vertex] ?: emptyList()) {
        if (!(used[to] ?: true)) {
            dfs1(to, adjacencyList, used, order)
        }
    }
    order.add(vertex)
}

private fun dfs2(
    vertex: Long,
    tg: Map<Long, List<Long>>,
    used: HashMap<Long, Boolean>,
    component: ArrayList<Long>,
) {
    used[vertex] = true
    component.add(vertex)
    for (to in tg[vertex] ?: emptyList()) {
        if (!(used[to] ?: true)) {
            dfs2(to, tg, used, component)
        }
    }
}

fun SCC(graph: DirectedGraph): List<List<Long>> {
    val adjacencyList = getAdjacencyList(graph)
    val used =
        hashMapOf<Long, Boolean>().apply {
            graph.vertices.forEach { put(it, false) }
        }

    val order = ArrayList<Long>()
    for (i in graph.vertices) {
        if (!(used[i] ?: true)) {
            dfs1(i, adjacencyList, used, order)
        }
    }

    val scc = ArrayList<List<Long>>()
    used.keys.forEach { key ->
        used[key] = false
    }
    val tg = transposedGraph(graph)
    for (i in order.asReversed()) {
        if (!(used[i] ?: true)) {
            val component = ArrayList<Long>()
            dfs2(i, tg, used, component)
            scc.add(component)
        }
    }

    return scc
}
