package model

import model.DirectedGraph.DirectedVertex
import space.kscience.kmath.operations.Ring

interface Graph<V, E: Comparable<E>> {
    val ring: Ring<E>
    val vertices: Collection<V>
    val edges: Collection<Edge<E, V>>

    fun addVertex(value: V)
    fun addEdge(firstVertex: V, secondVertex: V, element: E)
    fun deleteVertex(value: V)
    fun deleteEdge(firstVertex: V, secondVertex: V)
    fun getEdgesByVertex(vertex: V): Collection<Edge<E, V>>
    fun findVertex(value: V): Vertex<V>?
}
