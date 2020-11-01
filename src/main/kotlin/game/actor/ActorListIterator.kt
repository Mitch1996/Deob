package game.actor

class ActorListIterator<E : Actor>(private val entities: Array<Any?>, indicesSet: Set<Int>, entityList: ActorList) : MutableIterator<E>  {

    private val indices: Array<Int> = indicesSet.toTypedArray()
    private var entityLists: ActorList = entityList
    private var curIndex = 0

    override fun hasNext(): Boolean {
        return indices.size != curIndex
    }

    override fun next(): E {
        val temp = entities[indices[curIndex]]
        curIndex++
        return temp as E
    }

    override fun remove() {
        if (curIndex >= 1) {
            entityLists.remove(indices[curIndex - 1])
        }
    }
}
