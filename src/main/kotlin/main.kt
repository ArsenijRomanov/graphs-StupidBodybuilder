import androidx.compose.ui.unit.IntRect
import space.kscience.kmath.operations.*
import model.*
import java.math.BigDecimal

fun main(){
    val g = DirectedGraph<Int, Int>(IntRing)
    g.addVertex(1)
    g.addVertex(2)
    g.addVertex(3)
    g.addVertex(4)
    g.addVertex(5)
    g.addVertex(6)

    g.addEdge(2, 1, 10)
    g.addEdge(1, 3, 20)
    g.addEdge(3, 2, 30)
    g.addEdge(4, 2, 40)
    g.addEdge(5, 4, 50)
    g.addEdge(4, 6, 60)
    g.addEdge(6, 5, 70)

    println(g.allWeights())
    for (i in g.edges){
        println(i.element)
    }
}
