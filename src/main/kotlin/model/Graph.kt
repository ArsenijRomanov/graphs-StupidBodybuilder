package model

interface Graph<E, V> {
    val vertices: Collection<Vertex<V>>
    val edges: Collection<Edge<E, V>>

    fun addVertex(value: V)
    fun addEdge(firstVertex: V, secondVertex: V, element: E)
    fun deleteVertex(value: V)
    fun deleteEdge(firstVertex: V, secondVertex: V)
}
