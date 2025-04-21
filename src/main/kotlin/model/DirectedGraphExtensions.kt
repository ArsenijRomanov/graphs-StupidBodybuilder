package model

fun <V> DirectedGraph<V, IntWeight>.addEdge(firstVertex: V, secondVertex: V, element: Int){
    this.addEdge(firstVertex, secondVertex, IntWeight(element))
}
