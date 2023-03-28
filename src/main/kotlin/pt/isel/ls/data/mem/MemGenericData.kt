package pt.isel.ls.data.mem

import pt.isel.ls.data.Data
import pt.isel.ls.data.entities.Entity

open class MemGenericData<T : Entity>(private val entitiesList: MutableList<T>) : Data<T> {
    private var lastId = 1

    fun clearStorage() {
        lastId = 1
    }

    override fun getById(id: Int): T? {
        return entitiesList.firstOrNull { e -> e.id == id }
    }

    override fun add(entity: T): T {
        val newId = if (entitiesList.isEmpty()) 1 else entitiesList.maxBy { it.id!! }.id
        val completeEntity = entity.clone(
            id = newId!! + 1
        )
        entitiesList.add(completeEntity as T)
        return completeEntity
    }

    override fun delete(entity: T) {
        entitiesList.remove(entity)
    }

    override fun edit(entity: T) {
        entitiesList[entitiesList.indexOf(entitiesList.find { e -> e.id == entity.id })] = entity
    }

    override fun exists(entity: T): Boolean {
        return entitiesList.any { e -> e == entity }
    }
}
