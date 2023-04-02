package pt.isel.ls.tasksServices.dtos

import kotlinx.serialization.Serializable
import pt.isel.ls.data.utils.TimestampAsLongSerializer
import java.security.Timestamp

@Serializable
data class InputCardDto(val name:String,
                        val description:String,
                        @Serializable(with = TimestampAsLongSerializer::class)
                        val dueDate: java.sql.Timestamp?=null,):Dto
