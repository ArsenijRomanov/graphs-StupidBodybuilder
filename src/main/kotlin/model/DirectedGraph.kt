package model

class DirectedGraph<V, E: Weight<E>>() : Graph<V, E> {
    private val _vertices = hashMapOf<V, DirectedVertex<V>>()
    private val _edges = hashMapOf<Pair<V, V>, DirectedEdge<E, V>>()

    override val vertices: Collection<Vertex<V>>
        get() = _vertices.values

    override val edges: Collection<Edge<E, V>>
        get() = _edges.values

    private val edgesByVertex = hashMapOf< V, MutableMap< V, DirectedEdge<E, V> > >()

    fun getEdgesByVertex(vertex: V): Collection<Edge<E, V>>{
        val edges = edgesByVertex[vertex] ?: return emptyList()
        return edges.map { it.value }
    }

    fun getNeighborVertices(vertex: V): Collection<V>{
        return getEdgesByVertex(vertex).map {
            it.vertexes.second.value
        }
    }

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
         edgesToRemove.forEach { (to, _) ->
             _edges.remove(value to to)
         }

         for ((from, edges) in edgesByVertex) {
             if (edges.containsKey(value)) {
                 _edges.remove(from to value)
                 edges.remove(value)
             }
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

    fun getFromDegree(vertex: V): Int{
        val edges = edgesByVertex[vertex] ?: return 0
        return edges.size
    }

    fun getInDegree(vertex: V): Int{
        findVertex(vertex) ?: return 0
        var cnt = 0
        for ((_, edges) in edgesByVertex){
            if (edges.containsKey(vertex))
                ++cnt
        }
        return cnt
    }

    class DirectedVertex<V>(override val value: V) : Vertex<V>

    class DirectedEdge<E, V>(
        override var element: E,
        override val vertexes: Pair<DirectedVertex<V>, DirectedVertex<V>>
    ) : Edge<E, V>

    fun transposedGraph(): DirectedGraph<V, E>{
        val tg = DirectedGraph<V, E>()
        for (i in vertices)
            tg.addVertex(i.value)
        for (i in edges)
            tg.addEdge(i.vertexes.second.value,
                i.vertexes.first.value, i.element)
        return tg
    }

    private fun dfs1(vertex: V, used: HashMap<V, Boolean>, order: ArrayList<V>){
        used[vertex] = true
        for ((to, outgoingEdge) in edgesByVertex[vertex] ?: emptyMap()){
            if (!(used[to] ?: true))
                dfs1(to, used, order)
        }
        order.add(vertex)
    }

    private fun dfs2(vertex: V, tg: DirectedGraph<V, E>, used: HashMap<V, Boolean>, component: ArrayList<V>){
        used[vertex] = true
        component.add(vertex)
        for ((to, outgoingEdge) in tg.edgesByVertex[vertex] ?: emptyMap<V, Vertex<V>>()){
            if (!(used[to] ?: true))
                dfs2(to, tg, used, component)
        }
    }

    fun SCC(): List<List<V>>{
        val used = hashMapOf<V, Boolean>().apply {
            vertices.forEach { put(it.value, false) }
        }

        val order = ArrayList<V>()
        for (i in vertices){
            if (!(used[i.value] ?: true))
                dfs1(i.value, used, order)
        }


        val scc = ArrayList< List<V> >()
        used.keys.forEach { key ->
            used[key] = false
        }
        val tg = transposedGraph()
        for (i in order.asReversed()){
            if (!(used[i] ?: true)){
                val component = ArrayList<V>()
                dfs2(i, tg, used, component)
                scc.add(component)
            }
        }
        return scc
    }
}
