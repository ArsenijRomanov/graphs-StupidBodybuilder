package model

class UndirectedGraph() : Graph {
    private val _vertices = hashMapOf<Long, UndirectedVertex>()
    private val _edges = hashMapOf<Pair<Long, Long>, UndirectedEdge>()

    override val vertices: Collection<Long>
        get() = _vertices.keys

    override val edges: Collection<Edge>
        get() = _edges.values

    val edgesByVertex = hashMapOf<Long, MutableSet<UndirectedEdge>>()

    fun getEdgesByVertex(vertex: Long): Collection<Edge> {
        return edgesByVertex[vertex] ?: emptySet()
    }

    fun getNeighborVertices(vertex: Long): Collection<Long> {
        return getEdgesByVertex(vertex).map {
            val v1 = it.vertices.first
            val v2 = it.vertices.second
            if (v1 == vertex) v2 else v1
        }
    }

    override fun addVertex(value: Long) {
        if (findVertex(value) != null) return
        _vertices.put(value, UndirectedVertex(value))
        edgesByVertex.putIfAbsent(value, mutableSetOf())
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
        edgesByVertex[firstVertex]?.add(newEdge)
        edgesByVertex[secondVertex]?.add(newEdge)
    }

    override fun findVertex(value: Long): UndirectedVertex? {
        return _vertices[value]
    }

    fun findEdge(
        firstVertex: Long,
        secondVertex: Long,
    ): UndirectedEdge? {
        return _edges[firstVertex to secondVertex] ?: _edges[secondVertex to firstVertex]
    }

    fun getVertexDegree(vertex: Long): Int {
        val edges = edgesByVertex[vertex] ?: return 0
        return edges.size
    }

    class UndirectedVertex(override val value: Long) : Vertex

    class UndirectedEdge(
        override var weight: Long,
        override val vertices: Pair<Long, Long>,
    ) : Edge
}
