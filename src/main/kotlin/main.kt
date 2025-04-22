import space.kscience.kmath.operations.*
import model.*


fun main(){
    val g = DirectedGraph<Int, Int>(IntRing)
    g.addVertex(1)
    g.addVertex(2)
    g.addVertex(3)
    g.addVertex(4)
    g.addVertex(5)
    g.addVertex(6)

    g.addEdge(2, 1, 10)
    g.addEdge(1, 3, 10)
    g.addEdge(3, 2, 10)
    g.addEdge(4, 2, 10)
    g.addEdge(5, 4, 10)
    g.addEdge(4, 6, 10)
    g.addEdge(6, 5, 10)

    print(g.allWeights())
}
