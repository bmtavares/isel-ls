package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.data.utils.TimestampAsLongSerializer
import java.sql.Timestamp

@Serializable
data class Card(
    override val id: Int?,
    var name: String,
    var description: String,
    @Serializable(with = TimestampAsLongSerializer::class)
    var dueDate: Timestamp,
    var listId: Int?,
    val boardId: Int
) : Entity {
    override fun clone(id: Int): Card = this.copy(id = id)
}
