package model

interface Graph {
    val vertices: Collection<Long>
    val edges: Collection<Edge>

    fun addVertex(value: Long)

    fun addEdge(
        firstVertex: Long,
        secondVertex: Long,
        element: Long,
    )

    fun findVertex(value: Long): Vertex?

    fun findEdge(
        firstVertex: Long,
        secondVertex: Long,
    ): Edge?

    fun size(): Int

    fun isEmpty(): Boolean
}
