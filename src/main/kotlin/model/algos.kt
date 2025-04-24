package model

import androidx.compose.runtime.internal.illegalDecoyCallException
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke

sealed class Distance<E : Comparable<E>> : Comparable<Distance<E>> {
    data class Finite<E : Comparable<E>>(val value: E) : Distance<E>()
    object Infinity : Distance<Nothing>()

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <E : Comparable<E>> infinity(): Distance<E> = Infinity as Distance<E>

        fun <E : Comparable<E>> of(value: E?): Distance<E> = if (value == null) infinity() else Finite(value)
    }

    override fun compareTo(other: Distance<E>): Int = when {
        this is Infinity && other is Infinity -> 0
        this is Infinity -> 1
        other is Infinity -> -1
        this is Finite && other is Finite -> this.value.compareTo(other.value)
        else -> error("Unreachable")
    }

    fun getOrNull(): E? {
        if (this is Finite) return this.value
        return null
    }

    fun plus(ring: Ring<E>, other: E): Distance<E> = when (this) {
        is Finite -> Distance.of(ring { value + other })
        is Infinity -> this
    }
}

fun <E : Comparable<E>> infinity(): Distance<E> = Distance.infinity()

fun <E : Comparable<E>> minDistance(first: Distance<E>, second: Distance<E>): Distance<E> {
    return if (first < second) first else second
}

fun <V, E : Comparable<E>> minDistanceForDijkstra(
    distances: Map<V, Distance<E>>, used: MutableMap<V, Boolean>
): Pair<V, Distance<E>> {
    var min: Pair<V, Distance<E>>? = null
    for ((first, second) in distances) {
        if (min == null || (second < min.second && !(used[first] ?: false))) {
            min = first to second
        }
    }
    if (min == null) {
        throw NoSuchElementException("No unused vertex with finite distance found.")
    }
    used[min.first] = true
    return min
}

fun <V, E : Comparable<E>> checkGraphForNegativeWeight(graph: Graph<V, E>): Boolean {
    for (i in graph.edges) {
        if (i.element < graph.ring.zero) return true
    }
    return false
}


@Throws(IllegalArgumentException::class, NoSuchElementException::class)
fun <V, E : Comparable<E>> dijkstra(
    graph: Graph<V, E>, start: V, end: V
): List<V>? {
    if (graph.findVertex(start) == null || graph.findVertex(end) == null) {
        throw NoSuchElementException("Start or end vertex not found in the graph.")
    }
    if (start == end) return listOf(start)
    val ring = graph.ring
    val zero = ring.zero
    val vertices = graph.vertices
    val edges = graph.edges
    val distances = mutableMapOf<V, Distance<E>>()
    for (i in vertices) distances[i.value] = infinity()
    distances[start] = Distance.Finite(zero)

    if (checkGraphForNegativeWeight(graph)) {
        throw IllegalArgumentException("Graph contains negative edge weights. Dijkstra's algorithm cannot be used.")
    }

    val size = graph.vertices.size
    val used = mutableMapOf<V, Boolean>()
    val prev = mutableMapOf<V, V>()
    prev[start] = start

    for (i in 0..size - 1) {
        var stop = false
        val minValue = minDistanceForDijkstra(distances, used)
        if (minValue.second == infinity<E>()) return null

        val edges = graph.getEdgesByVertex(minValue.first)
        for (edge in edges) {
            val firstVertex = edge.vertexes.first.value
            val secondVertex = edge.vertexes.second.value
            val weight = edge.element
            val current = distances.getValue(secondVertex)
            val from = distances.getValue(minValue.first).getOrNull()

            val newDistance = if (from != null) {
                Distance.of(ring { from + weight })
            } else {
                infinity()
            }
            if (newDistance < current) {
                distances[secondVertex] = newDistance
                prev[secondVertex] = firstVertex
            }
            if (secondVertex == end) {
                stop = true
                break
            }
        }
        if (stop) break
    }

    if (!prev.containsKey(end)) return null

    val path = mutableListOf<V>()
    var cur: V? = end
    while (cur != null && cur != start) {
        path.add(cur)
        cur = prev[cur]
    }
    if (cur == null) return null // если путь не полный
    path.add(start)
    path.reverse()
    return path
}

