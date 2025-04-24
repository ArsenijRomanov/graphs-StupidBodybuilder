import space.kscience.kmath.operations.*
import model.*

fun main(){
    val graph = DirectedGraph<Int, Int>(IntRing)
    graph.addVertex(1)
    graph.addVertex(2)
    graph.addVertex(3)
    graph.addVertex(4)
    graph.addVertex(5)
    graph.addVertex(6)

    graph.addEdge(1, 2, 0)
    graph.addEdge(6, 1, 0)
    graph.addEdge(5, 6, 0)
    graph.addEdge(4, 5, 0)
    graph.addEdge(3, 4, 0)
    graph.addEdge(3, 1, 0)
    graph.addEdge(4, 1, 0)
    graph.addEdge(5, 1, 0)

    val rank = leaderRank(graph)
}
