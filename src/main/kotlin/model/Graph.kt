package model

interface Graph<E, V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<E, V>>

    fun addVertex(value: V)
    fun addEdge(from: V, to: V, element: E)
}
