package pt.isel.ls.data

import pt.isel.ls.data.entities.Entity
import pt.isel.ls.tasksServices.dtos.Dto

interface Data<K:Entity> {
    fun getById(id: Int): K
    fun delete(id: Int)
    fun exists(id: Int): Boolean
}
