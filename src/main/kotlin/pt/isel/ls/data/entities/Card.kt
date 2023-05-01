package pt.isel.ls.data.entities

import kotlinx.serialization.Serializable
import pt.isel.ls.data.utils.TimestampAsLongSerializer
import java.sql.Timestamp

@Serializable
data class Card(
    override val id: Int,
    val name: String,
    val description: String,
    @Serializable(with = TimestampAsLongSerializer::class)
    val dueDate: Timestamp?,
    val listId: Int?,
    val boardId: Int,
    val cIdx:Int
) : Entity {
    override fun clone(id: Int): Card = this.copy(id = id)
}
