package model

interface Edge<E, V> {
    var element: E
    val vertices: Pair<Vertex<V>, Vertex<V>>
}
