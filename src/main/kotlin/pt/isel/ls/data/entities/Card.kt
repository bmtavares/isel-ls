package pt.isel.ls.data.entities

import java.sql.Timestamp

data class Card(
    override val id: Int?,
    var name: String,
    var description: String,
    var dueDate: Timestamp,
    var listId: Int?,
    val boardId: Int
) : Entity {
    override fun clone(id: Int): Card = this.copy(id = id)
}
