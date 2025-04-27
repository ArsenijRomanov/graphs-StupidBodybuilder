package model

interface Edge<E, V> {
    var element: E
    val vertices: Pair<V, V>
}
