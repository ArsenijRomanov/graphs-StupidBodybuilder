package model

class UndirectedGraph<E, V>() : Graph<E, V> {
    private val _vertices = hashMapOf<V, UndirectedVertex<V>>()
    private val _edges = hashMapOf<Pair<V, V>, UndirectedEdge<E, V>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<E, V>>
        get() = _edges.values

    val edgesByVertex = hashMapOf< V, MutableSet< UndirectedEdge<E, V> > >()

    override fun addVertex(value: V) {
        if (findVertex(value) != null) return
        _vertices.put(value, UndirectedVertex(value))
        edgesByVertex.putIfAbsent(value, mutableSetOf())
    }

    override fun addEdge(firstVertex: V, secondVertex: V, element: E){
        addVertex(firstVertex)
        addVertex(secondVertex)
        val first = findVertex(firstVertex) ?: return
        val second = findVertex(secondVertex) ?: return
        if (findEdge(firstVertex, secondVertex) != null) return
        val newEdge = UndirectedEdge(element, first to second)
        _edges.put(firstVertex to secondVertex, newEdge)
        edgesByVertex[firstVertex]?.add(newEdge)
        edgesByVertex[secondVertex]?.add(newEdge)
    }


    fun deleteVertex(value: V){
        val edgesToRemove = edgesByVertex[value] ?: return
        for (edge in edgesToRemove){
            val firstVertex = edge.vertexes.first
            val secondVertex = edge.vertexes.second
            _edges.remove(firstVertex.value to secondVertex.value)
            if (firstVertex.value == value)
                edgesByVertex[secondVertex.value]?.remove(edge)
            else
                edgesByVertex[firstVertex.value]?.remove(edge)
        }
        edgesByVertex.remove(value)
        _vertices.remove(value)
    }

    fun deleteEdge(firstVertex: V, secondVertex: V){
        val edge = findEdge(firstVertex, secondVertex) ?: return
        val firstValue = edge.vertexes.first.value
        val secondValue = edge.vertexes.second.value
        _edges.remove(firstValue to secondValue)
        edgesByVertex[firstValue]?.remove(edge)
        edgesByVertex[secondValue]?.remove(edge)
    }

    fun findVertex(value: V): UndirectedVertex<V>?{
        return _vertices[value]
    }

    fun findEdge(firstVertex: V, secondVertex: V): UndirectedEdge<E, V>?{
        return _edges[firstVertex to secondVertex] ?: _edges[secondVertex to firstVertex]
    }

    class UndirectedVertex<V>(override val value: V) : Vertex<V>

    class UndirectedEdge<E, V>(
        override var element: E,
        override val vertexes: Pair<UndirectedVertex<V>, UndirectedVertex<V>>
    ) : Edge<E, V>
}
