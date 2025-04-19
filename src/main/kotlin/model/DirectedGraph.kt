package model

class DirectedGraph<E, V>() : Graph<E, V> {
    private val _vertices = hashMapOf<V, DirectedVertex<V>>()
    private val _edges = hashMapOf<Pair<V, V>, DirectedEdge<E, V>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<E, V>>
        get() = _edges.values

    private val edgesByVertex = hashMapOf< V, MutableMap< V, DirectedEdge<E, V> > >()

    override fun addVertex(value: V) {
        if (findVertex(value) != null) return
        _vertices.put(value, DirectedVertex(value))
        edgesByVertex.putIfAbsent(value, mutableMapOf())
    }

    override fun addEdge(firstVertex: V, secondVertex: V, element: E){
        addVertex(firstVertex)
        addVertex(secondVertex)
        val first = findVertex(firstVertex) ?: return
        val second = findVertex(secondVertex) ?: return
        if (findEdge(firstVertex, secondVertex) != null) return
        val newEdge = DirectedEdge(element, first to second)
        _edges.put(firstVertex to secondVertex, newEdge)
        edgesByVertex[firstVertex]?.put(secondVertex, newEdge)
    }


     override fun deleteVertex(value: V){
        val edgesToRemove = edgesByVertex[value] ?: return
        for (edge in edgesToRemove){
            val firstVertex = edge.value.vertexes.first
            val secondVertex = edge.value.vertexes.second
            _edges.remove(value to secondVertex.value)
            _edges.remove(secondVertex.value to value)
            edgesByVertex[secondVertex.value]?.remove(value)
        }
        edgesByVertex.remove(value)
        _vertices.remove(value)
    }

    override fun deleteEdge(firstVertex: V, secondVertex: V){
        val edge = findEdge(firstVertex, secondVertex) ?: return
        val firstValue = edge.vertexes.first.value
        val secondValue = edge.vertexes.second.value
        _edges.remove(firstValue to secondValue)
        edgesByVertex[firstValue]?.remove(secondValue)
    }

    fun findVertex(value: V): DirectedVertex<V>?{
        return _vertices[value]
    }

    fun findEdge(firstVertex: V, secondVertex: V): DirectedEdge<E, V>?{
        return _edges[firstVertex to secondVertex]
    }

    class DirectedVertex<V>(override val value: V) : Vertex<V>

    class DirectedEdge<E, V>(
        override var element: E,
        override val vertexes: Pair<DirectedVertex<V>, DirectedVertex<V>>
    ) : Edge<E, V>
}