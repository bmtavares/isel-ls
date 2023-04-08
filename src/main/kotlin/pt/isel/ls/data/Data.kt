package pt.isel.ls.data

import pt.isel.ls.data.entities.Entity

interface Data<K : Entity> {
    fun getById(id: Int): K
    fun delete(id: Int)
    fun exists(id: Int): Boolean
}
