package pt.isel.ls.data

import pt.isel.ls.data.entities.Entity

interface Data<T : Entity> {
    fun getById(id: Int): T?
    fun add(entity: T): T
    fun delete(entity: T)
    fun edit(entity: T)
    fun exists(entity: T): Boolean
}
