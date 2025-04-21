package model

fun <V> UndirectedGraph<V, IntWeight>.addEdge(firstVertex: V, secondVertex: V, element: Int){
    this.addEdge(firstVertex, secondVertex, IntWeight(element))
}