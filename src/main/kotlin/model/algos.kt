package model

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.invoke

sealed class Distance<E : Comparable<E>> : Comparable<Distance<E>> {
    data class Finite<E : Comparable<E>>(val value: E) : Distance<E>()
    object Infinity : Distance<Nothing>()

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun <E : Comparable<E>> infinity(): Distance<E> =
            Infinity as Distance<E>

        fun <E : Comparable<E>> of(value: E?): Distance<E> =
            if (value == null) infinity() else Finite(value)
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
    distances: Map<V, Distance<E>>,
    used: MutableMap<V, Boolean>
): Pair<V, Distance<E>> {
    var min: Pair<V, Distance<E>>? = null
    for ((first, second) in distances) {
        if (min == null || (second < min.second && !(used[first] ?: true))) {
            min = first to second
        }
    }
    if (min == null) {
        throw NoSuchElementException("No unused vertex with finite distance found.")
    }
    used[min.first] = true
    return min
}

fun <V, E : Comparable<E>> dijkstra(
    graph: Graph<V, E>,
    start: V,
    end: V
): List<Edge<E, V>>? {
    if (start == end) return emptyList()
    val ring = graph.ring
    val zero = ring.zero
    val vertices = graph.vertices
    val edges = graph.edges
    val distances = mutableMapOf<V, Distance<E>>()
    for (i in vertices)
        distances[i.value] = infinity()
    distances[start] = Distance.Finite(zero)

    for (i in edges) {
        if (i.element < zero) {
            println("Weight can not be negative in Dijkstra")
            return null
        }
    }

    val size = graph.vertices.size
    val used = mutableMapOf<V, Boolean>()

    for (i in 0..size - 1) {
        val minValue = minDistanceForDijkstra(distances, used)
        if (minValue.second == infinity<E>()) break

        val edges = graph.getEdgesByVertex(minValue.first)
        for (edge in edges) {
            val current = distances.getValue(edge.vertexes.second.value)
            val from = distances.getValue(minValue.first).getOrNull()

            val newDistance = if (from != null) {
                Distance.of(ring { from + edge.element })
            } else {
                infinity()
            }

            distances[edge.vertexes.second.value] = minDistance(current, newDistance)
        }

    }

    return emptyList()
}

