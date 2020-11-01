package game.actor

import java.util.*

abstract class ActorList : AbstractCollection<Actor>() {

    private val MIN_VALUE = 1
    private val lock = Any()
    lateinit var entities: Array<Any?>
    var indicies: MutableSet<Int> = HashSet()
    var curIndex = MIN_VALUE
    var capacity = 0

    open fun EntityList(capacity: Int) {
        entities = arrayOfNulls(capacity)
        this.capacity = capacity
    }

    open fun getEmptySlot(): Int {
        for (i in 1 until entities.size) {
            if (entities[i] == null) {
                return i
            }
        }
        return -1
    }

    override fun add(entity: Actor): Boolean {
        synchronized(lock) {
            val slot = getEmptySlot()
            if (slot == -1) {
                return false
            }
            add(entity, slot)
            return true
        }
    }

    override fun remove(entity: Actor): Boolean {
        synchronized(lock) {
            entities[entity.index] = null
            indicies.remove(entity.index)
            decreaseIndex()
        }
        return true
    }

    open fun remove(index: Int): Actor? {
        synchronized(lock) {
            val temp = entities[index]
            entities[index] = null
            indicies.remove(index)
            decreaseIndex()
            return temp as Actor?
        }
    }

    open operator fun get(index: Int): Actor? {
        synchronized(lock) {
            return if (index >= entities.size) null else entities[index] as Actor?
        }
    }

    open fun add(entity: Actor, index: Int) {
        if (entities[index] != null) {
            return
        }
        entities[index] = entity
        entity.index = (index)
        indicies.add(index)
    }

    override fun iterator(): MutableIterator<Actor> {
        synchronized(lock) { return ActorListIterator<Actor>(entities, indicies, this) }
    }

    open fun increaseIndex() {
        curIndex++
        if (curIndex >= capacity) {
            curIndex = MIN_VALUE
        }
    }

    open fun decreaseIndex() {
        curIndex--
        if (curIndex <= capacity) curIndex = MIN_VALUE
    }

    override fun contains(entity: Actor): Boolean {
        return indexOf(entity) > -1
    }

    open fun indexOf(entity: Actor): Int {
        for (index in indicies) {
            if (entities[index] == entity) {
                return index
            }
        }
        return -1
    }

    override val size: Int get() = indicies.size

}
