import model.*

fun main(){
    val graph = DirectedGraph<Int, IntWeight>()
    graph.addVertex(1)
    graph.addVertex(2)
    graph.addVertex(3)
    graph.addVertex(4)
    graph.addVertex(5)
    graph.addVertex(6)

    graph.addEdge(2, 1, 10)
    graph.addEdge(1, 3, 10)
    graph.addEdge(3, 2, IntWeight(10))
    graph.addEdge(4, 2, IntWeight(10))
    graph.addEdge(5, 4, IntWeight(10))
    graph.addEdge(4, 6, IntWeight(10))
    graph.addEdge(6, 5, IntWeight(10))

    val scc = graph.SCC()
}
