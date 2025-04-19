package model

interface Edge<E, V> {
    var element: E
    val vertexes: Pair<Vertex<V>, Vertex<V>>
}
