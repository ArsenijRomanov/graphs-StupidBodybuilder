package model

class DirectedGraph() : Graph {
    private val _vertices = hashMapOf<Long, DirectedVertex>()
    private val _edges = hashMapOf<Pair<Long, Long>, DirectedEdge>()

    override val vertices: Collection<Long>
        get() = _vertices.keys

    override val edges: Collection<Edge>
        get() = _edges.values

    override fun addVertex(value: Long) {
        if (findVertex(value) != null) return
        _vertices.put(value, DirectedVertex(value))
    }

    override fun addEdge(
        firstVertex: Long,
        secondVertex: Long,
        weight: Long,
    ) {
        addVertex(firstVertex)
        addVertex(secondVertex)
        if (findEdge(firstVertex, secondVertex) != null) return
        val newEdge = DirectedEdge(weight, firstVertex to secondVertex)
        _edges.put(firstVertex to secondVertex, newEdge)
    }

    override fun findVertex(value: Long): Vertex? {
        return _vertices[value]
    }

    override fun findEdge(
        firstVertex: Long,
        secondVertex: Long,
    ): Edge? {
        return _edges[firstVertex to secondVertex]
    }

    override fun size() = _vertices.size

    override fun isEmpty(): Boolean{
        return _vertices.isEmpty()
    }

    class DirectedVertex(override val value: Long) : Vertex

    class DirectedEdge(
        override var weight: Long,
        override val vertices: Pair<Long, Long>,
    ) : Edge
}
