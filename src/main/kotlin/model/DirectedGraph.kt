package model

class DirectedGraph() : Graph {
    private val _vertices = hashMapOf<Long, DirectedVertex>()
    private val _edges = hashMapOf<Pair<Long, Long>, DirectedEdge>()

    override val vertices: Collection<Long>
        get() = _vertices.keys

    override val edges: Collection<Edge>
        get() = _edges.values

    private val edgesByVertex = hashMapOf<Long, MutableMap<Long, DirectedEdge>>()

    fun getEdgesByVertex(vertex: Long): Collection<Edge> {
        val edges = edgesByVertex[vertex] ?: return emptyList()
        return edges.map { it.value }
    }

    fun getNeighborVertices(vertex: Long): Collection<Long> {
        return getEdgesByVertex(vertex).map {
            it.vertices.second
        }
    }

    override fun addVertex(value: Long) {
        if (findVertex(value) != null) return
        _vertices.put(value, DirectedVertex(value))
        edgesByVertex.putIfAbsent(value, mutableMapOf())
    }

    override fun addEdge(
        firstVertex: Long,
        secondVertex: Long,
        element: Long,
    ) {
        addVertex(firstVertex)
        addVertex(secondVertex)
        if (findEdge(firstVertex, secondVertex) != null) return
        val newEdge = DirectedEdge(element, firstVertex to secondVertex)
        _edges.put(firstVertex to secondVertex, newEdge)
        edgesByVertex[firstVertex]?.put(secondVertex, newEdge)
    }

    override fun findVertex(value: Long): DirectedVertex? {
        return _vertices[value]
    }

    fun findEdge(
        firstVertex: Long,
        secondVertex: Long,
    ): DirectedEdge? {
        return _edges[firstVertex to secondVertex]
    }

    fun getFromDegree(vertex: Long): Int {
        val edges = edgesByVertex[vertex] ?: return 0
        return edges.size
    }

    fun getInDegree(vertex: Long): Int {
        findVertex(vertex) ?: return 0
        var cnt = 0
        for ((_, edges) in edgesByVertex) {
            if (edges.containsKey(vertex)) {
                ++cnt
            }
        }
        return cnt
    }

    class DirectedVertex(override val value: Long) : Vertex

    class DirectedEdge(
        override var weight: Long,
        override val vertices: Pair<Long, Long>,
    ) : Edge
}
