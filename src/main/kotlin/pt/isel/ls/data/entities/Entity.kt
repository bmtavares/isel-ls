package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable

@Serializable
sealed interface Entity {
    val id: Int
    fun clone(id: Int): Entity
}
