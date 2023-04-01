package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import pt.isel.ls.data.utils.TimestampAsLongSerializer
import java.sql.Timestamp

@Serializable
data class EditCardDto(val name:String,
                       val description:String,
                       @Serializable(with = TimestampAsLongSerializer::class)
                       val dueDate: Timestamp?=null,
                       val listId:Int?=null
                       )