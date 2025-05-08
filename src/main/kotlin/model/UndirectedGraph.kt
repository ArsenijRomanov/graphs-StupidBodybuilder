package model

class UndirectedGraph() : Graph {
    private val _vertices = hashMapOf<Long, UndirectedVertex>()
    private val _edges = hashMapOf<Pair<Long, Long>, UndirectedEdge>()

    override val vertices: Collection<Long>
        get() = _vertices.keys

    override val edges: Collection<Edge>
        get() = _edges.values

    override fun addVertex(value: Long) {
        if (findVertex(value) != null) return
        _vertices.put(value, UndirectedVertex(value))
    }

    override fun addEdge(
        firstVertex: Long,
        secondVertex: Long,
        element: Long,
    ) {
        addVertex(firstVertex)
        addVertex(secondVertex)
        if (findEdge(firstVertex, secondVertex) != null) return
        val newEdge = UndirectedEdge(element, firstVertex to secondVertex)
        _edges.put(firstVertex to secondVertex, newEdge)
    }

    override fun findVertex(value: Long): Vertex? {
        return _vertices[value]
    }

    override fun findEdge(
        firstVertex: Long,
        secondVertex: Long,
    ): Edge? {
        return _edges[firstVertex to secondVertex] ?: _edges[secondVertex to firstVertex]
    }

    override fun size() = _vertices.size

    override fun isEmpty(): Boolean{
        return _vertices.isEmpty()
    }

    class UndirectedVertex(override val value: Long) : Vertex

    class UndirectedEdge(
        override var weight: Long,
        override val vertices: Pair<Long, Long>,
    ) : Edge
}
