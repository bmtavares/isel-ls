package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable

@Serializable
data class Board(
    override val id: Int?,
    var name: String,
    var description: String
) : Entity {
    override fun clone(id: Int): Board = this.copy(id = id)
}
